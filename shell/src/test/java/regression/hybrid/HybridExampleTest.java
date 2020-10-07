package regression.hybrid;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.HybridOption;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUtility;

public class HybridExampleTest {
	
	@Before
	public void init() {
		Properties.OVERALL_HYBRID_STRATEGY_TIMEOUT = 100000;
//		Properties.HYBRID_OPTION = new HybridOption[]{
//		    	HybridOption.DSE, HybridOption.RANDOM, HybridOption.SEARCH
//	    };
		
//		Properties.HYBRID_OPTION = new HybridOption[]{
//		    	HybridOption.RANDOM
//	    };
	}
	
	@Test
	public void testHybrid() {
		
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
//		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "function";
		int parameterNum = 3;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void bashTestHybrid() {
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
	    };
		
//		Method[] methods = clazz.getMethods();
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
    		
    		String fitnessApproach = "fbranch";
    		
    		int timeBudget = 30000;
    		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
        }
		
	}
	
	@Test
	public void testTest() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
//		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "test";
		int parameterNum = 2;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testLongpath1() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
//		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "longpath1";
		int parameterNum = 6;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testBreakPalindrome() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
//		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "breakPalindrome";
		int parameterNum = 1;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testBranch2() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
//		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "branch2";
		int parameterNum = 2;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testNonlinear2() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "nonlinear2";
		int parameterNum = 2;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testCountCompare() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "countCompare";
		int parameterNum = 2;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testPalindrome() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
//		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "Palindrome";
		int parameterNum = 1;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testNonlinear1() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "nonlinear1";
		int parameterNum = 4;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testLongpath() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.RANDOM
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "longpath";
		int parameterNum = 7;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
	
	@Test
	public void testIfnull() {
		Properties.HYBRID_OPTION = new HybridOption[]{
		    	HybridOption.DSE
	    };
		
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "ifnull";
		int parameterNum = 1;
				
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000000;

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 30000;
		TestUtility.evosuiteHybrid(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}
}
