package evosuite.shell.resanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;

public class ComparisonAnalyzer {
	public static void main(String[] args) {
		ComparisonAnalyzer cAna = new ComparisonAnalyzer();
		String branchSummaryAddress = SFConfiguration.sfBenchmarkFolder + File.separator + "summary.xlsx";
		String fbranchMaterialsAddress = SFConfiguration.sfBenchmarkFolder + File.separator + "analysis";
		cAna.runAnalyzer(branchSummaryAddress, fbranchMaterialsAddress);
	}

	private void runAnalyzer(String branchSummaryAddress, String fbranchMaterialsAddress) {
		Map<RecordItem, RecordItem> branchData = getBranchData(branchSummaryAddress);
		Map<RecordItem, RecordItem> fBranchData = getFBranchData(fbranchMaterialsAddress);
		
		List<List<Object>> worseCoverage = new ArrayList<>();
		List<List<Object>> worseTime = new ArrayList<>();
		List<List<Object>> equalTime = new ArrayList<>();
		List<List<Object>> goodOnes = new ArrayList<>();
		List<List<Object>> missingOnes = new ArrayList<>();
		List<List<Object>> smallAge = new ArrayList<>();
		
		for(RecordItem branchItem: branchData.keySet()) {
			RecordItem fBranchItem = fBranchData.get(branchItem);
			if(fBranchItem != null) {
				List<Object> item = toList(fBranchItem);
				item.add(branchItem.time);
				item.add(branchItem.coverage);
				item.add(branchItem.age);
				item.add(branchItem.IPFlag);
				if(fBranchItem.age < 10 && branchItem.age < 10) {
					smallAge.add(item);
				}
				else if(fBranchItem.IPFlag < branchItem.IPFlag) {
					worseCoverage.add(item);
				}
				else if(fBranchItem.IPFlag == branchItem.IPFlag) {
					if(fBranchItem.time >= 100 && branchItem.time >= 100 &&
							fBranchItem.coverage < 1 && branchItem.coverage < 1) {
						equalTime.add(item);
					}
					else {
						if(fBranchItem.time <= branchItem.time) {
							goodOnes.add(item);
						}
						else {
							worseTime.add(item);
						}
					}
				}
				else {
					goodOnes.add(item);
				}
			}
			else {
				List<Object> item = toList(branchItem);
				missingOnes.add(item);
			}
		}
		
		
		File writeFile = new File(fbranchMaterialsAddress + File.separator + "compare.xlsx"); 
		ExcelWriter writer = new ExcelWriter(writeFile);
		
		try {
			writer.getSheet("worseCoverage", header, 0);
			writer.writeSheet("worseCoverage", worseCoverage);
			
			writer.getSheet("worseTime", header, 0);
			writer.writeSheet("worseTime", worseTime);
			
			writer.getSheet("goodOnes", header, 0);
			writer.writeSheet("goodOnes", goodOnes);
			
			writer.getSheet("missingOnes", header, 0);
			writer.writeSheet("missingOnes", missingOnes);
			
			writer.getSheet("equalTime", header, 0);
			writer.writeSheet("equalTime", equalTime);
			
			writer.getSheet("smallAge", header, 0);
			writer.writeSheet("smallAge", smallAge);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	String[] header = new String[]{
			"Class", 
			"Method", 
			"Execution Time", 
			"Coverage", 
			"Age", 
			"Call Availability", 
			"IP Flag Coverage",
			"Uncovered IF Flag",
			"Random Seed",
			"Unavailable Call"
			};

	private List<Object> toList(RecordItem fBranchItem) {
		List<Object> oList = new ArrayList<>();
		oList.add(fBranchItem.className);
		oList.add(fBranchItem.methodName);
		oList.add(fBranchItem.time);
		oList.add(fBranchItem.coverage);
		oList.add(fBranchItem.age);
		oList.add(fBranchItem.callAvailability);
		oList.add(fBranchItem.IPFlag);
		oList.add(fBranchItem.uncoveredFlags);
		oList.add(fBranchItem.randomSeed);
		oList.add(fBranchItem.unavaiableCalls);
		return oList;
	}

	private Map<RecordItem, RecordItem> getFBranchData(String fbranchMaterialsAddress) {
		Map<RecordItem, RecordItem> fBranhData = new HashMap<>();
		
		File reportRoot = new File(fbranchMaterialsAddress);
		for(File f: reportRoot.listFiles()) {
			if(f.getName().endsWith("evotest.xlsx")) {
				ExcelReader reader = new ExcelReader(f, 0);
				List<List<Object>> datas = reader.listData("data");
				if(datas != null) {
					for(List<Object> data: datas) {
						RecordItem item = convertToItem(data);
						fBranhData.put(item, item);
					}
				}
			}
		}
		
		return fBranhData;
	}

	private RecordItem convertToItem(List<Object> data) {
		String unavailableCalls = null;
		if(data.size() > 9) {
			unavailableCalls = (String)data.get(9);
		}
		
		return new RecordItem(
				(String)data.get(0), 
				(String)data.get(1), 
				((Double)data.get(2)).intValue(), 
				(Double)data.get(3), 
				((Double)data.get(4)).intValue(), 
				(Double)data.get(5), 
				((Double)data.get(6)).intValue(), 
				(String)data.get(7), 
				((Double)data.get(8)).longValue(),
				unavailableCalls);
	}

	private Map<RecordItem, RecordItem> getBranchData(String branchSummaryAddress) {
		Map<RecordItem, RecordItem> branchData = new HashMap<>();
		File summary = new File(branchSummaryAddress);
		if(summary.exists()) {
			ExcelReader reader = new ExcelReader(summary, 0);
			List<List<Object>> datas = reader.listData("easy");
			if(datas != null) {
				for(List<Object> data: datas) {
					RecordItem item = convertToItem(data);
					branchData.put(item, item);
				}
			}
		}
		return branchData;
	}

	
}
