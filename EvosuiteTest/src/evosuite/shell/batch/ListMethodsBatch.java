package evosuite.shell.batch;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.ListMethods;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.experiment.TargetMethodTool;

public class ListMethodsBatch {

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest";
	}
	
	@Test
	public void runAll() throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			File projectFolder = projectFolders.get(projectName);
			String[] name = projectFolder.getName().split("_");
			if (NumberUtils.isCreatable(name[0]) && checkProject(name[0])) {
				SFBenchmarkUtils.setupProjectProperties(projectFolder);
				String[] args = new String[] {
						"-target",
						projectFolder.getAbsolutePath() + "/" + name[1] + ".jar",
						"-listMethods"	
				};
				EvosuiteForMethod.execute(args);
			}
		}
		TargetMethodTool tool = new TargetMethodTool();
		tool.generateMethodStatisticExcel(ListMethods.getTargetFilePath(),
				evosuite.shell.FileUtils.getFilePath(SFConfiguration.getReportFolder(), "targetMethodsStatistic.xlsx"));
	}
	
	private boolean checkProject(String prjIdx) {
//		return Integer.valueOf(prjIdx) == 28;
		return true;
	}
	
}
