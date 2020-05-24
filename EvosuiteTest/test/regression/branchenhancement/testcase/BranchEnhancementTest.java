package regression.branchenhancement.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
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
		Properties.APPLY_OBJECT_RULE = false;
		Properties.ADOPT_SMART_MUTATION = false;
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
		EvoTestResult resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach,
				"1589896747959");
		
		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach,
				"1589896747959");
				
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		
		assert ageT <= 140;
		assert timeT <= 20;
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT == 1.0;
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
		EvoTestResult resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		
		assert ageT <= 13;
		assert timeT <= 3;
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT == 1.0;
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
		EvoTestResult resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach,
				"1589897812206");
		
		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach,
				"1589897812206");
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		
		assert ageT <= 55;
		assert timeT <= 6;
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT == 1.0;
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
		EvoTestResult resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		double coverageF = resultF.getCoverage();

		assert ageT <= 20;
		assert timeT <= 3;
		assert coverageT == 1.0;
		assert coverageF == 0.0;
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

		int timeBudget = 150;
		EvoTestResult resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		double coverageF = resultF.getCoverage();
		
		assert ageT <= 1700;
		assert timeT <= 120;
		assert coverageT == 1.0;
		assert coverageF < 1.0;
	}
}
