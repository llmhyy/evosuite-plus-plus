package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class ComparativeRecorder extends ExperimentRecorder {
	private Logger log = LoggerUtils.getLogger(ComparativeRecorder.class);
	private ExcelWriter excelWriter;
	
	public static String GOOD_COVERAGE = "good coverage";
	public static String GOOD_TIME = "good time";
	public static String EQUAL = "equal";
	public static String WORSE_COVERAGE = "worse coverage";
	public static String WOSE_TIME = "worse time";
	
	public static String[] header = new String[]{
			"ProjectID",
			"Class", 
			"Method", 
			"Execution Time (F)", 
			"Execution Time (B)", 
			"Coverage (F)", 
			"Coverage (B)",
			"IP Flag Coverage (F)",
			"IP Flag Coverage (B)",
			"Age (F)", 
			"Age (B)", 
			"Uncovered IF Flag (F)",
			"Uncovered IF Flag (B)",
			"Call Availability (F)", 
			"Unavailable Call (F)"
			};
	
	public ComparativeRecorder() {
		super();
		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_evotest_compare.xlsx"));
		excelWriter.getSheet(GOOD_COVERAGE, header, 0);
		excelWriter.getSheet(GOOD_TIME, header, 0);
		excelWriter.getSheet(EQUAL, header, 0);
		excelWriter.getSheet(WORSE_COVERAGE, header, 0);
		excelWriter.getSheet(WOSE_TIME, header, 0);
	}

	
	public String getUnaviableCall(Map<String, Boolean> map) {
		StringBuffer buffer = new StringBuffer();
		for(String call: map.keySet()) {
			if(!map.get(call)) {
				buffer.append(call + "\n");				
			}
		}
		
		return buffer.toString();
	}
	
	public void recordBothResults(String className, String methodName, EvoTestResult bResult, EvoTestResult fResult, String sheet) {
		List<Object> rowData = new ArrayList<Object>();
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(bResult.getRandomSeed());
		rowData.add(fResult.getTime());
		rowData.add(bResult.getTime());
		rowData.add(fResult.getCoverage());
		rowData.add(bResult.getCoverage());
		rowData.add(fResult.getIPFlagCoverage());
		rowData.add(bResult.getIPFlagCoverage());
		rowData.add(fResult.getAge());
		rowData.add(bResult.getAge());
		rowData.add(fResult.getUncoveredFlags());
		rowData.add(bResult.getUncoveredFlags());
		rowData.add(fResult.getRatio());
		String unavailableString = getUnaviableCall(bResult.getMethodCallAvailability());
		rowData.add(unavailableString);
		try {
			excelWriter.writeSheet(sheet, Arrays.asList(rowData));
			logSuccessfulMethods(className, methodName);
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
	
	@Override
	public void recordError(String className, String methodName, Exception e) {
		List<Object> rowData = new ArrayList<Object>();
		rowData.add(className);
		rowData.add(methodName);
		rowData.add("Error" + e.getMessage());
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
		} catch (IOException ex) {
			log.error("Error", ex);
		}
		
	}
	
	public String getFinalReportFilePath() {
		return excelWriter.getFile().getAbsolutePath();
	}
}
