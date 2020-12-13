package org.evosuite.result.seedexpr;

import org.evosuite.result.BranchInfo;

public class BranchCoveringEvent extends Event {

	private BranchInfo branch;
	
	public BranchCoveringEvent(long timestamp, String dataType, BranchInfo branchInfo) {
		super(timestamp, dataType);
		this.setType(Event.branchCovering);
		
		this.setBranch(branchInfo);
	}

	public BranchInfo getBranch() {
		return branch;
	}

	public void setBranch(BranchInfo branch) {
		this.branch = branch;
	}

}
