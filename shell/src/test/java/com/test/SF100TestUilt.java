package com.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.result.TestGenerationResult;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;

public class SF100TestUilt {
	public static List<EvoTestResult> evoTestSingleMethod(String projectId,
			String[] targetMethods, String fitnessAppraoch, int iteration, 
			long seconds, boolean context, Long seed
//			, 
//			boolean applyObjectRule,
//			String option,
//			String strategy,
//			String cp
			) {
		/* configure */
		
		/* run */
		
		String projectName = projectId.substring(projectId.indexOf("_")+1, projectId.length());
		
		if(!new File(SFConfiguration.sfBenchmarkFolder + File.separator + "1_tullibee").exists()) {
			System.err.println("The dataset in " + SFConfiguration.sfBenchmarkFolder + " does not exsit!");
			return null;
		}
		
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

		String[] args = new String[] {
				"-Dapply_object_rule", "true",
				"-Denable_branch_enhancement", "true",
				"-generateTests",
				"-Dstrategy", "EMPIRICAL_HYBRID_COLLECTOR",
//				"-Dalgorithm", algorithm,
				
//				"-generateSuiteUsingDSE",
//				"-Dstrategy", "DSE",
				
//				"-generateTests",
//				"-Dstrategy", "EMPIRICAL_HYBRID_COLLECTOR",
				
//				"-generateMOSuite",
//				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "DYNAMOSA",
				
				
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", fitnessAppraoch,
//				"-projectCP", cp,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
				"-iteration", String.valueOf(iteration),
				"-Dadopt_smart_mutation", "true",
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(context), 
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.3",
				"-Dp_change_parameter", "0.6",
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
				"-Dprimitive_pool", "0.5",//0.0
				"-Ddynamic_pool", "0.5",//0.0
				"-Dlocal_search_ensure_double_execution", "false",
//				"-seed", "1556035769590"
				
		};
		
		if(seed != null) {
			args = ArrayUtils.add(args, "-seed");
			args = ArrayUtils.add(args,  seed.toString());
		}
		
		SFBenchmarkUtils.setupProjectProperties(projectId);
		return EvosuiteForMethod.generateTests(args);
	
	}
}
