package org.evosuite.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.setup.var.RelevantVariable;

public class BranchToVariableMapBuilder {
	
	private List<Branch> getTargetBranches(){
		BranchCoverageFactory branchFactory = new BranchCoverageFactory();
		List<BranchCoverageTestFitness> branchGoals = branchFactory.getCoverageGoals();
		
		List<Branch> branches = new ArrayList<>();
		for(BranchCoverageTestFitness goal: branchGoals) {
			Branch branch = goal.getBranch();
			if(!branches.contains(branch)) {
				branches.add(branch);
			}
		}
		
		return branches;
	}
	
	public Map<Branch, RelevantVariable> buildBranchToVariableMap(){
		Map<Branch, RelevantVariable> map = new HashMap<>();
		
		List<Branch> branches = getTargetBranches();
		for(Branch branch: branches) {
			RawControlFlowGraph graph = branch.getInstruction().getRawCFG();
			
			
			
		}
		
		
		
		
		return map;
	}
}
