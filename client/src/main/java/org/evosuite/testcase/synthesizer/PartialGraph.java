package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.graphs.dataflow.DepVariable;

public class PartialGraph {
	Map<DepVariable, DepVariableWrapper> allRelevantNodes = new HashMap<DepVariable, DepVariableWrapper>();
	
	public DepVariableWrapper fetch(DepVariable var) {
		DepVariableWrapper wrapper = allRelevantNodes.get(var);
		if(wrapper == null) {
			wrapper = new DepVariableWrapper(var);
			allRelevantNodes.put(var, wrapper);
		}
		
		return wrapper;
	}
	
	public List<DepVariableWrapper> getTopLayer(){
		List<DepVariableWrapper> list = new ArrayList<DepVariableWrapper>();
		for(DepVariableWrapper node: allRelevantNodes.values()) {
			if(node.parents.isEmpty()) {
				list.add(node);
			}
		}
		
		return list;
	}
}
