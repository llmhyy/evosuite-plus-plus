package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;

public class StatisticBranchFlagEvaluator {
	public static double evaluate(Branch b) {
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> rootVariables = branchesInTargetMethod.get(b);
		
		List<BytecodeInstruction> operands = b.getInstruction().getOperands();
		
		List<ComputationPath> pathList = new ArrayList<>();
		for(DepVariable root: rootVariables) {
			List<ComputationPath> computationPathList = ComputationPath.computePath(root, operands);
			ComputationPath bestPath = findPathWithLeastEntropyLoss(computationPathList);
			if(bestPath != null) {
				pathList.add(bestPath);				
			}
		}
		
		double sum = 0;
		for(ComputationPath p: pathList) {
			sum += p.getScore();
		}
		
		if(sum == 0) {
			return 0;
		}
		
		return sum/pathList.size();
	}
	
	private static ComputationPath findPathWithLeastEntropyLoss(List<ComputationPath> computationPathList) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
