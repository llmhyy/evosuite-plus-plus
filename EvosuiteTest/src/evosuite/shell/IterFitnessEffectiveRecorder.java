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

public class IterFitnessEffectiveRecorder extends FitnessEffectiveRecorder {
	private String currentMethod;
	private List<TestGenerationResult> currentResult = new ArrayList<>();
	
	private Logger log = LoggerUtils.getLogger(FitnessEffectiveRecorder.class);
	private ExcelWriter excelWriter;
	private int iterator;
	
	public IterFitnessEffectiveRecorder(int iterator) {
		super();
		this.iterator = iterator;
		excelWriter = new ExcelWriter(FileUtils.newFile(SFConfiguration.getReportFolder(), 
				new StringBuilder().append(projectId).append("_evotest_").append(iterator).append("times.xlsx").toString()));
		List<String> header = new ArrayList<>();
		header.add("Class");
		header.add("Method");
		for (int i = 1; i <= iterator; i++) {
			header.add("Execution Time -r" + i);
			header.add("Coverage -r" + i);
			header.add("Age -r" + i);
		}
		header.add("Method Availability");
		header.add("Avg Execution Time");
		header.add("Avg Coverage");
		header.add("Best Coverage");
		header.add("Age of Best Coverage");
		excelWriter.getSheet("data", header.toArray(new String[header.size()]), 0);
	}
	
	@Override
	public void record(String className, String methodName, TestGenerationResult r) {
		super.record(className, methodName, r);
		currentMethod = className + "#" + methodName;
		currentResult.add(r);
	}
	
	@Override
	public void recordError(String className, String methodName, Exception e) {
		super.recordError(className, methodName, e);
	}
	
	@Override
	public void recordEndMethod(String methodName, String className) {
		String methodId = className + "#" + methodName;
		if (currentResult.isEmpty() || !methodId.equals(currentMethod)) {
			currentMethod = null;
			currentResult.clear();
			return;
		}
		int successR = currentResult.size();
		List<Object> rowData = new ArrayList<Object>();
		while (currentResult.size() < (iterator - 1)) {
			TestGenerationResult e = new TestResult();
			currentResult.add(e);
		}
		rowData.add(className);
		rowData.add(methodName);
		double bestCvg = 0.0;
		double totalCvg = 0.0;
		double totalTime = 0.0;
		double age = 0;;
		for (int i = 0; i < iterator; i++) {
			TestGenerationResult r = currentResult.get(i);
			rowData.add(r.getElapseTime());
			rowData.add(r.getCoverage());
			rowData.add(r.getGeneticAlgorithm() == null ? -1 : r.getGeneticAlgorithm().getAge());
			if (bestCvg < r.getCoverage()) {
				bestCvg = r.getCoverage();
				age = r.getGeneticAlgorithm() == null ? -1 : r.getGeneticAlgorithm().getAge();
			}
			totalCvg += r.getCoverage();
			totalTime += r.getElapseTime();
		}
		rowData.add(currentResult.get(0).getAvailabilityRatio());
		rowData.add(totalTime / successR);
		rowData.add(totalCvg / successR);
		rowData.add(bestCvg);
		rowData.add(age);
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
		} catch (IOException e) {
			log.error("Error", e);
		}
		
		currentMethod = null;
		currentResult.clear();
	}
}
