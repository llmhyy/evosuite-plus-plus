package feature.fbranch.testcase;

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
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.fbranch.StatisticBranchFlagEvaluator;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;

public class DynamicSensitivityEvaluatorTest {
	@Before
	public void init() {
		Properties.TIMEOUT = 300000000;
		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
//		Properties.APPLY_OBJECT_RULE = true;
		Properties.APPLY_GRADEINT_ANALYSIS = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
	}
	
	public void writeResults() {
		ExcelWriter excelWriter = new ExcelWriter(evosuite.shell.FileUtils.newFile(Settings.getReportFolder(), "sensitivity_scores.xlsx"));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		header.add("Branch");
		header.add("Path");
		header.add("Testcase");
		header.add("HeadValue");
		header.add("TailValue");
		header.add("NewTestcase");
		header.add("NewHeadValue");
		header.add("NewTailValue");
		header.add("ValuePreserving");
		header.add("SensivityPreserving");
		header.add("FastChannel");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
		
		try {
			excelWriter.writeSheet("data", SensitivityMutator.data);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void testAaloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		writeResults();
		assert flagValue;
	}
	
	@Test
	public void testAloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testBaloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		writeResults();
		assert flagValue;
	}
	
	@Test
	public void testCaloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testD2fExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testDaddExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testDloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testF2dExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "f2dExample";
		int parameterNum = 1;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testGetfieldExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "getfieldExample";
		int parameterNum = 1;
		int lineNumber = 284;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		//TODO
		writeResults();
		Object oldHeadValue = SensitivityMutator.HeadValue;
		assert oldHeadValue == null;
		//assert oldHeadValue != null;

		assert flagValue;
	}
	
	@Test
	public void testGetstaticExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "getstaticExample";
		int parameterNum = 1;
		int lineNumber = 292;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		//TODO 
		Object oldHeadValue = SensitivityMutator.HeadValue;
		assert oldHeadValue == null;
		
		writeResults();
		assert flagValue;
	}
	
	@Test
	public void testI2bExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "i2bExample";
		int parameterNum = 1;
		int lineNumber = 300;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testI2cExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "i2cExample";
		int parameterNum = 1;
		int lineNumber = 309;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testI2fExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "i2flsExample";
		int parameterNum = 2;
		int lineNumber = 326;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testI2lExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "i2flsExample";
		int parameterNum = 2;
		int lineNumber = 329;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testI2sExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "i2sExample";//i2sExample,i2flsExample
		int parameterNum = 2;
		int lineNumber = 617;//617,331

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		//TODO 
		writeResults();
		assert flagValue;
	}
	
	@Test
	public void testIaddExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iaddExample";
		int parameterNum = 3;
		int lineNumber = 342;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	
	@Test
	public void testIdivExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iaddExample";
		int parameterNum = 3;
		int lineNumber = 344;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testImulExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iaddExample";
		int parameterNum = 3;
		int lineNumber = 346;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		 
		assert flagValue;
	}
	
	@Test
	public void testInegExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iaddExample";
		int parameterNum = 3;
		int lineNumber = 348;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testIsubExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iaddExample";
		int parameterNum = 3;
		int lineNumber = 355;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testIloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "ialoadExample";
		int parameterNum = 2;
		int lineNumber = 365;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		//TODO
		assert flagValue;
	}
	
	@Test
	public void testIaloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "ialoadExample";
		int parameterNum = 2;
		int lineNumber = 369;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		//TODO
		assert flagValue;
	}
	
	@Test
	public void testIandExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iandExample";
		int parameterNum = 3;
		int lineNumber = 380;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testIorExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iandExample";
		int parameterNum = 3;
		int lineNumber = 382;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		 
		assert flagValue;
	}
	
	
	@Test
	public void testIxorExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iandExample";
		int parameterNum = 3;
		int lineNumber = 385;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testIremExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iremExample";
		int parameterNum = 2;
		int lineNumber = 395;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	
	@Test
	public void testIload_1Example() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iload_1Example";
		int parameterNum = 1;
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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testIload_2Example() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "iload_2Example";
		int parameterNum = 2;
		int lineNumber = 425;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}

	
	@Test
	public void testInstanceofExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "instanceofExample";
		int parameterNum = 1;
		int lineNumber = 434;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		//TODO 
		writeResults();
		
		Object oldHeadValue = SensitivityMutator.HeadValue;
		Object oldTailValue = SensitivityMutator.TailValue;
		assert oldTailValue != oldHeadValue;
		//assert oldHeadValue == oldHeadValue;

		assert flagValue;
	}
	
	@Test
	public void testInvokeinterfaceExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "invokeinterfaceExample";
		int parameterNum = 1;
		int lineNumber = 442;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testInvokespecialExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "invokeinterfaceExample";
		int parameterNum = 1;
		int lineNumber = 447;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testInvokestaticExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "invokeinterfaceExample";
		int parameterNum = 1;
		int lineNumber = 449;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testInvokevirtualExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "invokevirtualExample";
		int parameterNum = 1;
		int lineNumber = 468;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testIshlExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "ishlExample";
		int parameterNum = 3;
		int lineNumber = 477;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testIshrExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "ishlExample";
		int parameterNum = 3;
		int lineNumber = 479;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testIushrExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "ishlExample";
		int parameterNum = 3;
		int lineNumber = 482;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testL2iExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "l2dExample";
		int parameterNum = 3;
		int lineNumber = 492;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testL2dExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "l2dExample";
		int parameterNum = 3;
		int lineNumber = 494;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testL2fExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "l2dExample";
		int parameterNum = 3;
		int lineNumber = 498;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLaddExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laddExample";
		int parameterNum = 2;
		int lineNumber = 507;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testLdivExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laddExample";
		int parameterNum = 2;
		int lineNumber = 509;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLmulExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laddExample";
		int parameterNum = 2;
		int lineNumber = 511;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLnegExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laddExample";
		int parameterNum = 2;
		int lineNumber = 514;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLsubExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laddExample";
		int parameterNum = 2;
		int lineNumber = 517;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLremExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laddExample";
		int parameterNum = 2;
		int lineNumber = 519;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laloadExample";
		int parameterNum = 2;
		int lineNumber = 529;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		writeResults();
		
		assert flagValue;
	}
	
	
	@Test
	public void testLaloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "laloadExample";
		int parameterNum = 2;
		int lineNumber = 533;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testLandExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "landExample";
		int parameterNum = 3;
		int lineNumber = 544;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLorExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "landExample";
		int parameterNum = 3;
		int lineNumber = 546;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLxorExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "landExample";
		int parameterNum = 3;
		int lineNumber = 549;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	
	@Test
	public void testLshlExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "lshlExample";
		int parameterNum = 3;
		int lineNumber = 589;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;

		assert flagValue;
	}
	
	@Test
	public void testLshrExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "lshlExample";
		int parameterNum = 3;
		int lineNumber = 591;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testLushrExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "lshlExample";
		int parameterNum = 3;
		int lineNumber = 594;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
	@Test
	public void testSaloadExample() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.fbranch.example.SensitivityMutatorExample.class;
		String methodName = "saloadExample";
		int parameterNum = 1;
		int lineNumber = 604;

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
		boolean flagValue = SensitivityMutator.testBranchSensitivity(set, branchesInTargetMethod, targetBranch,path).sensivityPreserving;
		
		assert flagValue;
	}
	
}
