package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;

public class SmartSeedBranchUpdateManager {

	public static Set<BranchSeedInfo> uncoveredApplicableBranchInfo = new HashSet<>();
	public static double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	public static Map<Branch,Integer> oldUncoveredApplicableBranchInfo = new HashMap<>();
	
	public static void updateUncoveredBranchInfo(Set<?> uncoveredGoals){
		
		if(!Properties.APPLY_SMART_SEED)
			return;
		
		int num = oldUncoveredApplicableBranchInfo.size();
		uncoveredApplicableBranchInfo.clear();
		
		Set<BranchSeedInfo> infoSet = new HashSet<>();
		Map<Branch,Integer> oldSet = new HashMap<>();
		for(Object obj: uncoveredGoals) {
			if(obj instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)obj;
				BranchCoverageGoal goal = bf.getBranchGoal();
				int type;
				if(num == 0) {//how to know the new iteration
					type = SeedingApplicationEvaluator.evaluate(goal.getBranch());
				}else {
					type = oldUncoveredApplicableBranchInfo.get(goal.getBranch());
				}
				BranchSeedInfo info = new BranchSeedInfo(goal.getBranch(), type, null);
				
				if(num == 0)
					oldSet.put(goal.getBranch(), type);
				if(info.getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
					infoSet.add(info);	
				}
			}
		}
		
//		oldUncoveredApplicableBranchInfo = oldSet;
		uncoveredApplicableBranchInfo = infoSet;
		if(infoSet.isEmpty()) {
			Properties.PRIMITIVE_POOL = 0.1;
		}
	}

}
