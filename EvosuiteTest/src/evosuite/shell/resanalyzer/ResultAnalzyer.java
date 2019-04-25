package evosuite.shell.resanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;

public class ResultAnalzyer {
	
	List<String> corruptedFiles = new ArrayList<>();
	
	public static void main(String[] args) {
		ResultAnalzyer rAna = new ResultAnalzyer();
		rAna.runAnalyzer(SFConfiguration.sfBenchmarkFolder + File.separator + "report-branch");
	}
	
	public void runAnalyzer(String reportRootFolder) {
		
		List<List<Object>> investigatedMethods = new ArrayList<>();
		List<List<Object>> longRunningTimeMethods = new ArrayList<>();
		List<List<Object>> easyMethods = new ArrayList<>();
		
		File reportRoot = new File(reportRootFolder);
		for(File f: reportRoot.listFiles()) {
			if(f.getName().endsWith("evotest.xlsx")) {
				ExcelReader reader = new ExcelReader(f, 0);
				List<List<Object>> datas = reader.listData("data");
				if(datas != null) {
					for(List<Object> data: datas) {
//						String className = (String) data.get(0);
//						String methodName = (String) data.get(1);
						
						Double coverage = (double) data.get(3);
						Double age = (double) data.get(4);
						if(coverage == 1) {
							easyMethods.add(data);
						}
						else {
							if(data.get(6)==null) {
								continue;
							}
							
							Double IPFCoverage = (double) data.get(6);
							if(coverage == 0 || age < 10) {
								longRunningTimeMethods.add(data);
							}
							else if(IPFCoverage == 1) {
								easyMethods.add(data);
							}
							else if(IPFCoverage < 1) {
								investigatedMethods.add(data);
							}
							
						}
					}
				}
			}
		}
		
		File writeFile = new File(reportRootFolder + File.separator + "summary.xlsx"); 
		ExcelWriter writer = new ExcelWriter(writeFile);
		
		try {
			writer.writeSheet("investigated", investigatedMethods);
			writer.writeSheet("easy", easyMethods);
			writer.writeSheet("longRunning", longRunningTimeMethods);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
