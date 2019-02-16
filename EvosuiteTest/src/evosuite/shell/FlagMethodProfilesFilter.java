package evosuite.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

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
				"ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1", "return under branch", "return under branch", "getfield", "invokemethod",
				"other", "Remarks"}, 0);
	}
	
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node)
			throws AnalyzerException, IOException {
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
		boolean valid = false;
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
					AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();
					if (CommonUtility.isInvokeMethodInsn(condDefinition)) {
						if (checkInvokedMethod(classLoader, condDefinition, METHOD_INVOKE_LEVEL, mc)) {
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
								if (checkInvokedMethod(classLoader, lastDef.getASMNode(), METHOD_INVOKE_LEVEL, mc)) {
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
			if (fm.returnUnderBranch > 0) {
				valid = true;
				break;
			}
		}
		return valid;
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
			rowData.add(fm.returnUnderBranch);
			rowData.add(fm.returnUnderBranch > 0 ? 1 : 0);
			rowData.add(fm.getField);
			rowData.add(fm.invokeMethods);
			rowData.add(fm.other);
			rowData.add(StringUtils.join(fm.notes, "\n"));
			data.add(rowData);
		}
		writer.writeSheet("data", data);
	}

	protected boolean checkInvokedMethod(ClassLoader classLoader, AbstractInsnNode insn, int level, MethodContent mc) throws AnalyzerException, IOException {
		if (level <= 0) {
			return false;
		}
		FlagMethod fm = new FlagMethod();
		mc.flagMethods.add(fm);
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

		if (CollectionUtil.existIn(className, String.class.getName(), File.class.getName(),
				HashMap.class.getName(), ArrayList.class.getName(), HashSet.class.getName(),
				Collection.class.getName(), List.class.getName())) {
			fm.notes.add("Exclusive methods!");
			return false;
		}
		
		MethodNode node = getMethod(classLoader, methodInsn, className);
		if (node == null) {
			fm.notes.add("Could not analyze (Does not have explicit code)!");
			return false;
		}
		
		try {
//			GraphPool.clearAll();
			ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			if (cfg == null) {
				BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
				bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
				bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
				cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			}
			DominatorTree<BasicBlock> dt = new DominatorTree<BasicBlock>(cfg);
			if (CollectionUtil.getSize(cfg.getBranches()) <= 1) {
				fm.notes.add("No branch!");
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
					AbstractInsnNode prev = getPreviousInsnNode(exit.getASMNode());
					if (prev instanceof MethodInsnNode) {
						fm.invokeMethods ++;
						valid |= checkInvokedMethod(classLoader, prev, level - 1, mc);
					} else if (CollectionUtil.existIn(prev.getOpcode(), Opcodes.ICONST_0, Opcodes.ICONST_1)) {
						fm.rConst ++;
						valid = true;
						if (dt.getImmediateDominator(exit.getBasicBlock()) != null) {
							fm.returnUnderBranch ++;
						}
					} else if (CollectionUtil.existIn(prev.getOpcode(), Opcodes.GETFIELD)) {
						fm.getField ++;
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
			return valid;
		} catch (Exception e) {
			log.debug("error!!", e);
			return false;
		}
	}
	
	private AbstractInsnNode getPreviousInsnNode(AbstractInsnNode insn) {
		AbstractInsnNode prevNode = insn.getPrevious();
//		while (prevNode != null && ((prevNode instanceof LabelNode) 
//				|| (prevNode instanceof LineNumberNode))) {
//			prevNode = prevNode.getPrevious();
//		}
		return prevNode;
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
		private List<FlagMethod> flagMethods = new ArrayList<>();
	}
	
	private static class FlagMethod {
		private String methodName;
		private int branch;
		private int rConst;
		private int returnUnderBranch;
		private int getField;
		private int invokeMethods;
		private int other;
		private List<String> notes = new ArrayList<>();
	}
}
