package evosuite.shell.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.evosuite.utils.CollectionUtil;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.ListMethods;
import evosuite.shell.excel.ExcelWriter;

public class TargetMethodTool {

	@Before
	public void setup() {
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
	}
	
	public static void main(String[] args) throws IOException {
		String root = SFConfiguration.sfBenchmarkFolder + "/evoTest-reports";
		List<String> inclusiveFiles = Arrays.asList(root + "/flagFilteredMethods.txt");
		List<String> exclusivesFiles = Arrays.asList(root + "/executed_methods.txt");
		
		String resultTxt = root + "/merge.txt";
		merge(inclusiveFiles, exclusivesFiles, resultTxt);
	}

	private static void merge(List<String> inclusiveFiles, List<String> exclusivesFiles, String resultTxt)
			throws IOException {
		TargetMethodTool mergeTxt = new TargetMethodTool();
		Map<String, Set<String>> inclusives = new HashMap<>();
		for (String file : inclusiveFiles) {
			inclusives.putAll(mergeTxt.readData(file));
		}
		Map<String, Set<String>> exclusives = new HashMap<>();
		for (String file : exclusivesFiles) {
			exclusives.putAll(mergeTxt.readData(file));
		}

		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			String projectId = projectFolders.get(projectName).getName();
			List<String> remainMethods = new ArrayList<>(CollectionUtil.nullToEmpty(inclusives.get(projectName)));
			remainMethods.removeAll(CollectionUtil.nullToEmpty(exclusives.get(projectName)));
			StringBuilder sb = new StringBuilder()
						.append("#------------------------------------------------------------------------\n")
						.append("#Project=").append(projectName).append("  -  ").append(projectId).append("\n")
						.append("#------------------------------------------------------------------------\n");
			for (String method : remainMethods) {
				sb.append(method).append("\n");
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
		System.out.println("Done!");
	}
	
	public Map<String, Set<String>> readData(String file) throws IOException {
		Map<String, Set<String>> content = new LinkedHashMap<>();
		List<String> lines = FileUtils.readLines(new File(file), Charset.defaultCharset());
		String curProject = null;
		for (String line : lines) {
			if (line.toLowerCase().startsWith("#project=")) {
				curProject = line.split("=")[1].split(" ")[0];
			} else {
				if (!line.startsWith("#")) {
					CollectionUtil.getSetInitIfEmpty(content, curProject).add(line);
				}
			}
		}
		return content;
	}

	@Test
	public void generateStatisticExcel() throws IOException {
		generateMethodStatisticExcel(
				"/Users/lylytran/Projects/Evosuite/modified-version/evosuite/EvosuiteTest/experiments/SF100/reports/flag-filtered-methods-with-GA-involved.txt",
				"/Users/lylytran/Projects/Evosuite/modified-version/evosuite/EvosuiteTest/experiments/SF100/reports/flag-filtered-methods-with-GA-involved.xlsx");
	}
	
	public void generateMethodStatisticExcel(String targetMethodTxt, String excelFile) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		Map<String, Set<String>> targetMethodsMap = readData(targetMethodTxt);
		ExcelWriter excelWriter = new ExcelWriter(new File(excelFile));
		excelWriter.getSheet("data", new String[]{"ProjectId", "ProjectName", "Number of Flag-problem methods"}, 0);
		List<List<Object>> data = new ArrayList<>();
		for (String project : projectFolders.keySet()) {
			List<Object> row = new ArrayList<>();
			row.add(projectFolders.get(project).getName());
			row.add(project);
			row.add(CollectionUtil.getSize(targetMethodsMap.get(project)));
			data.add(row);
		}
		excelWriter.writeSheet("data", data);
	}
}
