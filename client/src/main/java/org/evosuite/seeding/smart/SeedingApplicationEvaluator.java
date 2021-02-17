package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class SeedingApplicationEvaluator {

	public static int STATIC_POOL = 1;
	public static int DYNAMIC_POOL = 2;
	public static int NO_POOL = 3;

	public static Map<Branch, BranchSeedInfo> cache = new HashMap<>();

	public class PathPartition{
		List<ComputationPath> list = new ArrayList<>();
		
		public PathPartition(List<ComputationPath> list) {
			this.list = list;
		}
		
		public ComputationPath getSimplestChannel() {
			ComputationPath simplestPath = null;
			double score = -1;
			
			for(ComputationPath path: list) {
				if(simplestPath == null) {
					simplestPath = path;
					score = path.evaluateFastChannelScore();
				}
				else {
					double newScore = path.evaluateFastChannelScore();
					if(newScore < score) {
						simplestPath = path;
						score = newScore;
					}
				}
			}
			
			return simplestPath;
		}
		
		public boolean isEmpty() {
			return this.list.isEmpty();
		}
	}
	
	public class TwoSidePathList{
		public PathPartition side1 = new PathPartition(new ArrayList<>());
		public PathPartition side2 = new PathPartition(new ArrayList<>());
	}
	
	private static TwoSidePathList separateList(List<ComputationPath> pathList, List<BytecodeInstruction> operands) {
		TwoSidePathList twoSidePathList = new SeedingApplicationEvaluator().new TwoSidePathList();

		Map<BytecodeInstruction, List<ComputationPath>> sides = new HashMap<>();
		
		for(ComputationPath path: pathList) {
			BytecodeInstruction anchor = null;
			if(operands.size() == 1) {
				if(sides.isEmpty()) {
					createSide(sides, path);
				}
				else {
					BytecodeInstruction anchorInPath = findAnchorInPath(sides, path);
					if(anchorInPath != null) {
						List<ComputationPath> list = sides.get(anchorInPath);
						list.add(path);
					}
					else {
						Pair<BytecodeInstruction, BytecodeInstruction> pair 
							= findAnchorSharingCommonNodes(sides, path);
						
						if(pair != null) {
							BytecodeInstruction anchorSharingCommonNode = pair.getLeft();
							BytecodeInstruction commonNode = pair.getRight();
							
							List<ComputationPath> list = sides.get(anchorSharingCommonNode);
							sides.remove(anchorSharingCommonNode);
							
							list.add(path);
							sides.put(commonNode, list);								
						}
						else {
							createSide(sides, path);
						}
						
					}
				}
				
				
			}
			else if(operands.size()==2) {
				BytecodeInstruction operand1 = operands.get(0);
				BytecodeInstruction operand2 = operands.get(1);
				anchor = path.containsInstruction(operand1) ? operand1 : operand2;
				List<ComputationPath> list = sides.get(anchor);
				if(list==null) {
					list = new ArrayList<>();
				}
				list.add(path);
				sides.put(anchor, list);
			}
			else {
				System.err.println("something wrong, operand number is larger than 2");
			}
		}
		
		if(sides.keySet().size()==2) {
			Iterator<BytecodeInstruction> iter = sides.keySet().iterator();
			BytecodeInstruction key1 = iter.next();
			BytecodeInstruction key2 = iter.next();
			twoSidePathList.side1 = new SeedingApplicationEvaluator().new PathPartition(sides.get(key1));
			twoSidePathList.side2 = new SeedingApplicationEvaluator().new PathPartition(sides.get(key2));
		}
		else {
			List<ComputationPath> list = new ArrayList<>();
			for(List<ComputationPath> l: sides.values()) {
				list.addAll(l);
			}
			twoSidePathList.side1 = new SeedingApplicationEvaluator().new PathPartition(list);
			twoSidePathList.side2 = new SeedingApplicationEvaluator().new PathPartition(new ArrayList<>());
		}
		
		return twoSidePathList;
	}

	/**
	 * find a side whose computation paths which share common nodes with the path {@code path},
	 * if we can find such a side, we return pair.left as the anchor of that side, and pair.right as 
	 * the most-root node shared by the paths in the side and the path {@code path}.
	 * @param sides
	 * @param path
	 * @return
	 */
	private static Pair<BytecodeInstruction, BytecodeInstruction> findAnchorSharingCommonNodes(
			Map<BytecodeInstruction, List<ComputationPath>> sides, ComputationPath path) {
		
		for(BytecodeInstruction anchor: sides.keySet()) {
			List<ComputationPath> pathList = sides.get(anchor);
			ComputationPath p = pathList.get(0);
			
			for(int i=p.size()-2; i>=0; i--) {
				if(path.size() > 2 && !p.getInstruction(i).equals(anchor)) {
					BytecodeInstruction ins = path.getInstruction(path.size() - 2);
					BytecodeInstruction pIns = p.getInstruction(i);
					
					if(ins.equals(pIns)) {
						Pair<BytecodeInstruction, BytecodeInstruction> pair = Pair.of(anchor, ins);
						return pair;
					}
				}
			}
		}
		
		return null;
	}

	private static BytecodeInstruction findAnchorInPath(Map<BytecodeInstruction, List<ComputationPath>> sides,
			ComputationPath path) {
		
		for(BytecodeInstruction anchor: sides.keySet()) {
			if(path.containsInstruction(anchor)) {
				return anchor;
			}
		}
		
		return null;
	}

	private static void createSide(Map<BytecodeInstruction, List<ComputationPath>> sides, ComputationPath path) {
		BytecodeInstruction anchor;
		int index = path.size()-2;
		if(path.size() >= 2) {
			anchor = path.getComputationNodes().get(index).getInstruction();
			List<ComputationPath> list = sides.get(anchor);
			if(list==null) {
				list = new ArrayList<>();
			}
			list.add(path);
			sides.put(anchor, list);
		}
	}
	
	public static int evaluate(Branch b) {
		if (cache.containsKey(b)) {
			return cache.get(b).getBenefiticalType();
		}

		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if (branchesInTargetMethod == null)
			return NO_POOL;
		b = compileBranch(branchesInTargetMethod, b);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);
		methodInputs = compileInputs(methodInputs);

		try {
			List<BytecodeInstruction> operands = b.getInstruction().getOperands();

			if (methodInputs != null && operands != null) {
				List<ComputationPath> pathList = new ArrayList<>();
				for (DepVariable input : methodInputs) {
					List<ComputationPath> computationPathList = ComputationPath.computePath(input, operands);
					pathList.addAll(computationPathList);
				}
				removeRedundancy(pathList);

				TwoSidePathList list = separateList(pathList, operands);
				if (list.side1.isEmpty() || list.side2.isEmpty()) {
					return NO_POOL;
				} else {
					ComputationPath path1 = list.side1.getSimplestChannel();
					ComputationPath path2 = list.side2.getSimplestChannel();
					if (path1.isFastChannel() && path2.isFastChannel()
							|| !path1.isFastChannel() && !path2.isFastChannel()) {
						return NO_POOL;
					} else {
//						ComputationPath fastPath = path1.isFastChannel(operands) ? path1 : path2;
						ComputationPath otherPath = path2.isFastChannel() ? path1 : path2;

						if (otherPath.getInstruction(0).isConstant() 
								&& otherPath.size()<=2) {
							if(otherPath.isHardConstant(operands)) {
								cache.put(b, new BranchSeedInfo(b, STATIC_POOL,
										otherPath.getComputationNodes().get(0).getClass()));
								return STATIC_POOL;								
							}
						} 
						else{
							cache.put(b, new BranchSeedInfo(b, DYNAMIC_POOL, null));
							return DYNAMIC_POOL;
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		cache.put(b, new BranchSeedInfo(b, NO_POOL, null));
		return NO_POOL;
	}

	private static void removeRedundancy(List<ComputationPath> pathList) {
		List<ComputationPath> removeList = removeRedundancyPath(pathList);
		if (removeList != null) {
			for (ComputationPath path : removeList) {
				pathList.remove(path);
			}
		}
	}

	private static Set<DepVariable> compileInputs(Set<DepVariable> methodInputs) {
		boolean additionalInstruction = false;
		Set<DepVariable> local = new HashSet<>();
		if (methodInputs == null)
			return methodInputs;
		for (DepVariable input : methodInputs) {
			AbstractInsnNode node = input.getInstruction().getASMNode();
			if (node.getType() == AbstractInsnNode.LDC_INSN) {
				LdcInsnNode ldc = (LdcInsnNode) node;
				String cla = Properties.TARGET_CLASS.replace('.', '/');
				if (ldc.cst.toString().contains(cla + "#")) {
					additionalInstruction = true;
					local = removeVisitedDepVariable(local, input, methodInputs);
					continue;
				}
			}
			if (node.getType() == AbstractInsnNode.INT_INSN) {
				IntInsnNode iins = (IntInsnNode) node;
				if (additionalInstruction && iins.getOpcode() == Opcodes.SIPUSH) {
					additionalInstruction = false;
					continue;
				}
			}
			local.add(input);
		}
		return local;
	}

	private static Set<DepVariable> removeVisitedDepVariable(Set<DepVariable> local, DepVariable input,
			Set<DepVariable> methodInputs) {
		for (DepVariable visit : methodInputs) {
			AbstractInsnNode visitNode = visit.getInstruction().getASMNode();
			if (visitNode.getType() == AbstractInsnNode.INT_INSN) {
				IntInsnNode iins = (IntInsnNode) visitNode;
				if (iins.getOpcode() == Opcodes.SIPUSH
						&& visit.getInstruction().getLineNumber() == input.getInstruction().getLineNumber()) {
					if (local.contains(visit))
						local.remove(visit);
					break;
				}
			}

		}
		return local;
	}

	private static Branch compileBranch(Map<Branch, Set<DepVariable>> branchesInTargetMethod, Branch b) {
		Branch targetBranch = b;
//		targetBranch.getInstruction();
		List<Branch> lineBranches = new ArrayList<>();
		for (Branch br : branchesInTargetMethod.keySet()) {
			String[] s = b.toString().split(" ");
			String info = s[3] + " " + s[4];
			if (br.toString().contains(info) && !br.equals(b)) {
				targetBranch = br;
				break;
			}
			if (br.toString().contains(s[4])) {
				lineBranches.add(br);
			}
		}
		if (lineBranches.size() == 1 && !lineBranches.get(0).equals(b)) {
			targetBranch = lineBranches.get(0);
		}
		return targetBranch;
	}

//	private static Class<?> findOpcodeType(ComputationPath otherPath) {
//		int size = otherPath.getComputationNodes().size();
//		for (DepVariable n : otherPath.getComputationNodes()) {
//			BytecodeInstruction bi = n.getInstruction();
//			AbstractInsnNode node = bi.getASMNode();
//			if (node instanceof TypeInsnNode) {
//				TypeInsnNode tNode = (TypeInsnNode) node;
//				String des = tNode.desc.replace("/", ".");
//				Class<?> cla;
//				try {
//					if (Class.forName(des) != null) {
//						cla = Class.forName(des);
//						return cla;
//					}
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//			} else if (node instanceof MethodInsnNode) {
//				MethodInsnNode mNode = (MethodInsnNode) node;
//				String des = mNode.desc.replace("/", ".");
//				Class<?> cla;
//				try {
//					if (Class.forName(des) != null) {
//						cla = Class.forName(des);
//						return cla;
//					}
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//			int opcode = bi.getASMNode().getOpcode();
//			switch (opcode) {
//			case 0x97:
//				return double.class;
//
//			}
//		}
//
//		return null;
//	}

	private static List<ComputationPath> removeRedundancyPath(List<ComputationPath> pathList) {
		List<ComputationPath> localPathList = new ArrayList<>();
		List<ComputationPath> constant = new ArrayList<>();
		for (int i = 0; i < pathList.size(); i++) {
			ComputationPath path = pathList.get(i);
			int size = path.getComputationNodes().size();
			for (int j = i + 1; j < pathList.size(); j++) {
				ComputationPath pathNext = pathList.get(j);
				int sizeNext = pathNext.getComputationNodes().size();

				// method inputs to remove

				// have same input line and output line
				if (path.getComputationNodes().get(0).getInstruction().getLineNumber() == pathNext.getComputationNodes()
						.get(0).getInstruction().getLineNumber()
						&& path.getComputationNodes().get(size - 1).getInstruction().getLineNumber() == pathNext
								.getComputationNodes().get(sizeNext - 1).getInstruction().getLineNumber()) {
					if (ComputationPath.isStartWithMethodInput(path)) {
						localPathList.add(pathNext);
					} else {
						if (path.getScore() <= pathNext.getScore())
							localPathList.add(pathNext);
					}
				}

				// have same path
				if (size > 2 && sizeNext > 2) {
					if (path.getComputationNodes().get(size - 2)
							.equals(pathNext.getComputationNodes().get(sizeNext - 2)))
						localPathList.add(pathNext);
				}

				// start with method has 0
				if (pathNext.getComputationNodes().get(sizeNext - 1).getInstruction().explain().contains("StartsWith")
						&& pathNext.getComputationNodes().get(0).getInstruction().explain().contains("ICONST_0")) {
					if (!localPathList.contains(pathNext)) {
						localPathList.add(pathNext);
					}
				}

			}
			// constant
			if (path.getScore() < 3) {
				if (path.getComputationNodes().get(0).isConstant()) {
					constant.add(path);
					for (ComputationPath cons : localPathList) {
						if (cons.equals(path)) {
							localPathList.remove(path);
							break;
						}
					}
					for (ComputationPath small : constant) {
						if (small.getScore() < path.getScore()) {
							localPathList.add(path);
						}
					}
				}
			}

		}
		if (localPathList.size() != 0) {
			return localPathList;
		} else
			return null;
	}

//	private static ComputationPath findTheOtherPath(ComputationPath path, List<ComputationPath> pathList) {
//		// TODO Cheng Yan
//		ComputationPath theOtherPath = new ComputationPath();
//		for (ComputationPath otherPath : pathList) {
//			if (otherPath != path) {
//				theOtherPath = otherPath;
//				return theOtherPath;
//			}
//
//		}
//		return null;
//	}
//
//	private static ComputationPath findSimplestPath(List<ComputationPath> computationPathList) {
//		// TODO Cheng Yan
//		ComputationPath simplestPath = new ComputationPath();
//		simplestPath.setScore(9999);
//		boolean hasVar = false;
//		for (int i = 0; i < computationPathList.size(); i++) {
//			if (computationPathList.get(i).getScore() < simplestPath.getScore() && !hasVar)
//				simplestPath = computationPathList.get(i);
//			for (DepVariable node : computationPathList.get(i).getComputationNodes()) {
//				BytecodeInstruction ins = node.getInstruction();
//				if (ins.isLocalVariableUse()) {
//					hasVar = true;
//					simplestPath = computationPathList.get(i);
//				}
//			}
//
//		}
////		for (ComputationPath path : computationPathList) {
////			
////			if (path.getScore() < simplestPath.getScore())
////				simplestPath = path;
////		}
//		if (simplestPath.getScore() != 9999)
//			return simplestPath;
//		else
//			return null;
//	}

	public static List<BranchSeedInfo> evaluate(String targetMethod) throws ClassNotFoundException {
		List<BranchSeedInfo> interestedBranches = new ArrayList<>();

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				targetMethod);

		for (Branch branch : branches) {
			int type = evaluate(branch);
			Class<?> cla = cache.get(branch).getTargetType();
			if (type != NO_POOL) {
				interestedBranches.add(new BranchSeedInfo(branch, type, cla));
			}
		}

		return interestedBranches;
	}
}