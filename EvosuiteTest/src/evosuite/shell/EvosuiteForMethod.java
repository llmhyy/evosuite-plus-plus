package evosuite.shell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

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
	public static final String LIST_METHODS_FILE_NAME = "targetMethods.txt";
	public static String projectName;
	public static String projectId; // ex: 1_tullibee (project folder name)
	static FilterConfiguration filter;
	private int methodIterator = 1;
	
	private URLClassLoader evoTestClassLoader;

	public static void main(String[] args) {
		execute(args);
		System.exit(0);
	}

	public static void execute(String[] args) {
		try {
			setup();
//			Properties.CLIENT_ON_THREAD = true;
//			Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
			log.error("enter EvosuiteForMethod!");
			EvosuiteForMethod evoTest = new EvosuiteForMethod();
			if (CommonUtility.hasOpt(args, ListMethods.OPT_NAME)) {
				args = evoTest.extractListMethodsArgs(args);
				String[] targetClasses = evoTest.listAllTargetClasses(args);
				ListMethods.execute(targetClasses, evoTest.evoTestClassLoader);
			} else {
				filter = new FilterConfiguration(args);
				if (!filter.isValidProject(projectName)) {
					return;
				}
				String optValue = CommonUtility.getOptValue(args, ParameterOptions.METHOD_TEST_ITERATION);
				if (optValue != null) {
					evoTest.methodIterator = Integer.valueOf(optValue);
				}
				args = extractArgs(args, Arrays.asList(ParameterOptions.EXCLUSIVE_FILE_OPT,
						ParameterOptions.INCLUSIVE_FILE_OPT, ParameterOptions.METHOD_TEST_ITERATION));
				String[] targetClasses = evoTest.listAllTargetClasses(args);
				String[] truncatedArgs = extractArgs(args);
				evoTest.runAllMethods(targetClasses, truncatedArgs, projectName);
			}
		} catch (Throwable e) {
			log.error("Error!", e);
		}
		
		log.info("Finish!");
	}

	private static void setup() throws IOException {
		String workingDir = System.getProperty("user.dir");
		projectId = new File(workingDir).getName();
		projectName = projectId.substring(projectId.indexOf("_") + 1);
		String root = new File(workingDir).getParentFile().getAbsolutePath();
		SFConfiguration.sfBenchmarkFolder = root;
		File folder = new File(SFConfiguration.getReportFolder());
		if (!folder.exists()) {
			folder.mkdir();
		}
		LoggerUtils.setupLogger(SFConfiguration.getReportFolder(), projectId);
		log = LoggerUtils.getLogger(EvosuiteForMethod.class);
		Properties.MAX_OPEN_FILES_PER_PROCESS = 250;
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
		return extractArgs(args, excludedOpts);
	}

	private static String[] extractArgs(String[] args, Collection<String> excludedOpts) {
		List<String> newArgs = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			if (excludedOpts.contains(args[i])) {
				i++;
				continue;
			}
			newArgs.add(args[i]);
		}
		return newArgs.toArray(new String[newArgs.size()]);
	}
	
	private String[] extractListMethodsArgs(String[] args) throws Exception {
		List<String> newArgs = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			if (ListMethods.OPT_NAME.equals(args[i])) {
				continue;
			}
			newArgs.add(args[i]);
		}
		return newArgs.toArray(new String[newArgs.size()]);
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

	public void runAllMethods(String[] targetClasses, String[] args, String projectName) {
		FitnessEffectiveRecorder recorder;
		if (methodIterator > 1) {
			recorder = new IterFitnessEffectiveRecorder(methodIterator);
		} else {
			recorder = new FitnessEffectiveRecorder();
		}
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = evoTestClassLoader.loadClass(className);
				// ignore
				if (targetClass.isInterface()) {
					continue;
				}
				for (Method method : targetClass.getMethods()) {
					String methodName = method.getName() + Type.getMethodDescriptor(method);
					if (!filter.isValidMethod(projectName, CommonUtility.getMethodId(className, methodName))) {
						continue;
					}
					try {
						for (int i = 0; i < methodIterator; i++) {
							runMethod(methodName, className, args, recorder);
						}
						recorder.recordEndMethod(methodName, className);
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
	}

	@SuppressWarnings("unchecked")
	private void runMethod(String methodName, String className, String[] evosuiteArgs, ExperimentRecorder recorder) {
		log.info("----------------------------------------------------------------------");
		log.info("RUN METHOD: " + className + "#" + methodName);
		log.info("----------------------------------------------------------------------");
		
		try {
			// $EVOSUITE -criterion branch -target tullibee.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore
			String[] args = ArrayUtils.addAll(evosuiteArgs, 
					"-class", className,
					"-Dtarget_method", methodName
//					,"-Dstop_zero", "false"
					);
			log.info("evosuite args: " + StringUtils.join((Object[]) args, " "));
			EvoSuite evosuite = new EvoSuite();
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evosuite.parseCommandLine(args);
			for (List<TestGenerationResult> l : list) {
				for (TestGenerationResult r : l) {
					recorder.record(className, methodName, r);
					
					System.out.println("Used time: " + r.getElapseTime());
					System.out.println("Used generations: " + r.getGeneticAlgorithm().getAge());
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
				}
			}
		} catch (Exception e) {
			recorder.recordError(className, methodName, e);
		}
	}
}
