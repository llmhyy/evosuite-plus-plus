package org.evosuite.seeding.smart;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.ValueStatement;
import org.evosuite.utils.Randomness;

/**
 * 
 * A data structure to capture the method inputs of a method
 * 
 * @author Yun Lin
 *
 */
public class MethodInputs {
	
	private Map<String, ValueStatement> inputVariables;
	private Map<String, Object> inputConstants;

	public MethodInputs(Map<String, ValueStatement> inputVariables, Map<String, Object> inputConstants) {
		super();
		this.inputVariables = inputVariables;
		this.inputConstants = inputConstants;
	}

	public Map<String, ValueStatement> getInputVariables() {
		return inputVariables;
	}

	public void setInputVariables(Map<String, ValueStatement> inputVariables) {
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
			ValueStatement statement = inputVariables.get(key);
			if(statement instanceof AssignmentStatement) {
				AssignmentStatement aStat = (AssignmentStatement)statement;
				Type t = aStat.getValue().getType();
				if(t.equals(Integer.class)) {
					Object obj = Randomness.nextInt();
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(String.class)) {
					Object obj = Randomness.nextString(10);
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(Short.class)) {
					Object obj = Randomness.nextShort();
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(Long.class)) {
					Object obj = Randomness.nextLong();
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(Character.class)) {
					Object obj = Randomness.nextChar();
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(Byte.class)) {
					Object obj = Randomness.nextByte();
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(Double.class)) {
					Object obj = Randomness.nextDouble();
					aStat.setAssignmentValue(obj);
				}
				else if(t.equals(Float.class)) {
					Object obj = Randomness.nextFloat();
					aStat.setAssignmentValue(obj);
				}
			}
			else {
				statement.mutate(statement.getTestCase(), TestFactory.getInstance());				
			}
		}
		System.currentTimeMillis();
	}

	public MethodInputs identifyInputs(TestChromosome newTestChromosome) {
		Map<String, ValueStatement> inputVariables = new HashMap<>();
		for(String key: this.inputVariables.keySet()) {
			ValueStatement vStatement = this.inputVariables.get(key);
			Statement s = newTestChromosome.getTestCase().getStatement(vStatement.getPosition());
			if(s instanceof ValueStatement) {
				inputVariables.put(key, (ValueStatement)s);
			}
		}
		
		return new MethodInputs(inputVariables, inputConstants);
	}

}
