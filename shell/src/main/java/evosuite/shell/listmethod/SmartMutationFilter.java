package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.batch.ListMethodsBatch;
import evosuite.shell.utils.LoggerUtils;

public class SmartMutationFilter extends MethodFlagCondFilter {
	public static final String excelProfileSubfix = "_smartMutationFilterProfiles.xlsx";
	private static Logger log = LoggerUtils.getLogger(SmartMutationFilter.class);

	public SmartMutationFilter() {
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

		Set<BytecodeInstruction> insList = cfg.getRawGraph().vertexSet();

		if (FilterHelper.parameterIsInterfaceOrAbstract(node.desc, classLoader)) {
//			ListMethodsBatch.interfaceCount++;
			return false;
		}

		int binaryGoalCount = 0;
		int inputCount = 0;
		boolean containsString = false;

		Set<String> fieldSet = new HashSet<>();

		// Parameters
		inputCount += Type.getArgumentTypes(node.desc).length;

		// this.f
		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNode().getOpcode() == Opcodes.GETSTATIC || ins.getASMNode().getOpcode() == Opcodes.GETFIELD) {
				FieldInsnNode fieldNode = ((FieldInsnNode) ins.getASMNode());
				String name = fieldNode.name;
				if (fieldNode.owner.equals("java/lang/System")) {
					continue;
				}
				BytecodeInstruction stackIns = ins.getSourceOfStackInstruction(0);
				if (stackIns == null) {
					continue;
				}
				if (stackIns != null && stackIns.loadsReferenceToThis()) {
					fieldSet.add(name);
				}
			}
		}

		inputCount += fieldSet.size();

		// Two operand conditions
		for (BytecodeInstruction ifBranch : getIfBranchesInMethod(cfg)) {

			if (ifBranch.getOperandNum() == 2) {
				binaryGoalCount++;
			} else {
				Frame frame = ifBranch.getFrame();
				int index = frame.getStackSize() - 1;
				Value val = frame.getStack(index);
				if (val instanceof SourceValue) {
					SourceValue srcValue = (SourceValue) val;
					Set<AbstractInsnNode> nodes = srcValue.insns;
					for (AbstractInsnNode insnNode : nodes) {
						BytecodeInstruction src = BytecodeInstructionPool.getInstance(classLoader)
								.getInstruction(className, methodName, insnNode);
						if (CommonUtility.isInvokeMethodInsn(src.getASMNode())) {
							if (src.getCalledMethodsClass().equals("java.lang.String")) {
								binaryGoalCount++;
								containsString = true;
								break;
							}
						}
					}

				}
			}
		}

//		if (binaryGoalCount > 1) {
//			ListMethodsBatch.twoOperandIf1++;
//		}
//
//		if (binaryGoalCount > 2) {
//			ListMethodsBatch.twoOperandIf2++;
//		}
//
//		if (binaryGoalCount > 3) {
//			ListMethodsBatch.twoOperandIf3++;
//		}
//
//		if (binaryGoalCount > 4) {
//			ListMethodsBatch.twoOperandIf4++;
//		}
//
//		if (binaryGoalCount > 5) {
//			ListMethodsBatch.twoOperandIf5++;
//		}
//
//		if (inputCount > 1) {
//			ListMethodsBatch.inputCount1++;
//		}
//		if (inputCount > 2) {
//			ListMethodsBatch.inputCount2++;
//		}
//		if (inputCount > 3) {
//			ListMethodsBatch.inputCount3++;
//		}
//		if (inputCount > 4) {
//			ListMethodsBatch.inputCount4++;
//		}
//		if (inputCount > 5) {
//			ListMethodsBatch.inputCount5++;
//		}

		if (binaryGoalCount == 1 && containsString) {
			return true;
		}

		return false;
	}
}
