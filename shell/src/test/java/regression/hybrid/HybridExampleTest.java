package regression.hybrid;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUtility;

public class HybridExampleTest {
	@Test
	public void testHybrid() {
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		String methodName = "multiply";
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
	public void bashTestHybrid() {
		Class<?> clazz = regression.hybrid.example.HybridExample.class;
		
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
}
