package org.evosuite.testcase.synthesizer;

import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;

public class TestCaseLegitimizer {

	public static void legitimize(TestCase test) {
		ExecutionResult result = TestCaseExecutor.getInstance().execute(test);
		
		System.currentTimeMillis();
		
	}
	
}
