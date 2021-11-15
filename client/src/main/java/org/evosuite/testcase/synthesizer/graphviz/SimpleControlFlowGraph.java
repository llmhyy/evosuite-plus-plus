package org.evosuite.testcase.synthesizer.graphviz;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleControlFlowGraph implements Serializable {
	private static final long serialVersionUID = -8461828483501336801L;

	// Have to do it like this to avoid writing a key deserialiser
	public Map<String, SimpleBytecodeInstruction> stringRepresentationToNode = new HashMap<>();
	public Map<String, List<SimpleBytecodeInstruction>> stringRepresentationToAdjacencyList = new HashMap<>();
	
	public Set<SimpleBytecodeInstruction> entryPoints = new HashSet<>();
	
	@JsonIgnore
	public Map<BytecodeInstruction, SimpleBytecodeInstruction> memory = new HashMap<>();
	
	public SimpleControlFlowGraph() {
	}
	
	public SimpleControlFlowGraph(RawControlFlowGraph cfg) {
		// Traverse the entire graph and transform each node into a SimpleBytecodeInstruction
		// Maintain a list of edges for each node
		
		Set<BytecodeInstruction> entryPoints = cfg.determineEntryPoints();
		Set<BytecodeInstruction> exploredNodes = new HashSet<>();
		
		for (BytecodeInstruction entryPoint : entryPoints) {
			map(entryPoint, cfg, exploredNodes);
		}
	}
	
	private void createSimpleRepresentation(BytecodeInstruction node) {
		if (memory.containsKey(node)) {
			return;
		}
		
		SimpleBytecodeInstruction simpleNode = SimpleBytecodeInstruction.from(node);
		memory.put(node, simpleNode);
		stringRepresentationToNode.put(simpleNode.toString(), simpleNode);
		stringRepresentationToAdjacencyList.put(simpleNode.toString(), new ArrayList<>());
	}
	
	private void createSimpleRepresentationIfDoesNotExist(BytecodeInstruction node) {
		if (!memory.containsKey(node)) {
			createSimpleRepresentation(node);
		}
	}
	
	private void addEdgeBetween(BytecodeInstruction parent, BytecodeInstruction child) {
		createSimpleRepresentationIfDoesNotExist(parent);
		createSimpleRepresentationIfDoesNotExist(child);
		
		String representationOfParent = memory.get(parent).toString();
		SimpleBytecodeInstruction simpleChild = memory.get(child);
		
		stringRepresentationToAdjacencyList.get(representationOfParent).add(simpleChild);
	}
	
	// Helper method to traverse the graph. Essentially performs DFS.
	// 1) If our node has been visited OR has out-degree 0, return (we are done)
	// 2) Else, we construct the edges between the node and all children, and call map() on the children nodes
	private void map(BytecodeInstruction node, RawControlFlowGraph cfg, Set<BytecodeInstruction> exploredNodes) {
		boolean isAlreadyExplored = exploredNodes.contains(node);
		if (isAlreadyExplored) {
			return;
		}
		
		createSimpleRepresentationIfDoesNotExist(node);
		
		// Add the node to entry points if they have no parents
		boolean isEntryPoint = (cfg.getParents(node).size() == 0);
		if (isEntryPoint) {
			this.entryPoints.add(memory.get(node));
		}
		
		Set<BytecodeInstruction> children = cfg.getChildren(node);
		boolean isLeafNode = (children.size() == 0);
		if (isLeafNode) {
			return;
		}
		
		for (BytecodeInstruction child : children) {
			addEdgeBetween(node, child);
		}
		
		exploredNodes.add(node);
		
		for (BytecodeInstruction child : children) {
			map(child, cfg, exploredNodes);
		}
	}
	
	public SimpleControlFlowGraph from(File file) throws StreamReadException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleControlFlowGraph toReturn = mapper.readValue(file, SimpleControlFlowGraph.class);
		return toReturn;
	}
	
	public void to(File file) throws StreamWriteException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(file, this);
	}
}
