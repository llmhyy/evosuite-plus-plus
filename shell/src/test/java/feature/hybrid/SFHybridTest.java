package feature.hybrid;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.HybridOption;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import evosuite.shell.EvoTestResult;
import evosuite.shell.TempGlobalVariables;
import sf100.CommonTestUtil;

public class SFHybridTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		
		Properties.TIMEOUT = 30000000;
		Properties.OVERALL_HYBRID_STRATEGY_TIMEOUT = 100000;
		Properties.INDIVIDUAL_STRATEGY_TIMEOUT = 2*60;
//		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
	}
	@Test
	public void runTullibee() throws Exception {
//		Properties.HYBRID_OPTION = new HybridOption[]{
//		    	HybridOption.RANDOM
////		    	HybridOption.DSE
//	    };
		
		String projectId = "1_tullibee";
		String[] targetMethods = new String[]{
				"com.ib.client.EReader#processMsg(I)Z"
				
//				"com.ib.client.EClientSocket#reqContractDetails(ILcom/ib/client/Contract;)V"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
//		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 30000;
		Long seed = 1556814527153L;
//		Long seed = null;
		
		String fitnessApproach = "fbranch";
		SF100TestUilty.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed);
//		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		
	}
	@Test
	public void testBugExample() throws Exception {
//		Properties.HYBRID_OPTION = new HybridOption[]{
////		    	HybridOption.RANDOM
//		    	HybridOption.DSE
//	    };
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
		String projectId = "83_xbus";
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CustPayeeMsgRecChoice#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CustAddRqSequence#equals(Ljava/lang/Object;)Z"
//				"state.Party#remove(Lstate/Party;)V"
//				"net.sourceforge.ifxfv3.beans.BankAcctTrnRec#equals(Ljava/lang/Object;)Z"
//				"net.sf.xbus.protocol.xml.XBUSXMLMessage#synchronizeResponseFields(Lnet/sf/xbus/base/xbussystem/XBUSSystem;)V"
//				"net.sourceforge.ifxfv3.beans.LoanInfoCommon#equals(Ljava/lang/Object;)Z"
				"net.sf.xbus.protocol.xml.XBUSXMLMessage#synchronizeResponseFields(Lnet/sf/xbus/base/xbussystem/XBUSSystem;)V"
				};
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String cp = "target/classes;target/test-classes";
		
		String fitnessApproach = "fbranch";
		
		SF100TestUilty.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testProtectedExample() throws Exception {
		String projectId = "10_water-simulator";
		String[] targetMethods = new String[]{
				"simulator.WSA.BehaviourQueryConsumers#handleInform(Ljade/lang/acl/ACLMessage;)V"
				};
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1556814527153L;
		
		String fitnessApproach = "fbranch";
		
		
		SF100TestUilty.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void runA4j() throws Exception {
		String projectId = "2_a4j";
		String[] targetMethods = new String[]{
				"net.kencochrane.a4j.file.FileUtil#getASINFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;"
				};
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1556814527153L;
//		Long seed = null;
		
		String fitnessApproach = "fbranch";
		SF100TestUilty.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed);
	}
	
	@Test
	public void runQuartz() throws Exception {
		String projectId = "113_quartz";
		String[] targetMethods = new String[]{
				"org.quartz.SchedulerContext#setAllowsTransientData(Z)V"
		};
		
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1556814527153L;
//		Long seed = null;
		
		String fitnessApproach = "fbranch";
		SF100TestUilty.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed);
	}
	
	private List<Long> checkRandomSeeds(List<EvoTestResult> results0) {
		List<Long> randomSeeds = new ArrayList<>();
		for(EvoTestResult lu: results0){
			randomSeeds.add(lu.getRandomSeed());
		}
		
		return randomSeeds;
	}
}
