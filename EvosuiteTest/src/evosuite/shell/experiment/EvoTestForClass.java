package evosuite.shell.experiment;

import java.io.File;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;

public class EvoTestForClass {

	@Before
	public void setup() {
//		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.SEARCH_BUDGET = 60000;
//		Properties.GLOBAL_TIMEOUT = 60000;
//		Properties.TIMEOUT = 3000000;
		
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void testExample(){
//		-testLevel lClass
		
		String projectId = "1_tullibee";
		String projectName = "tullibee";
		String targetClass = "com.ib.client.Util";
		
		evoTestSingleClass(projectId, projectName, targetClass, "fbranch", 1);
	}
	
	
	public void evoTestSingleClass(String projectId, String projectName,
			String targetClass, String fitnessAppraoch, int iteration) {
		/* configure */
	
		/* run */
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetClass);

		long seconds = 90;
		boolean instrumentContext = true;
		String[] args = new String[] {
				"-generateMOSuite",
				"-Dstrategy", "MOSUITE",
				"-Dalgorithm", "MOSA",
				"-class", targetClass, 
//				"-projectCP", cp, //;lib/commons-math-2.2.jar
//				"-Dtarget_method", targetMethod, 
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
				"-Dp_test_delete", "0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.1",
				"-Dp_change_parameter", "0.1",
				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dlocal_search_ensure_double_execution", "false",
				"-testLevel", "lClass",
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
		};
		SFBenchmarkUtils.setupProjectProperties(projectId);
		EvosuiteForMethod.main(args);
	}
}
