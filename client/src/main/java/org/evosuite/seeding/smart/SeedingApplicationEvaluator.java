package org.evosuite.seeding.smart;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.TestSuiteGenerator;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.ga.metaheuristics.mosa.AbstractMOSA;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cdg.ControlDependenceGraph;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.interestednode.IInterestedNodeFilter;
import org.evosuite.graphs.interprocedural.interestednode.SmartSeedInterestedNodeFilter;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.testcase.SensitivityPreservance;
import org.evosuite.utils.MethodUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

import com.sun.tools.internal.ws.wsdl.parser.Constants;

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
//			double score = -1;
			
			for(ComputationPath path: list) {
				if(simplestPath == null) {
					simplestPath = path;
				}
				else {
					if(simplestPath.isFastChannel())
						break;
					if(path.isFastChannel())
						simplestPath = path;
				}
			}
			
//			for(ComputationPath path: list) {
//				if(simplestPath == null) {
//					simplestPath = path;
//					score = path.evaluateFastChannelScore();
//				}
//				else {
//					double newScore = path.evaluateFastChannelScore();
//					if(newScore < score) {
//						simplestPath = path;
//						score = newScore;
//					}
//				}
//			}
			
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
			
			if(path.shouldIgnore()) {
				continue;
			}
			
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
						System.currentTimeMillis();
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
		else {
			anchor = path.getComputationNodes().get(path.size()-1).getInstruction();
			List<ComputationPath> list = sides.get(anchor);
			if(list==null) {
				list = new ArrayList<>();
			}
			list.add(path);
			sides.put(anchor, list);
		}
	}
	

	public static BranchSeedInfo evaluate(Branch b) {
		if (cache.containsKey(b)) {
			return cache.get(b);
		}
		
		long t1 = System.currentTimeMillis();
		if(b.toString().contains("NULL")) {
			BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL,null);
			cache.put(b, branchInfo);
			return branchInfo;
		}
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if (branchesInTargetMethod == null) {
			BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null);
			cache.put(b, branchInfo);
			return branchInfo;
		}
		b = compileBranch(branchesInTargetMethod, b);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);
		methodInputs = compileInputs(methodInputs);

		List<DepVariable> constants = collectConstants(methodInputs, b);
		
		try {
			List<BytecodeInstruction> operands = b.getInstruction().getOperands();

			if (methodInputs != null && operands != null) {
				List<ComputationPath> pathList = new ArrayList<>();
				for (DepVariable input : methodInputs) {
					List<ComputationPath> computationPathList = ComputationPath.computePath(input, b);
					pathList.addAll(computationPathList);
				}
				
				removeRedundancy(pathList);
				AbstractMOSA.pathNum += pathList.size();
				List<ComputationPath> fastChannels = analyzeFastChannels(pathList);
				
				/** if there is a fast channel, we observe if there is any constants? 
				 * if yes, it is static, otherwise, it is dynamic
				 */
				if(fastChannels.size() != 0) {
					if(isRelevantToRegularExpression(b)) {
						return branchInfo(fastChannels, b, DYNAMIC_POOL);													
					}
					
					List<ComputationPath> nonFastChannelList = checkNonfastchannels(pathList, fastChannels);
					if (nonFastChannelList.size() == 0) {
						if (b.getInstruction().isSwitch())
							return new BranchSeedInfo(b, STATIC_POOL, BranchSeedInfo.INT);
						return branchInfo(fastChannels, b, DYNAMIC_POOL);
					}
					else {
						if (!constants.isEmpty()) {
							return branchInfo(fastChannels, b, STATIC_POOL);
						} else {
							return branchInfo(fastChannels, b, DYNAMIC_POOL);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null);
		cache.put(b, branchInfo);
		System.out.println("NO_POOL_1:" + b);
		return branchInfo;
	}

	private static List<ComputationPath> checkNonfastchannels(List<ComputationPath> pathList,
			List<ComputationPath> fastChannels) {
		List<ComputationPath> nonFastChannelList = new ArrayList<>();
		for(ComputationPath p : pathList) {
			if(!fastChannels.contains(p))
				nonFastChannelList.add(p);
		}
		return nonFastChannelList;
	}
	
	//TODO Cheng Yan
	private static boolean isRelevantToRegularExpression(Branch b) {
		if (b.getClassName().equals("java.lang.String")) {
			String calledMName = b.getInstruction().getCalledMethodName();
			if(calledMName.contains("matches")) {
				return true;
			}
		}
		return false;
	}

	private static void addSwitchConstants(Branch b, List<Object> constants) {
		DepVariable var = new DepVariable(b.getInstruction());
		if(var.getInstruction().getASMNode().getType() == AbstractInsnNode.LOOKUPSWITCH_INSN) {
			LookupSwitchInsnNode lNode = (LookupSwitchInsnNode)var.getInstruction().getASMNode();
			for(int i : lNode.keys) {
				constants.add(i);
			}
		}
	}

	private static BranchSeedInfo branchInfo(List<ComputationPath> fastChannels, Branch b, int type) {
		if(type == NO_POOL) {
			BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null);
			cache.put(b, branchInfo);
			System.out.println("type:" + b + ":" + type);
			return branchInfo;
		}
		
		String dataType = getDynamicDataType(fastChannels.get(0));
		if(dataType.equals("boolean"))
			type = NO_POOL;
		BranchSeedInfo branchInfo = new BranchSeedInfo(b, type, dataType);
		cache.put(b, branchInfo);
		System.out.println("type:" + b + ":" + type);
		return branchInfo;
	}

	private static boolean isBooleanMethod(ComputationPath p) {
		if (p.size() == 2 && p.getComputationNodes().get(0).isMethodInput()
				&& p.getComputationNodes().get(1).toString().contains("java/lang/Boolean.booleanValue()Z")) {
			return true;
		}
		return false;
	}

	private static List<DepVariable> collectConstants(Set<DepVariable> methodInputs, Branch b) {
		List<DepVariable> constants = new ArrayList<>();
		if (methodInputs == null)
			return constants;
		for (DepVariable var : methodInputs) {
			if (var.getInstruction().isConstant()) {
				if (var.getClassName().equals(Properties.TARGET_CLASS)
						&& var.getMethodName().split("#")[1].equals(Properties.TARGET_METHOD))
					constants.add(var);
			}
		}
		
//		if(b.getInstruction().isSwitch()) {
//			addSwitchConstants(b, constants);			
//		}
		
		return constants;
	}

	private static List<ComputationPath> analyzeFastChannels(List<ComputationPath> pathList) {
		List<ComputationPath> paths = new ArrayList<>();
		for(ComputationPath path: pathList) {
			if(path.isFastChannel()) {
				paths.add(path);
			}
			else {
				boolean visitedTargetMethod = false;
				for(int i=path.size()-1; i>=0; i--) {
					DepVariable var = path.getComputationNodes().get(i);
					BytecodeInstruction ins = var.getInstruction();
					if(ins.isMethodCall()) {
						String method = ins.getCalledMethod();
						
						if(!isBooleanReturnType(method)) {
							break;
						}
						
						if(isMethodInStopList(method)) {
							continue;
						}
						
						if(visitedTargetMethod)
							continue;
						visitedTargetMethod = true;
						String clazz = ins.getCalledMethodsClass();
						if(clazz.equals("java.util.List")) {
							clazz = "java.util.ArrayList";
						}
						
						InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
						String className = clazz;
						String methodName = ins.getCalledMethod();
						ActualControlFlowGraph graph = GraphPool.getInstance(classLoader).getActualCFG(clazz, methodName);
						
						Properties.ALWAYS_REGISTER_BRANCH = true;
						if(graph == null) {
							GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
							graph = GraphPool.getInstance(classLoader).getActualCFG(clazz, methodName);
						}
						
						if(graph != null) {
							ControlDependenceGraph cdg = GraphPool.getInstance(classLoader).getCDG(className, methodName);
							if(cdg == null) {
								GraphPool.getInstance(classLoader).registerActualCFG(graph);
								cdg = GraphPool.getInstance(classLoader).getCDG(className, methodName);
							}
							long t1 = System.currentTimeMillis();
							List<Branch> relevantBranches = analyzeRelevantBranches(graph);
							for (Branch branch : relevantBranches) {
								List<BytecodeInstruction> ops = branch.getInstruction().getOperands();
								
								if(branch.toString().contains("NULL"))
									continue;
								
								ops = reanalyze(ops, branch);
								
								DepVariable header = path.getComputationNodes().get(0);
								
								for (BytecodeInstruction op : ops) {
									
									SensitivityPreservance sp = SensitivityMutator
											.testBranchSensitivity(header, op, path.getBranch());

									if (sp.isSensitivityPreserving() && sp.isValuePreserving()) {
										paths.add(path);
									}
								}
								if(!paths.isEmpty())
									break;
							}
							long t2 = System.currentTimeMillis();
							AbstractMOSA.cascadeAnalysisTime += t2 - t1; 
						}
						
						Properties.ALWAYS_REGISTER_BRANCH = false;
						
					}
				}
				
				System.currentTimeMillis();
			}
		}
		
		return paths;
	}

	@SuppressWarnings("rawtypes")
	private static List<BytecodeInstruction> reanalyze(List<BytecodeInstruction> ops, Branch branch) {
		if(ops.size() > 1) {
			return ops;
		}
		else if(ops.size() == 1){
			BytecodeInstruction ins = ops.get(0);
			
			Frame frame = ins.getFrame();
			
			if(frame.getStackSize() == 2 && ins.isMethodCall()) {
				
				List<BytecodeInstruction> list = new ArrayList<>();
				for(int i=0; i<2; i++) {
					int index = frame.getStackSize() - i - 1;
					Value val = frame.getStack(index);
					
					if(val instanceof SourceValue) {
						SourceValue sValue = (SourceValue)val;
						InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
						MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, ins.getClassName(), ins.getMethodName());
						
						ActualControlFlowGraph cfg = ins.getActualCFG(); 
						for(AbstractInsnNode insNode: sValue.insns) {
							BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);
							list.add(defIns);
						}
						
					}
					
				}
				
				return list;
			}
	
		}
		
		return ops;
	}

	private static List<Branch> analyzeRelevantBranches(ActualControlFlowGraph graph) {
		List<BytecodeInstruction> insList = graph.getAllInstructions();
		List<BytecodeInstruction> returnInsList = findAllReturnIns(insList);
		
		List<Branch> branches = new ArrayList<>();
		for(BytecodeInstruction returnIns: returnInsList) {
			for(ControlDependency dep: returnIns.getControlDependencies()) {
				if(dep != null && !branches.contains(dep.getBranch())) {
					branches.add(dep.getBranch());
				}
			}
		}
		
		return branches;
	}

	private static List<BytecodeInstruction> findAllReturnIns(List<BytecodeInstruction> insList) {
		List<BytecodeInstruction> returnInsList = new ArrayList<>();
		for(BytecodeInstruction ins: insList) {
			if(ins.isReturn()) {
				returnInsList.add(ins);
			}
		}
		return returnInsList;
	}

	private static boolean isMethodInStopList(String method) {
		if(method.equals("booleanValue()Z") || method.equals("valueOf(Z)Ljava/lang/Boolean;")) {
			return true;
		}
		return false;
	}

	private static boolean isBooleanReturnType(String method) {
		String returnType = method.substring(method.indexOf(")")+1, method.length());
		return returnType.equals("Z") || returnType.equals("Ljava/lang/Boolean;");
	}

	private static String getDynamicDataType(ComputationPath otherPath) {
		List<DepVariable> computationNodes = otherPath.getComputationNodes();
		
		List<String> list = new ArrayList<String>();
		for(DepVariable n: computationNodes) {
			String str = n.getDataType();
			if(!str.equals(BranchSeedInfo.OTHER)) {
				str = finalType(str);
				list.add(str);
			}
		}
		
		if(list.isEmpty()) {
			return BranchSeedInfo.OTHER;
		}
		
		return list.get(list.size()-1);
//		System.currentTimeMillis();
//		List<String> fastpathTypes = new ArrayList<>();
//		BytecodeInstruction input = computationNodes.get(0).getInstruction();
//		BytecodeInstruction oprand = computationNodes.get(computationNodes.size() - 1).getInstruction();
//		BytecodeInstruction oprates = computationNodes.get(computationNodes.size() - 2).getInstruction();
//		
//		if(computationNodes.get(0).isParameter()) {
//			String inputTypes = input.getVariableName();
//			int i = Integer.parseInt(inputTypes.split("LV_")[1]);
//			String[] separateTypes = MethodUtil.parseSignature(input.getMethodName());
//			String inputType = separateTypes[i - 1];
//			fastpathTypes.add(inputType);
//			System.currentTimeMillis();
//		}
//		if(oprand.isInvokeSpecial() || oprand.isInvokeStatic()) {
//			DepVariable lastNode = computationNodes.get(computationNodes.size() - 2);
//			int i = relationNum(lastNode);
//			String[] outputTypes = MethodUtil.parseSignature(oprand.getMethodName());
//			String outputType = outputTypes[i];
//			fastpathTypes.add(outputType);
//			return finalType(outputType);
//		}
		
		
		
//		return null;
	}

	private static int relationNum(DepVariable lastNode) {
		List<DepVariable>[] relations = lastNode.getRelations();
		for(int i = 0; i < relations.length; i++) {
			if(relations[i] != null)
				return i;
		}
		return 0;
	}

//	private static String getConstantDataType(ComputationPath otherPath) {
//		BytecodeInstruction ins = otherPath.getInstruction(0);
//		String types[] = ins.getASMNodeString().split(" ");
//		for(String i : types) {
//			if(i.equals("LDC")) {
//				LdcInsnNode node =  (LdcInsnNode)ins.getASMNode();
//				Object cst = node.cst;
//				return finalType(cst.getClass().getName());
//			}
//			switch(i){
//				case "BYTE":
//				case "DOUBLE":
//				case "FLOAT":
//				case "INT":
//				case "LONG":
//				case "SHORT":
//				case "STRING":
//					return i.toLowerCase();
//				default:
//					return "OTHER".toLowerCase();
//				}
//		}
//			
//		return null;
//	}
	
	private static String getConstantDataType(Object obj) {
		if(!(obj instanceof DepVariable)) {
			return "OTHER".toLowerCase();
		}
		DepVariable var = (DepVariable) obj;
		BytecodeInstruction ins = var.getInstruction();
		String types[] = ins.getASMNodeString().split(" ");
		for(String i : types) {
			if(i.equals("LDC")) {
				LdcInsnNode node =  (LdcInsnNode)ins.getASMNode();
				Object cst = node.cst;
				return finalType(cst.getClass().getName());
			}
			switch(i){
				case "BYTE":
				case "DOUBLE":
				case "FLOAT":
				case "INT":
				case "LONG":
				case "SHORT":
				case "STRING":
					return i.toLowerCase();
				default:
					return "OTHER".toLowerCase();
				}
		}
			
		return null;
	}

	public static String finalType(String name) {
		String type[] =  name.split("\\.");
		if(type[type.length - 1].equals("Integer"))
			return BranchSeedInfo.INT;
		else
			return type[type.length - 1].toLowerCase();
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
			String[] s1 = br.toString().split(" ");
			
			for(int i = 0;i < s.length;i++) {
				if(s[0].equals(s1[0]) && i == 2) {
					String less0 = b.toString().split(s[i].toString(),0)[1];
					String less1 =br.toString().split(s1[i].toString(),0)[1];
					if(less0.equals(less1)) {
						targetBranch = br;
					}
				}
			}
//			if(s[0].equals(s1[0])) {
//				targetBranch = br;
//				System.currentTimeMillis();
//			}
		}
//			String info = s[3] + " " + s[4];
//			if (br.toString().contains(info) && !br.equals(b)) {
//				targetBranch = br;
//				break;
//			}
//			if (br.toString().contains(s[4])) {
//				lineBranches.add(br);
//			}
//		}
//		if (lineBranches.size() == 1 && !lineBranches.get(0).equals(b)) {
//			targetBranch = lineBranches.get(0);
//		}
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
						if (size <= sizeNext)
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
			if (path.getComputationNodes().size() < 3) {
				if (path.getComputationNodes().get(0).isConstant()) {
					constant.add(path);
					for (ComputationPath cons : localPathList) {
						if (cons.equals(path)) {
							localPathList.remove(path);
							break;
						}
					}
					for (ComputationPath small : constant) {
						if (small.getComputationNodes().size() < path.getComputationNodes().size()) {
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
			BranchSeedInfo info = evaluate(branch);
//			Class<?> cla = cache.get(branch).getTargetType();
			if (info.getBenefiticalType() != NO_POOL) {
				interestedBranches.add(info);
			}
		}

		return interestedBranches;
	}
}
