package org.evosuite.testcase.synthesizer.var;

import org.evosuite.graphs.interprocedural.var.ArrayElementVariable;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.graphs.interprocedural.var.InstanceFieldVariable;
import org.evosuite.graphs.interprocedural.var.ParameterVariable;
import org.evosuite.graphs.interprocedural.var.StaticFieldVariable;
import org.evosuite.graphs.interprocedural.var.ThisVariable;

public class DepVariableWrapperFactory {
	public static DepVariableWrapper createWrapperInstance(DepVariable var) {
		if(var instanceof ArrayElementVariable) {
			return new ArrayElementVariableWrapper(var);
		}
		else if(var instanceof InstanceFieldVariable) {
			return new InstanceFieldVariableWrapper(var);
		}
		else if(var instanceof StaticFieldVariable) {
			return new StaticFieldVariableWrapper(var);
		}
		else if(var instanceof ParameterVariable) {
			return new ParameterVariableWrapper(var);
		}
		else if(var instanceof ThisVariable) {
			return new ThisVariableWrapper(var);
		}
		else {
			return new OtherVariableWrapper(var);
		}
	}
}
