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
package org.evosuite.ga.metaheuristics.mosa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.NonduplicationComparator;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.ga.operators.ranking.CrowdingDistance;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.factories.RandomLengthTestFactory;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import evosuite.shell.ExceptionResult;

/**
 * Implementation of the DynaMOSA (Many Objective Sorting Algorithm) described
 * in the paper "Automated Test Case Generation as a Many-Objective Optimisation
 * Problem with Dynamic Selection of the Targets".
 * 
 * @author Annibale Panichella, Fitsum M. Kifetew, Paolo Tonella
 */
public class DynaMOSA<T extends Chromosome> extends AbstractMOSA<T> {

	private static final long serialVersionUID = 146182080947267628L;

	private static final Logger logger = LoggerFactory.getLogger(DynaMOSA.class);

	/** Manager to determine the test goals to consider at each generation */
	protected MultiCriteriaManager<T> goalsManager = null;

	protected CrowdingDistance<T> distance = new CrowdingDistance<T>();

	protected ExceptionBranchEnhancer<T> exceptionBranchEnhancer = new ExceptionBranchEnhancer<T>(goalsManager);
	
	protected ExceptionResult<T> exceptionResult = new ExceptionResult<>();
	
	/**
	 * Constructor based on the abstract class {@link AbstractMOSA}.
	 * 
	 * @param factory
	 */
	public DynaMOSA(ChromosomeFactory<T> factory) {
		super(factory);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected void evolve() {
//		Set<FitnessFunction<T>> prevCoveredGoals = new HashSet<FitnessFunction<T>>();
//		prevCoveredGoals.addAll(this.goalsManager.getCoveredGoals());
		List<T> offspringPopulation = this.breedNextGeneration();

		// Create the union of parents and offSpring
		List<T> union = new ArrayList<T>(this.population.size() + offspringPopulation.size());
		union.addAll(this.population);
		union.addAll(offspringPopulation);

		// Ranking the union
		logger.debug("Union Size = {}", union.size());

		// Ranking the union using the best rank algorithm (modified version of the non
		// dominated sorting algorithm

		FitnessFunction<T> newCoveredGoal = null;
		int count = 0;
		for(FitnessFunction<T> goal: this.goalsManager.getCoveredGoals()) {
			if(count == this.goalsManager.getCoveredGoals().size()-1) {
				newCoveredGoal = goal;
			}
			count++;
		}
		
		Set<FitnessFunction<T>> caredSet = new HashSet<>();
		if(newCoveredGoal == null) {
			caredSet = this.goalsManager.getCurrentGoals();
		}
		else {
			caredSet.add(newCoveredGoal);
		}
		/**
		 * rank with the previous covered goal
		 */
		this.rankingFunction.computeRankingAssignment(union, caredSet);

		// let's form the next population using "preference sorting and non-dominated
		// sorting" on the
		// updated set of goals
		int remain = Math.max(Properties.POPULATION, this.rankingFunction.getSubfront(0).size());
		int index = 0;
		List<T> front = null;
		this.population.clear();

		// Obtain the next front
		front = this.rankingFunction.getSubfront(index);
		this.population.addAll(front);
		remain = remain - front.size();
		/**
		 * re-rank with the current goal
		 */
		this.rankingFunction.computeRankingAssignment(union, this.goalsManager.getCurrentGoals());

		while ((remain > 0) && (remain >= front.size()) && !front.isEmpty()) {
			// Assign crowding distance to individuals
			this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());

			// Obtain the next front
			front = this.rankingFunction.getSubfront(index);
			
			// Add the individuals of this front
			this.population.addAll(front);

			// Decrement remain
			remain = remain - front.size();
			index++;
			front = this.rankingFunction.getSubfront(index);
		}

		// Remain is less than front(index).size, insert only the best one
		if (remain > 0 && !front.isEmpty()) { // front contains individuals to insert
			this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());
			Collections.sort(front, new NonduplicationComparator<T>(this.population));
			for (int k = 0; k < remain; k++) {
				this.population.add(front.get(k));
			}

			remain = 0;
		}
		
		// Add new randomly generate tests
		for (int i = 0; i < remain; i++) {
			T tch = null;
			if (this.getCoveredGoals().size() == 0 || Randomness.nextBoolean()) {
				tch = this.chromosomeFactory.getChromosome();
				tch.setChanged(true);
			} else {
				tch = (T) Randomness.choice(this.getSolutions()).clone();
				tch.mutate(); tch.mutate(); // TODO why is it mutated twice?
			}
			if (tch.isChanged()) {
				tch.updateAge(this.currentIteration);
				this.calculateFitness(tch);
//				if (this.getNumberOfCoveredGoals() > prevCoveredGoals) {
//					situation = Situation.RANDOM;
//					prevCoveredGoals = this.getNumberOfCoveredGoals();
//				}
				this.population.add(tch);
			}
		}

		// Get 50 populations based on ranking
		MutationPositionDiscriminator.discriminator.decreaseFrozenIteration();
		
		/**
		 * we can debug by setting break points here.
		 */
		printBestFitness();
		collectExceptionResults(this.currentIteration);

		this.currentIteration++;
		// logger.debug("N. fronts = {}", ranking.getNumberOfSubfronts());
		// logger.debug("1* front size = {}", ranking.getSubfront(0).size());
		logger.debug("Covered goals = {}", goalsManager.getCoveredGoals().size());
		logger.debug("Current goals = {}", goalsManager.getCurrentGoals().size());
		logger.debug("Uncovered goals = {}", goalsManager.getUncoveredGoals().size());
	}

	private void printBestFitness() {
		
		if(Properties.PRINT_FITNESS) {
			Map<FitnessFunction<T>, Double> bestMap = new HashMap<>();
			Map<FitnessFunction<T>, T> bestTestMap = new HashMap<>();

			for (T t : this.population) {
				if (t instanceof TestChromosome) {
					for (FitnessFunction<T> ff : this.goalsManager.getCurrentGoals()) {
						Double fitness = ff.getFitness(t);
						Double bestSoFar = bestMap.get(ff);
						if (bestSoFar == null) {
							bestSoFar = fitness;
							bestTestMap.put(ff, t);
						} else if (bestSoFar > fitness) {
							bestSoFar = fitness;
							bestTestMap.put(ff, t);
						}
						bestMap.put(ff, bestSoFar);
					}
				}
			}

			System.out.println(this.currentIteration + "th iteration ========================");
			for (FitnessFunction<T> ff : bestMap.keySet()) {
				Double fitness = bestMap.get(ff);
				T t = bestTestMap.get(ff);
				ff.getFitness(t);
				System.out.print(ff + ":");
				System.out.println(fitness);
				System.currentTimeMillis();
			}
		}

	}
	
	/**
	 * Returns a map from FitnessFunction (uncovered branch) to highest scoring TestChromosome.
	 * 
	 * @param bestTestMap
	 */
	private Map<FitnessFunction<T>, T> generateBestTestMap() {
		Map<FitnessFunction<T>, Double> goalToBestFitnessScore = new HashMap<>();
		Map<FitnessFunction<T>, T> goalToBestTest = new HashMap<>();
		for (T test : this.population) {
			boolean isTestChromosome = test instanceof TestChromosome;
			if (isTestChromosome) {
				// We compute the fitness functions for pair of <FitnessFunction, TestChromosome>
				// We keep the highest scoring TestChromosome for each FitnessFunction in our map.
				for (FitnessFunction<T> fitnessFunction : this.goalsManager.getCurrentGoals()) {
					Double fitness = fitnessFunction.getFitness(test);
					Double bestFitnessScoreSoFar = goalToBestFitnessScore.get(fitnessFunction);
					
					boolean isFirstTimeSeeingThisFitnessFunction = bestFitnessScoreSoFar == null;
					
					// Lower fitness score is better.
					boolean isCurrentTestBetterThanPreviousBest = bestFitnessScoreSoFar > fitness;
					if (isFirstTimeSeeingThisFitnessFunction || isCurrentTestBetterThanPreviousBest) {
						goalToBestFitnessScore.put(fitnessFunction, fitness);
						goalToBestTest.put(fitnessFunction, test);
						continue;
					}
				}
			}
		}
		return goalToBestTest;
	}
	
	private void collectExceptionResults(int currentIteration) {
		Map<FitnessFunction<T>, T> bestTestMap = generateBestTestMap();
		
		for (Entry<FitnessFunction<T>, T> entry : bestTestMap.entrySet()) {
			FitnessFunction<T> fitnessFunction = entry.getKey();
			T hopefullyATestChromosome = entry.getValue();
			boolean isTestChromosome = hopefullyATestChromosome instanceof TestChromosome;
			
			if (!isTestChromosome) {
				// What do we do?
				continue;
			}
			
			TestChromosome testChromosome = (TestChromosome) hopefullyATestChromosome;
			exceptionResult.updateExceptionResult(currentIteration, fitnessFunction, testChromosome);
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void generateSolution() {
		RandomLengthTestFactory.legitimizationSuccess = 0;
		RandomLengthTestFactory.legitimizationTrials = 0;
		
		logger.debug("executing generateSolution function");

		this.goalsManager = new MultiCriteriaManager<>(this.fitnessFunctions);
//		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph();
		MutationPositionDiscriminator.discriminator.setPurpose(this.goalsManager.getCurrentGoals());

		this.goalsManager.getCoveredGoals().clear();
		LoggingUtils.getEvoLogger().info("* Initial Number of Goals in DynMOSA = "
				+ this.goalsManager.getCurrentGoals().size() + " / " + this.getUncoveredGoals().size());

		logger.debug("Initial Number of Goals = " + this.goalsManager.getCurrentGoals().size());

		// initialize population
		long t1 = System.currentTimeMillis();
		TestCaseLegitimizer.startTime = t1;
		if (this.population.isEmpty()) {
			this.initializePopulation();
		}
		long t2 = System.currentTimeMillis();
		this.initializationOverhead = t2 - t1;
		
		// update current goals
		this.calculateFitness(true);
		T suite = getBestIndividual();
		this.initialCoverage = suite.getCoverage();
		logger.warn("initial coverage: " + this.initialCoverage);
		logger.warn("initialization overhead: " + this.initializationOverhead);

		// Calculate dominance ranks and crowding distance
		this.rankingFunction.computeRankingAssignment(this.population, this.goalsManager.getCurrentGoals());

		for (int i = 0; i < this.rankingFunction.getNumberOfSubfronts(); i++) {
			this.distance.fastEpsilonDominanceAssignment(this.rankingFunction.getSubfront(i),
					this.goalsManager.getCurrentGoals());
		}

		// next generations
		while (!isFinished() && this.goalsManager.getUncoveredGoals().size() > 0) {
			MutationPositionDiscriminator.discriminator.setPurpose(this.goalsManager.getCurrentGoals());

			if(Properties.ENABLE_BRANCH_ENHANCEMENT) {
				exceptionBranchEnhancer.setGoalManager(goalsManager);
				exceptionBranchEnhancer.updatePopulation(population);
				exceptionBranchEnhancer.enhanceBranchGoals();				
			}
			
			this.evolve();
			this.notifyIteration();
		}

		logger.warn("legitimizationSuccess: " + RandomLengthTestFactory.legitimizationSuccess);
		logger.warn("legitimizationTrials: " + RandomLengthTestFactory.legitimizationTrials);
		
		this.notifySearchFinished();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void calculateFitness(T c) {

		String oldGoalFingerprint = this.goalsManager.getCurrentGoalFingerPrint();

		this.goalsManager.calculateFitness(c);
		this.notifyEvaluation(c);

		String newGoalFingerprint = this.goalsManager.getCurrentGoalFingerPrint();

		if (!oldGoalFingerprint.equals(newGoalFingerprint)) {
			MutationPositionDiscriminator.discriminator.resetFrozenIteartion();
		} else {
//			MutationPositionDiscriminator.discriminator.decreaseFrozenIteration();
		}

	}
	
}
