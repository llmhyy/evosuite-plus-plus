package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ejml.simple.SimpleMatrix;
import org.evosuite.testcase.synthesizer.matrix.AccessEntry;
import org.evosuite.testcase.synthesizer.matrix.AccessMatrix;
import org.evosuite.testcase.synthesizer.matrix.Operation;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * Manager for an internal accessbility matrix.
 * Used in the improved OCG traversal algorithm.
 *
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
	private Map<NodePair, List<DepVariableWrapper>> nodesToPath = new HashMap<>();
	
	// Cache previously computed powers of the internalMatrix.
	private Map<Integer, SimpleMatrix> powerCache = new HashMap<>();
	
	// Whether initialisation has been completed.
	private boolean isInitialised = false;
	
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
		
		// SimpleMatrices are 0-indexed.
		int currentNodeIndex = 0;
		List<DepVariableWrapper> partialGraphNodes = partialGraph.getNodes();
		this.size = partialGraphNodes.size();
		this.internalMatrix = new SimpleMatrix(this.size, this.size);
		
		for (DepVariableWrapper node : partialGraphNodes) {
			// Shouldn't be, but defensive check.
			boolean isAlreadyIndexed = nodeToIndex.containsKey(node);
			if (isAlreadyIndexed) {
				continue;
			}
			
			_setIndexNodeMapping(node, currentNodeIndex);
			currentNodeIndex++;
		}
		
		// Set all diagonal entries to 1
		for (int i = 0; i < size; i++) {
			_unsafeSet(i, i, true);
		}
		
		// Generate the accessibility matrix entries for each node
		AccessMatrix matrix = new AccessMatrix(size);
		for(DepVariableWrapper root: partialGraph.getTopLayer()) {
			buildMatrix(root, matrix);
		}
		
		for (DepVariableWrapper node : nodeToIndex.keySet()) {
			generateAccessibilityMatrixEntriesFor(node);
		}
		
		powerCache.put(1, internalMatrix);
		
		isInitialised = true;
	}
	
	private void buildMatrix(DepVariableWrapper parent, AccessMatrix matrix) {
		List<DepVariableWrapper> children = getAllChildren(parent);
		
		if(!children.isEmpty()) {
			for(DepVariableWrapper child: children) {
				List<DepVariableWrapper> path = searchPathFromParentToChild(parent, child);
				List<Operation> implementation = checkValidity(path, parent, child);
				
				if(!implementation.isEmpty()) {
					int i = nodeToIndex.get(parent);
					int j = nodeToIndex.get(child);
					matrix.set(i, j, new AccessEntry(implementation));
				}
			}
			
			for(DepVariableWrapper node: parent.children) {
				buildMatrix(node, matrix);
			}
		}
		
	}

	//TODO we actually can return mutiple sequence of operations
	private List<Operation> checkValidity(List<DepVariableWrapper> path, DepVariableWrapper parent,
			DepVariableWrapper child) {
		//TODO darien
		List<Operation> list = new ArrayList<>();
		if(!parent.var.isPrimitive()) {
			//find all the calls to cover the nodes on the path
//			Class<?> clazz = getClass(parent);
//			for(Method method: clazz.getMethods()) {
//				//see if method can cover some of the path, if yes, ...; otherwise ...
//			}
			
			//if the path has been covered, return the
		}
		
		return new ArrayList<>();
	}

	private List<DepVariableWrapper> searchPathFromParentToChild(DepVariableWrapper parent, DepVariableWrapper child) {
		// TODO Darien write a recurisve function to know who to access child from parent on the graph
		
		ArrayList<DepVariableWrapper> list = new ArrayList<DepVariableWrapper>();
		list.add(parent);
		list.add(child);
		return list;
	}

	private List<DepVariableWrapper> getAllChildren(DepVariableWrapper parent) {
		// TODO Darien
		return parent.children;
	}

	private void generateAccessibilityMatrixEntriesFor(DepVariableWrapper node) {
		// TODO
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
	
	/**
	 * Checks if a path of given length exists starting at the fromNode and ending at the toNode exists.
	 * @param fromNode The node to start pathing from.
	 * @param toNode The node to path to.
	 * @param length The length of the path.
	 * @return {@code true} if such a path exists, {@code false} otherwise.
	 */
	public boolean isPathOfLengthExistsBetween(DepVariableWrapper fromNode, DepVariableWrapper toNode, int length) {
		_initialisationCheck();
		_nullParameterCheck(fromNode);
		_nullParameterCheck(toNode);
		_exclusiveBoundsCheck(length, 0, size);
		
		// Check if the cache contains a pre-computed power matrix
		boolean isCachedResultExists = powerCache.containsKey(length);
		if (!isCachedResultExists) {
			// Compute the power matrices up to the given value
			SimpleMatrix previousMatrix = internalMatrix;
			// Start at 2 since we already have the 1st power of the matrix (i.e. the base matrix itself).
			for (int i = 2; i <= length; i++) {
				SimpleMatrix multipliedMatrix = previousMatrix.mult(internalMatrix);
				powerCache.put(i, multipliedMatrix);
				previousMatrix = multipliedMatrix;
			}
		}
		SimpleMatrix powerMatrix = powerCache.get(length);
		int fromNodeIndex = _getIndexFor(fromNode);
		int toNodeIndex = _getIndexFor(toNode);
		return _unsafeGet(powerMatrix, fromNodeIndex, toNodeIndex);
	}
	
	public boolean isPathExistsBetween(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		for (int i = 1; i < size; i++) {
			if (isPathOfLengthExistsBetween(fromNode, toNode, i)) {
				return true;
			}
		}
		return false;
	}
	
	public List<DepVariableWrapper> getPathBetween(DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		if (!isPathExistsBetween(fromNode, toNode)) {
			return new ArrayList<>();
		}
		
		return nodesToPath.get(new NodePair(fromNode, toNode));
	}
}
