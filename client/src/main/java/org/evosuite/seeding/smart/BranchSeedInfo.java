package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.TestChromosome;

public class BranchSeedInfo {
	
	public static String BYTE = "byte";
	public static String DOUBLE = "double";
	public static String FLOAT = "float";
	public static String INT = "int";
	public static String LONG = "long";
	public static String SHORT = "short";
	public static String STRING = "string";
	public static String CHARACTER = "character";
	public static String TBYTE = Byte.class.getCanonicalName();
	public static String TDOUBLE = Double.class.getCanonicalName();
	public static String TFLOAT = Float.class.getCanonicalName();
	public static String TINT = Integer.class.getCanonicalName();
	public static String TLONG = Long.class.getCanonicalName();
	public static String TSHORT = Short.class.getCanonicalName();
	public static String TSTRING = String.class.getCanonicalName();
	public static String TCHARACTER = Character.class.getCanonicalName();
	public static String OTHER = "unknown";
	
	private Branch branch;
	private Branch auxilaryBranch;
	private String operandType; 
	private ValuePreservance valuePreservance;
	
	public TestChromosome referredTest;
	
	private List<ObservedConstant> potentialSeeds = new ArrayList<>();
	
	public void addPotentialSeed(ObservedConstant value) {
		this.getPotentialSeeds().add(value);
	}
	
	public BranchSeedInfo(Branch branch, int benefiticalType, String operandType, 
			ValuePreservance valuePreservance) {
		super();
		this.branch = branch;
		this.benefiticalType = benefiticalType;
		this.operandType = operandType;
		this.valuePreservance = valuePreservance;
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

	public Branch getAuxilaryBranch() {
		return auxilaryBranch;
	}

	public void setAuxilaryBranch(Branch auxilaryBranch) {
		this.auxilaryBranch = auxilaryBranch;
	}

	public List<ObservedConstant> getPotentialSeeds() {
		return potentialSeeds;
	}

	public ValuePreservance getValuePreservance() {
		return valuePreservance;
	}

	

}
