package regression.fbranch.testcase;

import java.lang.reflect.Method;

import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUtility;

import evosuite.shell.EvoTestResult;

public class ValueRangeExampleTest extends FBranchTestSetup {
	@Test
	public void testValueRangeExample() {
		Class<?> clazz = regression.fbranch.example.ValueRangeExample.class;
		String methodName = "targetM";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
//		String cp = "target/test-classes;target/test-classes";

		int timeBudget = 200;
		EvoTestResult resultT = null;
		EvoTestResult resultF = null;

		resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, "fbranch");

		resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, "branch");

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		double coverageF = resultF.getCoverage();

		assert ageT <= 1300;
		assert timeT <= 150;
		assert coverageT == 1.0;
		assert coverageF < 1.0;
	}
}
