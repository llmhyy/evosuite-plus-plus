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
import feature.smartseed.example.empirical.EmpiricalStudyExample;
import feature.smartseed.example.empirical.ResourceDescriptor;
import feature.smartseed.example.empirical.ResourcesDirectory;
import feature.smartseed.example.empirical.SimpleNode;

public class SmartSeedRuntimeTest {
	
	@Before
	public void init() {
		Properties.APPLY_SMART_SEED = true;
		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.STATISTICS_BACKEND = StatisticsBackend.CSV;
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
		
		assert res1.getCoverage() <= 1.0;
		assert res1.getAge() > 15;
	}
	
	@Test
	public void testTrueCase2On() throws IOException {
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
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 5;
		
		
	}
	
	@Test
	public void testTrueCase2Off() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "test";
		int parameterNum = 2;
		
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

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 15;
		
		
	}
	
	@Test
	public void testTrueCase3On() throws IOException {
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
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 50;
		
		
	}
	
	@Test
	public void testTrueCase3Off() throws IOException {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "fieldTest";
		int parameterNum = 2;
		
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

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 15;
		
		
	}
	
	
	@Test
	public void testParallelOn() {
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
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 10;
		
	}
	
	@Test
	public void testParallelOff() {
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
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 15;
		
	}
	
	@Test
	public void testHighQualityExampleOn() {
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
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 10;
	}
	
	@Test
	public void testHighQualityExampleOff() {
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
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 10;
	}
	
	
//	@Test
//	public void testCompareTwoVariablesOn() throws IOException {
//		Class<?> clazz = SmartSeedExample.class;
//		String methodName = "dynamicExample1";
//		int parameterNum = 2;
//		
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/classes;target/test-classes";
//
//		String fitnessApproach = "branch";
//		
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//				
//		boolean aor = false;
//		boolean ass = true;
//
//		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
//				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
//		
//		EvoTestResult res1 = results.iterator().next();
//		assert res1.getCoverage() == 1.0;
//		assert res1.getAge() < 20;
//	}
//	
//	@Test
//	public void testCompareTwoVariablesOff() throws IOException {
//		Class<?> clazz = SmartSeedExample.class;
//		String methodName = "dynamicExample1";
//		int parameterNum = 2;
//		
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/classes;target/test-classes";
//
//		String fitnessApproach = "branch";
//		
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//				
//		boolean aor = false;
//		boolean ass = false;
//
//		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
//				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
//		
//		EvoTestResult res1 = results.iterator().next();
//		assert res1.getCoverage() == 1.0;
//		assert res1.getAge() < 30;
//	}
	
	@Test
	public void testMatchesExampleOn() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "matchesExample";
		int parameterNum = 1;
		
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
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 5;
	}
	
	@Test
	public void testMatchesExampleOff() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "matchesExample";
		int parameterNum = 1;
		
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
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 5;
	}
	
	@Test
	public void testStringCompareOn() {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "stringCompare";
		int parameterNum = 1;
		
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
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 10;
	}
	
	@Test
	public void testStringCompareOff() {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "stringCompare";
		int parameterNum = 1;
		
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
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 5;
	}
	
	@Test
	public void testSpecialPointOn() {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "specialPoint";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 1000000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 50;
	}
	
	@Test
	public void testSpecialPointOff() {
		//worse example
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "specialPoint";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 10;
	}
	
	@Test
	public void testNoPoolOn() {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "parseStackTraceElement";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100000;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 50;
	}
	
	@Test
	public void testNoPoolOff() {
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "parseStackTraceElement";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 50;
	}
	
	
	@Test
	public void testElementIndex() {
		//no pool 
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "elementIndex";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 50;
	}
	
	@Test
	public void testJDKInstrument() {
		//no pool 
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "JDKInstrument";
		int parameterNum = 3;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 100;
	}
	
	@Test
	public void testStringCorrelationOn() {
		//string correlation 
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample3";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 100;
	}
	
	@Test
	public void testStringCorrelationOff() {
		//string correlation 
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample3";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 100;
	}
	
	@Test
	public void testStringContainCharOn() {
		//string char 
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "stringContains";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() < 50;
	}
	
	@Test
	public void testStringContainCharOff() {
		//string char 
		Class<?> clazz = feature.smartseed.example.truecase.TrueExample.class;
		String methodName = "stringContains";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		EvoTestResult res1 = results.iterator().next();
		assert res1.getCoverage() == 1.0;
		assert res1.getAge() > 10;
	}
		
}
