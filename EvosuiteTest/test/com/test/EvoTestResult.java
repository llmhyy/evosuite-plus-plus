package com.test;

public class EvoTestResult {
	private int time;
	private double coverage;
	private int age;
	private double ratio;

	public EvoTestResult(int time, double coverage, int age, double ratio) {
		super();
		this.time = time;
		this.coverage = coverage;
		this.age = age;
		this.setRatio(ratio);
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getCoverage() {
		return coverage;
	}

	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
}
