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
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.ga.metaheuristics.mosa.AbstractMOSA;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cdg.ControlDependenceGraph;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.instrumentation.BytecodeInstrumentation;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.statements.ValueStatement;
import org.evosuite.utils.Randomness;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

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
	
	public static BranchSeedInfo evaluate(Branch b, TestChromosome testSeed, BranchFitness bf) {
		
//		for(Branch br: cache.keySet()) {
//			BranchSeedInfo info = cache.get(br);
//			if(info.getBenefiticalType() == SeedingApplicationEvaluator.NO_POOL) {
//				List<ObservedConstant> o = info.getValuePreservance().getEstiamtedConstants();
//				System.currentTimeMillis();
//			}
//		}
		
		if (cache.containsKey(b)) {
			BranchSeedInfo info = cache.get(b);
			if(info.getBenefiticalType() == SeedingApplicationEvaluator.NO_POOL && !b.toString().contains("NULL")) {
				double value = Randomness.nextDouble(0, 1);
				if(value > 0.1) {
					return info;
				}
			}
			else {
				return info;
			}
			
		}
		
		if(b == null || b.toString().contains("NULL")) {
			BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null, null);
			cache.put(b, branchInfo);
			return branchInfo;
		}
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if (branchesInTargetMethod == null) {
			BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null, null);
			cache.put(b, branchInfo);
			return branchInfo;
		}
		Set<DepVariable> nodes = branchesInTargetMethod.get(b);
		if(nodes == null) {
			nodes = branchesInTargetMethod.get(compileBranch(branchesInTargetMethod, b));
		}
		Set<DepVariable> methodInputs = compileInputs(nodes);

		try {
			List<BytecodeInstruction> operands = b.getInstruction().getOperands();
			if (methodInputs != null && operands != null) {

				if (b.isSwitchCaseBranch()) {
					String key1 = b.toString();
					int index1 = key1.indexOf("SWITCH");
					int index2 = key1.indexOf("Case");
					if(key1.contains("Default")) {
						index2 = key1.indexOf("Default") - 1;
					}
					if (cache.size() != 0) {
						BranchSeedInfo info = checkReusableSwithBranches(b, key1, index1, index2);
						if(info != null) {
							return info;
						}
					}
				}
				if(b.getInstruction().getLineNumber() == 90) {
					System.currentTimeMillis();
				}
				
				ValuePreservance preservance = analyzeChannel(methodInputs, b, testSeed, bf);

				System.currentTimeMillis();
				if (preservance != null && preservance.isValuePreserving()) {
					List<MatchingResult> results = preservance.getMatchingResults();
					MatchingResult result = Randomness.choice(results);
					ValueStatement statement = result.getMatchedInputVariable();
					TestChromosome referredTest = new TestChromosome();
					referredTest.setTestCase(statement.getTestCase());
					
					if(isRelevantToRegularExpression(methodInputs)){
						String type = "string";
						BranchSeedInfo branchInfo = new BranchSeedInfo(b, DYNAMIC_POOL, type, preservance);
						branchInfo.referredTest = referredTest;
						cache.put(b, branchInfo);
						System.out.println("DYNAMIC_POOL type:" + b + ":" + type);
						AbstractMOSA.smartBranchNum += 1;
						AbstractMOSA.runtimeBranchType.put(b.getInstruction().toString(), "DYNAMIC_POOL");
						return branchInfo;
					}
					
					/**
					 * label as static pool
					 */
					List<ObservedConstant> staticConstants = preservance.getEstiamtedStaticConstants(); 
					if (!staticConstants.isEmpty()) {
						ObservedConstant obConstant = staticConstants.get(0);
						String type = finalType(result.getMatchedInputVariable().getAssignmentValue().getClass().toString());
						BranchSeedInfo branchInfo = new BranchSeedInfo(b, STATIC_POOL, type, preservance);
						branchInfo.referredTest = referredTest;
						cache.put(b, branchInfo);
						System.out.println("STATIC_POOL type:" + b + ":" + type);
						
						AbstractMOSA.smartBranchNum += 1;
						AbstractMOSA.runtimeBranchType.put(b.getInstruction().toString(),"STATIC_POOL");
						
						updateTestSeedWithConstantAssignment(result, statement, staticConstants, branchInfo);
						return branchInfo;
					} 
					/**
					 * label as dynamic pool
					 */
					else {
						List<ObservedConstant> dynamicConstants = preservance.getEstiamtedDynamicConstants();
						
						String type = finalType(preservance.getMatchingResults().get(0).getMatchedObservation().getClass().toString());
						BranchSeedInfo branchInfo = new BranchSeedInfo(b, DYNAMIC_POOL, type, preservance);
						branchInfo.referredTest = referredTest;
						cache.put(b, branchInfo);
						System.out.println("DYNAMIC_POOL type:" + b + ":" + type);
						AbstractMOSA.smartBranchNum += 1;
						AbstractMOSA.runtimeBranchType.put(b.getInstruction().toString(),"DYNAMIC_POOL");
						
						updateTestSeedWithConstantAssignment(result, statement, dynamicConstants, branchInfo);
						return branchInfo;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null, null);
		cache.put(b, branchInfo);
		System.out.println("NO_POOL_1:" + b);
		return branchInfo;
	}

	private static BranchSeedInfo checkReusableSwithBranches(Branch b, String key1, int index1, int index2) {
		for (Branch b0 : cache.keySet()) {
			if (b0.isSwitchCaseBranch() && (b0.toString().contains(key1.substring(index1, index2)))) {
				cache.put(b, cache.get(b0));
				System.out.println(b + ":" + cache.get(b0).getBenefiticalType());
				
				if(cache.get(b0).getBenefiticalType() != SeedingApplicationEvaluator.NO_POOL) {
					AbstractMOSA.smartBranchNum += 1;
					String branchType = cache.get(b0).getBenefiticalType() == SeedingApplicationEvaluator.STATIC_POOL ? "STATIC_POOL" : "DYNAMIC_POOL";
					AbstractMOSA.runtimeBranchType.put(b.getInstruction().toString(), branchType);
				}
				return cache.get(b0);
			}
		}
		
		return null;
	}

	private static void updateTestSeedWithConstantAssignment(MatchingResult result, ValueStatement statement,
			List<ObservedConstant> observedConstants, BranchSeedInfo branchInfo) {
		if(observedConstants.isEmpty()) return;
		for (ObservedConstant obj : observedConstants) {
			branchInfo.addPotentialSeed(obj);
			try {
				if (statement != null) {
					if (obj.isCompatible(statement.getAssignmentValue())) {
						if (statement instanceof ValueStatement) {
							((ValueStatement) statement).setAssignmentValue(obj.getValue());
						}
						// correlation
						if (result.needRelaxedMutation()) {
							if (result.getMatchedObservation() instanceof Character
									&& obj.getValue() instanceof Integer) {
								int in = (Integer) obj.getValue();
								Character c = (char) in;

								Object objValue = statement.getAssignmentValue();
								if (objValue != null) {
									String stringAddChar = objValue.toString().concat(c.toString());
									statement.setAssignmentValue(stringAddChar);
								}

							} else {
								Object objValue = statement.getAssignmentValue();
								if (objValue != null) {
									String appendString = objValue.toString().concat(obj.getValue().toString());
									statement.setAssignmentValue(appendString);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isRelevantToRegularExpression(Set<DepVariable> methodInputs) {
		
		if(methodInputs == null || methodInputs.isEmpty()) return false;
		
		DepVariable var = methodInputs.iterator().next();
		RawControlFlowGraph graph = var.getInstruction().getRawCFG();
		for(int i=0; i<graph.vertexCount(); i++) {
			BytecodeInstruction ins = graph.getInstruction(i);
			if(ins.isMethodCall()) {
				MethodInsnNode mNode = (MethodInsnNode) ins.getASMNode();
				if (mNode.owner.equals("org/evosuite/instrumentation/testability/StringHelper")) {
					String calledMName = mNode.name;
					if (calledMName.toLowerCase().contains("matches") || calledMName.equals("StringMatchRegex"))
						return true;
				}
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
			BranchSeedInfo branchInfo = new BranchSeedInfo(b, NO_POOL, null, null);
			cache.put(b, branchInfo);
			System.out.println("type:" + b + ":" + type);
			return branchInfo;
		}
		
		String dataType = getDynamicDataType(fastChannels.get(0));
//		if(dataType.equals("boolean"))
//			type = NO_POOL;
		BranchSeedInfo branchInfo = new BranchSeedInfo(b, type, dataType, null);
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

	private static List<DepVariable> collectConstants(Set<DepVariable> methodInputs, Branch b, List<ComputationPath> nonFastChannels) {
		List<DepVariable> constants = new ArrayList<>();
		System.currentTimeMillis();
		for (ComputationPath nonFastChannel : nonFastChannels) {
			int index = nonFastChannel.size() - 1;
			DepVariable operand = nonFastChannel.getComputationNodes().get(index);

			if (operand.isConstant()) {
				constants.add(operand);
			} else if (operand.isMethodCall()) {
				MethodInsnNode mNode = (MethodInsnNode) operand.getInstruction().getASMNode();
				if (mNode.owner.equals("org/evosuite/instrumentation/testability/StringHelper")) {
					if (nonFastChannel.isPureConstantPath())
						constants.add(nonFastChannel.getComputationNodes().get(0));
				}
			} 
		}

		return constants;
	}
	
	private static ValuePreservance analyzeChannel(Set<DepVariable> inputs, Branch targetBranch,
			TestChromosome testSeed, BranchFitness bf) {
		/**
		 * the operands corresponding to method inputs and constants
		 */
		System.currentTimeMillis();
		List<BytecodeInstruction> observations = parseRelevantOperands(targetBranch);
		
		List<DepVariable> headers = new ArrayList<>(inputs); 
		ValuePreservance sp = SensitivityMutator.testBranchSensitivity(headers, observations, targetBranch, testSeed, bf);
		return sp;
	}


	private static List<DepVariable> addNotConstantHeaders(Set<DepVariable> inputs) {
		List<DepVariable> headers = new ArrayList<>();
		for(DepVariable var : inputs) {
			if(!var.isConstant())
				headers.add(var);
		}
		return headers;
	}

	private static boolean compareArrayLength(List<ComputationPath> pathList) {
		for (ComputationPath path : pathList) {
			DepVariable var = path.getTail();
			if (!(var.getInstruction().getASMNode().getOpcode() == Opcodes.ARRAYLENGTH)) {
				return false;
			}
		}
		return true;
	}


	private static void addAllField(List<DepVariable> headers, Set<DepVariable> inputs) {
		for(DepVariable var : inputs) {
			if(var.isStaticField() || var.isInstaceField()) {
				if(!headers.contains(var))
					headers.add(var);
			}
		}		
	}

	private static boolean allConstantsHeaders(List<DepVariable> headers) {
		for(DepVariable var : headers) {
			if(!var.isConstant()) return false;
		}
		return true;
	}

	private static List<DepVariable> retrieveHeads(List<ComputationPath> pathList) {
		List<DepVariable> varList = new ArrayList<>();
		for(ComputationPath path: pathList) {
			DepVariable var = path.getFirstPrimitiveNode();
			System.currentTimeMillis();
			if(var != null && !varList.contains(var)) {
				varList.add(var);				
			}
			
			//switch
			if(path.getBranch().isSwitchCaseBranch()) {
				var = path.getComputationNodes().get(0);
				if(var != null && !varList.contains(var)) {
					varList.add(var);				
				}
			}
		}
		
		return varList;
	}

	private static List<BytecodeInstruction> parseRelevantOperands(Branch targetBranch) {
		List<BytecodeInstruction> list = new ArrayList<>();
		parseRelevantOperands(targetBranch.getInstruction(), list);
		System.currentTimeMillis();
		return list;
	}
	
	
	private static BytecodeInstruction searchFieldDefinition(BytecodeInstruction getField) {
		
		FieldInsnNode fNode = (FieldInsnNode) getField.getASMNode();
		BytecodeInstruction i = getField;
		
		while(i != null) {
			i = i.getPreviousInstruction();
			
			if(i == null) {
				return null;
			}
			
			if(i.isFieldDefinition()) {
				FieldInsnNode iNode = (FieldInsnNode) i.getASMNode();
				
				
				if(iNode.name.equals(fNode.name)) {
					return i;
				}
			}
		}
		
		return null;
	}
	
	private static void add(List<BytecodeInstruction> list, BytecodeInstruction ins) {
		if(ins != null && !list.contains(ins)) {
			if(!ins.isDefinition()) {
				list.add(ins);					
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void parseRelevantOperands(BytecodeInstruction targetIns, List<BytecodeInstruction> list) {
		System.currentTimeMillis();
		DepVariable var = new DepVariable(targetIns);

		// element index
//		if (var.getName().equals("array index"))
		if (var.isLoadArrayElement()) {
			add(list, var.getInstruction());
			return;
		}
		
		if(var.isPrimitive()) {
			add(list, var.getInstruction());
		}		
		if(targetIns.toString().contains("NULL")){
			return;
		}
		
		if(targetIns.isMethodCall()) {
			String methodName = targetIns.getCalledMethod();
			String calledClass =targetIns.getCalledMethodsClass();
			if(isBooleanReturnType(methodName) || isBooleanReturnClass(calledClass)) {
				if(methodName.equals("booleanValue()Z")) {
					BytecodeInstruction getfield = targetIns.getSourceOfMethodInvocationInstruction();
					BytecodeInstruction setfield = searchFieldDefinition(getfield);
					if(setfield != null) {
						parseRelevantOperands(setfield, list);						
					}
				}
				else if(methodName.equals("valueOf(Z)Ljava/lang/Boolean;")) {
					BytecodeInstruction ins = targetIns.getOperands().get(0);
					parseRelevantOperands(ins, list);
				}
				
				String clazz = targetIns.getCalledMethodsClass();
				
				//TODO handle subclasses and implementation
				if(clazz.equals("java.util.List")) {
					clazz = "java.util.ArrayList";
				}
				
				if(BytecodeInstrumentation.checkIfCanInstrument(clazz)) {
					Properties.ALWAYS_REGISTER_BRANCH = true;
					ActualControlFlowGraph graph = parseGraph(clazz, methodName);
					Properties.ALWAYS_REGISTER_BRANCH = false;
					List<BytecodeInstruction> returnInsList = findAllReturnIns(graph);
					for (BytecodeInstruction returnIns : returnInsList) {
						List<BytecodeInstruction> ops = returnIns.getOperands();
						ops = reanalyze(ops);
						for (BytecodeInstruction op : ops) {
							if(op.isConstant()) {
								for(Branch b: op.getControlDependentBranches()) {
									parseRelevantOperands(b.getInstruction(), list);
								}
							}
							else {
								parseRelevantOperands(op, list);							
							}
						}
					}
				}
				else {
					//TODO can get array list observation (ALOAD)
					getInstructionOperands(targetIns,list);
					return;
				}
			}
			else {
				BytecodeInstruction ins = targetIns.getNextInstruction();
				if(!ins.isBranch()) {
					add(list, ins);
					return;
				}else {
					getInstructionOperands(ins,list);
					return;
				}
			}
			
			return;
		}
		
		if (var.isCompare()) {
			for (BytecodeInstruction ins : targetIns.getOperands()) {
				if(passMethodInfoIntInstruction(ins)) continue;
				add(list, ins);					
			}
			return;
		}

		for (BytecodeInstruction ins : targetIns.getOperands()) {
			if(passMethodInfoIntInstruction(ins)) continue;
			parseRelevantOperands(ins, list);
		}
		
		if(targetIns.getOperands().size() == 0)
			add(list, targetIns);
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(targetIns.getClassName(),
				targetIns.getMethodName());
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		
		if (targetIns.isLocalVariableUse() || targetIns.isFieldUse()){
			MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
			DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
			defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
			
			List<BytecodeInstruction> defs = DefUseAnalyzer.getDefFromUse(targetIns);
			
			for (BytecodeInstruction def : defs) {
				parseRelevantOperands(def, list);
			}
		}
		
		try {
			for(ControlDependency control: targetIns.getControlDependencies()) {
				BytecodeInstruction controlIns = control.getBranch().getInstruction();
				int operandNum = controlIns.getOperandNum();
				
				MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
				
				for (int i = 0; i < operandNum; i++) {
					Frame frame = controlIns.getFrame();
					int index = frame.getStackSize() - operandNum + i ;
					Value val = frame.getStack(index);
					if(val instanceof SourceValue) {
						SourceValue srcValue = (SourceValue)val;
						for(AbstractInsnNode insNode: srcValue.insns) {
							BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);
							parseRelevantOperands(defIns, list);
						}
					}
				}
			}					
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean passMethodInfoIntInstruction(BytecodeInstruction ins) {		
		AbstractInsnNode node = ins.getASMNode();
		if (node.getType() == AbstractInsnNode.INT_INSN) {
			IntInsnNode iins = (IntInsnNode) node;
			if (iins.getOpcode() == Opcodes.SIPUSH) {
				AbstractInsnNode preNode = ins.getPreviousInstruction().getASMNode();
				if(preNode.getType() == AbstractInsnNode.LDC_INSN) {
					LdcInsnNode ldc = (LdcInsnNode) preNode;
					String cla = Properties.TARGET_CLASS.replace('.', '/');
					if (ldc.cst.toString().contains(cla + "#")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void getInstructionOperands(BytecodeInstruction targetIns, List<BytecodeInstruction> list) {
		for (BytecodeInstruction ins : targetIns.getOperands()) {
			if(passMethodInfoIntInstruction(ins)) continue;
			BytecodeInstruction insCopy = ins;
			
			if (insCopy.getASMNode().getOpcode() == Opcodes.CHECKCAST) {
				getInstructionOperands(insCopy, list);
				continue;
			}
			
			if (insCopy.getASMNode().getOpcode() <= Opcodes.SASTORE && insCopy.getASMNode().getOpcode() >= Opcodes.ISTORE) {
				continue;
			}
			
			if (insCopy.getASMNode().getOpcode() == Opcodes.PUTSTATIC) {
				BytecodeInstruction i = insCopy.getPreviousInstruction();
				add(list, i);
				continue;
			}
			
			add(list, insCopy);
		}
	}

	private static boolean isBooleanReturnClass(String calledClass) {
		String className = calledClass.split("\\.")[calledClass.split("\\.").length - 1];
		return className.equals("ContainerHelper") || className.equals("StringHelper");
	}

	private static ActualControlFlowGraph parseGraph(String clazz,
			String methodName) {
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		ActualControlFlowGraph graph = GraphPool.getInstance(classLoader).getActualCFG(clazz, methodName);
		
		if(graph == null) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(clazz);
			graph = GraphPool.getInstance(classLoader).getActualCFG(clazz, methodName);
		}
		
		if(graph != null) {
			ControlDependenceGraph cdg = GraphPool.getInstance(classLoader).getCDG(clazz, methodName);
			if(cdg == null) {
				GraphPool.getInstance(classLoader).registerActualCFG(graph);
				cdg = GraphPool.getInstance(classLoader).getCDG(clazz, methodName);
			}
			
			return graph;
		}
		
		return graph;
	}

	@SuppressWarnings("rawtypes")
	private static List<BytecodeInstruction> reanalyze(List<BytecodeInstruction> ops) {
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
		List<BytecodeInstruction> returnInsList = findAllReturnIns(graph);
		
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

	private static List<BytecodeInstruction> findAllReturnIns(ActualControlFlowGraph graph) {
		List<BytecodeInstruction> insList = graph.getAllInstructions();
		List<BytecodeInstruction> returnInsList = new ArrayList<>();
		for(BytecodeInstruction ins: insList) {
			if(ins.isReturn()) {
				returnInsList.add(ins);
			}
		}
		return returnInsList;
	}

	private static boolean isMethodInStopList(String method) {
		if(method.equals("booleanValue()Z") || 
				method.equals("valueOf(Z)Ljava/lang/Boolean;")) {
			return true;
		}
		return false;
	}

	public static boolean isBooleanReturnType(String method) {
		String returnType = method.substring(method.indexOf(")") + 1, method.length());
		return returnType.equals("Z") || returnType.equals("Ljava/lang/Boolean;")
				|| method.equals("StringEquals(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;I)I")
				|| method.equals("StringStartsWith(Ljava/lang/String;Ljava/lang/String;I)I");
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
	}

	public static String finalType(String name) {
		String type[] =  name.split("\\.");
		if(type[type.length - 1].equals("Integer"))
			return BranchSeedInfo.INT;
		else
			return type[type.length - 1].toLowerCase();
	}

	private static void removeRedundancy(List<ComputationPath> pathList) {
		List<ComputationPath> redundantList = getRedundantPaths(pathList);
		System.currentTimeMillis();
		for (ComputationPath path : redundantList) {
			pathList.remove(path);
		}
	}

	private static Set<DepVariable> compileInputs(Set<DepVariable> methodInputs) {
		boolean additionalInstruction = false;
		Set<DepVariable> local = new HashSet<>();
		if (methodInputs == null)
			return methodInputs;
		for (DepVariable input : methodInputs) {
			
			if(!(input.isConstant() || input.isMethodInput())) continue;
			
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
						return targetBranch;
					}
				}
			}
		}
		
		if(lineBranches.size() == 0) {
			for (Branch br : branchesInTargetMethod.keySet()) {
				String[] s = b.toString().split(" ");
				String[] s1 = br.toString().split(" ");
				
				if(s[s.length - 1].equals(s1[s1.length - 1])) {
					lineBranches.add(br);
					if(s[s.length - 2].equals(s1[s1.length - 2])) {
						targetBranch = br;
						return targetBranch;
					}
				}
			}
			if(lineBranches.size() > 0)
				targetBranch = lineBranches.get(0);
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

	private static List<ComputationPath> getRedundantPaths(List<ComputationPath> pathList) {
		List<ComputationPath> localPathList = new ArrayList<>();
		for (int i = 0; i < pathList.size(); i++) {
			ComputationPath path = pathList.get(i);
			
			if(localPathList.contains(path)) continue;
			l:
			for (int j = i + 1; j < pathList.size(); j++) {
				ComputationPath pathNext = pathList.get(j);
				if(localPathList.contains(pathNext)) continue;				
				if(path.size() != pathNext.size()) continue;
				
				for(int k=0; k<path.size(); k++) {
					if(path.getComputationNodes().get(k).getInstruction().getInstructionId() != 
							pathNext.getComputationNodes().get(k).getInstruction().getInstructionId()) {
						break l;
					}
				}
				
				localPathList.add(pathNext);

			}
			System.currentTimeMillis();
		}
		
		return localPathList;
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
			BranchSeedInfo info = evaluate(branch, null ,null);
//			Class<?> cla = cache.get(branch).getTargetType();
			if (info.getBenefiticalType() != NO_POOL) {
				interestedBranches.add(info);
			}
		}

		return interestedBranches;
	}
}
