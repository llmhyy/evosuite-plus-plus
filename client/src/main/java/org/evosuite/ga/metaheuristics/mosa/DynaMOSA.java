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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.OnlyCrowdingComparator;
import org.evosuite.ga.metaheuristics.mosa.structural.BranchFitnessGraph;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.ga.metaheuristics.mosa.structural.StructuralGoalManager;
import org.evosuite.ga.operators.ranking.CrowdingDistance;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.runtime.mock.MockFramework;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.transform.impl.AddDelegateTransformer;

import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;

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
	private static final double EXCEPTION_THRESHOLD = 0.9;
	private static int waitIterations = 0;

	/** Manager to determine the test goals to consider at each generation */
	protected StructuralGoalManager<T> goalsManager = null;

	protected CrowdingDistance<T> distance = new CrowdingDistance<T>();

	protected Map<FitnessFunction<T>, Integer> goalsFreq = new LinkedHashMap<>();

	/**
	 * map for all covered goals
	 */
	Map<FitnessFunction<T>, Integer> coveredTimesMap = new HashMap<>();

	/**
	 * goals added to the tree
	 */
	Set<FitnessFunction<T>> addedGoals = new HashSet<FitnessFunction<T>>();
	FitnessFunction<T> ff = null;
	// GoalToEnhance -> <GoalToAdd, ExceptionTimes>
	Map<FitnessFunction<T>, Map<FitnessFunction<T>, Integer>> exceptionMap = new HashMap<>();
	int rootCoveredTimes = 0;
	Map<FitnessFunction<T>, Integer> rootExceptionMap = new HashMap<>();
	Set<FitnessFunction<T>> goalsToAdd = new HashSet<FitnessFunction<T>>();
	FitnessFunction<T> edgeInTarget = null;

	/**
	 * Constructor based on the abstract class {@link AbstractMOSA}.
	 * 
	 * @param factory
	 */
	public DynaMOSA(ChromosomeFactory<T> factory) {
		super(factory);
	}

	/** {@inheritDoc} */
	@Override
	protected void evolve() {
		List<T> offspringPopulation = this.breedNextGeneration();

		// Create the union of parents and offSpring
		List<T> union = new ArrayList<T>(this.population.size() + offspringPopulation.size());
		union.addAll(this.population);
		union.addAll(offspringPopulation);

		// Ranking the union
		logger.debug("Union Size = {}", union.size());

		// Ranking the union using the best rank algorithm (modified version of the non
		// dominated sorting algorithm
		this.rankingFunction.computeRankingAssignment(union, this.goalsManager.getCurrentGoals());

		// let's form the next population using "preference sorting and non-dominated
		// sorting" on the
		// updated set of goals
		int remain = Math.max(Properties.POPULATION, this.rankingFunction.getSubfront(0).size());
		int index = 0;
		List<T> front = null;
		this.population.clear();

		// Obtain the next front
		front = this.rankingFunction.getSubfront(index);

		while ((remain > 0) && (remain >= front.size()) && !front.isEmpty()) {
			// Assign crowding distance to individuals
			this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());

			// Add the individuals of this front
			this.population.addAll(front);

			// Decrement remain
			remain = remain - front.size();

			// Obtain the next front
			index++;
			if (remain > 0) {
				front = this.rankingFunction.getSubfront(index);
			}
		}

		// Remain is less than front(index).size, insert only the best one
		if (remain > 0 && !front.isEmpty()) { // front contains individuals to insert
			this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());
			Collections.sort(front, new OnlyCrowdingComparator());
			for (int k = 0; k < remain; k++) {
				this.population.add(front.get(k));
			}

			remain = 0;
		}

		// Get 50 populations based on ranking
		MutationPositionDiscriminator.discriminator.decreaseFrozenIteration();
		printBestFitness();

		this.currentIteration++;
		// logger.debug("N. fronts = {}", ranking.getNumberOfSubfronts());
		// logger.debug("1* front size = {}", ranking.getSubfront(0).size());
		logger.debug("Covered goals = {}", goalsManager.getCoveredGoals().size());
		logger.debug("Current goals = {}", goalsManager.getCurrentGoals().size());
		logger.debug("Uncovered goals = {}", goalsManager.getUncoveredGoals().size());
	}

	private void printBestFitness() {
		Map<FitnessFunction<T>, Double> bestMap = new HashMap<>();
		Map<FitnessFunction<T>, T> bestTestMap = new HashMap<>();

		for (T t : this.population) {
			if (t instanceof TestChromosome) {
				for (FitnessFunction<T> ff : this.goalsManager.getCurrentGoals()) {
					if (t instanceof TestChromosome) {
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
		}

		System.out.println(this.currentIteration + "th iteration ========================");
		for (FitnessFunction<T> ff : bestMap.keySet()) {
			Double fitness = bestMap.get(ff);
			System.out.print(ff + ":");
			System.out.println(fitness);
			System.currentTimeMillis();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void generateSolution() {
		logger.debug("executing generateSolution function");

		for (FitnessFunction<T> ff : getTotalGoals()) {
			goalsFreq.put(ff, 0);
		}

//		for (FitnessFunction<T> ff : this.fitnessFunctions) {
//			occurrenceMap.put(ff, 0);
//		}

		this.goalsManager = new MultiCriteriaManager<>(this.fitnessFunctions);
//		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph();
		MutationPositionDiscriminator.discriminator.currentGoals = this.goalsManager.getCurrentGoals();

		LoggingUtils.getEvoLogger().info("* Initial Number of Goals in DynMOSA = "
				+ this.goalsManager.getCurrentGoals().size() + " / " + this.getUncoveredGoals().size());

		logger.debug("Initial Number of Goals = " + this.goalsManager.getCurrentGoals().size());

		// initialize population
		if (this.population.isEmpty()) {
			this.initializePopulation();
		}

		// update current goals
		this.calculateFitness();

		// Calculate dominance ranks and crowding distance
		this.rankingFunction.computeRankingAssignment(this.population, this.goalsManager.getCurrentGoals());

		for (int i = 0; i < this.rankingFunction.getNumberOfSubfronts(); i++) {
			this.distance.fastEpsilonDominanceAssignment(this.rankingFunction.getSubfront(i),
					this.goalsManager.getCurrentGoals());
		}

		// next generations
		while (!isFinished() && this.goalsManager.getUncoveredGoals().size() > 0) {
			MutationPositionDiscriminator.discriminator.setPurpose(this.goalsManager.getCurrentGoals());

			this.branchEnhancement();
			this.evolve();
			this.notifyIteration();
		}

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

	protected void branchEnhancement() {

		for (T population : this.population) {

			ExecutionResult executionResult = ((TestChromosome) population).getLastExecutionResult();
			Map<Integer, Integer> falseGoals = executionResult.getTrace().getCoveredFalse();
			Map<Integer, Integer> trueGoals = executionResult.getTrace().getCoveredTrue();
			Collection<Throwable> allExceptions = executionResult.getAllThrownExceptions();

			// className -> methodName -> lineNumber -> coveredTimes
			Map<String, Map<String, Map<Integer, Integer>>> coverageMap = executionResult.getTrace().getCoverageData();

			// target method is covered
			if (coverageMap.get(Properties.TARGET_CLASS) != null) {
				if (coverageMap.get(Properties.TARGET_CLASS).get(Properties.TARGET_METHOD) != null) {
					// root is covered
					rootCoveredTimes++;
				} else {
					continue;
				}
			} else {
				continue;
			}

			addCoveredTimes(falseGoals, false);
			addCoveredTimes(trueGoals, true);

			if (!allExceptions.isEmpty()) {
				Throwable thrownException = allExceptions.iterator().next();
				goalsToAdd = new HashSet<FitnessFunction<T>>();

				retrieveGoalsToAdd(thrownException);
//				population.getFitnessValues()
				if (goalsToAdd != null && !goalsToAdd.isEmpty()) {

					for (FitnessFunction<T> goalToAdd : goalsToAdd) {
						boolean foundInTarget = getEdgeInTargetMethod(thrownException);

						// Invoked by target
						if (foundInTarget) {
							// Root
							if (edgeInTarget == null) {
								if (rootExceptionMap.get(goalToAdd) == null) {
									rootExceptionMap.put(goalToAdd, 1);
								} else {
									rootExceptionMap.replace(goalToAdd, rootExceptionMap.get(goalToAdd) + 1);
								}

							} else {
								if (exceptionMap.get(edgeInTarget) == null
										|| exceptionMap.get(edgeInTarget).get(goalToAdd) == null) {
									Map<FitnessFunction<T>, Integer> toAddMap = new HashMap<>();
									toAddMap.put(goalToAdd, 1);
									exceptionMap.put(edgeInTarget, toAddMap);
								} else {
									exceptionMap.get(edgeInTarget).replace(goalToAdd,
											exceptionMap.get(edgeInTarget).get(goalToAdd) + 1);
								}
							}
						}

						// Not invoked by target
						// TODO: handle exception before target
						// Some insights: can be parallel, added to the root path, breakthru one by one;
						else {

						}
					}

				}
			}
		}

		checkExceptionProb();

	}

	protected void retrieveGoalsToAdd(Throwable exception) {
		Set<ControlDependency> cds = new HashSet<ControlDependency>();
		MockFramework.disable();
		StackTraceElement[] stack = exception.getStackTrace();
		if (stack != null && stack.length != 0 && stack[0] != null) {
			String className = stack[0].getClassName();
			int lineNum = stack[0].getLineNumber();
			List<BytecodeInstruction> insList = BytecodeInstructionPool
					.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtClass(className, lineNum);
			for (BytecodeInstruction ins : insList) {
				if (ins.getASMNodeString().contains(exception.getClass().getSimpleName())) {
					cds = ins.getControlDependencies();
					for (ControlDependency cd : cds) {
						// precaution to prevent wrongly add goals
						for (FitnessFunction<T> addedGoal: addedGoals) {
							if (((BranchCoverageTestFitness) addedGoal).getBranch() == cd.getBranch()) {
								return;
							}
						}
						// Add the goal which will not incur this exception
						goalsToAdd.add(createOppFitnessFunction(cd));
//						searchParent(cd);
					}
					break;
				}
			}
		}
	}

	protected boolean getEdgeInTargetMethod(Throwable exception) {
		edgeInTarget = null;
		StackTraceElement[] stack = exception.getStackTrace();

		for (StackTraceElement element : stack) {
			if (element.getClassName().equals(Properties.TARGET_CLASS)
					&& element.getMethodName().equals(Properties.TARGET_METHOD.split(Pattern.quote("("))[0])) {
				// Invoke by target method
				int targetLine = element.getLineNumber();
				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getAllInstructionsAtLineNumber(element.getClassName(), Properties.TARGET_METHOD, targetLine);
				Set<ControlDependency> cds = insList.get(0).getControlDependencies();

				if (!addedGoals.isEmpty()) {
					checkPotentialNewPath();
				}

				// ROOT
				if (cds == null || cds.isEmpty()) {
					edgeInTarget = null;
//					checkPotentialNewPath();
				} else if (cds.size() == 1) {
//					checkPotentialNewPath(cds.iterator().next());
					edgeInTarget = createFitnessFunction(cds.iterator().next());
				} else {
					// Get nearest goal
					for (ControlDependency cd : cds) {
						Set<ControlDependency> parentCds = cd.getBranch().getInstruction().getControlDependencies();
						if (cds.contains(parentCds)) {
							cds.remove(parentCds);
						}
					}
					edgeInTarget = cds.iterator().hasNext() ? createFitnessFunction(cds.iterator().next()) : null;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Add covered times for each goal
	 * 
	 * @param goals
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	protected void addCoveredTimes(Map<Integer, Integer> goals, boolean value) {
		for (Integer goal : goals.keySet()) {
			for (FitnessFunction<T> ff : fitnessFunctions) {
				if (((BranchCoverageTestFitness) ff).getBranchGoal().getId() == goal
						&& ((BranchCoverageTestFitness) ff).getBranchExpressionValue() == value) {
					if (coveredTimesMap.get(ff) == null) {
						coveredTimesMap.put(ff, 1);
					} else {
						coveredTimesMap.replace(ff, coveredTimesMap.get(ff) + 1);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected FitnessFunction<T> createOppFitnessFunction(ControlDependency cd) {
		Branch branch = cd.getBranch();
		boolean value = cd.getBranchExpressionValue();
		return (FitnessFunction<T>) BranchCoverageFactory.createBranchCoverageTestFitness(branch, !value);
	}

	@SuppressWarnings("unchecked")
	protected FitnessFunction<T> createFitnessFunction(ControlDependency cd) {
		Branch branch = cd.getBranch();
		boolean value = cd.getBranchExpressionValue();
		return (FitnessFunction<T>) BranchCoverageFactory.createBranchCoverageTestFitness(branch, value);
	}

	protected void searchParent(ControlDependency cd) {
		if (!cd.getBranch().getInstruction().getControlDependencies().isEmpty()) {
			Set<ControlDependency> parentCds = cd.getBranch().getInstruction().getControlDependencies();
			for (ControlDependency parentCd : parentCds) {
				goalsToAdd.add(createFitnessFunction(parentCd));
				searchParent(parentCd);
			}
		}
	}

	protected void checkExceptionProb() {
		if (this.currentIteration > 30) {

			if (frozen()) {
				reduceFrozenIterations();
				return;
			}

			FitnessFunction<T> goalToAddToRoot = null;
			double sumRoot = 0.0;
			
			// For root
			for (Entry<FitnessFunction<T>, Integer> entry : rootExceptionMap.entrySet()) {
				double maxRoot = 0.0;
				double timesRoot = entry.getValue() * 1.0 / rootCoveredTimes;
				sumRoot += entry.getValue() * 1.0 / rootCoveredTimes;
				if (timesRoot > maxRoot) {
					maxRoot = timesRoot;
					goalToAddToRoot = entry.getKey();
				}
			}

			if (sumRoot > EXCEPTION_THRESHOLD) {
				if (!addedGoals.contains(goalToAddToRoot)) {
					addedGoals.add(goalToAddToRoot);
					updateRootPath(goalToAddToRoot);
					resetFrozenIteration();
				}
			}

			// For normal path
			for (Entry<FitnessFunction<T>, Map<FitnessFunction<T>, Integer>> entry : exceptionMap.entrySet()) {
				int sumNormal = 0;
				int timesNormal = 0;
				int maxNormal = 0;
				int coveredTimes = 0;
				if (coveredTimesMap.keySet().iterator().next() instanceof FBranchTestFitness) {
					coveredTimes = coveredTimesMap.get(new FBranchTestFitness(((BranchCoverageTestFitness) entry.getKey()).getBranchGoal()));
				} else if (coveredTimesMap.keySet().iterator().next() instanceof BranchCoverageTestFitness) {
					coveredTimes = coveredTimesMap.get(((BranchCoverageTestFitness) entry.getKey()));
				}

				FitnessFunction<T> targetGoal = null;
				FitnessFunction<T> parentGoal = null;
				Map<FitnessFunction<T>, Integer> exceptionsInGoal = entry.getValue();
				for (Entry<FitnessFunction<T>, Integer> entry1 : exceptionsInGoal.entrySet()) {
					timesNormal = entry1.getValue();
					sumNormal += timesNormal;
					if (timesNormal > maxNormal) {
						targetGoal = entry1.getKey();
						parentGoal = entry.getKey();
					}
				}

				if (sumNormal * 1.0 / coveredTimes > EXCEPTION_THRESHOLD) {
					if (!addedGoals.contains(targetGoal)) {
						addedGoals.add(targetGoal);
						updateNormalPath(targetGoal, parentGoal);
						resetFrozenIteration();
					}
				}
			}
		}
	}

	private void reduceFrozenIterations() {
		waitIterations--;
	}

	private boolean frozen() {
		return waitIterations > 0;
	}

	private void resetFrozenIteration() {
		waitIterations = 10;
	}

	protected void updateRootPath(FitnessFunction<T> goalToAdd) {
		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph().updateRoot(goalToAdd);
		this.goalsManager.getCurrentGoals().clear();
		this.goalsManager.getCurrentGoals().add(goalToAdd);
		rootExceptionMap.clear();
		rootCoveredTimes = 0;
	}

	protected void updateNormalPath(FitnessFunction<T> goalToAdd, FitnessFunction<T> parentGoal) {
		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph().updatePath(goalToAdd, parentGoal);
		this.goalsManager.getCurrentGoals().add(goalToAdd);
//		for (FitnessFunction<T> child : graph.getStructuralChildren(goalToAdd)) {
//			this.goalsManager.getCurrentGoals().remove(child);
//		}
		exceptionMap.get(parentGoal).clear();
		coveredTimesMap.remove(parentGoal);
	}

	/**
	 * check parent after adding a goal
	 * 
	 * @return
	 */
	protected int checkPotentialNewPath() {
		Set<BytecodeInstruction> allExits = GraphPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getRawCFG(Properties.TARGET_CLASS, Properties.TARGET_METHOD).determineAllExitPoints();
		Set<BytecodeInstruction> exits = GraphPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getRawCFG(Properties.TARGET_CLASS, Properties.TARGET_METHOD).determineExitPoints();
		Set<BytecodeInstruction> throwExits = allExits;
		throwExits.removeAll(exits);
		for (BytecodeInstruction exit : throwExits) {
			FitnessFunction<T> goal = createFitnessFunction(exit.getControlDependencies().iterator().next());
			if (!addedGoals.contains(goal)) {
				while (exit.getControlDependencies().iterator().next() != null) {

				}
			}

			// Inside same method
			while (exit.getControlDependencies().iterator().hasNext()) {

//					if (exit.getControlDependentBranch().get)
				exit.getControlDependencies().iterator().next().getBranch().getInstruction().getControlDependencies();
			}
		}
		return rootCoveredTimes;
	}
}
