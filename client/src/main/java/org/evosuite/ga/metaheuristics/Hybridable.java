package org.evosuite.ga.metaheuristics;

import org.evosuite.testsuite.TestSuiteChromosome;

public interface Hybridable {
	public void updatePopulation(TestSuiteChromosome suite);

	public void generateSolution(TestSuiteChromosome previousSeeds);
	
}
