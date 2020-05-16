package travisTest;

import static org.junit.jupiter.api.Assertions.assertAll;

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
		
		Properties.ADOPT_SMART_MUTATION = false;
		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
		Properties.ADOPT_SMART_MUTATION = true;
		EvoTestResult resultT = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		
		assert ageF <= 70;
		assert timeT < timeF;
		assert timeF <= 35;
		assert coverageT == 1.0;
	}

}
