package com.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 30000;
//		Properties.TIMELINE_INTERVAL = 3000;
		int timeBudget = 3000;
		
		int repeat = 1;
		
		String fitnessApproach = "fbranch";
		List<EvoTestResult> l1 = runRepetativeTimes(targetClass, targetMethod, cp, timeBudget, fitnessApproach, repeat);
		
//		fitnessApproach = "branch";
//		List<EvoTestResult> l2 = runRepetativeTimes(targetClass, targetMethod, cp, timeBudget, fitnessApproach, repeat);
//		
//		System.out.println("fbranch" + ":");
//		for(EvoTestResult lu: l1){
//			System.out.println(lu.getCoverage());
//			System.out.println(lu.getProgress());
//		}
//		
//		System.out.println("branch" + ":");
//		for(EvoTestResult lu: l2){
//			System.out.println(lu.getCoverage());
//			System.out.println(lu.getProgress());
//		}
	}

	private List<EvoTestResult> runRepetativeTimes(String targetClass, String targetMethod, String cp, int timeBudget, String fitnessApproach, int repeat) {
		FastMathTest t = new FastMathTest();
		List<EvoTestResult> l = new ArrayList<>();
		for(int i=0; i<repeat; i++){
			try {
				EvoTestResult tu = t.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
				l.add(tu);		
//				Thread.sleep(60000);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return l;
	}

}
