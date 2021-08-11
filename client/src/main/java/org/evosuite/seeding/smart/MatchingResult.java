package org.evosuite.seeding.smart;

import org.evosuite.testcase.statements.PrimitiveStatement;

/**
 * the matching result of checking the value preserving property of an input and an observation.
 * 
 * @author Yun Lin
 *
 */
@SuppressWarnings("rawtypes")
public class MatchingResult {
	private PrimitiveStatement matchedInputVariable;
	private Object matchedObservation;

	public MatchingResult(PrimitiveStatement matchedInputVariable, Object matchedObservation) {
		super();
		this.matchedInputVariable = matchedInputVariable;
		this.matchedObservation = matchedObservation;
	}

	public PrimitiveStatement getMatchedInputVariable() {
		return matchedInputVariable;
	}

	public void setMatchedInputVariable(PrimitiveStatement matchedInputVariable) {
		this.matchedInputVariable = matchedInputVariable;
	}

	public Object getMatchedObservation() {
		return matchedObservation;
	}

	public void setMatchedObservation(Object matchedObservation) {
		this.matchedObservation = matchedObservation;
	}

}
