package feature.fbranch.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class BranchDistanceGradientTest extends FBranchTestSetup{
	
	
	@Test
	public void testValueRangeExample() throws ClassNotFoundException, RuntimeException {
		Properties.TIMEOUT = 300000000;
		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		
		Class<?> clazz = feature.fbranch.example.ValueRangeExample.class;
		String methodName = "targetM";
		int parameterNum = 2;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		int lineNumber = 10;
		
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		TestCase test = TestUtil.initializeTest(targetBranch, TestFactory.getInstance(), false);
		TestChromosome individual = new TestChromosome();
		individual.setTestCase(test);
		
		BranchCoverageTestFitness fitness = BranchCoverageFactory.createBranchCoverageTestFitness(targetBranch, false);
		FBranchTestFitness ftt = new FBranchTestFitness(fitness.getBranchGoal());
		double fit = ftt.getFitness(individual);
		
		System.out.println("The fitness function is: " + fit);
		System.out.println("The random seed is: " + org.evosuite.utils.Randomness.getSeed());
		assert fit != 1;
		

		
	}
}
