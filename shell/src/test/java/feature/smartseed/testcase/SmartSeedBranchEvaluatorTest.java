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
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class SmartSeedBranchEvaluatorTest {
	@Test
	public void testSmartSeedBranch1() throws ClassNotFoundException, RuntimeException {
		Properties.TIMEOUT = 300000000;
		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		Properties.APPLY_SMART_SEED = true;
		
		Class<?> clazz = feature.smartseed.example.SmartSeedExample.class;
		String methodName = "staticExample2";
		int parameterNum = 2;
		int lineNumber = 24;

		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
		Properties.TARGET_METHOD = method.getName() + MethodUtil.getSignature(method);
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		
		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		
		List<Branch> branches = BranchPool.getInstance(classLoader).getBranchesForMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		
		Branch targetBranch = TestUtil.searchBranch(branches, lineNumber);
		
		int type = SeedingApplicationEvaluator.evaluate(targetBranch);
		
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		

		
	}
}
