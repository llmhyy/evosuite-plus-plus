package regression.hybrid;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.ProgramArgumentUtils;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.FilterConfiguration;
import evosuite.shell.FilterConfiguration.Filter;
import evosuite.shell.FilterConfiguration.InclusiveFilter;
import evosuite.shell.ParameterOptions;
import evosuite.shell.Settings;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

public class SF100TestUilty {
	public static FilterConfiguration filter;
	public static EvoTestResult evoTestSingleMethod(String projectId,
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
		String cp = "target/classes;target/test-classes";
		
		EvoSuite evo = new EvoSuite();

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
//		return EvosuiteForMethod.generateTests(args);
		try {
			Settings.setup(args);
			args = ProgramArgumentUtils.extractArgs(args, ParameterOptions.ALL_OPTIONS);
			String[] truncatedArgs = extractArgs(args);
			filter = new FilterConfiguration(Settings.getInclusiveFilePath(), null);
			InclusiveFilter inclusiveFilter = getInclusiveFilter();
			Set<String> methodIDs = inclusiveFilter.getInclusives().get(projectName);
			List<String> methodIDList = new ArrayList<String>(methodIDs);
			
			for(String methodID: methodIDList) {
			String className = methodID.substring(0, methodID.indexOf("#"));
			String methodName = methodID.substring(methodID.indexOf("#")+1, methodID.length());
			String[] args1 = ArrayUtils.addAll(truncatedArgs, "-class", className, "-Dtarget_method", methodName);
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(args1);
			for (List<TestGenerationResult> l : list) {
				for (TestGenerationResult r : l) {
					System.out.println(r.getProgressInformation());
					if (r.getDistribution() != null) {
						for (int i = 0; i < r.getDistribution().length; i++) {
							System.out.println(r.getDistribution()[i]);
						}
					}

					int age = 0;
					if (r.getGeneticAlgorithm() != null) {
						age = r.getGeneticAlgorithm().getAge();
						System.out.println("Generations: " + age);
					}

					System.out.println("Used time: " + r.getElapseTime());
					System.out.println("Age: " + r.getAge());



					return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
							r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
							r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
					}
				}
			}
			}catch (Throwable e) {
				e.printStackTrace();
			}
			
			return null;
	
	}
	
	private static String setup(String projectId) throws IOException {
		String workingDir = System.getProperty("user.dir");
		String projectName;
		projectId = new File(workingDir).getName();
		projectName = projectId.substring(projectId.indexOf("_") + 1);
		String root = new File(workingDir).getParentFile().getAbsolutePath();
		Settings.setSfBenchmarkFolder(root);
		File folder = new File(Settings.getReportFolder());
		if (!folder.exists()) {
			folder.mkdir();
		}
		LoggerUtils.setupLogger(Settings.getReportFolder(), projectId);
		return root;
	}
	
	private static String[] extractArgs(String[] args) throws Exception {
		Set<String> excludedOpts = new HashSet<>();
		excludedOpts.add("-target");
		excludedOpts.add("-prefix");
		excludedOpts.add("-class");
		return ProgramArgumentUtils.extractArgs(args, excludedOpts);
	}
	private static InclusiveFilter getInclusiveFilter() {
		for(Filter f: filter.getFilters()) {
			if(f instanceof InclusiveFilter) {
				return (InclusiveFilter)f;
			}
		}
		
		return null;
	}
}
