package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.impl.store.Path;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
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
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);
		
		List<BytecodeInstruction> operands = b.getInstruction().getOperands();

		List<ComputationPath> pathList = new ArrayList<>();
		for (DepVariable input : methodInputs) {
			List<ComputationPath> computationPathList = ComputationPath.computePath(input, operands);
			ComputationPath path = findSimplestPath(computationPathList);
			if(path != null)
				pathList.add(path);
		}
		
		for (ComputationPath path : pathList) {
			if (path.isFastChannel(operands)) {
				ComputationPath otherPath = findTheOtherPath(path, pathList);
				if (otherPath.isConstant()) {
					return STATIC_POOL;
				} else if (!otherPath.isFastChannel(operands)) {
					return DYNAMIC_POOL;
				}
			}
		}

		return NO_POOL;
	}

	private static ComputationPath findTheOtherPath(ComputationPath path, List<ComputationPath> pathList) {
		// TODO Cheng Yan
		ComputationPath theOtherPath = new ComputationPath();
		for(int i = 0;i < pathList.size();i++) {
			if(pathList.get(i) != path) {
				theOtherPath = pathList.get(i);
				return theOtherPath;
			}
		}
		return null;
	}

	private static ComputationPath findSimplestPath(List<ComputationPath> computationPathList) {
		// TODO Cheng Yan
		ComputationPath simplestPath = new ComputationPath();
		simplestPath.setScore(9999);
		for(int i = 0;i < computationPathList.size();i++) {
			if(computationPathList.get(i).getScore() < simplestPath.getScore() ) {
				simplestPath = computationPathList.get(i);
				return simplestPath;
			}
				
		}
		return null;
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
