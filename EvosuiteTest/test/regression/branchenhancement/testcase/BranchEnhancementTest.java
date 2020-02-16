package regression.branchenhancement.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

/**
 * This is used for regression test for branch enhancement (i.e., exception
 * handling) Current test cases inside this class all use same seed
 * 1578927395578
 */
public class BranchEnhancementTest {

	@Test
	public void testColtExample() {
		Class<?> clazz = regression.branchenhancement.example.ColtExample.class;

		String methodName = "mergeSortInPlace";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age == 56;
		assert time <= 10;
		assert coverage == 1.0;
		System.currentTimeMillis();
	}

	@Test
	public void testInevitableConstructorExample() {
		Class<?> clazz = regression.branchenhancement.example.InevitableConstructorExample.class;

		String methodName = "targetM";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age == 35;
		assert time <= 15;
		assert coverage == 1.0;
		System.currentTimeMillis();
	}

	@Test
	public void testLayeredCallExample() {
		Class<?> clazz = regression.branchenhancement.example.LayeredCallExample.class;

		String methodName = "targetM";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age == 17;
		assert time <= 5;
		assert coverage == 1.0;
		System.currentTimeMillis();
	}

//	@Test
//	public void testLayeredCallExample2() {
////		Class<?> clazz = com.example.TestV1.class;
////		Class<?> clazz = com.example.PassedExample1.class;
////		Class<?> clazz = com.example.PassedExample2.class;
////		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
////		Class<?> clazz = com.example.passedExamples.ConstructorAndCallExample.class;
////		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
////		Class<?> clazz = com.example.PassedExampleColt.class;
//
//		String methodName = "targetM";
////		String methodName = "test";
////		String methodName = "mergeSortInPlace";
//		int parameterNum = 3;
//		
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/classes";
//
//		// Properties.LOCAL_SEARCH_RATE = 1;
////		Properties.DEBUG = true;
////		Properties.PORT = 8000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		Properties.BRANCH_COMPARISON_TYPES = true;
//		Properties.TIMEOUT = 10000000;
////		Properties.TIMELINE_INTERVAL = 3000;
//		
//		String fitnessApproach = "fbranch";
//		
//		int timeBudget = 100000;
//		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		System.currentTimeMillis();
//	}

	@Test
	public void testMultipleConstructorsExample() {
		Class<?> clazz = regression.branchenhancement.example.MultipleConstructorsExample.class;

		String methodName = "targetM";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age == 132;
		assert time <= 20;
		assert coverage == 1.0;
		System.currentTimeMillis();
	}

	@Test
	public void testMultipleExceptionExample() {
		Class<?> clazz = regression.branchenhancement.example.MultipleExceptionExample.class;

		String methodName = "test";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age == 972;
		assert time <= 80;
		assert coverage == 1.0;
		System.currentTimeMillis();
	}

	@Test
	public void testWekaTabuSearch_Bug() {
		String projectId = "101_weka";
		String[] targetMethods = new String[] {
				"weka.classifiers.bayes.net.search.local.TabuSearch#setOptions([Ljava/lang/String;)V" };

		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1581833614927L;

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;

		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, targetMethods, fitnessApproach, repeatTime, budget,
				true, seed);
//		
		EvoTestResult result = results0.get(0);
		System.currentTimeMillis();
	}
}
