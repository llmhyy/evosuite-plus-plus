package org.evosuite.testcase.synthesizer.improvedsynth;

import org.ejml.simple.SimpleMatrix;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.var.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Manager for an internal accessibility matrix.
 * Used in the improved OCG traversal algorithm.
 */
public class AccessibilityMatrixManager {
	// The size of the square matrix.
	// Also equivalent to the number of nodes in the partial graph.
	private int size;
	
	// The backing matrix, uses EJML.
	private SimpleMatrix internalMatrix;
	
	// Internal mappings between OCG nodes and matrix row/column indices.
	private Map<DepVariableWrapper, Integer> nodeToIndex = new HashMap<>();
	private Map<Integer, DepVariableWrapper> indexToNode = new HashMap<>();
	
	// Map of a (from, to) node pair to the path found.
	// Access should check first if there exists a path between the nodes.
	// In our case, since we only consider (direct) adjacency, our access paths
	// should always have node list of length 2 and a single operation (operations list of length 1).
	private Map<NodePair, ConstructionPath> nodesToPath = new HashMap<>();
	
	// Cache previously computed powers of the internalMatrix.
	private Map<Integer, SimpleMatrix> powerCache = new HashMap<>();
	
	// Cache previously computed descendants
	private Map<DepVariableWrapper, List<DepVariableWrapper>> nodeToDescendants = new HashMap<>();
	
	// Cache previously computed neighbours
	private Map<DepVariableWrapper, List<DepVariableWrapper>> nodeToNeighbours = new HashMap<>();
	
	// Whether initialisation has been completed.
	private boolean isInitialised = false;
	
	public void buildNodeIndexMappings(PartialGraph partialGraph) {
		// SimpleMatrices are 0-indexed.
		int currentNodeIndex = 0;		
		Stack<DepVariableWrapper> stack = new Stack<DepVariableWrapper>();
		for (DepVariableWrapper rootNode : partialGraph.getTopLayer()) {
			stack.push(rootNode);
		}
		
		while(!stack.isEmpty()) {
			DepVariableWrapper node = stack.pop();
			
			boolean isAlreadyIndexed = nodeToIndex.containsKey(node);
			if (isAlreadyIndexed) {
				continue;
			}
			
			_setIndexNodeMapping(node, currentNodeIndex);
			currentNodeIndex++;
			
			if(node.children != null && !node.children.isEmpty()) {
				stack.addAll(node.children);					
			}
		}
	}
	
	/**
	 * Initialises the accessibility matrix with a given partial graph.
	 * The method will
	 * 1) Set mappings between nodes and indices
	 * 2) Compute the accessibility between adjacent nodes
	 * @param partialGraph The {@code PartialGraph} to use for initialisation.
	 */
	public void initialise(PartialGraph partialGraph) {
		if (isInitialised) {
			return;
		}		
		
		this.size = partialGraph.getNodes().size();
		this.internalMatrix = new SimpleMatrix(this.size, this.size);
		
		buildNodeIndexMappings(partialGraph);
		
		// Set all diagonal entries to 1
		for (int i = 0; i < size; i++) {
			_unsafeSet(i, i, true);
		}

		/* // paired-design

		DepVariableWrapper arr_elem = null;
		DepVariableWrapper thisContainsList = null;

		for (DepVariableWrapper node: map.keySet()) {
			if (node instanceof ArrayElementVariableWrapper)
				arr_elem = node;
		}

		for (DepVariableWrapper topLayerNode : partialGraph.getTopLayer()) {
			thisContainsList = topLayerNode;
		}

		generateAccessibilityMatrixEntriesForPair(thisContainsList, arr_elem);
		*/

		//---------------- debugging specifically

		/*
		// change of structure
		LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> map = buildMap(partialGraph);
		// it contains every top-descendant pair
		// key is top node. values are stack of lower nodes
		// iterate all layers.

		LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> copiedMap = new LinkedHashMap<>();

		for (Map.Entry<DepVariableWrapper, Stack<DepVariableWrapper>> entry : map.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}

		for (DepVariableWrapper node: copiedMap.keySet()) {
			while (!copiedMap.get(node).isEmpty()) {
				generateAccessibilityMatrixEntriesForPair(node, map.get(node).pop());
			}
		}*/



		//comment out : previous approach
		// Generate the accessibility matrix entries for each node
		Queue<DepVariableWrapper> queue = new ArrayDeque<>();
		for (DepVariableWrapper topLayerNode : partialGraph.getTopLayer()) {
			queue.offer(topLayerNode);
		}
		while (!queue.isEmpty()) {
			DepVariableWrapper node = queue.poll();
			generateAccessibilityMatrixEntriesFor(node);
			for (DepVariableWrapper childNode : node.children) {
				queue.offer(childNode);
			}
		}
		
		powerCache.put(1, internalMatrix);
		
		isInitialised = true;
	}

	private void generateAccessibilityMatrixEntriesForPair(DepVariableWrapper parentNode, DepVariableWrapper childNode) {
		ConstructionPath nodeAccessPath = findPathBetween(parentNode, childNode);
		if (nodeAccessPath != null) {
			_unsafeSet(_getIndexFor(parentNode), _getIndexFor(childNode), true);
			nodesToPath.put(new NodePair(parentNode, childNode), nodeAccessPath);
		}
	}

	private LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> initialiseMap(PartialGraph partialGraph) {
		LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> map = new LinkedHashMap<>();
		Queue<DepVariableWrapper> queue = new ArrayDeque<>();
		for (DepVariableWrapper topLayerNode : partialGraph.getTopLayer()) {
			queue.offer(topLayerNode);
		}

		while (!queue.isEmpty()) {
			DepVariableWrapper node = queue.poll();
			map.put(node, new Stack<DepVariableWrapper>());
			for (DepVariableWrapper childNode : node.children) {
				queue.offer(childNode);
			}
		}
		return map;
	}

	private LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> buildMap(PartialGraph partialGraph) {
		LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> map = initialiseMap(partialGraph);
		for (DepVariableWrapper topLayerNode : partialGraph.getTopLayer()) {
			iterateNode(topLayerNode, map);
		}

		return map;
	}

	private void iterateNode(DepVariableWrapper node, LinkedHashMap<DepVariableWrapper, Stack<DepVariableWrapper>> map) {
		for (DepVariableWrapper childNode : node.children) {
			Stack<DepVariableWrapper> temp = map.getOrDefault(node, new Stack<>());
			temp.push(childNode);
			map.put(node, temp);
		}
		for (DepVariableWrapper childNode : node.children) {
			iterateNode(childNode, map);
		}

		for (DepVariableWrapper childNode : node.children) {
			Stack<DepVariableWrapper> s1 = map.getOrDefault(node, new Stack<>());
			Stack<DepVariableWrapper> s2 = map.getOrDefault(childNode, new Stack<>());
			addStack(s1, s2);
		}
	}

	private void addStack(Stack<DepVariableWrapper> s1, Stack<DepVariableWrapper> s2) {
		// add s2 to s1

		Stack<DepVariableWrapper> copiedS2 = new Stack<>();
		copiedS2.addAll(s2);

		Stack<DepVariableWrapper> temp = new Stack<>();
		while (!copiedS2.empty()) {
			temp.push(copiedS2.pop());
		}

		while(!temp.empty()) {
			s1.push(temp.pop());
		}
	}

	private void generateAccessibilityMatrixEntriesFor(DepVariableWrapper node) {
		// We don't bother looking for paths that go upstream
		// Only attempt to find paths between node to descendants.
		List<DepVariableWrapper> descendants = getAllDescendantsOf(node);
		for (DepVariableWrapper descendant : descendants) {
			// for debugging only
			/* if (descendant instanceof ArrayElementVariableWrapper) {
				ConstructionPath nodeAccessPath = findPathBetween(node, descendant);
			} else { continue; }*/

			ConstructionPath nodeAccessPath = findPathBetween(node, descendant);
			if (nodeAccessPath != null) {
				_unsafeSet(_getIndexFor(node), _getIndexFor(descendant), true);
				nodesToPath.put(new NodePair(node, descendant), nodeAccessPath);
			}
		}
	}
	
	private ConstructionPath validateDirectFieldSet(DepVariableWrapper fromNode, DepVariableWrapper toNode) {	
		boolean isToNodeLeaf = toNode.children.isEmpty();
		if (!isToNodeLeaf) {
			return null;
		}
		
		boolean isDirectChild = fromNode.children.contains(toNode);
		if (!isDirectChild) {
			return null;
		}
		
		Field toNodeField = null;
		try {
			toNodeField = DepVariableWrapperUtil.extractFieldFrom(fromNode, toNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (toNodeField == null) {
			return null;
		}
		
		boolean isFieldPublic = Modifier.isPublic(toNodeField.getModifiers());
		boolean isFieldFinal = Modifier.isFinal(toNodeField.getModifiers());
		boolean isCanSetField = isFieldPublic && !isFieldFinal;
		if (!isCanSetField) {
			return null;
		}
		
		return new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode)
				.addToOperations(new FieldAccess(toNodeField))
				.build();
	}

	private ConstructionPath validateArrayMethodCallSet(DepVariableWrapper fromNode, DepVariableWrapper toNode, String varName) {
		ConstructionPathBuilder builder = new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode);

		boolean isPathFound = false;

		List<Method> candidateMethods = DepVariableWrapperUtil.extractNonNativeMethodsFrom(fromNode);

		for (Method candidateMethod : candidateMethods) {
			boolean isValidSetter = DepVariableWrapperUtil.testArrayFieldSetter(candidateMethod, varName);
			if (isValidSetter) {
				builder.addToOperations(new MethodCall(candidateMethod));
				isPathFound = true;
				break;
			}
		}
		/* // debug specifically
		Method add = candidateMethods.get(1);
		String x = toNode.getVariableName(); // useless. delete later
		if (!x.equals("value")) {
			boolean isValidSetter_0 = DepVariableWrapperUtil.testArrayFieldSetter(add, varName);
			if (isValidSetter_0) {
				builder.addToOperations(new MethodCall(add)); // TODO: add multiple method calls?
				isPathFound = true;
			}
		}*/

		if (isPathFound) {
			return builder.build();
		}

		return null;
	}

	private ConstructionPath validateMethodCallSet(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		ConstructionPathBuilder builder = new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode);
		
		boolean isPathFound = false;
		Class<?> toNodeClass = null;
		try {
			toNodeClass = DepVariableWrapperUtil.extractClassFrom(toNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (toNodeClass == null) {
			//return null; // comment out
		}

		// changed here @caiyi
//		String fromName = fromNode.getVariableName();
//		String toName = toNode.getVariableName();
//		ArrayList<String> relevantVarNames = new ArrayList<>(Arrays.asList(fromName, toName));
//		List<Method> candidateMethods = DepVariableWrapperUtil.extractMethodsRelating(fromNode, relevantVarNames);

		List<Method> candidateMethods = DepVariableWrapperUtil.extractMethodsAccepting(fromNode, toNodeClass);

		for (Method candidateMethod : candidateMethods) {
			boolean isValidSetter = DepVariableWrapperUtil.testFieldSetter(candidateMethod, toNode);
			if (isValidSetter) {
				builder.addToOperations(new MethodCall(candidateMethod));
				isPathFound = true;
				break;
			}
		}
		
		if (isPathFound) {
			return builder.build();
		}
		
		return null;
	}
	
	private ConstructionPath validateDirectFieldGet(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		ConstructionPathBuilder builder = new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode);
		
		// A successful path from fromNode to toNode would be an operation where
		// we can get the field enclosed in toNode directly from the caller object
		// corresponding to fromNode.
		// Check if a direct field access is viable.
		// Direct field gets are only possible if
		// 1) The toNode is a direct child of the fromNode
		// 2) The field is public
		boolean isDirectChild = fromNode.children != null && fromNode.children.contains(toNode);
		if (!isDirectChild) {
			return null;
		}
		
		Field toNodeField = null;
		try {
			toNodeField = DepVariableWrapperUtil.extractFieldFrom(fromNode, toNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (toNodeField == null) {
			return null;
		}
		
		boolean isFieldPublic = Modifier.isPublic(toNodeField.getModifiers());
		// We assume isDirectChild is true here, hence we omit it from the condition.
		boolean isCanGetField = isFieldPublic;
		if (!isCanGetField) {
			return null;
		}
		
		return builder.addToOperations(new FieldAccess(toNodeField))
				.build();
	}
	
	private ConstructionPath validateMethodCallGet(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		ConstructionPathBuilder builder = new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode);
		
		boolean isPathFound = false;
		Class<?> toNodeClass = null;
		try {
			toNodeClass = DepVariableWrapperUtil.extractClassFrom(toNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (toNodeClass == null) {
			return null;
		}

		List<Method> candidateMethods = DepVariableWrapperUtil.extractMethodsReturning(fromNode, toNodeClass);

		for (Method candidateMethod : candidateMethods) {
			boolean isValidGetter = DepVariableWrapperUtil.testFieldGetter(candidateMethod, toNode);
			if (isValidGetter) {
				builder.addToOperations(new MethodCall(candidateMethod));
				isPathFound = true;
				break;
			}
		}
		
		if (isPathFound) {
			return builder.build();
		}
		
		return null;
	}
	
	private ConstructionPath findPathBetween(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		boolean isArrayElement = (toNode instanceof ArrayElementVariableWrapper);
		boolean isField = (toNode instanceof FieldVariableWrapper);
		boolean isOther = (toNode instanceof OtherVariableWrapper);
		boolean isParameter = (toNode instanceof ParameterVariableWrapper);
		boolean isThis = (toNode instanceof ThisVariableWrapper);

		if (isArrayElement) {
			// old
			// return findDirectParentForArrayElement(fromNode, toNode);
			// new
			return findPathToArrayElement(fromNode, toNode);
		}
		
		if (isField) {
			return findPathToField(fromNode, toNode);
		}
		
		if (isOther) {
			return findPathToOther(fromNode, toNode);
		}
		
		if (isParameter) {
			return findPathToParameter(fromNode, toNode);
		}
		
		if (isThis) {
			return findPathToThis(fromNode, toNode);
		}
		
		return null;
	}

	private ConstructionPath findPathToArrayElement(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		ConstructionPath constructionPath = findDirectParentForArrayElement(fromNode, toNode);
		if (constructionPath != null) {
			return constructionPath;
		}

		// since toNode is array element, we check whether fromNode has a method whose body contains array name(class' direct variable)
		// so that we aim to later filter out related array setters by array name

		boolean isToNodeLeaf = toNode.children.isEmpty(); // this must be true

		// TODO
		// Edge case here: if the field is an array, it will not be a leaf node
		// even if we wish to set it. What to do here?
		if (isToNodeLeaf) {
			/*ConstructionPath firstAttempt = validateDirectFieldSet(fromNode, toNode); // setters do not exist
			if (firstAttempt != null) {
				return firstAttempt;
			}*/

			ArrayList<DepVariableWrapper> potentialNodes = new ArrayList<>();
			for (DepVariableWrapper directChild: fromNode.children) {
				if (getAllDescendantsOf(directChild).contains(toNode) && directChild instanceof FieldVariableWrapper)
					// && directChild.var.getType() == DepVariable.ARRAY_ELEMENT)
					potentialNodes.add(directChild);
			}

			if (!potentialNodes.isEmpty()) {
				for (DepVariableWrapper node : potentialNodes) {
					String variableName = node.getVariableName();
					ConstructionPath secondAttempt = validateArrayMethodCallSet(fromNode, toNode, variableName);
					if (secondAttempt != null) {
						return secondAttempt;
					}
				}
			}
			// Could just return secondAttempt, but want to make it explicit.
			return null;
		} else {
			ConstructionPath firstAttempt = validateDirectFieldGet(fromNode, toNode);
			if (firstAttempt != null) {
				return firstAttempt;
			}

			ConstructionPath secondAttempt = validateMethodCallGet(fromNode, toNode);
			if (secondAttempt != null) {
				return secondAttempt;
			}

			return null;
		}
	}

	private ConstructionPath findDirectParentForArrayElement(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// Highly complex topic
		// For now, fall back on the original behaviour of the previous algorithm
		// For reachability, we only mark as reachable if the fromNode is the direct parent of the toNode
		// i.e. it's the array

		boolean isDirectParent = (toNode.parents != null && toNode.parents.contains(fromNode));
		if (!isDirectParent) {
			return null;
		}
		if (!(toNode instanceof ArrayElementVariableWrapper)) {
			return null;
		}
		ArrayElementVariableWrapper toNode2 = (ArrayElementVariableWrapper) toNode;
		BytecodeInstruction indexInstruction = null;
		try {
			indexInstruction = toNode2.var.getInstruction().getPreviousInstruction();
		} catch (NullPointerException e) {
			return null;
		}
		
		return new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode)
				.addToOperations(new ArrayElementAccess(indexInstruction))
				.build();
	}
	
	// Handles the case when the toNode represents a field (is a FieldVariableWrapper)
	private ConstructionPath findPathToField(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		boolean isToNodeLeaf = toNode.children.isEmpty();
		// TODO 
		// Edge case here: if the field is an array, it will not be a leaf node
		// even if we wish to set it. What to do here?
		if (isToNodeLeaf) {
			ConstructionPath firstAttempt = validateDirectFieldSet(fromNode, toNode); // setters may not exist
			if (firstAttempt != null) {
				return firstAttempt;
			}
			
			ConstructionPath secondAttempt = validateMethodCallSet(fromNode, toNode); // take note !
			if (secondAttempt != null) {
				return secondAttempt;
			}
			
			// Could just return secondAttempt, but want to make it explicit.
			return null;
		} else {
			ConstructionPath firstAttempt = validateDirectFieldGet(fromNode, toNode);
			if (firstAttempt != null) {
				return firstAttempt;
			}
			
			ConstructionPath secondAttempt = validateMethodCallGet(fromNode, toNode);
			if (secondAttempt != null) {
				return secondAttempt;
			}
			
			return null;
		}
	}
	
	private ConstructionPath findPathToOther(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// Other here is either a method invocation or ALOAD/ALOAD_*/DUP/DUP2
		// If it's a method invocation, then find the method and return a path with that method?
		// If it's ALOAD/ALOAD_*/DUP/DUP2, then do nothing?
		if (!(toNode instanceof OtherVariableWrapper)) {
			return null;
		}
		
		try {
			ConstructionPathBuilder builder = new ConstructionPathBuilder()
					.addToPath(fromNode)
					.addToPath(toNode);
			boolean isMethodInvocation = (toNode.var.getInstruction().isMethodCall());
			if (isMethodInvocation) {
				Method invokedMethod = DepVariableWrapperUtil.getInvokedMethod(toNode.var.getInstruction());
				builder.addToOperations(new MethodCall(invokedMethod));
				return builder.build();
			}
			
			// No operation if it's ALOAD/ALOAD_*/DUP/DUP2?
			try {
				return builder.build();
			} catch (IllegalArgumentException e) {
				// Build failed
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	private int getParameterOrder(ParameterVariableWrapper node) {
		return node.var.getParamOrder();
	}
	
	private ConstructionPath findPathToParameter(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// We should always be able to access parameters from any node
		// NodeAccessPath should just contain a ParameterAccess containing the parameter order
		if (!(toNode instanceof ParameterVariableWrapper)) {
			return null;
		}
		
		return new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode)
				.addToOperations(new ParameterReference(getParameterOrder((ParameterVariableWrapper) toNode)))
				.build();
	}
	
	private ConstructionPath findPathToThis(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// We can always access 'this' from anywhere in the caller object.
		// NodeAccessPath should just contain a ThisReference
		return new ConstructionPathBuilder()
				.addToPath(fromNode)
				.addToPath(toNode)
				.addToOperations(new ThisReference())
				.build();
	}
	
	private void _initialisationCheck() {
		if (!isInitialised) {
			throw new IllegalStateException("Method was called before AccessbilityMatrixManager was initialised!");
		}
	}
	
	private void _nullParameterCheck(Object parameter) {
		if (parameter == null) {
			throw new IllegalArgumentException("A parameter was null when it should not have been!");
		}
	}
	
	private void _exclusiveBoundsCheck(int number, int lowerBound, int upperBound) {
		if (number > lowerBound && number < upperBound) {
			return;
		}
		throw new IllegalArgumentException("A parameter did not adhere to the given bounds (strictly between " + lowerBound + " and " + upperBound + ")!");
	}
	
	private void _unsafeSet(int rowIndex, int colIndex, boolean value) {
		internalMatrix.set(rowIndex, colIndex, value ? 1 : 0);
	}
	
	private boolean _unsafeGet(int rowIndex, int colIndex) {
		return internalMatrix.get(rowIndex, colIndex) == 1;
	}
	
	private boolean _unsafeGet(SimpleMatrix matrix, int rowIndex, int colIndex) {
		return matrix.get(rowIndex, colIndex) == 1;
	}
	
	private int _getIndexFor(DepVariableWrapper node) {		
		Integer nodeIndex = nodeToIndex.get(node);
		if (nodeIndex == null) {
			throw new IllegalArgumentException("Could not find an index for the given node.");
		}
		
		return nodeIndex;
	}
	
	private DepVariableWrapper _getNodeFor(int index) {
		// Note: no need to do additional bounds checks for index, since 
		// if the index is invalid node will be null.
		DepVariableWrapper node = indexToNode.get(index);
		if (node == null) {
			throw new IllegalArgumentException("Could not find a node for the given index.");
		}
		return node;
	}
	
	private void _setIndexNodeMapping(DepVariableWrapper node, int index) {
		if (nodeToIndex.containsKey(node)) {
			throw new IllegalArgumentException("Attempted to set a node that already has an index!");
		}
		
		if (index < 0 || index >= size) {
			throw new IllegalArgumentException("Attempted to set an invalid index!");
		}
		
		nodeToIndex.put(node, index);
		indexToNode.put(index, node);
	}
	
	/**
	 * Sets an entry into the accessbility matrix. This method has various sanity checks built in.
	 * @param fromNode The node we are pathing from (row entry)
	 * @param toNode The node we are pathing to (column entry)
	 * @param value Whether we can access the toNode from the fromNode.
	 */
	public void set(DepVariableWrapper fromNode, DepVariableWrapper toNode, boolean value) {
		_initialisationCheck();
		_nullParameterCheck(fromNode);
		_nullParameterCheck(toNode);
		
		Integer fromNodeIndex = _getIndexFor(fromNode);
		Integer toNodeIndex = _getIndexFor(toNode);
		
		_unsafeSet(fromNodeIndex, toNodeIndex, value);
	}
	
	/**
	 * Gets an entry from the accessbility matrix. This method has various sanity checks built in.
	 * @param fromNode The node we are pathing from (row entry)
	 * @param toNode The node we are pathing to (column entry)
	 * @return Whether we can access the toNode from the fromNode.
	 */
	public boolean get(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		_initialisationCheck();
		_nullParameterCheck(fromNode);
		_nullParameterCheck(toNode);
		
		Integer fromNodeIndex = _getIndexFor(fromNode);
		Integer toNodeIndex = _getIndexFor(toNode);
		
		return _unsafeGet(fromNodeIndex, toNodeIndex);
	}
	
	private List<DepVariableWrapper> getAllDescendantsOf(DepVariableWrapper node) {
		if (nodeToDescendants.containsKey(node)) {
			return nodeToDescendants.get(node);
		}
		
		List<DepVariableWrapper> descendants = new ArrayList<>();
		descendants.addAll(node.children);
		for (DepVariableWrapper directChild : node.children) {
			if (!directChild.children.isEmpty()) {
				descendants.addAll(getAllDescendantsOf(directChild));
			}
		}
		
		nodeToDescendants.put(node, descendants);
		return new ArrayList<>(descendants); // Defensive copy
	}
	
	private SimpleMatrix getPower(int exponent) {
		if (powerCache.containsKey(exponent)) {
			return powerCache.get(exponent);
		}
		
		SimpleMatrix matrixToExponent = getPower(exponent - 1).mult(internalMatrix);
		powerCache.put(exponent, matrixToExponent);
		return matrixToExponent;
	}
	
	/**
	 * Determines the length of the shortest path from the fromNode to the toNode.
	 * If no such path exists, return -1.
	 * 
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public int findShortestPathLength(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		if (fromNode == toNode) {
			return 0;
		}
		
		for (int i = 1; i <= size; i++) {
			SimpleMatrix temp = getPower(i);
			int from = _getIndexFor(fromNode);
			int to = _getIndexFor(toNode);
			boolean isPathOfLengthIExists = temp.get(from, to) > 0;
			//boolean isPathOfLengthIExists = getPower(i).get(_getIndexFor(fromNode), _getIndexFor(toNode)) > 0;
			if (isPathOfLengthIExists) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns a list of the neighbours of the given node.
	 * @param fromNode
	 * @return
	 */
	public List<DepVariableWrapper> getNeighboursOf(DepVariableWrapper fromNode) {
		if (nodeToNeighbours.containsKey(fromNode)) {
			return new ArrayList<>(nodeToNeighbours.get(fromNode));
		}
		
		List<DepVariableWrapper> neighbours = new ArrayList<>();
		for (DepVariableWrapper toNode : nodeToIndex.keySet()) {
			if (fromNode == toNode) {
				continue;
			}
			
			if (get(fromNode, toNode)) {
				neighbours.add(toNode);
			}
		}
		
		nodeToNeighbours.put(fromNode, neighbours);
		return neighbours;
	}
	
	public ConstructionPath getNodeAccessPath(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		return nodesToPath.get(new NodePair(fromNode, toNode));
	}
}
