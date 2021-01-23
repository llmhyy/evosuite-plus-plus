package feature.smartseed.testcase;

import java.lang.reflect.Method;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtility;
import feature.smartseed.example.SmartSeedExample;

public class SmartSeedRuntimeTest {
	
	@Test
	public void testConsturctingConstantPool4UncoveredBranch() {
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
		
		boolean aor = true;
		TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, timeBudget, true, aor, cp, fitnessApproach, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");
	}
	
	
	
	
	@Test
	public void testConsturctingDynamicPool4UncoveredBranch() {
		Properties.APPLY_SMART_SEED = true;
		
		//TODO Cheng Yan
	}
}
