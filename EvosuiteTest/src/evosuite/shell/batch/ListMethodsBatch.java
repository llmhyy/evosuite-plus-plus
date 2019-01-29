package evosuite.shell.batch;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;

public class ListMethodsBatch {

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100-listMethods";
	}
	
	@Test
	public void runAll() {
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
	}

	private boolean checkProject(String prjIdx) {
//		return Integer.valueOf(prjIdx) == 24;
		return true;
	}
	
}
