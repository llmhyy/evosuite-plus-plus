package feature.hybrid;

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
import sf100.CommonTestUtil;

public class SeedStrategyTesting {
	@Test
	public void test() throws IOException {
		Class<?> clazz = feature.hybrid.example.SeedStrategyExample.class;
		String methodName = "removeComment";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

//		 Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		

		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 300000000;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		String fitnessApproach = "branch";
		
		int timeBudget = 10;
		
		int repeatTime = 30;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;

		SeedStrategyUtil.evosuiteDynaMOSA(targetClass, targetMethod,cp,fitnessApproach,
				repeatTime,timeBudget,true,seed,
				aor,0.0,0.0);		
	}
}
