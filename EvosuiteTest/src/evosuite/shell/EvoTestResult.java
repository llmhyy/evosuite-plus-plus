package evosuite.shell;

import java.util.ArrayList;
import java.util.List;

public class EvoTestResult {
	private int time;
	private double coverage;
	private int age;
	private double ratio;
	private List<Double> progress;
	private List<String> availableCalls = new ArrayList<>();
	private List<String> unavailableCalls = new ArrayList<>();
	private int[] distribution;
	
	private double IPFlagCoverage;
	private String uncoveredFlags;

	public EvoTestResult(int time, double coverage, int age, 
			double ratio, List<Double> progress, 
			double IPFlagCoverage, String uncoveredFlag, int[] distribution) {
		super();
		this.time = time;
		this.coverage = coverage;
		this.age = age;
		this.ratio = ratio;
		this.progress = progress;
		this.IPFlagCoverage = IPFlagCoverage;
		this.uncoveredFlags = uncoveredFlag;
		this.setDistribution(distribution);
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

	public List<Double> getProgress() {
		return progress;
	}

	public void setProgress(List<Double> progress) {
		this.progress = progress;
	}

	public List<String> getAvailableCalls() {
		return availableCalls;
	}

	public void setAvailableCalls(List<String> availableCalls) {
		this.availableCalls = availableCalls;
	}

	public List<String> getUnavailableCalls() {
		return unavailableCalls;
	}

	public void setUnavailableCalls(List<String> unavailableCalls) {
		this.unavailableCalls = unavailableCalls;
	}

	public double getIPFlagCoverage() {
		return this.IPFlagCoverage;
	}

	public void setIPFlagCoverage(double iPFlagCoverage) {
		this.IPFlagCoverage = iPFlagCoverage;
	}

	public String getUncoveredFlags() {
		return uncoveredFlags;
	}

	public void setUncoveredFlags(String uncoveredFlags) {
		this.uncoveredFlags = uncoveredFlags;
	}

	public int[] getDistribution() {
		return distribution;
	}

	public void setDistribution(int[] distribution) {
		this.distribution = distribution;
	}

}
