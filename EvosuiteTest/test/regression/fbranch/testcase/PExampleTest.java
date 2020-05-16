package regression.fbranch.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;

public class PExampleTest{
	@Test
	public void testPExample() {
		Class<?> clazz = regression.fbranch.example.PExample.class;
		String methodName = "get";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 1000000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 100;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 290;
		assert time <= 120;
		assert coverage == 1.0;
	}
}
