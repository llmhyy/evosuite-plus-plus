package org.evosuite.ga.metaheuristics;

import org.evosuite.testsuite.TestSuiteChromosome;

public interface Hybridable {
	/**
	 * update the population with the previous seeds
	 * @param previousSeeds
	 */
	public void updatePopulation(TestSuiteChromosome previousSeeds);

	public void generateSolution(TestSuiteChromosome previousSeeds);
	
}
