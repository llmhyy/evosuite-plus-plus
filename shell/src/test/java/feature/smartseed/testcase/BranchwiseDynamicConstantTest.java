package feature.smartseed.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.smart.BranchwiseConstantPoolManager;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;
import feature.smartseed.example.InstrumentationExample;

public class BranchwiseDynamicConstantTest {
	
	@Before
	public void init() {
		Properties.APPLY_SMART_SEED = true;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.TIMEOUT = 10000000;
	}
	
	@Test
	public void testBranchwiseDynamicEquals() throws IOException {
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "equalsExample";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 1000000;
		Long seed = null;
				
		boolean aor = false;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		
		//TODO Cheng Yan
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		assert pool.getRandomString().equals("abc");
		
		
//		for(EvoTestResult res: results) {
//			
//			if(res == null) {
//				repeatTime--;
//				continue;
//			}
//			
//			coverage += res.getCoverage();
//			initCoverage += res.getInitialCoverage();
//			time += res.getTime();
//			iteration += res.getAge();
//		}
//		
//		System.out.println("coverage: " + coverage/repeatTime);
//		System.out.println("initCoverage: " + initCoverage/repeatTime);
//		System.out.println("time: " + time/repeatTime);
//		System.out.println("iteration: " + iteration/repeatTime);
//		System.out.println("repeat: " + repeatTime);
		
	}
	
	@Test
	public void testBranchwiseDynamicEqualsIgnore() throws IOException {
		//TODO
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "equalsExample";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 1000000;
		Long seed = null;
				
		boolean aor = false;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		
		//TODO Cheng Yan
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		assert pool.getRandomString().equals("abc");
		
	}

	private int getRelevantBranchID() {
		// TODO Auto-generated method stub
		return 0;
	}
}
