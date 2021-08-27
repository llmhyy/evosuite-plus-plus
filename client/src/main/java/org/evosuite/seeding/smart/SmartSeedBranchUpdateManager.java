package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.utils.Randomness;

public class SmartSeedBranchUpdateManager {

	public static Set<BranchSeedInfo> uncoveredApplicableBranchInfo = new HashSet<>();
	public static double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	public static Set<BranchSeedInfo> totalUncoveredGoals = new HashSet<>();

	/**
	 * if it is a static pool branch, we just set the value to further speed up the seed
	 * 
	 * @param bestMap
	 * @param bestMapTest
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static TestChromosome updateUncoveredBranchInfo(Map<FitnessFunction, Double> bestMap,
			Map<FitnessFunction, TestChromosome> bestMapTest) {

		if(bestMap.isEmpty()) return null;
		
		if (!Properties.APPLY_SMART_SEED)
			return null;

		Properties.APPLY_CHAR_POOL = true;

		uncoveredApplicableBranchInfo.clear();
		totalUncoveredGoals.clear();

		Set<BranchSeedInfo> infoSet = new HashSet<>();
		Set<BranchSeedInfo> uncoveredGoal = new HashSet<>();
		// TODO select the most potential branches
		FitnessFunction minff = null;
		
		List<FitnessFunction> list = new ArrayList<>();
		for(FitnessFunction ff: bestMap.keySet()) {
			Double d = bestMap.get(ff);
			if(d < 1) {
				list.add(ff);
			} 
		}
		
		TestChromosome testSeed = null;
		int uncoveredGoalsSize = list.size();
		
		if(uncoveredGoalsSize == 0 && minff == null) return null;
		
		if(uncoveredGoalsSize == 0) {
			if(minff == null) return null;
			else {
				list.add(minff);
				uncoveredGoalsSize += 1;
			}
		}
		
		Object[] list0 = list.toArray();
		Object ff = null;
		if(uncoveredGoalsSize == 1)
			ff = list0[0];
		else
			ff = list0[Randomness.nextInt(0, uncoveredGoalsSize)];
		if (ff instanceof BranchFitness) {
			BranchFitness bf = (BranchFitness) ff;
			BranchCoverageGoal goal = bf.getBranchGoal();
			
			if (goal.getBranch().getClassName().equals(Properties.TARGET_CLASS)
					&& goal.getBranch().getMethodName().equals(Properties.TARGET_METHOD)) {
				testSeed = bestMapTest.get(ff);
				
				if(testSeed.getLastExecutionResult().hasTestException()) {
					return null;
				}
				
				Branch b = goal.getBranch();
				
//				long t1 = System.currentTimeMillis();
				BranchSeedInfo info = SeedingApplicationEvaluator.evaluate(b, testSeed);
				testSeed = info.referredTest;
//				long t2 = System.currentTimeMillis();
//				SensitivityMutator.total += (t2-t1);
				
				uncoveredGoal.add(info);
				if (info.getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
					infoSet.add(info);
				}
			}

		}

		uncoveredApplicableBranchInfo = infoSet;
		totalUncoveredGoals = uncoveredGoal;

		List<Branch> uncoveredBranches = new ArrayList<>();
		boolean isAllUncoveredBranchNoPool = isAllUncoveredBranchNoPool(uncoveredBranches, list0, SeedingApplicationEvaluator.cache, bestMapTest);
		if(isAllUncoveredBranchNoPool) {
			Properties.PRIMITIVE_POOL = oldPrimitivePool * 0.5;
			testSeed = null;
//			Properties.MAX_INT = 10000;
		}
		else {
			Properties.PRIMITIVE_POOL = oldPrimitivePool;
		}
		
		return testSeed;
	}

	private static boolean isAllUncoveredBranchNoPool(List<Branch> uncoveredBranches, Object[] list0, Map<Branch, BranchSeedInfo> cache, Map<FitnessFunction, TestChromosome> bestMapTest) {
		// TODO 
		for (Object ff : list0) {
			if (ff instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness) ff;
				BranchCoverageGoal goal = bf.getBranchGoal();

				if (goal.getBranch().getClassName().equals(Properties.TARGET_CLASS)
						&& goal.getBranch().getMethodName().equals(Properties.TARGET_METHOD)) {
//					testSeed = bestMapTest.get(ff);
					Branch b = goal.getBranch();

					if (cache.containsKey(b)
							&& cache.get(b).getBenefiticalType() == SeedingApplicationEvaluator.NO_POOL) {
						uncoveredBranches.add(b);
					}
				}
			}
		}
		
		if(uncoveredBranches.size() == list0.length)
			return true;
		
		return false;
	}

}
