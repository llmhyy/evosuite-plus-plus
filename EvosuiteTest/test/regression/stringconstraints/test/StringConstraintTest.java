package regression.stringconstraints.test;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUtility;

import evosuite.shell.EvoTestResult;

public class StringConstraintTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
	}
	
	@Test
	public void testConstraint() {
		Class<?> clazz = regression.stringconstraints.example.StringConstraintExample.class;

		String methodName = "method";
		int parameterNum = 3;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 10000;
		Properties.APPLY_OBJECT_RULE = false;
		Properties.P_TEST_INSERT = 0;
		Properties.CHROMOSOME_LENGTH = 10;
		EvoTestResult resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach, "");
		System.currentTimeMillis();
		
//		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//
//		assert ageT <= 1;
//		assert timeT <= 1;
//		assert ageT < ageF;
//		assert timeT <= timeF;
//		assert coverageT == 1.0;
	}
}
