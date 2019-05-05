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

public class ActualStatisticsRecorder extends ExperimentRecorder {
	private Logger log = LoggerUtils.getLogger(ActualStatisticsRecorder.class);
	private ExcelWriter excelWriter;
	
	private static String[] header = new String[]{
			"Class", 
			"Method", 
			"Good Ratio",
			};
	
	public ActualStatisticsRecorder() {
		super();
		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_evotest_overall.xlsx"));
		excelWriter.getSheet("data", header, 0);
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
	
	public void recordRatio(String className, String methodName, double ratio) {
		List<Object> rowData = new ArrayList<Object>();
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(ratio);
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
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
