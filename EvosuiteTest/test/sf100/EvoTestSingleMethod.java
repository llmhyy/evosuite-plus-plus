package sf100;

import java.io.File;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.experiment.BenchmarkAddress;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;

public class EvoTestSingleMethod {
	
	String fitnessAppraoch = "branch";

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void runMyGrid() {
		String projectId = "38_javabullboard";
		String projectName = "javabullboard";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"framework.util.PropertyUtils#copyProperties(Ljava/lang/Object;Ljava/lang/Object;)V"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		fitnessAppraoch = "fbranch";
		for (int i = 0; i < 1; i++) {
			FileUtils.deleteFolder(new File(FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, "evosuite-tests")));
			FileUtils.deleteFolder(new File(FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, "evosuite-report")));
			evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch);
			System.out.println("i=" + i);
		}
	}
	
	public void evoTestSingleMethod(String projectId, String projectName,
			String[] targetMethods, String fitnessAppraoch) {
		/* configure */
	
		/* run */
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

		long seconds = 300;
		boolean instrumentContext = true;
		String[] args = new String[] {
				"-criterion", fitnessAppraoch,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
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
				"-Dp_test_delete", "0",
				"-Dp_test_change", "0.9",
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
				"-Dmax_length", "1",
				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", "100"
				
		};
		SFBenchmarkUtils.setupProjectProperties(projectId);
		EvosuiteForMethod.main(args);
	}
	
	public void evoSuiteSingleMethod() {
		String projectId = "1_tullibee";
		SFBenchmarkUtils.setupProjectProperties(projectId);
	}
}
