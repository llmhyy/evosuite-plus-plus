package org.evosuite.graphs.dataflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;

public class Dataflow {
	/**
	 * a map maintains what variables are depended by which branch
	 * 
	 */
	public Map<Branch, List<DepVariable>> branchDependantMap = new HashMap<>();
	
	
}
