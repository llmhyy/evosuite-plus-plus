package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.evosuite.BranchDistributionInformation;
import org.evosuite.result.TestGenerationResult;
import org.mockito.internal.util.StringUtil;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

public class OneBranchRecorder extends ExperimentRecorder {
	
	private Logger log = LoggerUtils.getLogger(DistributionRecorder.class);
	private ExcelWriter oneBranchExcelWriter;

	public OneBranchRecorder() {
		super();
		oneBranchExcelWriter = new ExcelWriter(
				FileUtils.newFile(Settings.getReportFolder(), projectId + "_oneBranch.xlsx"));
		oneBranchExcelWriter.getSheet("branch",
				new String[] { "Class", "Method", "branchID", "covered", "distance", "time", "fitness" }, 0);
	}

	public OneBranchRecorder(String strategy) {
		super();
		oneBranchExcelWriter = new ExcelWriter(
				FileUtils.newFile(Settings.getReportFolder(), projectId + "_" + strategy + "_oneBranch.xlsx"));
		oneBranchExcelWriter.getSheet("branch",
				new String[] {"Class", "Method", "branchID", "covered", "distance", "time", "fitness" }, 0);

	}
	
	public void record(String className, String methodName, EvoTestResult result) {
		if (result == null) {
			return;
		}
		List<Object> oneBranchRowData = new ArrayList<>();
		
		List<BranchDistributionInformation> branches= result.getBranchInformation();
		for(BranchDistributionInformation branch : branches) {
			oneBranchRowData = new ArrayList<>();
			oneBranchRowData.add(className);
			oneBranchRowData.add(methodName);
			oneBranchRowData.add(branch.getBranchId());
			oneBranchRowData.add(branch.getCovered());
			oneBranchRowData.add(branch.getUnCoveredDistributionValue());
			oneBranchRowData.add(branch.getTime());
			oneBranchRowData.add(branch.getFitness());
			
			record(oneBranchRowData);
			
			
		}
		
		logSuccessfulMethods(className, methodName);
	
	}
	
	public void record(List<Object> branchRowData) {
		try {
			// System.out.println(timeRowData);
			oneBranchExcelWriter.writeSheet("branch", Arrays.asList(branchRowData));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFinalReportFilePath() {
		return oneBranchExcelWriter.getFile().getAbsolutePath();
	}

}
