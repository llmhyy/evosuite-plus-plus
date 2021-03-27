package feature.smartseed.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.result.BranchDynamicAnalyzer;
import org.evosuite.seeding.smart.BranchSeedInfo;
import org.evosuite.seeding.smart.SmartSeedBranchUpdateManager;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;
import feature.smartseed.example.SmartSeedExample;
import feature.smartseed.example.empirical.EmpiricalStudyExample;
import feature.smartseed.example.empirical.ResourcesDirectory;

public class SmartSeedRuntimeEvaluatorTest {
	@Before
	public void init() {
		Properties.APPLY_SMART_SEED = true;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.TIMEOUT = 10000000;
//		Properties.ENABLE_TRACEING_EVENT = true;
		//Properties.RUNTIMEBRANCH = true;
		
		Properties.APPLY_GRADEINT_ANALYSIS = true;
		Properties.CHROMOSOME_LENGTH = 5;
	}
	
	@Test
	public void testFindBranchHeadInfo0() throws IOException {
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "dynamicExample1";
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
		boolean ass = true;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo1() throws IOException {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA",  0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	@Test
	public void testFindBranchHeadInfo2() throws IOException {
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample1";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	@Test
	public void testFindBranchHeadInfo3() {
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample2";
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
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
		
	}
	
	@Test
	public void testFindBranchHeadInfo4() {
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample3";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo5() {
		
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "staticExample4";
		int parameterNum = 4;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	
	@Test
	public void testFindBranchHeadInfo6() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "dynamicExample1";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo7() {
		Class<?> clazz = EmpiricalStudyExample.class;
		String methodName = "addMenuItem";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo8() {
		Class<?> clazz = EmpiricalStudyExample.class;
		String methodName = "parse";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo9() {
		Class<?> clazz = EmpiricalStudyExample.class;
		String methodName = "accept";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo10() {
		Class<?> clazz = EmpiricalStudyExample.class;
		String methodName = "loadInstructions";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo11() {
		Class<?> clazz = ResourcesDirectory.class;
		String methodName = "addResource";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo12() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "equalsIgnoreCaseExample";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo13() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "stratWithExample";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	@Test
	public void testFindBranchHeadInfo14() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "endWithExample";
		int parameterNum = 2;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	@Test
	public void testFindBranchHeadInfo15() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "matchesExample";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo16() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "patternMatchesExample";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
	
	@Test
	public void testFindBranchHeadInfo17() {
		Class<?> clazz = SmartSeedExample.class;
		String methodName = "combinationExample";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 5;
		Long seed = null;
				
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		boolean hasBranchHead = false;
		for(BranchSeedInfo branchInfo : SmartSeedBranchUpdateManager.totalUncoveredGoals) {
			Branch b = branchInfo.getBranch();
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			if(BranchDynamicAnalyzer.branchHead.containsKey(b)) {
				hasBranchHead = true;
			}
		}
		assert hasBranchHead;
	}
}
