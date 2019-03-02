package evosuite.shell;

import java.util.Arrays;
import java.util.List;

public class ParameterOptions {
	public static final String LIST_METHODS_OPT = "-listMethods";
	public static final String INCLUSIVE_FILE_OPT = "-inclusiveFile";
	public static final String EXCLUSIVE_FILE_OPT = "-exclusiveFile";
	public static final String METHOD_TEST_ITERATION = "-iteration";
	public static final String REPORT_FOLDER = "-reportFolder";
	public static final String RUNNING_MARKER_FILE = "-markerFile";
	public static final String TEST_LEVEL = "-testLevel";
	public static final String METHOD_FILTER_OPTION = "-mFilterOpt";
	public static final List<String> ALL_OPTIONS = Arrays.asList(LIST_METHODS_OPT, INCLUSIVE_FILE_OPT,
			EXCLUSIVE_FILE_OPT, METHOD_TEST_ITERATION, REPORT_FOLDER, RUNNING_MARKER_FILE, TEST_LEVEL,
			METHOD_FILTER_OPTION);

	
	public static enum TestLevel {
		lClass,
		lMethod
	}
}
