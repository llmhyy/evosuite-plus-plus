package com.test;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import evosuite.shell.EvoTestResult;

/**
 * TODO: ziheng
 * need to specify the expected results 
 */
public class BranchEnhancementTest {
	@Test
	public void testColtExample() {
//		Class<?> clazz = com.example.TestV1.class;
//		Class<?> clazz = com.example.PassedExample1.class;
//		Class<?> clazz = com.example.PassedExample2.class;
//		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
//		Class<?> clazz = com.example.passedExamples.ConstructorAndCallExample.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
		Class<?> clazz = com.example.passedExamples.ColtExample.class;
//		Class<?> clazz = com.example.PassedExampleColt.class;

		String methodName = "mergeSortInPlace";
//		String methodName = "test";
//		String methodName = "mergeSortInPlace";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 100000;
		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.currentTimeMillis();
	}
	
	@Test
	public void testInevitableConstructorExample() {
//		Class<?> clazz = com.example.TestV1.class;
//		Class<?> clazz = com.example.PassedExample1.class;
//		Class<?> clazz = com.example.PassedExample2.class;
//		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
//		Class<?> clazz = com.example.passedExamples.ColtExample.class;
//		Class<?> clazz = com.example.PassedExampleColt.class;

		String methodName = "targetM";
//		String methodName = "test";
//		String methodName = "mergeSortInPlace";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 100000;
		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.currentTimeMillis();
	}
	
	@Test
	public void testLayeredCallExample() {
//		Class<?> clazz = com.example.TestV1.class;
//		Class<?> clazz = com.example.PassedExample1.class;
//		Class<?> clazz = com.example.PassedExample2.class;
//		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
//		Class<?> clazz = com.example.passedExamples.ConstructorAndCallExample.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
		Class<?> clazz = com.example.passedExamples.LayeredCallExample.class;
//		Class<?> clazz = com.example.PassedExampleColt.class;

		String methodName = "targetM";
//		String methodName = "test";
//		String methodName = "mergeSortInPlace";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 100000;
		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.currentTimeMillis();
	}
	
//	@Test
//	public void testLayeredCallExample2() {
////		Class<?> clazz = com.example.TestV1.class;
////		Class<?> clazz = com.example.PassedExample1.class;
////		Class<?> clazz = com.example.PassedExample2.class;
////		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
////		Class<?> clazz = com.example.passedExamples.ConstructorAndCallExample.class;
////		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
////		Class<?> clazz = com.example.PassedExampleColt.class;
//
//		String methodName = "targetM";
////		String methodName = "test";
////		String methodName = "mergeSortInPlace";
//		int parameterNum = 3;
//		
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/classes";
//
//		// Properties.LOCAL_SEARCH_RATE = 1;
////		Properties.DEBUG = true;
////		Properties.PORT = 8000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		Properties.BRANCH_COMPARISON_TYPES = true;
//		Properties.TIMEOUT = 10000000;
////		Properties.TIMELINE_INTERVAL = 3000;
//		
//		String fitnessApproach = "fbranch";
//		
//		int timeBudget = 100000;
//		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		System.currentTimeMillis();
//	}
	
	@Test
	public void testMultipleConstructorsExample() {
//		Class<?> clazz = com.example.TestV1.class;
//		Class<?> clazz = com.example.PassedExample1.class;
//		Class<?> clazz = com.example.PassedExample2.class;
//		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
//		Class<?> clazz = com.example.passedExamples.ConstructorAndCallExample.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
		Class<?> clazz = com.example.passedExamples.MultipleConstructorsExample.class;
//		Class<?> clazz = com.example.PassedExampleColt.class;

		String methodName = "targetM";
//		String methodName = "test";
//		String methodName = "mergeSortInPlace";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 100000;
		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.currentTimeMillis();
	}
	
	@Test
	public void testMultipleExceptionExample() {
//		Class<?> clazz = com.example.TestV1.class;
//		Class<?> clazz = com.example.PassedExample1.class;
//		Class<?> clazz = com.example.PassedExample2.class;
//		Class<?> clazz = com.example.passedExamples.InevitableConstructorExample.class;
//		Class<?> clazz = com.example.passedExamples.ConstructorAndCallExample.class;
//		Class<?> clazz = com.example.passedExamples.LayeredCallExample2.class;
		Class<?> clazz = com.example.passedExamples.MultipleExceptionExample.class;
//		Class<?> clazz = com.example.PassedExampleColt.class;

		String methodName = "test";
//		String methodName = "test";
//		String methodName = "mergeSortInPlace";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.BRANCH_COMPARISON_TYPES = true;
		Properties.TIMEOUT = 10000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "fbranch";
		
		int timeBudget = 100000;
		TestUility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		System.currentTimeMillis();
	}
}
