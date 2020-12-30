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
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class StatisticalFlagBranchEvaluatorTest {
	@Test
	public void testFlagExample1() throws ClassNotFoundException, RuntimeException {
		Properties.TIMEOUT = 300000000;
		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
//		Properties.TT = true;
		
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example";
		int parameterNum = 2;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		int lineNumber = 6;
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		double flagValue = StatisticBranchFlagEvaluator.evaluate(targetBranch);
		
		//TODO
		assert flagValue == 0.123;
		

		
	}
}
