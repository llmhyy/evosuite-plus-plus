package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.result.BranchInfo;
import org.evosuite.result.seedexpr.BranchCoveringEvent;
import org.evosuite.result.seedexpr.Event;
import org.evosuite.statistics.logToExcel;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

/**
 * 
 * @author lyly Generate report with "Execution Time", "Coverage", "Age"
 *         information
 */
public class FitnessEffectiveRecorder extends ExperimentRecorder {
	private Logger log = LoggerUtils.getLogger(FitnessEffectiveRecorder.class);
	private ExcelWriter excelWriter;
	private ExcelWriter evoSeedWriter;
	private OutputStreamWriter jsonWriter;

	public FitnessEffectiveRecorder() throws IOException {
		super();
		
		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_evotest.xlsx"));
		excelWriter.getSheet("data",
				new String[] { "Class", "Method", "Execution Time", "Coverage", "Age", "Call Availability",
						"IP Flag Coverage", "Uncovered IF Flag", "Random Seed", "Unavailable Call", "Initial Coverage",
						"Initialization Overhead", "CoveredBranchWithTest","Missing Branches","Missing InstructID"},
				0);
		
		evoSeedWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), "evoseedType.xlsx"));
		evoSeedWriter.getSheet("data",
				new String[] { "PID","Class", "Method", "Branch-Type", "Num"},
				0);
			    
		//json
//		File jsonFile = null;
//		jsonFile = new File(Settings.getReportFolder().toString() + '\\'+projectId + "_evotest.json");
//		jsonFile.createNewFile();
//		if (jsonFile.isFile()) {
//		    // create jsonWriter
//			System.out.println("file:" + jsonFile);
//			jsonWriter = new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8");
//		}
	    
	}

	public String getUnaviableCall(Map<String, Boolean> map) {
		StringBuffer buffer = new StringBuffer();
		for (String call : map.keySet()) {
			if (!map.get(call)) {
				buffer.append(call + "\n");
			}
		}

		return buffer.toString();
	}

	@Override
	public void record(String className, String methodName, EvoTestResult r) {
		List<Object> rowData = new ArrayList<Object>();
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(r.getTime());
		rowData.add(r.getCoverage());
		rowData.add(r.getAge());
		rowData.add(r.getRatio());
		rowData.add(r.getIPFlagCoverage());
		rowData.add(r.getUncoveredFlags());
		rowData.add(r.getRandomSeed());
		String unavailableString = getUnaviableCall(r.getMethodCallAvailability());
		rowData.add(unavailableString);
		rowData.add(r.getInitialCoverage());
		rowData.add(r.getInitializationOverhead());
		rowData.add(r.getCoveredBranchWithTest().toString());

		StringBuffer sb = new StringBuffer();
		if (r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
			for (BranchInfo b : r.getMissingBranches()) {
				sb.append(b.toString() + b.getStringValue() + ":" + b.getTruthValue() + "\\n");
			}
		}
		String missingBranches = sb.toString();
		if (missingBranches.isEmpty()) {
			missingBranches = "NA";
		}
		rowData.add(missingBranches);
		
		StringBuffer si = new StringBuffer();
		if (r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
			for (BranchInfo b : r.getMissingBranches()) {
				si.append(b.getStringValue() + ":" + b.getTruthValue() + "\\n");
			}
		}
		String missingInstructID = si.toString();
		if (missingInstructID.isEmpty()) {
			missingInstructID = "NA";
		}
		rowData.add(missingInstructID);
		
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
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

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			StackTraceElement ste = e.getStackTrace()[i];
			String s = "class Name: " + ste.getClassName() + ", line number:  " + ste.getLineNumber() + "\n";
			sb.append(s);
		}

		rowData.add("Error" + sb.toString());
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
		} catch (IOException ex) {
			log.error("Error", ex);
		}

	}

	public String getFinalReportFilePath() {
		return excelWriter.getFile().getAbsolutePath();
	}
	
	@Override
	public void recordSeedingToJson(String className, String methodName, EvoTestResult r) throws IOException {
		List<Object> rowData = new ArrayList<Object>();		
		rowData.add("Events");
		if (r.getEventSequence() != null && !r.getEventSequence().isEmpty()) {
			for (Event e: r.getEventSequence()) {
				Map<String, Object> eventMap = new HashMap<>();
				if(e.toString().contains("branchCovering")) {
					BranchCoveringEvent bc = (BranchCoveringEvent)e;
					BranchInfo b = bc.getBranch();
					String testcase = bc.getNewCode();
					eventMap.put("Type",e.toString());
					eventMap.put("BranchInfo",b);
					eventMap.put("Testcode",testcase);
					rowData.add(eventMap);
				}else
				{
					eventMap.put("Type",e.toString());
					eventMap.put("DataType",e.getDataType());
					eventMap.put("oldValue",e.getOldValue());
					eventMap.put("value",e.getValue());
					rowData.add(eventMap);
				}
					
							
			}
		}
		rowData.add("EndSeed");
		String json = JSON.toJSONString(rowData,true);

		try {
			jsonWriter.flush();
			jsonWriter.write(json);
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
	
	@Override
	public void recordEvoSeedTime(String className, String methodName, EvoTestResult r) {
		List<Object> rowData = new ArrayList<Object>();
		rowData.add(projectId);
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(r.getRuntimeBranchType().toString());
		rowData.add(r.getSmartBranchNum());
//		rowData.add(r.getAge());
//		rowData.add(r.getInitialCoverage());
//		rowData.add(r.getInitializationOverhead());
//		rowData.add(r.getBranchNum());
//		rowData.add(r.getPathNum());
//		rowData.add(r.getSmartSeedAnalyzeTime());
//		rowData.add(r.getGetFirstTailValueTime());
//		rowData.add(r.getAll10MutateTime());
//		rowData.add(r.getCascadeAnalysisTime());
//		rowData.add(r.getEvolveTime());
//		rowData.add(r.getParent1EvolveTime());
//		rowData.add(r.getParent2EvolveTime());
//		rowData.add(r.getRandomTestcaseTime());
//		rowData.add(r.getGenerateTime());
		
		try {
			evoSeedWriter.writeSheet("data", Arrays.asList(rowData));
			logSuccessfulMethods(className, methodName);
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
	
	@Override
	public void recordCoverageOnDiffTime() {
		ExcelWriter excelWriter = new ExcelWriter(evosuite.shell.FileUtils.newFile(Settings.getReportFolder(), "coverageOnDiffTime.xlsx"));
		List<String> header = new ArrayList<>();
		header.add("PID");
		header.add("Class");
		header.add("Method");
		header.add("Execution Time");
		header.add("Coverage");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
		
		try {
			excelWriter.writeSheet("data", logToExcel.data);
			logToExcel.clear();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
}
