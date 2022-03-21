package org.evosuite.testcase.synthesizer.matrix;

import java.util.List;

public class AccessEntry {
	private List<Operation> operations;

	public AccessEntry(List<Operation> operations) {
		super();
		this.operations = operations;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
	
	
}	
