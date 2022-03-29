package org.evosuite.testcase.synthesizer.improvedsynth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.evosuite.runtime.System;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.VariableInTest;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;

public class ImprovedConstructionPathSynthesizer extends ConstructionPathSynthesizer {
	private AccessibilityMatrixManager accessibilityMatrixManager;
	
	public ImprovedConstructionPathSynthesizer(boolean isDebug) {
		super(isDebug);
	}
	
	private AccessibilityMatrixManager prepare(PartialGraph partialGraph, TestCase testCase)
			throws ConstructionFailedException, ClassNotFoundException {
//		PartialGraph partialGraph = constructPartialComputationGraph(branch);
		// Access pairs here denotes a pair of nodes, one root and one leaf, such that we can
		// access (set) the leaf node starting from the root node.
		List<ConstructionPlan> constructionPlans = new ArrayList<>();
		AccessibilityMatrixManager accessibilityMatrixManager = new AccessibilityMatrixManager();
		accessibilityMatrixManager.initialise(partialGraph, testCase);
		
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
				
				boolean isPathExists = accessibilityMatrixManager.findShortestPathLength(rootNode, leafNode) > 0;
				if (isPathExists) {
					
					List<DepVariableWrapper> path = getPathBetween(rootNode, leafNode);
					if (path == null || path.size() < 2) {
						continue;
					}
					
					List<Operation> operations = generateOperations(path);	
					List<DepVariableWrapper> constructionPath = getPathBetween(rootNode, leafNode);
					ConstructionPlan plan = new ConstructionPlan(rootNode, leafNode, operations, constructionPath);
					
					constructionPlans.add(plan);
				}
			}
		}
		
		accessibilityMatrixManager.setConstructionPlans(constructionPlans);
		
		// Set up test case statement generator
//		TestCaseStatementGenerator testCaseStatementGenerator = new TestCaseStatementGeneratorBuilder()
//				.setGraph2CodeMap(graph2CodeMap)
//				.setAccessibilityMatrixManager(accessibilityMatrixManager)
//				.setTestCase(testCase)
//				.setBranch(branch)
//				.build();
			
		
//		setPartialGraph(partialGraph);
//		setGraph2CodeMap(graph2CodeMap);
		
		return accessibilityMatrixManager;
	}
	
	public void buildNodeStatementCorrespondence(TestCase test, Branch b, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {

		PartialGraph partialGraph = constructPartialComputationGraph(b);
		
		this.accessibilityMatrixManager = prepare(partialGraph, test);
		
//		System.currentTimeMillis();
//		GraphVisualizer.visualizeComputationGraph(b, 10000);
		if(isDebugger) {
			GraphVisualizer.visualizeComputationGraph(partialGraph, 5000, "test");			
		}
		
		logTest(test, b, isDebugger, 0, null);
		
		int c = 1;
		
		/**
		 * track what variable reference can be reused. Note that, one node can corresponding to multiple statements.
		 * It is because a static field/method can be called twice dynamically.
		 * Therefore, we need to construct multiple fields/methods for different object of the same type.
		 */
		Map<DepVariableWrapper, VarRelevance> map = new HashMap<>();
		
		List<ConstructionPlan> pairList = this.accessibilityMatrixManager.getConstructionPlans();
		for(ConstructionPlan plan: pairList) {
			DepVariableWrapper root = plan.getRoot();
			
			List<DepVariableWrapper> nodeOrders = plan.getConstructionPath(); // N+1 nodes 3
			List<Operation> operations = plan.getOperations(); // N operations 2
			
			for(int i=0; i<=operations.size(); i++) {
				DepVariableWrapper node = nodeOrders.get(i+1); 
				Operation operation = operations.get(i); 
				
				boolean isValid = checkDependency(root, map);
				if(isValid) {
					VariableInTest testVariable = getCallerObject(map, node);
					deriveCodeForTest(map, test, testVariable, b, allowNullValue, operation);
					
					node.processed = true;
					
					logTest(test, b, isDebugger, c++, node);
				} 
			}

			
			if(test.size() > Properties.CHROMOSOME_LENGTH) {
				break;
			}
		}
		
		System.currentTimeMillis();
		this.setPartialGraph(partialGraph);
		this.setGraph2CodeMap(map);
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
