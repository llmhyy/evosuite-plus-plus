package sf100;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;

public class CommonTestUtil {
	public static List<EvoTestResult> evoTestSingleMethod(String projectId,
			String[] targetMethods, String fitnessAppraoch, int iteration, 
			long seconds, boolean context, Long seed) {
		/* configure */
	
		/* run */
		
		String projectName = projectId.substring(projectId.indexOf("_")+1, projectId.length());
		
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

//		boolean instrumentContext = true;
		String[] args = new String[] {
//				"-generateTests",
//				"-Dstrategy", "ONEBRANCH",
//				"-Dalgorithm", "random",
//				"-generateSuiteUsingDSE",
				"-generateMOSuite",
				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "MOSA",
				"-Dalgorithm", "DYNAMOSA",
				
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", fitnessAppraoch,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
				"-iteration", String.valueOf(iteration),
//				"-Djunit_check", "false"
////				"-generateSuiteUsingDSE",
////				"-class", targetClass, 
////				"-projectCP", cp, //;lib/commons-math-2.2.jar
////				"-setup", "bin", "lib/commons-math-2.2.jar",
////				"-Dtarget_method", targetMethod, 
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(context), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.3",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.6",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
//				"-seed", "1556035769590"
				
		};
		
		if(seed != null) {
			args = ArrayUtils.add(args, "-seed");
			args = ArrayUtils.add(args,  seed.toString());
		}
		
		SFBenchmarkUtils.setupProjectProperties(projectId);
		return EvosuiteForMethod.generateTests(args);
	}
	
	public static void getBranchFeatures(String projectId, String oneBranchFile) {
		/* configure */
	
		/* run */
		
		String projectName = projectId.substring(projectId.indexOf("_")+1, projectId.length());
		
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();

//		boolean instrumentContext = true;
		String[] args = new String[] {
//				"-generateTests",
//				"-Dstrategy", "ONEBRANCH",
//				"-Dalgorithm", "random",
//				"-generateSuiteUsingDSE",
				"-generateMOSuite",
				"-Dstrategy", "MOSUITE",
				"-Dalgorithm", "MOSA",
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", "branch",
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
//				"-inclusiveFile", file.getAbsolutePath(),
//				"-Djunit_check", "false"
////				"-generateSuiteUsingDSE",
////				"-class", targetClass, 
////				"-projectCP", cp, //;lib/commons-math-2.2.jar
////				"-setup", "bin", "lib/commons-math-2.2.jar",
////				"-Dtarget_method", targetMethod, 
				"-Dinstrument_context", "true", 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.3",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.6",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
//				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-retrieveBranchFeature", 
				"-branchLabelFile", oneBranchFile,
				
		};
		
		
//		SFBenchmarkUtils.setupProjectProperties(projectId);
		EvosuiteForMethod.execute(args);
	}
}
