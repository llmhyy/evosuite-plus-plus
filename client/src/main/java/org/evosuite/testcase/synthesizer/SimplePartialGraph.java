package org.evosuite.testcase.synthesizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simplified form of the partial graph, for use in constructing graph visualisations.
 */
public class SimplePartialGraph implements Serializable {
	private static final long serialVersionUID = -3204184931804320688L;
	
	// Used to prevent infinite recursion during construction.
	@JsonIgnore
	public Map<String, SimpleDepVariableWrapper> memory = new HashMap<>();
	
	public Map<String, SimpleDepVariableWrapper> allRelevantNodes = new HashMap<>();
	
	public String branch;

	// Needed for de/serialization
	public SimplePartialGraph() {
		
	}
	
	public SimplePartialGraph(PartialGraph partialGraph) {
		branch = partialGraph.getBranch().toString();
		
		for (Map.Entry<DepVariableWrapper, DepVariableWrapper> entry : partialGraph.allRelevantNodes.entrySet()) {
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
}
