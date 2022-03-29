package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;

public class TestCaseStatementGenerator {
	private Map<DepVariableWrapper, VarRelevance> graph2CodeMap;
	private AccessibilityMatrixManager accessibilityMatrixManager;
	private TestCase testCase;
	private Branch branch;
	
	// Don't allow initialisation without parameters.
	@SuppressWarnings("unused")
	private TestCaseStatementGenerator() {
	}
	
	public TestCaseStatementGenerator(Map<DepVariableWrapper, VarRelevance> graph2CodeMap,
			AccessibilityMatrixManager accessibilityMatrixManager, TestCase testCase, Branch branch) {
		this.graph2CodeMap = graph2CodeMap;
		this.accessibilityMatrixManager = accessibilityMatrixManager;
		this.testCase = testCase;
		this.branch = branch;
	}
	
	
}
