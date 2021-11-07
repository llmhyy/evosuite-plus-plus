package org.evosuite.testcase.synthesizer.graphviz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.Statement;

public class SimpleStatement implements Serializable {
	private static final long serialVersionUID = -8057364066960091047L;
	
	public int statementNumber;
	
	public Map<Integer, String> statementNumberToTestCase = new HashMap<>();
	
	public String wholeTestCase;
	
	// Required for deserialisation
	public SimpleStatement() {
	}
	
	public SimpleStatement(int statementNumber, TestCase testCase) {
		this.statementNumber = statementNumber;
		for (int i = 0; i < testCase.size(); i++) {
			Statement statement = testCase.getStatement(i);
			int key = i;
			String value = statement.getCode();
			statementNumberToTestCase.put(key, value);
		}
		wholeTestCase = testCase.toCode();
	}
}
