package org.evosuite.testcase.synthesizer.improvedsynth;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * Given the root node, and the leaf node, we calculate the plan of contrusting test case.
 * @author llmhy
 *
 */
public class NodePair {
	private DepVariableWrapper root;
	private DepVariableWrapper leaf;
	
	
	public NodePair(DepVariableWrapper root, DepVariableWrapper leaf) {
		if (root == null || leaf == null) {
			throw new IllegalArgumentException("Nodes cannot be null!");
		}
		
		this.root = root;
		this.leaf = leaf;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NodePair)) {
			return false;
		}
		
		NodePair other = (NodePair) o;
		return this.root.equals(other.root) && this.leaf.equals(other.leaf);
	}
	
	@Override
	public int hashCode() {
		return root.hashCode() + 31 * leaf.hashCode();
	}
	
	@Override
	public String toString() {
		return root.toString() + System.lineSeparator() + leaf.toString();
	}
	
	public DepVariableWrapper getRoot() {
		return root;
	}
	
	public DepVariableWrapper getLeaf() {
		return leaf;
	}
}
