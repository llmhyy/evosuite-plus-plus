package com.test.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;

import com.test.EvosuiteForMethod;
import com.test.tool.SFBenchmarkUtils;

public class ListMethodsBatch {

	
	public static void main(String[] args) {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			File projectFolder = projectFolders.get(projectName);
			String[] name = projectFolder.getName().split("_");
			if (NumberUtils.isCreatable(name[0])) {
//				Integer projectOrder = Integer.valueOf(name[0]);
//				if (projectOrder > 13) {
//					break;
//				}
//				Properties.CP = "";
				ClassPathHandler.resetSingleton();
				System.setProperty("user.dir", projectFolder.getAbsolutePath());
				EvoSuite.base_dir_path = System.getProperty("user.dir");
				new EvoSuite().setupProperties();
				List<String> newCp = new ArrayList<>();
				String cp = Properties.CP;
				if (cp != null && !cp.isEmpty()) {
					for (String clp : cp.split(File.pathSeparator)) {
						newCp.add(EvoSuite.base_dir_path + "/" + clp);
					}
				}
				Properties.CP = StringUtils.join(newCp, File.pathSeparator);
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
