package evosuite.shell.experiment;

import evosuite.shell.FileUtils;

public class SFConfiguration {
	public static String sfBenchmarkFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest";
	
	public static String getReportFolder() {
		return FileUtils.getFilePath(sfBenchmarkFolder, "/evoTest-reports");
	}
	
}
