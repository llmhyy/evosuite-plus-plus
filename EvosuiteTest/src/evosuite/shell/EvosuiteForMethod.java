package evosuite.shell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.CommonUtility;
import org.evosuite.utils.ExternalProcessHandler;
import org.evosuite.utils.ProgramArgumentUtils;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import evosuite.shell.ParameterOptions.TestLevel;
import evosuite.shell.listmethod.ListMethods;
import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.TargetMethodIOUtils;

/**
 * 
 * @author thilyly_tran
 * 
 * [Check these scripts: /experiments/SF100/evotest.bat & listMethods.bat for an example]
 * To Run EvosuiteForMethod
 * <p>
 * + ListMethods (with filter)<p>
 * EvosuiteForMethod -listMethods<p>
 * (All other options are just the same as which that are used to run evosuite -listClasses)<p>
 * 		REQUIRE ARGUMENTS: [-jar|-class|-prefix]<p>
 * <p>
 * + Run evosuite separately by Methods<p>
 * (All options from evosuite are reused, except those two options are added for exceptional inclusive/exclusive filtered methods:<p>
 * 	-inclusiveFile [file path] (check {@link FilterConfiguration.InclusiveFilter}<p>
 *  -exclusiveFile [file path]<p>
 *  options to determine target methods:
 *  	[-jar|-class|-prefix|-]
 *  <p>
 *  
 *  
 *  Possible outputs: evoTest-reports folder which includes logs, xlsx and other reports (listMethods.txt)
 *  [base_dir]<p>
 *  |__ evoTest-reports<p>
 *  	|__logs<p>
 *  	|	|__*.log<p>
 *  	|__*.xlsx	<p>
 *  	|__targetMethods.txt <p>
 *  	|__successfulMethods.txt <p>
 *  
 */
@SuppressWarnings("deprecation")
public class EvosuiteForMethod {
	private static Logger log;
	public static String projectName;
	public static String projectId; // ex: 1_tullibee (project folder name)
	static FilterConfiguration filter;
	
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
			setup();
			log.error("enter EvosuiteForMethod!");
			EvosuiteForMethod evoTest = new EvosuiteForMethod();
			Settings.setup(args);
			if (Settings.isListMethods()) {
				args = ProgramArgumentUtils.extractArgs(args, ParameterOptions.getListMethodsOptions());
				String[] targetClasses = evoTest.listAllTargetClasses(args);
				ListMethods.execute(targetClasses, evoTest.evoTestClassLoader, Settings.getmFilterOpt(),
						Settings.getTargetMethodFilePath(), Settings.getTargetClassFilePath());
			} else {
				FitnessEffectiveRecorder recorder;
				if (Settings.getIteration() > 1) {
					recorder = new IterFitnessEffectiveRecorder(Settings.getIteration());
				} else {
					recorder = new FitnessEffectiveRecorder();
				}
				String existingReport = recorder.getFinalReportFilePath();
				Set<String> succeedMethods = null;
				if (Settings.isReportBasedFilterEnable()) {
					succeedMethods = TargetMethodIOUtils.collectMethods(existingReport);
				}
				filter = new FilterConfiguration(Settings.getInclusiveFilePath(), succeedMethods);
				if (!filter.isValidProject(projectName)) {
					return null;
				}
				args = ProgramArgumentUtils.extractArgs(args, ParameterOptions.ALL_OPTIONS);
				String[] targetClasses = evoTest.listAllTargetClasses(args);
				String[] truncatedArgs = extractArgs(args);
				
				if (Settings.getTestLevel() == TestLevel.lMethod) {
					results = evoTest.runAllMethods(targetClasses, truncatedArgs, projectName, recorder);
				} else {
					results = evoTest.runAllClasses(targetClasses, truncatedArgs, projectName, recorder);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("Error!", e);
		}
		
		log.info("Finish!");
		return results;
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
//		Properties.MAX_OPEN_FILES_PER_PROCESS = 250;
		ExternalProcessHandler.terminateClientsIfCannotCancelGentlely = true;
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
			return new String[] {cmd.getOptionValue("class")};
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

	public List<EvoTestResult> runAllMethods(String[] targetClasses, String[] args, String projectName, FitnessEffectiveRecorder recorder) {
		List<EvoTestResult> results = new ArrayList<>();
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = evoTestClassLoader.loadClass(className);
				// ignore
				if (targetClass.isInterface()) {
					continue;
				}
				for (Method method : targetClass.getDeclaredMethods()) {
					String methodName = method.getName() + Type.getMethodDescriptor(method);
					if (!filter.isValidElementId(projectName, CommonUtility.getMethodId(className, methodName))) {
						continue;
					}
					try {
						for (int i = 0; i < Settings.getIteration(); i++) {
							EvoTestResult result = runMethod(methodName, className, args, recorder);
							results.add(result);
						}
						recorder.recordEndIterations(methodName, className);
					} catch (Throwable t) {
						String msg = new StringBuilder().append("[").append(projectName).append("]").append(className)
								.append("#").append(methodName).append("\n")
								.append("Error: \n")
								.append(t.getMessage()).toString();
						log.debug(msg, t);
					}
				}
			} catch (Throwable t) {
				log.error("Error!", t);
			}
		}
		
		return results;
	}

	private EvoTestResult runMethod(String methodName, String className, String[] evosuiteArgs, ExperimentRecorder recorder) {
		log.info("----------------------------------------------------------------------");
		log.info("RUN METHOD: " + className + "#" + methodName);
		log.info("----------------------------------------------------------------------");
		
		// $EVOSUITE -criterion branch -target tullibee.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore
		String[] args = ArrayUtils.addAll(evosuiteArgs, 
				"-class", className,
				"-Dtarget_method", methodName
				);
		return invokeEvosuite(methodName, className, args, recorder);
	}
	
	public List<EvoTestResult> runAllClasses(String[] targetClasses, String[] args, String projectName,
			FitnessEffectiveRecorder recorder) {
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
							EvoTestResult result = runClass(className, args, recorder);
							results.add(result);
						}
						recorder.recordEndIterations("", className);
					} catch (Throwable t) {
						String msg = new StringBuilder().append("[").append(projectName).append("]").append(className).append("\n")
								.append("Error: \n")
								.append(t.getMessage()).toString();
						log.debug(msg, t);
					}
				}
			} catch (Throwable t) {
				log.error("Error!", t);
			}
		}
		
		return results;
	}
	
	private EvoTestResult runClass(String className, String[] evosuiteArgs, ExperimentRecorder recorder) {
		log.info("----------------------------------------------------------------------");
		log.info("RUN CLASS: " + className);
		log.info("----------------------------------------------------------------------");
		String[] args = ArrayUtils.addAll(evosuiteArgs, 
				"-class", className
				);
		return invokeEvosuite("", className, args, recorder);
	}

	@SuppressWarnings("unchecked")
	private EvoTestResult invokeEvosuite(String methodName, String className, String[] args, ExperimentRecorder recorder) {
		EvoTestResult result = null;
		try {
			log.info("evosuite args: " + StringUtils.join((Object[]) args, " "));
			EvoSuite evosuite = new EvoSuite();
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evosuite.parseCommandLine(args);
			for (List<TestGenerationResult> l : list) {
				for (TestGenerationResult r : l) {
					
					System.out.println("Used time: " + r.getElapseTime());
					System.out.println("Used generations: " + r.getAge());
					int count = 0;
					for(String key: RuntimeRecord.methodCallAvailabilityMap.keySet()) {
						if(RuntimeRecord.methodCallAvailabilityMap.get(key)) {
							count++;
						}
						else {
							System.out.println("Missing analyzing call: " + key);
						}
					}
					int size = RuntimeRecord.methodCallAvailabilityMap.size();
					double ratio = -1;
					if(size != 0) {
						ratio = (double)count/size;
					}
					System.out.println("Method call availability: " + ratio);
					
					System.out.println("Available calls: " + r.getAvailableCalls());
					System.out.println("Unavailable calls: " + r.getUnavailableCalls());
					
					result = new EvoTestResult(r.getElapseTime(), r.getCoverage(), 
							r.getAge(), r.getAvailabilityRatio(), r.getProgressInformation(),
							r.getIPFlagCoverage(), r.getUncoveredIPFlags());
					result.setAvailableCalls(r.getAvailableCalls());
					result.setUnavailableCalls(r.getUnavailableCalls());
					recorder.record(className, methodName, result);
				}
			}
		} catch (Exception e) {
			recorder.recordError(className, methodName, e);
		}
		
		return result;
	}
}
