package evosuite.shell.experiment;

import java.io.File;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;

public class EvoTestSingleMethod {
	
	String fitnessAppraoch = "branch";

	@Before
	public void setup() {
//		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		
//		Properties.SEARCH_BUDGET = 60000;
//		Properties.GLOBAL_TIMEOUT = 6000000;
//		Properties.TIMEOUT = 30000000;
		
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void runAMethod() {
		String projectId = "102_colt";
		String projectName = "colt";
		String[] targetMethods = new String[]{
				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
				};
		fitnessAppraoch = "fbranch";
		FileUtils.deleteFolder(new File(FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, "evosuite-tests")));
		FileUtils.deleteFolder(new File(FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, "evosuite-report")));
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch, 3);
	}
	
	public void evoTestSingleMethod(String projectId, String projectName,
			String[] targetMethods, String fitnessAppraoch) {
		evoTestSingleMethod(projectId, projectName, targetMethods, fitnessAppraoch, 1);
	}
	
	public void evoTestSingleMethod(String projectId, String projectName,
			String[] targetMethods, String fitnessAppraoch, int iteration) {
		/* configure */
	
		/* run */
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

		long seconds = 100;
		boolean instrumentContext = true;
		String[] args = new String[] {
				"-criterion", fitnessAppraoch,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
				"-iteration", String.valueOf(iteration),
				"-reportFolder", "unittest-report",
				"-testLevel", "lMethod", 
				"-Dp_test_delete", "0", 
				"-Dp_test_change", "0.9", 
				"-Dp_test_insert", "0.1", 
				"-Dp_change_parameter", "0.1", 
				"-Dlocal_search_rate", "3", 
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
				"-Ddynamic_pool", "0.0", 
				"-Dlocal_search_ensure_double_execution", "false", 
				"-generateMOSuite", 
				"-Dstrategy", "MOSUITE", 
				"-Dalgorithm", "MOSA", 
				
		};
		SFBenchmarkUtils.setupProjectProperties(projectId);
		EvosuiteForMethod.main(args);
	}
	
	public void evoSuiteSingleMethod() {
		String projectId = "1_tullibee";
		SFBenchmarkUtils.setupProjectProperties(projectId);
	}
}
