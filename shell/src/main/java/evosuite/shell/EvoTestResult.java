package evosuite.shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.BranchDistributionInformation;
import org.evosuite.result.BranchInfo;
import org.evosuite.result.seedexpr.Event;
import org.evosuite.statistics.OutputVariable;

public class EvoTestResult {
	
	private double initialCoverage;
	private long initializationOverhead;
	
	private List<BranchInfo> missingBranches = new ArrayList<BranchInfo>();
	private Map<BranchInfo, String> coveredBranchWithTest = new HashMap<BranchInfo, String>();
	
	private int time;
	private double coverage;
	private int age;
	private double ratio;
	private List<Double> progress;
	private List<String> availableCalls = new ArrayList<>();
	private List<String> unavailableCalls = new ArrayList<>();
	private int[] distribution;
	private Map<Integer, Double> unCoveredBranchDistribution;
	private Map<Integer, Integer> distributionMap;
	private List<BranchDistributionInformation> branchInformation;
	private long randomSeed;

	private double IPFlagCoverage;
	private String uncoveredFlags;
	
	private Map<String, Boolean> methodCallAvailability;
	
	private Map<String, OutputVariable<?>> coverageTimeLine = new HashMap<>();
	
    private int smartBranchNum;
    private Map<String,String> runtimeBranchType;

	public EvoTestResult(int time, double coverage, int age, double ratio, List<Double> progress, double IPFlagCoverage,
			String uncoveredFlag, Map<Integer, Integer> distributionMap,
			Map<Integer, Double> unCoveredBranchDistribution, long randomSeed, Map<String, Boolean> map) {
		super();
		this.time = time;
		this.coverage = coverage;
		this.age = age;
		this.progress = progress;
		this.IPFlagCoverage = IPFlagCoverage;
		this.uncoveredFlags = uncoveredFlag;
		this.setDistributionMap(distributionMap);
		this.setUncoveredBranchDistribution(unCoveredBranchDistribution);
		this.randomSeed = randomSeed;
		this.setMethodCallAvailability(map);
		
		int count = 0;
		for (String key : map.keySet()) {
			if (map.get(key)) {
				count++;
			} else {
				System.out.println("Missing analyzing call: " + key);
			}
		}
		int size = map.size();
		this.ratio = -1;
		if (size != 0) {
			this.ratio = (double) count / size;
		}
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

	public Map<Integer, Integer> getDistributionMap() {
		return distributionMap;
	}

	public void setDistributionMap(Map<Integer, Integer> dsitributionMap) {
		this.distributionMap = dsitributionMap;
	}

	public Map<Integer, Double> getUncoveredBranchDistribution() {
		return unCoveredBranchDistribution;
	}

	public void setUncoveredBranchDistribution(Map<Integer, Double> uncoveredBranchDistribution) {
		this.unCoveredBranchDistribution = uncoveredBranchDistribution;
	}

	public long getRandomSeed() {
		return this.randomSeed;
	}

	public List<BranchDistributionInformation> getBranchInformation() {
		return branchInformation;
	}

	public void setBranchInformation(List<BranchDistributionInformation> branchInformation) {
		this.branchInformation = branchInformation;
	}

	public Map<String, Boolean> getMethodCallAvailability() {
		return methodCallAvailability;
	}

	public void setMethodCallAvailability(Map<String, Boolean> methodCallAvailability) {
		this.methodCallAvailability = methodCallAvailability;
	}

	public double getInitialCoverage() {
		return initialCoverage;
	}

	public void setInitialCoverage(double initialCoverage) {
		this.initialCoverage = initialCoverage;
	}

	public long getInitializationOverhead() {
		return initializationOverhead;
	}

	public void setInitializationOverhead(long initializationOverhead) {
		this.initializationOverhead = initializationOverhead;
	}

	public List<BranchInfo> getMissingBranches() {
		return missingBranches;
	}

	public void setMissingBranches(List<BranchInfo> missingBranches) {
		this.missingBranches = missingBranches;
	}

	public Map<BranchInfo, String> getCoveredBranchWithTest() {
		return coveredBranchWithTest;
	}

	public void setCoveredBranchWithTest(Map<BranchInfo, String> coveredBranchWithTest) {
		this.coveredBranchWithTest = coveredBranchWithTest;
	}

	private List<Event> eventSequence;
	
	public void setEventSequence(List<Event> eventSequence) {
		this.eventSequence = eventSequence;
	}
	
	public List<Event> getEventSequence(){
		return eventSequence;
	}

	public int getSmartBranchNum() {
		return smartBranchNum;
	}

	public void setSmartBranchNum(int smartBranchNum) {
		this.smartBranchNum = smartBranchNum;
	}

	public Map<String,String> getRuntimeBranchType() {
		return runtimeBranchType;
	}

	public void setRuntimeBranchType(Map<String,String> runtimeBranchType) {
		this.runtimeBranchType = runtimeBranchType;
	}

	public Map<String, OutputVariable<?>> getCoverageTimeLine() {
		return coverageTimeLine;
	}

	public void setCoverageTimeLine(Map<String, OutputVariable<?>> coverageTimeLine) {
		this.coverageTimeLine = coverageTimeLine;
	}	

}
