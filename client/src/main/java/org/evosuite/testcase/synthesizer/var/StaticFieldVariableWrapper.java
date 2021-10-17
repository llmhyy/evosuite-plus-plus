package org.evosuite.testcase.synthesizer.var;

import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.VariableInTest;
import org.evosuite.testcase.variable.VariableReference;

public class StaticFieldVariableWrapper extends FieldVariableWrapper {

	protected StaticFieldVariableWrapper(DepVariable var) {
		super(var);
	}

	@Override
	public List<VariableReference> generateOrFindStatement(TestCase test, boolean isLeaf, VariableInTest variable,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		return super.generateOrFindStatement(test, isLeaf, variable, map, b, allowNullValue);
	}
}
