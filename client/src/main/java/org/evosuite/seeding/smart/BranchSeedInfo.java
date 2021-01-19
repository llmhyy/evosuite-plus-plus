package org.evosuite.seeding.smart;

import org.evosuite.coverage.branch.Branch;

public class BranchSeedInfo {
	private Branch branch;
	
	/**
	 * see type defined in {@code SeedingApplicationEvaluator}
	 */
	private int benefiticalType;

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public int getBenefiticalType() {
		return benefiticalType;
	}

	public void setBenefiticalType(int benefiticalType) {
		this.benefiticalType = benefiticalType;
	}

	public BranchSeedInfo(Branch branch, int benefiticalType) {
		super();
		this.branch = branch;
		this.benefiticalType = benefiticalType;
	}
}
