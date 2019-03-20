/**
 * Copyright (C) 2010-2017 Gordon Fraser, Andrea Arcuri and EvoSuite
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
package org.evosuite.testsuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.SecondaryObjective;
import org.evosuite.ga.localsearch.LocalSearchObjective;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testsuite.localsearch.TestSuiteLocalSearch;

/**
 * <p>
 * TestSuiteChromosome class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public class TestSuiteChromosome extends AbstractTestSuiteChromosome<TestChromosome> {

	/** Secondary objectives used during ranking */
	private static final List<SecondaryObjective<?>> secondaryObjectives = new ArrayList<SecondaryObjective<?>>();
	private static int secondaryObjIndex = 0;
	private static final long serialVersionUID = 88380759969800800L;
	
	private List<Double> progressInfomation = new ArrayList<>();
	private int[] distribution; 
	private GeneticAlgorithm<? extends Chromosome> geneticAlgorithm;
//	private int age;

	private double IPFlagCoverage;
	private String uncoveredIPFlags;
	
	/**
	 * Add an additional secondary objective to the end of the list of
	 * objectives
	 * 
	 * @param objective
	 *            a {@link org.evosuite.ga.SecondaryObjective} object.
	 */
	public static void addSecondaryObjective(SecondaryObjective<?> objective) {
		secondaryObjectives.add(objective);
	}

	public static void ShuffleSecondaryObjective() {
		Collections.shuffle(secondaryObjectives);
	}
	
	public static int getSecondaryObjectivesSize(){
		return secondaryObjectives.size();
	}
	
	public static boolean isFirstSecondaryObjectiveEnabled(){
		return secondaryObjIndex == 0;
	}
	
	public static void disableFirstSecondaryObjective() {
		if (secondaryObjIndex != 1)
			secondaryObjIndex = 1;
	}
	
	public static void enableFirstSecondaryObjective() {
		if (secondaryObjIndex != 0)
			secondaryObjIndex = 0;
	}

	public static void reverseSecondaryObjective() {
		Collections.reverse(secondaryObjectives);
	}
	/**
	 * Remove secondary objective from list, if it is there
	 * 
	 * @param objective
	 *            a {@link org.evosuite.ga.SecondaryObjective} object.
	 */
	public static void removeSecondaryObjective(SecondaryObjective<TestSuiteChromosome> objective) {
		secondaryObjectives.remove(objective);
	}

	public static void removeAllSecondaryObjectives() {
		secondaryObjectives.clear();
	}

	/**
	 * <p>
	 * Constructor for TestSuiteChromosome.
	 * </p>
	 */
	public TestSuiteChromosome() {
		super();
	}

	/**
	 * <p>
	 * Constructor for TestSuiteChromosome.
	 * </p>
	 * 
	 * @param testChromosomeFactory
	 *            a {@link org.evosuite.ga.ChromosomeFactory} object.
	 */
	public TestSuiteChromosome(ChromosomeFactory<TestChromosome> testChromosomeFactory) {
		super(testChromosomeFactory);
	}

	/**
	 * <p>
	 * Constructor for TestSuiteChromosome.
	 * </p>
	 * 
	 * @param source
	 *            a {@link org.evosuite.testsuite.TestSuiteChromosome} object.
	 */
	protected TestSuiteChromosome(TestSuiteChromosome source) {
		super(source);
	}

	/**
	 * Add a test to a test suite
	 * 
	 * @param test
	 *            a {@link org.evosuite.testcase.TestCase} object.
	 */
	public TestChromosome addTest(TestCase test) {
		TestChromosome c = new TestChromosome();
		c.setTestCase(test);
		addTest(c);

		return c;
	}

	
	public void clearMutationHistory() {
		for(TestChromosome test : tests) {
			test.getMutationHistory().clear();
		}
	}

	/**
	 * Remove all tests
	 */
	public void clearTests() {
		tests.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Create a deep copy of this test suite
	 */
	@Override
	public TestSuiteChromosome clone() {
		return new TestSuiteChromosome(this);
	}

	/* (non-Javadoc)
	 * @see org.evosuite.ga.Chromosome#compareSecondaryObjective(org.evosuite.ga.Chromosome)
	 */
	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public  <T extends Chromosome> int compareSecondaryObjective(T o) {
		int objective = secondaryObjIndex;
		int c = 0;
		while (c == 0 && objective < secondaryObjectives.size()) {
			SecondaryObjective<T> so = (SecondaryObjective<T>) secondaryObjectives.get(objective++);
			if (so == null)
				break;
			c = so.compareChromosomes((T) this, o);
		} 
		return c;
	}

	/**
	 * For manual algorithm
	 * 
	 * @param testCase
	 *            to remove
	 */
	public void deleteTest(TestCase testCase) {
		if (testCase != null) {
			for (int i = 0; i < tests.size(); i++) {
				if (tests.get(i).getTestCase().equals((testCase))) {
					tests.remove(i);
				}
			}
		}
	}
	
	
	

	/**
	 * <p>
	 * getCoveredGoals
	 * </p>
	 * 
	 * @return a {@link java.util.Set} object.
	 */
	public Set<TestFitnessFunction> getCoveredGoals() {
		Set<TestFitnessFunction> goals = new LinkedHashSet<TestFitnessFunction>();
		for (TestChromosome test : tests) {
			final Set<TestFitnessFunction> goalsForTest = test.getTestCase().getCoveredGoals();
			goals.addAll(goalsForTest);
		}
		return goals;
	}
	
	public void removeCoveredGoal(TestFitnessFunction f) {
		for (TestChromosome test : tests) {
			if(test.getTestCase().getCoveredGoals().remove(f)) {
				
			}
		}
	}

	/**
	 * <p>
	 * getTests
	 * </p>
	 * 
	 * @return a {@link java.util.List} object.
	 */
	public List<TestCase> getTests() {
		List<TestCase> testcases = new ArrayList<TestCase>();
		for (TestChromosome test : tests) {
			testcases.add(test.getTestCase());
		}
		return testcases;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean localSearch(LocalSearchObjective<? extends Chromosome> objective) {
		TestSuiteLocalSearch localSearch = TestSuiteLocalSearch.selectTestSuiteLocalSearch();
		return localSearch.doSearch(this, (LocalSearchObjective<TestSuiteChromosome>) objective);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Apply mutation on test suite level
	 */
	@Override
	public void mutate() {
		for (int i = 0; i < Properties.NUMBER_OF_MUTATIONS; i++) {
			super.mutate();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Determine relative ordering of this chromosome to another chromosome If
	 * fitness is equal, the shorter chromosome comes first
	 */
	/*
	 * public int compareTo(Chromosome o) { if(RANK_LENGTH && getFitness() ==
	 * o.getFitness()) { return (int) Math.signum((length() -
	 * ((TestSuiteChromosome)o).length())); } else return (int)
	 * Math.signum(getFitness() - o.getFitness()); }
	 */
	@Override
	public String toString() {
		String result = "TestSuite: " + tests.size() + "\n";
		int i = 0;
		for (TestChromosome test : tests) {
			result += "Test "+i+": \n";
			i++;
			if(test.getLastExecutionResult() != null) {
				result += test.getTestCase().toCode(test.getLastExecutionResult().exposeExceptionMapping());
			} else {
				result += test.getTestCase().toCode() + "\n";
			}
		}
		return result;
	}
	
	private int timeUsed;
	private double availabilityRatio;
	private List<String> availableCalls = new ArrayList<>();
	private List<String> unavailableCalls = new ArrayList<>();

	public void setTimeUsed(int timeUsed) {
		this.timeUsed = timeUsed;
	}
	
	public int getTimeUsed(){
		return this.timeUsed;
	}

	public List<Double> getProgressInfomation() {
		return progressInfomation;
	}

	public void setProgressInfomation(List<Double> progressInfomation) {
		this.progressInfomation = progressInfomation;
	}

	public GeneticAlgorithm<? extends Chromosome> getGeneticAlgorithm() {
		return geneticAlgorithm;
	}

	public void setGeneticAlgorithm(GeneticAlgorithm<? extends Chromosome> geneticAlgorithm) {
		this.geneticAlgorithm = geneticAlgorithm;
	}

	public int[] getDistribution() {
		return distribution;
	}

	public void setDistribution(int[] distribution) {
		this.distribution = distribution;
	}

	public void setAvailabilityRatio(double availabilityRatio) {
		this.availabilityRatio = availabilityRatio;
	}

	public double getAvailabilityRatio() {
		return this.availabilityRatio;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public void setIPFlagCoverage(double IPFlagCoverage) {
		this.IPFlagCoverage = IPFlagCoverage;
	}

	public double getIPFlagCoverage() {
		return this.IPFlagCoverage;
	}

	public void setUncoveredIPFlags(String uncoveredIPFlags) {
		this.uncoveredIPFlags = uncoveredIPFlags;
	}

	public String getUncoveredIPFlags() {
		return this.uncoveredIPFlags;
	}
}
