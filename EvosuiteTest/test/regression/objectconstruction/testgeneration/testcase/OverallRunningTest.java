package regression.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUtility;

import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class ObjectConstructionTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
	}
	
	@Test
	public void testCascadingCallExample() {
		Class<?> clazz = regression.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample.class;

		String methodName = "targetM";
		int parameterNum = 0;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		EvoTestResult resultT = null;
		EvoTestResult resultF = null;

		try {
			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		} catch (Exception e) {
			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		}

		Properties.APPLY_OBJECT_RULE = false;
		
		try {
			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		} catch (Exception e) {
			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		}

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();

		assert ageT <= 1;
		assert timeT <= 1;
		assert ageT < ageF;
		assert timeT <= timeF;
		assert coverageT == 1.0;
	}
	
	// pending debugging
	@Test
	public void testCorinaPngencoderExample() {
		String projectId = "35_corina";
		String[] targetMethods = new String[]{
				"corina.map.PngEncoderB#pngEncode(Z)[B"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1590296724051L;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testDsachatFrameExample() {
		String projectId = "12_dsachat";
		String[] targetMethods = new String[]{
				"dsachat.gm.gui.InternalGmHeroFrame#valueChanged(Ljavax/swing/event/TreeSelectionEvent;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1590297701552L;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testGangupBasicRulesExample() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testJfxExample() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.ChkOrdAudRs_TypeSequence2#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testJhandballMovesExample() {
		String projectId = "56_jhandballmoves";
		String[] targetMethods = new String[]{
				"visu.handball.moves.model.HandballModel#setMoveName(Ljava/lang/String;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testJwbfArticleExample() {
		String projectId = "23_jwbf";
		String[] targetMethods = new String[]{
				"net.sourceforge.jwbf.core.contentRep.Article#getRevisionId()Ljava/lang/String;"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testLilithExample() {
		String projectId = "43_lilith";
		String[] targetMethods = new String[]{
				"de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufDecoder#convert(Lde/huxhorn/lilith/data/logging/protobuf/generated/LoggingProto$LoggingEvent;)Lde/huxhorn/lilith/data/logging/LoggingEvent;"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testObjectexplorerExample() {
		String projectId = "63_objectexplorer";
		String[] targetMethods = new String[]{
				"de.paragon.explorer.figure.ListBoxFigureBuilder#setStaticAttributesVisible(Lde/paragon/explorer/figure/ListBoxFigure;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testOpenhreExample() {
		String projectId = "75_openhre";
		String[] targetMethods = new String[]{
				"com.browsersoft.openhre.hl7.impl.regular.ExpressionNodeList#addExpressionList(Lcom/browsersoft/openhre/hl7/impl/regular/ExpressionNodeList;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testOpenjmsExample() {
		String projectId = "66_openjms";
		String[] targetMethods = new String[]{
				"org.exolab.jms.net.rmi.RMIManagedConnectionAcceptor#close()V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testSummaExample() {
		String projectId = "44_summa";
		String[] targetMethods = new String[]{
				"dk.statsbiblioteket.summa.common.filter.object.MUXFilter#pump()Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testWekaArjarchiveentryExample() {
		String projectId = "101_weka";
		String[] targetMethods = new String[]{
				"org.apache.commons.compress.archivers.arj.ArjArchiveEntry#isDirectory()Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
	@Test
	public void testXbusFtpsenderExample() {
		String projectId = "83_xbus";
		String[] targetMethods = new String[]{
				"net.sf.xbus.technical.ftp.FTPSender#execute(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.APPLY_OBJECT_RULE = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}
	
}
