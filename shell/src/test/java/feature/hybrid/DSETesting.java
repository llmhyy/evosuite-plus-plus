package feature.hybrid;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.HybridOption;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtility;

public class DSETesting {
	@Test
	public void testEvosuiteSolver() {
		Class<?> clazz = feature.hybrid.example.HybridExample.class;
		String methodName = "longpath1";
		int parameterNum = 6;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

//		Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 200000;
		TestUtility.evosuiteEvosuiteDSE(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
	}
	
	@Test
	public void bashTestHybrid() {
		Class<?> clazz = feature.hybrid.example.HybridExample.class;
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
            String methodName = method.getName();
            int parameterNum = method.getParameterCount();
            
            String targetClass = clazz.getCanonicalName();
    		Method methodUnderTest = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

    		String targetMethod = methodUnderTest.getName() + MethodUtil.getSignature(methodUnderTest);
    		String cp = "target/classes;target/test-classes";

    		// Properties.LOCAL_SEARCH_RATE = 1;
//    		Properties.DEBUG = true;
//    		Properties.PORT = 8000;
    		Properties.CLIENT_ON_THREAD = true;
    		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
    		
//    		Properties.LOCAL_SEARCH_BUDGET = 1000000;

    		Properties.SEARCH_BUDGET = 60000;
    		Properties.GLOBAL_TIMEOUT = 60000;
    		Properties.TIMEOUT = 300000000;
//    		Properties.TIMELINE_INTERVAL = 3000;
    		
    		String fitnessApproach = "branch";
    		
    		int timeBudget = 3000;
    		TestUtility.evosuiteEvosuiteDSE(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
        }
		
	}
	
	@Test
	public void testZ3Solver() {
		Class<?> clazz = feature.hybrid.example.HybridExample.class;
		String methodName = "breakPalindrome";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

//		 Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 2000000;
		TestUtility.evosuiteZ3DSE(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
	}
	
	@Test
	public void testCVC4Solver() {
		Class<?> clazz = feature.hybrid.example.HybridExample.class;
		String methodName = "test";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

//		 Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 20;
		TestUtility.evosuiteCVC4DSE(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
	}
}
