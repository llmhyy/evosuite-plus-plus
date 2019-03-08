package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
import org.evosuite.graphs.cdg.DominatorTree;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BasicBlock;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.CFGFrame;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
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

import com.sun.tools.classfile.Opcode;

import evosuite.shell.DefUseAnalyzer;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.OpcodeUtils;

public class FlagMethodProfilesFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(FlagMethodProfilesFilter.class);
	private ExcelWriter writer;
	
	public FlagMethodProfilesFilter() {
		String statisticFile = new StringBuilder(SFConfiguration.getReportFolder()) 
				.append(File.separator).append(EvosuiteForMethod.projectId)
				.append("_flagMethodProfiles.xlsx").toString();
		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data", new String[]{
				"ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1", "branch", "getfield", "branch",
				"iLoad", "branch", "invokemethod", "other", "Remarks", "has Primitve type", "hasPrimitiveComparison"}, 0);
	}
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException {
		log.debug(String.format("#Method %s#%s", className, methodName));
		
		//		GraphPool.clearAll();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}
//		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (CollectionUtil.isNullOrEmpty(cfg.getBranches())) {
			return false;
		} 
		boolean defuseAnalyzed = false;
		MethodContent mc = new MethodContent();
		mc.hasPrimitiveParam = hasPrimitiveParam(node, cn);
		
		boolean valid = false;
		Map<String, Boolean> methodValidityMap = new HashMap<>();
		for (BytecodeInstruction insn : cfg.getBranches()) {
			AbstractInsnNode insnNode = insn.getASMNode();
			if (CollectionUtil.existIn(insnNode .getOpcode(), Opcodes.IFEQ, Opcodes.IFNE)) {
				StringBuilder sb = new StringBuilder()
							.append(OpcodeUtils.getCode(insnNode.getOpcode()))
							.append(", prev -- ")
							.append(OpcodeUtils.getCode(insnNode.getPrevious().getOpcode()));
				log.info(sb.toString());
				CFGFrame frame = insn.getFrame();
				Value value = frame.getStack(0);
				if (value instanceof SourceValue) {
					SourceValue srcValue = (SourceValue) value;
					
					//TODO the value could be defined multiple times
					AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();
					
					if (CommonUtility.isInvokeMethodInsn(condDefinition)) {
						if (checkInvokedMethod(classLoader, condDefinition, insn.getLineNumber(), mc, methodValidityMap)) {
							log.info("!FOUND IT! in method " + methodName);
							valid = true;
						}
					} else {
						BytecodeInstruction condBcDef = cfg.getInstruction(node.instructions.indexOf(condDefinition));
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
								if (checkInvokedMethod(classLoader, lastDef.getASMNode(), insn.getLineNumber(), mc, methodValidityMap)) {
									log.info("!FOUND IT! in method " + methodName);
									valid = true;
								}
							}
						}
					}
				}
			}
		}
		logToExcel(mc, className, methodName);
		valid = false;
		for (FlagMethod fm : mc.flagMethods) {
			if (fm.rConstBranch > 0) {
				valid = true;
				break;
			}
		}
		return valid;
	}

	private boolean hasPrimitiveParam(MethodNode mn, ClassNode cn) {
		try {
			Type[] argTypes = Type.getArgumentTypes(mn.desc);
			for (Type type : argTypes) {
				if (considerAsPrimitiveType(type)) {
					return true;
				}
			}
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean considerAsPrimitiveType(Type type) {
		switch (type.getSort()) {
		case Type.BOOLEAN:
		case Type.CHAR:
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
		case Type.FLOAT:
		case Type.LONG:
		case Type.DOUBLE:
			return true;
		case Type.ARRAY:
			return considerAsPrimitiveType(type.getElementType());
		case Type.OBJECT:
			String className = type.getClassName();
			if (String.class.getName().equals(className)) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	private void logToExcel(MethodContent mc, String className, String methodName) throws IOException {
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
			data.add(rowData);
		}
		writer.writeSheet("data", data);
	}

	protected boolean checkInvokedMethod(ClassLoader classLoader, AbstractInsnNode insn, int calledLineInTargetMethod, MethodContent mc,
			Map<String, Boolean> visitMethods) throws AnalyzerException, IOException {
		FlagMethod fm = new FlagMethod();
		
		MethodInsnNode methodInsn = null;
		String className = null;
		String methodName = null;
		if (insn.getOpcode() == Opcodes.INVOKEDYNAMIC) {
			InvokeDynamicInsnNode idInsn = (InvokeDynamicInsnNode) insn;
			fm.methodName = idInsn.name + idInsn.desc;
		} else if (insn instanceof MethodInsnNode) {
			methodInsn = (MethodInsnNode) insn;
			className = methodInsn.owner.replace("/", ".");
			methodName = CommonUtility.getMethodName(methodInsn.name, methodInsn.desc);
			
			fm.methodName = className + "#" + methodName;
		}
		if (!CollectionUtil.existIn(insn.getOpcode(), Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESTATIC,
				Opcodes.INVOKESPECIAL)) {
			fm.notes.add("InvokedMethod insn opcode: " + OpcodeUtils.getCode(insn.getOpcode()));
			return false;
		}
		if (visitMethods.containsKey(fm.methodName)) {
			return visitMethods.get(fm.methodName);
		}
		mc.flagMethods.add(fm);
		if (!RuntimeInstrumentation.checkIfCanInstrument(className)) {
			fm.notes.add(Remarks.UNINSTRUMENTABLE.text);
			visitMethods.put(fm.methodName, false);
			return false;
		}
		
		MethodNode methodNode = getMethod(classLoader, methodInsn, className);
		if (methodNode == null) {
			fm.notes.add(Remarks.NO_SOURCE.text);
			visitMethods.put(fm.methodName, false);
			return false;
		}
		
		try {
//			GraphPool.clearAll();
			ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			if (cfg == null) {
				BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
				bytecodeAnalyzer.analyze(classLoader, className, methodName, methodNode);
				bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
				cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			}
			DominatorTree<BasicBlock> dt = new DominatorTree<BasicBlock>(cfg);
			if (CollectionUtil.getSize(cfg.getBranches()) <= 1) {
				fm.notes.add(Remarks.NOBRANCH.text);
				visitMethods.put(fm.methodName, false);
				return false;
			}
			fm.branch = cfg.getBranches().size();
			Set<BytecodeInstruction> exitPoints = cfg.getExitPoints();
			boolean valid = false;
			for (BytecodeInstruction exit : exitPoints) {
				if (OpcodeUtils.isReturnInsn(exit.getASMNode().getOpcode())) {
					try {
						Set<ControlDependency> cds = exit.getControlDependencies();
						for (ControlDependency cd : cds) {
							System.out.println(cd);
						}
					} catch (Exception ex) {
						if (!className.startsWith("java")) {
							System.out.println(className);
						}
						// ignore
					}
					AbstractInsnNode prev = getDefinitionInsn(exit);
					fm.notes.add("Invoked Line Number: " + calledLineInTargetMethod);
					
					if (prev instanceof MethodInsnNode) {
						fm.invokeMethods ++;
						valid |= checkInvokedMethod(classLoader, prev, calledLineInTargetMethod, mc, visitMethods);
					} else if (CollectionUtil.existIn(prev.getOpcode(), Opcodes.ICONST_0, Opcodes.ICONST_1)) {
						fm.rConst ++;
						valid = true;
						BytecodeInstruction defBcInsn = exit.getActualCFG().getInstruction(methodNode.instructions.indexOf(prev));
						if (dt.getImmediateDominator(defBcInsn.getBasicBlock()) != null) {
							fm.rConstBranch = 1;
							fm.hasInterestedPrimitiveCompareCond = hasPrimitiveCompareCondConstrainst(dt, defBcInsn, exit.getActualCFG(), methodNode,
									classLoader, className);
						}
					} else if (CollectionUtil.existIn(prev.getOpcode(), Opcodes.GETFIELD)) {
						valid = true;
						BytecodeInstruction defBcInsn = exit.getActualCFG().getInstruction(methodNode.instructions.indexOf(prev));
						if (dt.getImmediateDominator(defBcInsn.getBasicBlock()) != null) {
							fm.rGetFieldBranch = 1;
							fm.hasInterestedPrimitiveCompareCond = hasPrimitiveCompareCondConstrainst(dt, defBcInsn, exit.getActualCFG(), methodNode,
									classLoader, className);
						}
						fm.getField ++;
					} else if (CollectionUtil.existIn(prev.getOpcode(), Opcodes.ILOAD)) {
						valid = true;
						BytecodeInstruction defBcInsn = exit.getActualCFG().getInstruction(methodNode.instructions.indexOf(prev));
						if (dt.getImmediateDominator(defBcInsn.getBasicBlock()) != null) {
							fm.rIloadBranch = 1;
							fm.hasInterestedPrimitiveCompareCond = hasPrimitiveCompareCondConstrainst(dt, defBcInsn, exit.getActualCFG(), methodNode,
									classLoader, className);
						}
						fm.iload ++;
					} else {
						fm.other ++;
						if (prev.getOpcode() == -1) {
							fm.notes.add(prev.getClass().getSimpleName());
						} else {
							fm.notes.add(OpcodeUtils.getCode(prev.getOpcode()));
						}
					}
				}
			}
			visitMethods.put(fm.methodName, valid);
			return valid;
		} catch (Exception e) {
			log.debug("error!!", e);
			visitMethods.put(fm.methodName, false);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private int hasPrimitiveCompareCondConstrainst(DominatorTree<BasicBlock> dt, BytecodeInstruction defBcInsn, ActualControlFlowGraph cfg, 
				MethodNode node, ClassLoader classLoader, String className) {
		try {
			BasicBlock immediateDominator = defBcInsn.getBasicBlock();
			while (immediateDominator != null) {
				Iterator<BytecodeInstruction> it = immediateDominator.iterator();
				BytecodeInstruction cond = null;
				while (it.hasNext()) {
					BytecodeInstruction detailNode = it.next();
					int opcode = detailNode.getASMNode().getOpcode();
					if (OpcodeUtils.isCondition(opcode)
							&& (opcode != Opcodes.IF_ACMPEQ) && (opcode != Opcodes.IF_ACMPNE)) {
						cond = detailNode;
						break;
					}
				}
				if (cond != null) {
					AbstractInsnNode insnNode = cond.getASMNode();
					if (CollectionUtil.existIn(insnNode.getOpcode(), Opcodes.IFEQ, Opcodes.IFNE,
							Opcode.IFLT, Opcode.IFGE, Opcode.IFGT, Opcode.IFLE)) {
						CFGFrame frame = cond.getFrame();
						Value value = frame.getStack(0);
						if(isFromPrimitiveSource(cfg, node, classLoader, className, value)) {
							return 1;
						}
					} else if (CollectionUtil.existIn(insnNode.getOpcode(), Opcode.IF_ICMPEQ, 
							Opcode.IF_ICMPNE, Opcode.IF_ICMPLT, Opcode.IF_ICMPGE,
							Opcode.IF_ICMPGT, Opcode.IF_ICMPLE)) {
						CFGFrame frame = cond.getFrame();
						Value value1 = frame.getStack(0);
						Value value2 = frame.getStack(1);
						if(isFromPrimitiveSource(cfg, node, classLoader, className, value1)
								|| isFromPrimitiveSource(cfg, node, classLoader, className, value2)) {
							return 1;
						}
					}
				}
				immediateDominator = dt.getImmediateDominator(immediateDominator);
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println();
		}
		return 0;
	}

	private boolean isFromPrimitiveSource(ActualControlFlowGraph cfg, MethodNode node, ClassLoader classLoader,
			String className, Value value) {
		if (value instanceof SourceValue) {
			String methodName = CommonUtility.getMethodName(node);
			SourceValue srcValue = (SourceValue) value;
			AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();
			if (isPrimitiveAssignment(condDefinition)) {
				return true;
			} else {
				BytecodeInstruction condBcDef = cfg.getInstruction(node.instructions.indexOf(condDefinition));
				if (condBcDef.isUse()) {
					new DefUseAnalyzer().analyze(classLoader, node, className, methodName, node.access);
					Use use = DefUseFactory.makeUse(condBcDef);
					List<Definition> defs = DefUsePool.getDefinitions(use); // null if it is a method parameter.
					Definition lastDef = null;
					for (Definition def : CollectionUtil.nullToEmpty(defs)) {
						if (lastDef == null || def.getInstructionId() > lastDef.getInstructionId()) {
							lastDef = def;
						}
					}
					if (lastDef != null && isPrimitiveAssignment(lastDef.getASMNode())) {
						return true;
					}
				}
			}
		}
		return false;
	}
		
	private boolean isPrimitiveAssignment(AbstractInsnNode insn) {
		String code = OpcodeUtils.getCode(insn.getOpcode());
		if (code.startsWith("ICONST") || code.startsWith("FCONST")
				|| code.startsWith("DCONST") || code.startsWith("LCONST")) {
			return true;
		}
		return false;
	}

	private AbstractInsnNode getDefinitionInsn(BytecodeInstruction ireturnNode) {
		AbstractInsnNode prevInsn = ireturnNode.getASMNode().getPrevious();
		if (prevInsn.getOpcode()  < 0) {
			BytecodeInstruction node = ireturnNode;
			CFGFrame frame = node.getFrame();
			while (frame == null) {
				frame = node.getPreviousInstruction().getFrame();
			}
			Value value = frame.getStack(0);
			if (value instanceof SourceValue) {
				SourceValue srcValue = (SourceValue) value;
				return (AbstractInsnNode) srcValue.insns.iterator().next();
			} 
//			while (frame != null) {
//				Value value = frame.getStack(0);
//				if (value instanceof SourceValue) {
//					SourceValue srcValue = (SourceValue) value;
//					try {
//						return (AbstractInsnNode) srcValue.insns.iterator().next();
//					} catch (Exception e) {
//						log.debug("Error: ", e);
//						System.out.println();
//						frame = frame.getSuccessors().get(node.getInstructionId());
//					}
//				} else {
//					frame = null;
//				}
//			} 
		}
		
		return prevInsn;
	}
	
	private MethodNode getMethod(ClassLoader classLoader, MethodInsnNode methodInsn, String className) throws IOException {
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
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;
			for (MethodNode m : l) {
				if (m.name.equals(methodInsn.name) && m.desc.equals(methodInsn.desc)) {
					if ((m.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
						return null;
					}
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
		while(it.hasNext()) {
			Object node = it.next();
			System.out.println(node);
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
	
	private static class MethodContent {
		private boolean hasPrimitiveParam;
		private List<FlagMethod> flagMethods = new ArrayList<>();
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
	}
	
	public static enum Remarks {
		UNINSTRUMENTABLE ("Cannot instrument!"),
		NOBRANCH("No branch!"),
		NO_SOURCE("Could not analyze (Does not have explicit code)!"),
		;
		
		private String text;
		private Remarks(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}
}
