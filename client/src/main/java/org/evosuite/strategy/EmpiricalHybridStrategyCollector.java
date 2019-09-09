package org.evosuite.strategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.Algorithm;
import org.evosuite.Properties.Criterion;
import org.evosuite.ShutdownTestWriter;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
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

	private static final Logger logger = LoggerFactory.getLogger(TestGenerationStrategy.class);
	
	/**
	 * TODO we may need to change it according to the length of branch condition or path condition
	 */
	public static long branchWiseBudget = 100*1000;
	
	public static final long strategyWiseBudget = 20*1000;
	
	class Segmentation{
		List<Branch> branchSegmentation;
		List<Constraint<?>> constraintSegmentation;
		long timeout;
		@SuppressWarnings("rawtypes")
		GeneticAlgorithm strategy;
	}
	
	public List<Branch> transferGoal2BranchSeq(TestFitnessFunction tff){
		//TODO need to get the control flow graph from the branch.
		if(tff instanceof FBranchTestFitness) {
			FBranchTestFitness fBranchFitness = (FBranchTestFitness)tff;
			RawControlFlowGraph cfg = fBranchFitness.getBranch().getInstruction().getRawCFG();
			
			//TODO handle cfg.
		}
		
		
		return null;
	}
	
	public List<Constraint<?>> transferGoal2ConstraintSeq(TestFitnessFunction tff){
		//TODO
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public TestSuiteChromosome generateTests() {
		// In order to improve strategy's performance, in here we explicitly disable EvoSuite's
		// archive, as it is not used anyway by this strategy
		Properties.TEST_ARCHIVE = false;

		// Set up search algorithm
		LoggingUtils.getEvoLogger().info("* Setting up search algorithm for individual test generation");
		ExecutionTracer.enableTraceCalls();

		PropertiesTestGAFactory factory = new PropertiesTestGAFactory();
		
		List<TestSuiteFitnessFunction> fitnessFunctions = getFitnessFunctions();

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
		if (Properties.SHUFFLE_GOALS) {
			// LoggingUtils.getEvoLogger().info("* Shuffling goals");
			Randomness.shuffle(goals);
		}
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
		List<GeneticAlgorithm<TestChromosome>> strategyList = getTotalStrategies(factory, stoppingCondition);
		
		for (TestFitnessFunction fitnessFunction : goals) {
			List<TestChromosome> seeds = new ArrayList<>();
			suite.addTests(seeds);

			//TODO need to reset the branch wise budget with regard to the goal here.
			
			//TODO one branch need to run multiple times, a good approximation is 10 times.
			
			
			List<Branch> relevantBranchList = transferGoal2BranchSeq(fitnessFunction);
			List<Constraint<?>> relevantConstraint = transferGoal2ConstraintSeq(fitnessFunction);
			
			
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			
			for(; end - start < branchWiseBudget; end = System.currentTimeMillis()) {
				/**
				 * TODO randomly select a strategy and run it for a while.
				 */
				int index = Randomness.nextInt(strategyList.size());
				GeneticAlgorithm strategy = strategyList.get(index);
				

				if (ShutdownTestWriter.isInterrupted()) {
					continue;
				}

				// FitnessFunction fitness_function = new
				strategy.addFitnessFunction(fitnessFunction);

				// Perform search
				logger.info("Starting evolution for goal " + fitnessFunction);
				long t1 = System.currentTimeMillis();
				strategy.generateSolution();
				long t2 = System.currentTimeMillis();
				long realTimeout = t2 - t1;

				/**
				 * if this strategy can cover the branch
				 */
				if(strategy.getBestIndividual().getFitness() == 0.0) {
					if (Properties.PRINT_COVERED_GOALS)
						LoggingUtils.getEvoLogger().info("* Covered!"); // : " +
					logger.info("Found solution, adding to test suite at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
					TestChromosome best = (TestChromosome) strategy.getBestIndividual();
					best.getTestCase().addCoveredGoal(fitnessFunction);
					suite.addTest(best);
					
					//TODO record strategy and its breaking through path segmentation
					
					// Calculate and keep track of overall fitness
					for (TestSuiteFitnessFunction fitness_function : fitnessFunctions)
					    fitness_function.getFitness(suite);
					
					break;
				} 
				/**
				 * TODO 
				 * 1. check out which path segmentation is reached.
				 * 2. set strategy and timeout for the path segmentation
				 * 3. pass all the seeds to next strategy
				 */
				else {
					logger.info("Found no solution for " + fitnessFunction + " at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
					seeds = strategy.getPopulation();
					suite.addTests(seeds);
				}
			}
			

		}
		


		return suite;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<GeneticAlgorithm<TestChromosome>> getTotalStrategies(PropertiesTestGAFactory factory, StoppingCondition stoppingCondition) {
		//TODO derive three strategies
		List<GeneticAlgorithm<TestChromosome>> list = new ArrayList<GeneticAlgorithm<TestChromosome>>();
		
		GeneticAlgorithm<TestChromosome> ga = factory.getSearchAlgorithm();
		ga.setStoppingCondition(stoppingCondition);
		list.add(ga);
		
		Properties.ALGORITHM = Algorithm.RANDOM_SEARCH;
		GeneticAlgorithm<TestChromosome> random = factory.getSearchAlgorithm();
		random.setStoppingCondition(stoppingCondition);
		list.add(random);
		
		DSEAlgorithm dse = new DSEAlgorithm();
		dse.setStoppingCondition(stoppingCondition);
		list.add((GeneticAlgorithm)dse);
		
		return list;
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
