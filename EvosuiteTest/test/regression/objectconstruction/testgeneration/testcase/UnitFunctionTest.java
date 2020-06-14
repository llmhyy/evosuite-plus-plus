package regression.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUtility;

import evosuite.shell.EvoTestResult;

public class UnitFunctionTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = true;
	}
	
	@Test
	public void testPublicStaticFieldExample() {
		Class<?> clazz = regression.objectconstruction.graphgeneration.example.staticfield.PublicStaticFieldExample.class;

		String methodName = "method";
		int parameterNum = 0;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 1000000;
		EvoTestResult resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);

//		Properties.APPLY_OBJECT_RULE = false;
//		EvoTestResult resultF = TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();

		assert ageT <= 1;
		assert timeT <= 1;
//		assert ageT < ageF;
//		assert timeT <= timeF;
		assert coverageT == 1.0;
	}
	
	@Test
	public void testPrivateStaticFieldExample() {
		//TODO Ziheng
	}
}
