package regression.objectconstruction.testgeneration.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class MultiSBSTAlgorithmTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
	}
	
	@Test
	public void testDynaMOSA() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testMOSA() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testMonotonicGA() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateSuite", "Evosuite", "Monotonic_GA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
}
