package com.test;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

public class FastMathTest extends AbstractETest{
	
	@Test
	public void test() {
		Class<?> clazz = org.apache.commons.math.util.FastMath.class;
		String methodName = "pow";
		int parameterNum = 2;
		
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
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000;

//		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 300;
		FastMathTest t = new FastMathTest();
		Tuple tuple1 = t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.out.println("coverage:" + tuple1.coverage);
//		Tuple tuple2 = t.evosuite(targetClass, targetMethod, cp, timeBudget, true, "branch");
//		
//		System.out.println("fbranch: " + tuple1.coverage);
//		System.out.println("fbranch: " + tuple2.coverage);
	}

	

}
