package evosuite.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.utils.ProgramArgumentUtils;

import evosuite.shell.ParameterOptions.TestLevel;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.listmethod.ListMethods;
import evosuite.shell.listmethod.MethodFilterOption;

public class Settings {
	public static final int DEFAULT_ITERATION = 1;
	public static final String DEFAULT_REPORT_FOLDER_NAME = "evoTest-reports";

	private static String sfBenchmarkFolder = SFConfiguration.sfBenchmarkFolder;
	private static boolean listMethods;
	private static String inclusiveFilePath;
	private static int iteration = DEFAULT_ITERATION;
	private static String reportFolder = DEFAULT_REPORT_FOLDER_NAME;
	private static String markerFile;
	private static TestLevel testLevel = TestLevel.lMethod;
	private static MethodFilterOption mFilterOpt;
	private static String targetMethodFilePath;
	private static boolean reportBasedFilter;
	private static String branchExperimentFile;
	private static boolean runBothMethods = false;
	private static boolean retrieveBranchFeature = false;
	private static String branchLabelFile;
	
	public static List<String> insterestedProjects;

	public static void setup(String[] args) throws Exception {
		runBothMethods = ProgramArgumentUtils.hasOpt(args, ParameterOptions.RUN_BOTH_METHODS);
		
		retrieveBranchFeature = ProgramArgumentUtils.hasOpt(args, ParameterOptions.RETRIEVE_BRANCH_FEATURE);
		branchLabelFile = ProgramArgumentUtils.getOptValue(args, ParameterOptions.BRANCH_LABEL_FILE);
		
		branchExperimentFile = ProgramArgumentUtils.getOptValue(args, ParameterOptions.BRANCH_EXPERIMENT_FILE);
		listMethods = ProgramArgumentUtils.hasOpt(args, ListMethods.OPT_NAME);
		inclusiveFilePath = ProgramArgumentUtils.getOptValue(args, ParameterOptions.INCLUSIVE_FILE_OPT);
		String optValue = ProgramArgumentUtils.getOptValue(args, ParameterOptions.METHOD_TEST_ITERATION);
		if (optValue != null) {
			iteration = Integer.valueOf(optValue);
		}
		optValue = ProgramArgumentUtils.getOptValue(args, ParameterOptions.REPORT_FOLDER);
		if (optValue != null) {
			reportFolder = optValue;
		} else {
			optValue = ProgramArgumentUtils.getOptValue(args, "-criterion");
			if (optValue == null) {
				optValue = ProgramArgumentUtils.getOptValue(args, "-Dcriterion");
			}
			if (optValue != null) {
				StringBuilder sb = new StringBuilder().append("report")
						.append("-").append(optValue)
						;		
				reportFolder = sb.toString();
			}
		}
		optValue = ProgramArgumentUtils.getOptValue(args, ParameterOptions.RUNNING_MARKER_FILE);
		if (optValue != null) {
			markerFile = optValue;
		}
		
		/* test level */
		optValue = ProgramArgumentUtils.getOptValue(args, ParameterOptions.TEST_LEVEL);
		if (optValue != null) {
			testLevel = TestLevel.valueOf(optValue);
		}
		
		/* method filter option */
		optValue = ProgramArgumentUtils.getOptValue(args, ParameterOptions.METHOD_FILTER_OPTION);
		mFilterOpt = MethodFilterOption.HAS_BRANCH;
		if (optValue != null) {
			mFilterOpt = MethodFilterOption.of(optValue);
		}
		
		targetMethodFilePath = getTargetMethodFilePath(mFilterOpt);
		insterestedProjects = parseInterestedProjects(inclusiveFilePath);
		parseInterestedMethods(branchExperimentFile);
		
		if (ProgramArgumentUtils.hasOpt(args, ParameterOptions.REPORT_BASED_FILTER)) {
			reportBasedFilter = true;
		}
		
		checkAnaylyzedMethod();
	}
	
	public static Set<String> analyzedMethods = new HashSet<>();
	private static void checkAnaylyzedMethod() {
		String reportFolder = Settings.getReportFolder();
		String overallFile = reportFolder + File.separator + EvosuiteForMethod.projectId + "_evotest_overall.xlsx";
		
		File f = new File(overallFile);
		if(!f.exists()) {
			return;
		}
		
		ExcelReader reader = new ExcelReader(f, 0);
		List<List<Object>> datas = reader.listData("data");
		if(datas == null) {
			return;
		}
		
		for(List<Object> data: datas) {
			String className = (String) data.get(0);
			String methodName = (String) data.get(1);
			
			String methodID = className + "#" + methodName;
			analyzedMethods.add(methodID);
		}
		
	}
	
	public static Map<String, List<Long>> investigatedMethods; 
	public static Map<String, List<Long>> easyMethods;
	public static Set<String> longRunningMethods;
	
	private static void parseInterestedMethods(String branchExperimentFile2) {
		if(branchExperimentFile2 == null) return;
		
		File f = new File(branchExperimentFile2);
		if(!f.exists()) {
			System.err.print(branchExperimentFile2 + " does not exist");
			return;
		}
		
		investigatedMethods = new HashMap<>();
		easyMethods = new HashMap<>();
		longRunningMethods = new HashSet<>();
		
		ExcelReader reader = new ExcelReader(f, 0);
		List<List<Object>> datas = reader.listData("investigated");
		for(List<Object> data: datas) {
			String className = (String) data.get(0);
			String methodName = (String) data.get(1);
			double seedDouble = (Double) data.get(8);
			Long seed = (long)seedDouble;
			
			String sig = className + "#" + methodName;
			List<Long> seeds = investigatedMethods.get(sig);
			if(seeds == null) {
				seeds = new ArrayList<>();
			}
			
			if(!seeds.contains(seed)) {
				seeds.add(seed);
			}
			
			investigatedMethods.put(sig, seeds);
		}
		
		List<List<Object>> easyDatas = reader.listData("easy");
		for(List<Object> data: easyDatas) {
			String className = (String) data.get(0);
			String methodName = (String) data.get(1);
			double seedDouble = (Double) data.get(8);
			Long seed = (long)seedDouble;
			
			String sig = className + "#" + methodName;
			List<Long> seeds = easyMethods.get(sig);
			if(seeds == null) {
				seeds = new ArrayList<>();
			}
			
			if(!seeds.contains(seed)) {
				seeds.add(seed);
			}
			
			easyMethods.put(sig, seeds);
		}
		
		List<List<Object>> longRunningDatas = reader.listData("longRunning");
		for(List<Object> data: longRunningDatas) {
			String className = (String) data.get(0);
			String methodName = (String) data.get(1);
			
			String sig = className + "#" + methodName;
			longRunningMethods.add(sig);
		}
	}

	private static List<String> parseInterestedProjects(String targetMethodFilePath2) throws IOException {
		if(targetMethodFilePath2==null) {
			return null;
		}
		
		List<String> interestedProjects = new ArrayList<>(); 
		String prefix = "#Project=";
		FileReader fileReader = new FileReader(new File(targetMethodFilePath2));
		BufferedReader br = new BufferedReader(fileReader);
		String line = null;
		while ((line = br.readLine()) != null) {
			if(line.startsWith(prefix)) {
				int lastIndex = line.length();
				if(line.contains(" ")) {
					lastIndex = line.indexOf(" ");
				}
				
				String project = line.substring(prefix.length(), lastIndex);
				interestedProjects.add(project.trim());
			}
		}
		return interestedProjects;
	}

	public static String getReportFolder() {
		return FileUtils.getFilePath(sfBenchmarkFolder, reportFolder);
	}
	
	public static String getTargetMethodFilePath(MethodFilterOption mFilterOpt) {
		return FileUtils.getFilePath(getReportFolder(),
				String.format("targetMethods_%s.txt", mFilterOpt.getText()));
	}
	
	public static String getTargetClassFilePath() {
		return FileUtils.getFilePath(getReportFolder(),
				String.format("targetClasses%s.txt", mFilterOpt.getText()));
	}

	public static boolean isListMethods() {
		return listMethods;
	}

	public static String getInclusiveFilePath() {
		return inclusiveFilePath;
	}

	public static int getIteration() {
		return iteration;
	}
	
	public static String getMarkerFile() {
		return markerFile;
	}
	
	public static TestLevel getTestLevel() {
		return testLevel;
	}

	public static MethodFilterOption getmFilterOpt() {
		return mFilterOpt;
	}
	
	public static String getTargetMethodFilePath() {
		return targetMethodFilePath;
	}
	
	public static void setSfBenchmarkFolder(String sfBenchmarkFolder) {
		Settings.sfBenchmarkFolder = sfBenchmarkFolder;
	}
	
	public static boolean isReportBasedFilterEnable() {
		return reportBasedFilter;
	}

	public static boolean isRunBothMethods() {
		return runBothMethods;
	}

	public static boolean isRetrieveBranchFeature() {
		return retrieveBranchFeature;
	}

	public static String getBranchLabelFile() {
		return branchLabelFile;
	}

	public static void setBranchLabelFile(String branchLabelFile) {
		Settings.branchLabelFile = branchLabelFile;
	}

}
