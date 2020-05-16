package regression.branchenhancement.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.MethodUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;

/**
 * This is used for regression test for branch enhancement (i.e., exception
 * handling) Current test cases inside this class all use same seed
 * 1578927395578
 */
public class BranchEnhancementTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;

		Properties.ENABLE_BRANCH_ENHANCEMENT = true;
	}

	@AfterClass
	public static void cleanup() {
		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
	}

	@Test
	public void testColtExample() {
		Class<?> clazz = regression.branchenhancement.example.ColtExample.class;

		String methodName = "mergeSortInPlace";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach,
				"1589896747959");
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 80;
		assert time <= 40;
		assert coverage == 1.0;
	}

	@Test
	public void testInevitableConstructorExample() {
		Class<?> clazz = regression.branchenhancement.example.InevitableConstructorExample.class;

		String methodName = "targetM";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);

		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 55;
		assert time <= 60;
		assert coverage == 1.0;
	}

	@Test
	public void testLayeredCallExample() {
		Class<?> clazz = regression.branchenhancement.example.LayeredCallExample.class;

		String methodName = "targetM";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		LoggingUtils.getEvoLogger().info(targetClass + targetMethod + cp + timeBudget + fitnessApproach);
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach,
				"1589897812206");
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 35;
		assert time <= 5;
		assert coverage == 1.0;
	}

	@Test
	public void testMultipleConstructorsExample() {
		Class<?> clazz = regression.branchenhancement.example.MultipleConstructorsExample.class;

		String methodName = "targetM";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		LoggingUtils.getEvoLogger().info(targetClass + ' ' + targetMethod + ' ' + cp + timeBudget + fitnessApproach);
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 120;
		assert time <= 15;
		assert coverage == 1.0;
	}

	@Test
	public void testMultipleExceptionExample() {
		Class<?> clazz = regression.branchenhancement.example.MultipleExceptionExample.class;

		String methodName = "test";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		LoggingUtils.getEvoLogger().info(targetClass + targetMethod + cp + timeBudget + fitnessApproach);
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 1100;
		assert time <= 100;
		assert coverage == 1.0;
	}
}
