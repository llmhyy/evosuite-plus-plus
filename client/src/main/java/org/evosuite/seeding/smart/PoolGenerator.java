package org.evosuite.seeding.smart;

import java.util.Set;

public class PoolGenerator {
	public static Set<?> evaluate(BranchSeedInfo b) {
		
		Class<?> clazz = analyzeType(b);
		
		//TODO
		if(b.getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL) {
			
		}
		else if(b.getBenefiticalType() == SeedingApplicationEvaluator.DYNAMIC_POOL) {
			
		}
		
		return null;
	}

	private static Class<?> analyzeType(BranchSeedInfo b) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
