package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;

public class TestCaseStatementGeneratorBuilder {
	private Map<DepVariableWrapper, VarRelevance> graph2CodeMap;
	private AccessibilityMatrixManager accessibilityMatrixManager;
	private TestCase testCase;
	private Branch branch;
	
	public TestCaseStatementGeneratorBuilder setGraph2CodeMap(Map<DepVariableWrapper, VarRelevance> map) {
		graph2CodeMap = map;
		return this;
	}
	
	public TestCaseStatementGeneratorBuilder setAccessibilityMatrixManager(AccessibilityMatrixManager manager) {
		accessibilityMatrixManager = manager;
		return this;
	}
	
	public TestCaseStatementGeneratorBuilder setTestCase(TestCase testCase) {
		this.testCase = testCase;
		return this;
	}
	
	public TestCaseStatementGeneratorBuilder setBranch(Branch branch) {
		this.branch = branch;
		return this;
	}
	
	public TestCaseStatementGenerator build() {
		if (graph2CodeMap == null) {
			throw new IllegalStateException("Graph2CodeMap is not set!");
		}
		
		if (accessibilityMatrixManager == null) {
			throw new IllegalStateException("AccessibilityMatrixManager is not set!");
		}
		
		if (testCase == null) {
			throw new IllegalStateException("TestCase is not set!");
		}
		
		if (branch == null) {
			throw new IllegalStateException("Branch is not set!");
		}
		
		return new TestCaseStatementGenerator(graph2CodeMap, accessibilityMatrixManager, testCase, branch);
	}
}
