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
import org.evosuite.utils.Randomness;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import net.bytebuddy.jar.asm.Opcodes;

public class BranchwiseConstantPoolManager {
	public static Map<Integer, ConstantPool> STATIC_POOL_CACHE = new HashMap<>();
	public static Map<Integer, ConstantPool> DYNAMIC_POOL_CACHE = new HashMap<>();
	
	public static ConstantPool getBranchwiseDynamicConstantPool(Integer branchId) {
		ConstantPool pool = DYNAMIC_POOL_CACHE.get(branchId);
		if(pool == null) {
			pool = new DynamicConstantPool();
			DYNAMIC_POOL_CACHE.put(branchId, pool);
		}
		
		return pool;
	}
	
	public static void addBranchwiseDynamicConstant(Integer branchId, Object obj) {
		//TODO Cheng Yan, we should use a separate property to control the dynamic pool size
		Properties.DYNAMIC_POOL_SIZE = 10;
		ConstantPool pool = getBranchwiseDynamicConstantPool(branchId);
		pool.add(obj);
		if(SmartSeedBranchUpdateManager.uncoveredApplicableBranchInfo.isEmpty())
			Properties.DYNAMIC_POOL_SIZE = 50;
	}
	
	public static ConstantPool evaluate(BranchSeedInfo b) {
		
//		Class<?> type = analyzeType(b);
		
		if(b.getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL) {
			
			double r = Randomness.nextDouble(0, 1);
			if(r > 0.2) {
				if(STATIC_POOL_CACHE.containsKey(b.getBranch().getActualBranchId())) {
					return STATIC_POOL_CACHE.get(b.getBranch().getActualBranchId());
				}
				
				ConstantPool pool = new StaticConstantPool(false);
				Set<Object> relevantConstants = parseRelevantConstants(b);
				for(Object obj: relevantConstants) {
					pool.add(obj);				
				}
				STATIC_POOL_CACHE.put(b.getBranch().getActualBranchId(), pool);
				return pool;				
			}
			else {
				ConstantPool pool = getBranchwiseDynamicConstantPool(b.getBranch().getActualBranchId());
				return pool;
			}
			
		}
		else if(b.getBenefiticalType() == SeedingApplicationEvaluator.DYNAMIC_POOL) {
			ConstantPool pool = getBranchwiseDynamicConstantPool(b.getBranch().getActualBranchId());
			return pool;
		}
		
		return null;
	}

	private static Set<Object> parseRelevantConstants(BranchSeedInfo b) {
		Set<Object> constantValues = new HashSet<>();
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b.getBranch());
		if (b.getBranch().isSwitchCaseBranch()) {
			b.getBranch().getTargetCaseValue();
			if(b.getBranch().getTargetCaseValue() != null)
				constantValues.add(b.getBranch().getTargetCaseValue());
//			System.currentTimeMillis();
		}
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
						case Opcodes.ICONST_0:
							constantValues.add(new Integer(0));	
							break;
						case Opcodes.ICONST_1:
							constantValues.add(new Integer(1));
							break;
						case Opcodes.ICONST_2:
							constantValues.add(new Integer(2));
							break;
						case Opcodes.ICONST_3:
							constantValues.add(new Integer(3));
							break;
						case Opcodes.ICONST_4:
							constantValues.add(new Integer(4));
							break;
						case Opcodes.ICONST_5:
							constantValues.add(new Integer(5));
							break;
						case Opcodes.LCONST_0:
							constantValues.add(new Long(0));
							break;
						case Opcodes.LCONST_1:
							constantValues.add(new Long(1));
							break;
						case Opcodes.FCONST_0:
							constantValues.add(new Float(0));
							break;
						case Opcodes.FCONST_1:
							constantValues.add(new Float(1));
							break;
						case Opcodes.FCONST_2:
							constantValues.add(new Float(2));
							break;
						case Opcodes.DCONST_0:
							constantValues.add(new Double(0));
							break;
						case Opcodes.DCONST_1:
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

}
