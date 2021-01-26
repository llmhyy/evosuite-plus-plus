package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class SeedingApplicationEvaluator {

	public static int STATIC_POOL = 1;
	public static int DYNAMIC_POOL = 2;
	public static int NO_POOL = 3;
	
	public static Map<Branch, BranchSeedInfo> cache = new HashMap<>();

	public static int evaluate(Branch b){
		if(cache.containsKey(b)) {
			return cache.get(b).getBenefiticalType();
		}
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);
		
		List<BytecodeInstruction> operands = b.getInstruction().getOperands();

		List<ComputationPath> pathList = new ArrayList<>();
		for (DepVariable input : methodInputs) {
			List<ComputationPath> computationPathList = ComputationPath.computePath(input, operands);
			ComputationPath path = findSimplestPath(computationPathList);
			if(path != null)
				pathList.add(path);
		}
		
		List<ComputationPath> removeList = removeRedundancyPath(pathList);
		if(removeList != null) {
			for(ComputationPath path : removeList) {
				pathList.remove(path);
			}
		}
		
		for (ComputationPath path : pathList) {
			if (path.isFastChannel(operands)) {
//				Class<?> cla = findOpcodeType(path);
				ComputationPath otherPath = findTheOtherPath(path, pathList);
				if(otherPath == null && b.getInstruction().getASMNodeString().contains("NULL")) {
					List<BytecodeInstruction> computationNodes = new ArrayList<>();
					computationNodes.add(b.getInstruction());
					otherPath = new ComputationPath();
					otherPath.setComputationNodes(computationNodes);
				}
				if (otherPath.isConstant()) {
					cache.put(b, new BranchSeedInfo(b, STATIC_POOL,otherPath.getComputationNodes().get(0).getClass()));
					return STATIC_POOL;
				} else if (!otherPath.isFastChannel(operands)) {
//					if(cla == null)
//						cla = findOpcodeType(otherPath);
					cache.put(b, new BranchSeedInfo(b, DYNAMIC_POOL,null));
					return DYNAMIC_POOL;
				}
			}
		}

		cache.put(b, new BranchSeedInfo(b, NO_POOL,null));
		return NO_POOL;
	}

	private static Class<?> findOpcodeType(ComputationPath otherPath){
		int size = otherPath.getComputationNodes().size();
		for(BytecodeInstruction bi : otherPath.getComputationNodes()) {
			AbstractInsnNode node = bi.getASMNode();
			if(node instanceof TypeInsnNode) {
				TypeInsnNode tNode = (TypeInsnNode) node;
				String des = tNode.desc.replace("/", ".");
				Class<?> cla;
				try {
					if(Class.forName(des) != null) {
						cla = Class.forName(des);
						return cla;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}		
			}else if(node instanceof MethodInsnNode) {
				MethodInsnNode mNode = (MethodInsnNode) node;
				String des = mNode.desc.replace("/", ".");
				Class<?> cla;
				try {
					if(Class.forName(des) != null) {
						cla = Class.forName(des);
						return cla;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			int opcode = bi.getASMNode().getOpcode();
			switch(opcode) {
			case 0x97:
				return double.class;
				
			}
		}
		
		return null;
	}

	private static List<ComputationPath> removeRedundancyPath(List<ComputationPath> pathList) {
		List<ComputationPath> localPathList = new ArrayList<>();
		for(int i = 0;i < pathList.size();i++) {
			ComputationPath path = pathList.get(i);
			int size = path.getComputationNodes().size();
			boolean haveLineConstant = false;
			for(int j = i + 1;j < pathList.size();j++) {
				ComputationPath pathNext = pathList.get(j);
				int sizeNext = pathNext.getComputationNodes().size();
				
				//method inputs to remove
				if(!path.getComputationNodes().get(0).isConstant() &&
						size >= 2 && sizeNext >= 2 &&
						path.getComputationNodes().get(size - 2) == pathNext.getComputationNodes().get(sizeNext - 2)) {
					if(path.getScore() <= pathNext.getScore()){
						if(!localPathList.contains(pathNext)) {
							localPathList.add(pathNext);
						}
					}
				}
				if(pathNext.getComputationNodes().get(0).isConstant() &&
						pathNext.getComputationNodes().get(0).getLineNumber() == path.getComputationNodes().get(size - 1).getLineNumber())
					haveLineConstant = true;
			}
			
			//constants need to remove
			if(path.getComputationNodes().get(0).isConstant() && haveLineConstant) {
				if(path.getComputationNodes().get(0).getLineNumber() != path.getComputationNodes().get(size - 1).getLineNumber()) {
					if(!localPathList.contains(path)) {
						localPathList.add(path);
					}
				}
			}
			
		}
		if(localPathList.size() != 0)
			return localPathList;
		else
			return null;
	}

	private static ComputationPath findTheOtherPath(ComputationPath path, List<ComputationPath> pathList) {
		// TODO Cheng Yan
		ComputationPath theOtherPath = new ComputationPath();
		for(ComputationPath otherPath : pathList) {
			if(otherPath != path) {
				theOtherPath = otherPath;
				return theOtherPath;
			}
				
		}
		return null;
	}

	private static ComputationPath findSimplestPath(List<ComputationPath> computationPathList) {
		// TODO Cheng Yan
		ComputationPath simplestPath = new ComputationPath();
		simplestPath.setScore(9999);
		for(ComputationPath path : computationPathList) {
			if(path.getScore() < simplestPath.getScore())
				simplestPath = path;
		}
		if(simplestPath.getScore() != 9999)
			return simplestPath;
		else
			return null;
	}

	public static List<BranchSeedInfo> evaluate(String targetMethod) throws ClassNotFoundException {
		List<BranchSeedInfo> interestedBranches = new ArrayList<>();

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				targetMethod);

		for (Branch branch : branches) {
			int type = evaluate(branch);
			Class<?> cla = cache.get(branch).getTargetType();
			if (type != NO_POOL) {
				interestedBranches.add(new BranchSeedInfo(branch, type,cla));
			}
		}

		return interestedBranches;
	}
}
