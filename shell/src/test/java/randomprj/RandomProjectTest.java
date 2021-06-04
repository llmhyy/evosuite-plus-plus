package randomprj;

import java.io.File;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.result.TestGenerationResult;
import org.junit.Before;
import org.junit.Test;

public class RandomProjectTest{
	
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

//		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
//		Properties.APPLY_OBJECT_RULE = true;
//		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
//		Properties.ADOPT_SMART_MUTATION = false;
//		
//		Properties.INSTRUMENT_CONTEXT = true;
//		Properties.CHROMOSOME_LENGTH = 200;
//		
////		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 20;
////		Properties.TOTAL_LEGITIMIZATION_BUDGET = 50;
//		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 0;
//		Properties.TOTAL_LEGITIMIZATION_BUDGET = 0;
//		Properties.TIMEOUT = 1000;
////		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
	}
	
	
	@Test
	public void testBugExample() {
		String targetClass = "com.alibaba.fastjson.JSONObject";
		String targetMethod = "(Ljava/lang/String;)F";
		String projectRoot = "D:\\linyun\\git_space\\reg\\subject-repo\\fastjason\\project\\";
		
		//TODO we may still need to parse maven class path
		String cp = projectRoot + File.separator + "target/classes" + File.pathSeparator + 
				projectRoot + File.separator + "target/test-classes";
		
		evoTestSmartSeedMethod(targetClass,  
				targetMethod, cp, 10, 
				"generateMOSuite", "MOSUITE", "DynaMOSA");	
	}
	
	public static void evoTestSmartSeedMethod(String targetClass, String targetMethod, String cp,
			long seconds, 
			String option,
			String strategy,
			String algorithm) {
		/* configure */
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		String[] args = new String[] {
				"-"+option,
				"-Dstrategy", strategy,
				"-Dalgorithm", algorithm,
				"-Dcriterion", "branch",
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dmax_attempts", "100",
				"-Dassertions", "true",
		};
		
		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(args);
	}

}
