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

	// Goal -> <GoalToAdd, ExceptionTimes>
	Map<FitnessFunction<T>, Map<FitnessFunction<T>, Integer>> exceptionMap = new HashMap<>();
	Map<FitnessFunction<T>, Integer> coveredMap = new HashMap<>();
	Map<FitnessFunction<T>, Double> exceptionP = new HashMap<>();

	int rootCovered = 0;

	Map<FitnessFunction<T>, Integer> rootExceptionMap = new HashMap<>();
	Map<FitnessFunction<T>, Double> rootExceptionP = new HashMap<>();

//	Set<Branch> rootBranchs = new HashSet<Branch>();
	Set<FitnessFunction<T>> addedGoals = new HashSet<FitnessFunction<T>>();

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

//			this.branchEnhancement();
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

//	private void branchEnhancement1() {
//		for (T population : this.population) {
//			Collection<Throwable> exceptionsCollections = ((TestChromosome) population).getLastExecutionResult()
//					.getAllThrownExceptions();
//			for (Throwable throwable : exceptionsCollections) {
//				if (!(throwable instanceof NullPointerException)) {
//					System.currentTimeMillis();
//				}
//			}
//		}
//		FitnessFunction<T> prevFitnessFunction = null;
//		for (FitnessFunction<T> ff : this.goalsManager.getCurrentGoals()) {
//			goalsFreq.replace(ff, goalsFreq.get(ff) + 1);
//			// Cannot breakthrough a goal in 30 iterations
//			if (goalsFreq.get(ff) >= 30) {
//				// Check if potential exception situations occur
//				if (prevFitnessFunction != null) {
//					Branch targetBranch = ((BranchCoverageTestFitness) prevFitnessFunction).getBranchGoal().getBranch();
//					Branch currentBranch = ((BranchCoverageTestFitness) ff).getBranchGoal().getBranch();
//					if (targetBranch == currentBranch) {
//						for (T population : this.population) {
//							Map<String, Map<String, Map<Integer, Integer>>> coverageDataMap = ((TestChromosome) population)
//									.getLastExecutionResult().getTrace().getCoverageData();
//							for (Map.Entry<String, Map<String, Map<Integer, Integer>>> entry : coverageDataMap
//									.entrySet()) {
//								if (entry != null) {
//									String className = entry.getKey();
//									for (Map.Entry<String, Map<Integer, Integer>> entry2 : entry.getValue()
//											.entrySet()) {
//										if (entry2 != null) {
//											String methodName = entry2.getKey();
//											Set<Integer> lineNums = entry2.getValue().keySet();
//
//											if (!hasVisitParentGoal(targetBranch, lineNums, methodName)) {
//												return;
//											}
//
//											for (Map.Entry<Integer, Integer> entry3 : entry2.getValue().entrySet()) {
//												if (entry3 != null) {
//													Integer lineNum = entry3.getKey();
//													checkPotentialException(className, methodName, lineNum, ff);
//												}
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			prevFitnessFunction = ff;
//		}
//	}
//
//	private boolean hasVisitParentGoal(Branch targetBranch, Set<Integer> lineNums, String methodName) {
//		// Visit the goal?
//		int parentTarget;
//		if (targetBranch.getInstruction().getControlDependentBranch() != null) {
//			BytecodeInstruction fatherBranch = targetBranch.getInstruction().getControlDependentBranch()
//					.getInstruction();
//			parentTarget = fatherBranch.getLineNumber();
//			for (Integer line : lineNums) {
//				if (line == parentTarget + 1 && methodName == fatherBranch.getMethodName()) {
//					return true;
//				}
//			}
//			return false;
//		}
//		return true;
//	}
//
//	@SuppressWarnings("unchecked")
//	private void checkPotentialException(String className, String methodName, int lineNum,
//			FitnessFunction<T> fitnessFunction) {
//
//		List<BytecodeInstruction> insList = BytecodeInstructionPool
//				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
//				.getAllInstructionsAtLineNumber(className, methodName, lineNum);
//		for (BytecodeInstruction ins : insList) {
//			if (ins.getASMNodeString().contains("Exception")) {
//				Set<Branch> branchesToAdd = ins.getControlDependentBranches();
//				for (Branch branch : branchesToAdd) {
//					if (!branch.isInstrumented()) {
//						this.goalsManager.updateCurrentGoals((FitnessFunction<T>) BranchCoverageFactory
//								.createBranchCoverageTestFitness(branch, true));
//						this.goalsManager.updateCurrentGoals((FitnessFunction<T>) BranchCoverageFactory
//								.createBranchCoverageTestFitness(branch, false));
//						goalsFreq.replace(fitnessFunction, 0);
//					}
//				}
//			}
//		}
//	}

	protected void branchEnhancement() {
//		Set<Integer> branchGoalsId = new HashSet<Integer>();
//		List<Integer> branchLineNums = new ArrayList<Integer>();

//		for (FitnessFunction<T> ff : this.fitnessFunctions) {
//			branchGoalsId.add(((BranchCoverageTestFitness) ff).getBranchGoal().getId());
//			branchLineNums.add(((BranchCoverageTestFitness) ff).getBranchGoal().getLineNumber());
//		}

		for (T population : this.population) {

			ExecutionResult executionResult = ((TestChromosome) population).getLastExecutionResult();
			Map<Integer, Integer> falseGoals = executionResult.getTrace().getCoveredFalse();
			Map<Integer, Integer> trueGoals = executionResult.getTrace().getCoveredTrue();
			Collection<Throwable> allExceptions = executionResult.getAllThrownExceptions();

			// className -> methodName -> lineNumber -> 1
			Map<String, Map<String, Map<Integer, Integer>>> coverageMap = executionResult.getTrace().getCoverageData();

			// target method is covered
			if (coverageMap.get(Properties.TARGET_CLASS) != null) {
				if (coverageMap.get(Properties.TARGET_CLASS).get(Properties.TARGET_METHOD) != null) {
					rootCovered++;
				} else {
					continue;
				}
			} else {
				continue;
			}

			addCoveredTimes(falseGoals, false);
			addCoveredTimes(trueGoals, true);

			for (Throwable exception : allExceptions) {
				Set<FitnessFunction<T>> goalsToAdd = detectExceptions(exception);

				if (goalsToAdd != null && !goalsToAdd.isEmpty()) {

//					Branch targetBranch = getCorrespondingBranch(exception);
//					int toAddId = toAdd.getActualBranchId();
					FitnessFunction<T> goalToAdd = goalsToAdd.iterator().next();
					int result = getEdgeInTargetMethod(exception);

					if (result == 1) {
						// ROOT
						if (edgeInTarget == null) {
							if (rootExceptionMap.get(goalToAdd) == null) {
								rootExceptionMap.put(goalToAdd, 1);
							} else {
								rootExceptionMap.replace(goalToAdd, rootExceptionMap.get(goalToAdd) + 1);
							}

						} else {
//						FitnessFunction<T> ff = getCorrespondingGoal(targetBranch, coverageMap);
//						if (ff != null) {
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

				}
			}

//			calculateRootProb();

		}

		calculateProb();

	}

	protected Set<FitnessFunction<T>> detectExceptions(Throwable exception) {
		int lineNum = 0;
		String className = null;
		Set<ControlDependency> dependencies = new HashSet<ControlDependency>();
		Set<FitnessFunction<T>> goalsToAdd = new HashSet<FitnessFunction<T>>();
		MockFramework.disable();
		StackTraceElement[] stack = exception.getStackTrace();
		if (stack != null && stack.length != 0 && stack[0] != null) {
			className = stack[0].getClassName();
			lineNum = stack[0].getLineNumber();
			if (className.equals(Properties.TARGET_CLASS)) {
				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getAllInstructionsAtClass(className, lineNum);
				for (BytecodeInstruction ins : insList) {
					if (ins.getASMNodeString().contains(exception.getClass().getSimpleName())) {
						dependencies = ins.getControlDependencies();
						for (Iterator<ControlDependency> dependencyIterator = dependencies
								.iterator(); dependencyIterator.hasNext();) {
							ControlDependency dependency = dependencyIterator.next();
//							goalsToAdd.add((FitnessFunction<T>) BranchCoverageFactory.createBranchCoverageTestFitness(dependency));
							goalsToAdd.add(createRevFitnessFunction(dependency));

						}
						return goalsToAdd;
					}
				}
			}
		}
		return goalsToAdd;
	}

//	protected Branch getCorrespondingBranch(Throwable exception) {
//		StackTraceElement[] stack = exception.getStackTrace();
//		for (StackTraceElement element : stack) {
//			if (element.getClassName().equals(Properties.TARGET_CLASS)
//					&& element.getMethodName().equals(Properties.TARGET_METHOD.split(Pattern.quote("("))[0])) {
//				int targetLine = element.getLineNumber();
//				List<BytecodeInstruction> insList = BytecodeInstructionPool
//						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
//						.getAllInstructionsAtLineNumber(element.getClassName(), Properties.TARGET_METHOD, targetLine);
//				Set<ControlDependency> dependencies = insList.get(0).getControlDependencies();
//				
//				//ROOT
//				if (dependencies == null || dependencies.isEmpty()) {
//					return null;
//				} else {
//					
//				}
//				return insList.get(0).getControlDependentBranch();
////				insList.get(0).getControlDependentBranches()
//			}
//		}
//		return null;
//	}

//	protected FitnessFunction<T> getCorrespondingGoal(Branch target,
//			Map<String, Map<String, Map<Integer, Integer>>> coverageMap) {
//		int targetLine = target.getInstruction().getLineNumber() + 1;
//		for (Map.Entry<String, Map<String, Map<Integer, Integer>>> entry : coverageMap.entrySet()) {
//			String className = entry.getKey();
//			if (className.equals(Properties.TARGET_CLASS)) {
//				for (Map.Entry<String, Map<Integer, Integer>> entry2 : entry.getValue().entrySet()) {
//					String methodName = entry2.getKey();
//					if (methodName.equals(Properties.TARGET_METHOD)) {
//						for (Map.Entry<Integer, Integer> entry3 : entry2.getValue().entrySet()) {
//							if (entry3.getKey() == targetLine) {
//								for (FitnessFunction<T> ff : fitnessFunctions) {
//									if (((BranchCoverageTestFitness) ff).getBranch() == target
//											&& ((BranchCoverageTestFitness) ff).getBranchExpressionValue() == false) {
//										return ff;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}
	protected int getEdgeInTargetMethod(Throwable exception) {
		edgeInTarget = null;
		StackTraceElement[] stack = exception.getStackTrace();

//		if (!addedGoals.isEmpty()) {
//		List<BytecodeInstruction> insList1 = BytecodeInstructionPool
//				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
//				.getAllInstructionsAtClass(stack[0].getClassName(), stack[0].getLineNumber());
//		String method = insList1.get(0).getMethodName();
//
//		}
//		}
//		GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getRawCFG(Properties.TARGET_CLASS,Properties.TARGET_METHOD).determineMethodCalls().get(0).;

		for (StackTraceElement element : stack) {
			if (element.getClassName().equals(Properties.TARGET_CLASS)
					&& element.getMethodName().equals(Properties.TARGET_METHOD.split(Pattern.quote("("))[0])) {
				int targetLine = element.getLineNumber();
				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getAllInstructionsAtLineNumber(element.getClassName(), Properties.TARGET_METHOD, targetLine);
				Set<ControlDependency> dependencies = insList.get(0).getControlDependencies();

				
				if (!addedGoals.isEmpty())
					checkPotentialNewPath();
				
				// ROOT
				if (dependencies == null || dependencies.isEmpty()) {
					edgeInTarget = null;
					checkPotentialNewPath();
				} else if (dependencies.size() == 1) {
//					checkPotentialNewPath(dependencies.iterator().next());
					edgeInTarget = createFitnessFunction(dependencies.iterator().next());
				} else {
					// Get nearest goal
					Iterator<ControlDependency> dependencyIterator = dependencies.iterator();
					while (dependencyIterator.hasNext()) {
						Set<ControlDependency> cds = dependencyIterator.next().getBranch().getInstruction()
								.getControlDependencies();
						if (dependencies.contains(cds))
							dependencies.remove(cds);
					}
					edgeInTarget = dependencies.iterator().hasNext()
							? createFitnessFunction(dependencies.iterator().next())
							: null;
				}
				return 1;
			}
		}
		return -1;
	}

	protected void addCoveredTimes(Map<Integer, Integer> goals, boolean value) {
		for (Integer goal : goals.keySet()) {
			for (FitnessFunction<T> ff : fitnessFunctions) {
				if (((BranchCoverageTestFitness) ff).getBranchGoal().getId() == goal
						&& ((BranchCoverageTestFitness) ff).getBranchExpressionValue() == value) {
					if (coveredMap.get(ff) == null) {
						coveredMap.put(ff, 1);
					} else {
						coveredMap.replace(ff, coveredMap.get(ff) + 1);
					}
				}
			}
		}
	}

	protected FitnessFunction<T> createRevFitnessFunction(ControlDependency cd) {
		Branch branch = cd.getBranch();
		boolean value = cd.getBranchExpressionValue();
		return (FitnessFunction<T>) BranchCoverageFactory.createBranchCoverageTestFitness(branch, !value);
	}

	protected FitnessFunction<T> createFitnessFunction(ControlDependency cd) {
		Branch branch = cd.getBranch();
		boolean value = cd.getBranchExpressionValue();
		return (FitnessFunction<T>) BranchCoverageFactory.createBranchCoverageTestFitness(branch, value);
	}

	protected void calculateProb() {
		if (this.currentIteration > 30) {

			if (waitIterations > 0) {
				waitIterations--;
				return;
			}
			
			double sumRoot = 0.0;
			double timesRoot = 0.0;
			double maxRoot = 0.0;
			FitnessFunction<T> targetGoalRoot = null;
			for (Entry<FitnessFunction<T>, Integer> entry : rootExceptionMap.entrySet()) {
				rootExceptionP.put(entry.getKey(), entry.getValue() * 1.0 / rootCovered);
			}
			for (Entry<FitnessFunction<T>, Double> entry : rootExceptionP.entrySet()) {
				timesRoot = entry.getValue();
				sumRoot += entry.getValue();
				if (timesRoot > maxRoot) {
					maxRoot = timesRoot;
					targetGoalRoot = entry.getKey();
				}
				
				if (sumRoot > EXCEPTION_THRESHOLD) {
					if (!addedGoals.contains(targetGoalRoot)) {
						addedGoals.add(targetGoalRoot);
						updateRootPath(targetGoalRoot);
					}
				}
//				if (entry.getValue() > 0.3) {
//					// FOUND
//					FitnessFunction<T> targetGoalRoot = entry.getKey();
////					Branch targetBranch = BranchPool
////							.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
////							.getBranch(branchId);
////					while (targetBranch.getInstruction().getControlDependentBranch() != null) {
////						rootBranchs.add(targetBranch.getInstruction().getControlDependentBranch());
////					}
////					if (!rootBranchs.contains(targetBranch)) {
////						rootBranchs.add(targetBranch);
////						updateRootPath(targetBranch);
////					}
//					if (!addedGoals.contains(targetGoalRoot)) {
//						addedGoals.add(targetGoalRoot);
//						updateRootPath(targetGoalRoot);
//					}
//				}
			}

			for (Entry<FitnessFunction<T>, Map<FitnessFunction<T>, Integer>> entry : exceptionMap.entrySet()) {
				int sum = 0;
				int times = 0;
				int max = 0;
				int coveredTimes = coveredMap.get(entry.getKey());
				FitnessFunction<T> targetGoal = null;
				FitnessFunction<T> parentGoal = null;
				Map<FitnessFunction<T>, Integer> exceptionsInGoal = entry.getValue();
				for (Entry<FitnessFunction<T>, Integer> entry1 : exceptionsInGoal.entrySet()) {
					times = entry1.getValue();
					sum += times;
					if (times > max) {
						targetGoal = entry1.getKey();
						parentGoal = entry.getKey();
					}
				}
				// FOUND
				if (sum * 1.0 / coveredTimes > EXCEPTION_THRESHOLD) {
//					exceptionP.put(entry1.getKey(), (entry1.getValue() * 1.0 / coveredTimes));	
					if (!addedGoals.contains(targetGoal)) {
						addedGoals.add(targetGoal);
						updateNormalPath(targetGoal, parentGoal);
						waitIterations = 10;
					}
				}
			}
		}
	}

//	protected void updateRootPath(Branch targetBranch) {
//		FitnessFunction<T> ff1 = (FitnessFunction<T>) BranchCoverageFactory
//				.createBranchCoverageTestFitness(targetBranch, true);
//		FitnessFunction<T> ff2 = (FitnessFunction<T>) BranchCoverageFactory
//				.createBranchCoverageTestFitness(targetBranch, false);
//		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph().updateRoot(ff1, ff2);
//		this.goalsManager.getCurrentGoals().clear();
//		this.goalsManager.getCurrentGoals().add(ff1);
//		this.goalsManager.getCurrentGoals().add(ff2);
//	}

	protected void updateRootPath(FitnessFunction<T> goal) {
		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph().updateRoot(goal);
		this.goalsManager.getCurrentGoals().clear();
		this.goalsManager.getCurrentGoals().add(goal);
	}

	protected void updateNormalPath(FitnessFunction<T> goal, FitnessFunction<T> parentGoal) {
		BranchFitnessGraph<T, FitnessFunction<T>> graph = ((MultiCriteriaManager<T>) goalsManager)
				.getBranchFitnessGraph();
		graph.updatePath(goal, parentGoal);
		this.goalsManager.getCurrentGoals().add(goal);
		for (FitnessFunction<T> child : graph.getStructuralChildren(goal)) {
			this.goalsManager.getCurrentGoals().remove(child);
		}
	}

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
				
				//Inside same method
				while (exit.getControlDependencies().iterator().next() != null) {
					
//					if (exit.getControlDependentBranch().get)
					exit.getControlDependencies().iterator().next().getBranch().getInstruction().getControlDependencies();
			}
		}
		return rootCovered;
	}
}
