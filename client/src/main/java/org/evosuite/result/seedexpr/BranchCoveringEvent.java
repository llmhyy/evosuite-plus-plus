package org.evosuite.result.seedexpr;

import org.evosuite.result.BranchInfo;

public class BranchCoveringEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4072156078972405855L;
	private BranchInfo branch;
	private String testCode;
	
	public BranchCoveringEvent(long timestamp, BranchInfo branchInfo, String testCode) {
		super(timestamp);
		this.setType(Event.branchCovering);
		this.setTestCode(testCode);
		this.setBranch(branchInfo);
	}

	public BranchInfo getBranch() {
		return branch;
	}

	public void setBranch(BranchInfo branch) {
		this.branch = branch;
	}

	public String getTestCode() {
		return testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}

}
