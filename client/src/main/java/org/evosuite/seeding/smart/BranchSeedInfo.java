package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.coverage.branch.Branch;

public class BranchSeedInfo {
	
	public static String BYTE = "byte";
	public static String DOUBLE = "double";
	public static String FLOAT = "float";
	public static String INT = "int";
	public static String LONG = "long";
	public static String SHORT = "short";
	public static String STRING = "string";
	public static String OTHER = "other";
	
	private Branch branch;
	private String operandType; 
	
	public BranchSeedInfo(Branch branch, int benefiticalType, String operandType) {
		super();
		this.branch = branch;
		this.benefiticalType = benefiticalType;
		this.operandType = operandType;
	}
	
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

	public String getOperandType() {
		return operandType;
	}

	public void setOperandType(String operandType) {
		this.operandType = operandType;
	}

	

}
