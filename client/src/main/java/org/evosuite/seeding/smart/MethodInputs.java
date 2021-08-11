package org.evosuite.seeding.smart;

import java.util.Map;

import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.PrimitiveStatement;

/**
 * 
 * A data structure to capture the method inputs of a method
 * 
 * @author Yun Lin
 *
 */
@SuppressWarnings("rawtypes")
public class MethodInputs {
	
	private Map<String, PrimitiveStatement> inputVariables;
	private Map<String, Object> inputConstants;

	public MethodInputs(Map<String, PrimitiveStatement> inputVariables, Map<String, Object> inputConstants) {
		super();
		this.inputVariables = inputVariables;
		this.inputConstants = inputConstants;
	}

	public Map<String, PrimitiveStatement> getInputVariables() {
		return inputVariables;
	}

	public void setInputVariables(Map<String, PrimitiveStatement> inputVariables) {
		this.inputVariables = inputVariables;
	}

	public Map<String, Object> getInputConstants() {
		return inputConstants;
	}

	public void setInputConstants(Map<String, Object> inputConstants) {
		this.inputConstants = inputConstants;
	}

	public void mutate() {
		for(String key: inputVariables.keySet()) {
			PrimitiveStatement statement = inputVariables.get(key);
			statement.mutate(statement.getTestCase(), TestFactory.getInstance());
		}
	}

}
