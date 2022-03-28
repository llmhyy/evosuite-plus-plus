package org.evosuite.testcase.synthesizer.improvedsynth;

public class ParameterReference extends Operation {
	private final int parameterOrder;
	
	public ParameterReference(int parameterOrder) {
		this.parameterOrder = parameterOrder;
	}
	
	public int getParameterOrder() {
		return parameterOrder;
	}
	
	@Override
	public String toString() {
		return "[ParameterReference]: " + parameterOrder;
	}
}
