package feature.smartseed.testcase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.seeding.smart.BranchSeedInfo;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import common.TestUtility;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.listmethod.ListMethods;
import evosuite.shell.listmethod.MethodFilterOption;

public class SmartSeedMethodEvaluatorTest {
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
	public void testSmartSeedMethod1() throws ClassNotFoundException, RuntimeException {		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "dynamicExample1";
		int parameterNum = 2;
		int lineNumber = 20;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		List<BranchSeedInfo> benefitedBranches = SeedingApplicationEvaluator.evaluate(Properties.TARGET_METHOD);
		
		assert !benefitedBranches.isEmpty();
				
	}
	
	@Test
	public void testSmartSeedFilter1() throws ClassNotFoundException, RuntimeException, IOException {		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
//		String methodName = "dynamicExample1";
//		int parameterNum = 2;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		EvosuiteForMethod evoTest = new EvosuiteForMethod();
		ClassLoader classLoader = URLClassLoader.getSystemClassLoader();
		String[] classes = {Properties.TARGET_CLASS};
		
		int i = ListMethods.execute(classes, classLoader, MethodFilterOption.BRANCHWISE_METHOD,
				cp);
		assert i == 12;
				
	}
	
	@Test
	public void testSmartSeedFilter2() throws ClassNotFoundException, RuntimeException, IOException {		
		Class<?> clazz = feature.smartseed.example.empirical.EmpiricalStudyExample.class;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		EvosuiteForMethod evoTest = new EvosuiteForMethod();
		ClassLoader classLoader = URLClassLoader.getSystemClassLoader();
		String[] classes = {Properties.TARGET_CLASS};
		
		int i = ListMethods.execute(classes, classLoader, MethodFilterOption.BRANCHWISE_METHOD,
				cp);
		assert i == 5;
				
	}
}
