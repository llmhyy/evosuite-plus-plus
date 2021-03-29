package feature.fbranch.testcase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;

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
	
	public void writeResults() {
		ExcelWriter excelWriter = new ExcelWriter(evosuite.shell.FileUtils.newFile(Settings.getReportFolder(), "sensitivity_scores.xlsx"));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		header.add("Path");
		header.add("Branch");
		header.add("Fitness Value");
		header.add("Iteration");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
		
		try {
			excelWriter.writeSheet("data", SensitivityMutator.data);
		} catch (IOException e) {
			System.out.println(e);
		}
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

		int timeBudget = 30;
		EvoTestResult result = null;
		
		result = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "branch");
		
		System.currentTimeMillis();
		
		int ageT = result.getAge();
		int timeT = result.getTime();
		double coverageT = result.getCoverage();
		
		writeResults();
	}
	
	@Test
	public void testValueRangeExample2() {
		Class<?> clazz = feature.fbranch.example.BooleanFlagExample1.class;
		String methodName = "targetM2";
		int parameterNum = 0;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes" + File.pathSeparator + "target/classes";

		int timeBudget = 30;
		EvoTestResult result = null;
		
		result = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "branch");
		
		System.currentTimeMillis();
		
		int ageT = result.getAge();
		int timeT = result.getTime();
		double coverageT = result.getCoverage();

		writeResults();
	}

	@Test
	public void testValueRangeExample3() {
		Class<?> clazz = feature.fbranch.example.BooleanFlagExample1.class;
		String methodName = "targetM3";
		int parameterNum = 0;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes" + File.pathSeparator + "target/classes";

		int timeBudget = 30;
		EvoTestResult result = null;
		
		result = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "branch");
		
		System.currentTimeMillis();
		
		int ageT = result.getAge();
		int timeT = result.getTime();
		double coverageT = result.getCoverage();

		writeResults();
	}
	
	@Test
	public void testFlagEffectExample5() {
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example5";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes" + File.pathSeparator + "target/classes";

		int timeBudget = 30;
		EvoTestResult result = null;
		
		result = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "branch");
		
		System.currentTimeMillis();
		
		int ageT = result.getAge();
		int timeT = result.getTime();
		double coverageT = result.getCoverage();

		writeResults();
	}
	
	@Test
	public void testFlagEffectExample6() {
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example9";
		int parameterNum = 2;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes" + File.pathSeparator + "target/classes";

		int timeBudget = 300000;
		EvoTestResult result = null;
		
		result = TestUtility.evosuiteFlagBranch(targetClass, targetMethod, cp, timeBudget, true, "branch");
		
		System.currentTimeMillis();
		
		int ageT = result.getAge();
		int timeT = result.getTime();
		double coverageT = result.getCoverage();

		writeResults();
	}
}
