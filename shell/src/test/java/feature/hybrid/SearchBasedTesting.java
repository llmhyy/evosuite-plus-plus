package feature.hybrid;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtility;

public class SearchBasedTesting {
	@Test
	public void test() {
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
		
		int timeBudget = 300;
//		TestUtility.evosuiteGA(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		TestUtility.evosuiteMOSA(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		TestUtility.evosuiteDynaMOSA(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		
//		List<Tuple> l = new ArrayList<>();
//		for(int i=0; i<7; i++){
//			Tuple tu = t.evosuite(targetClass, targetMethod, cp, timeBudget, true);
//			l.add(tu);
//		}
//		
//		for(Tuple lu: l){
//			System.out.println(lu.time + ", " + lu.age);
//		}
	}
}
