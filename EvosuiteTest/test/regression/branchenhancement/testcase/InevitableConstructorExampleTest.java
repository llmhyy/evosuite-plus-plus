package regression.branchenhancement.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;

public class InevitableConstructorExampleTest extends BranchEnhancementTestSetup {
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
		EvoTestResult resultT = null;
		EvoTestResult resultF = null;

		try {
			resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		} catch (Exception e1) {
			try {
				resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			} catch (Exception e2) {
				resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			}
		}

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		try {
			resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		} catch (Exception e1) {
			try {
				resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			} catch (Exception e2) {
				resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			}
		}

		if (resultT == null) {
			resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		}

		if (resultF == null) {
			resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		}

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();

		assert ageT <= 13;
		assert timeT <= 4;
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT == 1.0;
	}
}
