package org.evosuite.seeding.smart;

import org.evosuite.testcase.statements.ValueStatement;

/**
 * the matching result of checking the value preserving property of an input and an observation.
 * 
 * @author Yun Lin
 *
 */
public class MatchingResult {
	private ValueStatement matchedInputVariable;
	private Object matchedObservation;
	private Object matchedObservationKey;
	public boolean needRelaxedMutation = false;

	public MatchingResult(ValueStatement matchedInputVariable, Object matchedObservation, Object matchedObservationKey) {
		super();
		this.matchedInputVariable = matchedInputVariable;
		this.matchedObservation = matchedObservation;
		this.setMatchedObservationKey(matchedObservationKey);
	}

	public ValueStatement getMatchedInputVariable() {
		return matchedInputVariable;
	}

	public void setMatchedInputVariable(ValueStatement matchedInputVariable) {
		this.matchedInputVariable = matchedInputVariable;
	}

	public Object getMatchedObservation() {
		return matchedObservation;
	}

	public void setMatchedObservation(Object matchedObservation) {
		this.matchedObservation = matchedObservation;
	}

	public boolean needRelaxedMutation() {
		return needRelaxedMutation;
	}

	public void setNeedRelaxedMutation(boolean isCorrelation) {
		this.needRelaxedMutation = isCorrelation;
	}

	public Object getMatchedObservationKey() {
		return matchedObservationKey;
	}

	public void setMatchedObservationKey(Object matchedObservationKey) {
		this.matchedObservationKey = matchedObservationKey;
	}

}
