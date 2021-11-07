package org.evosuite.testcase.synthesizer.graphviz;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;
import org.evosuite.testcase.variable.VariableReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simplified form of the partial graph, for use in constructing graph visualisations.
 */
public class SimplePartialGraph implements Serializable {
	private static final long serialVersionUID = -3204184931804320688L;
	
	// Used to prevent infinite recursion during construction.
	@JsonIgnore
	public Map<String, SimpleDepVariableWrapper> memory = new HashMap<>();
	
	// Only used during conversion from a PartialGraph, not used during deserialisation
	@JsonIgnore
	public Map<DepVariableWrapper, VarRelevance> graph2CodeMap;
	
	public Map<String, SimpleDepVariableWrapper> allRelevantNodes = new HashMap<>();
	
	public String branch;
	
	public Map<String, SimpleStatement> nodeToStatement = new HashMap<>();
	
	public List<String> graphTraversalOrder = new ArrayList<>();
	
	// Needed for de/serialization
	public SimplePartialGraph() {
		
	}
	
	public SimplePartialGraph(PartialGraph partialGraph) {
		branch = partialGraph.getBranch().toString();
		
		for (Map.Entry<DepVariableWrapper, DepVariableWrapper> entry : partialGraph.getAllRelevantNodes().entrySet()) {
			DepVariableWrapper key = entry.getKey();
			DepVariableWrapper value = entry.getValue();
			
			initialiseFrom(key);
			initialiseFrom(value);
			
			SimpleDepVariableWrapper simpleKey = getFromMemory(key.var.getShortLabel());
			SimpleDepVariableWrapper simpleValue = getFromMemory(value.var.getShortLabel());
			allRelevantNodes.put(simpleKey.shortLabel, simpleKey);
			allRelevantNodes.put(simpleValue.shortLabel, simpleValue);
		}
	}
	
	public void writeTo(File file) throws StreamWriteException, DatabindException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(file, this);
	}
	
	public void recordGraphTraversalOrder(DepVariableWrapper node, VarRelevance varRel) {
		String nodeLabel = node.var.getShortLabel();
		this.graphTraversalOrder.add(nodeLabel);
		
		if (varRel == null || varRel.matchedVars.isEmpty()) {
			return;
		}
		
		VariableReference matchedVariable = varRel.matchedVars.get(0);
		int statementPosition = matchedVariable.getStPosition();
		TestCase testCase = matchedVariable.getTestCase();
		
		SimpleStatement simpleStatement = new SimpleStatement(statementPosition, testCase);
		this.nodeToStatement.put(nodeLabel, simpleStatement);
	}
	
	// Compatibility methods for GraphVisualizer
	@JsonIgnore
	public List<SimpleDepVariableWrapper> getTopLayer() {
		List<SimpleDepVariableWrapper> toReturn = new ArrayList<>();
		for (SimpleDepVariableWrapper simpleWrapper : allRelevantNodes.values()) {
			if (simpleWrapper.parents.size() == 0) {
				toReturn.add(simpleWrapper);
			}
		}
		return toReturn;
	}
	
	public String getBranch() {
		return branch;
	}
	
	private SimpleDepVariableWrapper getFromMemory(String key) {
		// If it's not in memory, create it
		if (!memory.containsKey(key)) {
			SimpleDepVariableWrapper simpleDepVariableWrapper = new SimpleDepVariableWrapper();
			simpleDepVariableWrapper.shortLabel = key;
			memory.put(key, simpleDepVariableWrapper);
			return simpleDepVariableWrapper;
		}
		
		return memory.get(key);
	}
	
	private void initialiseFrom(DepVariableWrapper wrapper) {
		String shortLabel = wrapper.var.getShortLabel();
		SimpleDepVariableWrapper simpleWrapper = getFromMemory(shortLabel);
		
		for (DepVariableWrapper parent : wrapper.parents) {
			String parentShortLabel = parent.var.getShortLabel();
			SimpleDepVariableWrapper simpleParent = getFromMemory(parentShortLabel);
			if (simpleWrapper.parents.contains(simpleParent)) {
				continue;
			}
			simpleWrapper.parents.add(simpleParent);
		}
		
		for (DepVariableWrapper child : wrapper.children) {
			String childShortLabel = child.var.getShortLabel();
			SimpleDepVariableWrapper simpleChild = getFromMemory(childShortLabel);
			if (simpleWrapper.children.contains(simpleChild)) {
				continue;
			}
			simpleWrapper.children.add(simpleChild);
			
		}
	}
	
	// Records the node to statement test case correspondence
	private void recordNodeToStatementCorrespondence(DepVariableWrapper node) {
		VarRelevance varRelevance = this.graph2CodeMap.get(node);
		if (varRelevance == null) {
			return;
		}
		
		// There are no matched variables
		// In this case, we cannot do anything
		if (varRelevance.matchedVars.size() == 0) {
			return;
		}
		
		VariableReference varRef = varRelevance.matchedVars.get(0);
		int statementPosition = varRef.getStPosition();
		TestCase testCase = varRef.getTestCase();
		SimpleStatement simpleStatement = new SimpleStatement(statementPosition, testCase);
		this.nodeToStatement.put(node.var.getShortLabel(), simpleStatement);
	}
}
