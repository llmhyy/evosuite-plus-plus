package org.evosuite.graphs.dataflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;

public class Dataflow {
	/**
	 * a map maintains what variables are dependent by which branch
	 * 
	 */
	public Map<Branch, List<DepVariable>> branchDepVarsMap;
	public Map<Branch, List<Branch>> branchDepBranchesMap;
	
	public Dataflow() {
		branchDepVarsMap = new HashMap<>();
	}
	
	public Map<Branch, List<DepVariable>> getBranchDependentMap() {
		return this.branchDepVarsMap;
	}
	
	public void addEntry(Branch branch, List<DepVariable> varList) {
		this.branchDepVarsMap.put(branch, varList);
	}
}
