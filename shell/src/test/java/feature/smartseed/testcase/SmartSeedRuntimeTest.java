package feature.smartseed.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.SeedStrategyUtil;
import common.TestUtility;
import evosuite.shell.EvoTestResult;
import feature.smartseed.example.SmartSeedExample;
import sf100.CommonTestUtil;

public class SmartSeedRuntimeTest {
	
	@Test
	public void testConsturctingConstantPool4UncoveredBranch1() throws IOException {
		Properties.APPLY_SMART_SEED = true;
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample1";
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
		
		int repeatTime = 30;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 1.0, 0.0);	
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
	public void testConsturctingConstantPool4UncoveredBranch2() {
		Properties.APPLY_SMART_SEED = true;
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample2";
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
		
		int repeatTime = 30;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 1.0, 0.0);	
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
	public void testConsturctingConstantPool4UncoveredBranch3() {
		Properties.APPLY_SMART_SEED = true;
		
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
		
		int repeatTime = 30;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, false, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 1.0, 0.0);	
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
	public void testConsturctingConstantPool4UncoveredBranch4() {
		Properties.APPLY_SMART_SEED = true;
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample4";
		int parameterNum = 4;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 1000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 100;
		
		int repeatTime = 30;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 1.0, 0.0);	
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
	public void testConsturctingDynamicPool4UncoveredBranch() {
		Properties.APPLY_SMART_SEED = true;
		
		//TODO Cheng Yan
	}
}
