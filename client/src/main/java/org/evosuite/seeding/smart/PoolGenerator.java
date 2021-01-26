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
import org.objectweb.asm.tree.IntInsnNode;
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
					int i;
					long l;
					float f;
					double d;
					switch(iNode.getOpcode()) {
						case 1:
							constantValues.add(null);
							break;
						case 2:
							i = -1;
							constantValues.add(i);
							break;
						case 3:
							i = 0;
							constantValues.add(i);	
							break;
						case 4:
							i = 1;
							constantValues.add(i);
							break;
						case 5:
							i = 2;
							constantValues.add(i);
							break;
						case 6:
							i = 3;
							constantValues.add(i);
							break;
						case 7:
							i = 4;
							constantValues.add(i);
							break;
						case 8:
							i = 5;
							constantValues.add(i);
							break;
						case 9:
							l = 0;
							constantValues.add(l);
							break;
						case 10:
							l = 1;
							constantValues.add(l);
							break;
						case 11:
							f = 0;
							constantValues.add(f);
							break;
						case 12:
							f = 1;
							constantValues.add(f);
							break;
						case 13:
							f = 2;
							constantValues.add(f);
							break;
						case 14:
							d = 0;
							constantValues.add(d);
							break;
						case 15:
							d = 1;
							constantValues.add(d);
							break;
						}				
				}
				else if(node instanceof IntInsnNode){
					IntInsnNode iNode = (IntInsnNode)node;
					int i =iNode.operand;
					constantValues.add(i);
				}			
				System.currentTimeMillis();				
			}
		}		
		return constantValues;
	}

	private static Class<?> analyzeType(BranchSeedInfo b) {
		// TODO Cheng Yan
		return b.getTargetType();
		
//		return null;
	}
	
}
