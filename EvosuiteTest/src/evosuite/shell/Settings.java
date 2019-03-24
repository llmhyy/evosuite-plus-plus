package evosuite.shell;

import org.evosuite.utils.ProgramArgumentUtils;

import evosuite.shell.ParameterOptions.TestLevel;
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

	public static void setup(String[] args) throws Exception {
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
		
		if (ProgramArgumentUtils.hasOpt(args, ParameterOptions.REPORT_BASED_FILTER)) {
			reportBasedFilter = true;
		}
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
}
