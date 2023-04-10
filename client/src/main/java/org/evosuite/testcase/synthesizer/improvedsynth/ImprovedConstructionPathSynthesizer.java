package org.evosuite.testcase.synthesizer.improvedsynth;

import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.runtime.System;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.*;
import org.evosuite.testcase.statements.numeric.*;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.UsedReferenceSearcher;
import org.evosuite.testcase.synthesizer.VariableInTest;
import org.evosuite.testcase.synthesizer.var.*;
import org.evosuite.testcase.variable.ArrayIndex;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ImprovedConstructionPathSynthesizer extends ConstructionPathSynthesizer {
	private AccessibilityMatrixManager accessibilityMatrixManager;
	
	public ImprovedConstructionPathSynthesizer(boolean isDebug) {
		super(isDebug);
	}
	
	private List<ConstructionPath> getConstructionPathsFrom(PartialGraph partialGraph) {
		List<ConstructionPath> constructionPaths = new ArrayList<>();
		
		accessibilityMatrixManager = new AccessibilityMatrixManager();
		accessibilityMatrixManager.initialise(partialGraph);
		
		List<DepVariableWrapper> rootNodes = partialGraph.getTopLayer();
		List<DepVariableWrapper> leafNodes = partialGraph.getLeaves();
		for (DepVariableWrapper leafNode : leafNodes) {
			for (DepVariableWrapper rootNode : rootNodes) {
				boolean isPathExists = accessibilityMatrixManager.findShortestPathLength(rootNode, leafNode) > 0;
				if (isPathExists) {
					List<DepVariableWrapper> path = getPathBetween(rootNode, leafNode);
					if (path == null || path.size() < 2) {
						continue;
					}
					
					List<Operation> operations = generateOperations(path);	
					ConstructionPath constructionPath = new ConstructionPath(operations, path);
					constructionPaths.add(constructionPath);
					break;
				}
			}
		}

		return constructionPaths;
	}
	
	public void buildNodeStatementCorrespondence(TestCase testCase, Branch branch, boolean isAllowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {
		PartialGraph partialGraph = constructPartialComputationGraph(branch);
		
//		if (isDebugger) {
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 5000, "test");
//		}
		
		List<ConstructionPath> constructionPaths = getConstructionPathsFrom(partialGraph);
		Map<DepVariableWrapper, VarRelevance> nodeToVarReference = new HashMap<>();
		
		for (ConstructionPath constructionPath : constructionPaths) {
			generateTestCaseStatementsUsing(constructionPath, nodeToVarReference, testCase, branch, isAllowNullValue);
		}
				
		this.setPartialGraph(partialGraph);
		this.setGraph2CodeMap(nodeToVarReference);
	}

	private void generateTestCaseStatementsUsing(
			ConstructionPath constructionPath, 
			Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance,
			TestCase testCase,
			Branch branch,
			boolean isAllowNullValue
	) {
		List<DepVariableWrapper> nodes = constructionPath.getPath();
		List<Operation> operations = constructionPath.getOperations();
		// Process the first node before starting on the rest
		// We assume it is a ThisVariableWrapper
		// Panic otherwise
		boolean isRootThisVariableWrapper = (constructionPath.getRoot() instanceof ThisVariableWrapper);
		if (!isRootThisVariableWrapper) {
			return;
		}
		ThisVariableWrapper rootNode = (ThisVariableWrapper) constructionPath.getRoot();
		VarRelevance thisRelevance = DepVariableWrapperUtil.getVarRelevanceFrom(rootNode, testCase, nodeToVarRelevance);
		if (thisRelevance == null) {
			return;
		}
		addOrMerge(nodeToVarRelevance, rootNode, thisRelevance);
		
		for (int i = 0; i < operations.size(); i++) {
			DepVariableWrapper prevNode = nodes.get(i);
			DepVariableWrapper nextNode = nodes.get(i + 1);
			Operation operation = operations.get(i);
			
			// Infer what to do from the operation
			boolean isArrayElementAccess = operation instanceof ArrayElementAccess;
			boolean isFieldAccess = operation instanceof FieldAccess;
			boolean isMethodCall = operation instanceof MethodCall;
			boolean isParameter = operation instanceof ParameterReference;
			boolean isThis = operation instanceof ThisReference;
			
			if (isArrayElementAccess) {
				ArrayElementAccess arrayElementAccess = (ArrayElementAccess) operation;
				generateArrayElementAccessStatement(prevNode, nextNode, arrayElementAccess, testCase, nodeToVarRelevance, isAllowNullValue);
			}
			
			if (isFieldAccess) {
				FieldAccess fieldAccess = (FieldAccess) operation;
				generateFieldAccessStatement(nextNode, fieldAccess, testCase, nodeToVarRelevance, isAllowNullValue);
			}
			
			if (isMethodCall) {
				MethodCall methodCall = (MethodCall) operation;
				generateMethodCallStatement(prevNode, nextNode, methodCall, testCase, nodeToVarRelevance, isAllowNullValue);
			}
			
			if (isParameter) {
				// ParameterReference parameterReference = (ParameterReference) operation;
				// The operation here shouldn't be different from the original algorithm
				// For now, try using the original algorithm's approach here
				// TODO: 
				//   Tidy this up, different level of abstraction v.s. other branches
				//   This should probably be in a method.
				boolean isNodeParameterVariableWrapper = (nextNode instanceof ParameterVariableWrapper);
				if (!isNodeParameterVariableWrapper) {
					// Error state, panic
					continue;
				}
				
				ParameterVariableWrapper parameterVariableWrapper = (ParameterVariableWrapper) nextNode;
				boolean isLeaf = parameterVariableWrapper.children == null || parameterVariableWrapper.children.isEmpty();
				addOrMerge(nodeToVarRelevance, parameterVariableWrapper, parameterVariableWrapper.generateOrFind(testCase, isLeaf, getCallerObject(nodeToVarRelevance, parameterVariableWrapper).callerObject, nodeToVarRelevance, branch, isAllowNullValue));
			}
			
			if (isThis) {
				// ThisReference thisReference = (ThisReference) operation;
				// In theory, we shouldn't need to do anything
				// A ThisReference should always be backed by a statement in the seed test
				// TODO: See if this assertion holds true
			}
		}
	}
	
	private VariableReference deriveCallerObjectFrom(Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance, DepVariableWrapper node) {
		VarRelevance varRelevanceOfPrevNode = nodeToVarRelevance.get(node);
		if (varRelevanceOfPrevNode == null) {
			// Error state, panic
			return null;
		}
		List<VariableReference> matchedVarsOfPrevNode = varRelevanceOfPrevNode.matchedVars;
		if (matchedVarsOfPrevNode.size() == 0) {
			// Error state, panic
			return null;
		}
		return matchedVarsOfPrevNode.get(0);
	}
	
	// The ArrayElementAccess operation will typically contain a hint as to what
	// the index accessed in the branch is
	// This will allow us to build a similar index for getter/setter during object construction
	private static int generateArrayIndexFrom(BytecodeInstruction hintInstruction, int arrayLength) {
		int defaultReturnValue = arrayLength > 1 ? Randomness.nextInt(arrayLength - 1) : 0;
		if (hintInstruction == null) {
			return defaultReturnValue;
		}
		
		try {
			int hintOpcode = hintInstruction.getASMNode().getOpcode();
			switch (hintOpcode) {
				case (Opcodes.ICONST_0):
					return 0;
				case (Opcodes.ICONST_1):
					return 1;
				case (Opcodes.ICONST_2):
					return 2;
				case (Opcodes.ICONST_3):
					return 3;
				case (Opcodes.ICONST_4):
					return 4;
				case (Opcodes.ICONST_5):
					return 5;
				default:
					return defaultReturnValue;
			}
		} catch (NullPointerException e) {
			return defaultReturnValue;
		}
		
		// TODO: deal with ALOAD/ILOAD etc. and generate the appropriate index
	}
	
	private void generateArrayElementAccessStatement(
			DepVariableWrapper prevNode,
			DepVariableWrapper currentNode, 
			ArrayElementAccess arrayElementAccess,
			TestCase testCase, 
			Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance, 
			boolean isAllowNullValue
		) {
		boolean isLeaf = (currentNode.children == null || currentNode.children.isEmpty());
		if (!(currentNode instanceof ArrayElementVariableWrapper)) {
			return;
		}
		VariableInTest variableInTest = getCallerObject(nodeToVarRelevance, currentNode);
		VariableReference callerObject = (variableInTest == null ? null : variableInTest.callerObject);
		if (callerObject == null) {
			callerObject = deriveCallerObjectFrom(nodeToVarRelevance, prevNode);
			if (callerObject == null) {
				// Error state, panic
				return;
			}
		}
		
		Statement stat = testCase.getStatement(callerObject.getStPosition());
		if(stat instanceof NullStatement) {
			List<VariableReference> list = new ArrayList<>();
			list.add(stat.getReturnValue());
			addOrMerge(nodeToVarRelevance, currentNode, new VarRelevance(list, list));
			return;
		}
		
		
		int readOpcode = currentNode.var.getInstruction().getASMNode().getOpcode();
		int writeOpcode = -1;
		try {
			writeOpcode = getCorrespondingWriteOpcode(readOpcode);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}

		// Check for reused array element
		List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(testCase, variableInTest);
		if(!usedArrayElementList.isEmpty()) {
			addOrMerge(nodeToVarRelevance, currentNode, new VarRelevance(usedArrayElementList, usedArrayElementList));
		}
		
		List<VariableReference> usedArrayRefs = isLeaf
				? searchArrayElementWritingReference(testCase, variableInTest, writeOpcode)
				: searchArrayElementReadingReference((ArrayElementVariableWrapper) currentNode, testCase, variableInTest, readOpcode);
		if (usedArrayRefs != null && !usedArrayRefs.isEmpty()) {
			addOrMerge(nodeToVarRelevance, currentNode, new VarRelevance(usedArrayRefs, usedArrayRefs));
		}
		
		// TODO: Search for array getter/setter
		
		// Try to directly set array element
		if (callerObject instanceof ArrayReference) {
			ArrayReference arrayReference = (ArrayReference) variableInTest.callerObject;
			int arrayLength = arrayReference.getArrayLength();
			if(arrayReference.getArrayLength() <= 0) {
				arrayLength = Randomness.nextInt(10) + 1;
			}
			int index = generateArrayIndexFrom(arrayElementAccess.getIndexInstruction(), arrayLength);
			
			ArrayIndex arrayIndex = new ArrayIndex(testCase, arrayReference, index);
			VariableReference varRef = createArrayElementVariable(testCase, arrayReference);
			if (varRef == null) {
				return;
			}
			
			AssignmentStatement assignStat = new AssignmentStatement(testCase, arrayIndex, varRef);
			testCase.addStatement(assignStat, varRef.getStPosition() + 1);
			VariableReference ref = assignStat.getReturnValue();
			List<VariableReference> vars = new ArrayList<>();
			if (ref != null) {
				vars.add(ref);						
			}
			
			addOrMerge(nodeToVarRelevance, currentNode, new VarRelevance(vars, vars));
		}
	}

	private void generateMethodCallStatement(
			DepVariableWrapper prevNode,
			DepVariableWrapper currentNode, 
			MethodCall methodCall, 
			TestCase testCase,
			Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance, 
			boolean isAllowNullValue
		) {
		Method method = methodCall.getMethod();
		Field field = DepVariableWrapperUtil.extractFieldFrom(currentNode);
		boolean isStaticField = Modifier.isStatic(field.getModifiers());
		boolean isInstanceField = !isStaticField;
		VariableInTest variableInTest = getCallerObject(nodeToVarRelevance, currentNode);
		boolean isLeaf = currentNode.children == null || currentNode.children.isEmpty();
		VariableReference callerObject = (variableInTest == null ? null : variableInTest.callerObject);
		
		if (isInstanceField) {
			if (variableInTest == null) {
				return;
			}
			
			if (variableInTest.callerObject == null) {
				// It's possible that we "skip" a few nodes when generating
				// In that case, we need to look at the previous node
				// in the construction path to provide a hint
				// as to what the caller object should be
				callerObject = deriveCallerObjectFrom(nodeToVarRelevance, prevNode);
				if (callerObject == null) {
					// Error state, panic
					return;
				}
			}
		}
		
		String fieldOwner = field.getDeclaringClass().getCanonicalName();
		String fieldName = field.getName();
		
		// Ignore String.value since String is immutable
		if (fieldOwner.equals("java.lang.String") && fieldName.equals("value")) {
			return;
		}
		
		Statement stat = testCase.getStatement(callerObject.getStPosition());
		if (stat instanceof NullStatement) {
			addOrMerge(nodeToVarRelevance, currentNode, stat.getReturnValue());
		}
		
//		We don't use this check since it's possible that we call a setter method
//		from a class that isn't the direct owner of the field 
//		e.g. Parent -> Child -> integer and Parent exposes a setInteger method
//		We might call Parent#setInteger to set Child.integer. In these cases,
//		The callerType and fieldOwner aren't compatible (and that's okay)
//		
//		String callerType = variableInTest.callerObject.getClassName();
//		if ((!VariableCodeGenerationUtil.isPrimitiveClass(callerType)) 
//				&& (!VariableCodeGenerationUtil.isCompatible(fieldOwner, callerType))) {
//			return;
//		}

		UsedReferenceSearcher usedRefSearcher = new UsedReferenceSearcher();
		VariableReference usedFieldInTest;
		if (isLeaf) {
			// if the field is leaf, check if there is setter in the testcase
			usedFieldInTest = usedRefSearcher.searchRelevantFieldWritingReferenceInTest(testCase, field, variableInTest.callerObject);
		} else {
			// if the field is not leaf, check if there is getter in the testcase
			usedFieldInTest = usedRefSearcher.searchRelevantFieldReadingReferenceInTest(testCase, field, variableInTest.callerObject);
		}
		
		if (usedFieldInTest != null) {
			// Generate some elements for container classes
			if(Collection.class.isAssignableFrom(usedFieldInTest.getVariableClass())) {
				VariableCodeGenerationUtil.generateElements(field.getType(), testCase, usedFieldInTest);
			}
			
			addOrMerge(nodeToVarRelevance, currentNode, DepVariableWrapperUtil.generateVarRelevanceFrom(usedFieldInTest));
			return;
		}

		// Try to generate the relevant statement in the test case
		GenericField genericField = new GenericField(field, field.getDeclaringClass());
		if (genericField.isFinal()) {
			return;
		}
		
		if (!isLeaf) {
			// Generate getter
			try {
				VariableReference getterObject = VariableCodeGenerationUtil.generateFieldGetterInTest(testCase, callerObject, nodeToVarRelevance, field.getDeclaringClass(), field, usedRefSearcher, method);
				addOrMerge(nodeToVarRelevance, currentNode, getterObject);
				return;
			} catch (ConstructionFailedException e) {
				e.printStackTrace();
			}
		} else {
			// Generate setter
			VariableReference setterObject;
			try {
				setterObject = VariableCodeGenerationUtil.generateFieldSetterInTest(testCase, callerObject, nodeToVarRelevance, field.getDeclaringClass(), field, isAllowNullValue, method);
				if (setterObject != null) {
					Statement statement = testCase.getStatement(setterObject.getStPosition());
					if (statement instanceof MethodStatement) {
						MethodStatement methodStatement = (MethodStatement) statement;
						if (!methodStatement.getParameterReferences().isEmpty()) {
							VariableReference variableReference = methodStatement.getParameterReferences().get(0);
							addOrMerge(nodeToVarRelevance, currentNode, variableReference);
							return;
						}
					}
				}
				addOrMerge(nodeToVarRelevance, currentNode, setterObject);
				return;
			} catch (ClassNotFoundException | ConstructionFailedException e) {
				e.printStackTrace();
			}
		}
	}

	private void generateFieldAccessStatement(
			DepVariableWrapper node, 
			FieldAccess fieldAccess, 
			TestCase testCase,
			Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance,
			boolean isAllowNullValue
		) {
		Field field = fieldAccess.getField();
		boolean isStaticField = Modifier.isStatic(field.getModifiers());
		boolean isInstanceField = !isStaticField;
		VariableInTest variableInTest = getCallerObject(nodeToVarRelevance, node);
		boolean isLeaf = node.children == null || node.children.isEmpty();
		
		if (isInstanceField) {
			if (variableInTest == null || variableInTest.callerObject == null) {
				return;
			}
		}
		
		VariableReference callerObject = variableInTest.callerObject;
				
		String fieldOwner = field.getDeclaringClass().getCanonicalName();
		String fieldName = field.getName();
		String fieldType = field.getType().getCanonicalName();
		
		// Ignore String.value
		if (fieldOwner.equals("java.lang.String") && fieldName.equals("value")) {
			return;
		}
		
		Statement stat = testCase.getStatement(callerObject.getStPosition());
		if (stat instanceof NullStatement) {
			addOrMerge(nodeToVarRelevance, node, DepVariableWrapperUtil.generateVarRelevanceFrom(stat.getReturnValue()));
		}
		
		String callerType = variableInTest.callerObject.getClassName();
		if ((!VariableCodeGenerationUtil.isPrimitiveClass(callerType)) 
				&& (!VariableCodeGenerationUtil.isCompatible(fieldOwner, callerType))) {
			return;
		}

		UsedReferenceSearcher usedRefSearcher = new UsedReferenceSearcher();
		VariableReference usedFieldInTest;
		if (isLeaf) {
			// if the field is leaf, check if there is setter in the testcase
			usedFieldInTest = usedRefSearcher.searchRelevantFieldWritingReferenceInTest(testCase, field, variableInTest.callerObject);
		} else {
			// if the field is not leaf, check if there is getter in the testcase
			usedFieldInTest = usedRefSearcher.searchRelevantFieldReadingReferenceInTest(testCase, field, variableInTest.callerObject);
		}
		
		if (usedFieldInTest != null) {
			// Generate some elements for container classes
			if(Collection.class.isAssignableFrom(usedFieldInTest.getVariableClass())) {
				VariableCodeGenerationUtil.generateElements(field.getType(), testCase, usedFieldInTest);
			}
			
			addOrMerge(nodeToVarRelevance, node, DepVariableWrapperUtil.generateVarRelevanceFrom(usedFieldInTest));
			return;
		}

		// Try to generate the relevant statement in the test case
		GenericField genericField = new GenericField(field, field.getDeclaringClass());
		if (genericField.isFinal()) {
			return;
		}
		
		// If the field is primitive, we need to change the value of "fieldType"
		// The field type encoding that VariableCodeGenerationUtil.generatePublicFieldSetterOrGetter
		// expects is the JNI style field types (e.g. I for int), which is not what field.getType().getCanonicalName()
		// returns (e.g. "int" for int). Hence, we need to "translate" it back
		if (field.getType().isPrimitive()) {
			fieldType = translatePrimitiveTypeToJniStyle(fieldType);
		}
		// TODO: Check if the method expects JNI style field types for e.g. arrays
		
		// Try three times before we give up
		for (int i = 0; i < 3; i++) {
			try {
				VariableReference variableReference = VariableCodeGenerationUtil.generatePublicFieldSetterOrGetter(testCase, variableInTest.callerObject, fieldType, genericField, isAllowNullValue);
				addOrMerge(nodeToVarRelevance, node, DepVariableWrapperUtil.generateVarRelevanceFrom(variableReference));
				return;
			} catch(ConstructionFailedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String translatePrimitiveTypeToJniStyle(String fieldType) {
		switch (fieldType) {
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
			return fieldType;
		}
	}
	
	private List<Operation> generateOperations(List<DepVariableWrapper> path) {		
		List<Operation> operations = new ArrayList<>();
		DepVariableWrapper currentNode = path.get(0); // closer to root
		DepVariableWrapper greedyNode = path.get(path.size() - 1); // closer to leaf
		DepVariableWrapper nextNode; // not used

		ConstructionPath accessPath = accessibilityMatrixManager.getNodeAccessPath(currentNode, greedyNode);
		if (accessPath != null) {
			operations.addAll(accessPath.getOperations());
			return operations;
		}

		// old approach
		for (int i = 1; i < path.size(); i++) {
			nextNode = path.get(i);
			accessPath = accessibilityMatrixManager.getNodeAccessPath(currentNode, nextNode);
			if (accessPath == null) {
				return new ArrayList<>(); // Access chain is broken, failure
			}
			operations.addAll(accessPath.getOperations());
			currentNode = nextNode;
		}

		return operations;
	}
	
	private List<DepVariableWrapper> getPathBetween(DepVariableWrapper sourceNode, DepVariableWrapper endNode) {
		// Enhanced BFS to track the path taken
		// See https://stackoverflow.com/questions/8922060/how-to-trace-the-path-in-a-breadth-first-search/50575971#50575971
		Set<DepVariableWrapper> visitedNodes = new HashSet<>();
		List<DepVariableWrapper> path = null;
		
		Queue<BfsNodeWrapper> queue = new ArrayDeque<>();
		List<DepVariableWrapper> initialPath = new ArrayList<>();
		initialPath.add(sourceNode);
		queue.offer(new BfsNodeWrapper(sourceNode, initialPath));
		visitedNodes.add(sourceNode);
		while (!queue.isEmpty()) {
			BfsNodeWrapper currentNodeWrapper = queue.poll();
			List<DepVariableWrapper> currentPath = currentNodeWrapper.getPath();
			if (currentNodeWrapper.getNode() == endNode) {
				path = currentPath;
				break;
			}
			List<DepVariableWrapper> currentNodeNeighbours = accessibilityMatrixManager.getNeighboursOf(currentNodeWrapper.getNode());
			for (DepVariableWrapper neighbour : currentNodeNeighbours) {
				if (!visitedNodes.contains(neighbour)) {
					visitedNodes.add(neighbour);
					List<DepVariableWrapper> currentPathPlusNeighbour = new ArrayList<>(currentPath);
					currentPathPlusNeighbour.add(neighbour);
					queue.offer(new BfsNodeWrapper(neighbour, currentPathPlusNeighbour));
				}
			}
		}
		return path;
	}
	
	private static void addOrMerge(Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance, DepVariableWrapper key, VarRelevance value) {
		if (nodeToVarRelevance.containsKey(key)) {
			VarRelevance oldVarRelevance = nodeToVarRelevance.get(key);
			oldVarRelevance.merge(value);
			nodeToVarRelevance.put(key, oldVarRelevance);	
		} else {
			nodeToVarRelevance.put(key, value);
		}
	}
	
	private static void addOrMerge(Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance, DepVariableWrapper key, VariableReference variableReference) {
		VarRelevance value = DepVariableWrapperUtil.generateVarRelevanceFrom(variableReference);
		addOrMerge(nodeToVarRelevance, key, value);
	}
	
	private static int getCorrespondingWriteOpcode(int readOpcode) {
		switch (readOpcode) {
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

	// TODO: Check if this works for our new approach 
	// nodePath might no longer be accurate?
	private static List<VariableReference> searchUsedArrayElementReference(TestCase test, VariableInTest variable) {
		List<VariableReference> elementList = new ArrayList<>();
		if(variable.callerObject instanceof ArrayReference && variable.nodePath.size() == 1) {
			ArrayReference arrayObject = (ArrayReference) variable.callerObject;
			for (int i = 0; i < test.size(); i++) {
				Statement statement = test.getStatement(i);
				if (statement instanceof AssignmentStatement) {
					AssignmentStatement assignmentStatement = (AssignmentStatement) statement;
					VariableReference var = assignmentStatement.getReturnValue();
					if (var.isArrayIndex()) {
						if (var instanceof ArrayIndex) {
							ArrayIndex index = (ArrayIndex)var;
							if (index.getArray().equals(arrayObject)) {
								elementList.add(assignmentStatement.getValue());
							}
						}
					}
				}
			}
		}
		
		return elementList;
	}
	
	
	/**
	 * check if current method is a valid getter for array element
	 *
	 * @param node
	 * @param opcodeRead
	 * @return
	 */
	private static boolean checkValidArrayElementGetter(ArrayElementVariableWrapper node, Method method, int opcodeRead, VariableInTest variable) {
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
			//TODO needs to check whether the instruction can correspond to the VariableInTest
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
	
	/*
	 * AALOAD, BALOAD, CALOAD, DALOAD, FALOAD, IALOAD, LALOAD, SALOAD
	 */
	private static List<VariableReference> searchArrayElementReadingReference(ArrayElementVariableWrapper node, TestCase test, VariableInTest variable, int opcodeRead) {
		List<VariableReference> varList = new ArrayList<VariableReference>();
		
		for (int i = 0; i < test.size(); i ++) {
			Statement statement = test.getStatement(i);
			// check reading through method call
			if (statement instanceof MethodStatement) {
				MethodStatement methodStatement = (MethodStatement) statement;
				Method method = methodStatement.getMethod().getMethod();
				VariableReference variableReference = methodStatement.getCallee();
				if (variableReference != null && variableReference.equals(variable.callerObject)) {
					boolean isValid = checkValidArrayElementGetter(node, method, opcodeRead, variable);
					if (isValid) {
						VariableReference var = methodStatement.getReturnValue();
						varList.add(var);
					}
				}
			}
		}
		
		return varList;
	}

	/**
	 * check whether this method can cover the instructions on the node path,
	 * 
	 * if yes, we return the parameter with dataflow to setting the array element.
	 * 
	 * @param ins
	 * @param variable
	 * @return
	 */
	private static BytecodeInstruction isMatchPath(BytecodeInstruction ins, VariableInTest variable) {
		BytecodeInstruction param = null;
		/**
		 * correspond to each node in the path
		 */
		boolean[] visitedPath = new boolean[variable.nodePath.size()];
		int cursor = variable.nodePath.size()-1;
		visitedPath[cursor] = true;
		cursor--;
		
		while(cursor >= 0) {
			boolean isValid = false;
			for (int i = 0; i < ins.getOperandNum(); i++) {
				BytecodeInstruction defIns = ins.getSourceOfStackInstruction(i);
				
				if(defIns.isParameter()) {
					param = defIns;
				}
				
				if(defIns.equals(variable.nodePath.get(cursor).var.getInstruction())) {
					cursor--;
					isValid = true;
					break;
				}
			}
			
			if(!isValid) {
				break;
			}
		}
		
		boolean isMatch = true;
		for(int i=0; i<visitedPath.length; i++) {
			isMatch = isMatch & visitedPath[i];
		}
		
		if(!isMatch) {
			return null;
		}
		
		return param;
	}
	
	/**
	 * check if current method is a valid setter for array element
	 *
	 * @param opcodeWrite
	 * @return
	 */
	private static Integer checkValidArrayElementSetter(Method method, 
			int opcodeWrite, VariableInTest variable) {
		
		String className = method.getDeclaringClass().getCanonicalName();
		String methodName = method.getName() + MethodUtil.getSignature(method);
		
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getAllInstructionsAtMethod(className, methodName);
		if (insList == null /* && RuntimeInstrumentation.checkIfCanInstrument(className) */) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(className, methodName);
		}
		
		if (insList != null) {
			for (BytecodeInstruction ins : insList) {
				// *ASTORE
				if (ins.getASMNode().getOpcode() == opcodeWrite) {
					BytecodeInstruction param = isMatchPath(ins, variable);
					if(param != null) {
						return param.getParameterPosition();
					}
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * AASTORE, BASTORE, CASTORE, DASTORE, FASTORE, IASTORE, LASTORE, SASTORE
	 */
	private static List<VariableReference> searchArrayElementWritingReference(TestCase test, VariableInTest variable, int opcodeWrite) {
		
		List<VariableReference> variableList = new ArrayList<VariableReference>(); 
		/**
		 * check array[*] = ....;, and collect all array[*]
		 */
		if(variable.isDirentNodeAccess() && variable.callerObject instanceof ArrayReference) {
			for (int i = 0;i < test.size(); i ++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof AssignmentStatement) {
					AssignmentStatement assignStat = (AssignmentStatement) stat;
					for (VariableReference varRef : assignStat.getVariableReferences()) {
						if (varRef.equals(variable.callerObject)) {
							variableList.add(assignStat.getReturnValue());
						}
					}		
				} 
			}
		}
		/**
		 * check obj.m() which assign the array element
		 */
		else {
			for (int i = 0;i < test.size(); i ++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof MethodStatement) {
					MethodStatement mStat = (MethodStatement) stat;
					VariableReference ref = mStat.getCallee();
					if (ref != null && ref.equals(variable.callerObject)) {
						Integer parameterPosition = checkValidArrayElementSetter(mStat.getMethod().getMethod(), opcodeWrite, variable); // Write this method?
						if(parameterPosition != -1) {
							VariableReference param = mStat.getParameterReferences().get(parameterPosition);
							variableList.add(param);
						}
						
					}	
				} 
			}
		}
		
		System.currentTimeMillis();
		
		return variableList;
	}
	
	@SuppressWarnings("rawtypes")
	private static PrimitiveStatement generatePrimitiveStatementFor(TestCase testCase, ArrayReference arrayRef) {
		Class<?> arrayClass = arrayRef.getComponentClass();
		// TODO: For now, only support numeric/string primitives
		if (arrayClass.equals(boolean.class)) {
			return new BooleanPrimitiveStatement(testCase, false);
		} else if (arrayClass.equals(byte.class)) {
			return new BytePrimitiveStatement(testCase, (byte) 0);
		} else if (arrayClass.equals(double.class)) {
			return new CharPrimitiveStatement(testCase, (char) 0);
		} else if (arrayClass.equals(float.class)) {
			return new FloatPrimitiveStatement(testCase, 0.0f);
		} else if (arrayClass.equals(int.class)) {
			return new IntPrimitiveStatement(testCase, 0);
		} else if (arrayClass.equals(long.class)) {
			return new LongPrimitiveStatement(testCase, 0L);
		} else if (arrayClass.equals(short.class)) {
			return new ShortPrimitiveStatement(testCase, (short) 0);
		} else {
			return null;
		}
	}
	
	private static VariableReference createArrayElementVariable(TestCase test, ArrayReference arrayRef) {
		Class<?> clazz = arrayRef.getComponentClass();
		if (clazz.getConstructors().length < 1) {
			if (clazz.isPrimitive()) {
				try {
					@SuppressWarnings("rawtypes")
					PrimitiveStatement primitiveStatement = generatePrimitiveStatementFor(test, arrayRef);
					VariableReference returnedVar = TestFactory.getInstance().addPrimitive(test, primitiveStatement, arrayRef.getStPosition() + 1);
					return returnedVar;
				} catch (ConstructionFailedException e) { 
					e.printStackTrace();
				}
			} else {
				return null;
			}
		}
		
		Constructor<?> constructor = clazz.getConstructors()[0];
		GenericConstructor gConstructor = new GenericConstructor(constructor,
				constructor.getDeclaringClass());
		try {
			VariableReference returnedVar = TestFactory.getInstance().addConstructor(test, gConstructor,
					arrayRef.getStPosition() + 1, 2);
			return returnedVar;
			
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}
