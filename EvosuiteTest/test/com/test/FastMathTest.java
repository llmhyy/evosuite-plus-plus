package com.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

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
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.LOCAL_SEARCH_BUDGET = 1000;

		Properties.TIMEOUT = 30000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 300;
		FastMathTest t = new FastMathTest();
//		EvoTestResult tuple1 = t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		System.out.println("coverage:" + tuple1.getCoverage());

		
		List<EvoTestResult> l = new ArrayList<>();
		for(int i=0; i<5; i++){
			try {
				EvoTestResult tu = t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
				l.add(tu);		
//				Thread.sleep(60000);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		for(EvoTestResult lu: l){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
	}

	

}
