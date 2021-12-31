package org.evosuite.testcase.synthesizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ConstructionPath;
import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.runtime.System;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.graphviz.GraphVisualisationData;
import org.evosuite.testcase.synthesizer.graphviz.GraphVisualisationDataBuilder;
import org.evosuite.testcase.synthesizer.graphviz.SimpleControlFlowGraph;
import org.evosuite.testcase.synthesizer.graphviz.SimplePartialGraph;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConstructionPathSynthesizer {
	public static int graphVisCounter = 0;
	
//	private TestFactory testFactory;
//	private static final Logger logger = LoggerFactory.getLogger(ConstructionPathSynthesizer.class);
	
	public static String debuggerFolder = "";

	public ConstructionPathSynthesizer(boolean isDebug) {
		super();
//		this.testFactory = testFactory;
		this.isDebugger = isDebug;
	}

	/**
	 * 
	 * @param b
	 * @param isBranchInTargetMethod
	 * @return
	 */
	public PartialGraph constructPartialComputationGraph(Branch b) {
		PartialGraph graph = new PartialGraph();
		
		System.currentTimeMillis();
		
		Map<Branch, Set<DepVariable>> map = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> variables = map.get(b);
		graph.setBranch(b);
		
		if(variables == null) {
			return graph;
		}
		
		for(DepVariable source: variables) {
			Map<DepVariable, ArrayList<ConstructionPath>> rootInfo = source.getRootVars();
			
			for(DepVariable root: rootInfo.keySet()) {
				/**
				 *  this=> class; parameter => method; static field=> whatever
				 */
				if(
					(root.referenceToThis() 
							&&  root.getInstruction().getClassName().equals(Properties.TARGET_CLASS)) 
					|| 
					(root.isParameter()
							&& root.getInstruction().getClassName().equals(Properties.TARGET_CLASS)
							&& root.getInstruction().getMethodName().equals(Properties.TARGET_METHOD)) 
					|| 
					root.isStaticField() 
					||
					root.isLoadArrayElement()
						) {
					
					List<ConstructionPath> paths = rootInfo.get(root);
					
					for(ConstructionPath path: paths) {
						for(int i=0; i<path.size()-1; i++) {
							DepVariableWrapper child = graph.fetchAndMerge(path.get(i));
							DepVariableWrapper parent = graph.fetchAndMerge(path.get(i+1));
							
							child.addParent(parent);
							parent.addChild(child);
						}
						
						if(path.size() == 1){
							graph.fetchAndMerge(path.get(0));
						}
					}
				}
			}
		}
		
		return graph;
	}
	
	private PartialGraph partialGraph;
	private Map<DepVariableWrapper, VarRelevance> graph2CodeMap;
	
	
	public boolean isDebugger = true;
	
	public void buildNodeStatementCorrespondence(TestCase test, Branch b, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {

		PartialGraph partialGraph = constructPartialComputationGraph(b);
		
		// Declaration needed for scoping
		GraphVisualisationDataBuilder graphVisDataBuilder = null;
		if (isDebugger) {
			// For recording data for graph visualisation
			graphVisDataBuilder = new GraphVisualisationDataBuilder();
			graphVisDataBuilder.addPartialGraph(partialGraph);
			graphVisDataBuilder.addCfgFor(b.getClassName(), b.getMethodName());
		}
		
//		System.currentTimeMillis();
//		GraphVisualizer.visualizeComputationGraph(b, 10000);
		if (isDebugger) {
			GraphVisualizer.visualizeComputationGraph(partialGraph, 5000, "test");	
		}
		
		logTest(test, b, isDebugger, 0, null);
		
		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
		
		/**
		 * track what variable reference can be reused. Note that, one node can corresponding to multiple statements.
		 * It is because a static field/method can be called twice dynamically.
		 * Therefore, we need to construct multiple fields/methods for different object of the same type.
		 */
		Map<DepVariableWrapper, VarRelevance> map = new HashMap<>();

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
			
//			logger.warn(String.valueOf(c) + ":" + node.toString());
			if(c==11) {
				System.currentTimeMillis();
			}
			c++;
			/**
			 * for each method callsite, we only generate once. 
			 */
			if(map.containsKey(node) && node.var.isMethodCall()){
				continue;
			}
			
			boolean isValid = checkDependency(node, map);
			if(isValid) {
				VariableInTest testVariable = getCallerObject(map, node);
				
				try {
					deriveCodeForTest(map, test, testVariable, b, allowNullValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				node.processed = true;
				
//				partialGraph.taint(map, node, test, callerObject);
				
				logTest(test, b, isDebugger, c, node);
				
				/**
				 *  the order of children size matters
				 */
				Collections.sort(node.children, new Comparator<DepVariableWrapper>() {
					@Override
					public int compare(DepVariableWrapper o1, DepVariableWrapper o2) {
						if(o1.var.isMethodCall() && !o2.var.isMethodCall()){
							return -1;
						}
						else if(!o1.var.isMethodCall() && o2.var.isMethodCall()){
							return 1;
						}
						
						return o1.children.size() - o2.children.size();
					}
				});
				
				for (DepVariableWrapper child : node.children) {
					queue.add(child);
				}
				
				// Record graph traversal
				if (isDebugger) {
					try {
						graphVisDataBuilder.recordGraphTraversalOrder(node, map.get(node));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				Integer count = methodCounter.get(node);
				if(count == null) count = 0;
				
				if(count < 5){
					queue.add(node);					
					methodCounter.put(node, ++count);
				}
			}
			
			if(test.size() > Properties.CHROMOSOME_LENGTH) {
				break;
			}
		}
		
		System.currentTimeMillis();
		this.setPartialGraph(partialGraph);
		this.setGraph2CodeMap(map);
		
		if (isDebugger) {
			try {
				// Quick fix
				String filePath = java.lang.System.getProperty("user.dir") + File.separator + "graphVisData_" + b.toString() + "_" + graphVisCounter++ + ".json";
				java.lang.System.out.println("Writing graph visualisation data to " + filePath);
				graphVisDataBuilder.build().writeTo(new File(filePath));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void logTest(TestCase test, Branch b, boolean isDebugger, int iteration, DepVariableWrapper node) {
		if(!isDebugger) {
			return;
		}
		
		String subfolder = debuggerFolder + File.separator + b.toString() + File.separator;
		File f = new File(subfolder);
		if(!f.exists()) {
			f.mkdir();
		}
		
		String nodeName = "initial";
		if(node != null) {
			nodeName = node.getShortName();
		}
		
		String testScript = subfolder + iteration  + "-" + nodeName + ".txt";
		testScript = testScript.replace("/", ".");
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(testScript), "utf-8"));
			writer.write(test.toString());
		}
		catch (IOException ex) {
		    System.currentTimeMillis();
		} finally {
		   try {writer.close();} 
		   catch (Exception ex) {
			   System.currentTimeMillis();
		   }
		}
		
		System.currentTimeMillis();
		
	}

	private boolean checkDependency(DepVariableWrapper node, Map<DepVariableWrapper, VarRelevance> map) {
		/**
		 * ensure every parent of current node is visited in the map
		 */
		for (DepVariableWrapper parent : node.parents) {
			if(map.get(parent) == null 
					|| map.get(parent).matchedVars == null
					|| map.get(parent).matchedVars.isEmpty()) {
				
				if(!parent.processed) {
					return false;					
				}
			}
		}

		return true;
	}
	
	/**
	 * Some node cannot correspond to any variable in test source code, but their children can.
	 * For example, arraylist.elementData[i] has a path like: arraylist -> elementData => [].
	 * the elementData in arraylist cannot be accessed, but we still need to find the correspondence
	 * between arraylist.elementData[i] and some variables in the source code.
	 * 
	 * Therefore, here, we need to find (1) the caller object and (2) additional nodes a call should cover
	 */
	private VariableInTest getCallerObject(Map<DepVariableWrapper, VarRelevance> map, DepVariableWrapper node) {
		List<DepVariableWrapper> callerNodes = node.getCallerNode();
		/**
		 * for root nodes
		 */
		if(callerNodes == null) {
			callerNodes = new ArrayList<>();
			callerNodes.add(node);
		}
		
		VariableReference callerObj = null;
		List<DepVariableWrapper> nodePath = new ArrayList<>();
		nodePath.add(node);
		
		DepVariableWrapper callerNode = callerNodes.get(0);
		VarRelevance variableRel = map.get(callerNode);
		
		while(variableRel == null || 
				variableRel.matchedVars == null ||
				variableRel.matchedVars.isEmpty()) {
			DepVariableWrapper parentNode = callerNode.getFirstParent();
			if(parentNode != null) {
				if(!nodePath.contains(parentNode)) {
					callerNode = parentNode;
					nodePath.add(callerNode);
					variableRel = map.get(callerNode);					
				}
				else {
					break;
				}
			}
			else {
				break;
			}
		}
		
		if(variableRel == null || 
				variableRel.matchedVars == null ||
				variableRel.matchedVars.isEmpty()) {
			return new VariableInTest(callerObj, nodePath);
		}
		
		
//		for(int i=0; i<callerObjects.size(); i++){
//			VariableReference callerObject = callerObjects.get(i);
//			GenericClass t = callerObject.getGenericClass();
//			if(t.isPrimitive()) {
//				continue;
//			}
//			
//			callerObj = callerObject;
//		}
		
		Collections.reverse(nodePath);
		return new VariableInTest(variableRel.matchedVars.get(0), nodePath);
	}
	
//	private void buildNodeStatementRelation(TestCase test, Map<DepVariableWrapper, List<VariableReference>> map,
//			DepVariableWrapper node, Branch b, boolean allowNullValue) throws ClassNotFoundException, ConstructionFailedException {
//		
//		VariableReference callerObject = getCallerObject(map, node);
//		deriveCodeForTest(map, test, callerObject, node, b, allowNullValue);
//		
//	}

	private boolean deriveCodeForTest(Map<DepVariableWrapper, VarRelevance> map, TestCase test, 
			VariableInTest testVariable, Branch b, boolean allowNullValue) 
					throws ClassNotFoundException, ConstructionFailedException{
		DepVariableWrapper node = testVariable.getNode();
		boolean isLeaf = node.children.isEmpty();
		
		VarRelevance relevance = node.generateOrFindStatement(test, isLeaf, testVariable, map, b, allowNullValue);
//		List<VariableReference> generatedVariables = node.generateOrFindStatement(test, isLeaf, testVariable, map, b, allowNullValue);
		if (relevance != null) {
			VarRelevance rel = map.get(node);
			if(rel == null){
				map.put(node, relevance);
			}
			else {
				rel.merge(relevance);
				map.put(node, rel);		
			}
		}
		else {
			return false;
		}
		
		return true;
	}

	
//	private boolean isLoadedClass(Class<?> fieldDeclaringClass) {
//		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
//		String className = fieldDeclaringClass.getName();
//		Map<String, RawControlFlowGraph> graphMap = GraphPool.getInstance(classLoader).getRawCFGs(className);
//		if(graphMap != null && !graphMap.isEmpty()) {
//			return true;
//		}
//		
//		return false;
//	}

	

	@SuppressWarnings("rawtypes")
	protected boolean hasNotInvoked(TestCase test, Executable setter) {
		for(int i=0; i<test.size(); i++) {
			Statement s = test.getStatement(i);
			if(s instanceof MethodStatement && setter instanceof Method) {
				Method m = ((MethodStatement)s).getMethod().getMethod();
				if(m.equals(setter)) {
					return false;
				}
			}
			
			if(s instanceof ConstructorStatement && setter instanceof Constructor) {
				Constructor m = ((ConstructorStatement)s).getConstructor().getConstructor();
				if(m.equals(setter)) {
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

//	private Set<Integer> searchRelevantParameterOfSetterInMethod(String className, String methodName, Field field) {
//		/**
//		 * get all the field setter bytecode instructions in the method. 
//		 * the field setter can be taken from callee method of @code{methodName}.
//		 */
//		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
//		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<BytecodeInstruction, List<BytecodeInstruction>>();
//		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = DataDependencyUtil.analyzeFieldSetter(className, methodName,
//				field, 5, cascadingCallRelations, setterMap);
//		Set<Integer> validParams = new HashSet<>();
//		if (fieldSetterMap.isEmpty()) {
//			return validParams;
//		}
//
//		for (Entry<BytecodeInstruction, List<BytecodeInstruction>> entry : fieldSetterMap.entrySet()) {
//			BytecodeInstruction setterIns = entry.getKey();
//			List<BytecodeInstruction> callList = entry.getValue();
//			Set<Integer> validParamPos = DataDependencyUtil.checkValidParameterPositions(setterIns, className, methodName, callList);
//			if (!validParamPos.isEmpty()) {
//				validParams.addAll(validParamPos);
//			}
//		}
//		return validParams;
//	}

	
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
			if(v < Properties.NULL_PROBABILITY) {
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

	public Map<DepVariableWrapper, VarRelevance> getGraph2CodeMap() {
		return graph2CodeMap;
	}

	public void setGraph2CodeMap(Map<DepVariableWrapper, VarRelevance> graph2CodeMap) {
		this.graph2CodeMap = graph2CodeMap;
	}

	


}
