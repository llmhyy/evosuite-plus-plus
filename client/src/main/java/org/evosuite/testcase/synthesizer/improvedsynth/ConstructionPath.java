package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * Represents a path from a given node to another given node.
 * Wraps a list of operations (either method calls or field accesses) to access the field represented
 * by the given node, as well as a path in the OCG (a list of OCG nodes).
 */
public class ConstructionPath {
	private final List<Operation> operations;
	private final List<DepVariableWrapper> path;
	
	public ConstructionPath(List<Operation> operations, List<DepVariableWrapper> path) {
		if (operations == null || path == null) {
			throw new IllegalArgumentException("Neither operations nor path can be null!");
		}
		
		if (path.size() < 2) {
			throw new IllegalArgumentException("Path must be at least of length 2!");
		}
		
		if (operations.size() < 1) {
			throw new IllegalArgumentException("Must have at least one operation!");
		}
		
		this.operations = new ArrayList<>(operations);
		this.path = new ArrayList<>(path);
	}
	
	public List<Operation> getOperations() {
		return new ArrayList<>(operations);
	}
	
	public List<DepVariableWrapper> getPath() {
		return new ArrayList<>(path);
	}
	
	public DepVariableWrapper getRoot() {
		return path.get(0);
	}
	
	public DepVariableWrapper getLeaf() {
		return path.get(path.size() - 1);
	}
}
