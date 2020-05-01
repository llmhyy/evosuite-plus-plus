package travisTest;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;

public class TravisSmartMutationTest {
	@Test
	public void testStringArrayExample() {
		Class<?> clazz = regression.smartmutation.example.StringArrayExample.class;

		String methodName = "main";
		int parameterNum = 1;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";
		
		Properties.CLIENT_ON_THREAD = true;

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		int age = result.getAge();
		int time = result.getTime();
		double coverage = result.getCoverage();
		assert age <= 70;
		assert time <= 35;
		assert coverage == 1.0;
	}

}
