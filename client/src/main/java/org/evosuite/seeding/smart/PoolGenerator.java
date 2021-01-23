package org.evosuite.seeding.smart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.DynamicConstantPool;
import org.evosuite.seeding.StaticConstantPool;

public class PoolGenerator {
	public static Map<Branch, ConstantPool> poolCache = new HashMap<>();
	public static ConstantPool evaluate(BranchSeedInfo b) {
		
		Class<?> clazz = analyzeType(b);
		if(poolCache.containsKey(b.getBranch())) {
			return poolCache.get(b.getBranch());
		}
		
		if(b.getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL) {
			//TODO cache is possible
			ConstantPool pool = new StaticConstantPool(false);
			Set<Object> relevantConstants = parseRelevantConstants(b);
			for(Object obj: relevantConstants) {
				pool.add(obj);				
			}
			poolCache.put(b.getBranch(), pool);
			return pool;
			
		}
		else if(b.getBenefiticalType() == SeedingApplicationEvaluator.DYNAMIC_POOL) {
			ConstantPool pool = new DynamicConstantPool();
			poolCache.put(b.getBranch(), pool);
			return pool;
		}
		
		return null;
	}

	private static Set<Object> parseRelevantConstants(BranchSeedInfo b) {
		// TODO
		ActualControlFlowGraph graph = b.getBranch().getInstruction().getActualCFG();
		
		Set<Object> relevantConstants = new HashSet<Object>();
		
		int lineNumber = b.getBranch().getInstruction().getLineNumber();
		
 		Set<BytecodeInstruction> rowGraph = graph.getRawGraph().getGraph().vertexSet();

		for(BytecodeInstruction row : rowGraph) {
			if(row.getLineNumber() == lineNumber) {
				if(row.isConstant()) {				
					//instruction to object
					
					Object o = row.getASMNode();
					relevantConstants.add(o);
				}
				if(row.getASMNodeString().contains("LOAD")){
					String line = row.getASMNodeString().split(" ")[1];
					BytecodeInstruction i = row;
					while(!(i.getASMNodeString().contains("STORE") &&
							i.getASMNodeString().split(" ")[1].equals(line))) {
						if(i.equals(rowGraph.iterator().next()))
							break;
						i = i.getPreviousInstruction();
					}
					if(i.equals(rowGraph.iterator().next()))
						continue;
					i = i.getPreviousInstruction();
					
					//instruction to object
					
					Object o = i.getASMNode();
					relevantConstants.add(o);
				}
					
			}
		}
		
		return relevantConstants;
	}

	private static Class<?> analyzeType(BranchSeedInfo b) {
		// TODO Cheng Yan
		return null;
	}
	
}
