package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

public class NodeAccessPathBuilder {
	List<DepVariableWrapper> path = new ArrayList<>();
	List<Operation> operations = new ArrayList<>();
	
	public NodeAccessPathBuilder() {
		
	}
	
	public NodeAccessPathBuilder addToPath(DepVariableWrapper node) {
		path.add(node);
		return this;
	}
	
	public NodeAccessPathBuilder addToOperations(Operation operation) {
		operations.add(operation);
		return this;
	}
	
	public NodeAccessPath build() {
		return new NodeAccessPath(operations, path);
	}
}
