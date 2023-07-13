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
	public void beforeTest() throws Exception {
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
	public void testBugExample() throws Exception {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = SF100Project.P6;
//		String projectId = SF100Project.P23;
//		String projectId = SF100Project.P31;
//		String projectId = SF100Project.P38;
//		String projectId = SF100Project.P66;
		String projectId = SF100Project.P69;
//		String projectId = SF100Project.P83;
//		String projectId = SF100Project.P92;

		String[] targetMethods = new String[]{
//				"org.jcvi.assembly.cas.alignment.DefaultCasMatch#equals(Ljava/lang/Object;)Z"
//				"br.com.jnfe.core.Ambiente#equals(Ljava/lang/Object;)Z"
//				"org.exolab.jms.message.MessageProperties#setProperty(Ljava/lang/String;Ljava/lang/Object;)V"
//				"net.sf.xisemele.impl.OperationsHelperImpl#find(Lorg/w3c/dom/Document;Ljava/lang/String;)Lorg/w3c/dom/Node;"
//				"br.com.jnfe.core.EnvNFe#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.jwbf.core.bots.util.CachArticle#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.jwbf.mediawiki.contentRep.SimpleFile#equals(Ljava/lang/Object;)Z"
//				"framework.util.ObjectUtils#copyProperties(Ljava/util/Map;Ljava/lang/Object;)V"
//				"framework.util.ObjectUtils#toCollection(Ljava/lang/Object;)Ljava/util/Collection;"
				"macaw.persistenceLayer.demo.InMemoryVariableManager#getVariable(Lmacaw/businessLayer/User;Ljava/lang/String;)Lmacaw/businessLayer/Variable;"
//				"macaw.persistenceLayer.demo.InMemoryOntologyTermFilter#filterOntologyTerms(Lmacaw/businessLayer/User;Ljava/lang/String;Ljava/lang/String;)Ljava/util/ArrayList;"
//				"net.sf.xbus.admin.html.JournalBean#getDetailsAsTable()Ljava/lang/String;"
		};
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		boolean aor = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "CodaMOSA");
		
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
	public void testNewApproachOnCustom() throws Exception {
		String projectId = "0_custom";
		String[] targetMethods = new String[]{
//			"org.example.custom.arrayelementaccess.Parent#method()V", // Tested
//			"org.example.custom.arraylistexample.Parent#method()V", // Tested
//			"org.example.custom.directsetleafnode.Parent#method()V", // Tested
//			"org.example.custom.directsetleafnode2.Parent#method()V", // Tested
//			"org.example.custom.inaccessiblechild.Parent#method()V", // Tested
//			"org.example.custom.inaccessiblechild2.Parent#method()V", // Tested
//			"org.example.custom.inaccessiblechild3.Parent#method()V", // Tested
//			"org.example.custom.longgettercase.Parent#method()V", // Tested
//			"org.example.custom.longgettercase2.Parent#method()V", // Tested
//			"org.example.custom.parameterref.Parent#method(I)V", // Tested
//			"org.example.custom.recursivecase.LinkedListNode#method()V", // Tested
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
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
	public void testNewApproach() throws Exception {
		String projectId = SF100Project.P43;
		String[] targetMethods = new String[]{
//			"ch.bluepenguin.tapestry.components.menu.renderer.impl.HyperlinkMenuItemRenderer#startRender(Lorg/apache/tapestry/IMarkupWriter;Lch/bluepenguin/tapestry/components/menu/model/IMenuItem;)V"
//			"dash.performance.sequential.consumers.DirectConsumer#getComponent()Ldash/examples/component/IComponent;"
//			"com.objectmentors.state.Event#equals(Lcom/objectmentors/state/Event;)Z"
			"de.huxhorn.lilith.services.clipboard.AccessUriFormatter#isCompatible(Ljava/lang/Object;)Z"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 300;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testJiggler() throws Exception {
		String projectId = SF100Project.P89;
		String[] targetMethods = new String[]{
				// "jigl.image.MIPMap#get(FFF)[F",
				"jigl.image.levelSetTool.LevelSetSmooth#apply(IIIIZZ)Ljigl/image/RealColorImage;",
				// "jigl.signal.ops.levelOps.ClipNeg#apply(Ljigl/signal/RealSignal;Ljigl/signal/ROI;)Ljigl/signal/Signal;"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1500000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testWaterSimulator() throws Exception {
		String projectId = SF100Project.P10;
		String[] targetMethods = new String[]{
				"simulator.CA.BehaviourReplyNeighbour#action()V"
		};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 2;
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
	public void testIfxFramework() throws Exception {
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
	public void testJcviJavaCommon() throws Exception {
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
	public void testQuickServer() throws Exception {
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
	public void testProtectedExample() throws Exception {
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
	public void testEqualExampleDynaMOSA1() throws Exception {
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
	public void testEqualExampleDynaMOSA2() throws Exception {
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
	public void testEqualExampleMOSA1() throws Exception {
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
	public void testEqualExampleMOSA2() throws Exception {
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
	public void testEqualExampleMonotonicGA1() throws Exception {
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
	public void testEqualExampleMonotonicGA2() throws Exception {
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
	public void testDynaMOSA1() throws Exception {
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
	public void testDynaMOSA2() throws Exception {
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
	public void testExampleMOSA1() throws Exception {
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
	public void testExampleMOSA2() throws Exception {
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
	public void testExampleMonotonicGA1() throws Exception {
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
	public void testExampleMonotonicGA2() throws Exception {
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
//	public void test11Example() throws Exception {
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
//	public void test20Example() throws Exception {
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
	public void testGangupBasicRulesExample() throws Exception {
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
//	public void testCascadingCallExample() throws Exception {
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
//	public void testCorinaPngencoderExample() throws Exception {
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
//	public void testDsachatFrameExample() throws Exception {
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
//	public void testJfxExample() throws Exception {
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
//	public void testJhandballMovesExample() throws Exception {
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
//	public void testJwbfArticleExample() throws Exception {
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
//	public void testLilithExample() throws Exception {
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
//	public void testObjectexplorerExample() throws Exception {
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
//	public void testOpenhreExample() throws Exception {
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
//	public void testOpenjmsExample() throws Exception {
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
//	public void testSummaExample() throws Exception {
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
//	public void testWekaArjarchiveentryExample() throws Exception {
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
//	public void testXbusFtpsenderExample() throws Exception {
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
