package org.evosuite.ga.metaheuristics.mosa;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.ga.metaheuristics.mosa.structural.StructuralGoalManager;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.runtime.mock.MockFramework;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;

public class ExceptionBranchEnhancer<T extends Chromosome> {
	private static final double EXCEPTION_THRESHOLD = 0.5;
	
	/**
	 * frequency for all covered goals
	 */
	private Map<FitnessFunction<T>, Integer> goalCoverageFrequency = new HashMap<>();

	/**
	 * handledGoals avoids repetatively working on the same exception goals
	 */
	private Set<FitnessFunction<T>> handledExceptions = new HashSet<FitnessFunction<T>>();

	/**
	 * Exception Goal -> <Corresponding Goal>
	 * note that corresponding goal can be null if it is a root or outsider 
	 * Exception goals are categorized into outsider and insider (in terms of 
	 * target method) Insiders can be categorized into root and non-root.
	 * 
	 * Only non-root insider can have value.
	 */
	private Map<FitnessFunction<T>, FitnessFunction<T>> exceptionGoal2Corresponder = new HashMap<>();

	/**
	 * Exception Goal -> <Corresponding Goal>
	 */
	private Map<FitnessFunction<T>, Integer> exceptionFrequencyMap = new HashMap<>();

	/**
	 * how many times the target method is covered
	 */
	private int targetMethodCoveringTimes = 0;
	/**
	 * how many times the target method is not covered
	 */
	private int totalOutsideExceptionTimes = 0;
	
	
	private List<T> population;
	private StructuralGoalManager<T> goalsManager;
	
	
	public ExceptionBranchEnhancer(StructuralGoalManager<T> goalsManager) {
		super();
		this.goalsManager = goalsManager;
	}
	
	public void updatePopulation(List<T> population) {
		this.population = population;
	}
	
	public void setGoalManager(StructuralGoalManager<T> goalsManager) {
		this.goalsManager = goalsManager;
	}

	public void enhanceBranchGoals() {
		/**
		 * disabling the mock framework so that we can get the actual stack for an
		 * exception
		 */
		MockFramework.disable();

		for (T population : this.population) {
			ExecutionResult executionResult = ((TestChromosome) population).getLastExecutionResult();
			Map<Integer, Integer> falseGoals = executionResult.getTrace().getCoveredFalse();
			Map<Integer, Integer> trueGoals = executionResult.getTrace().getCoveredTrue();
			Collection<Throwable> allExceptions = executionResult.getAllThrownExceptions();

			// className -> methodName -> lineNumber -> coveredTimes
			Map<String, Map<String, Map<Integer, Integer>>> coverageMap = executionResult.getTrace().getCoverageData();

			if (coverageMap.get(Properties.TARGET_CLASS) != null
					&& coverageMap.get(Properties.TARGET_CLASS).get(Properties.TARGET_METHOD) != null) {
				targetMethodCoveringTimes++;
			} else {
				totalOutsideExceptionTimes++;
			}

			/**
			 * TODO here, the branch coverage is not context sensitive.
			 * 
			 * we collect the number of coverage of each goal (i.e., branch) so that we can
			 * detect how many "bombs" between each goal.
			 */
			collectCoveredTimes(falseGoals, false);
			collectCoveredTimes(trueGoals, true);

			/**
			 * record the where the exception happens.
			 */
			if (!allExceptions.isEmpty()) {
				Throwable thrownException = allExceptions.iterator().next();

				StackTraceElement[] stack = thrownException.getStackTrace();
				FitnessFunction<T> newExceptionGoal = createNewGoals(thrownException, stack, 0);

				if (newExceptionGoal != null) {
					FitnessFunction<T> goalInGraph = getCorrespondingGoalInGraph(thrownException, newExceptionGoal);
					exceptionGoal2Corresponder.put(newExceptionGoal, goalInGraph);

					Integer freq = exceptionFrequencyMap.get(newExceptionGoal);
					if (freq == null) {
						freq = new Integer(1);
					} else {
						freq = freq + 1;
					}
					exceptionFrequencyMap.put(newExceptionGoal, freq);

				} else {
					/**
					 * TODO for ziheng, means that the goal for the exception is not instrumented
					 */
				}
			}
		}

		evolveGoalGraphWithException();

		MockFramework.enable();

	}

	private FitnessFunction<T> createNewGoals(Throwable exception, StackTraceElement[] stack, int level) {
		Set<ControlDependency> cds = new HashSet<ControlDependency>();
		if (stack != null && stack.length > level && stack[level] != null) {
			String className = stack[level].getClassName();
			int lineNum = stack[level].getLineNumber();

			List<BytecodeInstruction> insList = BytecodeInstructionPool
					.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtClass(className, lineNum);

			for (BytecodeInstruction ins : insList) {
				if (ins.getASMNodeString().contains(exception.getClass().getSimpleName())) {
					cds = ins.getControlDependencies();

					if (cds != null && cds.size() > 0) {
						for (ControlDependency cd : cds) {
							FitnessFunction<T> fitness = createOppFitnessFunction(cd);
							return fitness;
						}

						break;
					} else {
						FitnessFunction<T> fitness = createNewGoals(exception, stack, level + 1);
						return fitness;
					}
				}
			}
		}

		return null;
	}

	/**
	 * return null if the exception is not incurred from the target method.
	 * 
	 * @param exception
	 * @param newExceptionGoal
	 * @return
	 */
	private FitnessFunction<T> getCorrespondingGoalInGraph(Throwable exception, FitnessFunction<T> newExceptionGoal) {
		StackTraceElement[] stack = exception.getStackTrace();

		for (StackTraceElement element : stack) {
			if (element.getClassName().equals(Properties.TARGET_CLASS)
					&& element.getMethodName().equals(Properties.TARGET_METHOD.split(Pattern.quote("("))[0])) {
				newExceptionGoal.setInTarget(true);
			}

			for (FitnessFunction<T> ff : this.goalsManager.getCurrentGoals()) {
				BranchCoverageGoal branchGoal = getBranch(ff);
				String branchClassName = branchGoal.getBranch().getInstruction().getClassName();
				String branchMethodName = branchGoal.getBranch().getInstruction().getMethodName();
				if (element.getClassName().equals(branchClassName)
						&& element.getMethodName().equals(branchMethodName.split(Pattern.quote("("))[0])) {
					List<BytecodeInstruction> insList = BytecodeInstructionPool
							.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
							.getAllInstructionsAtLineNumber(element.getClassName(), branchMethodName,
									element.getLineNumber());
					// TODO indicate that the method is not instrumented
					if (insList == null) {
						System.currentTimeMillis();
						continue;
					}

					Set<ControlDependency> cds = insList.get(0).getControlDependencies();

					if (cds.isEmpty()) {
						return null;
					}

					/**
					 * Choose an arbitrary control dependency
					 */
					for (ControlDependency cd : cds) {
						if (cd.getBranch().equals(branchGoal.getBranch())
								&& cd.getBranchExpressionValue() == branchGoal.getValue()) {
							return ff;
						}
					}
				}
			}
		}

		System.currentTimeMillis();

		return null;
	}

	private BranchCoverageGoal getBranch(FitnessFunction<T> ff) {
		if (ff instanceof FBranchTestFitness) {
			return ((FBranchTestFitness) ff).getBranchGoal();
		} else if (ff instanceof BranchCoverageTestFitness) {
			return ((BranchCoverageTestFitness) ff).getBranchGoal();
		}

		return null;
	}

	/**
	 * @param goals
	 * @param value
	 */
	protected void collectCoveredTimes(Map<Integer, Integer> goals, boolean value) {
		for (Integer goal : goals.keySet()) {
			for (FitnessFunction<T> ff : this.goalsManager.getCurrentGoals()) {
				if (((BranchCoverageTestFitness) ff).getBranchGoal().getId() == goal
						&& ((BranchCoverageTestFitness) ff).getBranchExpressionValue() == value) {
					if (goalCoverageFrequency.get(ff) == null) {
						goalCoverageFrequency.put(ff, 1);
					} else {
						goalCoverageFrequency.replace(ff, goalCoverageFrequency.get(ff) + 1);
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected FitnessFunction<T> createOppFitnessFunction(ControlDependency cd) {
		Branch branch = cd.getBranch();
		boolean value = cd.getBranchExpressionValue();
		Class clazz = checkCriterion();
		BranchCoverageGoal goal = new BranchCoverageGoal(branch, !value, cd.getBranch().getClassName(),
				cd.getBranch().getMethodName());
		if (clazz.equals(BranchCoverageTestFitness.class)) {
			return (FitnessFunction<T>) BranchCoverageFactory.createBranchCoverageTestFitness(branch, !value);
		} else if (clazz.equals(FBranchTestFitness.class)) {
			return (FitnessFunction<T>) new FBranchTestFitness(goal);
		} else {
			return null;
		}
	}

	protected void evolveGoalGraphWithException() {
		Map<FitnessFunction<T>, Double> exceptionOccuringProbability = updateExceptionOcurringProbability(
				exceptionGoal2Corresponder, exceptionFrequencyMap, goalCoverageFrequency, targetMethodCoveringTimes,
				totalOutsideExceptionTimes);
		/**
		 * find the most frequent occurred outsider exception goal
		 */
		FitnessFunction<T> outsider = findFrequentOutsiderException(exceptionOccuringProbability);
		if(outsider != null && isInevitable(outsider, exceptionOccuringProbability)) {
			FitnessFunction<T> corresponder = exceptionGoal2Corresponder.get(outsider);
			if(corresponder == null) {
				updateRoot(outsider);
			}
			else {
				updatePath(outsider, corresponder);
			}
			handledExceptions.add(outsider);
		}
		/**
		 * find the most frequent occurred insider exception goal
		 */
		else {
			FitnessFunction<T> insider = findFrequentInsiderException(exceptionOccuringProbability);
			if(insider != null) {
				FitnessFunction<T> corresponder = exceptionGoal2Corresponder.get(insider);
				if(corresponder == null) {
					updateRoot(outsider);
				}
				else {
					updatePath(outsider, corresponder);
				}
			}
			handledExceptions.add(insider);
		}
		
		resetStates();
	}
	
	private boolean isInevitable(FitnessFunction<T> outsider, Map<FitnessFunction<T>, Double> exceptionOccuringProbability) {
		return exceptionOccuringProbability.get(outsider) > EXCEPTION_THRESHOLD;
	}

	private FitnessFunction<T> findFrequentInsiderException(
			Map<FitnessFunction<T>, Double> exceptionOccuringProbability) {
		FitnessFunction<T> frequentGoal = null;
		Double freq = -1.0;
		
		for(FitnessFunction<T> exceptionGoal: exceptionOccuringProbability.keySet()) {
			if(!exceptionGoal.isInTarget()) {
				continue;
			}
			
			if(handledExceptions.contains(exceptionGoal)) {
				continue;
			}
			
			if(frequentGoal == null) {
				frequentGoal = exceptionGoal;
				freq = exceptionOccuringProbability.get(exceptionGoal);
			}
			else {
				double newFreq = exceptionOccuringProbability.get(exceptionGoal);
				if(newFreq > freq) {
					frequentGoal = exceptionGoal;
					freq = exceptionOccuringProbability.get(exceptionGoal);
				}
			}
		}
		
		return frequentGoal;
	}

	private FitnessFunction<T> findFrequentOutsiderException(
			Map<FitnessFunction<T>, Double> exceptionOccuringProbability) {
		FitnessFunction<T> frequentGoal = null;
		Double freq = -1.0;
		
		for(FitnessFunction<T> exceptionGoal: exceptionOccuringProbability.keySet()) {
			if(exceptionGoal.isInTarget()) {
				continue;
			}
			
			if(!handledExceptions.contains(exceptionGoal)) {
				continue;
			}
			
			if(frequentGoal == null) {
				frequentGoal = exceptionGoal;
				freq = exceptionOccuringProbability.get(exceptionGoal);
			}
			else {
				double newFreq = exceptionOccuringProbability.get(exceptionGoal);
				if(newFreq > freq) {
					frequentGoal = exceptionGoal;
					freq = exceptionOccuringProbability.get(exceptionGoal);
				}
			}
		}
		
		return frequentGoal;
	}

	private void resetStates() {
		this.exceptionFrequencyMap.clear();
		this.goalCoverageFrequency.clear();
		this.totalOutsideExceptionTimes = 0;
		this.targetMethodCoveringTimes = 0;
	}

	private void updateRoot(FitnessFunction<T> goalToAdd) {
		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph().updateRoot(goalToAdd);
		this.goalsManager.getCurrentGoals().clear();
		this.goalsManager.getCurrentGoals().add(goalToAdd);
	}

	private void updatePath(FitnessFunction<T> goalToAdd, FitnessFunction<T> parentGoal) {
		((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph().updatePath(goalToAdd, parentGoal);
		this.goalsManager.getCurrentGoals().add(goalToAdd);
		for (FitnessFunction<T> child : ((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph()
				.getStructuralChildren(goalToAdd)) {
			this.goalsManager.getCurrentGoals().remove(child);
		}
	}

	private Map<FitnessFunction<T>, Double> updateExceptionOcurringProbability(
			Map<FitnessFunction<T>, FitnessFunction<T>> exceptionGoal2Corresponder,
			Map<FitnessFunction<T>, Integer> exceptionFrequencyMap,
			Map<FitnessFunction<T>, Integer> goalCoverageFrequency, 
			int targetMethodCoveringTimes,
			int totalOutsideExceptionTimes) {
		Map<FitnessFunction<T>, Double> exceptionOccuringProbability = new HashMap<FitnessFunction<T>, Double>();
		for (FitnessFunction<T> exceptionGoal : exceptionGoal2Corresponder.keySet()) {
			FitnessFunction<T> corresponder = exceptionGoal2Corresponder.get(exceptionGoal);

			Integer freq = exceptionFrequencyMap.get(exceptionGoal);
			if (corresponder != null) {
				Integer totalCoverage = goalCoverageFrequency.get(corresponder);
				Double prob = freq * 1.0 / totalCoverage;
				exceptionOccuringProbability.put(exceptionGoal, prob);
			} else {
				Integer totalCoverage = exceptionGoal.isInTarget() ? targetMethodCoveringTimes
						: totalOutsideExceptionTimes;
				Double prob = freq * 1.0 / totalCoverage;
				exceptionOccuringProbability.put(exceptionGoal, prob);
			}
		}

		return exceptionOccuringProbability;
	}
	
	

	@SuppressWarnings("rawtypes")
	private Class checkCriterion() {
		for (Properties.Criterion criterion : Properties.CRITERION) {
			switch (criterion) {
			case FBRANCH:
				return FBranchTestFitness.class;
			case BRANCH:
				return BranchCoverageTestFitness.class;
			default:
				return null;
			}
		}
		return null;
	}

}
