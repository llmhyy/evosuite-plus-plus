package evosuite.shell.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class EvoTestTool {
	
	public static void main(String[] args) throws IOException {
		if ("-refineProp".equals(args[0])) {
			cleanUpPropertiesFile(args[1]); // SF100 path
		}
		System.out.println("Finish!");
	}
	
	@Test
	public void testRunUpdateFile() {
		try {
			cleanUpPropertiesFile(SFConfiguration.sfBenchmarkFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void cleanUpPropertiesFile(String rootFolder) throws IOException {
		File file = new File(rootFolder);
		for (File projFolder : file.listFiles()) {
			if (projFolder.isDirectory() && projFolder.getName().contains("_")) {
				System.out.println("Project: " + projFolder.getName());
				String projectName = projFolder.getName().substring(projFolder.getName().indexOf("_") + 1);
				File propertiesFile = new File(projFolder.getAbsolutePath() + "/evosuite-files/evosuite.properties"); 
				List<String> contents = FileUtils.readLines(propertiesFile, "UTF-8");
				List<String> newContents = new ArrayList<>(contents.size());
				for (String line : contents) {
					if (line.startsWith("CP")) {
						System.out.println(line);
						String[] cp = line.trim().split("=");
						if (cp.length > 1) {
							if (cp[1].contains(":")) {
								cp = cp[1].split(":");
							} 
							else if(cp[1].contains(";")) {
								cp = cp[1].split(";");
							}
							else {
								cp = new String[] {cp[1]};
							}
							List<String> entries = new ArrayList<>();
//							entries.add(projectName + ".jar");
							for (String path : cp) {
//								File clp = new File(projFolder.getAbsolutePath() + File.separator + path);
//								clp.exists() && clp.getName().endsWith(".jar") &&  
								if (!entries.contains(path)) {
									entries.add(path);
								}
							}
							line = "CP=" + StringUtils.join(entries, File.pathSeparator);
						}
						System.out.println("new " + line);
					}
					newContents.add(line);
				}
				FileUtils.writeLines(propertiesFile, newContents);
			}
		}
	}
}
