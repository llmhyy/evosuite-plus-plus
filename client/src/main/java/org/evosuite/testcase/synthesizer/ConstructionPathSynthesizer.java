package org.evosuite.testcase.synthesizer;

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
import org.evosuite.graphs.dataflow.ConstructionPath;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.graphs.dataflow.GraphVisualizer;
import org.evosuite.runtime.System;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AbstractStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.numeric.IntPrimitiveStatement;
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

	public ConstructionPathSynthesizer(TestFactory testFactory) {
		super();
		this.testFactory = testFactory;
	}

	private PartialGraph constructPartialComputationGraph(Branch b) {
		PartialGraph graph = new PartialGraph();
		
		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> variables = map.get(b);
		
		for(DepVariable source: variables) {
			Map<DepVariable, ArrayList<ConstructionPath>> rootInfo = source.getRootVars();
			
			for(DepVariable root: rootInfo.keySet()) {
				
				if((root.referenceToThis() || root.isParameter() || root.isStaticField()) 
						&& root.getInstruction().getMethodName().equals(Properties.TARGET_METHOD)) {
					
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
	
	public void constructDifficultObjectStatement(TestCase test, Branch b)
			throws ConstructionFailedException, ClassNotFoundException {

		PartialGraph partialGraph = constructPartialComputationGraph(b);
		
		GraphVisualizer.visualizeComputationGraph(b);
		GraphVisualizer.visualizeComputationGraph(partialGraph);
		
		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
		
		/**
		 * track what variable reference can be reused. Note that, one node can corresponding to multiple statements.
		 */
		Map<DepVariable, List<VariableReference>> map = new HashMap<>();

		/**
		 * use BFS on partial graph to generate test code, but we need to check the dependency, making
		 * sure that we have generated the code of its dependency.
		 */
		Queue<DepVariableWrapper> queue = new ArrayDeque<>(topLayer);
		/**
		 * when constructing some instruction, its dependency may not be ready.
		 */
		Map<DepVariable, Integer> methodCounter = new HashMap<>();
		while(!queue.isEmpty()) {
			DepVariableWrapper node = queue.remove();
			logger.warn(node.toString());
			/**
			 * for each method callsite, we only generate once. 
			 */
			if(map.containsKey(node.var) && node.var.isMethodCall()){
				continue;
			}
			
			boolean isValid = checkDependency(node, map);
			if(isValid) {
				enhanceTestStatement(test, map, node);	
				
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
				Integer count = methodCounter.get(node.var);
				if(count == null) count = 0;
				
				if(count < 5){
					queue.add(node);					
					methodCounter.put(node.var, ++count);
				}
			}
		}
		
		System.currentTimeMillis();
	}

	private boolean checkDependency(DepVariableWrapper node, Map<DepVariable, List<VariableReference>> map) {
		/**
		 * ensure every parent of current node is visited in the map
		 */
		for (DepVariableWrapper parent : node.parents) {
			if(map.get(parent.var) == null 
					|| map.get(parent.var).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private boolean enhanceTestStatement(TestCase test, Map<DepVariable, List<VariableReference>> map,
			DepVariableWrapper node) throws ClassNotFoundException, ConstructionFailedException {
		
		List<DepVariableWrapper> contextualNodes = node.checkContextualNode();
		System.currentTimeMillis();
		if(contextualNodes == null) {
			contextualNodes = new ArrayList<>();
			contextualNodes.add(node);
		}
		
		boolean success = true;
		for(DepVariableWrapper contextualNode: contextualNodes){
			List<VariableReference> inputObjects = map.get(contextualNode.var);
			/**
			 * for root nodes
			 */
			if(inputObjects == null){
				boolean s = deriveCodeForTest(map, test, null, node);
				success = success && s;				
			}
			else{
				for(VariableReference inputObject: inputObjects){
					boolean s = deriveCodeForTest(map, test, inputObject, node);
					success = success && s;	
				}
			}
			
		}
		
		return success;
	}

	private boolean deriveCodeForTest(Map<DepVariable, List<VariableReference>> map, TestCase test, 
			VariableReference inputObject, DepVariableWrapper node) 
					throws ClassNotFoundException, ConstructionFailedException{
		DepVariable var = node.var;
		boolean isLeaf = node.children.isEmpty();
		if (var.getType() == DepVariable.STATIC_FIELD) {
			inputObject = generateFieldStatement(test, var, isLeaf, inputObject);
		} else if (var.getType() == DepVariable.PARAMETER) {
			String castSubClass = checkCastClassForParameter(node);
			inputObject = generateParameterStatement(test, var, inputObject, castSubClass);
		} else if (var.getType() == DepVariable.INSTANCE_FIELD) {
			if (inputObject == null) {
				return false;
			}
			inputObject = generateFieldStatement(test, var, isLeaf, inputObject);
		} else if (var.getType() == DepVariable.OTHER) {
			int opcode = node.var.getInstruction().getASMNode().getOpcode();
			if(opcode == Opcode.ALOAD ||
					opcode == Opcode.ALOAD_1||
					opcode == Opcode.ALOAD_2||
					opcode == Opcode.ALOAD_3) {
				for(DepVariableWrapper parentNode: node.parents) {
					if(map.get(parentNode.var) != null) {
						inputObject = map.get(parentNode.var).get(0);
						break;
					}
				}
			}
			else if (opcode == Opcode.INVOKEVIRTUAL || 
					opcode == Opcode.INVOKESPECIAL ||
					opcode == Opcode.INVOKESTATIC || 
					opcode == Opcode.INVOKEDYNAMIC ||
					opcode == Opcode.INVOKEINTERFACE){
				inputObject = generateMethodCallStatement(test, node, map, inputObject);				
			}
		} else if (var.getType() == DepVariable.THIS) {
			if(node.parents.isEmpty()) {
				MethodStatement mStat = test.findTargetMethodCallStatement();
				inputObject = mStat.getCallee();				
			}
			else {
				for(DepVariableWrapper parentNode: node.parents) {
					if(map.get(parentNode.var) != null) {
						inputObject = map.get(parentNode.var).get(0);
						break;
					}
				}
			}
			
		} else if (var.getType() == DepVariable.ARRAY_ELEMENT) {
			inputObject = generateArrayElementStatement(test, node, isLeaf, inputObject);
		}
		
		if (inputObject != null) {
			List<VariableReference> list = map.get(var);
			if(list == null){
				list = new ArrayList<>();
			}
			if(!list.contains(inputObject)){
				list.add(inputObject);					
			}
			map.put(var, list);
		}
		
		return true;
	}

	private VariableReference generateArrayElementStatement(TestCase test, DepVariableWrapper node, boolean isLeaf,
			VariableReference parentVarRef) throws ConstructionFailedException {

		if (parentVarRef instanceof ArrayReference) {
			int opcodeRead = node.var.getInstruction().getASMNode().getOpcode();
			int opcodeWrite = getCorrespondingWriteOpcode(opcodeRead);
			VariableReference realParentRef = null;
			Statement statement = test.getStatement(parentVarRef.getStPosition());

			if (statement instanceof MethodStatement) {
				MethodStatement mStatement = (MethodStatement) statement;
				realParentRef = mStatement.getCallee();
			}
			
			double prob = Randomness.nextDouble();
			if (realParentRef  != null && prob > 0.99) {
				/**
				 * check reused array element
				 */
				VariableReference usedArrayRef = isLeaf
						? searchArrayElementWritingReference(test, node, parentVarRef, realParentRef, opcodeWrite)
						: searchArrayElementReadingReference(test, node, parentVarRef, realParentRef, opcodeRead);
				if (usedArrayRef != null) {
					return isLeaf ? null : usedArrayRef;
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
						return newParentVarRef;
					}
					return null;
				}
			}
			/**
			 * direct set
			 */
			else{
				ArrayReference arrayRef = (ArrayReference) parentVarRef;
				int index = Randomness.nextInt(10);
				
//				GenericClass clazz = new GenericClass(int.class);
//				VariableReference indexVariable = TestFactory.getInstance().
//						createPrimitive(test, clazz, arrayRef.getStPosition()+1, 0);
//				Statement stat = test.getStatement(indexVariable.getStPosition());
//				IntPrimitiveStatement iStat = (IntPrimitiveStatement)stat;
//				
//				iStat.setValue(index);
				
				ArrayIndex arrayIndex = new ArrayIndex(test, arrayRef, index);
				VariableReference varRef = createArrayElementVariable(test, arrayRef);
				AssignmentStatement assignStat = new AssignmentStatement(test, arrayIndex, varRef);
				test.addStatement(assignStat, varRef.getStPosition() + 1);
				return assignStat.getReturnValue();				
			}
		}
		return null;
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
			Map<DepVariable, List<VariableReference>> map, VariableReference inputObject) {
		DepVariable var = node.var;
		int opcode = var.getInstruction().getASMNode().getOpcode();
		try {
			MethodInsnNode methodNode = ((MethodInsnNode) var.getInstruction().getASMNode());
			String owner = methodNode.owner;
			String fieldOwner = owner.replace("/", ".");
			String fullName = methodNode.name + methodNode.desc;
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
//			if (fieldDeclaringClass.isInterface() || Modifier.isAbstract(fieldDeclaringClass.getModifiers())) {
//				return targetObject;
//			}
			org.objectweb.asm.Type[] types = org.objectweb.asm.Type
					.getArgumentTypes(fullName.substring(fullName.indexOf("("), fullName.length()));
			Class<?>[] paramClasses = new Class<?>[types.length];
			int index = 0;
			for (org.objectweb.asm.Type type : types) {
				Class<?> paramClass = getClassForType(type);
				paramClasses[index++] = paramClass;
			}

			if (!fullName.contains("<init>")) {
				Method call = fieldDeclaringClass.getMethod(fullName.substring(0, fullName.indexOf("(")), paramClasses);

				VariableReference calleeVarRef = null;
				Map<Integer, VariableReference> paramRefMap = new HashMap<>();

				for (DepVariableWrapper par : node.parents) {
					VariableReference parRef = map.get(par.var).get(0);
					int position = par.var.findRelationPosition(var);
					if (position > -1) {
						paramRefMap.put(position, parRef);
					}
				}

				if (opcode == Opcodes.INVOKESTATIC) {
					calleeVarRef = null;
					GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
					VariableReference varRef = testFactory.addMethod(test, genericMethod,
							inputObject.getStPosition() + 1, 1, false);
					MethodStatement statement = (MethodStatement) test.getStatement(varRef.getStPosition());
					for (int i = 0; i < statement.getParameterReferences().size();i ++) {
						VariableReference oldParam = statement.getParameterReferences().get(i);
						VariableReference newParam = paramRefMap.get(i);
						if (newParam != null) {
							statement.replace(oldParam, newParam);
						}
					}
					return varRef;
				} else {
					calleeVarRef = paramRefMap.get(0);
					if (calleeVarRef != null) {
						GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
						VariableReference varRef = testFactory.addMethodFor(test, calleeVarRef, genericMethod,
								calleeVarRef.getStPosition() + 1, false);
						MethodStatement statement = (MethodStatement) test.getStatement(varRef.getStPosition());
						for (int i = 0; i < statement.getParameterReferences().size();i ++) {
							VariableReference oldParam = statement.getParameterReferences().get(i);
							VariableReference newParam = paramRefMap.get(i + 1);
							if (newParam != null) {
								statement.replace(oldParam, newParam);
							}
						}
						return varRef;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
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
	private VariableReference generateFieldStatement(TestCase test, DepVariable var, boolean isLeaf,
			VariableReference targetObjectReference) {
		FieldInsnNode fieldNode = (FieldInsnNode) var.getInstruction().getASMNode();
		String fieldType = fieldNode.desc;
		String fieldOwner = fieldNode.owner.replace("/", ".");
		String fieldName = fieldNode.name;

		if (targetObjectReference != null) {
			String parentType = targetObjectReference.getClassName();
			if (!isPrimitiveClass(parentType)) {
				if (!isCompatible(fieldOwner, parentType)) {
					return targetObjectReference;
				}
			}
		}

		try {
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
			registerAllMethods(fieldDeclaringClass);
			
			Field field = searchForField(fieldDeclaringClass, fieldName);
			
			/**
			 * if the field is leaf, check if there is setter in the testcase
			 * if the field is not leaf, check if there is getter in the testcase
			 * if found, stop here
			 */
			UsedReferenceSearcher usedRefSearcher = new UsedReferenceSearcher();
			VariableReference usedFieldInTest = isLeaf
					? usedRefSearcher.searchRelevantFieldWritingReferenceInTest(test, field, targetObjectReference)
					: usedRefSearcher.searchRelevantFieldReadingReferenceInTest(test, field, targetObjectReference);
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
				VariableReference obj = 
					generatePublicFieldSetterOrGetter(test, targetObjectReference, fieldType, genericField);
				return obj;
			}

			/**
			 * deal with non-public field
			 */
			if (!isLeaf) {
				Method getter = searchForPotentialGetterInClass(fieldDeclaringClass, field);
				if (getter != null) {
					VariableReference newParentVarRef = null;
					GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
					if (targetObjectReference == null) {
						MethodStatement mStat = test.findTargetMethodCallStatement();
						newParentVarRef = testFactory.addMethod(test, gMethod, mStat.getPosition() - 1, 2, false);
					} else {
						newParentVarRef = testFactory.addMethodFor(test, targetObjectReference, gMethod,
								targetObjectReference.getStPosition() + 1, false);
					}
					return newParentVarRef;
				}
				return null;
			} 
			else {
				/**
				 * generate setter in current class
				 */
				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getInstructionsIn(fieldDeclaringClass.getName());

				if (insList == null) {
					return null;
				}
				
				String targetClassName = checkTargetClassName(field, targetObjectReference);
				Executable setter = searchForPotentialSetterInClass(field, targetClassName);
				if (setter != null) {
					if(setter instanceof Method){
						GenericMethod gMethod = new GenericMethod((Method)setter, setter.getDeclaringClass());
						if (targetObjectReference == null) {
							MethodStatement mStat = test.findTargetMethodCallStatement();
							testFactory.addMethod(test, gMethod, mStat.getPosition() - 1, 2, false);
						} else {
							testFactory.addMethodFor(test, targetObjectReference, gMethod,
									targetObjectReference.getStPosition() + 1, false);
						}
						return null;
					}
					else if(setter instanceof Constructor){
						GenericConstructor gConstructor = new GenericConstructor((Constructor)setter,
								setter.getDeclaringClass());
						VariableReference returnedVar = testFactory.addConstructor(test, gConstructor,
								targetObjectReference.getStPosition() + 1, 2);

						for (int i = 0; i < test.size(); i++) {
							Statement stat = test.getStatement(i);
							if (returnedVar.getStPosition() < stat.getPosition()) {
								if (stat.references(targetObjectReference)) {
									stat.replace(targetObjectReference, returnedVar);
								}
							}
						}
						return null;
					}
				}
				
				return null;

			}

		} catch (ClassNotFoundException | SecurityException | 
				ConstructionFailedException e) {
			e.printStackTrace();
			return null;
		}

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
			String fieldType, GenericField genericField) throws ConstructionFailedException {
		AbstractStatement stmt;
		if (CollectionUtil.existIn(fieldType, "Z", "B", "C", "S", "I", "J", "F", "D")) {
			stmt = addStatementToSetOrGetPublicField(test, fieldType,
					genericField, targetObjectReference, new PrimitiveFieldInitializer());
		} else {
			stmt = addStatementToSetOrGetPublicField(test, fieldType,
					genericField, targetObjectReference, new NonPrimitiveFieldInitializer());
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
	private void registerAllMethods(Class<?> fieldDeclaringClass) {
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
	 * @param parentVarRef
	 * @param castSubClass
	 * @return
	 * @throws ConstructionFailedException
	 * @throws ClassNotFoundException
	 */
	private VariableReference generateParameterStatement(TestCase test, DepVariable var,
			VariableReference parentVarRef, String castSubClass)
			throws ConstructionFailedException, ClassNotFoundException {

		/**
		 * find the existing parameters
		 */
		if (parentVarRef == null) {
			MethodStatement mStat = test.findTargetMethodCallStatement();
			int paramPosition = var.getParamOrder();
			VariableReference paramRef = mStat.getParameterReferences().get(paramPosition - 1);

			/**
			 * make sure the parameter obj is not null
			 */
			
			int paramPosInTest = paramRef.getStPosition();
			Statement paramDef = test.getStatement(paramPosInTest);
			if(paramDef instanceof NullStatement){
				TestFactory testFactory = TestFactory.getInstance();
				boolean isSuccess = testFactory.changeNullStatement(test, paramDef);
				if(isSuccess){
					paramRef = mStat.getParameterReferences().get(paramPosition - 1);
				}
				
			}
			
			return paramRef;
		}

		VariableReference paramRef = generateParameter(test, var, castSubClass);

		if (paramRef == null) {
			return parentVarRef;
		}

		MethodStatement targetStatement = test.findTargetMethodCallStatement();
		if (targetStatement != null) {
			VariableReference oldParamRef = targetStatement.getParameterReferences().get(var.getParamOrder() - 1);
			targetStatement.replace(oldParamRef, paramRef);
		}

		return paramRef;
	}

	private VariableReference generateParameter(TestCase test, DepVariable var, String castSubClass)
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
			paramRef = testFactory.addConstructor(test, gc, mStat.getPosition() - 1, 2);
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
		List<Integer> numberOfValidParams = new ArrayList<>();
		
		for(Method m: targetClass.getMethods()){
			String signature = m.getName() + ReflectionUtil.getSignature(m);
			findSetterInfo(field, targetClass, fieldSettingMethods, difficultyList, numberOfValidParams, m, signature);
		}
		
		for(Constructor c: targetClass.getConstructors()){
			String signature = "<init>" + ReflectionUtil.getSignature(c);
			findSetterInfo(field, targetClass, fieldSettingMethods, difficultyList, numberOfValidParams, c, signature);
		}
		
		if(!fieldSettingMethods.isEmpty()){
//			Executable entry = Randomness.choice(fieldSettingMethods);
			double[] scores = new double[fieldSettingMethods.size()];
			for(int i=0; i<scores.length; i++){
				scores[i] = estimateCoverageLikelihood(difficultyList.get(i), numberOfValidParams.get(i));
				System.currentTimeMillis();
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
		//TODO too simple?
		return (double)validParamNum /(map.size());
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
			List<Map<BytecodeInstruction, List<BytecodeInstruction>>> difficultyList, List<Integer> numberOfValidParams,
			Executable m, String signature) {
		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = 
				DataDependencyUtil.analyzeFieldSetter(targetClass.getCanonicalName(), signature,
						field, 5, cascadingCallRelations, setterMap);
		
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
			numberOfValidParams.add(releventPrams.size());
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
	
	public static VariableReference addConstructorForClass(TestFactory testFactory, TestCase test, int position, String desc)
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
			GenericField genericField, VariableReference parentVarRef, FieldInitializer fieldInitializer) 
					throws ConstructionFailedException {
		MethodStatement mStat = test.findTargetMethodCallStatement();
		int insertionPosition = (parentVarRef != null) ? 
				parentVarRef.getStPosition() + 1 : mStat.getPosition() - 1; 
		
		FieldReference fieldVar = null;
		if (genericField.isStatic() || parentVarRef == null) {
			fieldVar = new FieldReference(test, genericField);
		} else {
			fieldVar = new FieldReference(test, genericField, parentVarRef);
		}

		VariableReference objRef = fieldInitializer.assignField(testFactory, test, fieldType, 
				genericField, insertionPosition, fieldVar);	
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

	private Type convertToType(String desc) {
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


}
