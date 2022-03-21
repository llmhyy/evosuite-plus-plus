package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * DepVariableWrapper wrapper for BFS.
 * Also contains the current path for backtracking.
 */
public class BfsNodeWrapper {
	private DepVariableWrapper node;
	private List<DepVariableWrapper> currentPath;
	
	public BfsNodeWrapper(DepVariableWrapper node, List<DepVariableWrapper> path) {
		this.node = node;
		currentPath = new ArrayList<>(path);
	}
	
	public DepVariableWrapper getNode() {
		return node;
	}
	
	public List<DepVariableWrapper> getPath() {
		return new ArrayList<>(currentPath);
	}
	
}
