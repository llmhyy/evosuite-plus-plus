package feature.smartseed.testcase;

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
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;
import feature.smartseed.example.empirical.Config;

public class SmartSeedNegativeBranchEvaluatorTest {
	@Before
	public void init() {
		Properties.TIMEOUT = 300000000;
//		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
//		Properties.APPLY_GRADEINT_ANALYSIS_IN_SMARTSEED = true;
	}
	
	@Test
	public void testSmartSeedBranch1() throws ClassNotFoundException, RuntimeException {
		//tail value not sensitive 
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample";
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch3() throws ClassNotFoundException, RuntimeException {
		//null one path
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	
	@Test
	public void testSmartSeedBranch6() throws ClassNotFoundException, RuntimeException {
		//one path
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "matcherMatchesExample";
		int parameterNum = 1;
		int lineNumber = 131;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	
	@Test
	public void testSmartSeedBranch20() throws ClassNotFoundException, RuntimeException {
		
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
//		writeResults();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testMethodReturnValueAsSingtelOperand() throws ClassNotFoundException, RuntimeException {
		//path list is null
		/**
		 * if(isOk()){...}
		 */
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "invokeDiffOprand";
		int parameterNum = 0;
		int lineNumber = 202;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
	}
	
	@Test
	public void testMethodReturnValueAsOneOfOperands() throws ClassNotFoundException, RuntimeException {
		//only constant path
		/**
		 * if(isOk() == 3){...}
		 */
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "invokeDiffOprand";
		int parameterNum = 0;
		int lineNumber = 204;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
	}
	
	@Test
	public void testFont() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "chooseFontFamily";
		int parameterNum = 2;
		int lineNumber = 144;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
	}
	
	@Test
	public void testColumnClass() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "setColumnClass";
		int parameterNum = 2;
		int lineNumber = 153;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
	}
	
	@Test
	public void testAddColumn() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "addColumn";
		int parameterNum = 2;
		int lineNumber = 166;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testGetDialectForProduct() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "getDialectForProduct";
		int parameterNum = 1;
		int lineNumber = 180;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testElement() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.XmlElement.class;
		String methodName = "getElement";
		int parameterNum = 1;
		int lineNumber = 69;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testRemoveElement() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.XmlElement.class;
		String methodName = "removeElement";
		int parameterNum = 1;
		int lineNumber = 84;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testStringReplace() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "stringReplaceAll";
		int parameterNum = 3;
		int lineNumber = 226;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testInfo() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.XmlElement.class;
		String methodName = "getAuthenticationInfo";
		int parameterNum = 1;
		int lineNumber = 94;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testAuthorized() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;
		String methodName = "isAuthorized";
		int parameterNum = 1;
		int lineNumber = 252;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	@Test
	public void testLogoEnabled() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = Config.class;
		String methodName = "isLogoEnabled";
		int parameterNum = 0;
		int lineNumber = 86;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch, null ,null).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
	}
}
