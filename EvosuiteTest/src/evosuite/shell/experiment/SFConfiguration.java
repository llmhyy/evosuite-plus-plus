package evosuite.shell.experiment;

import evosuite.shell.FileUtils;
import evosuite.shell.Settings;
import evosuite.shell.listmethod.MethodFilterOption;

public class SFConfiguration {
	public static String sfBenchmarkFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest";
	
	public static String getReportFolder() {
		return FileUtils.getFilePath(sfBenchmarkFolder, Settings.getReportFolder());
	}
	
	public static String getTargetMethodFilePath(MethodFilterOption mFilterOpt) {
		return FileUtils.getFilePath(SFConfiguration.getReportFolder(),
				String.format("targetMethods_%s.txt", mFilterOpt.getText()));
	}
}
