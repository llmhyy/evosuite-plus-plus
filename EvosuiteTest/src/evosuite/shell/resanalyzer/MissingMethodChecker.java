package evosuite.shell.resanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.experiment.SFConfiguration;

public class MissingMethodChecker {

	public static void main(String[] args) {
		MissingMethodChecker checker = new MissingMethodChecker();
		
//		String branchSummaryAddress = SFConfiguration.sfBenchmarkFolder + File.separator + "summary.xlsx";
		String resultFile = SFConfiguration.sfBenchmarkFolder 
				+ File.separator + "report-fbranch" + File.separator + "overall_compare.xlsx";
		String targetMethodFile = SFConfiguration.sfBenchmarkFolder 
				+ File.separator + "target-method-104-all.txt";
		String summaryFile = SFConfiguration.sfBenchmarkFolder 
				+ File.separator + "summary.xlsx";
		checker.runAnalyzer(resultFile, targetMethodFile, summaryFile);

	}

	private void runAnalyzer(String resultFile, String targetMethodFile, String summaryFile) {
		Map<String, Integer> resultedMethods = retriveResultedMethods(resultFile);
		Map<String, String> totalMethods = retrieveTotalMethods(targetMethodFile);
		Set<String> removedMethods = retrieveRemovedMethods(summaryFile);
		
		Map<String, List<String>> missingMethods = new HashMap<>();
		if(resultedMethods.size() + removedMethods.size() < totalMethods.size()) {
			for(String method: totalMethods.keySet()) {
				if(!resultedMethods.containsKey(method) && !removedMethods.contains(method)) {
					String projectID = totalMethods.get(method);
					
					List<String> methods = missingMethods.get(projectID);
					if(methods == null) {
						methods = new ArrayList<>();
					}
					methods.add(method);
					missingMethods.put(projectID, methods);
				}
			}
		}
		
		System.out.println("resulted methods: " + resultedMethods.size());
		System.out.println("removed methods: " + removedMethods.size());
		System.out.println("total methods: " + totalMethods.size());
		
		writeToComplementaryFile(missingMethods);
	}

	private void writeToComplementaryFile(Map<String, List<String>> missingMethods) {
		String resultTxt = SFConfiguration.sfBenchmarkFolder 
				+ File.separator + "complementaryMethods.txt";
		for(String projectID: missingMethods.keySet()) {
			String projectName = projectID.substring(projectID.indexOf("_")+1, projectID.length());
        	StringBuilder sb = new StringBuilder()
					.append("#------------------------------------------------------------------------\n")
					.append("#Project=").append(projectName).append("  -  ").append(projectID).append("\n")
					.append("#------------------------------------------------------------------------\n");
			for (String method : missingMethods.get(projectID)) {
				sb.append(method).append("\n");
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
		
	}

	private Set<String> retrieveRemovedMethods(String summaryFile) {
		Set<String> removedMethods = new HashSet<>();
		
		File file = new File(summaryFile);
		ExcelReader reader = new ExcelReader(file, 0);
		List<List<Object>> bc = reader.listData(ResultAnalzyer.LONG_RUNNING);
		for(List<Object> row: bc) {
			String className = (String) row.get(0);
			String methodName = (String) row.get(1);
			String id = className + "#" + methodName;
			
			removedMethods.add(id);
		}
		
		return removedMethods;
	}

	private Map<String, String> retrieveTotalMethods(String targetMethodFile) {
		Map<String, String> totalMethods = new HashMap<>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					targetMethodFile));
			String line = reader.readLine();
			String projName = null;
			while (line != null) {
				// read next line
				line = reader.readLine();
				if(line!=null) {
					if(line.startsWith("#")) {
						if(!line.startsWith("#-")) {
							projName = line.substring(line.lastIndexOf(" ")+1, line.length());
							System.currentTimeMillis();
						}
					}
					else {
						totalMethods.put(line, projName);						
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return totalMethods;
	}

	private Map<String, Integer> retriveResultedMethods(String resultFile) {
		String[] sheets = new String[] {
				ComparativeResultMerger.BETTER_COVERAGE, 
				ComparativeResultMerger.BETTER_TIME,
				ComparativeResultMerger.EQUAL,
				ComparativeResultMerger.WORSE_COVERAGE,
				ComparativeResultMerger.WORSE_TIME};
		
		Map<String, Integer> map = new HashMap<>();
		
		File file = new File(resultFile);
		ExcelReader reader = new ExcelReader(file, 0);
		for(String sheet: sheets) {
			List<List<Object>> bc = reader.listData(sheet);
			for(List<Object> row: bc) {
				String className = (String) row.get(0);
				String methodName = (String) row.get(1);
				String id = className + "#" + methodName;
				
				Integer count = map.get(id);
				if(count == null) {
					count = 1;
				}
				count++;
				map.put(id, count);
			}
		}
		
		return map;
	}

}
