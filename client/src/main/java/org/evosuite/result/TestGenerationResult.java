/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.BranchDistributionInformation;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.result.seedexpr.Event;
import org.evosuite.result.seedexpr.EventSequence;
import org.evosuite.statistics.OutputVariable;
import org.evosuite.testcase.TestCase;

public interface TestGenerationResult extends Serializable {

	public enum Status { SUCCESS, TIMEOUT, ERROR };
	
	/** Did test generation succeed? */
	public Status getTestGenerationStatus();
	
	/** If there was an error, this contains the error message */
	public String getErrorMessage();
	
	/** The entire GA in its final state */
	public GeneticAlgorithm<?> getGeneticAlgorithm();
	
	/** Map from test method to ContractViolation */
	public Set<Failure> getContractViolations(String name);
	
	/** Class that was tested */
	public String getClassUnderTest();
	
	/** Target coverage criterion used to create this test suite */
	public String[] getTargetCriterion();
	
	/** Coverage level of the target criterion */
	public double getTargetCoverage(FitnessFunction<?> function);
	
	/** Map from test method to EvoSuite test case */
	public TestCase getTestCase(String name);

	/** Map from test method to EvoSuite test case */
	public String getTestCode(String name);
	
	/** JUnit test suite source code */
	public String getTestSuiteCode();
	
	/** Lines covered by test */ 
	public Set<Integer> getCoveredLines(String name);
	
	public Set<BranchInfo> getCoveredBranches(String name);

	public Set<MutationInfo> getCoveredMutants(String name);

	public Set<MutationInfo> getExceptionMutants();

	/** Lines covered by final test suite */ 
	public Set<Integer> getCoveredLines();

	/** Branches covered by final test suite */ 
	public Set<BranchInfo> getCoveredBranches();

	/** Mutants detected by final test suite */ 
	public Set<MutationInfo> getCoveredMutants();

	/** Lines not covered by final test suite */ 
	public Set<Integer> getUncoveredLines();

	/** Branches not covered by final test suite */ 
	public Set<BranchInfo> getUncoveredBranches();

	/** Mutants not detected by final test suite */ 
	public Set<MutationInfo> getUncoveredMutants();

	/** Comment for that test */
	public String getComment(String name);
	
	public int getElapseTime();
	public void setElapseTime(int elapseTime);
	public double getCoverage();
	public void setCoverage(double coverage);
	public void setAvailabilityRatio(double availabilityRatio);
	
	public List<Double> getProgressInformation();
	public void setProgressInformation(List<Double> progressInformtion);
	public int[] getDistribution();
	public void setDistribution(int[] distribution);
	public double getAvailabilityRatio();

	public List<String> getAvailableCalls();
	public void setAvailableCalls(List<String> availableCalls);
	public List<String> getUnavailableCalls();
	public void setUnavailableCalls(List<String> unavailableCalls);
	public int getAge();
	public void setAge(int age);

	public void setIPFlagCoverage(double IPFlagCoverage);
	public double getIPFlagCoverage();
	
	public void setUncoveredIPFlags(String uncoveredIPFlags);
	public String getUncoveredIPFlags();
	
	public Map<Integer, Double> getUncoveredBranchDistribution();
	public void setUncoveredBranchDistribution(Map<Integer, Double> uncoveredBranchDistribution);
	
	public Map<Integer, Integer> getDistributionMap();
	public void setDistributionMap(Map<Integer, Integer> distributionMap);

	public long getRandomSeed(); 
	public void setRandomSeed(long randomSeed); 
	
	public List<BranchDistributionInformation> getBranchInformation();
	public void setBranchInformation(List<BranchDistributionInformation> branchInformation); 
	
	public Map<String, Boolean> getMethodCallAvailabilityMap();
	public void setMethodCallAvailabilityMap(Map<String, Boolean> methodCallAvailabilityMap);
	
	public double getInitialCoverage();
	public void setInitialCoverage(double initialCoverage);
	
	public long getInitializationOverhead();
	public void setInitializationOverhead(long initializationOverhead);

	public List<BranchInfo> getMissingBranches();
	public void setMissingBranches(List<BranchInfo> missingBranches);
	
	public Map<BranchInfo, String> getCoveredBranchWithTest();
	public void setCoveredBranchWithTest(Map<BranchInfo, String> coveredBranchWithTest);

	
	public List<Event> getEventSequence();
	public void setEventSequence(List<Event> events);

	public int getSmartBranchNum();
	public void setSmartBranchNum(int smartBranchNum);
	
	public Map<String, String> getRuntimeBranchType();
	public void setRuntimeBranchType(Map<String, String> runtimeBranchType);

	public Map<String, OutputVariable<?>> getCoverageTimeLine();
	public void setCoverageTimeLine(Map<String, OutputVariable<?>> coverageTimeLine);
	
}
