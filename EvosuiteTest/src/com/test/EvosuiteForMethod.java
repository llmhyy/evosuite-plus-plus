package com.test;

import java.lang.reflect.Method;
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
import org.evosuite.TestGenerationContext;
import org.evosuite.result.TestGenerationResult;
import org.objectweb.asm.Type;

public class EvosuiteForMethod {

	public static void main(String[] args) throws Exception {
		EvosuiteForMethod tool = new EvosuiteForMethod();
		String[] targetClasses = tool.listAllTargetClasses(args);
		String[] truncatedArgs = tool.extractArgs(args);

//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
		runAllMethods(targetClasses, truncatedArgs);
		System.out.println("Finish!");
		System.exit(0);
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
	
	private String[] extractArgs(String[] args) throws Exception {
		Set<String> excludedOpts = new HashSet<>();
		excludedOpts.add("-target");
		excludedOpts.add("-prefix");
		excludedOpts.add("-class");
		
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
	
	private String[] listAllTargetClasses(String[] args) {
		CommandLine cmd = parseCommandLine(args);
		if (cmd.hasOption("class")) {
			new EvoSuite().setupProperties();
			CommandLineParameters.handleClassPath(cmd); // ensure classpath
			return new String[] {cmd.getOptionValue("class")};
		}
		String[] listClassesArgs = ArrayUtils.addAll(args, "-listClasses");
		EvoSuite evosuite = new EvoSuite();
		evosuite.parseCommandLine(listClassesArgs);
		String[] targetClasses = System.getProperty("evo_targetClasses").split(",");
		System.out.println(StringUtils.join(targetClasses, "\n"));
		return targetClasses;
	}

	public static void runAllMethods(String[] targetClasses, String[] args) throws Exception {
		List<String> allMethods = new ArrayList<>();
//		String testMethod = "com.ib.client.EWrapper" + "#" + "tickPrice(IIDI)V";
//		String testMethod = "com.ib.client.ExecutionFilter#equals(Ljava/lang/Object;)Z";
		for (String className : targetClasses) {
			Class<?> targetClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
			// ignore
			if (targetClass.isInterface()) {
				continue;
			}
//			if (!className.equals("com.ib.client.ExecutionFilter")) {
//				continue;
//			}
			for (Method method : targetClass.getMethods()) {
				String methodName = method.getName() + Type.getMethodDescriptor(method);
				String methodId = targetClass.getName() + "#" + methodName;
//				                                                                                                 
				allMethods.add(methodId);
				FileUtils.writeFile(System.getProperty("user.dir") + "/allMethods.txt", methodId + "\n", true);
				runMethod(methodName, className, args);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void runMethod(String methodName, String className, String[] evosuiteArgs) {
		System.out.println("Run method: " + className + "#" + methodName);
		// $EVOSUITE -criterion branch -target tullibee.jar -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore
		String[] args = ArrayUtils.addAll(evosuiteArgs, 
				"-class", className,
				"-Dtarget_method", methodName
				,"-Dsearch_budget", String.valueOf(10)
				,"-Dstop_zero", "false"
				);
		System.out.println("Run Cmd.." + StringUtils.join((Object[]) args, " "));
		EvoSuite evosuite = new EvoSuite();
		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evosuite.parseCommandLine(args);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				// System.out.println(r);
				System.out.println(r.getProgressInformation());
				if(r.getDistribution() != null){
					for(int i=0; i<r.getDistribution().length; i++){
						System.out.println(r.getDistribution()[i]);					
					}					
				}
			}
		}
	}


}
