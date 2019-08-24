package evosuite.shell.resanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import evosuite.shell.ComparativeRecorder;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.resanalyzer.ComparativeResultMerger.Record;

public class NewComparativeResultMerger {
//	public static String folderName = "new-result3";
	public static String folderName = "Aug24 3pm results";
	
	public static void main(String[] args) {
		NewComparativeResultMerger merger = new NewComparativeResultMerger();

		// String branchSummaryAddress = SFConfiguration.sfBenchmarkFolder +
		// File.separator + "summary.xlsx";
		String fbranchMaterialsAddress = SFConfiguration.sfBenchmarkFolder + File.separator + folderName;
		merger.runAnalyzer(fbranchMaterialsAddress);

	}

	public static final String BETTER_COVERAGE = "good coverage";
	public static final String BETTER_TIME = "good time";
	public static final String EQUAL = "equal";
	public static final String WORSE_COVERAGE = "worse coverage";
	public static final String WORSE_TIME = "worse time";
	public static final String ALL = "all";

	private ExcelWriter excelWriter;

	public NewComparativeResultMerger() {
		excelWriter = new ExcelWriter(new File(SFConfiguration.sfBenchmarkFolder + File.separator + folderName
				+ File.separator + "overall_compare.xlsx"));
		excelWriter.getSheet(BETTER_COVERAGE, ComparativeRecorder.header, 0);
		excelWriter.getSheet(BETTER_TIME, ComparativeRecorder.header, 0);
		excelWriter.getSheet(EQUAL, ComparativeRecorder.header, 0);
		excelWriter.getSheet(WORSE_COVERAGE, ComparativeRecorder.header, 0);
		excelWriter.getSheet(WORSE_TIME, ComparativeRecorder.header, 0);
		excelWriter.getSheet(ALL, ComparativeRecorder.header, 0);
	}

	private void runAnalyzer(String fbranchMaterialsAddress) {
		File root = new File(fbranchMaterialsAddress);

		if (!root.exists()) {
			System.err.println(root + " does not exsit");
			return;
		}

		Map<String, Map<String, RecordItem>> branchRecord = new HashMap<>();
		Map<String, Map<String, RecordItem>> fbranchRecord = new HashMap<>();
		
		for (File file : root.listFiles()) {
			if(file.exists() && file.isDirectory()) {
				if(file.getName().endsWith("-branch")) {
					aggregateRecord(file, branchRecord);
				}
				else if(file.getName().endsWith("-fbranch")) {
					aggregateRecord(file, fbranchRecord);
				}
			}
		}
		
		Map<String, List<CompareResult>> results = compare(branchRecord, fbranchRecord);
		
		try {
			excelWriter.writeSheet(BETTER_COVERAGE, transferSetToList(results.get(BETTER_COVERAGE)));
			excelWriter.writeSheet(BETTER_TIME, transferSetToList(results.get(BETTER_TIME)));
			excelWriter.writeSheet(EQUAL, transferSetToList(results.get(EQUAL)));
			excelWriter.writeSheet(WORSE_TIME, transferSetToList(results.get(WORSE_TIME)));
			excelWriter.writeSheet(WORSE_COVERAGE, transferSetToList(results.get(WORSE_COVERAGE)));
			excelWriter.writeSheet(ALL, transferSetToList(results));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private List<List<Object>> transferSetToList(Map<String, List<CompareResult>> results) {
		List<List<Object>> items = new ArrayList<>();
		
		for(String type: results.keySet()) {
			for(CompareResult record: results.get(type)) {
				List<Object> item = new ArrayList<>();
				item.add(record.projectID);
				item.add(record.className);
				item.add(record.methodName);
				item.add(record.timeF);
				item.add(record.timeB);
				item.add(record.coverageF);
				item.add(record.coverageB);
				item.add(record.IPConverageF);
				item.add(record.IPConverageB);
				item.add(record.ageF);
				item.add(record.ageB);
				item.add(record.uncoveredIPF);
				items.add(item);
			}
		}
		
		return items;
	}

	/**
	 * prj => methodID => coverage information
	 * @param branchRecord
	 * @param fbranchRecord
	 * @return
	 */
	private Map<String, List<CompareResult>> compare(Map<String, Map<String, RecordItem>> branchRecord,
			Map<String, Map<String, RecordItem>> fbranchRecord) {
		
		Map<String, List<CompareResult>> result = new HashMap<String, List<CompareResult>>();
		for(String proj: branchRecord.keySet()) {
			Map<String, RecordItem> averageRecord = branchRecord.get(proj);
			for(String methodID: averageRecord.keySet()) {
				RecordItem branchItem = averageRecord.get(methodID);
				RecordItem fbranchItem = null;
				try {
					fbranchItem = fbranchRecord.get(proj).get(methodID);					
				}
				catch(Exception e) {
					System.currentTimeMillis();
				}
				
				if(fbranchItem != null) {
					CompareResult cResult = new CompareResult(proj, branchItem.className, branchItem.methodName, 
							fbranchItem.time, branchItem.time, 
							fbranchItem.coverage, branchItem.coverage, 
							fbranchItem.IPFlag, branchItem.IPFlag, 
							fbranchItem.age, branchItem.age, 
							fbranchItem.uncoveredFlags);
					
					if(cResult.isGoodCoverage()) {
						addCompareResult(BETTER_COVERAGE, cResult, result);
					}
					else if(cResult.isGoodTime()) {
						addCompareResult(BETTER_TIME, cResult, result);
					}
					else if(cResult.isWorseCoverage()) {
						addCompareResult(WORSE_COVERAGE, cResult, result);
					}
					else if(cResult.isWorseTime()) {
						addCompareResult(WORSE_TIME, cResult, result);
					}
					else {
						addCompareResult(EQUAL, cResult, result);
					}
				}
			}
		}
		
		return result;
	}

	private void addCompareResult(String type, CompareResult cResult, Map<String, List<CompareResult>> result) {
		List<CompareResult> list = result.get(type);
		if(list == null) {
			list = new ArrayList<CompareResult>();
		}
		
		list.add(cResult);
		result.put(type, list);
	}

	private List<List<Object>> transferSetToList(List<CompareResult> list) {
		List<List<Object>> items = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (CompareResult record : list) {
				List<Object> item = new ArrayList<>();
				item.add(record.projectID);
				item.add(record.className);
				item.add(record.methodName);
				item.add(record.timeF);
				item.add(record.timeB);
				item.add(record.coverageF);
				item.add(record.coverageB);
				item.add(record.IPConverageF);
				item.add(record.IPConverageB);
				item.add(record.ageF);
				item.add(record.ageB);
				item.add(record.uncoveredIPF);
				items.add(item);
			}
		}
		return items;
	}

	/**
	 * branchRecord: projectID -> methodID -> record
	 * @param file
	 * @param branchRecord
	 * @param projID
	 */
	private void aggregateRecord(File file, Map<String, Map<String, RecordItem>> branchRecord) {
		for(File subFile: file.listFiles()) {
			String subFileName = subFile.getName();
			if(subFileName.endsWith("_evotest.xlsx") && !subFileName.contains("~")) {
				String projectName = subFileName.substring(0, subFileName.indexOf("_evotest.xlsx"));
				ExcelReader reader = new ExcelReader(subFile, 0);
				List<List<Object>> bc = reader.listData("data");
				
				RecordItem prevItem = null;
				List<RecordItem> list = new ArrayList<RecordItem>();
				for(List<Object> obj: bc) {
					RecordItem item = convertToItem(obj);
					if(item == null) continue;
					
					if(prevItem == null || (prevItem.equals(item))) {
						list.add(item);
					}
					else {
						RecordItem averageItem = deriveAverage(list);
						Map<String, RecordItem> averageRecord = branchRecord.get(projectName);
						if(averageRecord == null) {
							averageRecord = new HashMap<String, RecordItem>();
							branchRecord.put(projectName, averageRecord);
						}
						
						String methodID = averageItem.className + "#" + averageItem.methodName;
						averageRecord.put(methodID, averageItem);
						
						list.clear();
						list.add(item);
					}
					
					prevItem = item;
					
				}
			}
		}
		
	}
	
	private RecordItem deriveAverage(List<RecordItem> list) {
		if(list.isEmpty()) {
			return null;
		}
		
		RecordItem average = list.get(0).clone();
		for(int i=0; i<list.size(); i++) {
			if(i != 0) {
				RecordItem item = list.get(i);
				average.time += item.time;
				average.coverage += item.coverage;
				average.age += item.age;
				average.IPFlag += item.IPFlag;
			}
		}
		
		System.currentTimeMillis();
		
		double size = list.size();
		average.time /= size;
		average.coverage /= size;
		average.age /= size;
		average.IPFlag /= size;
		
		return average;
	}

	private RecordItem convertToItem(List<Object> data) {
		String unavailableCalls = null;
		if(data.size() > 9) {
			unavailableCalls = (String)data.get(9);
		}
		
		RecordItem item = null;
		
		try {
			item = new RecordItem(
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
		catch(Exception e) {
			
		}
		
		
		return item;
	}
	

}
