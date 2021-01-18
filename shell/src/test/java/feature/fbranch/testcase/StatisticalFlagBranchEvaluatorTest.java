package feature.fbranch.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.fbranch.StatisticBranchFlagEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class StatisticalFlagBranchEvaluatorTest {
	@Before
	public void init() {
		Properties.TIMEOUT = 300000000;
		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
//		Properties.APPLY_OBJECT_RULE = true;
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
	}
	
	@Test
	public void testFlagExample1() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example1";
		int parameterNum = 2;
		int lineNumber = 6;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();

		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				Properties.TARGET_METHOD);

		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);

		double flagValue = StatisticBranchFlagEvaluator.evaluate(targetBranch);

		System.out.println(flagValue);
		assert flagValue == 0.0;
	}
	
	@Test
	public void testFlagExample2() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example2";
		int parameterNum = 2;
		int lineNumber = 23;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();

		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				Properties.TARGET_METHOD);

		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);

		double flagValue = StatisticBranchFlagEvaluator.evaluate(targetBranch);

		System.out.println(flagValue);
		assert flagValue == 4.0;
	}
	
	@Test
	public void testFlagExample3() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example3";
		int parameterNum = 2;
		int lineNumber = 32;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();

		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				Properties.TARGET_METHOD);

		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);

		double flagValue = StatisticBranchFlagEvaluator.evaluate(targetBranch);

		System.out.println(flagValue);
		assert flagValue == 4.0;
	}
	
	@Test
	public void testFlagExample4() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example4";
		int parameterNum = 2;
		int lineNumber = 40;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();

		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				Properties.TARGET_METHOD);

		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);

		double flagValue = StatisticBranchFlagEvaluator.evaluate(targetBranch);

		System.out.println(flagValue);
		assert flagValue == 1.0;
	}
	
	@Test
	public void testFlagExample5() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example5";
		int parameterNum = 2;
		int lineNumber = 53;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();

		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));

		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS,
				Properties.TARGET_METHOD);

		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);

		double flagValue = StatisticBranchFlagEvaluator.evaluate(targetBranch);

		System.out.println(flagValue);
		assert flagValue == 1.0;
	}
}
