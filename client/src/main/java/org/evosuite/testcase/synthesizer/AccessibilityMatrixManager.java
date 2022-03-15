package org.evosuite.testcase.synthesizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ejml.simple.SimpleMatrix;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * Manager for an internal accessbility matrix.
 * Used in the improved OCG traversal algorithm.
 *
 */
public class AccessibilityMatrixManager {
	private int size;
	private SimpleMatrix internalMatrix;
	private Map<DepVariableWrapper, Integer> nodeToIndex;
	private Map<Integer, DepVariableWrapper> indexToNode;
	private boolean isInitialised = false;
	
	/**
	 * Constructor for AccessibilityMatrixManager.
	 * @param size The number of nodes to model, equal to the size of the (square) accessibility matrix.
	 */
	public AccessibilityMatrixManager() {
		nodeToIndex = new HashMap<>();
		indexToNode = new HashMap<>();
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
		for (DepVariableWrapper node : nodeToIndex.keySet()) {
			generateAccessibilityMatrixEntriesFor(node);
		}
	}
	
	private void generateAccessibilityMatrixEntriesFor(DepVariableWrapper node) {
		// TODO
	}
	
	private void _unsafeSet(int rowIndex, int colIndex, boolean value) {
		internalMatrix.set(rowIndex, colIndex, value ? 1 : 0);
	}
	
	private boolean _unsafeGet(int rowIndex, int colIndex) {
		return internalMatrix.get(rowIndex, colIndex) == 1;
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
		if (!isInitialised) {
			throw new IllegalStateException("Can't set before AccessbilityMatrixManager is initialised!");
		}
		
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
		if (!isInitialised) {
			throw new IllegalStateException("Can't set before AccessbilityMatrixManager is initialised!");
		}
		
		Integer fromNodeIndex = _getIndexFor(fromNode);
		Integer toNodeIndex = _getIndexFor(toNode);
		
		return _unsafeGet(fromNodeIndex, toNodeIndex);
	}
}
