package regression.branchenhancement.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUtility;

import evosuite.shell.EvoTestResult;

public class ColtExampleTest extends BranchEnhancementTestSetup {
	@Test
	public void testColtExample() {
		Class<?> clazz = regression.branchenhancement.example.ColtExample.class;

		String methodName = "mergeSortInPlace";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		EvoTestResult resultT = null;
		EvoTestResult resultF = null;

		String seed = "1589896747959";

		try {
			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
		} catch (Exception e1) {
			try {
				resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
			} catch (Exception e2) {
				resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
			}
		}

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		try {
			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
		} catch (Exception e1) {
			try {
				resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
			} catch (Exception e2) {
				resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
			}
		}

		if (resultT == null) {
			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
		}

		if (resultF == null) {
			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, seed);
		}

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();

		assert ageT <= 140;
		assert timeT <= 22;
		assert ageT < ageF;
		assert timeT <= timeF;
		assert coverageT == 1.0;
	}
}
