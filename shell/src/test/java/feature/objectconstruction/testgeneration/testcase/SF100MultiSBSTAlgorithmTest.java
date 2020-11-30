package feature.objectconstruction.testgeneration.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.statistics.RuntimeVariable;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class SF100MultiSBSTAlgorithmTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
	}

	@Test
	public void testDynaMOSA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.CustDiscRec#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA2() {
		String projectId = "13_jdbacl";
		String[] targetMethods = new String[]{
				"org.databene.jdbacl.model.DBDataType#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA3() {
		String projectId = "45_lotus";
		String[] targetMethods = new String[]{
				"lotus.core.phases.DeclareBlockersPhase#doPhase()V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA4() {
		String projectId = "83_xbus";
		String[] targetMethods = new String[]{
				"net.sf.xbus.protocol.xml.XBUSXMLMessage#synchronizeResponseFields(Lnet/sf/xbus/base/xbussystem/XBUSSystem;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA5() {
		String projectId = "6_jnfe";
		String[] targetMethods = new String[]{
				"br.com.jnfe.core.filter.JNFeFilterAdapter#doFilter(Lorg/helianto/core/criteria/OrmCriteriaBuilder;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 50;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA6() {
		String projectId = "80_wheelwebtool";
		String[] targetMethods = new String[]{
				"wheel.components.Checkbox#renderComponent(Lorg/xmlpull/v1/XmlSerializer;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA7() {
		String projectId = "44_summa";
		String[] targetMethods = new String[]{
				"dk.statsbiblioteket.summa.storage.api.filter.UpdateFromFulldumpFilter#init(Ldk/statsbiblioteket/summa/common/configuration/Configuration;Ldk/statsbiblioteket/summa/storage/api/WritableStorage;Ldk/statsbiblioteket/summa/storage/api/ReadableStorage;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA8() {
		String projectId = "35_corina";
		String[] targetMethods = new String[]{
				"corina.index.Polynomial#index()V"
			};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA9() {
		String projectId = "32_httpanalyzer";
		String[] targetMethods = new String[]{
				"httpanalyzer.ParamsUrlTools#splitUrl(Ljava/lang/String;Ljavax/swing/JFrame;)[Ljava/lang/String;"
			};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testMOSA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRsSequence#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testMonotonicGA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.CreditAuthCanRsSequence2#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 3;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateSuite", "Evosuite", "Monotonic_GA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testMonotonicGA2() {
		String projectId = "96_heal";
		String[] targetMethods = new String[]{
				"org.heal.module.metadata.ContributorBean#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateSuite", "Evosuite", "Monotonic_GA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
//	@Test
	public void testMOSA() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testMonotonicGA() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor, "generateSuite", "Evosuite", "Monotonic_GA");
//		
//		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		double initCoverage = resultF.getInitialCoverage();
		
		System.currentTimeMillis();
	}
}
