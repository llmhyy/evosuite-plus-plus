package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.evosuite.result.TestGenerationResult;
import org.mockito.internal.util.StringUtil;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

public class IterDistributionRecorder extends DistributionRecorder {

	private Logger log = LoggerUtils.getLogger(DistributionRecorder.class);
	private ExcelWriter iterdistributionExcelWriter;

	public IterDistributionRecorder() {
		super();
		iterdistributionExcelWriter = new ExcelWriter(
				FileUtils.newFile(Settings.getReportFolder(), projectId + "_distribution_stat.xlsx"));
		iterdistributionExcelWriter.getSheet("data",
				new String[] { "Class", "Method", "averagedistance", "time", "coverage" }, 0);
		allresults = new ArrayList<TestGenerationResult>();

	}

	public IterDistributionRecorder(String strategy) {
		super(strategy);
		iterdistributionExcelWriter = new ExcelWriter(
				FileUtils.newFile(Settings.getReportFolder(), projectId + "_" + strategy + "_distribution_stat.xlsx"));
		iterdistributionExcelWriter.getSheet("data",
				new String[] { "Class", "Method", "averagedistance", "time", "coverage" }, 0);
		allresults = new ArrayList<TestGenerationResult>();

	}

	@Override
	public void record(String className, String methodName, TestGenerationResult r) {
		super.record(className, methodName, r);
		allresults.add(r);
	}

	@Override
	public void recordEndIterations(String methodName, String className) {
		try {
			List<Object> iterationRowData = new ArrayList<>();
			iterationRowData.add(className);
			iterationRowData.add(methodName);
			double avedistance = 0.0;
			double avetime = 0.0;
			double avecoverage = 0.0;
			if (distances.size() >= 1) {
				for (int i = 1; i <= distances.size(); i++) {
					avedistance = avedistance + distances.get(i - 1);
				}
				avedistance = avedistance / distances.size();
			} else {
				avedistance = 0;
			}
			if (allresults.size() >= 1) {
				for (int i = 1; i <= allresults.size(); i++) {
					avetime = avetime + allresults.get(i - 1).getElapseTime();
					avecoverage = avecoverage + allresults.get(i - 1).getCoverage();
				}
				avetime = avetime / (allresults.size());
				avecoverage = avecoverage / (allresults.size());
				iterationRowData.add(avedistance);
				iterationRowData.add(avetime);
				iterationRowData.add(avecoverage);
				iterdistributionExcelWriter.writeSheet("data", Arrays.asList(iterationRowData));
			} else {
				avetime = 0;
				avecoverage = 0;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.distances = new ArrayList<Double>();
		this.allresults = new ArrayList<TestGenerationResult>();

	}

	public String getFinalReportFilePath() {
		return iterdistributionExcelWriter.getFile().getAbsolutePath();
	}

}
