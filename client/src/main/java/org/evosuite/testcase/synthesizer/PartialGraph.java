package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.graphs.dataflow.DepVariable;

public class PartialGraph {
	Map<DepVariable, DepVariableWrapper> allRelevantNodes = new HashMap<DepVariable, DepVariableWrapper>();
	
	/**
	 * In the original computation graph, multiple node can represent the same variable.
	 * Therefore, we need to merge those nodes when generating the partial graph.
	 * 
	 * @param var
	 * @return
	 */
	public DepVariableWrapper fetchAndMerge(DepVariable var) {
		DepVariableWrapper wrapper = allRelevantNodes.get(var);
		if(wrapper == null) {
			wrapper = new DepVariableWrapper(var);
			allRelevantNodes.put(var, wrapper);
		}
		else{
			for(int i=0; i<wrapper.var.getRelations().length; i++){
				List<DepVariable> list = wrapper.var.getRelations()[i];
				List<DepVariable> list0 = var.getRelations()[i];
				
				if(list0 != null){
					if(list == null) list = new ArrayList<>();
					
					for(DepVariable v: list0){
						if(!list.contains(v)){
							list.add(v);
						}
					}
				}
				
				wrapper.var.getRelations()[i] = list;
			}
			
			for(int i=0; i<wrapper.var.getReverseRelations().length; i++){
				List<DepVariable> list = wrapper.var.getReverseRelations()[i];
				List<DepVariable> list0 = var.getReverseRelations()[i];
				
				if(list0 != null){
					if(list == null) list = new ArrayList<>();
					
					for(DepVariable v: list0){
						if(!list.contains(v)){
							list.add(v);
						}
					}
				}
				
				wrapper.var.getReverseRelations()[i] = list;
			}
			
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
