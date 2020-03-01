package regression.smartmutation.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;

public class SmartMutationTest {
	@Test
	public void testStringArrayExample() {
		Class<?> clazz = regression.smartmutation.example.StringArrayExample.class;

		String methodName = "main";
		int parameterNum = 1;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";
		
		Properties.CLIENT_ON_THREAD = true;

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		int age = result.getAge();
//		int time = result.getTime();
//		double coverage = result.getCoverage();
//		assert age == 56;
//		assert time <= 10;
//		assert coverage == 1.0;
		System.currentTimeMillis();
	}

}
