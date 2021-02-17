package feature.smartseed.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.smart.BranchwiseConstantPoolManager;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

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
		
		//TODO Cheng Yan
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"it is a difficult string");
		assert hasRelevantValue;
		
		
	}
	

	@Test
	public void testBranchwiseDynamicEqualsIgnore() throws IOException {
		//TODO
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "equalsIgnoreCaseExample";
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

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		//TODO Cheng Yan
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"ignorecase");
		assert hasRelevantValue;
		
	}
	
	@Test
	public void testBranchwiseDynamicStarWith() throws IOException {
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "stratWithExample";
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

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"right");
		assert hasRelevantValue;		
	}
	
	@Test
	public void testBranchwiseDynamicEndWith() throws IOException {
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "endWithExample";
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

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"end");
		assert hasRelevantValue;		
	}
	
	@Test
	public void testBranchwiseDynamicMatchesWith() throws IOException {
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "matchesExample";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
				
		boolean aor = false;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"tr");
		assert hasRelevantValue;		
	}
	
	@Test
	public void testBranchwiseDynamicPatternMatchesWith() throws IOException {
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "patternMatchesExample";
		int parameterNum = 1;
		
		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/classes;target/test-classes";

		String fitnessApproach = "branch";
		
		int repeatTime = 1;
		int budget = 1000;
		Long seed = null;
				
		boolean aor = false;

		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, true, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"colours");
		assert hasRelevantValue;		
	}
	
	@Test
	public void testBranchwiseDynamicContains() throws IOException {
		Class<?> clazz = InstrumentationExample.class;
		String methodName = "containExample";
		int parameterNum = 1;
		
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
		
		//TODO Cheng Yan
		int branchID = getRelevantBranchID();
		ConstantPool pool = BranchwiseConstantPoolManager.DYNAMIC_POOL_CACHE.get(branchID);
		boolean hasRelevantValue = getRelevantValue(pool,"kkkkk");
		assert hasRelevantValue;
		
		
	}

	private int getRelevantBranchID() {
		BytecodeInstruction instruction = null;
		ClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		List<BytecodeInstruction> instructions =BytecodeInstructionPool.getInstance(classLoader).getAllInstructionsAtMethod(Properties.TARGET_CLASS, Properties.TARGET_METHOD);
		for(BytecodeInstruction ins : instructions) {
			AbstractInsnNode node = ins.getASMNode();
			System.currentTimeMillis();
			if(node.getType() == AbstractInsnNode.LDC_INSN) {
				LdcInsnNode ldc = (LdcInsnNode) node;
				String cla = Properties.TARGET_CLASS.replace('.', '/');
				if(ldc.cst.equals(cla + "#" +Properties.TARGET_METHOD)) {
					instruction = ins;
					break;
				}
			}
		}
		
		while(!instruction.isBranch()) {
			instruction = instruction.getNextInstruction();
		}
		
		if(instruction != null) {
			if (BranchPool.getInstance(classLoader).isKnownAsBranch(instruction)) {
				Branch b = BranchPool.getInstance(classLoader).getBranchForInstruction(instruction);
				if (b == null)
					return -1;

				return b.getActualBranchId();
			}
		}
		
		return -1;
	}
	
	private boolean getRelevantValue(ConstantPool pool, Object object) {
		Class type = object.getClass();
		
		if(type.equals(String.class)) {
			String s;
			while(true) {
				s = pool.getRandomString();
				if(s.contains((CharSequence) object))
					return true;
			}			
		}
		
		return false;
	}
}
