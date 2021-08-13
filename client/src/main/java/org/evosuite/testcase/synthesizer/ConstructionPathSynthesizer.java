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
import java.lang.reflect.Type;
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
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.ConstructionPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.runtime.System;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AbstractStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.ArrayIndex;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericField;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.bytecode.Opcode;

public class ConstructionPathSynthesizer {

	private TestFactory testFactory;
	private static final Logger logger = LoggerFactory.getLogger(ConstructionPathSynthesizer.class);
	
	public static String debuggerFolder = "";

	public ConstructionPathSynthesizer(TestFactory testFactory) {
		super();
		this.testFactory = testFactory;
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
	private Map<DepVariableWrapper, List<VariableReference>> graph2CodeMap;
	
	public void constructDifficultObjectStatement(TestCase test, Branch b, boolean isDebugger, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {

		PartialGraph partialGraph = constructPartialComputationGraph(b);
//		System.currentTimeMillis();
//		GraphVisualizer.visualizeComputationGraph(b, 10000);
		if(isDebugger) {
			GraphVisualizer.visualizeComputationGraph(partialGraph, 5000, "test");			
		}
		
		logTest(test, b, isDebugger, 0, null);
		
		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
		
//		System.currentTimeMillis();
		
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
				enhanceTestStatement(test, map, node, b, allowNullValue);	
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
		
//		System.currentTimeMillis();
		this.setPartialGraph(partialGraph);
		this.setGraph2CodeMap(map);
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

	private boolean checkDependency(DepVariableWrapper node, Map<DepVariableWrapper, List<VariableReference>> map) {
		/**
		 * ensure every parent of current node is visited in the map
		 */
		for (DepVariableWrapper parent : node.parents) {
			if(map.get(parent) == null 
					|| map.get(parent).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private boolean enhanceTestStatement(TestCase test, Map<DepVariableWrapper, List<VariableReference>> map,
			DepVariableWrapper node, Branch b, boolean allowNullValue) throws ClassNotFoundException, ConstructionFailedException {
		
		List<DepVariableWrapper> callerNodes = node.getCallerNode();
		/**
		 * for root nodes
		 */
		if(callerNodes == null) {
			callerNodes = new ArrayList<>();
			callerNodes.add(node);
		}
		
		boolean success = true;
		for(DepVariableWrapper callerNode: callerNodes){
			List<VariableReference> callerObjects = map.get(callerNode);
			/**
			 * for root nodes
			 */
			if(callerObjects == null){
				boolean s = deriveCodeForTest(map, test, null, node, b, allowNullValue);
				success = success && s;				
			}
			else{
				for(int i=0; i<callerObjects.size(); i++){
					VariableReference callerObject = callerObjects.get(i);
					GenericClass t = callerObject.getGenericClass();
					if(t.isPrimitive()) {
						continue;
					}
					boolean s = deriveCodeForTest(map, test, callerObject, node, b, allowNullValue);
					success = success && s;	
				}
			}
			
		}
		
		return success;
	}

	private boolean deriveCodeForTest(Map<DepVariableWrapper, List<VariableReference>> map, TestCase test, 
			VariableReference callerObject, DepVariableWrapper node, Branch b, boolean allowNullValue) 
					throws ClassNotFoundException, ConstructionFailedException{
		List<VariableReference> generatedVariables = new ArrayList<>();;
		boolean isLeaf = node.children.isEmpty();
		if (node.var.getType() == DepVariable.STATIC_FIELD) {
			VariableReference generatedVariable = generateFieldStatement(test, node, isLeaf, callerObject, map, b, allowNullValue);
			generatedVariables.add(generatedVariable);
		} else if (node.var.getType() == DepVariable.PARAMETER) {
			String castSubClass = checkCastClassForParameter(node);
			if(castSubClass == null) {
				int paramPosition = node.var.getParamOrder() - 1;
				List<String> recommendations = InterproceduralGraphAnalysis.recommendedClasses.get(paramPosition);
				if(recommendations!=null && !recommendations.isEmpty()) {
					castSubClass = Randomness.choice(recommendations);					
				}
			}
			VariableReference generatedVariable = generateParameterStatement(test, node, callerObject, map, castSubClass, allowNullValue);
			generatedVariables.add(generatedVariable);
		} else if (node.var.getType() == DepVariable.INSTANCE_FIELD) {
			if (callerObject == null) {
				return false;
			}
			VariableReference generatedVariable = generateFieldStatement(test, node, isLeaf, callerObject, map, b, allowNullValue);
			generatedVariables.add(generatedVariable);
		} else if (node.var.getType() == DepVariable.OTHER) {
			int opcode = node.var.getInstruction().getASMNode().getOpcode();
			if(opcode == Opcode.ALOAD ||
					opcode == Opcode.ALOAD_1||
					opcode == Opcode.ALOAD_2||
					opcode == Opcode.ALOAD_3 ||
					opcode == Opcode.DUP ||
					opcode == Opcode.DUP2) {
				for(DepVariableWrapper parentNode: node.parents) {
					if(map.get(parentNode) != null) {
						VariableReference generatedVariable = map.get(parentNode).get(0);
						generatedVariables.add(generatedVariable);
						break;
					}
				}
			}
			else if (opcode == Opcode.INVOKEVIRTUAL || 
					opcode == Opcode.INVOKESPECIAL ||
					opcode == Opcode.INVOKESTATIC || 
					opcode == Opcode.INVOKEDYNAMIC ||
					opcode == Opcode.INVOKEINTERFACE){
				VariableReference generatedVariable = generateMethodCallStatement(test, node, map, callerObject, allowNullValue);	
				generatedVariables.add(generatedVariable);
			}
		} else if (node.var.getType() == DepVariable.THIS) {
			if(node.parents.isEmpty()) {
				MethodStatement mStat = test.findTargetMethodCallStatement();
				if(mStat != null) {
					VariableReference generatedVariable = mStat.getCallee();
					generatedVariables.add(generatedVariable);					
				}
			}
			else {
				for(DepVariableWrapper parentNode: node.parents) {
					if(map.get(parentNode) != null) {
						VariableReference generatedVariable = map.get(parentNode).get(0);
						generatedVariables.add(generatedVariable);
						break;
					}
				}
			}
			
		} else if (node.var.getType() == DepVariable.ARRAY_ELEMENT) {
			generatedVariables = generateArrayElementStatement(test, node, isLeaf, callerObject);
		}
		
		if (generatedVariables != null) {
			List<VariableReference> list = map.get(node);
			if(list == null){
				list = new ArrayList<>();
			}
			
			for(VariableReference ref: generatedVariables) {
				if(!list.contains(ref)){
					list.add(ref);					
				}				
			}
			map.put(node, list);
		}
		else {
			return false;
		}
		
		return true;
	}

	private List<VariableReference> generateArrayElementStatement(TestCase test, DepVariableWrapper node, boolean isLeaf,
			VariableReference callerObject) throws ConstructionFailedException {
		Statement stat = test.getStatement(callerObject.getStPosition());
		if(stat instanceof NullStatement) {
			return null;
		}
		
		if (callerObject instanceof ArrayReference) {
			int opcodeRead = node.var.getInstruction().getASMNode().getOpcode();
			int opcodeWrite = getCorrespondingWriteOpcode(opcodeRead);
			VariableReference realParentRef = null;
			Statement statement = test.getStatement(callerObject.getStPosition());

			if (statement instanceof MethodStatement) {
				MethodStatement mStatement = (MethodStatement) statement;
				realParentRef = mStatement.getCallee();
			}
			
			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
			if(!usedArrayElementList.isEmpty()) {
				return usedArrayElementList;
			}
			
			double prob = Randomness.nextDouble();
			if (realParentRef  != null && prob > 0.8) {
				/**
				 * check reused array element
				 */
				VariableReference usedArrayRef = isLeaf
						? searchArrayElementWritingReference(test, node, callerObject, realParentRef, opcodeWrite)
						: searchArrayElementReadingReference(test, node, callerObject, realParentRef, opcodeRead);
				if (usedArrayRef != null) {
					VariableReference generatedVariable = isLeaf ? null : usedArrayRef;
					List<VariableReference> vars = new ArrayList<>();
					if(generatedVariable != null) {
						vars.add(generatedVariable);						
					}
					return vars;
				}

				if (isLeaf) {
					/**
					 * generate setter
					 */
					Method setter = searchSetterForArrayElement(test, node, realParentRef, opcodeWrite);
					if (setter != null) {
						GenericMethod gMethod = new GenericMethod(setter, setter.getDeclaringClass());
						testFactory.addMethodFor(test, realParentRef, gMethod,
								realParentRef.getStPosition() + 1, false);
						return null;
					}
				} else {
					/**
					 * generate getter
					 */
					Method getter = searchGetterForArrayElement(test, node, realParentRef, opcodeRead);
					if (getter != null) {
						VariableReference newParentVarRef = null;
						GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
						newParentVarRef = testFactory.addMethodFor(test, realParentRef, gMethod,
								realParentRef.getStPosition() + 1, false);
//						V newParentVarRef;
						List<VariableReference> vars = new ArrayList<>();
						if(newParentVarRef != null) {
							vars.add(newParentVarRef);						
						}
						return vars;
					}
					return null;
				}
			}
			/**
			 * direct set
			 */
			else{
				ArrayReference arrayRef = (ArrayReference) callerObject;
				int length = arrayRef.getArrayLength();
				if(arrayRef.getArrayLength() <= 0) {
					length = Randomness.nextInt(10) + 1;
				}
				int index = Randomness.nextInt(length);
				
				ArrayIndex arrayIndex = new ArrayIndex(test, arrayRef, index);
				VariableReference varRef = createArrayElementVariable(test, arrayRef);
				AssignmentStatement assignStat = new AssignmentStatement(test, arrayIndex, varRef);
				test.addStatement(assignStat, varRef.getStPosition() + 1);
				VariableReference ref = assignStat.getReturnValue();
				List<VariableReference> vars = new ArrayList<>();
				if(ref != null) {
					vars.add(ref);						
				}
				return vars;
			}
		}
		
		return null;
	}

	private List<VariableReference> searchUsedArrayElementReference(TestCase test, VariableReference arrayObject) {
		List<VariableReference> elementList = new ArrayList<>();
		for(int i=0; i<test.size(); i++) {
			Statement statement = test.getStatement(i);
			if(statement instanceof AssignmentStatement) {
				AssignmentStatement aStat = (AssignmentStatement)statement;
				VariableReference var = aStat.getReturnValue();
				if(var.isArrayIndex()) {
					if(var instanceof ArrayIndex) {
						ArrayIndex index = (ArrayIndex)var;
						if(index.getArray().equals(arrayObject)) {
							elementList.add(aStat.getValue());
						}
					}
					
					System.currentTimeMillis();
				}
			}
		}
		return elementList;
	}

	private Method searchGetterForArrayElement(TestCase test, DepVariableWrapper node, VariableReference realParentRef, int opcodeRead) {
		Set<Method> targetMethods = new HashSet<>();
		Class<?> clazz = realParentRef.getGenericClass().getRawClass();
		for (Method method : clazz.getMethods()) {
			boolean isValid = checkValidArrayElementGetter(method, node, opcodeRead);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}

	private Method searchSetterForArrayElement(TestCase test, DepVariableWrapper node, VariableReference realParentRef, int opcodeWrite) {
		Set<Method> targetMethods = new HashSet<>();
		Class<?> clazz = realParentRef.getGenericClass().getRawClass();
		for (Method method : clazz.getMethods()) {
			boolean isValid = checkValidArrayElementSetter(method, node, opcodeWrite);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}

	private VariableReference searchArrayElementWritingReference(TestCase test, DepVariableWrapper node, VariableReference parentVarRef, VariableReference realParentRef, int opcodeWrite) {
		/*
		 * AASTORE, BASTORE, CASTORE, DASTORE, FASTORE, IASTORE, LASTORE, SASTORE
		 */
		for (int i = 0;i < test.size(); i ++) {
			Statement stat = test.getStatement(i);
			// check directly writing
			if (stat instanceof AssignmentStatement) {
				AssignmentStatement assignStat = (AssignmentStatement) stat;
				for (VariableReference varRef : assignStat.getVariableReferences()) {
					if (varRef.equals(parentVarRef)) {
						return assignStat.getReturnValue();
					}
				}
			} 
			// check writing through method call
			else if (stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement) stat;
				VariableReference ref = mStat.getCallee();
				if (ref != null && ref.equals(realParentRef)) {
					boolean isValid = checkValidArrayElementSetter(mStat.getMethod().getMethod(), node, opcodeWrite);
					if (isValid) {
						return mStat.getReturnValue();
					}
				}
			}
		}
		return null;
	}

	private int getCorrespondingWriteOpcode(int opcodeRead) {
		switch (opcodeRead) {
		case Opcodes.AALOAD:
			return Opcodes.AASTORE;
		case Opcodes.BALOAD:
			return Opcodes.BASTORE;
		case Opcodes.CALOAD:
			return Opcodes.CASTORE;
		case Opcodes.DALOAD:
			return Opcodes.DASTORE;
		case Opcodes.FALOAD:
			return Opcodes.FASTORE;
		case Opcodes.IALOAD:
			return Opcodes.IASTORE;
		case Opcodes.LALOAD:
			return Opcodes.LASTORE;
		case Opcodes.SALOAD:
			return Opcodes.SASTORE;
		default:
			throw new IllegalArgumentException("Illegal Opcode");
		}
	}

	/**
	 * check if current method is a valid setter for array element
	 * 
	 * @param className
	 * @param methodName
	 * @param node
	 * @param opcodeWrite
	 * @return
	 */
	private boolean checkValidArrayElementSetter(Method method, DepVariableWrapper node,
			int opcodeWrite) {
		boolean isValid = false;
		DepVariable parentVar = node.parents.get(0).var;
		if (parentVar.getType() != DepVariable.INSTANCE_FIELD && parentVar.getType() != DepVariable.STATIC_FIELD) {
			return false;
		}
		String className = method.getDeclaringClass().getCanonicalName();
		String methodName = method.getName() + MethodUtil.getSignature(method);
		FieldInsnNode parentFNode = (FieldInsnNode) parentVar.getInstruction().getASMNode();
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getAllInstructionsAtMethod(className, methodName);
		if (insList == null && RuntimeInstrumentation.checkIfCanInstrument(className)) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(className, methodName);
		}
		if (insList != null) {
			for (BytecodeInstruction ins : insList) {
				// *ASTORE
				if (ins.getASMNode().getOpcode() == opcodeWrite) {
					for (int i = 0; i < ins.getOperandNum(); i++) {
						BytecodeInstruction defIns = ins.getSourceOfStackInstruction(i);
						if (defIns.getASMNode().getOpcode() == Opcodes.ACONST_NULL) {
							return false;
						}
						if (defIns.isFieldUse()) {
							FieldInsnNode fnode = ((FieldInsnNode) defIns.getASMNode());
							if (fnode.name.equals(parentFNode.name)) {
								isValid = true;
							}
						}
					}
				}
			}
		}
		return isValid;
	}
	
	/**
	 * check if current method is a valid getter for array element
	 * 
	 * @param className
	 * @param methodName
	 * @param node
	 * @param opcodeRead
	 * @return
	 */
	private boolean checkValidArrayElementGetter(Method method, DepVariableWrapper node, int opcodeRead) {
		DepVariable parentVar = node.parents.get(0).var;
		if (parentVar.getType() != DepVariable.INSTANCE_FIELD && parentVar.getType() != DepVariable.STATIC_FIELD) {
			return false;
		}
		FieldInsnNode parentFNode = (FieldInsnNode) parentVar.getInstruction().getASMNode();
		if (!parentFNode.desc.contains("[")) {
			return false;
		}
		
		String parentDesc = parentFNode.desc;
		String formalDesc = parentDesc.substring(2, parentDesc.length() - 1).replace("/", ".");
		if (!method.getReturnType().getCanonicalName().equals(formalDesc)) {
			return false;
		}
		
		String className = method.getDeclaringClass().getCanonicalName();
		String methodName = method.getName() + MethodUtil.getSignature(method);
		ActualControlFlowGraph cfg = GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getActualCFG(className, methodName);
		if (cfg == null) {
			return false;
		}
		for (BytecodeInstruction exit : cfg.getExitPoints()) {
			if (exit.isReturn()) {
				BytecodeInstruction returnInsn = exit.getSourceOfStackInstruction(0);
				if (returnInsn.getASMNode().getOpcode() == opcodeRead) {
					for (int i = 0;i < returnInsn.getOperandNum(); i ++) {
						BytecodeInstruction defIns = returnInsn.getSourceOfStackInstruction(i);
						if (defIns.isFieldUse()) {
							FieldInsnNode fnode = ((FieldInsnNode) defIns.getASMNode());
							if (fnode.name.equals(parentFNode.name)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private VariableReference searchArrayElementReadingReference(TestCase test, DepVariableWrapper node, VariableReference parentVarRef, VariableReference realParentRef, int opcodeRead) {
		/*
		 * AALOAD, BALOAD, CALOAD, DALOAD, FALOAD, IALOAD, LALOAD, SALOAD
		 */
		for (int i = 0;i < test.size(); i ++) {
			Statement stat = test.getStatement(i);
			// check reading through method call
			if (stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement) stat;
				Method method = mStat.getMethod().getMethod();
				VariableReference ref = mStat.getCallee();
				if (ref != null && ref.equals(realParentRef)) {
					boolean isValid = checkValidArrayElementGetter(method, node, opcodeRead);
					if (isValid) {
						return mStat.getReturnValue();
					}
				}
			}
		}
		return null;
	}

	private VariableReference createArrayElementVariable(TestCase test, ArrayReference arrayRef) {
		Class<?> clazz = arrayRef.getComponentClass();
		Constructor<?> constructor = clazz.getConstructors()[0];
		GenericConstructor gConstructor = new GenericConstructor(constructor,
				constructor.getDeclaringClass());
		try {
			VariableReference returnedVar = testFactory.addConstructor(test, gConstructor,
					arrayRef.getStPosition() + 1, 2);
			return returnedVar;
			
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	private String checkCastClassForParameter(DepVariableWrapper node) throws ClassNotFoundException {
		DepVariable var = node.var;
		
		String potentialCastType = null;
		
		if(node.parents.isEmpty()) return null;
		
		DepVariableWrapper parent = node.parents.iterator().next();
		while (parent != null) {
			DepVariable v = parent.var;
			if (v.getInstruction().toString().contains("CHECKCAST")) {
				BytecodeInstruction ins = v.getInstruction();
				AbstractInsnNode insNode = ins.getASMNode();
				if (insNode instanceof TypeInsnNode) {
					TypeInsnNode tNode = (TypeInsnNode) insNode;
					String classType = tNode.desc;
					potentialCastType = org.objectweb.asm.Type.getObjectType(classType).getClassName();

					ActualControlFlowGraph actualControlFlowGraph = var.getInstruction().getActualCFG();
					int paramOrder = var.getParamOrder();

					String methodSig = actualControlFlowGraph.getMethodName();
					String[] parameters = extractParameter(methodSig);
					String paramType = parameters[paramOrder - 1];

					if (isCompatible(paramType, potentialCastType)) {
						return potentialCastType;
					}
				}
			}
			
			parent = parent.parents.iterator().next();
		}

		return null;
	}

	private VariableReference generateMethodCallStatement(TestCase test, DepVariableWrapper node,
			Map<DepVariableWrapper, List<VariableReference>> map, VariableReference callerObject, boolean allowNullValue) {
		int opcode = node.var.getInstruction().getASMNode().getOpcode();
		try {
			MethodInsnNode methodNode = ((MethodInsnNode) node.var.getInstruction().getASMNode());
			String owner = methodNode.owner;
			String fieldOwner = owner.replace("/", ".");
			String fullName = methodNode.name + methodNode.desc;
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
			org.objectweb.asm.Type[] types = org.objectweb.asm.Type
					.getArgumentTypes(fullName.substring(fullName.indexOf("("), fullName.length()));
			Class<?>[] paramClasses = new Class<?>[types.length];
			int index = 0;
			for (org.objectweb.asm.Type type : types) {
				Class<?> paramClass = getClassForType(type);
				paramClasses[index++] = paramClass;
			}

			if (!fullName.contains("<init>")) {
				Method call = null;
				try {
					call = fieldDeclaringClass.getMethod(fullName.substring(0, fullName.indexOf("(")), paramClasses); 
				}
				catch(Exception e) {}
						
				if(call == null) {
					return null;
				}
				
				VariableReference calleeVarRef = null;
				Map<Integer, VariableReference> paramRefMap = new HashMap<>();

				for (DepVariableWrapper par : node.parents) {
					VariableReference parRef = map.get(par).get(0);
					int position = par.findRelationPosition(node);
					if (position > -1) {
						paramRefMap.put(position, parRef);
					}
				}

				if (opcode == Opcodes.INVOKESTATIC) {
					calleeVarRef = null;
					GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
					VariableReference varRef = testFactory.addMethod(test, genericMethod,
							callerObject.getStPosition() + 1, 1, allowNullValue);
					MethodStatement statement = (MethodStatement) test.getStatement(varRef.getStPosition());
					for (int i = 0; i < statement.getParameterReferences().size();i ++) {
						VariableReference oldParam = statement.getParameterReferences().get(i);
						VariableReference newParam = paramRefMap.get(i);
						if (newParam != null) {
							statement.replace(oldParam, newParam);
							replaceMapFromNode2Code(map, oldParam, newParam);
						}
					}
					return varRef;
				} else {
					Statement stat = test.getStatement(callerObject.getStPosition());
					if(stat instanceof NullStatement) {
						return null;
					}
					
					calleeVarRef = paramRefMap.get(0);
					if (calleeVarRef != null) {
						
						Class<?> calleeType = calleeVarRef.getVariableClass();
						Class<?> callObjectType = call.getDeclaringClass();
						
						if(calleeType.isAssignableFrom(callObjectType)) {
							GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
							VariableReference varRef = testFactory.addMethodFor(test, calleeVarRef, genericMethod,
									calleeVarRef.getStPosition() + 1, allowNullValue);
							MethodStatement statement = (MethodStatement) test.getStatement(varRef.getStPosition());
							for (int i = 0; i < statement.getParameterReferences().size();i ++) {
								VariableReference oldParam = statement.getParameterReferences().get(i);
								VariableReference newParam = paramRefMap.get(i + 1);
								if (newParam != null) {
									statement.replace(oldParam, newParam);
									replaceMapFromNode2Code(map, oldParam, newParam);
								}
							}
							return varRef;
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * set the field into the parentVarRef
	 * 
	 * @param test
	 * @param position
	 * @param var
	 * @param parentVarRef
	 * @param isStatic
	 * @return
	 */
	private VariableReference generateFieldStatement(TestCase test, DepVariableWrapper node, boolean isLeaf,
			VariableReference callerObject, Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
		String fieldType = fieldNode.desc;
		String fieldOwner = fieldNode.owner.replace("/", ".");
		String fieldTypeName = fieldType.replace("/", ".");
		if(fieldTypeName.startsWith("L")) {
			fieldTypeName = fieldTypeName.substring(1, fieldTypeName.length()-1);
		}
		else if(fieldTypeName.startsWith("[L")) {
			fieldTypeName = fieldTypeName.substring(2, fieldTypeName.length()-1);
		}
		String fieldName = fieldNode.name;

		if (callerObject != null) {
			Statement stat = test.getStatement(callerObject.getStPosition());
			if(stat instanceof NullStatement) {
				return null;
			}
			
			String callerType = callerObject.getClassName();
			if (!isPrimitiveClass(callerType)) {
				if (!isCompatible(fieldOwner, callerType)) {
					System.currentTimeMillis();
					return null;
				}
			}
		}

		try {
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
//			registerAllMethods(fieldDeclaringClass);	
			Field field = searchForField(fieldDeclaringClass, fieldName);
			/**
			 * if the field is leaf, check if there is setter in the testcase
			 * if the field is not leaf, check if there is getter in the testcase
			 * if found, stop here
			 */
			UsedReferenceSearcher usedRefSearcher = new UsedReferenceSearcher();
			VariableReference usedFieldInTest = isLeaf
					? usedRefSearcher.searchRelevantFieldWritingReferenceInTest(test, field, callerObject)
					: usedRefSearcher.searchRelevantFieldReadingReferenceInTest(test, field, callerObject);
			System.currentTimeMillis();
			if (usedFieldInTest != null) {
				return usedFieldInTest;
			}

			/**
			 * now we try to generate the relevant statement in the test case.
			 */
			GenericField genericField = new GenericField(field, field.getDeclaringClass());
			int fieldModifiers = field.getModifiers();

			/**
			 * deal with public field, we handle the public field getter/setter in the same way, a.k.a., create
			 * a new public field instance.
			 */
			if (Modifier.isPublic(fieldModifiers)) {
				if (genericField.isFinal()) {
					return null;
				}
				
				VariableReference obj = 
					generatePublicFieldSetterOrGetter(test, callerObject, fieldType, genericField, allowNullValue);
				return obj;
			}

			/**
			 * deal with non-public field
			 */
			if (!isLeaf) {
				VariableReference getterObject = generateFieldGetterInTest(test, callerObject, map, fieldDeclaringClass, field,
						usedRefSearcher, b);
				return getterObject;
			} 
			else {
				generateFieldSetterInTest(test, callerObject, map, fieldDeclaringClass, field, allowNullValue);
//				System.currentTimeMillis();
				return null;
			}

		} catch (ClassNotFoundException | SecurityException | ConstructionFailedException e) {
			printConstructionError(test, node, b);
			e.printStackTrace();
			return null;
		}

	}

	private void printConstructionError(TestCase test, DepVariableWrapper node, Branch b) {
		logger.error("exception happens when processing branch " + b);
		if(node != null) {
			logger.error("working on node" + node);			
		}
		logger.error("partial test case:");
		logger.error(test.toString());
	}

	private VariableReference generateFieldGetterInTest(TestCase test, VariableReference targetObjectReference,
			Map<DepVariableWrapper, List<VariableReference>> map, Class<?> fieldDeclaringClass, Field field, 
			UsedReferenceSearcher usedRefSearcher, Branch b)
			throws ConstructionFailedException {
		/**
		 * make sure this field has been set before get, 
		 * otherwise we may have a null pointer exception after using the retrieved field.
		 */
		VariableReference fieldSetter = usedRefSearcher.searchRelevantFieldWritingReferenceInTest(test, field, targetObjectReference);
		if(fieldSetter == null) {
			try {
				fieldSetter = generateFieldSetterInTest(test, targetObjectReference, map, fieldDeclaringClass, field, false);
				if(fieldSetter != null) {
					Statement s = test.getStatement(fieldSetter.getStPosition());
					/**
					 * if the field setter is a constructor, thus, the object represented by the field setter, o_new, should be
					 * the target object reference as the field is now only relevant to o_new.
					 */
					if(s instanceof ConstructorStatement) {
						ConstructorStatement cStat = (ConstructorStatement)s;
						VariableReference relevantParam = null;
//						if(cStat.getParameterReferences().size() == 1) {
//							relevantParam = cStat.getParameterReferences().get(0);
//						}
						
						for(VariableReference vRef: cStat.getParameterReferences()) {
							if(vRef.getType().equals(field.getGenericType())) {
								relevantParam = vRef;
							}
						}
						
						if(relevantParam != null) {
							System.currentTimeMillis();
							return relevantParam;
						}
						
						targetObjectReference = fieldSetter;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (ConstructionFailedException e) {
				printConstructionError(test, null, b);
				e.printStackTrace();
			}
		}
//		else {
//			return fieldSetter;
//		}
		
//		System.currentTimeMillis();
		
		int insertionPostion = -1;
		if(fieldSetter != null) {
			insertionPostion = fieldSetter.getStPosition()+1;
			if(targetObjectReference != null && 
					targetObjectReference.getStPosition() > fieldSetter.getStPosition()) {
				insertionPostion = targetObjectReference.getStPosition() + 1;
			}
		}
		else if (targetObjectReference == null) {
			MethodStatement mStat = test.findTargetMethodCallStatement();
			insertionPostion = mStat.getPosition() - 1;
		}
		else {
			insertionPostion = targetObjectReference.getStPosition() + 1;
		}
		
		System.currentTimeMillis();
		Method getter = searchForPotentialGetterInClass(fieldDeclaringClass, field);
		if (getter != null) {
			VariableReference newParentVarRef = null;
			GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
			if (targetObjectReference == null) {
				newParentVarRef = testFactory.addMethod(test, gMethod, insertionPostion, 2, false);
			} else {
				newParentVarRef = testFactory.addMethodFor(test, targetObjectReference, gMethod,
						insertionPostion, false);
			}
			return newParentVarRef;
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * generate setter in current test
	 */
	private VariableReference generateFieldSetterInTest(TestCase test, VariableReference targetObjectReference,
			Map<DepVariableWrapper, List<VariableReference>> map, Class<?> fieldDeclaringClass, Field field, boolean allowNullValue)
			throws ClassNotFoundException, ConstructionFailedException {
		String className = fieldDeclaringClass.getName();
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getInstructionsIn(className);

		if (insList == null && RuntimeInstrumentation.checkIfCanInstrument(className)) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getInstructionsIn(className);
		}
		
		if (insList == null) {
			
			return null;
		}
		
		String targetClassName = checkTargetClassName(field, targetObjectReference);
		Executable setter = searchForPotentialSetterInClass(field, targetClassName);
		if (setter != null && !isTarget(setter)) {
			if(setter instanceof Method){
				GenericMethod gMethod = new GenericMethod((Method)setter, setter.getDeclaringClass());
				VariableReference generatedSetter = null;
				if (targetObjectReference == null) {
					MethodStatement mStat = test.findTargetMethodCallStatement();
					generatedSetter = testFactory.addMethod(test, gMethod, mStat.getPosition() - 1, 2, allowNullValue);
				} else {
					generatedSetter = testFactory.addMethodFor(test, targetObjectReference, gMethod,
							targetObjectReference.getStPosition() + 1, allowNullValue);
				}
				return generatedSetter;
			}
			else if(setter instanceof Constructor){
				GenericConstructor gConstructor = new GenericConstructor((Constructor)setter,
						setter.getDeclaringClass());
				VariableReference returnedVar = testFactory.addConstructor(test, gConstructor,
						targetObjectReference.getStPosition() + 1, 2);

				for (int i = 0; i < test.size(); i++) {
					Statement stat = test.getStatement(i);
					if (stat.references(targetObjectReference)) {
						if (returnedVar.getStPosition() < stat.getPosition()) {
							stat.replace(targetObjectReference, returnedVar);
							replaceMapFromNode2Code(map, targetObjectReference, returnedVar);
						}
						else if (returnedVar.getStPosition() > stat.getPosition() 
								&& stat.getPosition() != targetObjectReference.getStPosition()){
							System.currentTimeMillis();
						}
					}
					
				}
				
				return returnedVar;
			}
		}
		
		return null;
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

	private void replaceMapFromNode2Code(Map<DepVariableWrapper, List<VariableReference>> map,
			VariableReference oldObject, VariableReference newObject) {
		for(DepVariableWrapper key: map.keySet()) {
			List<VariableReference> list = map.get(key);
			
			if(list.contains(oldObject)) {
				for(int i=0; i<list.size(); i++) {
					if(list.get(i).equals(oldObject)) {
						list.set(i, newObject);
					}
				}
				
			}
		}
		
	}

	private boolean isTarget(Executable setter) {
		String className = setter.getDeclaringClass().getCanonicalName();
		
		String name = null;
		if(setter instanceof Method) {
			name = setter.getName();
		}
		else {
			name = "<init>";
		}
		
		String methodName = name + ReflectionUtil.getSignature(setter);
		
		return className.equals(Properties.TARGET_CLASS) 
				&& methodName.equals(Properties.TARGET_METHOD);
	}

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

	private String checkTargetClassName(Field field, VariableReference targetObjectReference) {
		if(targetObjectReference != null){
			return targetObjectReference.getType().getTypeName();
		}
		else{
			return field.getDeclaringClass().getCanonicalName();
		}
	}

	private VariableReference generatePublicFieldSetterOrGetter(TestCase test, VariableReference targetObjectReference,
			String fieldType, GenericField genericField, boolean allowNullValue) throws ConstructionFailedException {
		AbstractStatement stmt;
		if (CollectionUtil.existIn(fieldType, "Z", "B", "C", "S", "I", "J", "F", "D")) {
			stmt = addStatementToSetOrGetPublicField(test, fieldType,
					genericField, targetObjectReference, new PrimitiveFieldInitializer(), allowNullValue);
		} else {
			stmt = addStatementToSetOrGetPublicField(test, fieldType,
					genericField, targetObjectReference, new NonPrimitiveFieldInitializer(), allowNullValue);
		}

		if (stmt != null && stmt.getReturnValue() != null) {
			return stmt.getReturnValue();
		}
		
		return null;
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

	private boolean isCompatible(String parentType, String subType) {
		try {
			Class<?> parentClass = Class.forName(parentType, true,
					TestGenerationContext.getInstance().getClassLoaderForSUT());
			Class<?> subClass = Class.forName(subType, true,
					TestGenerationContext.getInstance().getClassLoaderForSUT());
			return parentClass.isAssignableFrom(subClass);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * generate getter in current class
	 */
	private Method searchForPotentialGetterInClass(Class<?> fieldDeclaringClass, Field field) {
		Set<Method> targetMethods = new HashSet<>();

		for (Method method : fieldDeclaringClass.getMethods()) {
			boolean isValid = DataDependencyUtil.isFieldGetter(method, field);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}

//	private boolean isPrimitiveType(String fieldType) {
//		boolean flag = !fieldType.contains("L") && !fieldType.contains(";");
//		return flag;
//	}
	
	private boolean isPrimitiveClass(String fieldType) {
		boolean flag = fieldType.equals("int") || fieldType.equals("double") || fieldType.equals("float")
				|| fieldType.equals("long") || fieldType.equals("short") || fieldType.equals("char") || fieldType.equals("byte");
		return flag;
	}

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

	/**
	 * If field is not found in current declaring class, we search recursively for
	 * its superclass. For simplicity, we only search for setter, getter,
	 * constructor. in current class instead of the real declaring class of this
	 * field.
	 */
	private Field searchForField(Class<?> fieldDeclaringClass, String fieldName) {
		try {
			Field field = fieldDeclaringClass.getDeclaredField(fieldName);
			return field;
		} catch (NoSuchFieldException e) {
			if (fieldDeclaringClass.getSuperclass() != null) {
				return searchForField(fieldDeclaringClass.getSuperclass(), fieldName);
			}
		}
		return null;
	}

	/**
	 * parameter statement is supposed to be used only in the target method
	 * invocation
	 * 
	 * @param test
	 * @param position
	 * @param var
	 * @param callerObject
	 * @param map 
	 * @param castSubClass
	 * @return
	 * @throws ConstructionFailedException
	 * @throws ClassNotFoundException
	 */
	private VariableReference generateParameterStatement(TestCase test, DepVariableWrapper node,
			VariableReference callerObject, Map<DepVariableWrapper, List<VariableReference>> map, String castSubClass, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {
		if(castSubClass != null) {
			VariableReference newParameter = generateParameter(test, node.var, castSubClass, allowNullValue);

			if (newParameter == null) {
				return callerObject;
			}

			MethodStatement targetStatement = test.findTargetMethodCallStatement();
			if (targetStatement != null) {
				VariableReference oldParamRef = targetStatement.getParameterReferences().get(node.var.getParamOrder() - 1);
				targetStatement.replace(oldParamRef, newParameter);
			}

			return newParameter;
		}
		else {
			/**
			 * find the existing parameters
			 */
			MethodStatement mStat = test.findTargetMethodCallStatement();
			int paramPosition = node.var.getParamOrder();
			VariableReference paramRef = null;
			if(paramPosition >= 1)
				paramRef = mStat.getParameterReferences().get(paramPosition - 1);
			else
				return paramRef;
			
			/**
			 * make sure the parameter obj is not null
			 */
			int paramPosInTest = paramRef.getStPosition();
			Statement paramDef = test.getStatement(paramPosInTest);
			if(!allowNullValue && paramDef instanceof NullStatement){
				TestFactory testFactory = TestFactory.getInstance();
				boolean isSuccess = testFactory.changeNullStatement(test, paramDef);
				if(isSuccess){
					paramRef = mStat.getParameterReferences().get(paramPosition - 1);
				}
			}
			
			return paramRef;
		}
		
		
		
	}

	private VariableReference generateParameter(TestCase test, DepVariable var, String castSubClass, boolean allowNullValue)
			throws ConstructionFailedException {

		String paramType = castSubClass;
		if (paramType == null) {
			ActualControlFlowGraph actualControlFlowGraph = var.getInstruction().getActualCFG();
			int paramOrder = var.getParamOrder();

			String methodSig = actualControlFlowGraph.getMethodName();
			String[] parameters = extractParameter(methodSig);
			paramType = parameters[paramOrder - 1];
		}

		Class<?> paramClass;
		try {
			paramClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(paramType);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		
		GenericClass paramDeclaringClazz = new GenericClass(paramClass);
		VariableReference paramRef = null;
		Constructor<?> constructor = Randomness.choice(paramDeclaringClazz.getRawClass().getConstructors());
		if (constructor != null) {
			GenericConstructor gc = new GenericConstructor(constructor, paramDeclaringClazz);
			MethodStatement mStat = test.findTargetMethodCallStatement();
			
			if(allowNullValue) {
				if(Randomness.nextDouble() < Properties.NULL_PROBABILITY) {
					paramRef = TestFactory.getInstance().createNull(test, paramClass, mStat.getPosition() - 1, 0);
				}
			}
			else {
				paramRef = testFactory.addConstructor(test, gc, mStat.getPosition() - 1, 2);				
			}
		}

		return paramRef;
	}

	private String[] extractParameter(String methodSig) {
		String parameters = methodSig.substring(methodSig.indexOf("(") + 1, methodSig.indexOf(")"));
		String[] args = parameters.split(";");
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace("/", ".").substring(1, args[i].length());
		}
		return args;
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
	
	/**
	 * return a method along with one of its parameters to setter the field.
	 * 
	 * @param field
	 * @param fieldOwner
	 * @param targetClass
	 * @param insList
	 * @param operation
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings("rawtypes")
	private Executable searchForPotentialSetterInClass(Field field, String targetClassName) throws ClassNotFoundException{
		
		Class<?> targetClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
				.loadClass(targetClassName);
		
		List<Executable> fieldSettingMethods = new ArrayList<>();
		/**
		 * map <field setter instruction, <m_1, ..., m_n>>, where 
		 * m_n is the method to call field setter instruction
		 * m_1 is the method called by test
		 */
		List<Map<BytecodeInstruction, List<BytecodeInstruction>>> difficultyList = new ArrayList<>();
		List<Set<Integer>> numberOfValidParams = new ArrayList<>();
		
		for(Method m: targetClass.getMethods()){
			String signature = m.getName() + ReflectionUtil.getSignature(m);
			findSetterInfo(field, targetClass, fieldSettingMethods, difficultyList, numberOfValidParams, m, signature);
		}
		
		for(Constructor c: targetClass.getConstructors()){
			String signature = "<init>" + ReflectionUtil.getSignature(c);
			findSetterInfo(field, targetClass, fieldSettingMethods, difficultyList, numberOfValidParams, c, signature);
		}
		
		System.currentTimeMillis();
		
		if(!fieldSettingMethods.isEmpty()){
//			Executable entry = Randomness.choice(fieldSettingMethods);
			double[] scores = new double[fieldSettingMethods.size()];
			for(int i=0; i<scores.length; i++){
				scores[i] = estimateCoverageLikelihood(difficultyList.get(i), numberOfValidParams.get(i).size());

				java.lang.reflect.Parameter[] pList = fieldSettingMethods.get(i).getParameters();
				boolean typeCompatible = false;
				
				for(Integer index: numberOfValidParams.get(i)) {
					scores[i] += 1;
					java.lang.reflect.Parameter p = pList[index];
					Class<?> c = p.getType();
					Class<?> c0 = field.getType();
					
					if(c0.isAssignableFrom(c)) {
						scores[i] += 20;
						typeCompatible = true;
					}
				}
				
				if(fieldSettingMethods.get(i) instanceof Method && typeCompatible) {
					scores[i] += 10;
				}
				
//				System.currentTimeMillis();
			}
			
			double[] probability = normalize(scores);
			double p = Randomness.nextDouble();
			System.currentTimeMillis();
			int selected = select(p, probability);
			return fieldSettingMethods.get(selected);
		}
		
		return null;
	}
	
	/**
	 * we have three factors to estimate how difficult a setter is to influence some branch in the 
	 * target method, (1) call chain, (2) the control flow in the call chain, (3) the number of mutable 
	 * variables.
	 * 
	 * @param map
	 * @param integer
	 * @return
	 */
	private double estimateCoverageLikelihood(Map<BytecodeInstruction, List<BytecodeInstruction>> map,
			Integer validParamNum) {
		
		double sum = 0;
		for(BytecodeInstruction ins: map.keySet()) {
			double callchainSize = map.get(ins).size();
			sum += 1/(callchainSize+1);
		}
		
		return (double)(validParamNum+1) * sum;
	}

	private int select(double p, double[] probability) {
		for(int i=0; i<probability.length; i++){
			if(i==0){
				if(p<=probability[i]){
					return i;
				}				
			}
			else{
				if(probability[i-1]<p && p<=probability[i]){
					return i;
				}
			}
			
		}
		
		return 0;
	}

	private double[] normalize(double[] scores) {
		double sum = 0;
		for(int i=0; i<scores.length; i++){
			sum += scores[i];
		}
		
		double[] prob = new double[scores.length];
		for(int i=0; i<scores.length; i++){
			prob[i] = scores[i]/sum;
		}
		
		for(int i=1; i<scores.length; i++){
			prob[i] += prob[i-1];
		}
		
		return prob;
	}

	

	private void findSetterInfo(Field field, Class<?> targetClass, List<Executable> fieldSettingMethods,
			List<Map<BytecodeInstruction, List<BytecodeInstruction>>> difficultyList, List<Set<Integer>> validParams,
			Executable m, String signature) {
		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = 
				DataDependencyUtil.analyzeFieldSetter(targetClass.getCanonicalName(), signature,
						field, 5, cascadingCallRelations, setterMap);
		
//		System.currentTimeMillis();
		
		Set<Integer> releventPrams = new HashSet<>();
		for (Entry<BytecodeInstruction, List<BytecodeInstruction>> entry : fieldSetterMap.entrySet()) {
			BytecodeInstruction setterIns = entry.getKey();
			List<BytecodeInstruction> callList = entry.getValue();
			Set<Integer> validParamPos = DataDependencyUtil.checkValidParameterPositions(setterIns, 
					targetClass.getCanonicalName(), signature, callList);
			releventPrams.addAll(validParamPos);
		}
		
		if(!fieldSetterMap.isEmpty()){
			fieldSettingMethods.add(m);
			difficultyList.add(fieldSetterMap);
			validParams.add(releventPrams);
		}
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
	
	private AbstractStatement addStatementToSetOrGetPublicField(TestCase test, String fieldType,
			GenericField genericField, VariableReference parentVarRef, FieldInitializer fieldInitializer, boolean allowNullValue) 
					throws ConstructionFailedException {
		MethodStatement mStat = test.findTargetMethodCallStatement();
		int insertionPosition = (parentVarRef != null) ? 
				parentVarRef.getStPosition() + 1 : mStat.getPosition() - 1; 
		
		if(insertionPosition >= mStat.getPosition()) {
			insertionPosition = mStat.getPosition() - 1;
		}
		
		FieldReference fieldVar = null;
		if (genericField.isStatic() || parentVarRef == null) {
			fieldVar = new FieldReference(test, genericField);
		} else {
			fieldVar = new FieldReference(test, genericField, parentVarRef);
		}

		VariableReference objRef = fieldInitializer.assignField(testFactory, test, fieldType, 
				genericField, insertionPosition, fieldVar, allowNullValue);	
		
		if(objRef == null) {
			System.currentTimeMillis();
		}
		
		AbstractStatement stmt = new AssignmentStatement(test, fieldVar, objRef);		
		test.addStatement(stmt, objRef.getStPosition()+1);
		
		return stmt;
	}

	private Class<?> getClassForType(org.objectweb.asm.Type type) {
		if (type == org.objectweb.asm.Type.BOOLEAN_TYPE) {
			return boolean.class;
		} else if (type == org.objectweb.asm.Type.BYTE_TYPE) {
			return byte.class;
		} else if (type == org.objectweb.asm.Type.CHAR_TYPE) {
			return char.class;
		} else if (type == org.objectweb.asm.Type.SHORT_TYPE) {
			return short.class;
		} else if (type == org.objectweb.asm.Type.INT_TYPE) {
			return int.class;
		} else if (type == org.objectweb.asm.Type.LONG_TYPE) {
			return long.class;
		} else if (type == org.objectweb.asm.Type.FLOAT_TYPE) {
			return float.class;
		} else if (type == org.objectweb.asm.Type.DOUBLE_TYPE) {
			return double.class;
		} else {
			try {
				String className = type.getClassName();
				if (type.getSort() != org.objectweb.asm.Type.ARRAY) {
					Class<?> clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
					return clazz;
				} else {
					StringBuffer buffer = new StringBuffer();
					org.objectweb.asm.Type elementType = type.getElementType();
					String arrayString = extractedArrayString(type);
					if (elementType.getSort() <= 8) {
						className = convertToShortName(elementType.getClassName());
						buffer.append(arrayString);
						buffer.append(className);
					} else {
						className = elementType.getClassName();
						buffer.append(arrayString);
						buffer.append("L");
						buffer.append(className);
						buffer.append(";");
					}
					String fullName = buffer.toString();
					Class<?> clazzArray = Class.forName(fullName, true,
							TestGenerationContext.getInstance().getClassLoaderForSUT());
					return clazzArray;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String extractedArrayString(org.objectweb.asm.Type type) {
		int arrayLength = type.getDimensions();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arrayLength; i++) {
			buffer.append("[");
		}
		return buffer.toString();
	}

	private String convertToShortName(String desc) {
		switch (desc) {
		case "boolean":
			return "Z";
		case "byte":
			return "B";
		case "char":
			return "C";
		case "short":
			return "S";
		case "int":
			return "I";
		case "long":
			return "J";
		case "float":
			return "F";
		case "double":
			return "D";
		default:
			return null;
		}
	}

	protected Type convertToType(String desc) {
		switch (desc) {
		case "Z":
			return Boolean.TYPE;
		case "B":
			return Byte.TYPE;
		case "C":
			return Character.TYPE;
		case "S":
			return Short.TYPE;
		case "I":
			return Integer.TYPE;
		case "J":
			return Long.TYPE;
		case "F":
			return Float.TYPE;
		case "D":
			return Double.TYPE;
		default:
			Class<?> clazz;
			String str = desc.substring(1, desc.length()-1);
			String className = str.replace("/", ".");
			try {
				clazz = Class.forName(className, true,
						TestGenerationContext.getInstance().getClassLoaderForSUT());
			} catch (ClassNotFoundException e) {
				clazz = null;
				e.printStackTrace();
			}
			return clazz;
		}
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
