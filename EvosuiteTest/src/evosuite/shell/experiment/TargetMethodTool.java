package evosuite.shell.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.evosuite.utils.CollectionUtil;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.Randomness;

public class TargetMethodTool {
	private String baseDir = System.getProperty("user.dir");

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
		for (String file : CollectionUtil.nullToEmpty(inclusiveFiles)) {
			inclusives.putAll(mergeTxt.readData(file));
		}
		Map<String, Set<String>> exclusives = new HashMap<>();
		for (String file : CollectionUtil.nullToEmpty(exclusivesFiles)) {
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
				baseDir + "/experiments/SF100/reports/flag-filtered-methods-with-GA-involved.txt",
				baseDir + "/experiments/SF100/reports/flag-filtered-methods-with-GA-involved.xlsx");
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
	
	@Test
	public void selectMethods() {
		String excelFile = baseDir + "/experiments/SF100/reports/flag-filtered-wth-GA-involved-branch.xlsx";
		String resultTxt = baseDir + "/experiments/SF100/reports/targetMethods-200methods.txt";
		ExcelReader reader = new ExcelReader(new File(excelFile), 0);
		List<List<Object>> data = reader.listData("data");
		
		for (Iterator<List<Object>> it = data.iterator(); it.hasNext();) {
			List<Object> row = it.next();
			double coverage = ((Number) row.get(ReportHeader.Coverage.ordinal())).doubleValue();
			if (coverage == 0.0 || coverage == 1.0) {
				it.remove();
			}
		}
		
		Map<String, List<List<Object>>> dataMap = toMap(data);
		for (String key : dataMap.keySet()) {
			List<List<Object>> rows = dataMap.get(key);
			if (rows.size() > 10) {
				dataMap.put(key, Randomness.randomSubList(rows, 10));
			}
		}
		data = toList(dataMap);
		
		List<List<Object>> selectedRows = Randomness.randomSubList(data, 200);
		Map<String, List<List<Object>>> selectedMethods = toMap(selectedRows);
		
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			List<List<Object>> rows = selectedMethods.get(projectName);
			StringBuilder sb = new StringBuilder()
					.append("#------------------------------------------------------------------------\n")
					.append("#Project=").append(projectName).append("  -  ").append(projectFolders.get(projectName).getName()).append("\n")
					.append("#------------------------------------------------------------------------\n");
			for (List<Object> row : CollectionUtil.nullToEmpty(rows)) {
				sb.append(row.get(ReportHeader.Class.ordinal())).append("#").append(row.get(ReportHeader.Method.ordinal())).append("\n");
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
	}
	
	private List<List<Object>> toList(Map<String, List<List<Object>>> dataMap) {
		List<List<Object>> data = new ArrayList<>();
		for (List<List<Object>> rows : dataMap.values()) {
			data.addAll(rows);
		}
		return data;
	}

	private Map<String, List<List<Object>>> toMap(List<List<Object>> rows) {
		Map<String, List<List<Object>>> map = new HashMap<>();
		for (List<Object> row : rows) {
			String projectName = ((String)row.get(ReportHeader.ProjectId.ordinal())).split("_")[1];
			CollectionUtil.getListInitIfEmpty(map, projectName).add(row);
		}
		return map;
	}
	
	private enum ReportHeader {
		ProjectId,	Class,	Method,	Execution, Time,	Coverage,	Age
	}
}
