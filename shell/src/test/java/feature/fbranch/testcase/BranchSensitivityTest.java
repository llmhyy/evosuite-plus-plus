package feature.fbranch.testcase;

import java.io.File;
import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;

public class BranchSensitivityTest extends FBranchTestSetup{
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		Properties.ADOPT_SMART_MUTATION = true;
		Properties.APPLY_GRADEINT_ANALYSIS = true;
	}

	@Test
	public void testValueRangeExample() {
		Class<?> clazz = feature.fbranch.example.BooleanFlagExample1.class;
		String methodName = "targetM";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes" + File.pathSeparator + "target/classes";

		int timeBudget = 10000000;
		EvoTestResult result = null;
		
		result = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "branch");
		
		System.currentTimeMillis();
		
		int ageT = result.getAge();
		int timeT = result.getTime();
		double coverageT = result.getCoverage();

		
	}

}
