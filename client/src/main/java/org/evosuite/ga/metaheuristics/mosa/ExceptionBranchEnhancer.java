package org.evosuite.ga.metaheuristics.mosa;

import java.util.ArrayList;
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
	 * Goal -> Covered Times
	 */
	private Map<FitnessFunction<T>, Integer> goalCoverageFrequency = new HashMap<>();

	/**
	 * handledGoals avoids repetitively working on the same exception goals
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
	 * Exception Goal -> Occurrence Times
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
	private MultiCriteriaManager<T> goalsManager;
	
	public ExceptionBranchEnhancer(MultiCriteriaManager<T> goalsManager) {
		super();
		this.goalsManager = goalsManager;
	}
	
	public void updatePopulation(List<T> population) {
		this.population = population;
	}
	
	public void setGoalManager(MultiCriteriaManager<T> goalsManager) {
		this.goalsManager = goalsManager;
	}

	public void enhanceBranchGoals() {
		/**
		 * disabling the mock framework so that we can get the actual stack for an
		 * exception
		 */
		MockFramework.disable();

		/**
		 * consider some exception may be called by both target and non-target methods,
		 * temporarily, as long as an exception is triggered by target method, the exception
		 * is considered as an insider.
		 * 
		 * TODO: support context-sensitive fitness.
		 */
		Map<FitnessFunction<T>, FitnessFunction<T>> exceptionGoalInTarget = new HashMap<>();
		
		for (T individual : this.population) {
			ExecutionResult executionResult = ((TestChromosome) individual).getLastExecutionResult();
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
			 * TODO for ziheng, the branch coverage is not context sensitive.
			 * 
			 * we collect the number of coverage of each goal (i.e., branch) so that we can
			 * detect how many "bombs" between each goal.
			 */
			collectCoveredTimes(falseGoals, false);
			collectCoveredTimes(trueGoals, true);
			
			/**
			 * update all covered method times
			 */
			for (String methodName : executionResult.getTrace().getCoveredMethods()) {
				Integer freq = CallBlackList.calledMethods.get(methodName);
				if (freq == null) {
					freq = 0;
				}
				CallBlackList.calledMethods.put(methodName, freq + 1);
			}

			if (!allExceptions.isEmpty()) {
				Throwable thrownException = allExceptions.iterator().next();

				StackTraceElement[] stack = thrownException.getStackTrace();
				FitnessFunction<T> newExceptionGoal = createNewGoals(thrownException, stack, 0);
				
				/**
				 * record what method call can trigger exception
				 */
				StackTraceElement elementToCallException = findElementForException(stack);
				if(elementToCallException != null) {
					String methodSig = covert2Sig(elementToCallException);
					if(methodSig == null) {
						/**
						 * TODO, it means some method is not instrumented.
						 */
					}
					
					Integer freq = CallBlackList.exceptionTriggeringCall.get(methodSig);		
					if(freq == null) {
						freq = 0;
					}
					// methodSig doesn't correspond to any class here
					CallBlackList.exceptionTriggeringCall.put(methodSig, freq+1);
				}
				
				/**
				 * record the where the exception happens.
				 */
				if (newExceptionGoal != null) {
					FitnessFunction<T> goalInGraph = getCorrespondingGoalInGraph(stack, newExceptionGoal);
					/**
					 * TODO: need to improve here to support context sensitive goal. 
					 */
					FitnessFunction<T> prevGoal = exceptionGoalInTarget.get(newExceptionGoal);
					if(prevGoal != null) {
						newExceptionGoal.setInTarget(prevGoal.isInTarget() || newExceptionGoal.isInTarget());						
					}
					
					exceptionGoal2Corresponder.put(newExceptionGoal, goalInGraph);

					Integer freq = exceptionFrequencyMap.get(newExceptionGoal);
					if (freq == null) {
						freq = 0;
					}
					exceptionFrequencyMap.put(newExceptionGoal, freq+1);

				} else {
					/**
					 * TODO for ziheng, means that the goal for the exception is not instrumented
					 */
					
				}
			}
		}

		boolean needToEvolveGoalGraph = needToEvolveGoalGraph();
		
		//after certain iterations
		if(needToEvolveGoalGraph) {
			evolveGoalGraphWithException();			
		}
		
		MockFramework.enable();

	}

	private String covert2Sig(StackTraceElement elementToCallException) {
		String className = elementToCallException.getClassName();
		int lineNumber = elementToCallException.getLineNumber();
		
		List<BytecodeInstruction> insList = 
				BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).
				getAllInstructionsAtClass(className, lineNumber);
		
		if(insList == null) {
			System.currentTimeMillis();
		}
		
		if(!insList.isEmpty()) {
			BytecodeInstruction instruction = insList.get(0);
			return className + "." + instruction.getMethodName();
		}
		
		return null;
	}

	private StackTraceElement findElementForException(StackTraceElement[] stack) {
		StackTraceElement target = null;
		// Not sure what this means
		for(StackTraceElement element: stack) {
			if(element.getClassName().startsWith("sun.")) {
				break;
			}
			target = element;
		}
		
		return target;
	}

	private boolean needToEvolveGoalGraph() {
		for(FitnessFunction<T> goal: this.goalsManager.getCurrentGoals()) {
			if(this.handledExceptions.contains(goal)) {
				return false;
			}
		}
		return true;
	}

	private FitnessFunction<T> createNewGoals(Throwable exception, StackTraceElement[] stack, int level) {
		Set<ControlDependency> cds = new HashSet<ControlDependency>();
		if (stack != null && stack.length > level && stack[level] != null) {
			String className = stack[level].getClassName();
			int lineNum = stack[level].getLineNumber();

			List<BytecodeInstruction> insList = BytecodeInstructionPool
					.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtClass(className, lineNum);

			if(insList != null) {
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
			
		}

		return null;
	}
	
	private BranchCoverageGoal getBranchGoal(FitnessFunction<T> ff) {
		if (ff instanceof FBranchTestFitness) {
			return ((FBranchTestFitness) ff).getBranchGoal();
		} else if (ff instanceof BranchCoverageTestFitness) {
			return ((BranchCoverageTestFitness) ff).getBranchGoal();
		}

		return null;
	}

	/**
	 * return null if the exception is not incurred from the target method.
	 * 
	 * @param stack
	 * @param newExceptionGoal
	 * @return
	 */
	private FitnessFunction<T> getCorrespondingGoalInGraph(StackTraceElement[] stack, FitnessFunction<T> newExceptionGoal) {
		for (StackTraceElement element : stack) {
			if (element.getClassName().equals(Properties.TARGET_CLASS)
					&& element.getMethodName().equals(Properties.TARGET_METHOD.split(Pattern.quote("("))[0])) {
				newExceptionGoal.setInTarget(true);
			}

			List<FitnessFunction<T>> allCorrespondingGolas = new ArrayList<FitnessFunction<T>>();
			allCorrespondingGolas.addAll(this.goalsManager.getCurrentGoals());
			allCorrespondingGolas.addAll(this.goalsManager.getCoveredGoals());
			
			for (FitnessFunction<T> ff : allCorrespondingGolas) {
				BranchCoverageGoal branchGoal = getBranchGoal(ff);
				String branchClassName = branchGoal.getBranch().getInstruction().getClassName();
				String branchMethodName = branchGoal.getBranch().getInstruction().getMethodName();
				if (element.getClassName().equals(branchClassName)
						&& element.getMethodName().equals(branchMethodName.split(Pattern.quote("("))[0])) {
					List<BytecodeInstruction> insList = BytecodeInstructionPool
							.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
							.getAllInstructionsAtLineNumber(element.getClassName(), branchMethodName,
									element.getLineNumber());
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

		return null;
	}

	/**
	 * Update covered times for every goal
	 * @param goals
	 * @param value
	 */
	protected void collectCoveredTimes(Map<Integer, Integer> goals, boolean value) {
		// why use current goals
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
	private FitnessFunction<T> createOppFitnessFunction(ControlDependency cd) {
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

	private void evolveGoalGraphWithException() {
		Map<FitnessFunction<T>, Double> exceptionOccuringProbability = updateExceptionOcurringProbability(
				exceptionGoal2Corresponder, exceptionFrequencyMap, goalCoverageFrequency, targetMethodCoveringTimes,
				totalOutsideExceptionTimes);
		/**
		 * find the most frequently occurred outsider exception goal
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
					updateRoot(insider);
				}
				else {
					updatePath(insider, corresponder);
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

		for (FitnessFunction<T> exceptionGoal : exceptionOccuringProbability.keySet()) {
			if (!exceptionGoal.isInTarget()) {
				continue;
			}

			if (handledExceptions.contains(exceptionGoal)) {
				continue;
			}

			double newFreq = exceptionOccuringProbability.get(exceptionGoal);
			if (newFreq > freq) {
				frequentGoal = exceptionGoal;
				freq = newFreq;
			}
		}

		return frequentGoal;
	}

	private FitnessFunction<T> findFrequentOutsiderException(
			Map<FitnessFunction<T>, Double> exceptionOccuringProbability) {
		FitnessFunction<T> frequentGoal = null;
		Double freq = -1.0;

		for (FitnessFunction<T> exceptionGoal : exceptionOccuringProbability.keySet()) {
			if (exceptionGoal.isInTarget()) {
				continue;
			}

			if (handledExceptions.contains(exceptionGoal)) {
				continue;
			}

			double newFreq = exceptionOccuringProbability.get(exceptionGoal);
			if (newFreq > freq) {
				frequentGoal = exceptionGoal;
				freq = newFreq;
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

	private void updateRoot(FitnessFunction<T> newGoal) {
		//TODO for ziheng, need to restructure the graph based on the control flow of new goal
		this.goalsManager.getBranchFitnessGraph().updateRoot(newGoal);
		this.goalsManager.getCurrentGoals().clear();
		this.goalsManager.getCurrentGoals().add(newGoal);
		this.goalsManager.updateBranchGoal(newGoal);
	}

	private void updatePath(FitnessFunction<T> newGoal, FitnessFunction<T> parentGoal) {
		// TODO for ziheng, need to restructure the graph based on the control flow of new goal
		this.goalsManager.getBranchFitnessGraph().updatePath(newGoal, parentGoal);
		this.goalsManager.getCurrentGoals().add(newGoal);
		this.goalsManager.updateBranchGoal(newGoal);
		for (FitnessFunction<T> child : ((MultiCriteriaManager<T>) goalsManager).getBranchFitnessGraph()
				.getStructuralChildren(newGoal)) {
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
			if(freq == null) {
				continue;
			}
			
			Integer totalCoverage = null;
			Double prob = 0.0;		
			//Non-root insider
			if (corresponder != null) {
				totalCoverage = goalCoverageFrequency.get(corresponder);
				prob = freq * 1.0 / totalCoverage;
			} else {
				totalCoverage = exceptionGoal.isInTarget() ? targetMethodCoveringTimes
						: totalOutsideExceptionTimes;
				prob = freq * 1.0 / totalCoverage;
			}
			exceptionOccuringProbability.put(exceptionGoal, prob);
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
