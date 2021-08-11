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
import org.evosuite.utils.Randomness;

public class SmartSeedBranchUpdateManager {

	public static Set<BranchSeedInfo> uncoveredApplicableBranchInfo = new HashSet<>();
	public static double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	public static Set<BranchSeedInfo> totalUncoveredGoals = new HashSet<>();

	@SuppressWarnings("rawtypes")
	public static void updateUncoveredBranchInfo(Map<FitnessFunction, Double> bestMap,
			Map<FitnessFunction, TestChromosome> bestMapTest) {

		if(bestMap.isEmpty()) return;
		
		if (!Properties.APPLY_SMART_SEED)
			return;
		long nowtime = System.currentTimeMillis();
		// baseline run 30s
//		if((nowtime - TestCaseLegitimizer.startTime) / 1000 < 20)
//			return;

		Properties.APPLY_CHAR_POOL = true;

		uncoveredApplicableBranchInfo.clear();
		totalUncoveredGoals.clear();

		Set<BranchSeedInfo> infoSet = new HashSet<>();
		Set<BranchSeedInfo> uncoveredGoal = new HashSet<>();
		// select one branch randomly
		// TODO select the most potential branches
		List<FitnessFunction> list = new ArrayList<>();
		for(FitnessFunction ff: bestMap.keySet()) {
			Double d = bestMap.get(ff);
			if(d < 2) {
				list.add(ff);
			}
		}
		
		int uncoveredGoalsSize = list.size();
		Object[] list0 = list.toArray();
		Object ff = list0[Randomness.nextInt(0, uncoveredGoalsSize)];
		if (ff instanceof BranchFitness) {
			BranchFitness bf = (BranchFitness) ff;
			BranchCoverageGoal goal = bf.getBranchGoal();
			
			if (goal.getBranch().getClassName().equals(Properties.TARGET_CLASS)
					&& goal.getBranch().getMethodName().equals(Properties.TARGET_METHOD)) {
				TestChromosome testSeed = bestMapTest.get(ff);
				Branch b = goal.getBranch();
				BranchSeedInfo info = SeedingApplicationEvaluator.evaluate(b, testSeed);
				uncoveredGoal.add(info);
				if (info.getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
					infoSet.add(info);
				}
			}

		}
//		}
		System.out.println("all branches analyze time :" + (System.currentTimeMillis() - nowtime) / 1000);
		uncoveredApplicableBranchInfo = infoSet;
		totalUncoveredGoals = uncoveredGoal;

//		double ratio = infoSet.size() * 1.0 / uncoveredGoals.size() * 1.0;
		if (!infoSet.isEmpty())
			Properties.PRIMITIVE_POOL = oldPrimitivePool * (1 + 1);
		else
			Properties.PRIMITIVE_POOL = oldPrimitivePool;
	}

}
