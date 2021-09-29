package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ConstructionPath;
import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.runtime.System;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;

public class ConstructionPathSynthesizer {
//	private TestFactory testFactory;
//	private static final Logger logger = LoggerFactory.getLogger(ConstructionPathSynthesizer.class);
	
	private TestFactory testFactory;
	private PartialGraph partialGraph;
	private Map<DepVariableWrapper, List<VariableReference>> graph2CodeMap;
	public boolean isDebugger = false;

	public ConstructionPathSynthesizer(boolean isDebug) {
		super();
//		this.testFactory = testFactory;
		this.isDebugger = isDebug;
	}
	
	/**
	 * 
	 * @param b
	 * @return
	 */
	public PartialGraph constructPartialComputationGraph(Branch b) {
		PartialGraph graph = new PartialGraph();
		
		// This maps the branch to a set of dependent variables (all one of static field, instance field, or parameter)
		Map<Branch, Set<DepVariable>> branchToDependentVariables = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> dependentVariables = branchToDependentVariables.get(b);
		
		graph.setBranch(b);
		
		// The branch has no dependent variables
		if (dependentVariables == null) {
			return graph;
		}
		
		for (DepVariable source : dependentVariables) {
			// Root variables are method input variables such as parameters.
			// A path can look like parameter -> field1 -> field2, where field2 is a branch operand.
			// In this case it would be root -> ... -> source.
			Map<DepVariable, ArrayList<ConstructionPath>> rootInfo = source.getRootVars();
			
			// Each root is a method input variable such as a parameter.
			// We only consider the relevant roots:
			//   1) The root is of the form this.{something} where the class of the root is our target class
			//      This is for when the branch operand is e.g. this.foo().bar()
			//   2) The root is a parameter, and the class and method match
			//      (root.getInstruction().getMethodName() refers to the method in which the instruction for the root resides)
			//      This is for when the branch operand is e.g. methodParam.foo().bar()
			//   3) The root is a static field.
			//      This is for when the branch operand is e.g. SomeClass.someStaticField.foo()
			for (DepVariable root : rootInfo.keySet()) {
				boolean isRootThisAndPointsToTargetClass = (root.referenceToThis() && 
						root.getInstruction().getClassName().equals(Properties.TARGET_CLASS));
				boolean isRootParamOfTargetMethod = (root.isParameter() && 
						root.getInstruction().getClassName().equals(Properties.TARGET_CLASS) && 
						root.getInstruction().getMethodName().equals(Properties.TARGET_METHOD));
				boolean isRootStaticField = root.isStaticField();
				boolean isRootLoadArrayElement = root.isLoadArrayElement();
				if (isRootThisAndPointsToTargetClass || isRootParamOfTargetMethod || isRootStaticField || isRootLoadArrayElement) {
					// There can be multiple paths from the root to the source
					// (Why?)
					List<ConstructionPath> paths = rootInfo.get(root);
					
					for (ConstructionPath path : paths) {
						if (path.size() == 1) {
							graph.fetchAndMerge(path.get(0));
							continue;
						}
						
						// Path size >= 2
						for (int i = 0; i < path.size() - 1; i++) {
							DepVariable nodeInPath = path.get(i);
							DepVariable successorNodeInPath = path.get(i + 1);
							DepVariableWrapper mergedNodeInPath = graph.fetchAndMerge(nodeInPath);
							DepVariableWrapper mergedSuccessorNodeInPath = graph.fetchAndMerge(successorNodeInPath);
							
							mergedNodeInPath.addParent(mergedSuccessorNodeInPath);
							mergedSuccessorNodeInPath.addChild(mergedNodeInPath);
						}
					}
				}
			}
		}
		return graph;
	}

	/**
	 * Handles cases when the object is non-trivial and we need to construct
	 * statement(s) to initialise the object (e.g. setting fields, preparing dependencies
	 * to pass in the constructor, etc.).
	 * 
	 * @param testCase The test case to modify.
	 * @param branch The branch that the test case aims to cover.
	 * @param isDebugger ?
	 * @param allowNullValue ?
	 * @throws ConstructionFailedException (When?)
	 * @throws ClassNotFoundException (When?)
	 */
	public void buildNodeStatementCorrespondence(TestCase testCase, Branch branch, boolean isDebugger, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {
		// This is the object construction graph mentioned in the ACM paper
		// It provides dataflow relations between the branch operands and method inputs.
		PartialGraph partialGraph = constructPartialComputationGraph(branch);
		
		if (isDebugger) {
			GraphVisualizer.visualizeComputationGraph(partialGraph, 5000, "test");
		}
		
		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
		
		/**
		 * track what variable reference can be reused. Note that, one node can corresponding to multiple statements.
		 * It is because a static field/method can be called twice dynamically.
		 * Therefore, we need to construct multiple fields/methods for different object of the same type.
		 */
		Map<DepVariableWrapper, List<VariableReference>> map = new HashMap<>();

		/**
		 * use BFS on partial graph to generate test code, but we need to check the dependency, making
		 * sure that we have generated the code of its dependency.
		 */
		Queue<DepVariableWrapper> queue = new ArrayDeque<>(topLayer);
		/**
		 * when constructing some instruction, its dependency may not be ready.
		 */
		Map<DepVariableWrapper, Integer> methodCounter = new HashMap<>();
		int c = 1;
		while(!queue.isEmpty()) {
			DepVariableWrapper node = queue.remove();
			c++;
			/**
			 * for each method callsite, we only generate once. 
			 */
			if (map.containsKey(node) && node.var.isMethodCall()){
				continue;
			}
			
			boolean isValid = checkDependency(node, map);

			if (isValid) {
				buildNodeStatementRelation(testCase, map, node, branch, allowNullValue);
				
				/**
				 *  the order of children size matters
				 */
				Collections.sort(node.children, new Comparator<DepVariableWrapper>() {
					@Override
					public int compare(DepVariableWrapper o1, DepVariableWrapper o2) {
						if (o1.var.isMethodCall() && !o2.var.isMethodCall()){
							return -1;
						}
						else if (!o1.var.isMethodCall() && o2.var.isMethodCall()){
							return 1;
						}
						
						return o1.children.size() - o2.children.size();
					}
				});
				
				for (DepVariableWrapper child : node.children) {
					queue.add(child);
				}
				
			} else {
				Integer count = methodCounter.get(node);
				if (count == null) count = 0;
				
				if (count < 5){
					queue.add(node);					
					methodCounter.put(node, ++count);
				}
			}
			
			if (testCase.size() > Properties.CHROMOSOME_LENGTH) {
				break;
			}
		}
		
		this.setPartialGraph(partialGraph);
		this.setGraph2CodeMap(map);
	}

	private boolean checkDependency(DepVariableWrapper node, Map<DepVariableWrapper, List<VariableReference>> map) {
		/**
		 * ensure every parent of current node is visited in the map
		 */
		for (DepVariableWrapper parent : node.parents) {
			if (map.get(parent) == null 
					|| map.get(parent).isEmpty()) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 
	 * @param testCase
	 * @param callerNodeToCallerObjectsMap
	 * @param node
	 * @param branch
	 * @param isAllowNullValue
	 * @return {@code true} if successful, {@code false} otherwise.
	 * @throws ClassNotFoundException
	 * @throws ConstructionFailedException
	 */
	private boolean buildNodeStatementRelation(TestCase testCase, Map<DepVariableWrapper, List<VariableReference>> callerNodeToCallerObjectsMap,
			DepVariableWrapper node, Branch branch, boolean isAllowNullValue) throws ClassNotFoundException, ConstructionFailedException {		
		List<DepVariableWrapper> callerNodes = node.getCallerNode();
		// For root nodes
		if (callerNodes == null) {
			callerNodes = new ArrayList<>();
			callerNodes.add(node);
		}
		
		boolean isEnhancementSuccessful = true;
		for (DepVariableWrapper callerNode : callerNodes) {
			List<VariableReference> callerObjects = callerNodeToCallerObjectsMap.get(callerNode);
			// For root nodes
			if (callerObjects == null) {
				boolean isCodeDerivationSuccessful = deriveCodeForTest(callerNodeToCallerObjectsMap, testCase, null, node, branch, isAllowNullValue);
				isEnhancementSuccessful &= isCodeDerivationSuccessful;
				continue;
			}
			
			for (int i = 0; i < callerObjects.size(); i++) {
				VariableReference callerObject = callerObjects.get(i);
				GenericClass genericClass = callerObject.getGenericClass();
				// Why?
				if (genericClass.isPrimitive()) {
					continue;
				}
				boolean isCodeDerivationSuccessful = deriveCodeForTest(callerNodeToCallerObjectsMap, testCase, callerObject, node, branch, isAllowNullValue);
				isEnhancementSuccessful &= isCodeDerivationSuccessful;
			}
		}
		
		return isEnhancementSuccessful;
	}

	private boolean deriveCodeForTest(Map<DepVariableWrapper, List<VariableReference>> map, TestCase test, 
			VariableReference callerObject, DepVariableWrapper node, Branch b, boolean allowNullValue) 
					throws ClassNotFoundException, ConstructionFailedException{
		boolean isLeaf = node.children.isEmpty();
		
		List<VariableReference> generatedVariables = node.generateOrFindStatement(test, isLeaf, callerObject, map, b, allowNullValue);
		if (generatedVariables != null) {
			List<VariableReference> list = map.get(node);
			if(list == null){
				list = new ArrayList<>();
			}
			
			for(VariableReference ref: generatedVariables) {
				if(ref != null && !list.contains(ref)){
					list.add(ref);											
				}				
			}
			map.put(node, list);
		}
		else {
			return false;
		}
		
		// What is this side effect here?
		// Why is the deriveCodeForTest method adding things to this map?
		List<VariableReference> list = callerNodeToCallerObjectsMap.get(node);
		boolean isListNull = (list == null);
		boolean isListContainsGeneratedVariable = (list.contains(generatedVariable));
		if (isListNull) {
			list = new ArrayList<>();
		}
		
		if (!isListContainsGeneratedVariable) {
			list.add(generatedVariable);					
		}
		callerNodeToCallerObjectsMap.put(node, list);
		return true;
	}

	
//	private boolean isLoadedClass(Class<?> fieldDeclaringClass) {
//		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
//		String className = fieldDeclaringClass.getName();
//		Map<String, RawControlFlowGraph> graphMap = GraphPool.getInstance(classLoader).getRawCFGs(className);
//		if (graphMap != null && !graphMap.isEmpty()) {
//			return true;
//		}
//		
//		return false;
//	}

	

	@SuppressWarnings("rawtypes")
	protected boolean hasNotInvoked(TestCase test, Executable setter) {
		for(int i=0; i<test.size(); i++) {
			Statement s = test.getStatement(i);
			if (s instanceof MethodStatement && setter instanceof Method) {
				Method m = ((MethodStatement)s).getMethod().getMethod();
				if (m.equals(setter)) {
					return false;
				}
			}
			
			if (s instanceof ConstructorStatement && setter instanceof Constructor) {
				Constructor m = ((ConstructorStatement)s).getConstructor().getConstructor();
				if (m.equals(setter)) {
					return false;
				}
			}
		}
		
		return true;
	}


//	private VariableReference retrieveParamReference4Field(TestCase test, Parameter parameter, Executable executable,
//			Statement statement) {
//
//		if (!(statement instanceof EntityWithParametersStatement)) {
//			return null;
//		}
//
//		EntityWithParametersStatement newStatement = (EntityWithParametersStatement) statement;
//		for (int i = 0; i < executable.getParameters().length; i++) {
//			Parameter setterParam = executable.getParameters()[i];
//			if (parameter.equals(setterParam)) {
//				List<VariableReference> paramRefs = newStatement.getParameterReferences();
//				return paramRefs.get(i);
//			}
//		}
//
//
//		return null;
//	}

	

	

	

	public static BytecodeInstruction traceBackToMethodCall(BytecodeInstruction ins) {

		if (ins.isUse()) {
			FBranchDefUseAnalyzer.analyze(ins.getRawCFG());

			Use use = DefUsePool.getUseByInstruction(ins);
			List<Definition> defs = DefUsePool.getDefinitions(use);

			if (defs == null) {
				return null;
			}

			if (!defs.isEmpty()) {
				Definition def = defs.get(0);
				if (def.getFrame().getStackSize() > 0) {
					BytecodeInstruction call = def.getSourceOfStackInstruction(0);

					if (call != null && call.isMethodCall()) {
						return call;
					}
				}
			}
		}

		return null;
	}

	
//			//keep traverse
//			DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
//			defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
//			Use use = DefUseFactory.makeUse(defIns);
//			// Ignore method parameter
//			List<Definition> defs = DefUsePool.getDefinitions(use);
//			for (Definition def : CollectionUtil.nullToEmpty(defs)) {
//				if (def != null) {
//					BytecodeInstruction defInstruction = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, def.getASMNode());
//					buildInputOutputForInstruction(defInstruction, node,
//							outputVar, cfg, allLeafDepVars, visitedIns);
//				}
//			}

	

	

//	@SuppressWarnings("rawtypes")
//	private Parameter searchForQualifiedParameter(Executable executable, Field field, Parameter[] parameters,
//			String opcode) {
//		List<Parameter> qualifiedParams = new ArrayList<>();
//		String fullName = null;
//		for (Parameter param : parameters) {
//			if (param.getType().getCanonicalName().equals(field.getType().getCanonicalName())) {
//				qualifiedParams.add(param);
//			}
//		}
//
//		if (!qualifiedParams.isEmpty()) {
//			if (executable instanceof Method) {
//				Method method = ((Method) executable);
//				fullName = new GenericMethod(method, method.getDeclaringClass()).getNameWithDescriptor();
//			} else if (executable instanceof Constructor) {
//				Constructor constructor = ((Constructor) executable);
//				fullName = new GenericConstructor(constructor, constructor.getDeclaringClass()).getNameWithDescriptor();
//			}
//
//			List<BytecodeInstruction> insList = BytecodeInstructionPool
//					.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
//					.getAllInstructionsAtMethod(executable.getDeclaringClass().getCanonicalName(), fullName);
//			for (BytecodeInstruction ins : insList) {
//				if (ins.getASMNodeString().contains(opcode)) {
//					BytecodeInstruction defIns = ins.getSourceOfStackInstruction(0);
//					int count = 0;
//					if (defIns.isParameter()) {
//						for (int i = 0; i < defIns.getLocalVariableSlot(); i++) {
//							String typeName = executable.getParameterTypes()[i].getTypeName();
//							if (typeName.equals(qualifiedParams.get(0).getType().getCanonicalName())) {
//								count++;
//							}
//						}
//						if (count >= 1) {
//							return qualifiedParams.get(count - 1);
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}

//	private boolean isPrimitiveType(String fieldType) {
//		boolean flag = !fieldType.contains("L") && !fieldType.contains(";");
//		return flag;
//	}
	
//	@SuppressWarnings("rawtypes")
//	private boolean isCalledConstructor(TestCase test, VariableReference parentVarRef, Constructor constructor) {
//
//		if (parentVarRef == null) {
//			return false;
//		}
//
//		Statement stat = test.getStatement(parentVarRef.getStPosition());
//		if (stat instanceof ConstructorStatement) {
//			ConstructorStatement s = (ConstructorStatement) stat;
//			Constructor calledConstructor = s.getConstructor().getConstructor();
//
//			return calledConstructor.equals(constructor);
//		}
//		return false;
//	}

	@SuppressWarnings("rawtypes")
	protected void registerAllMethods(Class<?> fieldDeclaringClass) {
		try {
			for (Method method : fieldDeclaringClass.getDeclaredMethods()) {
				String methodName = method.getName() + MethodUtil.getSignature(method);
				MethodUtil.registerMethod(fieldDeclaringClass, methodName);
			}

			for (Constructor constructor : fieldDeclaringClass.getDeclaredConstructors()) {
				String constructorName = "<init>" + MethodUtil.getSignature(constructor);
				MethodUtil.registerMethod(fieldDeclaringClass, constructorName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Set<Integer> searchRelevantParameterOfSetterInMethod(String className, String methodName, Field field) {
		/**
		 * get all the field setter bytecode instructions in the method. 
		 * the field setter can be taken from callee method of @code{methodName}.
		 */
		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<BytecodeInstruction, List<BytecodeInstruction>>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = DataDependencyUtil.analyzeFieldSetter(className, methodName,
				field, 5, cascadingCallRelations, setterMap);
		Set<Integer> validParams = new HashSet<>();
		if (fieldSetterMap.isEmpty()) {
			return validParams;
		}

		for (Entry<BytecodeInstruction, List<BytecodeInstruction>> entry : fieldSetterMap.entrySet()) {
			BytecodeInstruction setterIns = entry.getKey();
			List<BytecodeInstruction> callList = entry.getValue();
			Set<Integer> validParamPos = DataDependencyUtil.checkValidParameterPositions(setterIns, className, methodName, callList);
			if (!validParamPos.isEmpty()) {
				validParams.addAll(validParamPos);
			}
		}
		return validParams;
	}

	
//	private Map.Entry<Constructor, Parameter> searchForPotentialConstructor(Field field, String fieldOwner,
//			Class<?> fieldDeclaringClass, List<BytecodeInstruction> insList)
//			throws NoSuchMethodException, ClassNotFoundException {
//
//		String opcode = Modifier.isStatic(field.getModifiers()) ? "PUTSTATIC" : "PUTFIELD";
//
//		Map<Constructor, Parameter> targetConstructors = new HashMap<>();
//		for (BytecodeInstruction ins : insList) {
//			if (ins.getASMNodeString().contains(opcode)) {
//				FieldInsnNode insnNode = ((FieldInsnNode) ins.getASMNode());
//				String tmpName = insnNode.name;
//				String tmpOwner = insnNode.owner;
//				if (tmpName.equals(field.getName()) && tmpOwner.equals(fieldOwner)) {
//					String methodName = ins.getMethodName();
//					org.objectweb.asm.Type[] types = org.objectweb.asm.Type
//							.getArgumentTypes(methodName.substring(methodName.indexOf("("), methodName.length()));
//					Class<?>[] paramClasses = new Class<?>[types.length];
//					int index = 0;
//					for (org.objectweb.asm.Type type : types) {
//						Class<?> paramClass = getClassForType(type);
//						paramClasses[index++] = paramClass;
//					}
//
//					if (methodName.contains("<init>")) {
//						Constructor targetConstructor = fieldDeclaringClass.getDeclaredConstructor(paramClasses);
//						Set<Integer> validParamPositions = searchRelevantParameterOfSetterInMethod(fieldDeclaringClass.getCanonicalName(), methodName, field);
//						if (!validParamPositions.isEmpty()) {
//							Integer validParamPos = Randomness.choice(validParamPositions);
//							Parameter param = targetConstructor.getParameters()[validParamPos];
//							targetConstructors.put(targetConstructor, param);
//						}
//					}
//				}
//			}
//		}
//
//		Map.Entry<Constructor, Parameter> entry = Randomness.choice(targetConstructors.entrySet());
//		return entry;
//	}
	
	public static VariableReference addConstructorForClass(TestFactory testFactory, TestCase test, int position, String desc, boolean allowNullValue)
			throws ConstructionFailedException {
		try {
			String fieldType;
			if (desc.contains("/")) {
				fieldType = desc.replace("/", ".").substring(desc.indexOf("L") + 1, desc.length() - 1);
			} else {
				fieldType = desc;
			}
			Class<?> fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(fieldType);

			if (fieldClass.isInterface() || Modifier.isAbstract(fieldClass.getModifiers())) {
				Set<String> subclasses = DependencyAnalysis.getInheritanceTree()
						.getSubclasses(fieldClass.getCanonicalName());
				String subclass = Randomness.choice(subclasses);
				fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(subclass);
			}

			double v = Randomness.nextDouble();
			if (v < Properties.NULL_PROBABILITY) {
				VariableReference variableReference = testFactory.createNull(test, fieldClass, position, 0);
				return variableReference;
			}
			
			Constructor<?> constructor = Randomness.choice(fieldClass.getConstructors());
			if (constructor != null) {
				GenericConstructor genericConstructor = new GenericConstructor(constructor, fieldClass);
				VariableReference variableReference = testFactory.addConstructor(test, genericConstructor, position + 1,
						2);
				return variableReference;
			}
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
  }

	public PartialGraph getPartialGraph() {
		return partialGraph;
	}

	public void setPartialGraph(PartialGraph partialGraph) {
		this.partialGraph = partialGraph;
	}

	public Map<DepVariableWrapper, List<VariableReference>> getGraph2CodeMap() {
		return graph2CodeMap;
	}

	public void setGraph2CodeMap(Map<DepVariableWrapper, List<VariableReference>> graph2CodeMap) {
		this.graph2CodeMap = graph2CodeMap;
	}

	


}
