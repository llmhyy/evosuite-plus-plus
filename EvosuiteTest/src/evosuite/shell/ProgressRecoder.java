package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.evosuite.result.TestGenerationResult;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

public class ProgressRecoder extends ExperimentRecorder {
	private Logger log = LoggerUtils.getLogger(ProgressRecoder.class);
	private ExcelWriter distributionExcelWriter;
	private ExcelWriter progressExcelWriter;
	
	public ProgressRecoder() {
		super();
		distributionExcelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_distribution.xlsx"));
		distributionExcelWriter.getSheet("data", new String[] {"Class", "Method", ""}, 0);
		progressExcelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_progress.xlsx"));
		progressExcelWriter.getSheet("data", new String[] {"Class", "Method", ""}, 0);
	}
	
	@Override
	public void record(String className, String methodName, EvoTestResult result) {
		super.record(className, methodName, result);
	}
	
	
	@Override
	public void record(String className, String methodName, TestGenerationResult r) {
		if (r.getDistribution() == null || r.getDistribution().length == 0) {
			return;
		}
		log.info("" +r.getProgressInformation());
		for(int i=0; i<r.getDistribution().length; i++){
			log.info("" +r.getDistribution()[i]);					
		}	
		List<Object> progressRowData = new ArrayList<>();
		progressRowData.add(className);
		progressRowData.add(methodName);
		progressRowData.addAll(r.getProgressInformation());
		List<Object> distributionRowData = new ArrayList<>();
		distributionRowData.add(className);
		distributionRowData.add(methodName);
		for (int distr : r.getDistribution()) {
			distributionRowData.add(distr);
		}
		record(progressRowData, distributionRowData);
		logSuccessfulMethods(className, methodName);
	}
	
	public void record(List<Object> progressRowData, List<Object> distributionRowData) {
		try {
			progressExcelWriter.writeSheet("data", Arrays.asList(progressRowData));
			distributionExcelWriter.writeSheet("data", Arrays.asList(distributionRowData));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
