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
	
	public void generateStatementForLeafStartingFromRoot(DepVariableWrapper rootNode, DepVariableWrapper leafNode) {
		List<DepVariableWrapper> path = getPathBetween(rootNode, leafNode);
		if (path == null || path.size() < 2) {
			return; 
		}
		List<Operation> operations = generateOperations(path);		
		// Now we have a list of operations, we just need a starting object to begin the access chain
		
		
	}
	
	private List<Operation> generateOperations(List<DepVariableWrapper> path) {		
		List<Operation> operations = new ArrayList<>();
		DepVariableWrapper currentNode = path.get(0);
		DepVariableWrapper nextNode;
		for (int i = 1; i < path.size(); i++) {
			nextNode = path.get(i);
			NodeAccessPath accessPath = accessibilityMatrixManager.getNodeAccessPath(currentNode, nextNode);
			if (accessPath == null) {
				return new ArrayList<>(); // Access chain is broken, failure
			}
			operations.addAll(accessPath.getOperationsList());
			currentNode = nextNode;
		}
		return operations;
	}
	
	private List<DepVariableWrapper> getPathBetween(DepVariableWrapper sourceNode, DepVariableWrapper endNode) {
		// Enhanced BFS to track the path taken
		// See https://stackoverflow.com/questions/8922060/how-to-trace-the-path-in-a-breadth-first-search/50575971#50575971
		Set<DepVariableWrapper> visitedNodes = new HashSet<>();
		List<DepVariableWrapper> path = null;
		
		Queue<BfsNodeWrapper> queue = new ArrayDeque<>();
		List<DepVariableWrapper> initialPath = new ArrayList<>();
		initialPath.add(sourceNode);
		queue.offer(new BfsNodeWrapper(sourceNode, initialPath));
		visitedNodes.add(sourceNode);
		while (!queue.isEmpty()) {
			BfsNodeWrapper currentNodeWrapper = queue.poll();
			List<DepVariableWrapper> currentPath = currentNodeWrapper.getPath();
			if (currentNodeWrapper.getNode() == endNode) {
				path = currentPath;
				break;
			}
			List<DepVariableWrapper> currentNodeNeighbours = accessibilityMatrixManager.getNeighboursOf(currentNodeWrapper.getNode());
			for (DepVariableWrapper neighbour : currentNodeNeighbours) {
				if (!visitedNodes.contains(neighbour)) {
					visitedNodes.add(neighbour);
					List<DepVariableWrapper> currentPathPlusNeighbour = new ArrayList<>(currentPath);
					currentPathPlusNeighbour.add(neighbour);
					queue.offer(new BfsNodeWrapper(neighbour, currentPathPlusNeighbour));
				}
			}
		}
		return path;
	}
}
