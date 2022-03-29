package org.evosuite.testcase.synthesizer.var;

import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.VariableInTest;
import org.evosuite.testcase.synthesizer.improvedsynth.Operation;

public class InstanceFieldVariableWrapper extends FieldVariableWrapper {

	protected InstanceFieldVariableWrapper(DepVariable var) {
		super(var);
	}

	@Override
	public VarRelevance generateOrFindStatement(TestCase test, boolean isLeaf, VariableInTest variable,
			Map<DepVariableWrapper, VarRelevance> map, Branch b, boolean allowNullValue, Operation recommendation) {
		if (variable == null || variable.callerObject == null) {
			return null;
		}
		
		return super.generateOrFindStatement(test, isLeaf, variable, map, b, allowNullValue, recommendation);
	}
}
