package org.evosuite.testcase.synthesizer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;

public class ImprovedConstructionPathSynthesizer extends ConstructionPathSynthesizer {
	AccessibilityMatrixManager accessibilityMatrixManager;
	
	public ImprovedConstructionPathSynthesizer(boolean isDebug) {
		super(isDebug);
	}

	@Override
	public void buildNodeStatementCorrespondence(TestCase test, Branch b, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {
		PartialGraph partialGraph = constructPartialComputationGraph(b);
		// To avoid having to make a new Pair class, just re-use the Map.Entry class
		// Access pairs here denotes a pair of nodes, one root and one leaf, such that we can
		// access (set) the leaf node starting from the root node.
		List<Map.Entry<DepVariableWrapper, DepVariableWrapper>> accessPairs = new ArrayList<>();
		AccessibilityMatrixManager accessibilityMatrixManager = new AccessibilityMatrixManager();
		Map<DepVariableWrapper, VarRelevance> graph2CodeMap = new HashMap<>();
		accessibilityMatrixManager.initialise(partialGraph);
		
		List<DepVariableWrapper> rootNodes = partialGraph.getTopLayer();
		List<DepVariableWrapper> leafNodes = partialGraph.getLeaves();
		
		for (DepVariableWrapper leafNode : leafNodes) {
			// Tracks whether we have found a (root, leaf) pair that allows us to set the leaf node
			// by traversing from the root node.
			boolean isCurrentLeafNodePathValidated = false;
			for (DepVariableWrapper rootNode : rootNodes) {
				if (isCurrentLeafNodePathValidated) {
					break;
				}
				
				for (int i = 1; i < partialGraph.getGraphSize(); i++) {
					boolean isPathExists = accessibilityMatrixManager.isPathOfLengthExistsBetween(rootNode, leafNode, i);
					if (isPathExists) {
						// AbstractMap.SimpleEntry is one such class implementing the Map.Entry interface
						accessPairs.add(new AbstractMap.SimpleEntry<DepVariableWrapper, DepVariableWrapper>(rootNode, leafNode));
						isCurrentLeafNodePathValidated = true;
						break;
					}
				}
			}
		}
		
		// Set up test case statement generator
		TestCaseStatementGenerator testCaseStatementGenerator = new TestCaseStatementGeneratorBuilder()
				.setGraph2CodeMap(graph2CodeMap)
				.setAccessibilityMatrixManager(accessibilityMatrixManager)
				.setTestCase(test)
				.setBranch(b)
				.build();
			
		for (Map.Entry<DepVariableWrapper, DepVariableWrapper> accessPair : accessPairs) {
			DepVariableWrapper rootNode = accessPair.getKey();
			DepVariableWrapper leafNode = accessPair.getValue();
			testCaseStatementGenerator.generateStatementForLeafStartingFromRoot(rootNode, leafNode);
		}
		
		setPartialGraph(partialGraph);
		setGraph2CodeMap(graph2CodeMap);
	}
}
