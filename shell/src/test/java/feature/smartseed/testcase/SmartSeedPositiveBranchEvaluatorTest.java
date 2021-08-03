package feature.smartseed.testcase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import feature.smartseed.example.empirical.Config;

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
//		Properties.APPLY_GRADEINT_ANALYSIS_IN_SMARTSEED = true;
	}
	
	public void writeResults() {
		ExcelWriter excelWriter = new ExcelWriter(evosuite.shell.FileUtils.newFile(Settings.getReportFolder(), "fastchannel_info.xlsx"));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		header.add("Branch");
		header.add("Path");
//		header.add("Fitness Value");
		header.add("Testcase");
		header.add("HeadValue");
		header.add("TailValue");
		header.add("NewTestcase");
		header.add("NewHeadValue");
		header.add("NewTailValue");
		header.add("ValuePreserving");
		header.add("SensivityPreserving");
		header.add("FastChannel");
//		header.add("Iteration");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
		
		try {
			excelWriter.writeSheet("data", SensitivityMutator.data);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testDynamicExample1() throws ClassNotFoundException, RuntimeException {
		//Static or dynamic
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "dynamicExample1";
		int parameterNum = 2;
		int lineNumber = 25;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch2() throws ClassNotFoundException, RuntimeException {
		//one path
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type != SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch3() throws ClassNotFoundException, RuntimeException {
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample1";
		int parameterNum = 2;
		int lineNumber = 37;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch4() throws ClassNotFoundException, RuntimeException {
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample2";
		int parameterNum = 2;
		int lineNumber = 44;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch5() throws ClassNotFoundException, RuntimeException {
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "stratWithExample";
		int parameterNum = 2;
		int lineNumber = 91;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		writeResults();
		System.out.print(type);
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch7() throws ClassNotFoundException, RuntimeException {
		//constant value < 100
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample4";
		int parameterNum = 4;
		int lineNumber = 68;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
//		writeResults();
		System.out.print(type);
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch11() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "equalsIgnoreCaseExample";
		int parameterNum = 2;
		int lineNumber = 83;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch13() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "stratWithExample";
		int parameterNum = 1;
		int lineNumber = 99;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;//STATIC_POOL
		
	}
	
	@Test
	public void testSmartSeedBranch14() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "endWithExample";
		int parameterNum = 2;
		int lineNumber = 106;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void testSmartSeedBranch16() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "patternMatchesExample";
		int parameterNum = 1;
		int lineNumber = 122;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;//DYNAMIC_POOL
		
	}
	
	@Test
	public void testSmartSeedBranch17() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample2";
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
//		writeResults();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;//DYNAMIC_POOL
		
	}
	
	@Test
	public void testSmartSeedBranch18() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "combinationExample";
		int parameterNum = 1;
		int lineNumber = 139;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		assert type == SeedingApplicationEvaluator.STATIC_POOL;//DYNAMIC_POOL
		
	}
	
	@Test
	public void testSmartSeedBranch19() throws ClassNotFoundException, RuntimeException {
		//substring
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample3";
		int parameterNum = 2;
		int lineNumber = 55;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	
	

	
	
	@Test
	public void testMatchesExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "matchesExample";
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
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;//DYNAMIC_POOL
		
	}
	
	
	@Test
	public void testSwitchCase() throws ClassNotFoundException, RuntimeException {
		//TODO Cheng Yan
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "switchcaseExample";
		int parameterNum = 2;
		int lineNumber = 170;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
	}
	
	
	
	
	@Test
	public void testSingleOprand() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "singleOprand";
		int parameterNum = 0;
		int lineNumber = 189;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
	}
	
	@Test
	public void testHighQuality() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = Config.class;
		String methodName = "isHighQuality";
		int parameterNum = 0;
		int lineNumber = 63;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
	}
	
	@Test
	public void testCompareExample() throws ClassNotFoundException, RuntimeException {
		//substring
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "compareExample";
		int parameterNum = 1;
		int lineNumber = 225;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch).getBenefiticalType();
		
		writeResults();
		
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
}
