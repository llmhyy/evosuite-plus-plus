package org.evosuite.seeding.smart;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;

public class BranchUpdateManager {

	public static Set<BranchSeedInfo> uncoveredBranchInfo = new HashSet<>();
	
	public static void updateUncoveredBranchInfo(Set<?> uncoveredGoals){
		
		if(!Properties.APPLY_SMART_SEED)
			return;
		
		
		uncoveredBranchInfo.clear();
		
		Set<BranchSeedInfo> infoSet = new HashSet<>();
		for(Object obj: uncoveredGoals) {
			if(obj instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)obj;
				BranchCoverageGoal goal = bf.getBranchGoal();
				
				int type = SeedingApplicationEvaluator.evaluate(goal.getBranch());
				Class<?> cla = SeedingApplicationEvaluator.cache.get(goal.getBranch()).getTargetType();
				BranchSeedInfo info = new BranchSeedInfo(goal.getBranch(), type, cla);
				
				if(info.getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
					infoSet.add(info);					
				}
			}
		}
		
		uncoveredBranchInfo = infoSet;
	}

}
