package feature.fbranch.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;

public class ProjectFlagEffectTest{
	@Before
	public void init() {
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
		Properties.DEBUG = true;
		Properties.APPLY_OBJECT_RULE = false;
	}
	
	@Test
	public void testExample() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.fbranch.example.FlagEffectExample.class;
		String methodName = "example";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes;target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
		
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		Properties.TT = true;

		Properties.TIMEOUT = 1000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 100000;
		
		boolean aor = true;
		TestUtility.evoTestSingleMethod(targetClass,  
				targetMethod, timeBudget, true, aor, cp, fitnessApproach, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");
		
	}
	
}
