package evosuite.shell.resanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.experiment.SFConfiguration;

public class ResultAnalzyer2 {
	
	List<String> corruptedFiles = new ArrayList<>();
	
	static String folder = SFConfiguration.sfBenchmarkFolder + File.separator + "new-result3";
	static String[] targetFiles = new String[] {
			folder + File.separator + "t1.txt",
			folder + File.separator + "t2.txt"
	};
	static String excelName = folder + File.separator + "overall_compare.xlsx";
	
	public static void main(String[] args) {
		ResultAnalzyer2 rAna = new ResultAnalzyer2();
		try {
			rAna.runAnalyzer(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, List<String>> getMapFromSheet(String fileName, String sheetName){
		File f = new File(fileName);
		ExcelReader reader = new ExcelReader(f, 0);
		List<List<Object>> datas = reader.listData(sheetName);
		Map<String, List<String>> map = new HashMap<>();
		if(datas != null) {
			for(List<Object> data: datas) {
				String prj = (String) data.get(0);
				String className = (String) data.get(1);
				String methodName = (String) data.get(2);
				
				List<String> list = map.get(prj);
				if(list == null) {
					list = new ArrayList<String>();
					map.put(prj, list);
				}
				
				String methodID = className + "#" + methodName;
				if(!list.contains(methodID)) {
					list.add(methodID);
				}
			}
		}
		
		return map;
	}
	
	public int count(Map<String, List<String>> totalRunMethods) {
		int count = 0;
		for(String key: totalRunMethods.keySet()) {
			count += totalRunMethods.get(key).size();
		}
		
		return count;
	}
	
	public void runAnalyzer(String reportRootFolder) throws IOException {
		
		Map<String, List<String>> totalMethods = getTotalMethods(targetFiles);
		System.out.println("total methods: " + count(totalMethods));
		
		Map<String, List<String>> worseCoverageMethod = getMapFromSheet(excelName, "worse coverage");
		System.out.println("total worse methods: " + count(worseCoverageMethod));
		
		Map<String, List<String>> totalRunMethods = getMapFromSheet(excelName, "all");
		System.out.println("total runned methods: " + count(totalRunMethods));
		
		Map<String, List<String>> rerunMap = generateRerunMap(totalRunMethods, worseCoverageMethod, totalMethods);
		System.out.println("total methods: " + count(rerunMap));
		
		int size = 0;
		List<Map<String, List<String>>> list = new ArrayList<Map<String,List<String>>>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for(String key: rerunMap.keySet()) {
			map.put(key, rerunMap.get(key));
			size += rerunMap.get(key).size();
			if(size > 20) {
				list.add(map);
				size = 0;
				map = new HashMap<String, List<String>>();
			}
			
		}
		
		for(int i=1; i<=list.size(); i++) {
			writeToComplementaryFile(list.get(i-1), i);			
		}
		
	}
	
	
	private Map<String, List<String>> generateRerunMap(Map<String, List<String>> totalRunMethods,
			Map<String, List<String>> worseCoverageMethod, Map<String, List<String>> totalMethods) {
		Map<String, List<String>> rerunMap = new HashMap<String, List<String>>();
		
		for(String prj: totalMethods.keySet()) {
			List<String> allMethods = totalMethods.get(prj);
			
			List<String> rerunMethods = new ArrayList<String>();
			if(!allMethods.isEmpty()) {
				List<String> worseCoverageList = worseCoverageMethod.get(prj);
				List<String> runMethodList = totalRunMethods.get(prj);
				if(runMethodList==null) {
					rerunMethods = allMethods;
				}
				else {
					for(String method: allMethods) {
						if(worseCoverageList!=null && worseCoverageList.contains(method)) {
							rerunMethods.add(method);
						}
						else if(!runMethodList.contains(method)) {
							rerunMethods.add(method);
						}
						
					}
					
				}
				
				rerunMap.put(prj, rerunMethods);
			}
			
			
		}
		return rerunMap;
	}

	private Map<String, List<String>> getTotalMethods(String[] txtList) throws IOException {
		Map<String, List<String>> map = new HashMap<>();
		
		for(String txt: txtList) {
			File file = new File(txt);
			if (file.exists()) {
				FileReader fr = new FileReader(txt);
				BufferedReader br = new BufferedReader(fr);
				
				String currentPrj = null;
//				List<String> methodList = new ArrayList<String>();
				String str;
				while ((str = br.readLine()) != null) {
					if(str.startsWith("#Project")) {
						currentPrj = str.substring(str.indexOf("-   ")+4, str.length());
					}
					
					if(!str.startsWith("#")) {
						List<String> methodList = map.get(currentPrj);
						if(methodList == null) {
							methodList = new ArrayList<String>();
							map.put(currentPrj, methodList);
						}
						
						if(!methodList.contains(str)) {
							methodList.add(str);							
						}
					}
					
				}
				
				br.close();
				fr.close();
			}
			
		}
		
		
		return map;
	}

	private void writeToComplementaryFile(Map<String, List<String>> methods, int i) {
		String resultTxt = SFConfiguration.sfBenchmarkFolder  + File.separator + "new-result3"
				+ File.separator + i + ".txt";
		for(String projectID: methods.keySet()) {
			String projectName = projectID.substring(projectID.indexOf("_")+1, projectID.length());
        	StringBuilder sb = new StringBuilder()
					.append("#------------------------------------------------------------------------\n")
					.append("#Project=").append(projectName).append("  -  ").append(projectID).append("\n")
					.append("#------------------------------------------------------------------------\n");
			for (String method : methods.get(projectID)) {
				sb.append(method).append("\n");
			}
			evosuite.shell.FileUtils.writeFile(resultTxt, sb.toString(), true);
		}
		
	}	
}
