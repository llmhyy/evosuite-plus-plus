package org.evosuite.graphs.interprocedural;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.interprocedural.interestednode.IInterestedNodeFilter;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.Randomness;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class InterproceduralGraphAnalyzer {
	private Map<BytecodeInstruction, DepVariable> insPool = new HashMap<>();
	private IInterestedNodeFilter interestedNodeFilter;
	
	public Branch branchInProcess;
	
	
	public InterproceduralGraphAnalyzer(Branch b, IInterestedNodeFilter interestedNodeFilter) {
		this.branchInProcess = b;
		this.interestedNodeFilter = interestedNodeFilter;
	}

	private Map<String, Set<DepVariable>> analyzeReturnValueFromMethod(BytecodeInstruction instruction, int callGraphDepth){
		
		if(callGraphDepth <=0) {
			return new HashMap<>();
		}
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		String className = instruction.getCalledMethodsClass();
		String methodName = instruction.getCalledMethod();
		
		//TODO more in-depth analysis here
		try {
			Class<?> clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
			if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
				return new HashMap<>();
			}
		} catch (ClassNotFoundException e1) {
			return new HashMap<>();
		}
		
		ActualControlFlowGraph calledCfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
//		MethodNode innerNode = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
		
		if (calledCfg == null) {
			Properties.ALWAYS_REGISTER_BRANCH = true;
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).
				registerClass(className);
//			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
//			try {
//				bytecodeAnalyzer.analyze(classLoader, className, methodName, innerNode);
//			} catch (Exception e) {
//				/**
//				 * the cfg (e.g., jdk/library class) is out of our consideration
//				 */
//				return new HashMap<>();
//			}
//			
//			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			calledCfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			Properties.ALWAYS_REGISTER_BRANCH = false;
			
			if (calledCfg == null) {
				MethodNode innerNode = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
				BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
				try {
					bytecodeAnalyzer.analyze(classLoader, className, methodName, innerNode);
				} catch (Exception e) {
					/**
					 * the cfg (e.g., jdk/library class) is out of our consideration
					 */
					return new HashMap<>();
				}

				bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			}
		}
		
		if(calledCfg == null) {
			return new HashMap<>();
		}
		
		GraphPool.getInstance(classLoader).alwaysRegisterActualCFG(calledCfg);
		boolean canBeAnalyzed = FBranchDefUseAnalyzer.analyze(calledCfg.getRawGraph());
		if(!canBeAnalyzed) {
			return new HashMap<>();
		}
		
		Set<DepVariable> allDepVars = new HashSet<DepVariable>();
		Set<BytecodeInstruction> visitedIns = new HashSet<BytecodeInstruction>();
		for (BytecodeInstruction exit : calledCfg.getExitPoints()) {
			for (BytecodeInstruction returnInstruction : exit.getSourceOfStackInstructionList(0)) {
				searchDefDependentVariables(returnInstruction, calledCfg, allDepVars, visitedIns, callGraphDepth-1);
			}
		}
		
		HashMap<String, Set<DepVariable>> map = new HashMap<String, Set<DepVariable>>();
		map.put(className, allDepVars);
		return map;
	}
	
	
	
	/**
	 * value is written by {@code defIns}
	 * @param value
	 * @param defIns
	 * @return
	 */
	public DepVariable parseVariable(BytecodeInstruction defIns) {
//		DepVariable var = variablePool.get(var0); 
		DepVariable var = insPool.get(defIns);
		if(var == null) {
			var = new DepVariable(defIns);
			
			if(var.getName().contains("drain")){
				System.currentTimeMillis();
			}
			
//			variablePool.put(var, var);
			insPool.put(defIns, var);
		}
		else{
			if(var.getName().equals("checkRules(Lstate/Action;Lstate/GameState;)Z_LV_1")){
				System.currentTimeMillis();
			}
		}
		
		return var;
	}
	
	/**
	 * 
	 * This method collects all the variable depended by {@code value} and put them into {@code allDepVars}
	 * {@code depVars}
	 * 
	 * This search algorithm should take a walk through data flows until we find static field, instance field, or parameter.
	 * We would like to take the following usage into account:
	 * a.m1(x1, x2).m2(x3).f
	 * in which a can be either a static field, instance field, or parameter.
	 * 
	 * @param value
	 * @param cfg
	 * @param allLeafDepVars
	 * @param visitedIns
	 * @return
	 */
	public void searchDependantVariables(Value value, ActualControlFlowGraph cfg, Set<DepVariable> allLeafDepVars,
			Set<BytecodeInstruction> visitedIns, int callGraphDepth) {
		
		if (!(value instanceof SourceValue)) {
			return;
		}
		
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
		
		SourceValue srcValue = (SourceValue) value;
		/**
		 * get all the instruction defining the value.
		 */
		for(AbstractInsnNode insNode: srcValue.insns) {
			BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);
			if (defIns != null) {
				searchDefDependentVariables(defIns, cfg, allLeafDepVars, visitedIns, callGraphDepth);
			}
		}
		System.currentTimeMillis();
	}
	
	@SuppressWarnings("rawtypes")
	private void searchDefDependentVariables(BytecodeInstruction defIns, ActualControlFlowGraph cfg, Set<DepVariable> allLeafDepVars,
			Set<BytecodeInstruction> visitedIns, int callGraphDepth) {
		if (/* defIns.getInstructionId()==77 && */this.branchInProcess.getInstruction().getInstructionId()==276) {
			System.currentTimeMillis();
		}
		
		if (visitedIns.contains(defIns)) {
			return;
		}
		visitedIns.add(defIns);
		
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
		
		DepVariable outputVar = parseVariable(defIns);
		/**
		 * the variable is computed by values on stack
		 */
		List<DepVariable>[] inputVarArray = buildInputOutputForInstruction(defIns, node,
				outputVar, cfg, allLeafDepVars, visitedIns, callGraphDepth);
		
		/**
		 * if defIns is a method call, we need to explore more potential variables (fields) inside the method.
		 */
		if(defIns.isMethodCall() || defIns.isConstructorInvocation()) {
			
			if(!shouldStop(defIns)) {
				String recommnedClass = outputVar.getRecommendedImplementation();
				recommnedClass = exploreInterproceduralInstruction(allLeafDepVars, defIns, inputVarArray, recommnedClass, callGraphDepth);
				outputVar.setRecommendedImplementation(recommnedClass);				
			}
			
		}
		
		/**
		 *  handle load/get static, need to put the variable into the return list
		 */
		if(interestedNodeFilter.isInterested(outputVar)) {
			allLeafDepVars.add(outputVar);
		}
//		if(outputVar.isStaticField() || outputVar.isInstaceField() || outputVar.isLoadArrayElement()) {
//			allLeafDepVars.add(outputVar);
//		}
//		
//		if(outputVar.isParameter() && 
//				outputVar.getInstruction().getMethodName().equals(Properties.TARGET_METHOD) &&
//				outputVar.getClassName().equals(Properties.TARGET_CLASS)) {
//			allLeafDepVars.add(outputVar);
//		}
		
		/**
		 * the variable is computed by local variables
		 */
		
		if (defIns.isLocalVariableUse() || defIns.isFieldUse()){
//			if(defIns.getInstructionId() == 4 || defIns.getInstructionId() == 7) {
//				System.currentTimeMillis();
//			}
			//keep traverse
			DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
			defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
//			Use use = DefUseFactory.makeUse(defIns);
			// Ignore method parameter
//			List<Definition> ds = DefUsePool.getDefinitions(use);
			
			if(defIns.getLineNumber() == 53) {
				System.currentTimeMillis();
			}
			
			List<BytecodeInstruction> defs = DefUseAnalyzer.getDefFromUse(defIns);
			for (BytecodeInstruction def : defs) {
				buildInputOutputForInstruction(def, node,
						outputVar, cfg, allLeafDepVars, visitedIns, callGraphDepth);
			}
		}
		
		/**
		 * handle control flow, note that there is no need to build data flow
		 */
		try {
			for(ControlDependency control: defIns.getControlDependencies()) {
				BytecodeInstruction controlIns = control.getBranch().getInstruction();
				int operandNum = controlIns.getOperandNum();
				for (int i = 0; i < operandNum; i++) {
					Frame frame = controlIns.getFrame();
					int index = frame.getStackSize() - operandNum + i ;
					Value val = frame.getStack(index);
					searchDependantVariables(val, cfg, allLeafDepVars, visitedIns, callGraphDepth);
				}
			}					
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * some method could stop, e.g., the call in evosuite
	 * @param defIns
	 * @return
	 */
	private boolean shouldStop(BytecodeInstruction defIns) {
		
		String className = defIns.getCalledMethodsClass();
		if(className.contains("evosuite")) {
			return true;
		}
		
		return false;
	}

	/**
	 * precondition: defIns is a method call
	 * 
	 * @param allLeafDepVars
	 * @param defIns
	 * @param inputVarArray
	 */
	private String exploreInterproceduralInstruction(Set<DepVariable> allLeafDepVars, BytecodeInstruction defIns,
			List<DepVariable>[] inputVarArray, String recommendedClass, int callGraphDepth) {
		
		Map<String, Set<DepVariable>> relatedVariableMap = analyzeReturnValueFromMethod(defIns, callGraphDepth);
		if(relatedVariableMap.isEmpty()) {
			return null;
		}
		System.currentTimeMillis();
		
		String className = relatedVariableMap.keySet().iterator().next();
		Set<DepVariable> relatedVariables = relatedVariableMap.get(className);
		
		for(DepVariable var: relatedVariables) {
			System.currentTimeMillis();
			for(DepVariable rootVar: var.getRootVars().keySet()) {
				if(rootVar.getType() == DepVariable.PARAMETER) {
					if(!(rootVar.getInstruction().getClassName().
							equals(defIns.getCalledCFG().getClassName()) && 
							rootVar.getInstruction().getMethodName().equals(defIns.getCalledCFG().getMethodName()))) {
						continue;
					}					
				}
				
				if(rootVar.getType() == DepVariable.THIS) {
					if(!(rootVar.getInstruction().getClassName().
							equals(defIns.getCalledCFG().getClassName()))) {
						continue;
					}	
				}
				
				
				if(var.getType() == DepVariable.STATIC_FIELD) {
					allLeafDepVars.add(var);					
				}
				else {
//					ConstructionPath path0 = rootVar.findPath(var);
					ArrayList<ConstructionPath> paths = var.getRootVars().get(rootVar);
					ConstructionPath path = paths.get(0);
					
					if(path != null) {
						if(path.getPath().size() < 2) {
							continue;
						}
						
						DepVariable secondVar = path.getPath().get(path.size()-2);
						if(rootVar.getType() == DepVariable.PARAMETER) {
							int index = rootVar.getParamOrder();
							
							List<DepVariable> params = inputVarArray[index];
							
							for(DepVariable param: params) {
								param.buildRelation(secondVar, path.getPosition().get(path.size()-2));
							}
						}
						else if(rootVar.getType() == DepVariable.THIS) {
							List<DepVariable> objectVars = inputVarArray[0];
							for(DepVariable objectVar: objectVars) {
								if(path.getPosition().size() < path.size()-2) {
									System.currentTimeMillis();
								}
								objectVar.buildRelation(secondVar, path.getPosition().get(path.size()-2));									
							}
						}
					}
				}
			}
			
			allLeafDepVars.add(var);
		}
		
		return className;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<DepVariable>[] buildInputOutputForInstruction(BytecodeInstruction defInstruction, MethodNode node, 
			DepVariable outputVar, ActualControlFlowGraph cfg, Set<DepVariable> allDepVars, Set<BytecodeInstruction> visitedIns, int callGraphDepth) {
		List<DepVariable>[] intputVarArray = new ArrayList[DepVariable.OPERAND_NUM_LIMIT];
		int operandNum = defInstruction.getOperandNum();
		for (int i = 0; i < operandNum; i++) {
			List<DepVariable> inputVars = new ArrayList<DepVariable>();
			
			Frame frame = defInstruction.getFrame();
			int index = frame.getStackSize() - operandNum + i;
			Value val = frame.getStack(index);
			
			SourceValue inputVal = (SourceValue)val;
			for(AbstractInsnNode newDefInsNode: inputVal.insns) {
				BytecodeInstruction newDefIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, newDefInsNode);
				DepVariable inputVar = parseVariable(newDefIns);
				inputVar.buildRelation(outputVar, i);
				
				searchDependantVariables(val, cfg, allDepVars, visitedIns, callGraphDepth);
				
				inputVars.add(inputVar);
			}
			
			intputVarArray[i] = inputVars;
		}
		
		return intputVarArray;
	}

	

	
}
