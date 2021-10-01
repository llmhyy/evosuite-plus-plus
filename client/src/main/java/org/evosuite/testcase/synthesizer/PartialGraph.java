package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapperFactory;

public class PartialGraph {
	Map<DepVariableWrapper, DepVariableWrapper> allRelevantNodes = new HashMap<DepVariableWrapper, DepVariableWrapper>();
	
	public int getGraphSize() {
		return allRelevantNodes.keySet().size();
	}
	
	/**
	 * In the original computation graph, multiple node can represent the same variable.
	 * Therefore, we need to merge those nodes when generating the partial graph.
	 * 
	 * @param var
	 * @return
	 */
	public DepVariableWrapper fetchAndMerge(DepVariable var) {
		DepVariableWrapper tempWrapper = DepVariableWrapperFactory.createWrapperInstance(var);
		
		if(var.toString().contains("ALOAD")) {
			System.currentTimeMillis();
//			System.currentTimeMillis();
		}
		
		DepVariableWrapper wrapper = allRelevantNodes.get(tempWrapper);
		if(wrapper == null) {
			wrapper = DepVariableWrapperFactory.createWrapperInstance(var);
			allRelevantNodes.put(wrapper, wrapper);
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

	private Branch targetBranch;
	
	public void setBranch(Branch b) {
		this.targetBranch = b;
	}
	
	public Branch getBranch() {
		return this.targetBranch;
	}
}
