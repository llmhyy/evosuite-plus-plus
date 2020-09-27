package evosuite.shell.experiment;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.utils.CollectionUtil;
import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.utils.Randomness;

public class ExcelClassCollector {
	
	@Test
	public void run() {
		File report = new File("/Users/lylytran/Projects/Evosuite/experiments/replication_package/selected_classes.xlsx");
		Map<String, List<String>> classesMap = collectClasses(report);
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		Map<String, List<String>> testableClassMap = new HashMap<>();
		for (String project : classesMap.keySet()) {
			String projectName = SFBenchmarkUtils.getProjectName(project);
			if (projectFolders.containsKey(projectName)) {
				CollectionUtil.getListInitIfEmpty(testableClassMap, projectName).addAll(classesMap.get(project));
			}
		}
		List<String> testableClasses = SFBenchmarkUtils.toList(testableClassMap);
		if (testableClasses.size() > 20) {
			testableClasses = Randomness.randomSubList(testableClasses, 20);
		}
		testableClassMap = SFBenchmarkUtils.toMap(testableClasses);
		
		writeFile(projectFolders, testableClassMap, TestUtils.getAbsolutePath("/experiments/SF100-ForClass/20Classes.txt"));
	}

	private void writeFile(Map<String, File> projectFolders, Map<String, List<String>> testableClassMap,
			String resultFile) {
		int total = 0;
		StringBuilder sb = new StringBuilder();
		for (String project : projectFolders.keySet()) {
			if (CollectionUtil.isNotEmpty(testableClassMap.get(project))) {
				sb.append("#---------------------------------------------------------------------------\n")
				.append("#Project=").append(project).append("  -  ").append(projectFolders.get(project).getName()).append("\n")
				.append("#---------------------------------------------------------------------------\n");
				for (String className : testableClassMap.get(project)) {
					sb.append(className).append("\n");
					total++;
				}
			}
		}
		sb.append("#Total: ").append(total).append("\n");
		FileUtils.writeFile(resultFile, sb.toString(), false);
	}

	public Map<String, List<String>> collectClasses(File report) {
		ExcelReader reader = new ExcelReader(report, 0);
		List<List<Object>> data = reader.listData("selected_classes");
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (List<Object> row : data) {
			int idx = 0;
			String project = (String) row.get(idx++);
			String className = (String) row.get(idx);
			CollectionUtil.getListInitIfEmpty(result, project).add(className);
		}
		return result;
	}
}
