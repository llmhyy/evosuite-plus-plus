package com.test;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Test;

public class ETest2 extends AbstractETest{
	
	@Test
	public void test() {
		Class<?> clazz = Example1.class;
		String methodName = "example";
		int parameterNum = 4;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = getTragetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + getSignature(method);
		String cp = "target/classes;lib/commons-math-2.2.jar";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 10000000;
		ETest2 t = new ETest2();
		Tuple tu = t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
	}

	

}
