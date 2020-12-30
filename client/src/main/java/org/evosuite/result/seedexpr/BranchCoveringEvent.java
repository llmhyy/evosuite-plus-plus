package org.evosuite.result.seedexpr;

import org.evosuite.result.BranchInfo;

public class BranchCoveringEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4072156078972405855L;
	private BranchInfo branch;
	private String newCode;
	private String oldCode;
	private boolean isCovered;
	
	public BranchCoveringEvent(long timestamp, BranchInfo branchInfo, String newCode, String oldCode, boolean isCovered) {
		super(timestamp);
		this.setType(Event.branchCovering);
		this.setNewCode(newCode);
		this.setOldCode(oldCode);
		this.setBranch(branchInfo);
		this.setCovered(isCovered);
	}

	public BranchInfo getBranch() {
		return branch;
	}

	public void setBranch(BranchInfo branch) {
		this.branch = branch;
	}

	public String getNewCode() {
		return newCode;
	}

	public void setNewCode(String testCode) {
		this.newCode = testCode;
	}

	public boolean isCovered() {
		return isCovered;
	}

	public void setCovered(boolean isCovered) {
		this.isCovered = isCovered;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}


}
