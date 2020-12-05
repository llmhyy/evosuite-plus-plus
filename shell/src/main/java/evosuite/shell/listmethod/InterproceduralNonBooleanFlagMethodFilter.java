package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.coverage.dataflow.DefUseFactory;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.CFGFrame;
import org.evosuite.graphs.dataflow.DefUseAnalyzer;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class InterproceduralNonBooleanFlagMethodFilter extends MethodFlagCondFilter {
	public static final String excelProfileSubfix = "_ipfFlagMethod.xlsx";
	private static Logger log = LoggerUtils.getLogger(InterproceduralNonBooleanFlagMethodFilter.class);
	private ExcelWriter writer;

	public InterproceduralNonBooleanFlagMethodFilter() {
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
//		writer = new ExcelWriter(new File(statisticFile));
//		writer.getSheet("data",
//				new String[] { "ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1",
//						"branch", "getfield", "branch", "iLoad", "branch", "invokemethod", "other", "Remarks",
//						"has Primitve type", "hasPrimitiveComparison", "isValid" },
//				0);
	}

	public boolean checkMethod(String className, String methodName) {
		try {
			Class<?> clazz = Class.forName(className);
			ClassLoader classLoader = clazz.getClassLoader();

			ClassReader reader = new ClassReader(className);
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.SKIP_FRAMES);

			for (MethodNode methodNode : classNode.methods) {
				if (CommonUtility.getMethodName(methodNode).equals(methodName)) {
					return checkMethod(classLoader, className, methodName, methodNode, classNode);
				}
			}

			log.error("Method not found: " + methodName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug(String.format("#Method %s#%s", className, methodName));

		// Get actual CFG for target method
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeInstructionPool.getInstance(classLoader).registerMethodNode(node, className, node.name + node.desc);
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}

		BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
		List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);

		DefUseAnalyzer instr = new DefUseAnalyzer();
		instr.analyze(classLoader, node, className, methodName, node.access);

		MethodContent mc = new MethodContent();

		boolean valid = false;
		Map<String, Boolean> methodValidityMap = new HashMap<>();
		for (BytecodeInstruction insn : getIfBranchesInMethod(cfg)) {
			CFGFrame frame = insn.getFrame();
//			Value value = frame.getStack(0);
			int stackSize = frame.getStackSize();

			// Loop through either 1 or 2 operands of a branch
			for (int i = 0; i < stackSize; i++) {
				Value value = frame.getStack(i);
				if (!(value instanceof SourceValue)) {
					continue;
				}

				SourceValue srcValue = (SourceValue) value;
				AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();

//				if (CommonUtility.isGetFieldInsn(condDefinition)) {
//					FieldInsnNode fieldNode = (FieldInsnNode) condDefinition;
//					System.currentTimeMillis();
//				}

				/* Next instruction is to invokeMethod followed by IF- instruction */
				if (CommonUtility.isInvokeMethodInsn(condDefinition)) {
					if (checkNonBooleanFlagMethod(classLoader, condDefinition, insn.getLineNumber(), mc,
							methodValidityMap)) {
						log.info("!FOUND IT! in method " + methodName);
						valid = true;
					}
				} else {
					BytecodeInstruction condBcDef = cfg.getInstruction(node.instructions.indexOf(condDefinition));
					/* isFieldUse or isLocalVariableUse or isArrayLoadInstruction */
					// Check if the insn is use
					if (condBcDef.isUse()) {

						// TODO the value could be defined multiple times
						// Find the last instruction that defined this operand
						Use use = getUse(condBcDef);
						List<Definition> defs = DefUsePool.getDefinitions(use); // null if it is a method parameter.
						Definition lastDef = null;
						for (Definition def : CollectionUtil.nullToEmpty(defs)) {
							if (lastDef == null || def.getInstructionId() > lastDef.getInstructionId()) {
								lastDef = def;
							}
						}

						// Current Def node is XSTORE instruction
						// Need to get the previous instruction to check if it is a method call
						if (lastDef != null && CommonUtility.isInvokeMethodInsn(lastDef.getASMNode().getPrevious())) {
							if (checkNonBooleanFlagMethod(classLoader, lastDef.getASMNode().getPrevious(),
									insn.getLineNumber(), mc, methodValidityMap)) {
								log.info("!FOUND IT! in method " + methodName);
								valid = true;
							}
						}
					}
				}
			}
//			}
		}

		return valid;
	}

	protected boolean checkNonBooleanFlagMethod(ClassLoader classLoader, AbstractInsnNode flagDefIns,
			int calledLineInTargetMethod, MethodContent mc, Map<String, Boolean> visitMethods)
			throws AnalyzerException, IOException, ClassNotFoundException {
		FlagMethod flagMethod = new FlagMethod();
		MethodInsnNode methodInsn = null;
		String className = null;
		String methodName = null;
		/**
		 * a set of condition check
		 */
		if (flagDefIns.getOpcode() == Opcodes.INVOKEDYNAMIC) {
			InvokeDynamicInsnNode idInsn = (InvokeDynamicInsnNode) flagDefIns;
			flagMethod.methodName = idInsn.name + idInsn.desc;
		} else {
			methodInsn = (MethodInsnNode) flagDefIns;
			className = methodInsn.owner.replace("/", ".");
			methodName = CommonUtility.getMethodName(methodInsn.name, methodInsn.desc);

			flagMethod.methodName = className + "#" + methodName;
		}

		if (visitMethods.containsKey(flagMethod.methodName)) {
			flagMethod.valid = visitMethods.get(flagMethod.methodName);
			return flagMethod.valid;
		}

		mc.flagMethods.add(flagMethod);

		MethodNode methodNode = getMethod(classLoader, methodInsn, className);
		if (methodNode == null) {
			flagMethod.notes.add(Remarks.NO_SOURCE.text);
			visitMethods.put(flagMethod.methodName, false);
			return false;
		}

		// Ignore boolean methods
		if (Type.getReturnType(methodNode.desc) == Type.BOOLEAN_TYPE) {
			return false;
		}

		try {
			/**
			 * we get the cfg for called method here.
			 */
			ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);

			if (cfg == null) {
				BytecodeInstructionPool.getInstance(classLoader).registerMethodNode(methodNode, className,
						methodNode.name + methodNode.desc);
				BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
				bytecodeAnalyzer.analyze(classLoader, className, methodName, methodNode);
				bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
				cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			}

			if (CollectionUtil.getSize(getIfBranchesInMethod(cfg)) < 1) {
				flagMethod.notes.add(Remarks.NOBRANCH.text);
				visitMethods.put(flagMethod.methodName, false);
				return false;
			}

			BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
			List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);

			for (int i = 1; i < instructions.size(); i++) {
				BytecodeInstruction prevIns = instructions.get(i - 1);
				BytecodeInstruction currIns = instructions.get(i);
				// 1. Return value is a constant
				if (prevIns.isConstant() && currIns.isReturn()) {
					return true;
				}

				// 2. Return value is a method call
				if (i - 2 >= 0) {
					// Constructor method call
					if (prevIns.isInvokeSpecial() && prevIns.getCalledMethodName().equals("<init>")
							&& currIns.isReturn()) {
						if (constructorHasConstParam(instructions, i - 1)) {
							return true;
						}
					}

//					BytecodeInstruction objParamIns = instructions.get(i - 2);
//					if (objParamIns.isConstant() && prevIns.isInvokeSpecial() && currIns.isReturn()) {
//						return true;
//					}

					// TODO Other method calls
				}

				// Return value is a local variable
				if (prevIns.isLocalVariableUse() && currIns.isReturn()) {
					int varIndex = prevIns.getLocalVariableSlot();

					// 3. Return value is a local variable which was assigned a constant
					if (localVarAssignedConst(instructions, varIndex, i)) {
						return true;
					}

					// 4. Return value is a local variable which was assigned a 'constant' object
					if (localVarConstObj(instructions, varIndex, i)) {
						return true;
					}
				}
			}

			return false;

//			flagMethod.branch = getIfBranchesInMethod(cfg).size();
//			boolean valid = true;
//			visitMethods.put(flagMethod.methodName, valid);
//			flagMethod.valid = valid;
//			return valid;
		} catch (Exception e) {
			log.debug("error!!", e);
			visitMethods.put(flagMethod.methodName, false);
			return false;
		}
	}

	private boolean localVarAssignedConst(List<BytecodeInstruction> instructions, int varIndex, int endIndex) {
		for (int i = 1; i < endIndex; i++) {
			BytecodeInstruction prevIns = instructions.get(i - 1);
			BytecodeInstruction currIns = instructions.get(i);

			if (prevIns.isConstant() && currIns.isLocalVariableDefinition()
					&& currIns.getLocalVariableSlot() == varIndex) {
				return true;
			}
		}

		return false;
	}

	private boolean localVarConstObj(List<BytecodeInstruction> instructions, int varIndex, int endIndex) {
		for (int i = 2; i < endIndex; i++) {
			BytecodeInstruction objParamIns = instructions.get(i - 2);
			BytecodeInstruction prevIns = instructions.get(i - 1);
			BytecodeInstruction currIns = instructions.get(i);

			if (objParamIns.isConstant() && prevIns.isInvokeSpecial() && currIns.isLocalVariableDefinition()
					&& currIns.getLocalVariableSlot() == varIndex) {
				return true;
			}
		}

		return false;
	}

	private boolean constructorHasConstParam(List<BytecodeInstruction> instructions, int constructorIndex) {
		BytecodeInstruction constructorInsn = instructions.get(constructorIndex);
		MethodInsnNode methodInsnNode = (MethodInsnNode) constructorInsn.getASMNode();
		int numOfParams = Type.getArgumentTypes(methodInsnNode.desc).length - 1;

		for (int i = constructorIndex - 1; i >= 0 && i >= constructorIndex - numOfParams; i -= 1) {
			BytecodeInstruction paramIns = instructions.get(i);

			// Parameter for constructor is a constant, return true
			if (paramIns.isConstant()) {
				return true;
			}

			// Parameter for constructor is a local variable, check if the 
			// variable was assigned a constant
			if (paramIns.isLocalVariableUse()) {
				int varIndex = paramIns.getLocalVariableSlot();
				if (localVarAssignedConst(instructions, varIndex, i)) {
					return true;
				}
			}
		}

		return false;
	}

	private MethodNode getMethod(ClassLoader classLoader, MethodInsnNode methodInsn, String className)
			throws ClassNotFoundException, IOException {
		InputStream is = null;
		try {
			if (methodInsn.owner.startsWith("java")) {
				is = ResourceList.getInstance(this.getClass().getClassLoader()).getClassAsStream(className);
			} else if (methodInsn.owner.startsWith("com/example")) {
				ClassReader reader = new ClassReader(className);
				ClassNode classNode = new ClassNode();
				reader.accept(classNode, ClassReader.SKIP_FRAMES);

				for (MethodNode m : classNode.methods) {
					if (m.name.equals(methodInsn.name) && m.desc.equals(methodInsn.desc)) {
						return m;
					}
				}
			} else {
				is = ResourceList.getInstance(classLoader).getClassAsStream(className);
			}
			if (is == null) {
				is = getClassAsStream(className);
			}
			Class<?> targetClass = classLoader.loadClass(className);
			if (targetClass.isInterface()) {
				return null;
			}
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;
			if ((cn.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
				return null;
			}
			for (MethodNode m : l) {
				if (m.name.equals(methodInsn.name) && m.desc.equals(methodInsn.desc)) {
					dump(m);
					return m;
				}
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return null;
	}

	private Use getUse(BytecodeInstruction condBcDef) {
		// Operand is an object field
		if (condBcDef.isFieldDU()) {
			List<BytecodeInstruction> insList = condBcDef.getSourceListOfStackInstruction(0);
			for (BytecodeInstruction ins : insList) {
				if (ins.isUse()) {
					return DefUseFactory.makeUse(ins);
				}
			}
		}

		return DefUseFactory.makeUse(condBcDef);
	}

	private void dump(MethodNode m) {
		ListIterator it = m.instructions.iterator();
		while (it.hasNext()) {
			Object node = it.next();
		}
	}

	private InputStream getClassAsStream(String name) throws IOException {
		String path = name.replace('.', '/') + ".class";
		String windowsPath = name.replace(".", "\\") + ".class";
		String[] cpEntries = Properties.CP.split(File.pathSeparator);
		for (String cpEntry : cpEntries) {
			if (cpEntry.endsWith(".jar")) {
				JarFile jar = new JarFile(cpEntry);
				JarEntry entry = jar.getJarEntry(path);
				if (entry != null) {
					InputStream is = null;
					try {
						is = jar.getInputStream(entry);
						return is;
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
		return null;
	}

	static class MethodContent {
		boolean hasPrimitiveParam;
		List<FlagMethod> flagMethods = new ArrayList<>();
	}

	private static class FlagMethod {
		private String methodName;
		private int branch;
		private int rConst;
		private int rConstBranch;
		private int getField;
		private int rGetFieldBranch;
		private int iload;
		private int rIloadBranch;
		private int invokeMethods;
		private int other;
		private int hasInterestedPrimitiveCompareCond;
		private List<String> notes = new ArrayList<>();
		private boolean valid = false;
	}

	public static enum Remarks {
		UNINSTRUMENTABLE("Cannot instrument!"), NOBRANCH("No branch!"),
		NO_SOURCE("Could not analyze (Does not have explicit code)!"),;

		private String text;

		private Remarks(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}
}
