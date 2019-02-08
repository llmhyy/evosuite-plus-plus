package evosuite.shell;

import java.util.List;
import java.util.Set;

import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.result.BranchInfo;
import org.evosuite.result.Failure;
import org.evosuite.result.MutationInfo;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.testcase.TestCase;

public class TestResult implements TestGenerationResult {
	private static final long serialVersionUID = 1L;

	@Override
	public Status getTestGenerationStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneticAlgorithm<?> getGeneticAlgorithm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Failure> getContractViolations(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassUnderTest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getTargetCriterion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTargetCoverage(FitnessFunction<?> function) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TestCase getTestCase(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestCode(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTestSuiteCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Integer> getCoveredLines(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<BranchInfo> getCoveredBranches(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MutationInfo> getCoveredMutants(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MutationInfo> getExceptionMutants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Integer> getCoveredLines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<BranchInfo> getCoveredBranches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MutationInfo> getCoveredMutants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Integer> getUncoveredLines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<BranchInfo> getUncoveredBranches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MutationInfo> getUncoveredMutants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComment(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getElapseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setElapseTime(int elapseTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getCoverage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCoverage(double coverage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAvailabilityRatio(double availabilityRatio) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Double> getProgressInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProgressInformation(List<Double> progressInformtion) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getDistribution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDistribution(int[] distribution) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getAvailabilityRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

}
