package evosuite.shell;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.evosuite.CommandLineParameters;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHacker;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.ProgramArgumentUtils;
import org.slf4j.Logger;

import evosuite.shell.FilterConfiguration.Filter;
import evosuite.shell.FilterConfiguration.InclusiveFilter;
import evosuite.shell.ParameterOptions.TestLevel;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.listmethod.ListFeatures;
import evosuite.shell.listmethod.ListMethods;
import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.TargetMethodIOUtils;

/**
 * 
 * @author thilyly_tran
 * 
 *         [Check these scripts: /experiments/SF100/evotest.bat &
 *         listMethods.bat for an example] To Run EvosuiteForMethod
 *         <p>
 *         + ListMethods (with filter)
 *         <p>
 *         EvosuiteForMethod -listMethods
 *         <p>
 *         (All other options are just the same as which that are used to run
 *         evosuite -listClasses)
 *         <p>
 *         REQUIRE ARGUMENTS: [-jar|-class|-prefix]
 *         <p>
 *         <p>
 *         + Run evosuite separately by Methods
 *         <p>
 *         (All options from evosuite are reused, except those two options are
 *         added for exceptional inclusive/exclusive filtered methods:
 *         <p>
 *         -inclusiveFile [file path] (check
 *         {@link FilterConfiguration.InclusiveFilter}
 *         <p>
 *         -exclusiveFile [file path]
 *         <p>
 *         options to determine target methods: [-jar|-class|-prefix|-]
 *         <p>
 * 
 * 
 *         Possible outputs: evoTest-reports folder which includes logs, xlsx
 *         and other reports (listMethods.txt) [base_dir]
 *         <p>
 *         |__ evoTest-reports
 *         <p>
 *         |__logs
 *         <p>
 *         | |__*.log
 *         <p>
 *         |__*.xlsx
 *         <p>
 *         |__targetMethods.txt
 *         <p>
 *         |__successfulMethods.txt
 *         <p>
 * 
 */
@SuppressWarnings("deprecation")
public class EvosuiteForMethod {
	private static Logger log;
	public static String projectName;
	public static String projectId; // ex: 1_tullibee (project folder name)
	public static FilterConfiguration filter;

	private URLClassLoader evoTestClassLoader;

	public static void main(String[] args) {
		execute(args);
		System.exit(0);
	}

	public static List<EvoTestResult> generateTests(String[] args) {
		return execute(args);
	}
	
	public static List<EvoTestResult> execute(String[] args) {
		List<EvoTestResult> results = new ArrayList<>();
		try {
			// String workingDir = SFConfiguration.sfBenchmarkFolder + File.separator +
			// "1_tullibee";
			// System.setProperty("user.dir", workingDir);
			setup();
			System.out.println("enter EvosuiteForMethod!");
			EvosuiteForMethod evoTest = new EvosuiteForMethod();
			Settings.setup(args);

			/**
			 * generate feature for a given branch
			 */
			if(Settings.isRetrieveBranchFeature()) {
				
				File file = new File(".").getAbsoluteFile();
				if(file.isDirectory()) {
					String[] filenames = file.list(new ProjectNameFileter());
					
					String originalWorkingDir = System.getProperty("user.dir");
					for(String projectFile: filenames) {
						ClassPathHandler.resetSingleton();
						
						SFConfiguration.sfBenchmarkFolder = originalWorkingDir;
						SFBenchmarkUtils.setupProjectProperties(projectFile);
						
//						String workingDir = originalWorkingDir + File.separator + projectFile;
//						System.setProperty("user.dir", workingDir);
//						EvoSuite.base_dir_path = workingDir;
						
						String targetJar = projectFile.substring(projectFile.indexOf("_")+1, projectFile.length());
						
						int targetIndex = ProgramArgumentUtils.indexOfOpt(args, "-target");
						args[targetIndex+1] = originalWorkingDir + File.separator + projectFile + File.separator + targetJar + ".jar";
						
						args = ProgramArgumentUtils.extractArgs(args, ParameterOptions.ALL_OPTIONS);
						evoTest.listAllTargetClasses(args);
						String branchFile = Settings.getBranchLabelFile();
						String projectId = projectFile;
						
						for (String entry : ClassPathHandler.getInstance().getTargetProjectClasspath().split(File.pathSeparator)) {
							try {
								ClassPathHacker.addFile(entry);
							} catch (IOException e) {
								LoggingUtils.getEvoLogger().info("* Error while adding classpath entry: "
								                                         + entry);
							}
						}
						
						new ListFeatures().execute(projectId, branchFile, evoTest.evoTestClassLoader);		
						
//						System.setProperty("user.dir", originalWorkingDir);
					}
				}
				
			}
			
			if (Settings.insterestedProjects!=null && !Settings.insterestedProjects.contains(projectName)) {
				return new ArrayList<>();
			}

			/**
			 * show the statistics of the methods under class
			 */
			if (Settings.isListMethods()) {
				args = ProgramArgumentUtils.extractArgs(args, ParameterOptions.getListMethodsOptions());
				String[] targetClasses = evoTest.listAllTargetClasses(args);
				ListMethods.execute(targetClasses, evoTest.evoTestClassLoader, Settings.getmFilterOpt(),
						Settings.getTargetMethodFilePath(), Settings.getTargetClassFilePath());
			}
			/**
			 * execute the test
			 */
			else {
//				String usedStrategy = getStrategy(args);
				FitnessEffectiveRecorder fitnessRecorder;
//				DistributionRecorder distributionRecorder;
//				OneBranchRecorder oneBranchRecorder;

				List<ExperimentRecorder> recorderList = new ArrayList<>();

				if (Settings.getIteration() > 1) {
					fitnessRecorder = new IterFitnessEffectiveRecorder(Settings.getIteration());
//					distributionRecorder = new IterDistributionRecorder(usedStrategy);
					recorderList.add(fitnessRecorder);
//					recorderList.add(distributionRecorder);
				} else {
					fitnessRecorder = new FitnessEffectiveRecorder();
//					distributionRecorder = new DistributionRecorder(usedStrategy);
//					oneBranchRecorder = new OneBranchRecorder(strastr);
					
					recorderList.add(fitnessRecorder);
//					recorderList.add(distributionRecorder);
//					recorderList.add(oneBranchRecorder);
				}
				String existingReport = fitnessRecorder.getFinalReportFilePath();
				Set<String> succeedMethods = null;
				if (Settings.isReportBasedFilterEnable()) {
					succeedMethods = TargetMethodIOUtils.collectMethods(existingReport);
				}
				filter = new FilterConfiguration(Settings.getInclusiveFilePath(), succeedMethods);
				if (!filter.isValidProject(projectName)) {
					return null;
				}
				System.out.println(projectName + " is valid!");
				args = ProgramArgumentUtils.extractArgs(args, ParameterOptions.ALL_OPTIONS);
//				String[] targetClasses = evoTest.listAllTargetClasses(args);
				String[] targetClasses = new String[0];

				String[] truncatedArgs = extractArgs(args);

				if(Settings.isRunBothMethods()) {
					if (Settings.getTestLevel() == TestLevel.lMethod) {
						results = evoTest.runAllMethodsWithBothStrategy(truncatedArgs, projectName);
					} else {
//						results = evoTest.runAllClassesWithBothStrategy(targetClasses, truncatedArgs, projectName, recorderList);
					}	
				}
				else {
					if (Settings.getTestLevel() == TestLevel.lMethod) {
						results = evoTest.runAllMethods(truncatedArgs, projectName, recorderList);
					} else {
						results = evoTest.runAllClasses(targetClasses, truncatedArgs, projectName, recorderList);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("Error!", e);
		}

		log.info("Finish!");
		return results;
	}

	private static String getStrategy(String[] args) {
		StringBuffer strategy = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			strategy.append(args[i]);
		}
		String strastr = strategy.toString();
		if (strastr.indexOf("MOSA") >= 0) {
			strastr = "MOSA";
		} else {
			if (strastr.indexOf("Random") >= 0) {
				strastr = "Random";
			} else {
				if(strastr.indexOf("ONEBRANCH")>=0) {
					strastr = "ONEBRANCH";
				}
				else {
				strastr = "MonotonicGA";
				}
			}
		}
		return strastr;
	}
	
	private InclusiveFilter getInclusiveFilter() {
		for(Filter f: filter.getFilters()) {
			if(f instanceof InclusiveFilter) {
				return (InclusiveFilter)f;
			}
		}
		
		return null;
	}

	private List<EvoTestResult> runAllMethodsWithBothStrategy(String[] args,
			String projectName) {
		
		ComparativeRecorder comRecorder = new ComparativeRecorder();
		ActualStatisticsRecorder actRecorder = new ActualStatisticsRecorder();
		
		List<EvoTestResult> results = new ArrayList<>();
		
		InclusiveFilter inclusiveFilter = getInclusiveFilter();
		
		Set<String> methodIDs = inclusiveFilter.getInclusives().get(projectName);
		List<String> methodIDList = new ArrayList<String>(methodIDs);
		
		Collections.sort(methodIDList);
		
		for(String methodID: methodIDList) {
			String className = methodID.substring(0, methodID.indexOf("#"));
			String methodName = methodID.substring(methodID.indexOf("#")+1, methodID.length());
			
			System.currentTimeMillis();
			
			if (!filter.isValidElementId(projectName, methodID)) {
				continue;
			}

			if (Settings.longRunningMethods != null && Settings.longRunningMethods.contains(methodID)) {
				continue;
			}
			
//			if(Settings.analyzedMethods.contains(methodID)) {
//				continue;
//			}

			try {
				List<ResultPair> goodCoveragePairs = new ArrayList<>();
				List<ResultPair> goodTimePairs = new ArrayList<>();
				List<ResultPair> equalPairs = new ArrayList<>();
				List<ResultPair> worseTimePairs = new ArrayList<>();
				List<ResultPair> worseCoveragePairs = new ArrayList<>();
				
				for (int i = 0; i < Settings.getIteration(); i++) {
					int critierionIndex = ProgramArgumentUtils.indexOfOpt(args, "-criterion");
					int contextIndex = ProgramArgumentUtils.indexOfOpt(args, "-Dinstrument_context");
					if(critierionIndex != -1) {
						args[critierionIndex+1] = "branch";
						args[contextIndex+1] = "false";
						EvoTestResult branchResult = null;
						try {
							branchResult = runMethod(methodName, className, args, new ArrayList<>());									
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						if(branchResult == null)continue;
						
						Long randomSeed = branchResult.getRandomSeed();
						
						args[critierionIndex+1] = "branch";
						args[contextIndex+1] = "true";
						String[] newArgs = args;
						newArgs = ArrayUtils.addAll(newArgs, "-seed", String.valueOf(randomSeed));
						EvoTestResult fBranchResult = null;
						try {
							fBranchResult = runMethod(methodName, className, newArgs, new ArrayList<>());									
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						
						if(fBranchResult == null)continue;
						
						
						ResultPair pair = new ResultPair(branchResult, fBranchResult);
						if(pair.getCoverageAdvantage()>0) {
							goodCoveragePairs.add(pair);
						}
						else {
							if(pair.getTimeAdvantage()>0) {
								goodTimePairs.add(pair);
							}
							else if(pair.getCoverageAdvantage()==0 && pair.getTimeAdvantage()==0) {
								equalPairs.add(pair);
							}
							else if(pair.getTimeAdvantage()<0){
								worseTimePairs.add(pair);
							}
							else {
								worseCoveragePairs.add(pair);
							}
						}
						
//						if(goodCoveragePairs.size()>=3 || 
//								(goodCoveragePairs.size()>=1 && goodTimePairs.size()>=2)) {
//							break;
//						}
						
						results.add(branchResult);
						results.add(fBranchResult);
					}
				}
				
				int total = goodCoveragePairs.size() + goodTimePairs.size() + equalPairs.size()
					+ worseCoveragePairs.size() + worseTimePairs.size();
				int good = goodCoveragePairs.size() + goodTimePairs.size();
				double ratio = (double)good/total;
				
				actRecorder.recordRatio(className, methodName, ratio);

				int count = 0;
				for(ResultPair pair: goodCoveragePairs) {
					comRecorder.recordBothResults(className, methodName, pair.branchResult, pair.fBranchResult, "good coverage");
					count++;
//					if(count>=3) {
//						break;
//					}
				}
				
				//if(count<3) {
					for(ResultPair pair: goodTimePairs) {
						comRecorder.recordBothResults(className, methodName, pair.branchResult, pair.fBranchResult, "good time");	
						count++;
//						if(count>=3) {
//							break;
//						}
					}
				//}
				
				//if(count<3) {
					for(ResultPair pair: equalPairs) {
						comRecorder.recordBothResults(className, methodName, pair.branchResult, pair.fBranchResult, "equal");	
						count++;
//						if(count>=3) {
//							break;
//						}
					}
				//}
				
				//if(count<3) {
					for(ResultPair pair: worseTimePairs) {
						comRecorder.recordBothResults(className, methodName, pair.branchResult, pair.fBranchResult, "worse time");	
						count++;
//						if(count>=3) {
//							break;
//						}
					}
				//}
				
				//if(count<3) {
					for(ResultPair pair: worseCoveragePairs) {
						comRecorder.recordBothResults(className, methodName, pair.branchResult, pair.fBranchResult, "worse coverage");	
						count++;
//						if(count>=3) {
//							break;
//						}
					}
				//}
				
				
			} catch (Throwable t) {
				String msg = new StringBuilder().append("[").append(projectName).append("]").append(className)
						.append("#").append(methodName).append("\n").append("Error: \n").append(t.getMessage())
						.toString();
				System.err.println(msg);
				log.debug(msg, t);
			}
		}
		
		

		return results;
	}
	
	class ResultPair{
		EvoTestResult branchResult;
		EvoTestResult fBranchResult;
		public ResultPair(EvoTestResult branchResult, EvoTestResult fBranchResult) {
			super();
			this.branchResult = branchResult;
			this.fBranchResult = fBranchResult;
		}
		
		public double getCoverageAdvantage() {
			return this.fBranchResult.getCoverage() - this.branchResult.getCoverage();
		}
		
		public int getTimeAdvantage() {
			if(branchResult.getCoverage()==1 &&
				branchResult.getCoverage()==fBranchResult.getCoverage() && 
				branchResult.getTime()<=100 &&
				fBranchResult.getTime()<=100) {
				return this.branchResult.getTime() - this.fBranchResult.getTime();
			}
			else {
				return 0;
			}
		}
		
	}

	private static String setup() throws IOException {
		String workingDir = System.getProperty("user.dir");
		projectId = new File(workingDir).getName();
		projectName = projectId.substring(projectId.indexOf("_") + 1);
		String root = new File(workingDir).getParentFile().getAbsolutePath();
		Settings.setSfBenchmarkFolder(root);
		File folder = new File(Settings.getReportFolder());
		if (!folder.exists()) {
			folder.mkdir();
		}
		LoggerUtils.setupLogger(Settings.getReportFolder(), projectId);
		log = LoggerUtils.getLogger(EvosuiteForMethod.class);
		// Properties.MAX_OPEN_FILES_PER_PROCESS = 250;
		
		return root;
	}

	private CommandLine parseCommandLine(String[] args) {
		try {
			Options options = CommandLineParameters.getCommandLineOptions();

			String version = EvoSuite.class.getPackage().getImplementationVersion();
			if (version == null) {
				version = "";
			}

			// create the parser
			CommandLineParser parser = new GnuParser();
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			return cmd;
		} catch (ParseException e) {
			EvoSuite evosuite = new EvoSuite();
			evosuite.parseCommandLine(args); // to print the error
			return null;
		}
	}

	/**
	 * 
	 * */
	private static String[] extractArgs(String[] args) throws Exception {
		Set<String> excludedOpts = new HashSet<>();
		excludedOpts.add("-target");
		excludedOpts.add("-prefix");
		excludedOpts.add("-class");
		return ProgramArgumentUtils.extractArgs(args, excludedOpts);
	}

	private String[] listAllTargetClasses(String[] args) {
		CommandLine cmd = parseCommandLine(args);
		ensureClasspath(cmd);
		if (cmd.hasOption("class")) {
			return new String[] { cmd.getOptionValue("class") };
		}
		String[] listClassesArgs = ArrayUtils.addAll(args, "-listClasses");
		EvoSuite evosuite = new EvoSuite();
		evosuite.parseCommandLine(listClassesArgs);
		String[] targetClasses = System.getProperty("evo_targetClasses").split(",");
		return targetClasses;
	}

	private void ensureClasspath(CommandLine cmd) {
		new EvoSuite().setupProperties();
		CommandLineParameters.handleClassPath(cmd); // ensure classpath
		List<URL> urls = new ArrayList<>();
		for (String classPathElement : Properties.CP.split(File.pathSeparator)) {
			try {
				File f = new File(classPathElement);
				urls.add(f.toURI().toURL());
			} catch (IOException e) {
				log.info("Warning: Cannot load path " + classPathElement);
			}
		}
		evoTestClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
	}

	public List<EvoTestResult> runAllMethods(String[] args, String projectName,
			List<ExperimentRecorder> recorders) {
		List<EvoTestResult> results = new ArrayList<>();
		InclusiveFilter inclusiveFilter = getInclusiveFilter();
		Set<String> methodIDs = inclusiveFilter.getInclusives().get(projectName);
		List<String> methodIDList = new ArrayList<String>(methodIDs);
		
		Collections.sort(methodIDList);
		
		for(String methodID: methodIDList) {
			String className = methodID.substring(0, methodID.indexOf("#"));
			String methodName = methodID.substring(methodID.indexOf("#")+1, methodID.length());
			
			if (!filter.isValidElementId(projectName, methodID)) {
				continue;
			}

			if (Settings.investigatedMethods != null && Settings.investigatedMethods.containsKey(methodID)) {
				continue;
			}

			if (Settings.easyMethods != null && !Settings.easyMethods.containsKey(methodID)) {
				continue;
			}

			try {
				for (int i = 0; i < Settings.getIteration(); i++) {
					EvoTestResult result = runMethod(methodName, className, args, recorders);
					results.add(result);
				}

				for (ExperimentRecorder recorder : recorders) {
					recorder.recordEndIterations(methodName, className);
				}
			} catch (Throwable t) {
				String msg = new StringBuilder().append("[").append(projectName).append("]").append(className)
						.append("#").append(methodName).append("\n").append("Error: \n").append(t.getMessage())
						.toString();
				log.debug(msg, t);
			}
		}

		return results;
	}

	private EvoTestResult runMethod(String methodName, String className, String[] evosuiteArgs,
			List<ExperimentRecorder> recorders) {
		log.info("----------------------------------------------------------------------");
		log.info("RUN METHOD: " + className + "#" + methodName);
		log.info("----------------------------------------------------------------------");

		// $EVOSUITE -criterion branch -target tullibee.jar
		// -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore

		String[] args = ArrayUtils.addAll(evosuiteArgs, "-class", className, "-Dtarget_method", methodName);
		return invokeEvosuite(methodName, className, args, recorders);
	}

	public List<EvoTestResult> runAllClasses(String[] targetClasses, String[] args, String projectName,
			List<ExperimentRecorder> recorders) {
		List<EvoTestResult> results = new ArrayList<>();
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = evoTestClassLoader.loadClass(className);
				// ignore
				if (targetClass.isInterface()) {
					continue;
				}
				if (filter.isValidElementId(projectName, className)) {
					try {
						for (int i = 0; i < Settings.getIteration(); i++) {
							EvoTestResult result = runClass(className, args, recorders);
							results.add(result);
						}

						for (ExperimentRecorder recorder : recorders) {
							recorder.recordEndIterations("", className);
						}
					} catch (Throwable t) {
						String msg = new StringBuilder().append("[").append(projectName).append("]").append(className)
								.append("\n").append("Error: \n").append(t.getMessage()).toString();
						log.debug(msg, t);
					}
				}
			} catch (Throwable t) {
				log.error("Error!", t);
			}
		}

		return results;
	}

	private EvoTestResult runClass(String className, String[] evosuiteArgs, List<ExperimentRecorder> recorders) {
		log.info("----------------------------------------------------------------------");
		log.info("RUN CLASS: " + className);
		log.info("----------------------------------------------------------------------");
		String[] args = ArrayUtils.addAll(evosuiteArgs, "-class", className);
		return invokeEvosuite("", className, args, recorders);
	}

	@SuppressWarnings("unchecked")
	private EvoTestResult invokeEvosuite(String methodName, String className, String[] args,
			List<ExperimentRecorder> recorders) {
		EvoTestResult result = null;
		try {
			log.info("evosuite args: " + StringUtils.join((Object[]) args, " "));
			EvoSuite evosuite = new EvoSuite();
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evosuite.parseCommandLine(args);
			for (List<TestGenerationResult> l : list) {
				for (TestGenerationResult r : l) {

					System.out.println("Used time: " + r.getElapseTime());
					System.out.println("Used generations: " + r.getAge());
					System.out.println("Method call availability: " + r.getAvailabilityRatio());

					System.out.println("Available calls: " + r.getAvailableCalls());
					System.out.println("Unavailable calls: " + r.getUnavailableCalls());
					
					result = new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
							r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
							r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
					result.setAvailableCalls(r.getAvailableCalls());
					result.setUnavailableCalls(r.getUnavailableCalls());
					result.setBranchInformation(r.getBranchInformation());

					for (ExperimentRecorder recorder : recorders) {
						recorder.record(className, methodName, result);
					}
				}
			}
		} catch (Exception e) {
			for (ExperimentRecorder recorder : recorders) {
				recorder.recordError(className, methodName, e);
			}
		}

		return result;
	}
}
