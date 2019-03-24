package evosuite.shell.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.evosuite.utils.CollectionUtil;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFBenchmarkUtils;

public class TargetMethodIOUtils {
	
	private TargetMethodIOUtils(){}

	public static void merge(List<String> inclusiveFiles, List<String> exclusivesFiles, String resultTxt)
			throws IOException {
		Map<String, Set<String>> inclusives = new HashMap<>();
		for (String file : CollectionUtil.nullToEmpty(inclusiveFiles)) {
			inclusives.putAll(readData(file));
		}
		Map<String, Set<String>> exclusives = new HashMap<>();
		for (String file : CollectionUtil.nullToEmpty(exclusivesFiles)) {
			exclusives.putAll(readData(file));
		}
		
		int total = 0;
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
				total ++;
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
		evosuite.shell.FileUtils.writeFile(resultTxt, new StringBuilder().append("# total: ").append(total).toString(), true);
		System.out.println("Done!");
	}
	
	public static Map<String, Set<String>> readData(String file) throws IOException {
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

	public static void generateMethodDistributionExcel(String interprocedureFlagMethodsTxt, String flagMethodsTxt, String allMethodsTxt, String excelFile) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		Map<String, Set<String>> interprocedureFlagMethodsMap = readData(interprocedureFlagMethodsTxt);
		Map<String, Set<String>> flagMethodsMap = readData(flagMethodsTxt);
		Map<String, Set<String>> allMethodsMap = readData(allMethodsTxt);
		ExcelWriter excelWriter = new ExcelWriter(new File(excelFile));
		excelWriter.getSheet("data", new String[]{"ProjectId", "ProjectName", "Total methods", "Flag methods", "Flag methods/total", "Interprocedure flag methods", "Interprocedure flag methods/Total"}, 0);
		List<List<Object>> data = new ArrayList<>();
		for (String project : projectFolders.keySet()) {
			List<Object> row = new ArrayList<>();
			row.add(projectFolders.get(project).getName());
			row.add(project);

			int totalMethods = CollectionUtil.getSize(allMethodsMap.get(project));
			row.add(totalMethods);

			int totalFlagMethods = CollectionUtil.getSize(flagMethodsMap.get(project));
			row.add(totalFlagMethods);
			
			row.add((float) totalFlagMethods / (float)totalMethods);

			int totalInterprocedureFlagMethods = CollectionUtil.getSize(interprocedureFlagMethodsMap.get(project));
			row.add(totalInterprocedureFlagMethods);
			
			row.add((float) totalInterprocedureFlagMethods / (float)totalMethods);
			data.add(row);
		}
		excelWriter.writeSheet("data", data);
	}
	
	public static void generateMethodDistributionExcel(String targetMethodTxt, String allMethodsTxt, String excelFile) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		Map<String, Set<String>> targetMethodsMap = readData(targetMethodTxt);
		Map<String, Set<String>> allMethodsMap = readData(allMethodsTxt);
		ExcelWriter excelWriter = new ExcelWriter(new File(excelFile));
		excelWriter.getSheet("data", new String[]{"ProjectId", "ProjectName", "Total methods", "Total target methods", "Ratio"}, 0);
		List<List<Object>> data = new ArrayList<>();
		for (String project : projectFolders.keySet()) {
			List<Object> row = new ArrayList<>();
			row.add(projectFolders.get(project).getName());
			row.add(project);
			int totalMethods = CollectionUtil.getSize(allMethodsMap.get(project));
			row.add(totalMethods);
			int totalTargetMethods = CollectionUtil.getSize(targetMethodsMap.get(project));
			row.add(totalTargetMethods);
			row.add((float) totalTargetMethods / (float)totalMethods);
			data.add(row);
		}
		excelWriter.writeSheet("data", data);
	}
	
	public static void listTestableClasses(String targetMethodTxt, String targetClassesTxt) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		Map<String, Set<String>> targetMethodsMap = readData(targetMethodTxt);
		Map<String, List<String>> targetClassesMap = new LinkedHashMap<>();
		for (String project : projectFolders.keySet()) {
			Set<String> methods = targetMethodsMap.get(project);
			for (String method : CollectionUtil.nullToEmpty(methods)) {
				String className = method.split("#")[0];
				List<String> classes = CollectionUtil.getListInitIfEmpty(targetClassesMap, project);
				if (!classes.contains(className)) {
					classes.add(className);
				}
			}
		}
		writeTargetClassOrMethodTxt(targetClassesTxt, targetClassesMap);
	}
	
	public static void generateStatisticExcel(String targetMethodTxt, String excelFile) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		Map<String, Set<String>> targetMethodsMap = readData(targetMethodTxt);
		ExcelWriter excelWriter = new ExcelWriter(new File(excelFile));
		excelWriter.getSheet("data", new String[]{"ProjectId", "ProjectName", "Total methods"}, 0);
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
	
	public static void selectMethods(String excelFile, String resultTxt, String inclusiveTxt) throws IOException {
		ExcelReader reader = new ExcelReader(new File(excelFile), 0);
		Map<String, Set<String>> inclusive = readData(inclusiveTxt);
		List<List<Object>> data = reader.listData("data");
		
		for (Iterator<List<Object>> it = data.iterator(); it.hasNext();) {
			List<Object> row = it.next();
			double coverage = ((Number) row.get(ReportHeader.Coverage.ordinal())).doubleValue();
			if (coverage == 0.0 || coverage == 1.0) {
				it.remove();
			} else {
				String project = ((String) row.get(ReportHeader.ProjectId.ordinal())).split("_")[1];
				if (inclusive.get(project) == null || !inclusive.get(project).contains(row.get(ReportHeader.Class.ordinal()) + "#" + row.get(ReportHeader.Method.ordinal()))) {
					it.remove();;
				}
			}
		}
		int maxPerProject = 4;
		Map<String, List<List<Object>>> dataMap = toMap(data);
		for (String key : dataMap.keySet()) {
			List<List<Object>> rows = dataMap.get(key);
			if (rows.size() > maxPerProject) {
				dataMap.put(key, Randomness.randomSubList(rows, maxPerProject));
			}
		}
		data = CollectionUtil.toList(dataMap);
		
		List<List<Object>> selectedRows = Randomness.randomSubList(data, 100);
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
		reader.close();
	}
	
	public static void selectMethods(String resultTxt, String inclusiveTxt, int limit) throws IOException {
		new File(resultTxt).delete();
		Map<String, Set<String>> inclusive = readData(inclusiveTxt);
		List<String> selectedMethods = toList(inclusive);
		if (limit < selectedMethods.size()) {
			selectedMethods = Randomness.randomSubList(new ArrayList<String>(selectedMethods), 100);
		}
		Map<String, List<String>> map = new HashMap<>();
		for (String method : selectedMethods) {
			int idx = method.indexOf("#");
			CollectionUtil.getListInitIfEmpty(map, method.substring(0, idx)).add(method.substring(idx + 1));
		}
		writeTargetClassOrMethodTxt(resultTxt, map);
	}
	
	public static void splitMethods(String resultTemplateTxt, String inclusiveTxt, int limit) throws IOException {
		new File(resultTemplateTxt).delete();
		Map<String, Set<String>> inclusive = readData(inclusiveTxt);
		List<Map<String, List<String>>> maps = new ArrayList<>();
		Map<String, List<String>> current = new HashMap<>();
		maps.add(current);
		int subTotal = 0;
		for (String project : inclusive.keySet()) {
			for (String method : CollectionUtil.nullToEmpty(inclusive.get(project))) {
				if(subTotal >= limit) {
					current = new HashMap<>();
					maps.add(current);
					subTotal = 0;
				}
				CollectionUtil.getListInitIfEmpty(current, project).add(method);
				subTotal++;
			}
		}
		int i = 0;
		for (Map<String, List<String>> map : maps) {
			writeTargetClassOrMethodTxt(String.format("%s_set%d.txt", resultTemplateTxt, ++i), map);
		}
	}

	private static void writeTargetClassOrMethodTxt(String resultTxt, Map<String, List<String>> map) {
		new File(resultTxt).delete();
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		int total = 0;
		for (String projectName : projectFolders.keySet()) {
			List<String> methods = map.get(projectName);
			StringBuilder sb = new StringBuilder()
					.append("#------------------------------------------------------------------------\n")
					.append("#Project=").append(projectName).append("  -  ").append(projectFolders.get(projectName).getName()).append("\n")
					.append("#------------------------------------------------------------------------\n");
			for (String method : CollectionUtil.nullToEmpty(methods)) {
				sb.append(method).append("\n");
				total++;
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
		StringBuilder sb = new StringBuilder()
				.append("#-------- Total: ").append(total).append("--------");
		evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
	}
	
	public static void writeTargetClassOrMethodTxt(String projectName, String projectId, List<String> targetClassOrMethodList, String resultTxt) {
		StringBuilder sb = new StringBuilder()
				.append("#------------------------------------------------------------------------\n")
				.append("#Project=").append(projectName).append("  -  ").append(projectId).append("\n")
				.append("#------------------------------------------------------------------------\n");
		for (String targetClassOrMethod : CollectionUtil.nullToEmpty(targetClassOrMethodList)) {
			sb.append(targetClassOrMethod).append("\n");
		}
		evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
	}
	

	private static List<String> toList(Map<String, Set<String>> inclusive) {
		List<String> methods = new ArrayList<>();
		for (String projectName : inclusive.keySet()) {
			for (String method : inclusive.get(projectName)) {
				methods.add(projectName + "#" + method);
			}
		}
		return methods;
	}

	private static Map<String, List<List<Object>>> toMap(List<List<Object>> rows) {
		Map<String, List<List<Object>>> map = new HashMap<>();
		for (List<Object> row : rows) {
			String projectName = ((String)row.get(ReportHeader.ProjectId.ordinal())).split("_")[1];
			CollectionUtil.getListInitIfEmpty(map, projectName).add(row);
		}
		return map;
	}
	
	public static Set<String> collectMethods(String reportFile) {
		Set<String> methods = new HashSet<>();
		File file = new File(reportFile);
		if (!file.exists()) {
			return methods;
		}
		try {
			ExcelReader reader = new ExcelReader(file, 0);
			List<List<Object>> rows = reader.listData("data");
			for (List<Object> row : rows) {
				if (row.size() <= 2) {
					continue;
				}
				String methodId = row.get(0) + "#" + row.get(1);
				methods.add(methodId);
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return methods;
	}
	
	private enum ReportHeader {
		ProjectId,	Class,	Method,	Execution, Time,	Coverage,	Age
	}
}
