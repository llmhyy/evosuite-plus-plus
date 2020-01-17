package org.evosuite.coverage.branch;

import org.evosuite.setup.CallContext;

public interface BranchFitness {
	public BranchCoverageGoal getBranchGoal();
	
	public void setContext(CallContext callContext);
}
