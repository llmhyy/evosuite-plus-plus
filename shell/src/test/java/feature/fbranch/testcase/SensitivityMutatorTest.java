package feature.fbranch.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.seeding.smart.SensitivityMutator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;
import evosuite.shell.EvoTestResult;
import feature.fbranch.example.SensitivityMutatorExample;
import feature.smartseed.example.empirical.EmpiricalStudyExample;

public class SensitivityMutatorTest {
//	@Before
//	public void init() {
//		Properties.APPLY_SMART_SEED = true;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		Properties.TIMEOUT = 10000000;
//		Properties.ENABLE_TRACEING_EVENT = false;
//		
//		Properties.APPLY_GRADEINT_ANALYSIS = true;
//		Properties.CHROMOSOME_LENGTH = 5;
//	}
	
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
	
	
//	1.sensitive mutator //TODO Cheng Yan
	@Test
	public void testIandExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iandExample";
		int parameterNum = 3;
		int lineNumber = 371;

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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	
	@Test
	public void testAloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "aloadExample";
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testInvokevirtual0Example() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "invokevirtualExample";
		int parameterNum = 1;
		int lineNumber = 459;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testAaloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "aaloadExample";
		int parameterNum = 2;
		int lineNumber = 24;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testIload_0Example() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "iload_0Example";
		int parameterNum = 1;
		int lineNumber = 396;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testIload_1Example() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "iload_1Example";
		int parameterNum = 1;
		int lineNumber = 407;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testIload_2Example() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "iload_2Example";
		int parameterNum = 2;
		int lineNumber = 416;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testCaloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "caloadExample";
		int parameterNum = 1;
		int lineNumber = 65;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
//	@Test
//	public void testAload_0Example() {
//		Class<?> clazz = SensitivityMutatorExample.class;
//		String methodName = "aload_0Example";
//		int parameterNum = 2;
//		
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/classes;target/test-classes";
//
//		String fitnessApproach = "branch";
//		
//		int repeatTime = 10;
//		int budget = 10000;
//		Long seed = null;
//				
//		boolean aor = false;
//		boolean ass = true;
//		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
//				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
//	}
	
	@Test
	public void testBaloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "baloadExample";
		int parameterNum = 1;
		int lineNumber = 54;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testI2dExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "i2dExample";
		int parameterNum = 2;
		int lineNumber = 308;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testFremExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "fremExample";
		int parameterNum = 1;
		int lineNumber = 257;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testD2fExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "d2fExample";
		int parameterNum = 1;
		int lineNumber = 74;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testD2iExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "d2iExample";
		int parameterNum = 2;
		int lineNumber = 82;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testD2lExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "d2lExample";
		int parameterNum = 1;
		int lineNumber = 90;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testDaddExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "daddExample";
		int parameterNum = 2;
		int lineNumber = 100;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testDaloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "daloadExample";
		int parameterNum = 1;
		int lineNumber = 111;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;	
	}
	
	@Test
	public void testDdivExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "ddivExample";
		int parameterNum = 1;
		int lineNumber = 120;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testDloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "dloadExample";
		int parameterNum = 1;
		int lineNumber = 129;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	
	@Test
	public void testDmulExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "dmulExample";
		int parameterNum = 2;
		int lineNumber = 140;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;	
	}
	
	@Test
	public void testDnegExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "dnegExample";
		int parameterNum = 1;
		int lineNumber = 149;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testDremExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "dremExample";
		int parameterNum = 1;
		int lineNumber = 159;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	
	@Test
	public void testDsubExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "dsubExample";
		int parameterNum = 1;
		int lineNumber = 167;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testF2dExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "f2dExample";
		int parameterNum = 1;
		int lineNumber = 175;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testF2iExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "f2iExample";
		int parameterNum = 2;
		int lineNumber = 184;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}
	
	@Test
	public void testF2lExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "f2lExample";
		int parameterNum = 0;
		int lineNumber = 192;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();
		//relevantValue null
		assert flagValue;
	}
	
	@Test
	public void testFaddExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "faddExample";
		int parameterNum = 0;
		int lineNumber = 201;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();
		//relevantValue null
		assert flagValue;
	}
	
	@Test
	public void testFaloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "faloadExample";
		int parameterNum = 0;
		int lineNumber = 211;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();
		//relevantValue null
		assert flagValue;
	}
	
	@Test
	public void testFdivExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "fdivExample";
		int parameterNum = 1;
		int lineNumber = 221;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;	
	}
	
	@Test
	public void testFloadExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "floadExample";
		int parameterNum = 0;
		int lineNumber = 231;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();
		////relevantValue null
		assert flagValue;
	}
	
	@Test
	public void testFmulExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "fmulExample";
		int parameterNum = 1;
		int lineNumber = 241;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;	
	}
	
	@Test
	public void testFnegExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "fnegExample";
		int parameterNum = 1;
		int lineNumber = 249;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;	
	}
	
	@Test
	public void testFsubExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "fsubExample";
		int parameterNum = 0;
		int lineNumber = 267;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();
		//relevantStatement null
		assert flagValue == false;	
	}
	
	@Test
	public void testGetfieldExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "getfieldExample";
		int parameterNum = 1;
		int lineNumber = 275;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();
		////relevantValue null
		assert flagValue;
	}
	
	@Test
	public void testGetstaticExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = SensitivityMutatorExample.class;
		String methodName = "getstaticExample";
		int parameterNum = 1;
		int lineNumber = 283;
		
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
		
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, true);
		set.add(ff);
		
		ComputationPath path = null;
		boolean flagValue = SensitivityMutator.testBranchSensitivity(branchesInTargetMethod, targetBranch,path).isSensitivityPreserving();

		assert flagValue;
	}

}
