package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

public class ConstructionPathBuilder {
	List<DepVariableWrapper> path = new ArrayList<>();
	List<Operation> operations = new ArrayList<>();
	
	public ConstructionPathBuilder() {
		
	}
	
	public ConstructionPathBuilder addToPath(DepVariableWrapper node) {
		path.add(node);
		return this;
	}
	
	public ConstructionPathBuilder addToOperations(Operation operation) {
		operations.add(operation);
		return this;
	}
	
	public ConstructionPath build() {
		return new ConstructionPath(operations, path);
	}
}
