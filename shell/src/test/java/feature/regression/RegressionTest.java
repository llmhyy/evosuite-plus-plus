package feature.regression;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtility;
import feature.regression.example1.RegressionExample;

public class RegressionTest extends TestUtility{
	
//	@Before
//	public void beforeTest() {
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//
//		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
//		Properties.APPLY_OBJECT_RULE = true;
//		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
//		Properties.ADOPT_SMART_MUTATION = false;
//		
//		Properties.INSTRUMENT_CONTEXT = true;
//		Properties.CHROMOSOME_LENGTH = 200;
//		
////		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 20;
////		Properties.TOTAL_LEGITIMIZATION_BUDGET = 50;
//		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 0;
//		Properties.TOTAL_LEGITIMIZATION_BUDGET = 0;
//		Properties.TIMEOUT = 10;
////		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
//	}
	
	@Test
	public void testBasicRulesObj() {
//		Class<?> clazz = RegressionExample.class;
//		String targetClass = clazz.getCanonicalName();
		
		String targetClass = "com.RegressionExample";
		
//		Method method = clazz.getMethods()[0];
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String original = "D:\\workspace\\workspace-for-testing2\\original\\bin";
		String regression = "D:\\workspace\\workspace-for-testing2\\regression\\bin";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 100;
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		int timeBudget = 10;
		
		double coverage = evoTestRegressionSingleMethod(targetClass,  
				null, timeBudget, true, original, regression);
		
		System.out.println("coverage is:" + coverage);
//		assert coverage > 0.1;
		
	}
	
	public static double evoTestRegressionSingleMethod(String targetClass, String targetMethod, 
			int timeBudget, 
			boolean instrumentContext,
			String originalCP,
			String regressionCP) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
				"-regressionSuite",
				"-class", targetClass, 
				"-projectCP", originalCP,
				"-Dregressioncp", regressionCP
//				"-Dtarget_method", targetMethod
				};

		@SuppressWarnings("unchecked")
		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());
				
				return r.getCoverage();
			}
		}
		
		return 0;

	}
	
}
