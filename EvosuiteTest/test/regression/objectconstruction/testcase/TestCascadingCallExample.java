package regression.objectconstruction.testcase;

import java.lang.reflect.Method;

import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUility;

import evosuite.shell.EvoTestResult;

public class TestCascadingCallExample extends DebugSetup {

	@Test
	public void testCascadingCallExample() {
		Class<?> clazz = regression.objectconstruction.example.CascadingCallExample.class;

		String methodName = "target";
		int parameterNum = 0;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100000;
		EvoTestResult result = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.currentTimeMillis();
	}
	
}
