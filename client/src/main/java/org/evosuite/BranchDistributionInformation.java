package org.evosuite;

import java.io.Serializable;
import java.util.Map;

public class BranchDistributionInformation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long time;
	private boolean covered;
	private int branchId;
	private boolean value;
	private double fitness;
	private double unCoveredDistributionValue;
	private Map<Integer, Double> unCoveredDistributionMap;
	
	
	public BranchDistributionInformation(int time, boolean covered, int branchId, boolean value, double fitness,
			double unCoveredDistributionValue, Map<Integer, Double> unCoveredDistributionMap) {
		super();
		this.time = time;
		this.covered = covered;
		this.branchId = branchId;
		this.value = value;
		this.fitness = fitness;
		this.unCoveredDistributionValue = unCoveredDistributionValue;
		this.unCoveredDistributionMap = unCoveredDistributionMap;
	}
	public BranchDistributionInformation() {
		// TODO Auto-generated constructor stub
		super();
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public boolean getCovered() {
		return covered;
	}
	public void setCovered(boolean covered) {
		this.covered = covered;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	public double getUnCoveredDistributionValue() {
		return unCoveredDistributionValue;
	}
	public void setUnCoveredDistributionValue(double unCoveredDistributionValue) {
		this.unCoveredDistributionValue = unCoveredDistributionValue;
	}
	public Map<Integer, Double> getUnCoveredDistributionMap() {
		return unCoveredDistributionMap;
	}
	public void setUnCoveredDistributionMap(Map<Integer, Double> unCoveredDistributionMap) {
		this.unCoveredDistributionMap = unCoveredDistributionMap;
	}
	
	
	

}
