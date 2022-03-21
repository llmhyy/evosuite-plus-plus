package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

/**
 * Represents a path from a given node to another given node.
 * Wraps a list of operations (either method calls or field accesses) to access the field represented
 * by the given node, as well as a path in the OCG (a list of OCG nodes).
 */
public class NodeAccessPath {
	private final List<Operation> operationsList;
	private final List<DepVariableWrapper> nodesList;
	
	public NodeAccessPath(List<Operation> operationsList, List<DepVariableWrapper> nodesList) {
		this.operationsList = new ArrayList<>(operationsList);
		this.nodesList = new ArrayList<>(nodesList);
	}
	
	public List<Operation> getOperationsList() {
		return operationsList;
	}
	
	public List<DepVariableWrapper> getNodesList() {
		return nodesList;
	}
}
