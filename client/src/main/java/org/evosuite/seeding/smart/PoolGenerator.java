package org.evosuite.seeding.smart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.DynamicConstantPool;
import org.evosuite.seeding.StaticConstantPool;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class PoolGenerator {
	public static Map<Branch, ConstantPool> poolCache = new HashMap<>();
	public static ConstantPool evaluate(BranchSeedInfo b) {
		
		Class<?> type = analyzeType(b);
		if(poolCache.containsKey(b.getBranch())) {
			return poolCache.get(b.getBranch());
		}
		
		if(b.getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL) {
			//TODO cache is possible
			ConstantPool pool = new StaticConstantPool(false);
			Set<Object> relevantConstants = parseRelevantConstants(b, type);
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

	private static Set<Object> parseRelevantConstants(BranchSeedInfo b, Class<?> type) {
		Set<Object> constantValues = new HashSet<>();
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b.getBranch());
		
		for(DepVariable input: methodInputs) {
			if(input.getInstruction().isConstant()) {
				
				BytecodeInstruction ins = input.getInstruction();
				
				AbstractInsnNode node = ins.getASMNode();
				if(node instanceof LdcInsnNode) {
					LdcInsnNode lNode = (LdcInsnNode)node;
					constantValues.add(lNode.cst);
				}
				else if(node instanceof InsnNode) {
					InsnNode iNode = (InsnNode)node;
					//TODO Cheng Yan check constant related instructions
				}
				
				System.currentTimeMillis();
			}
		}
		
		
		
		return constantValues;
	}

	private static Class<?> analyzeType(BranchSeedInfo b) {
		// TODO Cheng Yan
		return null;
	}
	
}
