package org.evosuite.seeding.smart;

import java.util.Set;

import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.StaticConstantPool;

public class PoolGenerator {
	public static ConstantPool evaluate(BranchSeedInfo b) {
		
		Class<?> clazz = analyzeType(b);
		
		if(b.getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL) {
			//TODO cache is possible
			ConstantPool pool = new StaticConstantPool(false);
			Set<Object> relevantConstants = parseRelevantConstants(b);
			for(Object obj: relevantConstants) {
				pool.add(obj);				
			}
			
		}
		else if(b.getBenefiticalType() == SeedingApplicationEvaluator.DYNAMIC_POOL) {
			
		}
		
		return null;
	}

	private static Set<Object> parseRelevantConstants(BranchSeedInfo b) {
		// TODO
		ActualControlFlowGraph graph = b.getBranch().getInstruction().getActualCFG();
		
		// deal with the graph
		
		return null;
	}

	private static Class<?> analyzeType(BranchSeedInfo b) {
		// TODO Cheng Yan
		return null;
	}
	
}
