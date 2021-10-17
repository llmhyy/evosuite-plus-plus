package org.evosuite.testcase.synthesizer;

import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.variable.VariableReference;

class InstructionStatementMatch{
	public boolean isMatch;
	
	public DepVariableWrapper node;
	public List<VariableReference> variables;
	
	public InstructionStatementMatch(boolean isSet, DepVariableWrapper node, List<VariableReference> variables) {
		super();
		
		this.isMatch = isSet;
		
		this.node = node;
		this.variables = variables;
	}
	
}
