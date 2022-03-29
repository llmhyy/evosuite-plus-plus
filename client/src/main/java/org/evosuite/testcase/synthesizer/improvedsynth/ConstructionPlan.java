package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * Given the root node, and the leaf node, we calculate the plan of contrusting test case.
 * @author llmhy
 *
 */
public class ConstructionPlan {
	private DepVariableWrapper root;
	private DepVariableWrapper leaf;
	
	private List<Operation> operations;
	private List<DepVariableWrapper> constructionPath;
	
	public ConstructionPlan(DepVariableWrapper root, DepVariableWrapper leaf, 
			List<Operation> operations, List<DepVariableWrapper> constructionPath) {
		if (root == null || leaf == null) {
			throw new IllegalArgumentException("Nodes cannot be null!");
		}
		
		this.root = root;
		this.leaf = leaf;
		
		this.operations = operations;
		this.constructionPath = constructionPath;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ConstructionPlan)) {
			return false;
		}
		
		ConstructionPlan other = (ConstructionPlan) o;
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

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

	public List<DepVariableWrapper> getConstructionPath() {
		return constructionPath;
	}

	public void setConstructionPath(List<DepVariableWrapper> constructionPath) {
		this.constructionPath = constructionPath;
	}
}
