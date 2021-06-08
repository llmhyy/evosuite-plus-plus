package feature.smartseed.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import feature.smartseed.example.empirical.Config;
import feature.smartseed.example.empirical.constructor.DefaultDBColumn;

public class EmpricalMethodEvaluatorTest {
	@Before
	public void init() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
//		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
		
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CHROMOSOME_LENGTH = 200;
		
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 0;
		
		Properties.TIMEOUT = 100000;
		
		Properties.ENABLE_TRACEING_EVENT = true;
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_GRADEINT_ANALYSIS_IN_SMARTSEED = true;
		Properties.APPLY_GRADEINT_ANALYSIS = true;
//		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
	}
	
	public void writeResults() {
		ExcelWriter excelWriter = new ExcelWriter(evosuite.shell.FileUtils.newFile(Settings.getReportFolder(), "fastchannel_info.xlsx"));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		header.add("Branch");
		header.add("Path");
//		header.add("Fitness Value");
		header.add("Testcase");
		header.add("HeadValue");
		header.add("TailValue");
		header.add("NewTestcase");
		header.add("NewHeadValue");
		header.add("NewTailValue");
		header.add("ValuePreserving");
		header.add("SensivityPreserving");
		header.add("FastChannel");
//		header.add("Iteration");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
		
		try {
			excelWriter.writeSheet("data", SensitivityMutator.data);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	@Test 
	public void testHQ() {
		String[] stringArray0 = new String[7];
		String string0 = "0";
		stringArray0[0] = string0;
		String string1 = "0iia3dc|> Us%2";
		stringArray0[1] = string1;
		String string2 = ">16q M=`";
		stringArray0[2] = string2;
		String string3 = "9!ngC>!#ne_NFZ:zk/";
		stringArray0[3] = string3;
		String string4 = "iDwi!K";
		stringArray0[4] = string4;
		String string5 = "\"$J!Z";
		stringArray0[5] = string5;
		String string6 = "+";
		stringArray0[6] = string6;
		Config config0 = new Config(stringArray0);
		boolean boolean0 = config0.isHighQuality();


	}
		
	@Test
	public void testHighQuality() throws IOException {
		Class<?> clazz = Config.class;
		String methodName = "isHighQuality";
		int parameterNum = 0;
		
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
		
		List<EvoTestResult> results = TestUtility.evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp,fitnessApproach, repeatTime, budget, ass, true,
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5);	
		writeResults();
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
	public void testIsEquivalent() throws IOException {
		Class<?> clazz = DefaultDBColumn.class;
		String methodName = "isEquivalent";
		int parameterNum = 1;
		
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
		writeResults();
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
