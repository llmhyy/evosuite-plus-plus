package feature.objectconstruction.testgeneration.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import common.SF100Project;
import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class IncompleteOcgTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		Properties.ADOPT_SMART_MUTATION = false;
		
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CHROMOSOME_LENGTH = 200;
		
//		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 20;
//		Properties.TOTAL_LEGITIMIZATION_BUDGET = 50;
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 0;
		Properties.TOTAL_LEGITIMIZATION_BUDGET = 0;
		Properties.TIMEOUT = 1000;
//		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
	}
	
	@Test
	public void firstExample() {
		String projectId = SF100Project.P69;
		String[] targetMethods = new String[]{
			"macaw.presentationLayer.VariableSearchPanel#hasSearchResults()Z"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 3600 * 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void secondExample() {
		String projectId = "125_jedit";
		String[] targetMethods = new String[]{
			"org.gjt.sp.jedit.gui.PasteSpecialDialog#ok()V"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 3600 * 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
}
