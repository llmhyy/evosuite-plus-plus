package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.evosuite.result.BranchInfo;
import org.evosuite.result.seedexpr.Event;
import org.slf4j.Logger;

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
	private BufferedWriter csvWtriter;

	public FitnessEffectiveRecorder() throws IOException {
		super();
		
		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_evotest.xlsx"));
		excelWriter.getSheet("data",
				new String[] { "Class", "Method", "Execution Time", "Coverage", "Age", "Call Availability",
						"IP Flag Coverage", "Uncovered IF Flag", "Random Seed", "Unavailable Call", "Initial Coverage",
						"Initialization Overhead", "Missing Branches" },
				0);
		
		//csv
		File csvFile = null;
		csvFile = new File(Settings.getReportFolder().toString() + '\\'+projectId + "_evotest.csv");
		csvFile.createNewFile();
		if (csvFile.isFile()) {
		    // create BufferedReader
			csvWtriter = new BufferedWriter(new FileWriter(csvFile));
			Object[] head = {"Class", "Method", "Execution Time", "Coverage", "Age", "Call Availability",
					"IP Flag Coverage", "Uncovered IF Flag", "Random Seed", "Unavailable Call", "Initial Coverage",
					"Initialization Overhead","CoveredBranchWithTest", "Missing Branches","Missing InstructID","Events"};
		    List<Object> headList = Arrays.asList(head);
			writeRow(headList, csvWtriter);
//			System.out.println("csvFile：" + csvFile);
//			System.out.println("csvFileName：" + csvFile.getName());
		}
	    
	    
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

		StringBuffer sb = new StringBuffer();
		if (r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
			for (BranchInfo b : r.getMissingBranches()) {
				sb.append(b.toString() + "\\n");
			}
		}
		String missingBranches = sb.toString();
		if (missingBranches.isEmpty()) {
			missingBranches = "NA";
		}
		rowData.add(missingBranches);
		
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
	public void recordSeeding(String className, String methodName, EvoTestResult r) throws IOException {
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
//		StringBuffer ss = new StringBuffer();
//		if (r.getCoveredBranchWithTest() != null && !r.getCoveredBranchWithTest().isEmpty()) {
//			for (BranchInfo b: r.getCoveredBranchWithTest().keySet()) {
//				ss.append(r.getCoveredBranchWithTest().get(b) + "\\n");
//			}
//		}
//		String coveredBranch = ss.toString();
//		if (coveredBranch.isEmpty()) {
//			coveredBranch = "NA";
//		}
//		rowData.add(coveredBranch);

		StringBuffer sb = new StringBuffer();
		if (r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
			for (BranchInfo b : r.getMissingBranches()) {
				sb.append(b.toString() + "\\n");
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
		
		StringBuffer se = new StringBuffer();
		if (r.getEventSequence() != null && !r.getEventSequence().isEmpty()) {
			for (Event e: r.getEventSequence()) {
				se.append(e.toString() + ",");
			}
		}
		String events = se.toString();
		if (events.isEmpty()) {
			events = "NA";
		}
		rowData.add(events);
		
		csvWtriter.flush();
		

		try {
	        writeRow(rowData, csvWtriter);
//			logSuccessfulMethods(className, methodName);
//			csvWtriter.close();
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
	
	private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
	      for (Object data : row) {
	           StringBuffer sb = new StringBuffer();
	           String rowStr = sb.append("\"").append(data).append("\",").toString();
	           csvWriter.write(rowStr);
	        }
	        csvWriter.newLine();
	    }
}
