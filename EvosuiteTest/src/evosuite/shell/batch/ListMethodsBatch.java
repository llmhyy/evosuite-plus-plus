package evosuite.shell.batch;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.experiment.SFBenchmarkUtils;

public class ListMethodsBatch {

	
	public static void main(String[] args) {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			File projectFolder = projectFolders.get(projectName);
			String[] name = projectFolder.getName().split("_");
			if (NumberUtils.isCreatable(name[0])) {
				SFBenchmarkUtils.setupProjectProperties(projectFolder);
				args = new String[] {
						"-target",
						projectFolder.getAbsolutePath() + "/" + name[1] + ".jar",
						"-listMethods"	
				};
				EvosuiteForMethod.main(args);
			}
		}
	}
	
}
