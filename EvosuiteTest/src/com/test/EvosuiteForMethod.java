package com.test;

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
import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import com.test.excel.ExcelWriter;

@SuppressWarnings("deprecation")
public class EvosuiteForMethod {
	private static Logger log;
	public static final String LIST_METHODS_FILE_NAME = "targetMethods.txt";
	static String projectName;
	static String outputFolder;
	static String projectId; // ex: 1_tullibee
	static FilterConfiguration filter;
	
	private URLClassLoader evoTestClassLoader;

	public static void main(String[] args) {
		try {
			setup();
//			Properties.CLIENT_ON_THREAD = true;
//			Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
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
				args = extractArgs(args, Arrays.asList(ParameterOptions.EXCLUSIVE_FILE_OPT, ParameterOptions.INCLUSIVE_FILE_OPT));
				String[] targetClasses = evoTest.listAllTargetClasses(args);
				String[] truncatedArgs = extractArgs(args);
				evoTest.runAllMethods(targetClasses, truncatedArgs, projectName);
			}
		} catch (Throwable e) {
			log.error("Error!", e);
		}
		
		log.info("Finish!");
		System.exit(0);
	}

	private static void setup() throws IOException {
		String workingDir = System.getProperty("user.dir");
		projectId = new File(workingDir).getName();
		projectName = projectId.substring(projectId.indexOf("_") + 1);
		String root = new File(workingDir).getParentFile().getAbsolutePath();
		outputFolder = root + "/evoTest-reports";
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		LoggerUtils.setupLogger(outputFolder, projectId);
		log = LoggerUtils.getLogger(EvosuiteForMethod.class);
	}

	public EvosuiteForMethod() {
		
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
		ExperimentRecorder recorder = new ExperimentRecorder();
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = evoTestClassLoader.loadClass(className);
				// ignore
				if (targetClass.isInterface()) {
					continue;
				}
				List<String> testableMethod = MethodFilter.listTestableMethods(targetClass, evoTestClassLoader);
				for (Method method : targetClass.getMethods()) {
					String methodName = method.getName() + Type.getMethodDescriptor(method);
					if (!filter.isValidMethod(projectName, CommonUtility.getMethodId(className, methodName))) {
						continue;
					}
					if (testableMethod.contains(methodName)) {
						try {
							runMethod(methodName, className, args, recorder);
						} catch (Throwable t) {
							String msg = new StringBuilder().append("[").append(projectName).append("]").append(className)
									.append("#").append(methodName).append("\n")
									.append("Error: \n")
									.append(t.getMessage()).toString();
							log.debug(msg, t);
						}
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
					,"-Dstop_zero", "false"
					);
			log.info("evosuite args: " + StringUtils.join((Object[]) args, " "));
			EvoSuite evosuite = new EvoSuite();
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evosuite.parseCommandLine(args);
			for (List<TestGenerationResult> l : list) {
				for (TestGenerationResult r : l) {
					if (r.getDistribution() == null || r.getDistribution().length == 0) {
						continue; // ignore
					}
					log.info("" +r.getProgressInformation());
					for(int i=0; i<r.getDistribution().length; i++){
						log.info("" +r.getDistribution()[i]);					
					}	
					List<Object> progressRowData = new ArrayList<>();
					progressRowData.add(className);
					progressRowData.add(methodName);
					progressRowData.addAll(r.getProgressInformation());
					List<Object> distributionRowData = new ArrayList<>();
					distributionRowData.add(className);
					distributionRowData.add(methodName);
					for (int distr : r.getDistribution()) {
						distributionRowData.add(distr);
					}
					recorder.record(progressRowData, distributionRowData);
					recorder.logSuccessfulMethods(className, methodName);
				}
			}
		} catch (Exception e) {
			List<Object> progressRowData = new ArrayList<>();
			progressRowData.add(className);
			progressRowData.add(methodName);
			progressRowData.add(e.getMessage());
			List<Object> distributionRowData = new ArrayList<>();
			distributionRowData.add(className);
			distributionRowData.add(methodName);
			progressRowData.add(e.getMessage());
			recorder.record(progressRowData, distributionRowData);
		}
	}
	
	private static class ExperimentRecorder {
		private ExcelWriter distributionExcelWriter;
		private ExcelWriter progressExcelWriter;
		private String successfulMethodsFile;
		
		public ExperimentRecorder() {
			distributionExcelWriter = new ExcelWriter(FileUtils.newFile(outputFolder, projectId + "_distribution.xlsx"));
			distributionExcelWriter.getSheet("data", new String[] {"Class", "Method", ""}, 0);
			progressExcelWriter = new ExcelWriter(FileUtils.newFile(outputFolder, projectId + "_progress.xlsx"));
			progressExcelWriter.getSheet("data", new String[] {"Class", "Method", ""}, 0);
			successfulMethodsFile = outputFolder + "successfulMethods.txt";
			FileUtils.writeFile(successfulMethodsFile, "#Project " + projectId + "\n", false);
		}
		
		public void record(List<Object> progressRowData, List<Object> distributionRowData) {
			try {
				progressExcelWriter.writeSheet("data", Arrays.asList(progressRowData));
				distributionExcelWriter.writeSheet("data", Arrays.asList(distributionRowData));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void logSuccessfulMethods(String className, String methodName) {
			FileUtils.writeFile(successfulMethodsFile, CommonUtility.getMethodId(className, methodName) + "\n", true);
		}
	}
}
