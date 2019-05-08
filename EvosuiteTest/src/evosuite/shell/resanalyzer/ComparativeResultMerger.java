package evosuite.shell.resanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import evosuite.shell.ComparativeRecorder;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;

public class ComparativeResultMerger {

	public static void main(String[] args) {
		ComparativeResultMerger merger = new ComparativeResultMerger();
		
//		String branchSummaryAddress = SFConfiguration.sfBenchmarkFolder + File.separator + "summary.xlsx";
		String fbranchMaterialsAddress = SFConfiguration.sfBenchmarkFolder + File.separator + "report-fbranch";
		merger.runAnalyzer(fbranchMaterialsAddress);

	}

	public static final String BETTER_COVERAGE = "good coverage";
	public static final String BETTER_TIME = "good time";
	public static final String EQUAL = "equal";
	public static final String WORSE_COVERAGE = "worse coverage";
	public static final String WORSE_TIME = "worse time";
	
	private ExcelWriter excelWriter;
	
	public ComparativeResultMerger() {
		excelWriter = new ExcelWriter(new File(SFConfiguration.sfBenchmarkFolder + File.separator 
				+ "report-fbranch" + File.separator + "overall_compare.xlsx"));
		excelWriter.getSheet(BETTER_COVERAGE, ComparativeRecorder.header, 0);
		excelWriter.getSheet(BETTER_TIME, ComparativeRecorder.header, 0);
		excelWriter.getSheet(EQUAL, ComparativeRecorder.header, 0);
		excelWriter.getSheet(WORSE_COVERAGE, ComparativeRecorder.header, 0);
		excelWriter.getSheet(WORSE_TIME, ComparativeRecorder.header, 0);
	}
	
	private void runAnalyzer(String fbranchMaterialsAddress) {
		List<List<Object>> betterCoverage = new ArrayList<>();
		List<List<Object>> betterTime = new ArrayList<>();
		List<List<Object>> equal = new ArrayList<>();
		List<List<Object>> worseTime = new ArrayList<>();
		List<List<Object>> worseCoverage = new ArrayList<>();
		
		File root = new File(fbranchMaterialsAddress);
		
		if(!root.exists()) {
			System.err.println(root + " does not exsit");
			return;
		}
		
		for(File file: root.listFiles()) {
			if(file.exists() && file.getName().endsWith("evotest_compare.xlsx")
					&& !file.getName().startsWith("~$")){
				ExcelReader reader = new ExcelReader(file, 0);
				List<List<Object>> bc = reader.listData(BETTER_COVERAGE);
				List<List<Object>> bt = reader.listData(BETTER_TIME);
				List<List<Object>> eq = reader.listData(EQUAL);
				List<List<Object>> wc = reader.listData(WORSE_COVERAGE);
				List<List<Object>> wt = reader.listData(WORSE_TIME);
				
				betterCoverage.addAll(bc);
				betterTime.addAll(bt);
				equal.addAll(eq);
				worseCoverage.addAll(wc);
				worseTime.addAll(wt);
				
			}
		}
		
		try {
			excelWriter.writeSheet(BETTER_COVERAGE, betterCoverage);
			excelWriter.writeSheet(BETTER_TIME, betterTime);
			excelWriter.writeSheet(EQUAL, equal);
			excelWriter.writeSheet(WORSE_TIME, worseTime);
			excelWriter.writeSheet(WORSE_COVERAGE, worseCoverage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
