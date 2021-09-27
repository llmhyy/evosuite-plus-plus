package org.evosuite.testcase.synthesizer.var;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.variable.VariableReference;

public class ThisVariableWrapper extends DepVariableWrapper {

	protected ThisVariableWrapper(DepVariable var) {
		super(var);
	}

	@Override
	public List<VariableReference> generateOrFindStatement(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		List<VariableReference> list = new ArrayList<>();
		VariableReference var = generateOrFind(test, isLeaf, callerObject, map, b, allowNullValue);
		if(var != null) {
			list.add(var);
		}
		
		return list;
	}
	
	public VariableReference generateOrFind(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		if(this.parents.isEmpty()) {
			MethodStatement mStat = test.findTargetMethodCallStatement();
			if(mStat != null) {
				VariableReference generatedVariable = mStat.getCallee();
				return generatedVariable;				
			}
		}
		else {
			for(DepVariableWrapper parentNode: this.parents) {
				if(map.get(parentNode) != null) {
					VariableReference generatedVariable = map.get(parentNode).get(0);
					return generatedVariable;
				}
			}
		}
		
		return null;
	}

}
