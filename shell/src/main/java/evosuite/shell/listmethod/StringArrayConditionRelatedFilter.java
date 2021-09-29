package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.evosuite.coverage.dataflow.DefUseFactory;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class StringArrayConditionRelatedFilter extends MethodFlagCondFilter {
	public static final String excelProfileSubfix = "_stringArrayConditionRelatedProfiles.xlsx";
	private static Logger log = LoggerUtils.getLogger(StringArrayConditionRelatedFilter.class);
	private ExcelWriter writer;

	public StringArrayConditionRelatedFilter() {
//		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
//				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
//		File newFile = new File(statisticFile);
//		if (newFile.exists()) {
//			newFile.delete();
//		}
//		writer = new ExcelWriter(new File(statisticFile));
//		writer.getSheet("data",
//				new String[] { "ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1",
//						"branch", "getfield", "branch", "iLoad", "branch", "invokemethod", "other", "Remarks",
//						"has Primitve type", "hasPrimitiveComparison", "isValid" },
//				0);
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

		for (BytecodeInstruction insn : getIfBranchesInMethod(cfg)) {
			if (checkBranchDependentOnStringArray(classLoader, className, methodName, node, insn)) {
				return true;
			}
		}
		return false;
	}

//	private boolean checkParameterUse(MethodNode node, BytecodeInstruction insn) {
//		if (!FilterHelper.containStringArrayParameter(node.desc)) {
//			return false;
//		}
//		if (insn.isUse()) {
//			Use use = DefUseFactory.makeUse(insn);
//			List<Definition> defs = DefUsePool.getDefinitions(use); // null if it is a method parameter.
//			Definition lastDef = null;
//			for (Definition def : CollectionUtil.nullToEmpty(defs)) {
//				if (lastDef == null || def.getInstructionId() > lastDef.getInstructionId()) {
//					lastDef = def;
//				}
//			}
//		}
//		return false;
//	}
//
//	private boolean checkFieldUse(MethodNode node, BytecodeInstruction insn) {
//		return false;
//
//	}

	private boolean checkBranchDependentOnStringArray(ClassLoader classLoader, String className, String methodName,
			MethodNode node, BytecodeInstruction insn) {
		int operandNum = insn.getOperandNum();
		for (int i = 0; i < operandNum; i++) {
			Frame frame = insn.getFrame();
			int index = frame.getStackSize() - operandNum + i;
			Value val = frame.getStack(index);

			if (val instanceof SourceValue) {
				SourceValue srcValue = (SourceValue) val;
				for (AbstractInsnNode sourceIns : srcValue.insns) {
					AbstractInsnNode sourceInstruction = (AbstractInsnNode) sourceIns;
					BytecodeInstruction src = BytecodeInstructionPool.getInstance(classLoader).getInstruction(className,
							methodName, sourceInstruction);
					System.currentTimeMillis();
					if (src.getASMNode().getOpcode() == Opcodes.GETSTATIC
							|| src.getASMNode().getOpcode() == Opcodes.GETFIELD) {
						FieldInsnNode fieldNode = ((FieldInsnNode) src.getASMNode());
						String desc = fieldNode.desc;
						if (!desc.equals("[Ljava/lang/String;")) {
							continue;
						}

						BytecodeInstruction stackIns = src.getSourceOfStackInstruction(0);
						if (stackIns != null && stackIns.loadsReferenceToThis()) {
							return true;
						}
					} else if (src.isLocalVariableUse()) {
						if (src.isParameter()) {
							Type type = getParameterPosition(src);
							String name = type.getClassName();
							if (name.equals("java.lang.String[]")) {
								System.currentTimeMillis();
								return true;
							}
						}
					} else {
//						if (src.isUse() && src.getASMNode().getOpcode() == Opcodes.AALOAD) {
//							InsnNode insnNode = (InsnNode) src.getASMNode();
//							System.currentTimeMillis();
//						}
						return checkBranchDependentOnStringArray(classLoader, className, methodName, node, src);
					}
				}
			}
		}
		return false;
	}

	public Type getParameterPosition(BytecodeInstruction insn) {
		if (insn.isLocalVariableUse()) {
			String methodName = insn.getRawCFG().getMethodName();
			String methodDesc = methodName.substring(methodName.indexOf("("), methodName.length());
			Type[] typeArgs = Type.getArgumentTypes(methodDesc);

			int slot = insn.getLocalVariableSlot();

			if (insn.getRawCFG().isStaticMethod()) {
				return typeArgs[slot];
			} else {
				return typeArgs[slot - 1];
			}
		}
		return null;
	}
}
