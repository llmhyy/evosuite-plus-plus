package evosuite.shell.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConfigurationTransformer {

	public static String fileLocation = "D:\\linyun\\git_space\\SF100-clean";
	
	public static void main(String[] args) throws IOException {
		File folder = new File(fileLocation);
		
		if(folder.isDirectory()) {
			for(File file: folder.listFiles()) {
				if(file.getName().contains("_")) {
					
					String configurationFile = fileLocation + 
							File.separator + file.getName() + File.separator + 
							"evosuite-files" + File.separator + "evosuite.properties";
					
					BufferedReader reader = new BufferedReader(new FileReader(configurationFile));
					StringBuilder stringBuilder = new StringBuilder();
					String line = null;
					String ls = System.getProperty("line.separator");
					while ((line = reader.readLine()) != null) {
						
						if(line.startsWith("CP=")) {
							line = line.replace(":", ";");
						}
						
						stringBuilder.append(line);
						stringBuilder.append(ls);
					}
					// delete the last new line separator
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					reader.close();

					String content = stringBuilder.toString();
					
					try (PrintWriter out = new PrintWriter(configurationFile)) {
					    out.println(content);
					}
				}
			}
		}

	}

}
