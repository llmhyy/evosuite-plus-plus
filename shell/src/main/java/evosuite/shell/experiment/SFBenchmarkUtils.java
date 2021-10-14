package evosuite.shell.experiment;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.utils.CollectionUtil;

import evosuite.shell.FileUtils;
import evosuite.shell.utils.AlphanumComparator;

public class SFBenchmarkUtils {
	private static final boolean USE_USER_DIR = true;
	
	/**
	 * return map of project name and its folder */
	public static Map<String, File> listProjectFolders() {
		String workingFolder = "";
		
		if (USE_USER_DIR) {
			workingFolder = System.getProperty("user.dir");
		} else {
			workingFolder = SFConfiguration.sfBenchmarkFolder;
		}
			
		File[] files = new File(workingFolder).listFiles(new FilenameFilter() {
			
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
			if (NumberUtils.isNumber(folder.getName().split("_")[0])) {
				result.put(folder.getName().split("_")[1], folder);
			}
		}
		return result;
	}
	
	public static String getProjectName(String projectId) {
		int idx = projectId.indexOf("_");
		if (idx > 0) {
			return projectId.substring(idx + 1);
		}
		return projectId;
	}
	
	public static List<String> setupProjectProperties(File projectFolder) {
		ClassPathHandler.resetSingleton();
		DefUseAnalyzer.resetSingleton();
		System.setProperty("user.dir", projectFolder.getAbsolutePath());
		EvoSuite.base_dir_path = System.getProperty("user.dir");
		
		List<String> classPaths = new ArrayList<String>();
		
		File propertiesFile = new File(EvoSuite.base_dir_path + "/evosuite-files/evosuite.properties");
		File newPropertiesFile = null;
		if (propertiesFile.exists()) {
			newPropertiesFile = new File(propertiesFile.getAbsolutePath().replace("evosuite.properties", "evosuite-test.properties"));
			newPropertiesFile.deleteOnExit();
			classPaths = replaceRelativeWithAbsolutePaths(projectFolder, propertiesFile, newPropertiesFile);
		}
		if (newPropertiesFile != null) {
			Properties.PROPERTIES_FILE = "evosuite-files/evosuite-test.properties";
			System.setProperty("PROPERTIES_FILE", "evosuite-files/evosuite-test.properties");
		}
		new EvoSuite().setupProperties();
		return classPaths;
	}
	
	private static List<String> replaceRelativeWithAbsolutePaths(File projFolder, File propertiesFile, File newFile) {
		List<String> classPaths = new ArrayList<String>();
		
		try {
			List<String> contents = org.apache.commons.io.FileUtils.readLines(propertiesFile, "UTF-8");
			List<String> newContents = new ArrayList<>(contents.size());
			for (String line : contents) {
				if (line.startsWith("CP")) {
					line = line.replace(":lib/*", "");
					System.out.println(line);
					String[] cp = line.trim().split("=");
					if (cp.length > 1) {
						if (cp[1].contains(File.pathSeparator)) {
							cp = cp[1].split(File.pathSeparator);
						} else {
							cp = new String[] { cp[1] };
						}
						List<String> entries = new ArrayList<>();
						for (String path : cp) {
							if (path.endsWith(".txt")) {
								continue;
							}
							String newPath = projFolder.getAbsolutePath() + File.separator + path;
							if (new File(newPath).exists()) {
								entries.add(newPath.replace("\\", "/"));
							}
						}
						line = "CP=" + StringUtils.join(entries, File.pathSeparator);
						classPaths.addAll(entries);
					}
					System.out.println("new " + line);
				} else if (line.startsWith("inheritance_file")) {
					String path = line.split("=")[1];
					if (!new File(path).exists() && new File(EvoSuite.base_dir_path + "/evosuite-files/inheritance.xml.gz").exists()) {
						line = "inheritance_file=" + EvoSuite.base_dir_path + "/evosuite-files/inheritance.xml.gz";
						line = line.replace("\\", "/");
					} 
				} else if (line.startsWith("test_dir")) {
					String path = line.split("=")[1];
					if (!new File(path).exists()) {
						line = "test_dir=" + EvoSuite.base_dir_path + "/evosuite-tests";
						line = line.replace("\\", "/");
					} 
				}
				newContents.add(line);
			}

			Properties.TEST_DIR = EvoSuite.base_dir_path + "/evosuite-tests";
			org.apache.commons.io.FileUtils.writeLines(newFile, newContents);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return classPaths;
	}
	
	public static List<String> setupProjectProperties(String projectId) {
		File projectFolder = new File(SFConfiguration.sfBenchmarkFolder + "/" + projectId); 
		return setupProjectProperties(projectFolder);
	}
	
	public static void writeInclusiveFile(File file, boolean append, String projectName, String... methods) {
		StringBuilder content = new StringBuilder()
				.append("#Project=")
				.append(projectName).append("\n");
		for (String method : methods) {
			content.append(method).append("\n");
		}
			
		FileUtils.writeFile(file.getAbsolutePath(), content.toString(), append);
	}
	
	public static <T extends Collection<String>>List<String> toList(Map<String, T> projectElementMap) {
		List<String> list = new ArrayList<>();
		for (String projectName : projectElementMap.keySet()) {
			for (String classOrMethodElement : projectElementMap.get(projectName)) {
				list.add(projectName + "#" + classOrMethodElement);
			}
		}
		return list;
	}
	
	public static Map<String, List<String>> toMap(List<String> prjElements) {
		Map<String, List<String>> map = new HashMap<>();
		for (String classOrMethodElement : prjElements) {
			int idx = classOrMethodElement.indexOf("#");
			CollectionUtil.getListInitIfEmpty(map, classOrMethodElement.substring(0, idx)).add(classOrMethodElement.substring(idx + 1));
		}
		return map;
	}
}
