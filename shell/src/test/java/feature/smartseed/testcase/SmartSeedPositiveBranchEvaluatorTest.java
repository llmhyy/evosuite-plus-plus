package feature.smartseed.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

/**
 * This test is for checking applicable branch for branchwise smart seed strategy.
 * @author Yun Lin
 *
 */
public class SmartSeedPositiveBranchEvaluatorTest {
	
	@Before
	public void init() {
		Properties.TIMEOUT = 300000000;
//		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
	}
	
	@Test
	public void testSmartSeedBranch1() throws ClassNotFoundException, RuntimeException {

		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "dynamicExample1";
		int parameterNum = 2;
		int lineNumber = 23;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch2() throws ClassNotFoundException, RuntimeException {
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample";
		int parameterNum = 2;
		int lineNumber = 29;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch3() throws ClassNotFoundException, RuntimeException {
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample1";
		int parameterNum = 2;
		int lineNumber = 35;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch4() throws ClassNotFoundException, RuntimeException {
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample2";
		int parameterNum = 2;
		int lineNumber = 41;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch5() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample3";
		int parameterNum = 2;
		int lineNumber = 48;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	@Test
	public void testSmartSeedBranch6() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "accept";
		int parameterNum = 1;
		int lineNumber = 30;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch7() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "addMenuItem";
		int parameterNum = 2;
		int lineNumber = 57;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch8() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.ResourcesDirectory.class;
		String methodName = "addResource";
		int parameterNum = 2;
		int lineNumber = 31;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	@Test
	public void testSmartSeedBranch9() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "parse";
		int parameterNum = 1;
		int lineNumber = 115;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch10() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "loadInstructions";
		int parameterNum = 1;
		int lineNumber = 128;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch11() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "equalsIgnoreCaseExample";
		int parameterNum = 2;
		int lineNumber = 76;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
//	@Test
//	public void testSmartSeedBranch12() throws ClassNotFoundException, RuntimeException {
//		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
//		String methodName = "stratWithExample";
//		int parameterNum = 2;
//		int lineNumber = 84;
//
//		Properties.TARGET_CLASS = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
//		
//		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
//		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
//		
//		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
//		
//		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
//		
//		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
//		
//		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
//		
//		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
//		
//		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
//		
//	}
//	
	@Test
	public void testSmartSeedBranch13() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "stratWithExample";
		int parameterNum = 1;
		int lineNumber = 92;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch14() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "endWithExample";
		int parameterNum = 2;
		int lineNumber = 100;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	
	@Test
	public void testSmartSeedBranch15() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "matchesExample";
		int parameterNum = 1;
		int lineNumber = 108;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch16() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "patternMatchesExample";
		int parameterNum = 1;
		int lineNumber = 115;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch17() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "matcherMatchesExample";
		int parameterNum = 1;
		int lineNumber = 124;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
}
