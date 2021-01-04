package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.fbranch.ComputationPath;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DefUseAnalyzer;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class SeedingApplicationEvaluator {

	public static int STATIC_POOL = 1;
	public static int DYNAMIC_POOL = 2;
	public static int NO_POOL = 3;

	public static int evaluate(Branch b) {
		// TODO Cheng Yan
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);

		List<BytecodeInstruction> operands = retrieveOperands(b);

		List<ComputationPath> pathList = new ArrayList<>();
		for (DepVariable input : methodInputs) {
			List<ComputationPath> computationPathList = computePath(input, operands);
			ComputationPath path = findSimplestPath(computationPathList);
			pathList.add(path);
		}

		for (ComputationPath path : pathList) {
			if (path.isFastChannel()) {
				ComputationPath otherPath = findTheOtherPath(path, pathList);
				if (otherPath.isConstant()) {
					return STATIC_POOL;
				} else if (!otherPath.isFastChannel()) {
					return DYNAMIC_POOL;
				}
			}
		}

		return NO_POOL;
	}

	private static ComputationPath findTheOtherPath(ComputationPath path, List<ComputationPath> pathList) {
		// TODO Cheng Yan
		return null;
	}

	private static ComputationPath findSimplestPath(List<ComputationPath> computationPathList) {
		// TODO Cheng Yan
		return null;
	}

	private static List<ComputationPath> computePath(DepVariable root, List<BytecodeInstruction> operands) {
		// TODO Cheng Yan
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static List<BytecodeInstruction> retrieveOperands(Branch b) {
		List<BytecodeInstruction> operands = new ArrayList<>();
		Frame frame = b.getInstruction().getFrame();

		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(b.getClassName(),
				b.getMethodName());
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);

		for (int i = 0; i < b.getInstruction().getOperandNum(); i++) {
			int index = frame.getStackSize() - i - 1;
			Value val = frame.getStack(index);

			if (val instanceof SourceValue) {
				SourceValue srcValue = (SourceValue) val;
				/**
				 * get all the instruction defining the value.
				 */
				for (AbstractInsnNode insNode : srcValue.insns) {
					BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);
					if (defIns != null) {
						operands.add(defIns);
					}
				}
			}
		}
		return operands;
	}

	public static List<BranchSeedInfo> evaluate(String targetMethod) {
		List<BranchSeedInfo> interestedBranches = new ArrayList<>();

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				targetMethod);

		for (Branch branch : branches) {
			int type = evaluate(branch);
			if (type != NO_POOL) {
				interestedBranches.add(new BranchSeedInfo(branch, type));
			}
		}

		return interestedBranches;
	}
}
