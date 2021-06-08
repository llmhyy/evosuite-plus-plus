package feature.objectconstruction.testgeneration.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.runtime.sandbox.Sandbox;
import org.junit.Before;
import org.junit.Test;

import common.SF100Project;
import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class SF100OverallTest {
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
	public void testBugExample() {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = "58_fps370";
		String projectId = SF100Project.P1;
		
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CustPayeeMsgRecChoice#equals(Ljava/lang/Object;)Z"
//				"org.objectweb.asm.jip.attrs.StackMapAttribute#getFrame(Lorg/objectweb/asm/jip/Label;)Lorg/objectweb/asm/jip/attrs/StackMapFrame;"
//				"state.Party#remove(Lstate/Party;)V"
//				"net.sourceforge.ifxfv3.beans.BankAcctTrnRec#equals(Ljava/lang/Object;)Z"
//				"net.sf.xbus.protocol.xml.XBUSXMLMessage#synchronizeResponseFields(Lnet/sf/xbus/base/xbussystem/XBUSSystem;)V"
//				"net.sourceforge.ifxfv3.beans.LoanInfoCommon#equals(Ljava/lang/Object;)Z"
//				"net.sf.xbus.protocol.xml.XBUSXMLMessage#synchronizeResponseFields(Lnet/sf/xbus/base/xbussystem/XBUSSystem;)V"
//				"wheel.components.Checkbox#renderComponent(Lorg/xmlpull/v1/XmlSerializer;)V"
//				"net.sourceforge.ifxfv3.beans.CCAcctStmtInqRs#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.PmtMsgRecChoice#equals(Ljava/lang/Object;)Z"
//				"de.paragon.explorer.model.AttributeModelComparator#compare(Lde/paragon/explorer/model/AttributeModel;Lde/paragon/explorer/model/AttributeModel;)I"
//				"net.sourceforge.ifxfv3.beans.PmtLegalRptData#equals(Ljava/lang/Object;)Z"
//				"corina.index.Horizontal#index()V"
//				"com.lts.scheduler.Scheduler#cancel(Lcom/lts/scheduler/ScheduledEventListener;)V"
//				"org.exolab.jms.gc.GarbageCollectionService#doStart()V"
//				"com.lts.caloriecount.data.CalorieCountData#setFrequentFoods(Lcom/lts/application/data/IdApplicationDataList;)V"
//				"de.outstare.fortbattleplayer.gui.battlefield.BattleFieldLayoutManager#layoutContainer(Ljava/awt/Container;)V"
//				"org.jcvi.trace.frg.DefaultFragment#equals(Ljava/lang/Object;)Z"
//				"com.lts.scheduler.Scheduler#cancel(Lcom/lts/scheduler/ScheduledEventListener;)V"
//				"de.huxhorn.lilith.services.clipboard.AccessUriFormatter#isCompatible(Ljava/lang/Object;)Z"
//				"state.GameState#unpack([B)V"
//				"de.beiri22.filedp.FileDiffPatch#createPatch(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V"
//				"com.lts.xmlser.tags.CollectionTag#write(Lcom/lts/xmlser/XmlSerializer;Lcom/lts/io/IndentingPrintWriter;Ljava/lang/String;Ljava/lang/Object;Z)V"
				"com.ib.client.Contract#equals(Ljava/lang/Object;)Z"
//				"com.lts.xmlser.tags.CollectionTag#write(Lcom/lts/xmlser/XmlSerializer;Lcom/lts/io/IndentingPrintWriter;Ljava/lang/String;Ljava/lang/Object;Z)V"
//				"org.jcvi.trace.sanger.chromatogram.BasicChromatogram#equals(Ljava/lang/Object;)Z"

				};
		
		int repeatTime = 5;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		boolean aor = false;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateSuite", "Evosuite", "MONOTONIC_GA");
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("applied object rule: " + aor);
		System.out.println("overall legitimization budget: " + Properties.TOTAL_LEGITIMIZATION_BUDGET);
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
	}
	
	@Test
	public void testJiggler() {
		String projectId = SF100Project.P89;
		String[] targetMethods = new String[]{
				// "jigl.image.MIPMap#get(FFF)[F",
				"jigl.image.levelSetTool.LevelSetSmooth#apply(IIIIZZ)Ljigl/image/RealColorImage;",
				// "jigl.signal.ops.levelOps.ClipNeg#apply(Ljigl/signal/RealSignal;Ljigl/signal/ROI;)Ljigl/signal/Signal;"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 150;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testIfxFramework() {
		String projectId = SF100Project.P84;
		String[] targetMethods = new String[]{
				// "net.sourceforge.ifxfv3.beans.USA_ACHProf#equals(Ljava/lang/Object;)Z",
				// "net.sourceforge.ifxfv3.beans.Term#equals(Ljava/lang/Object;)Z",
				"net.sourceforge.ifxfv3.beans.PmtAckInfo#equals(Ljava/lang/Object;)Z",
				// "net.sourceforge.ifxfv3.beans.TotalFeeCharge_Type#equals(Ljava/lang/Object;)Z"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 150;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testJcviJavaCommon() {
		String projectId = SF100Project.P92;
		String[] targetMethods = new String[]{
				"org.jcvi.trace.fourFiveFour.flowgram.sff.NewblerSuffixNameConverter#getSuffixedRangeFrom(Ljava/lang/String;)Lorg/jcvi/Range;"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 150;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testQuickServer() {
		String projectId = SF100Project.P93;
		String[] targetMethods = new String[]{
				// "org.quickserver.net.server.QuickServer#run()V",
				"org.quickserver.util.io.ByteBufferInputStream#dumpContent()V"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 150;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testProtectedExample() {
		String projectId = "10_water-simulator";
		String[] targetMethods = new String[]{
				"simulator.WSA.BehaviourQueryConsumers#handleInform(Ljade/lang/acl/ACLMessage;)V"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleDynaMOSA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRq#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleDynaMOSA2() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.MediaAcctAdjInqRs#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleMOSA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRq#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleMOSA2() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.MediaAcctAdjInqRs#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleMonotonicGA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRq#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> results = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = false;
		results = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateSuite", "Evosuite", "MONOTONIC_GA");
		EvoTestResult resultF = results.get(0);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
	}
	
	@Test
	public void testEqualExampleMonotonicGA2() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRq#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> results = new ArrayList<EvoTestResult>();
		int repeatTime = 5;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		results = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateSuite", "Evosuite", "MONOTONIC_GA");
		EvoTestResult resultF = results.get(0);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
	}
	
	@Test
	public void testDynaMOSA1() {
		String projectId = "3_jigen";
		String[] targetMethods = new String[]{
				"com.jigen.msi.ResourcesDirectory#addResource(Lcom/jigen/msi/ResourceDescriptor;Ljava/util/LinkedList;)V"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testDynaMOSA2() {
		String projectId = "43_lilith";
		String[] targetMethods = new String[]{
				"de.huxhorn.lilith.engine.LoggingFileBufferFactory#resolveCodec(Lde/huxhorn/sulky/codec/filebuffer/MetaData;)Lde/huxhorn/sulky/codec/Codec;"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testExampleMOSA1() {
		String projectId = "3_jigen";
		String[] targetMethods = new String[]{
				"com.jigen.msi.ResourcesDirectory#addResource(Lcom/jigen/msi/ResourceDescriptor;Ljava/util/LinkedList;)V"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testExampleMOSA2() {
		String projectId = "43_lilith";
		String[] targetMethods = new String[]{
				"de.huxhorn.lilith.engine.LoggingFileBufferFactory#resolveCodec(Lde/huxhorn/sulky/codec/filebuffer/MetaData;)Lde/huxhorn/sulky/codec/Codec;"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "MOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testExampleMonotonicGA1() {
		String projectId = "3_jigen";
		String[] targetMethods = new String[]{
				"com.jigen.msi.ResourcesDirectory#addResource(Lcom/jigen/msi/ResourceDescriptor;Ljava/util/LinkedList;)V"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateSuite", "Evosuite", "MONOTONIC_GA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testExampleMonotonicGA2() {
		String projectId = "43_lilith";
		String[] targetMethods = new String[]{
				"de.huxhorn.lilith.engine.LoggingFileBufferFactory#resolveCodec(Lde/huxhorn/sulky/codec/filebuffer/MetaData;)Lde/huxhorn/sulky/codec/Codec;"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateSuite", "Evosuite", "MONOTONIC_GA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
//	@Test
//	public void test11Example() {
//		String projectId = "11_imsmart";
//		String[] targetMethods = new String[]{
//				"com.momed.cms.MContentManagerFileNet#checkInContent(Ljava/io/File;)Z"
//				};
//		
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "branch";
//		
//		
//		boolean aor = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, 
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		EvoTestResult resultF = resultsF.get(0);
//		
//		System.currentTimeMillis();
//	}
//	
//	@Test
//	public void test20Example() {
//		String projectId = "20_nekomud";
//		String[] targetMethods = new String[]{
//				"net.sourceforge.nekomud.nio.Connection#handleRead(Ljava/nio/channels/SelectionKey;)V"
//				};
//		
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "branch";
//		
//		
//		boolean aor = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, 
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		EvoTestResult resultF = resultsF.get(0);
//		
//		System.currentTimeMillis();
//	}
//	
//	
//	
	@Test
	public void testGangupBasicRulesExample() {
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
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor);
//		
		
	}
//	
//	@Test
//	public void testCascadingCallExample() {
//		Class<?> clazz = regression.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample.class;
//
//		String methodName = "targetM";
//		int parameterNum = 0;
//
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/test-classes";
//
//		String fitnessApproach = "fbranch";
//
//		int timeBudget = 100;
//		EvoTestResult resultT = null;
//		EvoTestResult resultF = null;
//
//		try {
//			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		} catch (Exception e) {
//			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		}
//
//		Properties.APPLY_OBJECT_RULE = false;
//		
//		try {
//			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		} catch (Exception e) {
//			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		}
//
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//
//		assert ageT <= 1;
//		assert timeT <= 1;
//		assert ageT < ageF;
//		assert timeT <= timeF;
//		assert coverageT == 1.0;
//	}
//	
//	// pending debugging
//	@Test
//	public void testCorinaPngencoderExample() {
//		String projectId = "35_corina";
//		String[] targetMethods = new String[]{
//				"corina.map.PngEncoderB#pngEncode(Z)[B"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = 1590296724051L;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testDsachatFrameExample() {
//		String projectId = "12_dsachat";
//		String[] targetMethods = new String[]{
//				"dsachat.gm.gui.InternalGmHeroFrame#valueChanged(Ljavax/swing/event/TreeSelectionEvent;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = 1590297701552L;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	
//	
//	@Test
//	public void testJfxExample() {
//		String projectId = "84_ifx-framework";
//		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.ChkOrdAudRs_TypeSequence2#equals(Ljava/lang/Object;)Z"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testJhandballMovesExample() {
//		String projectId = "56_jhandballmoves";
//		String[] targetMethods = new String[]{
//				"visu.handball.moves.model.HandballModel#setMoveName(Ljava/lang/String;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testJwbfArticleExample() {
//		String projectId = "23_jwbf";
//		String[] targetMethods = new String[]{
//				"net.sourceforge.jwbf.core.contentRep.Article#getRevisionId()Ljava/lang/String;"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testLilithExample() {
//		String projectId = "43_lilith";
//		String[] targetMethods = new String[]{
//				"de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufDecoder#convert(Lde/huxhorn/lilith/data/logging/protobuf/generated/LoggingProto$LoggingEvent;)Lde/huxhorn/lilith/data/logging/LoggingEvent;"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testObjectexplorerExample() {
//		String projectId = "63_objectexplorer";
//		String[] targetMethods = new String[]{
//				"de.paragon.explorer.figure.ListBoxFigureBuilder#setStaticAttributesVisible(Lde/paragon/explorer/figure/ListBoxFigure;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testOpenhreExample() {
//		String projectId = "75_openhre";
//		String[] targetMethods = new String[]{
//				"com.browsersoft.openhre.hl7.impl.regular.ExpressionNodeList#addExpressionList(Lcom/browsersoft/openhre/hl7/impl/regular/ExpressionNodeList;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testOpenjmsExample() {
//		String projectId = "66_openjms";
//		String[] targetMethods = new String[]{
//				"org.exolab.jms.net.rmi.RMIManagedConnectionAcceptor#close()V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testSummaExample() {
//		String projectId = "44_summa";
//		String[] targetMethods = new String[]{
//				"dk.statsbiblioteket.summa.common.filter.object.MUXFilter#pump()Z"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testWekaArjarchiveentryExample() {
//		String projectId = "101_weka";
//		String[] targetMethods = new String[]{
//				"org.apache.commons.compress.archivers.arj.ArjArchiveEntry#isDirectory()Z"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testXbusFtpsenderExample() {
//		String projectId = "83_xbus";
//		String[] targetMethods = new String[]{
//				"net.sf.xbus.technical.ftp.FTPSender#execute(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
	
}
