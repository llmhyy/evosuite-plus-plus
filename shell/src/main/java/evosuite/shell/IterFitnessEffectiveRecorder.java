package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.result.BranchInfo;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class IterFitnessEffectiveRecorder extends FitnessEffectiveRecorder {
	private String currentMethod;
	private List<EvoTestResult> currentResult = new ArrayList<>();

	private Logger log = LoggerUtils.getLogger(FitnessEffectiveRecorder.class);
	private ExcelWriter excelWriter;
	private int iterator;

	public IterFitnessEffectiveRecorder(int iterator) throws IOException {
		super();
		this.iterator = iterator;
		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), new StringBuilder()
				.append(projectId).append("_evotest_").append(iterator).append("times.xlsx").toString()));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		for (int i = 1; i <= iterator; i++) {
			header.add("Execution Time -r" + i);
			header.add("Coverage -r" + i);
			header.add("Age -r" + i);
			header.add("Initial Coverage -r" + i);
			header.add("Initialization Overhead -r" + i);
			header.add("Missing Branches -r" + i);
		}
		header.add("Method Availability");
		header.add("Avg Execution Time");
		header.add("Avg Coverage");
		header.add("Best Coverage");
		header.add("Age of Best Coverage");
		header.add("Avg Initial Coverage");
		header.add("Avg Initialization Overhead");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
	}

	@Override
	public void record(String className, String methodName, EvoTestResult r) {
		super.record(className, methodName, r);
		currentMethod = className + "#" + methodName;
		currentResult.add(r);
	}

	@Override
	public void recordError(String className, String methodName, Exception e) {
		super.recordError(className, methodName, e);
	}

	@Override
	public void recordEndIterations(String methodName, String className) {
		iterator = currentResult.size();

		String methodId = className + "#" + methodName;
		if (currentResult.isEmpty() || !methodId.equals(currentMethod)) {
			currentMethod = null;
			currentResult.clear();
			return;
		}
		double successR = currentResult.size();
		List<Object> rowData = new ArrayList<Object>();
		while (currentResult.size() < (iterator - 1)) {
			int index = (int) Math.random() * currentResult.size();
			EvoTestResult r = currentResult.get(index);
			currentResult.add(r);
		}

		rowData.add(className);
		rowData.add(methodName);
		double bestCvg = 0.0;
		double totalCvg = 0.0;
		double totalTime = 0.0;
		double ageOfBestCvg = 0;
		double totalInitialCoverage = 0.0;
		double totalInitializationOverhead = 0.0;
		for (int i = 0; i < iterator; i++) {
			EvoTestResult r = currentResult.get(i);
			rowData.add(r.getTime());
			rowData.add(r.getCoverage());
			rowData.add(r.getAge());
			rowData.add(r.getInitialCoverage());
			rowData.add(r.getInitializationOverhead());
			
			StringBuffer sb = new StringBuffer();
			if(r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
				for(BranchInfo b: r.getMissingBranches()) {
					sb.append(b.toString() + "\\n");
				}
			}
			String missingBranches = sb.toString();
			if (missingBranches.isEmpty()) {
				missingBranches = "NA";
			}
			rowData.add(missingBranches);
			
			if (bestCvg < r.getCoverage()) {
				bestCvg = r.getCoverage();
				ageOfBestCvg = r.getAge();
			} else if (bestCvg == r.getCoverage()) {
				ageOfBestCvg = Math.min(ageOfBestCvg, r.getAge());
			}
			totalCvg += r.getCoverage();
			totalTime += r.getTime();
			totalInitialCoverage += r.getInitialCoverage();
			totalInitializationOverhead += r.getInitializationOverhead();
		}
		rowData.add(currentResult.get(0).getRatio());
		rowData.add(totalTime / successR);
		rowData.add(totalCvg / successR);
		rowData.add(bestCvg);
		rowData.add(ageOfBestCvg);
		rowData.add(totalInitialCoverage / successR);
		rowData.add(totalInitializationOverhead / successR);
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
		} catch (IOException e) {
			log.error("Error", e);
		}

		currentMethod = null;
		currentResult.clear();
	}

	@Override
	public String getFinalReportFilePath() {
		return excelWriter.getFile().getAbsolutePath();
	}
}
