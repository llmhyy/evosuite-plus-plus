package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
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
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.graphs.interprocedural.var.DepVariableFactory;
import org.evosuite.instrumentation.BytecodeInstrumentation;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.testcase.TestCase;
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
						
						updateTestSeedWithConstantAssignment(result, statement, staticConstants, branchInfo, bf, b);
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
						
						updateTestSeedWithConstantAssignment(result, statement, dynamicConstants, branchInfo, bf, b);
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
			List<ObservedConstant> observedConstants, BranchSeedInfo branchInfo, BranchFitness bf, Branch b) {
		if(observedConstants.isEmpty()) return;
		for (ObservedConstant obj : observedConstants) {
			branchInfo.addPotentialSeed(obj);
			try {
//				TestCase test = statement.getTestCase();
//				Object oldValue = statement.getAssignmentValue();
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

	
	
	private static ValuePreservance analyzeChannel(Set<DepVariable> inputs, Branch targetBranch,
			TestChromosome testSeed, BranchFitness bf) {
		/**
		 * the operands corresponding to method inputs and constants
		 */
		List<BytecodeInstruction> observations = parseRelevantOperands(targetBranch);
		
		List<DepVariable> headers = new ArrayList<>(inputs); 
		ValuePreservance sp = SensitivityMutator.evaluateBranchSensitivity(headers, observations, targetBranch, testSeed, bf);
		return sp;
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
		DepVariable var = DepVariableFactory.createVariableInstance(targetIns);

		// element index
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
					System.currentTimeMillis();
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
		
		if(targetIns.isConstant()) {
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

	
	public static boolean isBooleanReturnType(String method) {
		String returnType = method.substring(method.indexOf(")") + 1, method.length());
		return returnType.equals("Z") || returnType.equals("Ljava/lang/Boolean;")
				|| method.equals("StringEquals(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;I)I")
				|| method.equals("StringStartsWith(Ljava/lang/String;Ljava/lang/String;I)I");
	}

	

	public static String finalType(String name) {
		String type[] =  name.split("\\.");
		if(type[type.length - 1].equals("Integer"))
			return BranchSeedInfo.INT;
		else
			return type[type.length - 1].toLowerCase();
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
		return targetBranch;
	}

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
