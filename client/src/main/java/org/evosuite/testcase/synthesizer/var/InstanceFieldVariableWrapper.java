package org.evosuite.testcase.synthesizer.var;

import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.variable.VariableReference;

public class InstanceFieldVariableWrapper extends FieldVariableWrapper {

	protected InstanceFieldVariableWrapper(DepVariable var) {
		super(var);
	}

	@Override
	public List<VariableReference> generateOrFindStatement(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		if (callerObject == null) {
			return null;
		}
		
		return super.generateOrFindStatement(test, isLeaf, callerObject, map, b, allowNullValue);
	}
}
