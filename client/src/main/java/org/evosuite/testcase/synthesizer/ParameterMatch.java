package org.evosuite.testcase.synthesizer;

import java.util.Set;

public class ParameterMatch {
	public boolean isMatch;
	public Set<Integer> parameterPoisitions;

	public ParameterMatch(boolean isMatch, Set<Integer> parameterPoisitions) {
		super();
		this.isMatch = isMatch;
		this.parameterPoisitions = parameterPoisitions;
	}

}
