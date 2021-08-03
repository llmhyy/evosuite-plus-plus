package org.evosuite.seeding.smart;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.runtime.Random;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.utils.Randomness;

public class SmartSeedBranchUpdateManager {

	public static Set<BranchSeedInfo> uncoveredApplicableBranchInfo = new HashSet<>();
	public static double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	public static Set<BranchSeedInfo> totalUncoveredGoals = new HashSet<>();
	
	public static void updateUncoveredBranchInfo(Set<?> uncoveredGoals){
		
		if(!Properties.APPLY_SMART_SEED)
			return;
		long nowtime = System.currentTimeMillis();
		//baseline run 30s
//		if((nowtime - TestCaseLegitimizer.startTime) / 1000 < 30)
//			return;
		
		Properties.APPLY_CHAR_POOL = true;
		
		uncoveredApplicableBranchInfo.clear();
		totalUncoveredGoals.clear();
		
		
		Set<BranchSeedInfo> infoSet = new HashSet<>();
		Set<BranchSeedInfo> uncoveredGoal = new HashSet<>();
		//select one branch randomly
		int uncoveredGoalsSize = uncoveredGoals.size();
		Object[] list =  uncoveredGoals.toArray();
//		for(Object obj: uncoveredGoals) {
		Object obj = list[Randomness.nextInt(0, uncoveredGoalsSize)];
			if(obj instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)obj;
				BranchCoverageGoal goal = bf.getBranchGoal();
				//TODO
				if(goal.getBranch().getClassName().equals(Properties.TARGET_CLASS) &&
						goal.getBranch().getMethodName().equals( Properties.TARGET_METHOD)) {
					BranchSeedInfo info = SeedingApplicationEvaluator.evaluate(goal.getBranch());
					uncoveredGoal.add(info);
					if(info.getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
						infoSet.add(info);	
					}
				}
				
			}
//		}
		System.out.println("all branches analyze time :" + (System.currentTimeMillis() - nowtime)/1000);
		uncoveredApplicableBranchInfo = infoSet;
		totalUncoveredGoals = uncoveredGoal;
		
		double ratio = infoSet.size()*1.0 / uncoveredGoals.size()*1.0 ;
		Properties.PRIMITIVE_POOL = oldPrimitivePool * (1 - ratio);
	}
	

}
