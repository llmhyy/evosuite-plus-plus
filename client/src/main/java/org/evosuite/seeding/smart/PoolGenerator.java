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

import net.bytebuddy.jar.asm.Opcodes;

public class PoolGenerator {
	public static Map<Branch, ConstantPool> poolCache = new HashMap<>();
	public static ConstantPool evaluate(BranchSeedInfo b) {
		
		Class<?> type = analyzeType(b);
		if(poolCache.containsKey(b.getBranch())) {
			return poolCache.get(b.getBranch());
		}
		
		if(b.getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL) {
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
					switch(iNode.getOpcode()) {
						case Opcodes.ACONST_NULL:
							constantValues.add(null);
							break;
						case Opcodes.ICONST_M1:
							constantValues.add(new Integer(-1));
							break;
						//TODO Cheng Yan, refactor code
						case 3:
							constantValues.add(new Integer(0));	
							break;
						case 4:
							constantValues.add(new Integer(1));
							break;
						case 5:
							constantValues.add(new Integer(2));
							break;
						case 6:
							constantValues.add(new Integer(3));
							break;
						case 7:
							constantValues.add(new Integer(4));
							break;
						case 8:
							constantValues.add(new Integer(5));
							break;
						case 9:
							constantValues.add(new Long(0));
							break;
						case 10:
							constantValues.add(new Long(1));
							break;
						case 11:
							constantValues.add(new Float(0));
							break;
						case 12:
							constantValues.add(new Float(1));
							break;
						case 13:
							constantValues.add(new Float(2));
							break;
						case 14:
							constantValues.add(new Double(0));
							break;
						case 15:
							constantValues.add(new Double(1));
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
