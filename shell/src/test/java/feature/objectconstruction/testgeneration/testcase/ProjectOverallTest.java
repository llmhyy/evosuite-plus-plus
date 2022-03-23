package feature.objectconstruction.testgeneration.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.seeding.smart.SensitivityMutator;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import feature.objectconstruction.testgeneration.example.ObjectExample;
import feature.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules;

public class ProjectOverallTest extends TestUtility{
	
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
		Class<?> clazz = BasicRules.class;
		String methodName = "checkRules";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes;target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 100;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 30;
		
		boolean aor = true;
		double coverage = TestUtility.evoTestSingleMethod(targetClass,  
				targetMethod, timeBudget, true, aor, cp, fitnessApproach, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");
		
		assert coverage > 0.3;
		
	}
	
	@Test
	public void testBasicRulesSuite() {
		Class<?> clazz = BasicRules.class;
		String methodName = "checkRules";
		int parameterNum = 2;
		
		System.out.println("aaa");
		System.out.println("aaa");
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes;target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 100;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 30;
		
		boolean aor = false;
		double coverage = TestUtility.evoTestSingleMethod(targetClass,  
				targetMethod, timeBudget, true, aor, cp, fitnessApproach, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");
		
		assert coverage > 0.3;
		
	}
	
	@Test
	public void testCascadeCall() {
		Class<?> clazz = ObjectExample.class;
		String methodName = "test";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 10000;
		
		boolean aor = true;
		TestUtility.evoTestSingleMethod(targetClass,  
				targetMethod, timeBudget, true, aor, cp, fitnessApproach, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");
		
	}
	
	@Test
	public void testStudentInterface() {
		Class<?> clazz = ObjectExample.class;
		String methodName = "test1";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testStudentAbstract() {
		Class<?> clazz = ObjectExample.class;
		String methodName = "test2";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testFlagExample1() {
		ExcelWriter excelWriter;

		Properties.TIMEOUT = 300000000;
		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		Properties.APPLY_OBJECT_RULE = false;
		Properties.APPLY_GRADEINT_ANALYSIS = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example5";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 10;
		
		boolean aor = false;
		TestUtility.evoTestSingleMethod(targetClass,  
				targetMethod, timeBudget, true, aor, cp, fitnessApproach, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");
		
		excelWriter = new ExcelWriter(evosuite.shell.FileUtils.newFile(Settings.getReportFolder(), "sensitivity_scores.xlsx"));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		header.add("Branch");
		header.add("Path");
		header.add("Fitness Value");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
		
		try {
			excelWriter.writeSheet("data", SensitivityMutator.data);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
