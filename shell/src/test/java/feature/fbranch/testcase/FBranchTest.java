package feature.fbranch.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;

public class FBranchTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = false;
		Properties.ADOPT_SMART_MUTATION = false;
	}

	@Test
	public void testValueRangeExample() {
		Class<?> clazz = feature.fbranch.example.BooleanFlagExample1.class;
		String methodName = "targetM";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		int timeBudget = 200;
		EvoTestResult resultT = null;
		EvoTestResult resultF = null;
		
		try {
			resultT = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "fbranch");
		} catch (NullPointerException e) {
			resultT = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "fbranch");
		}
		
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
