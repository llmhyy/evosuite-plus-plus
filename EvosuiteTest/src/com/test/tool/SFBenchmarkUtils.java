package com.test.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.utils.CollectionUtil;

import com.test.utils.AlphanumComparator;

public class SFBenchmarkUtils {
	
	public static Map<String, File> listProjectFolders() {
		File[] files = new File(SFConfiguration.sfBenchmarkFolder).listFiles(new FilenameFilter() {
			
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
