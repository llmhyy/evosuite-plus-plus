package feature.smartseed.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;
import feature.smartseed.example.SmartSeedExample;
import feature.smartseed.example.empirical.Config;
import feature.smartseed.example.empirical.ResourceDescriptor;

public class SmartSeedRuntimeTest {
	
	@Before
	public void init() {
		Properties.APPLY_SMART_SEED = true;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.CSV;
		Properties.TIMEOUT = 10000000;
		Properties.ENABLE_TRACEING_EVENT = true;
		
//		Properties.APPLY_GRADEINT_ANALYSIS = true;
//		Properties.CHROMOSOME_LENGTH = 5;
	}
	
	@Test
	public void testTrueCase1On() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "test";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;

		List<EvoTestResult> results1 = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results1.iterator().next();
		
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 10;
	}
	
	
	@Test
	public void testTrueCase1Off() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "test";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = false;

		List<EvoTestResult> results1 = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results1.iterator().next();
		
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 15;
	}
	
	@Test
	public void testTrueCase2() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "test";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 10000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
		
		
	}
	
	@Test
	public void testTrueCase3() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "fieldTest";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 10000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
		
		
	}
	
	@Test
	//array
	public void testTrueCase4() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "testArray";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 10000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
		
		
	}
	
	@Test
	public void parallelTest() {
		//parallel
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "paralleltest";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 100;
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testHighQualityExample() {
		//boolean
		Class<?> clazz = Config.class;
		String methodName = "isHighQuality";
		int parameterNum = 0;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 100;
		
		int repeatTime = 1;
		int budget = 100000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	
	@Test
	public void compareTwoVariablesTest() throws IOException {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "dynamicExample1";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 10000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testContains() {
		Class<?> clazz = ResourceDescriptor.class;
		String methodName = "contains";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 100;
		
		int repeatTime = 1;
		int budget = 100000;
		Long seed = null;
		
		boolean aor = false;
		boolean ass = true;
		
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	
}
