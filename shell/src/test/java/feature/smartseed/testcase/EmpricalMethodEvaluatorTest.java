package feature.smartseed.testcase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.result.BranchDynamicAnalyzer;
import org.evosuite.seeding.smart.BranchSeedInfo;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.seeding.smart.SmartSeedBranchUpdateManager;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.SensitivityMutator;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;
import evosuite.shell.EvoTestResult;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import feature.smartseed.example.SmartSeedExample;
import feature.smartseed.example.empirical.Config;
import feature.smartseed.example.empirical.EmpiricalStudyExample;
import feature.smartseed.example.empirical.ResourcesDirectory;
import sf100.CommonTestUtil;

public class EmpricalMethodEvaluatorTest {
	@Before
	public void init() {
		Properties.TIMEOUT = 300000000;
//		Properties.RANDOM_SEED = 1606757586999l;
//		Properties.INSTRUMENT_CONTEXT = true;
//		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		Properties.APPLY_GRADEINT_ANALYSIS_IN_SMARTSEED = true;
		
		Properties.APPLY_SMART_SEED = true;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		Properties.ENABLE_TRACEING_EVENT = true;
		
		Properties.APPLY_GRADEINT_ANALYSIS = true;
		Properties.CHROMOSOME_LENGTH = 5;
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
