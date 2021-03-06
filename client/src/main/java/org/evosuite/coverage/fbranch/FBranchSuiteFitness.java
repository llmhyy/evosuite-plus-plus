package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.ga.archive.Archive;
import org.evosuite.graphs.cfg.CFGMethodAdapter;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FBranchSuiteFitness extends TestSuiteFitnessFunction {
	private static final long serialVersionUID = 4220214075514277335L;
	

	private final static Logger logger = LoggerFactory.getLogger(TestSuiteFitnessFunction.class);

	// Coverage targets
	public int totalGoals;
	public int totalMethods;
	public int totalBranches;
	public final int numBranchlessMethods;
	private final Set<String> branchlessMethods;
	private final Set<String> methods;

	private final Set<Integer> branchesId;
	
	// Some stuff for debug output
	public int maxCoveredBranches = 0;
	public int maxCoveredMethods = 0;
	public double bestFitness = Double.MAX_VALUE;

	// Each test gets a set of distinct covered goals, these are mapped by branch id
	private final Map<Integer, TestFitnessFunction> branchCoverageTrueMap = new HashMap<Integer, TestFitnessFunction>();
	private final Map<Integer, TestFitnessFunction> branchCoverageFalseMap = new HashMap<Integer, TestFitnessFunction>();
	private final Map<String, TestFitnessFunction> branchlessMethodCoverageMap = new HashMap<String, TestFitnessFunction>();

	protected final Set<Integer> toRemoveBranchesT = new HashSet<>();
	protected final Set<Integer> toRemoveBranchesF = new HashSet<>();
	protected final Set<String> toRemoveRootBranches = new HashSet<>();	
	
	protected final Set<Integer> removedBranchesT = new HashSet<>();
	protected final Set<Integer> removedBranchesF = new HashSet<>();
	protected final Set<String> removedRootBranches = new HashSet<>();	
	
//	private Map<Integer, Branch> goalMap = new HashMap<>();
	
	// Total coverage value, used by Regression
	public double totalCovered = 0.0;	
	
//	private List<FBranchTestFitness> branchGoals;
	private Map<String, FBranchTestFitness> branchGoals;
	/**
	 * <p>
	 * Constructor for BranchCoverageSuiteFitness.
	 * </p>
	 */
	public FBranchSuiteFitness() {
		this(TestGenerationContext.getInstance().getClassLoaderForSUT());
		FBranchFitnessFactory factory = new FBranchFitnessFactory();
		List<FBranchTestFitness> branchGoalList = factory.getCoverageGoals();
		this.branchGoals = constructGoalMap(branchGoalList);
	}
	
	private Map<String, FBranchTestFitness> constructGoalMap(List<FBranchTestFitness> branchGoals){
		Map<String, FBranchTestFitness> map = new HashMap<>();
		for(FBranchTestFitness tf: branchGoals) {
			int branch = tf.getBranchGoal().getBranch().getActualBranchId();
			boolean value = tf.getBranchGoal().getValue();
			
			String key = String.valueOf(branch) + value;
			map.put(key, tf);
		}
		
		return map;
	}
	
	/**
	 * <p>
	 * Constructor for BranchCoverageSuiteFitness.
	 * </p>
	 */
	public FBranchSuiteFitness(ClassLoader classLoader) {
		
		FBranchFitnessFactory factory = new FBranchFitnessFactory();
		List<FBranchTestFitness> branchGoalList = factory.getCoverageGoals();
		this.branchGoals = constructGoalMap(branchGoalList);
		
		String prefix = Properties.TARGET_CLASS_PREFIX;

		if (prefix.isEmpty())
			prefix = Properties.TARGET_CLASS;

		totalMethods = CFGMethodAdapter.getNumMethodsPrefix(classLoader, prefix);
		totalBranches = BranchPool.getInstance(classLoader).getBranchCountForPrefix(prefix);
		numBranchlessMethods = BranchPool.getInstance(classLoader).getNumBranchlessMethodsPrefix(prefix);
		branchlessMethods = BranchPool.getInstance(classLoader).getBranchlessMethodsPrefix(prefix);
		methods = CFGMethodAdapter.getMethodsPrefix(classLoader, prefix);
		
		branchesId = new HashSet<>();

		determineCoverageGoals(true);
		
		totalGoals = 2 * totalBranches + numBranchlessMethods;

		logger.info("Total branch coverage goals: " + totalGoals);
		logger.info("Total branches: " + totalBranches);
		logger.info("Total branchless methods: " + numBranchlessMethods);
		logger.info("Total methods: " + totalMethods + ": " + methods);

	}

	private FBranchTestFitness getGoal(Integer branchId, boolean b) {
		String key = String.valueOf(branchId) + b;
		return this.branchGoals.get(key);
//		for(FBranchTestFitness ff: branchGoals){
//			if(ff.getBranch().getActualBranchId()==branchId){
//				if(ff.getBranchGoal().getValue()==b){
//					return ff;
//				}
//			}
//		}
//		
//		return null;
	}

	/**
	 * Initialize the set of known coverage goals
	 */
	protected void determineCoverageGoals(boolean updateArchive) {
		List<FBranchTestFitness> goals = new FBranchFitnessFactory().getCoverageGoals();
		for (FBranchTestFitness goal : goals) {
			// Skip instrumented branches - we only want real branches
			if(goal.getBranch() != null) {
				if(goal.getBranch().isInstrumented()) {
					continue;
				}
			}
			if(updateArchive && Properties.TEST_ARCHIVE)
				Archive.getArchiveInstance().addTarget(goal);
			
			if (goal.getBranch() == null) {
				branchlessMethodCoverageMap.put(goal.getClassName() + "."
				                                        + goal.getMethod(), goal);
			} else {
				getBranchesId().add(goal.getBranch().getActualBranchId());
				if (goal.getBranchExpressionValue())
					getBranchCoverageTrueMap().put(goal.getBranch().getActualBranchId(), goal);
				else
					branchCoverageFalseMap.put(goal.getBranch().getActualBranchId(), goal);
			}
		}
	}

	/**
	 * If there is an exception in a superconstructor, then the corresponding
	 * constructor might not be included in the execution trace
	 * 
	 * @param result
	 * @param callCount
	 */
	private void handleConstructorExceptions(TestChromosome test, ExecutionResult result,
	        Map<String, Integer> callCount) {

			if (result.hasTimeout() || result.hasTestException() || result.noThrownExceptions()) {
				return;
			}

			Integer exceptionPosition = result.getFirstPositionOfThrownException();
			// TODO: Not sure why that can happen
			if (exceptionPosition >= result.test.size()) {
				return;
			}

			Statement statement = null;
			if (result.test.hasStatement(exceptionPosition)) {
				statement = result.test.getStatement(exceptionPosition);
			}
			if (statement instanceof ConstructorStatement) {
				ConstructorStatement c = (ConstructorStatement) statement;
				String className = c.getConstructor().getName();
				String methodName = "<init>" + Type.getConstructorDescriptor(c.getConstructor().getConstructor());
				String name = className + "." + methodName;
				if (!callCount.containsKey(name)) {
					callCount.put(name, 1);
					if (branchlessMethodCoverageMap.containsKey(name)) {
						TestFitnessFunction goal = branchlessMethodCoverageMap.get(name);
						test.getTestCase().addCoveredGoal(goal);
						toRemoveRootBranches.add(name);
						if(Properties.TEST_ARCHIVE) {
							Archive.getArchiveInstance().updateArchive(goal, test, 0.0);
						}
					}

				}
			}
	}

	protected void handleBranchlessMethods(TestChromosome test, ExecutionResult result, Map<String, Integer> callCount) {
		for (Entry<String, Integer> entry : result.getTrace().getMethodExecutionCount().entrySet()) {

			if (entry.getKey() == null || !methods.contains(entry.getKey()) || removedRootBranches.contains(entry.getKey()))
				continue;
			if (!callCount.containsKey(entry.getKey()))
				callCount.put(entry.getKey(), entry.getValue());
			else {
				callCount.put(entry.getKey(),
						callCount.get(entry.getKey()) + entry.getValue());
			}
			// If a specific target method is set we need to check
			// if this is a target branch or not
			if (branchlessMethodCoverageMap.containsKey(entry.getKey())) {
				TestFitnessFunction goal = branchlessMethodCoverageMap.get(entry.getKey());
				test.getTestCase().addCoveredGoal(goal);
				toRemoveRootBranches.add(entry.getKey());
				if (Properties.TEST_ARCHIVE) {
					Archive.getArchiveInstance().updateArchive(goal, test, 0.0);
				}
			}
		}
	}

	protected void handlePredicateCount(ExecutionResult result, Map<Integer, Integer> predicateCount) {
		for (Entry<Integer, Integer> entry : result.getTrace().getPredicateExecutionCount().entrySet()) {
			if (!getBranchesId().contains(entry.getKey())
					|| (removedBranchesT.contains(entry.getKey())
					&& removedBranchesF.contains(entry.getKey())))
				continue;
			if (!predicateCount.containsKey(entry.getKey()))
				predicateCount.put(entry.getKey(), entry.getValue());
			else {
				predicateCount.put(entry.getKey(),
						predicateCount.get(entry.getKey())
								+ entry.getValue());
			}
		}
	}


	protected void handleTrueDistances(TestChromosome test, ExecutionResult result, Map<Integer, Double> trueDistance) {
		for (Entry<Integer, Double> entry : result.getTrace().getTrueDistances().entrySet()) {
			if(!getBranchesId().contains(entry.getKey())||removedBranchesT.contains(entry.getKey())) continue;
			if (!trueDistance.containsKey(entry.getKey()))
				trueDistance.put(entry.getKey(), entry.getValue());
			else {
				trueDistance.put(entry.getKey(),
						Math.min(trueDistance.get(entry.getKey()),
								entry.getValue()));
			}
			BranchCoverageTestFitness goal = (BranchCoverageTestFitness) this.getBranchCoverageTrueMap().get(entry.getKey());
			if(goal != null) {
				if ((Double.compare(entry.getValue(), 0.0) == 0)) {
					test.getTestCase().addCoveredGoal(goal);
					toRemoveBranchesT.add(entry.getKey());
				}
				if(Properties.TEST_ARCHIVE) {
					Archive.getArchiveInstance().updateArchive(goal, test, entry.getValue());
				}
			}
			
		}

	}

	protected void handleFalseDistances(TestChromosome test, ExecutionResult result, Map<Integer, Double> falseDistance) {
		for (Entry<Integer, Double> entry : result.getTrace().getFalseDistances().entrySet()) {
			if(!getBranchesId().contains(entry.getKey())||!branchCoverageFalseMap.containsKey(entry.getKey())||removedBranchesF.contains(entry.getKey())) continue;
			if (!falseDistance.containsKey(entry.getKey()))
				falseDistance.put(entry.getKey(), entry.getValue());
			else {
				falseDistance.put(entry.getKey(),
						Math.min(falseDistance.get(entry.getKey()),
								entry.getValue()));
			}
			BranchCoverageTestFitness goal = (BranchCoverageTestFitness) this.branchCoverageFalseMap.get(entry.getKey());
			if(goal != null) {
				if ((Double.compare(entry.getValue(), 0.0) == 0)) {
					test.getTestCase().addCoveredGoal(goal);
					toRemoveBranchesF.add(entry.getKey());
				}
				if(Properties.TEST_ARCHIVE) {
					Archive.getArchiveInstance().updateArchive(goal, test, entry.getValue());
				}				
			}
		}

	}

	/**
	 * Iterate over all execution results and summarize statistics
	 * 
	 * @param results
	 * @param predicateCount
	 * @param callCount
	 * @param trueDistance
	 * @param falseDistance
	 * @return
	 */
	private boolean analyzeTraces(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, List<ExecutionResult> results,
	        Map<Integer, Integer> predicateCount, Map<String, Integer> callCount,
	        Map<Integer, Double> trueDistance, Map<Integer, Double> falseDistance) {
		boolean hasTimeoutOrTestException = false;
		for (ExecutionResult result : results) {
			if (result.hasTimeout() || result.hasTestException()) {
				hasTimeoutOrTestException = true;
				continue;
			}

			TestChromosome test = new TestChromosome();
			test.setTestCase(result.test);
			test.setLastExecutionResult(result);
			test.setChanged(false);

			handleBranchlessMethods(test, result, callCount);
			handlePredicateCount(result, predicateCount);
			handleTrueDistances(test, result, trueDistance);
			handleFalseDistances(test, result, falseDistance);

			// In case there were exceptions in a constructor
			handleConstructorExceptions(test, result, callCount);
		}
		return hasTimeoutOrTestException;
	}
	
	@Override
	public boolean updateCoveredGoals() {
		if (!Properties.TEST_ARCHIVE) {
			return false;
		}
		
		for (String method : toRemoveRootBranches) {
			boolean removed = branchlessMethods.remove(method);
			TestFitnessFunction f = branchlessMethodCoverageMap.remove(method);
			if (removed && f != null) {
				totalMethods--;
				methods.remove(method);
				removedRootBranches.add(method);
				//removeTestCall(f.getTargetClass(), f.getTargetMethod());
			} else {
				throw new IllegalStateException("goal to remove not found");
			}
		}

		for (Integer branch : toRemoveBranchesT) {
			TestFitnessFunction f = getBranchCoverageTrueMap().remove(branch);
			if (f != null) {
				removedBranchesT.add(branch);
				if (removedBranchesF.contains(branch)) {
					totalBranches--;
					//if(isFullyCovered(f.getTargetClass(), f.getTargetMethod())) {
					//	removeTestCall(f.getTargetClass(), f.getTargetMethod());
					//}
				}
			} else {
				throw new IllegalStateException("goal to remove not found");
			}
		}
		for (Integer branch : toRemoveBranchesF) {
			TestFitnessFunction f = branchCoverageFalseMap.remove(branch);
			if (f != null) {
				removedBranchesF.add(branch);
				if (removedBranchesT.contains(branch)) {
					totalBranches--;
					//if(isFullyCovered(f.getTargetClass(), f.getTargetMethod())) {
					//	removeTestCall(f.getTargetClass(), f.getTargetMethod());
					//}
				}
			} else {
				throw new IllegalStateException("goal to remove not found");
			}
		}
		
		toRemoveRootBranches.clear();
		toRemoveBranchesF.clear();
		toRemoveBranchesT.clear();
		logger.info("Current state of archive: " + Archive.getArchiveInstance().toString());
		
		return true;
	}
	
	
	private double getTestFitness(BranchCoverageGoal goal, ExecutionResult result) {
		FBranchTestFitness fBranchFitness = getGoal(goal.getBranch().getActualBranchId(), goal.getValue());
		
		TestChromosome tc = new TestChromosome();
		tc.setTestCase(result.test);
		
		double f = fBranchFitness.getFitness(tc, result);
		
		return f;
	}
	
	
	private double getNewFitness(List<ExecutionResult> results, Map<BranchCoverageGoal, Double> interestedKeys) {
		
		if(results.isEmpty()) {
			return this.branchGoals.size();
		}
		
		Map<BranchCoverageGoal, List<Double>> fitnessMap = new HashMap<>();
		for(Integer key: getBranchCoverageTrueMap().keySet()) {
			TestFitnessFunction tf = getBranchCoverageTrueMap().get(key);
			
			if(tf instanceof BranchCoverageTestFitness) {
				BranchCoverageGoal goal = ((BranchCoverageTestFitness) tf).getBranchGoal();
				
				List<Double> fitnessList = new ArrayList<>();
				for(ExecutionResult result: results) {
					double f = getTestFitness(goal, result);
					fitnessList.add(f);
				}
				
//				if(interestedKeys.containsKey(goal)) {
//					double d = Collections.min(fitnessList);
//					System.currentTimeMillis();
//				}
				
				fitnessMap.put(goal, fitnessList);
			}
		}
		
		for(Integer key: branchCoverageFalseMap.keySet()) {
			TestFitnessFunction tf = branchCoverageFalseMap.get(key);
			if(tf instanceof BranchCoverageTestFitness) {
				BranchCoverageGoal goal = ((BranchCoverageTestFitness) tf).getBranchGoal();
				
				List<Double> fitnessList = new ArrayList<>();
				for(int i=0; i<results.size(); i++) {
					if(i==13) {
						System.currentTimeMillis();
					}
					
					ExecutionResult result = results.get(i);
					
					double f = getTestFitness(goal, result);
					fitnessList.add(f);
				}
				
//				if(interestedKeys.containsKey(goal)) {
//					double d = Collections.min(fitnessList);
//					System.currentTimeMillis();
//				}
				
				fitnessMap.put(goal, fitnessList);
			}
			
		}
		
		double totalFitness = 0;
		int covered = 0;
		int unexecutedBranch = 0;
		int executedBranchNode = 0;
		for(BranchCoverageGoal goal: fitnessMap.keySet()) {
			List<Double> fList = fitnessMap.get(goal);
			Collections.sort(fList);
			
			double minValue = fList.get(0);
			if(minValue > 1) {
				minValue = 1;
				unexecutedBranch++;
			}
			else if(minValue == 0) {
				covered++;
			}
			else {
				executedBranchNode++;
			}
			
//			totalFitness += minValue;
			
			double fit = FitnessAggregator.aggreateFitenss(fList);
			totalFitness += fit;
		}
//		System.currentTimeMillis();
		
		return totalFitness;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Execute all tests and count covered branches
	 */
	@Override
	public double getFitness(
	        AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		logger.trace("Calculating branch fitness");
		double fitness = 0.0;

		List<ExecutionResult> results = runTestSuite(suite);
		Map<Integer, Double> trueDistance = new HashMap<Integer, Double>();
		Map<Integer, Double> falseDistance = new HashMap<Integer, Double>();
		Map<Integer, Integer> predicateCount = new HashMap<Integer, Integer>();
		Map<String, Integer> callCount = new HashMap<String, Integer>();

		// Collect stats in the traces 
		boolean hasTimeoutOrTestException = analyzeTraces(suite, results, predicateCount,
		                                                  callCount, trueDistance,
		                                                  falseDistance);

		// Collect branch distances of covered branches
		int numCoveredBranches = 0;
		
		Map<BranchCoverageGoal, Double> interestedKeys = new HashMap<>();
		for (Integer key : predicateCount.keySet()) {
			
			double df = 0.0;
			double dt = 0.0;
			int numExecuted = predicateCount.get(key);
			
			if(removedBranchesT.contains(key))
				numExecuted++;
			if(removedBranchesF.contains(key))
				numExecuted++;
			
			if (trueDistance.containsKey(key)) {
				dt =  trueDistance.get(key);
			}
			if(falseDistance.containsKey(key)){
				df = falseDistance.get(key);
			}
			// If the branch predicate was only executed once, then add 1 
			if (numExecuted == 1) {
				fitness += 1.0;
			} else {
				fitness += normalize(df) + normalize(dt);
				int f = (int)fitness;
				if(fitness != f && fitness-f != 0.5) {
					BranchCoverageGoal trueGoal = getGoal(key, true).getBranchGoal();
					if(normalize(dt) != 0 && normalize(dt) != 0.5) {
						interestedKeys.put(trueGoal, normalize(dt));						
					}
					
					BranchCoverageGoal falseGoal = getGoal(key, false).getBranchGoal();
					if(normalize(df)!=0 && normalize(dt) != 0.5) {
						interestedKeys.put(falseGoal, normalize(df));						
					}
				}
			}

			if (falseDistance.containsKey(key)&&(Double.compare(df, 0.0) == 0))
				numCoveredBranches++;

			if (trueDistance.containsKey(key)&&(Double.compare(dt, 0.0) == 0))
				numCoveredBranches++;
		}
		
		// +1 for every branch that was not executed
		fitness += 2 * (totalBranches - predicateCount.size());
		
		
		double newFit = getNewFitness(results, interestedKeys);
		if(newFit != fitness) {
			System.currentTimeMillis();
		}
		
//		getNewFitness(results, interestedKeys);
		fitness = newFit;

		
		// Ensure all methods are called
		int missingMethods = 0;
		if(Properties.TARGET_METHOD.isEmpty()) {
			for (String e : methods) {
				if (!callCount.containsKey(e)) {
					fitness += 1.0;
					missingMethods += 1;
				}
			}
		}

		// Calculate coverage
		int coverage = numCoveredBranches;

		coverage +=removedBranchesF.size();
		coverage +=removedBranchesT.size();
		coverage +=removedRootBranches.size();
	
 		
		if (totalGoals > 0)
			suite.setCoverage(this, (double) coverage / (double) totalGoals);
		else 
            suite.setCoverage(this, 1);
		
		totalCovered = suite.getCoverage(this);

		suite.setNumOfCoveredGoals(this, coverage);
		suite.setNumOfNotCoveredGoals(this, totalGoals-coverage);
		
		if (hasTimeoutOrTestException) {
			logger.info("Test suite has timed out, setting fitness to max value "
			        + (totalBranches * 2 + totalMethods));
			fitness = totalBranches * 2 + totalMethods;
		}

		updateIndividual(this, suite, fitness);

//		assert (coverage <= totalGoals) : "Covered " + coverage + " vs total goals "
//		        + totalGoals;
//		assert (fitness >= 0.0);
//		assert (fitness != 0.0 || coverage == totalGoals) : "Fitness: " + fitness + ", "
//		        + "coverage: " + coverage + "/" + totalGoals;
//		assert (suite.getCoverage(this) <= 1.0) && (suite.getCoverage(this) >= 0.0) : "Wrong coverage value "
//		        + suite.getCoverage(this); 
		return fitness;
	}
	

	
	/*
	 * Max branch coverage value
	 */
	public int getMaxValue() {
		return  totalBranches * 2 + totalMethods;
	}

	/**
	 * Some useful debug information
	 * 
	 * @param coveredBranches
	 * @param coveredMethods
	 * @param fitness
	 */
	private void printStatusMessages(
	        AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite,
	        int coveredBranches, int coveredMethods, double fitness) {
		if (coveredBranches > maxCoveredBranches) {
			maxCoveredBranches = coveredBranches;
			logger.info("(Branches) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches and " + coveredMethods + "/"
			        + totalMethods + " methods");
			logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
		if (coveredMethods > maxCoveredMethods) {
			logger.info("(Methods) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches and " + coveredMethods + "/"
			        + totalMethods + " methods");
			maxCoveredMethods = coveredMethods;
			logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
		if (fitness < bestFitness) {
			logger.info("(Fitness) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches and " + coveredMethods + "/"
			        + totalMethods + " methods");
			bestFitness = fitness;
			logger.info("Fitness: " + fitness + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
	}

	public Set<Integer> getBranchesId() {
		return branchesId;
	}

	public Map<Integer, TestFitnessFunction> getBranchCoverageTrueMap() {
		return branchCoverageTrueMap;
	}

	public Map<Integer, TestFitnessFunction> getBranchCoverageFalseMap() {
		return branchCoverageFalseMap;
	}
	
}
