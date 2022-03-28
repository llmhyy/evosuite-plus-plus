package org.evosuite.testcase.synthesizer.improvedsynth;

import java.lang.reflect.Method;

public class MethodCall extends Operation {
	private final Method method;

	public MethodCall(Method method) {
		this.method = method;
	}
	
	public Method getMethod() {
		return method;
	}
	
	@Override
	public String toString() {
		return "[MethodCall]: " + method.toString();
	}
}
