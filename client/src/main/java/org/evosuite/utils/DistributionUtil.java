package org.evosuite.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;

public class DistributionUtil {
	public static Map<Integer, Integer> constructDistributionMap(List<BranchCoverageTestFitness> branchGoals ) {
		Map<Integer, Integer> distributionMap = new HashMap<>();
		for (BranchCoverageTestFitness goal : branchGoals) {
			Integer key = goal.getBranch().getActualBranchId();
			if (!goal.getValue()) {
				key = -key;
			}
			distributionMap.put(key, 0);
		}
		
		return distributionMap;
	}
	
	public static Map<Integer, Double> computeBranchDistribution(Map<Integer, Integer> distributionMap, List<BranchCoverageTestFitness> branchGoals) {
		Map<Integer, Double> uncoveredBranchDistributionValue = new HashMap<>();
		
		List<BranchCoverageGoal> uncoveredGoals = findUncoveredGoals(distributionMap, branchGoals);
		int sum = getTotalTestCases(distributionMap);
		for(BranchCoverageGoal uncoveredGoal: uncoveredGoals) {
			Map<Integer, Double> siblingsDistribution = findSiblingDistribution(uncoveredGoal, distributionMap);
			double numerator = 0;
			for(Integer key: siblingsDistribution.keySet()) {
				numerator += 1/(double)key * siblingsDistribution.get(key);
			}
			double branchDistributionValue = numerator / sum;
			int branchID = uncoveredGoal.getBranch().getActualBranchId();
			Integer key = uncoveredGoal.getValue() ? branchID : -branchID;
			
			uncoveredBranchDistributionValue.put(key, branchDistributionValue);
		}
		
		return uncoveredBranchDistributionValue;
	}

	private static Map<Integer, Double> findSiblingDistribution(BranchCoverageGoal uncoveredGoal,
			Map<Integer, Integer> distributionMap) {
		int index = 1;
		Map<Integer, Double> siblingDistribution = new HashMap<>();
		int branchID = uncoveredGoal.getBranch().getActualBranchId();
		int key = uncoveredGoal.getValue() ? -branchID : branchID;
		siblingDistribution.put(index, (double)distributionMap.get(key));
		
		BytecodeInstruction ins = uncoveredGoal.getBranch().getInstruction();
		while(ins.getControlDependencies().isEmpty()) {
			index++;
			boolean visitNewParent = false;
			for(ControlDependency cd: ins.getControlDependencies()) {
				branchID = cd.getBranch().getActualBranchId();
				
				if(siblingDistribution.containsKey(branchID) || siblingDistribution.containsKey(-branchID)) {
					continue;
				}
				
				key = cd.getBranchExpressionValue() ? -branchID : branchID;
				siblingDistribution.put(index, (double)distributionMap.get(key));
				
				ins = cd.getBranch().getInstruction(); 
				visitNewParent = true;
				break;
			}			
			
			if(!visitNewParent) {
				break;
			}
		}
		return siblingDistribution;
	}

	private static int getTotalTestCases(Map<Integer, Integer> distributionMap) {
		int sum = 0;
		for(Integer key: distributionMap.keySet()) {
			sum += distributionMap.get(key);
		}
		return sum;
	}

	private static List<BranchCoverageGoal> findUncoveredGoals(Map<Integer, Integer> distributionMap,
			List<BranchCoverageTestFitness> branchGoals) {
		List<BranchCoverageGoal> uncoveredGoals = new ArrayList<>();
		for(BranchCoverageTestFitness ff: branchGoals) {
			BranchCoverageGoal goal = ff.getBranchGoal();
			int key = goal.getValue() ? goal.getId() : -goal.getId();
			if(distributionMap.get(key) == 0) {
				uncoveredGoals.add(goal);
			}
		}
		return uncoveredGoals;
	}
}
