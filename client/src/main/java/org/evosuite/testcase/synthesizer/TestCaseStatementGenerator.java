package org.evosuite.testcase.synthesizer;

import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;

public class TestCaseStatementGenerator {
	private Map<DepVariableWrapper, VarRelevance> graph2CodeMap;
	private AccessibilityMatrixManager accessibilityMatrixManager;
	private TestCase testCase;
	private Branch branch;
	
	public TestCaseStatementGenerator(Map<DepVariableWrapper, VarRelevance> graph2CodeMap,
			AccessibilityMatrixManager accessibilityMatrixManager, TestCase testCase, Branch branch) {
		this.graph2CodeMap = graph2CodeMap;
		this.accessibilityMatrixManager = accessibilityMatrixManager;
		this.testCase = testCase;
		this.branch = branch;
	}
	
	public void generateStatementForLeafStartingFromRoot(DepVariableWrapper rootNode, DepVariableWrapper leafNode) {
		
	}
}
