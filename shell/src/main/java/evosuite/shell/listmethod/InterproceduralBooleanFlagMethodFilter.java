package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.evosuite.Properties;
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

/**
 * Base IPF Filter
 */
public class InterproceduralBooleanFlagMethodFilter extends MethodFlagCondFilter {
	public static final String excelProfileSubfix = "_ipfFlagMethod.xlsx";
	private static Logger log = LoggerUtils.getLogger(PrimitiveBasedFlagMethodFilter.class);
	private ExcelWriter writer;
	
	public InterproceduralBooleanFlagMethodFilter() {
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data",
				new String[] { "ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1",
						"branch", "getfield", "branch", "iLoad", "branch", "invokemethod", "other", "Remarks",
						"has Primitve type", "hasPrimitiveComparison", "isValid" },
				0);
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

		boolean defuseAnalyzed = false;
		MethodContent mc = new MethodContent();

		boolean valid = false;
		Map<String, Boolean> methodValidityMap = new HashMap<>();
		for (BytecodeInstruction insn : getIfBranchesInMethod(cfg)) {

			AbstractInsnNode insnNode = insn.getASMNode();

			/* check whether it is a flag condition */
			/* exist potential flag method */
			if (CollectionUtil.existIn(insnNode.getOpcode(), Opcodes.IFEQ, Opcodes.IFNE)) {

				CFGFrame frame = insn.getFrame();
				Value value = frame.getStack(0);
				if (value instanceof SourceValue) {
					SourceValue srcValue = (SourceValue) value;

					// TODO the value could be defined multiple times
					AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();
					/* Next instruction is to invokeMethod followed by IFEQ or IFNE */
					if (CommonUtility.isInvokeMethodInsn(condDefinition)) {

						if (checkBooleanFlagMethod(classLoader, condDefinition, insn.getLineNumber(), mc, methodValidityMap)) {
							log.info("!FOUND IT! in method " + methodName);
							valid = true;
						}
					} else {
						BytecodeInstruction condBcDef = cfg.getInstruction(node.instructions.indexOf(condDefinition));
						/* isFieldUse or isLocalVariableUse or isArrayLoadInstruction */
						// Check if the insn is use
						if (condBcDef.isUse()) {
							if (!defuseAnalyzed) {
								DefUseAnalyzer instr = new DefUseAnalyzer();
								instr.analyze(classLoader, node, className, methodName, node.access);
								defuseAnalyzed = true;
							}
							Use use = DefUseFactory.makeUse(condBcDef);
							List<Definition> defs = DefUsePool.getDefinitions(use); // null if it is a method parameter.
							Definition lastDef = null;
							for (Definition def : CollectionUtil.nullToEmpty(defs)) {
								if (lastDef == null || def.getInstructionId() > lastDef.getInstructionId()) {
									lastDef = def;
								}
							}
							if (lastDef != null && CommonUtility.isInvokeMethodInsn(lastDef.getASMNode())) {
								if (checkBooleanFlagMethod(classLoader, lastDef.getASMNode(), insn.getLineNumber(), mc,
										methodValidityMap)) {
									log.info("!FOUND IT! in method " + methodName);
									valid = true;
								}
							}
						}
					}
				}
			}
		}

		return valid;
	}
	
	protected boolean hasParam(MethodNode mn, ClassNode cn) {
		try {
			Type[] argTypes = Type.getArgumentTypes(mn.desc);
			return argTypes.length != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void logToExcel(MethodContent mc, String className, String methodName) throws IOException {
		List<List<Object>> data = new ArrayList<>();
		String methodFullName = className + "#" + methodName;
		for (FlagMethod fm : mc.flagMethods) {
			List<Object> rowData = new ArrayList<>();
			rowData.add(EvosuiteForMethod.projectId);
			rowData.add(EvosuiteForMethod.projectName);
			rowData.add(methodFullName);
			rowData.add(fm.methodName);
			rowData.add(fm.branch);
			rowData.add(fm.rConst);
			rowData.add(fm.rConstBranch);
			rowData.add(fm.getField);
			rowData.add(fm.rGetFieldBranch);
			rowData.add(fm.iload);
			rowData.add(fm.rIloadBranch);
			rowData.add(fm.invokeMethods);
			rowData.add(fm.other);
			rowData.add(StringUtils.join(fm.notes, "\n"));
			rowData.add(mc.hasPrimitiveParam);
			rowData.add(fm.hasInterestedPrimitiveCompareCond);
			rowData.add(fm.valid);
			data.add(rowData);
		}
		writer.writeSheet("data", data);
	}

	protected boolean checkBooleanFlagMethod(ClassLoader classLoader, AbstractInsnNode flagDefIns,
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

			flagMethod.branch = getIfBranchesInMethod(cfg).size();
			boolean valid = true;
			visitMethods.put(flagMethod.methodName, valid);
			flagMethod.valid = valid;
			return valid;
		} catch (Exception e) {
			log.debug("error!!", e);
			visitMethods.put(flagMethod.methodName, false);
			return false;
		}
	}

	private MethodNode getMethod(ClassLoader classLoader, MethodInsnNode methodInsn, String className)
			throws ClassNotFoundException, IOException {
		InputStream is = null;
		try {
			if (methodInsn.owner.startsWith("java")) {
				is = ResourceList.getInstance(this.getClass().getClassLoader()).getClassAsStream(className);
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