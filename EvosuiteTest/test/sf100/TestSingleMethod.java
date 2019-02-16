package sf100;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.experiment.BenchmarkAddress;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;

public class TestSingleMethod {
	
	String fitnessApproach = "branch";

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		
		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void runPropertiesUtil() {
		String projectId = "38_javabullboard";
		String projectName = "javabullboard";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"framework.util.PropertyUtils#copyProperties(Ljava/lang/Object;Ljava/lang/Object;)V"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		fitnessApproach = "fbranch";
		int repeatTime = 5;
		int budget = 100;
		List<EvoTestResult> results0 = evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget);
		
		fitnessApproach = "branch";
		List<EvoTestResult> results1 = evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget);
		
		System.out.println("fbranch" + ":");
		for(EvoTestResult lu: results0){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
		
		System.out.println("branch" + ":");
		for(EvoTestResult lu: results1){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
	}
	
	@Test
	public void runComponentCreator() {
		String projectId = "80_wheelwebtool";
		String projectName = "wheelwebtool";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"wheel.components.ComponentCreator#radio(Ljava/lang/String;)Lwheel/components/Radio;"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		fitnessApproach = "fbranch";
		int repeatTime = 5;
		int budget = 100;
		results0 = evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget);
		
		fitnessApproach = "branch";
		results1 = evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget);
		
		System.out.println("fbranch" + ":");
		for(EvoTestResult lu: results0){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
		
		System.out.println("branch" + ":");
		for(EvoTestResult lu: results1){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
	}
	
	
	@Test
	public void runA4j() {
		String projectId = "2_a4j";
		String projectName = "a4j";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"net.kencochrane.a4j.beans.RecentlyViewed#addProduct(Lnet/kencochrane/a4j/beans/MiniProduct;)V"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		fitnessApproach = "fbranch";
		int repeatTime = 1;
		int budget = 100;
		results0 = evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget);
		
//		fitnessApproach = "branch";
//		results1 = evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget);
		
		System.out.println("fbranch" + ":");
		for(EvoTestResult lu: results0){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
		
		System.out.println("branch" + ":");
		for(EvoTestResult lu: results1){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
	}
	
	public List<EvoTestResult> evoTestSingleMethod(String projectId, String projectName,
			String[] targetMethods, String fitnessAppraoch, int iteration, long seconds) {
		/* configure */
	
		/* run */
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

		boolean instrumentContext = true;
		String[] args = new String[] {
				"-criterion", fitnessAppraoch,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
				"-iteration", String.valueOf(iteration),
//				"-Djunit_check", "false"
////				"-generateRandom",
//				"-generateSuite",
//				// "-generateMOSuite",
////				"-generateSuiteUsingDSE",
////				"-Dstrategy", "random",
////				"-class", targetClass, 
////				"-projectCP", cp, //;lib/commons-math-2.2.jar
////				"-setup", "bin", "lib/commons-math-2.2.jar",
////				"-Dtarget_method", targetMethod, 
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.1",
				"-Dp_test_change", "0.8",
				"-Dp_test_insert", "0.1",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.1",
//				"-Dlocal_search_rate", "3",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "30",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
				"-Delite", "10",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Dlocal_search_ensure_double_execution", "false",
				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
//				"-seed", "100"
				
		};
		SFBenchmarkUtils.setupProjectProperties(projectId);
		return EvosuiteForMethod.generateTests(args);
	}
	
}
