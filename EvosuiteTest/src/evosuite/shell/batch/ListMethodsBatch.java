package evosuite.shell.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
	
	@Test
	public void cleanupJdkFile() throws IOException {
		String listFile = SFConfiguration.getReportFolder() + "/jdkClasses.txt";
		List<String> lines = FileUtils.readLines(new File(listFile));
		Set<String> set = new HashSet<String>(lines);
		lines = new ArrayList<>(set);
		Collections.sort(lines);
		evosuite.shell.FileUtils.writeFile(SFConfiguration.getReportFolder() + "/jdkClasses1.txt", 
				StringUtils.join(lines, "\n"), false);
	}
	
	private boolean checkProject(String prjIdx) {
//		return Integer.valueOf(prjIdx) == 28;
		return true;
	}
	
}
