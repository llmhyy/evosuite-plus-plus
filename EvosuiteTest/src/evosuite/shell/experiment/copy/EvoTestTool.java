package evosuite.shell.experiment.copy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class EvoTestTool {
	
	public static void main(String[] args) throws IOException {
		if ("-refineProp".equals(args[0])) {
			cleanUpPropertiesFile(args[1]); // SF100 path
		}
		System.out.println("Finish!");
	}

	public static void cleanUpPropertiesFile(String rootFolder) throws IOException {
		File file = new File(rootFolder);
		for (File projFolder : file.listFiles()) {
			if (projFolder.isDirectory() && projFolder.getName().contains("_")) {
				System.out.println("Project: " + projFolder.getName());
				File propertiesFile = new File(projFolder.getAbsolutePath() + "/evosuite-files/evosuite.properties"); 
				List<String> contents = FileUtils.readLines(propertiesFile, "UTF-8");
				List<String> newContents = new ArrayList<>(contents.size());
				for (String line : contents) {
					if (line.startsWith("CP")) {
						System.out.println(line);
						String[] cp = line.trim().split("=");
						if (cp.length > 1) {
							if (cp[1].contains(File.pathSeparator)) {
								cp = cp[1].split(File.pathSeparator);
							} else {
								cp = new String[] {cp[1]};
							}
							List<String> entries = new ArrayList<>();
							for (String path : cp) {
								if (new File(projFolder.getAbsolutePath() + File.separator + path).exists()) {
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
