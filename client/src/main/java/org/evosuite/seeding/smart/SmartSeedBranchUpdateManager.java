package org.evosuite.seeding.smart;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;

public class SmartSeedBranchUpdateManager {

	public static Set<BranchSeedInfo> uncoveredApplicableBranchInfo = new HashSet<>();
	public static double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	
	public static void updateUncoveredBranchInfo(Set<?> uncoveredGoals){
		
		if(!Properties.APPLY_SMART_SEED)
			return;
		
		
		uncoveredApplicableBranchInfo.clear();
		
		Set<BranchSeedInfo> infoSet = new HashSet<>();
		for(Object obj: uncoveredGoals) {
			if(obj instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)obj;
				BranchCoverageGoal goal = bf.getBranchGoal();
				
				int type = SeedingApplicationEvaluator.evaluate(goal.getBranch());
				BranchSeedInfo info = new BranchSeedInfo(goal.getBranch(), type, null);
				
				if(info.getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
					infoSet.add(info);					
				}
			}
		}
		
		uncoveredApplicableBranchInfo = infoSet;
		if(infoSet.isEmpty()) {
			Properties.PRIMITIVE_POOL = 0.1;
		}
	}

}
