package org.evosuite.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;

public class DistributionUtil {
	public static Map<Integer, Double> computeBranchDistribution(Map<Integer, Integer> distributionMap, List<BranchCoverageTestFitness> branchGoals) {
		Map<Integer, Double> uncoveredBranchDistributionValue = new HashMap<>();
		
		List<BranchCoverageGoal> uncoveredGoals = findUncoveredGoals(distributionMap, branchGoals);
		int sum = getTotalTestCases(distributionMap);
		for(BranchCoverageGoal uncoveredGoal: uncoveredGoals) {
			Map<Integer, Double> siblingsDistribution = findSiblingDistribution(uncoveredGoal, distributionMap);
			double numerator = 0;
			for(Integer key: siblingsDistribution.keySet()) {
				numerator += key * siblingsDistribution.get(key);
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
		// TODO Auto-generated method stub
		return null;
	}

	private static int getTotalTestCases(Map<Integer, Integer> distributionMap) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static List<BranchCoverageGoal> findUncoveredGoals(Map<Integer, Integer> distributionMap,
			List<BranchCoverageTestFitness> branchGoals) {
		// TODO Auto-generated method stub
		return null;
	}
}
