package org.evosuite.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.Algorithm;
import org.evosuite.Properties.Criterion;
import org.evosuite.ShutdownTestWriter;
import org.evosuite.coverage.FitnessFunctions;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.fbranch.FBranchSuiteFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.Hybridable;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.rmi.ClientServices;
import org.evosuite.rmi.service.ClientState;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.symbolic.DSEAlgorithm;
import org.evosuite.symbolic.expr.Constraint;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.evosuite.testsuite.TestSuiteMinimizer;
import org.evosuite.testsuite.factories.FixedSizeTestSuiteChromosomeFactory;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author linyun
 *
 */
public class EmpiricalHybridStrategyCollector extends TestGenerationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(EmpiricalHybridStrategyCollector.class);
	
	/**
	 * TODO we may need to change it according to the length of branch condition or path condition
	 */
	public static long branchWiseBudget = 100*1000;
	
	public static final long strategyWiseBudget = 20;
	
	class Segmentation{
		List<Branch> branchSegmentation;
		List<Constraint<?>> constraintSegmentation;
		long timeout;
		String strategy;
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public TestSuiteChromosome generateTests() {
		// In order to improve strategy's performance, in here we explicitly disable EvoSuite's
		// archive, as it is not used anyway by this strategy
		Properties.TEST_ARCHIVE = false;

		// Set up search algorithm
		LoggingUtils.getEvoLogger().info("* Setting up search algorithm for individual test generation");
		ExecutionTracer.enableTraceCalls();

		PropertiesTestGAFactory factory = new PropertiesTestGAFactory();
		
		// Get list of goals
        List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
		// long goalComputationStart = System.currentTimeMillis();
		List<TestFitnessFunction> goals = new ArrayList<TestFitnessFunction>();
		LoggingUtils.getEvoLogger().info("* Total number of test goals: ");
        for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories) {
            goals.addAll(goalFactory.getCoverageGoals());
            LoggingUtils.getEvoLogger().info("  - " + goalFactory.getClass().getSimpleName().replace("CoverageFactory", "")
                    + " " + goalFactory.getCoverageGoals().size());
        }

		if(!canGenerateTestsForSUT()) {
			LoggingUtils.getEvoLogger().info("* Found no testable methods in the target class "
					+ Properties.TARGET_CLASS);
			ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

			return new TestSuiteChromosome();
		}

		// Need to shuffle goals because the order may make a difference
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals,
		                                                                 goals.size());

		LoggingUtils.getEvoLogger().info("* Total number of test goals: " + goals.size());

		// Bootstrap with random testing to cover easy goals
		//statistics.searchStarted(suiteGA);
		ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

		StoppingCondition stoppingCondition = getStoppingCondition();
//		TestSuiteChromosome suite = (TestSuiteChromosome) bootstrapRandomSuite(fitnessFunctions.get(0), goalFactories.get(0));
		TestSuiteChromosome suite = new TestSuiteChromosome();


		stoppingCondition.setLimit(strategyWiseBudget);
		
		for (TestFitnessFunction fitnessFunction : goals) {
			
			logger.warn("working on " + fitnessFunction);
			
			List<TestChromosome> seeds = new ArrayList<>();
			suite.addTests(seeds);

			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			
			List<Segmentation> segList = new ArrayList<>();
			
			Segmentation prevSeg = new Segmentation();
			
			for(; end - start < branchWiseBudget; end = System.currentTimeMillis()) {
				/**
				 * randomly select a strategy and run it for a while.
				 */
				List<Hybridable> strategyList = getTotalStrategies(factory, stoppingCondition, fitnessFunction);
				int index = Randomness.nextInt(strategyList.size());
				Hybridable hybridStrategy = strategyList.get(index);
				GeneticAlgorithm<TestChromosome> strategy = (GeneticAlgorithm<TestChromosome>)hybridStrategy;
				
				logger.warn("applying " + strategy.getClass().getCanonicalName());

				if (ShutdownTestWriter.isInterrupted()) {
					continue;
				}

				
				// Perform search
				logger.info("Starting evolution for goal " + fitnessFunction);
				long t1 = System.currentTimeMillis();
				hybridStrategy.generateSolution(suite);
				
				long t2 = System.currentTimeMillis();
				long realTimeout = t2 - t1;

				/**
				 * if this strategy can cover the branch
				 */
				TestChromosome bestIndividual = null;
				if(strategy.getBestIndividual() instanceof TestChromosome) {
					bestIndividual = (TestChromosome)strategy.getBestIndividual();					
				}
				else{
					bestIndividual = findIndividualChromosomeFromSuite(strategy.getBestIndividual(), fitnessFunction);
				}
				
				Segmentation newSeg = parsePathSegmentation(prevSeg, bestIndividual, realTimeout, fitnessFunction);
				segList.add(newSeg);
				
				if(fitnessFunction.getFitness(bestIndividual) == 0.0) {
					if (Properties.PRINT_COVERED_GOALS)
						LoggingUtils.getEvoLogger().info("* Covered!"); // : " +
					logger.warn("Found solution, adding to test suite at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
					break;
				} 
				else {
					logger.warn("Found no solution for " + fitnessFunction + " at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
					seeds = strategy.getPopulation();
					suite.addTests(seeds);
				}
			}
			
			recordSegmentationList(segList);
			

		}
		


		return suite;
	}


	/**
	 * TODO 
	 * 1. check out which path segmentation is reached.
	 * 2. set strategy and timeout for the path segmentation
	 * 3. pass all the seeds to next strategy
	 * @param fitnessFunction 
	 * @param realTimeout 
	 */
	private Segmentation parsePathSegmentation(Segmentation prevSeg, TestChromosome bestIndividual, 
			long realTimeout, TestFitnessFunction fitnessFunction) {
		if(fitnessFunction instanceof FBranchTestFitness) {
			FBranchTestFitness fBranchFitness = (FBranchTestFitness)fitnessFunction;
			RawControlFlowGraph cfg = fBranchFitness.getBranch().getInstruction().getRawCFG();
			
			logger.warn(cfg.toString());
		}		
		return null;
	}

	private void recordSegmentationList(List<Segmentation> segList) {
		// TODO Xianglin
		
	}

	private TestChromosome findIndividualChromosomeFromSuite(Object bestIndividual, TestFitnessFunction fitnessFunction) {
		
		TestChromosome best = null;
		double fitness = -1;
		
		if(bestIndividual instanceof TestSuiteChromosome) {
			TestSuiteChromosome suite = (TestSuiteChromosome)bestIndividual;
			for(TestChromosome test: suite.getTestChromosomes()) {
				if(best == null) {
					best = test;
					fitness = fitnessFunction.getFitness(test);
				}
				else {
					double tmpFitness = fitnessFunction.getFitness(test);
					if(tmpFitness < fitness) {
						best = test;
						fitness = fitnessFunction.getFitness(test);
					}
					
				}
				
			}
		}
		
		return best;
	}

	private void deriveSingleGoal(FBranchSuiteFitness ff, TestFitnessFunction fitnessFunction) {
		ff.totalBranches = 1;
		ff.totalGoals = 1;
		
		/**
		 * TODO it is better to refactor, no need to be specific for FBranchTestFitness
		 */
		if(fitnessFunction instanceof FBranchTestFitness) {
			FBranchTestFitness function = (FBranchTestFitness)fitnessFunction;
			int branchID = function.getBranch().getActualBranchId();
			
			ff.getBranchesId().clear();
			ff.getBranchesId().add(branchID);
			
			Map<Integer, TestFitnessFunction> mapToModify = function.getBranchExpressionValue() ? ff.getBranchCoverageTrueMap() : ff.getBranchCoverageFalseMap();
			Map<Integer, TestFitnessFunction> mapToClear = !function.getBranchExpressionValue() ? ff.getBranchCoverageTrueMap() : ff.getBranchCoverageFalseMap();
			
			mapToClear.clear();
			
			Iterator<Integer> iter = mapToModify.keySet().iterator();
			while(iter.hasNext()) {
				Integer i = iter.next();
				if(i != branchID) {
					iter.remove();
				}
			}
			
			System.currentTimeMillis();
		}
		
		
		
//		ff.get
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Hybridable> getTotalStrategies(PropertiesTestGAFactory factory, StoppingCondition stoppingCondition, 
			TestFitnessFunction fitnessFunction) {
		List<Hybridable> list = new ArrayList<>();
		
		GeneticAlgorithm<TestChromosome> ga = factory.getSearchAlgorithm();
		setStrategyWiseBudget(ga);
		ga.addFitnessFunction(fitnessFunction);
		list.add((Hybridable)ga);
		
		Properties.ALGORITHM = Algorithm.RANDOM_SEARCH;
		GeneticAlgorithm<TestChromosome> random = factory.getSearchAlgorithm();
		setStrategyWiseBudget(random);
		random.addFitnessFunction(fitnessFunction);
		list.add((Hybridable)random);
		
		DSEAlgorithm dse = new DSEAlgorithm();
		setStrategyWiseBudget((GeneticAlgorithm)dse);
		TestSuiteFitnessFunction function = FitnessFunctions.getFitnessFunction(Properties.CRITERION[0]);
		if(function instanceof FBranchSuiteFitness) {
			FBranchSuiteFitness ff = (FBranchSuiteFitness)function;
			deriveSingleGoal(ff, fitnessFunction);
		}
		((GeneticAlgorithm)dse).addFitnessFunction(function);
		list.add((Hybridable)dse);
		
		return list;
	}

	private void setStrategyWiseBudget(GeneticAlgorithm<TestChromosome> ga) {
		for(StoppingCondition condition: ga.getStoppingConditions()) {
			if(condition instanceof MaxTimeStoppingCondition) {
				MaxTimeStoppingCondition mCondition = (MaxTimeStoppingCondition)condition;
				mCondition.setLimit(strategyWiseBudget);
			}
		}
	}

	private Set<Integer> getAdditionallyCoveredGoals(
	        List<? extends TestFitnessFunction> goals, Set<Integer> covered,
	        TestChromosome best) {

		Set<Integer> r = new HashSet<Integer>();
		ExecutionResult result = best.getLastExecutionResult();
		assert (result != null);
		// if (result == null) {
		// result = TestCaseExecutor.getInstance().execute(best.test);
		// }
		int num = -1;
		for (TestFitnessFunction goal : goals) {
			num++;
			if (covered.contains(num))
				continue;
			if (goal.isCovered(best, result)) {
				r.add(num);
				if (Properties.PRINT_COVERED_GOALS)
					LoggingUtils.getEvoLogger().info("* Additionally covered: "
					                                         + goal.toString());
			}
		}
		return r;
	}
	
	private TestSuiteChromosome bootstrapRandomSuite(FitnessFunction<?> fitness,
	        TestFitnessFactory<?> goals) {

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)
	            || ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS)) {
			LoggingUtils.getEvoLogger().info("* Disabled random bootstraping for dataflow criterion");
			Properties.RANDOM_TESTS = 0;
		}

		if (Properties.RANDOM_TESTS > 0) {
			LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite");
		} // else
		  // LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite disabled!");

		FixedSizeTestSuiteChromosomeFactory factory = new FixedSizeTestSuiteChromosomeFactory(Properties.RANDOM_TESTS);

		TestSuiteChromosome suite = factory.getChromosome();
		if (Properties.RANDOM_TESTS > 0) {
			TestSuiteMinimizer minimizer = new TestSuiteMinimizer(goals);
			minimizer.minimize(suite, true);
			LoggingUtils.getEvoLogger().info("* Initial test suite contains "
			                                         + suite.size() + " tests");
		}

		return suite;
	}
}
