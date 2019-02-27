package evosuite.shell.experiment.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GenerateScripts {
	String baseDir = System.getProperty("user.dir");
	private String branchTemplate;
	private String fbranchTemplate;
	private String outputFolder;
	
	@Test
	public void generateAllScriptsSF100() throws IOException {
		branchTemplate = baseDir + "/experiments/SF100/reports/scripts/script-template-branch.sh";
		fbranchTemplate = baseDir + "/experiments/SF100/reports/scripts/script-template-fbranch.sh";
		outputFolder = baseDir + "/experiments/SF100/reports/scripts";
		for (int i = 1; i <= 18; i++) {
			generateScript("targetMethod_byType_determined_set" + i, "", "determined-set-" + i);
		}
		generateScript("targetMethod_byType_undetermined", "-undetermined", "undetermined");
		generateScript("targetMethod_byType_uninterested_100", "-uninterested", "uninterested");
		generateScript("targetMethod_byType_uninstrumentable_100", "-uninstrumentable", "uninstrumentable");
	}
	
	@Test
	public void generateAllScriptsForMaths() throws IOException {
		branchTemplate = baseDir + "/experiments/testProjects/scripts/script-template-branch.sh";
		fbranchTemplate = baseDir + "/experiments/testProjects/scripts/script-template-fbranch.sh";
		outputFolder = baseDir + "/experiments/testProjects/scripts";
		generateScript("targetMethod_byType_undetermined", "-4prj-undetermined", "4prj-undetermined");
		generateScript("targetMethod_byType_uninterested", "-4prj-uninterested", "4prj-uninterested");
		generateScript("targetMethod_byType_uninstrumentable_100", "-4prj-uninstrumentable", "4prj-uninstrumentable");
	}
	
	private void generateScript(String fileName, String reportFolderSubfix, String name) throws IOException {
		List<String> content = org.apache.commons.io.FileUtils.readLines(new File(branchTemplate));
		List<String> newContent = new ArrayList<String>();
		for (String line : content) {
			String newLine = line;
			if (line.contains("${FILE_NAME}")) {
				newLine = line.replace("${FILE_NAME}", fileName);
			} else if(line.contains("${folder-subfix}")) {
				newLine = line.replace("${folder-subfix}", reportFolderSubfix);
			}
			newContent.add(newLine);
		}
		org.apache.commons.io.FileUtils
				.writeLines(new File(outputFolder + String.format("/evotest-%s-branch.sh", name)), newContent);
		
		content = org.apache.commons.io.FileUtils.readLines(new File(fbranchTemplate));
		newContent = new ArrayList<String>();
		for (String line : content) {
			String newLine = line;
			if (line.contains("${FILE_NAME}")) {
				newLine = line.replace("${FILE_NAME}", fileName);
			} else if(line.contains("${folder-subfix}")) {
				newLine = line.replace("${folder-subfix}", reportFolderSubfix);
			}
			newContent.add(newLine);
		}
		org.apache.commons.io.FileUtils.writeLines(
				new File(outputFolder + String.format("/evotest-%s-fbranch.sh", name)),
				newContent);
	}
	
}
