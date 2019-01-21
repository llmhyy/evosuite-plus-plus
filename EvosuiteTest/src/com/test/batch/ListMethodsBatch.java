package com.test.batch;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.utils.CollectionUtil;

import com.test.EvosuiteForMethod;
import com.test.utils.AlphanumComparator;

public class ListMethodsBatch {

	
	public static void main(String[] args) {
		String sF100Folder = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest";
		Map<String, File> projectFolders = listProjectFolders(sF100Folder);
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

	public static Map<String, File> listProjectFolders(String sf100Folder) {
		File[] files = new File(sf100Folder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("_") && !name.startsWith(".");
			}
		});
		List<File> projectFolders = CollectionUtil.toArrayList(files);
		AlphanumComparator comparator = new AlphanumComparator();
		Collections.sort(projectFolders, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return comparator.compare(o1.getName(), o2.getName());
			}
		});
		Map<String, File> result = new LinkedHashMap<String, File>();
		for (File folder : projectFolders) {
			result.put(folder.getName().split("_")[1], folder);
		}
		return result;
	}
}
