package com.test;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

public class OpenIntToFieldHashMapTest extends AbstractETest{
	
	@Test
	public void test() {
		Class<?> clazz = org.apache.commons.math.util.OpenIntToFieldHashMap.class;
		String methodName = "get";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = getTragetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;lib/commons-math-2.2.jar";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 30;

//		Properties.TIMEOUT = 3000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 2000000;
		OpenIntToFieldHashMapTest t = new OpenIntToFieldHashMapTest();
		t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}

	

}
