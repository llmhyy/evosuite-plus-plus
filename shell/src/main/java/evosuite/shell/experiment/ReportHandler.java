package evosuite.shell.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelUtils;
import evosuite.shell.excel.MergeExcels;
import evosuite.shell.utils.TargetMethodIOUtils;

public class ReportHandler {
	private String baseDir;

	@Before
	public void setup() {
		baseDir = System.getProperty("user.dir");
	}
	
	@Test
	public void compare() throws IOException {
		String fbranch = "/Users/lylytran/Projects/Evosuite/experiments/reports/SF100/trial/fbranch-determined-3times-trial.xlsx";
		String branch = "/Users/lylytran/Projects/Evosuite/experiments/reports/SF100/trial/branch-determined-3times-trial.xlsx";
		
		Map<String, List<Trial>> fbranchTrials = getTrials(fbranch);
		Map<String, List<Trial>> branchTrials = getTrials(branch);
		
		int totalBothReach100 = 0;
		int totalBetter = 0;
		int totalWorse = 0;
		List<String> better = new ArrayList<>();
		List<String> worse = new ArrayList<>();
		for (String methodId : fbranchTrials.keySet()) {
			List<Trial> ftrials = fbranchTrials.get(methodId);
			List<Trial> btrials = branchTrials.get(methodId);
			if (ftrials == null || btrials == null) {
				continue;
			}
			for (int i = 0; i <3 ;i++) {
				Trial ftrial = ftrials.get(i);
				Trial btrial = btrials.get(i);
				if (ftrial.cvg == 1 && btrial.cvg == 1) {
					totalBothReach100 ++;
					if (ftrial.time < btrial.time) {
						totalBetter++;
						better.add(String.format("%s \t -r%d:  \t  %s / %s", methodId, i + 1, ftrial.time, btrial.time));
					} else if (ftrial.time > btrial.time) {
						worse.add(String.format("%s  \t -r%d:  \t  %s / %s", methodId, i + 1, ftrial.time, btrial.time));
						totalWorse ++;
					}
				}
			}
		}
		System.out.println("Total : " + totalBothReach100);
		System.out.println("Better: " + better);
		StringBuilder sb = new StringBuilder()
				.append("--------------------------------------------------------------------------------\n")
				.append("Total: ").append(totalBothReach100).append("\n")
				.append("--------------------------------------------------------------------------------\n")
					.append("--------------------------------------------------------------------------------\n")
					.append("Better: ").append(totalBetter).append("\n")
					.append("--------------------------------------------------------------------------------\n")
					.append(StringUtils.join(better, "\n"))
					.append("\n\n")
					.append("--------------------------------------------------------------------------------\n")
					.append("Worse: ").append(totalWorse).append("\n")
					.append("--------------------------------------------------------------------------------\n")
					.append(StringUtils.join(worse, "\n"));
					
		FileUtils.writeFile("/Users/lylytran/Projects/Evosuite/modified-version/evosuite/EvosuiteTest/experiments/trialReport.txt", 
				sb.toString(), 
				false);
	}
	
	private Map<String, List<Trial>> getTrials(String file) {
		ExcelReader reader = new ExcelReader(new File(file), 0);
		Map<String, List<Trial>> map = new HashMap<String, List<Trial>>();
		List<String> headers = reader.listHeader("data");
		List<List<Object>> listData = reader.listData("data");
		for (List<Object> rowData : listData) {
			String methodId = rowData.get(0) + "#" + rowData.get(1) + "#" + rowData.get(2);
			List<Trial> trials = new ArrayList<>();
			map.put(methodId, trials);
			for (int i = 1; i <=3; i++) {
				double cvg = ((Number)rowData.get(headers.indexOf("Coverage -r" + i))).doubleValue();
				double time = ((Number)rowData.get(headers.indexOf("Execution Time -r" + i))).doubleValue();
				trials.add(new Trial(methodId, time, i, cvg));
			}
		}
		return map;
	}
	
	private static class Trial {
		String targetMethod;
		double time;
		int round;
		double cvg;
		public Trial(String targetMethod, double time, int round, double cvg) {
			super();
			this.targetMethod = targetMethod;
			this.time = time;
			this.round = round;
			this.cvg = cvg;
		}
	}
	
	@Test
	public void mergeAllExcels() throws IOException {
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/reports/SF100";
		mergeReports("_evotest_3times.xlsx", 
				"/Users/lylytran/Projects/Evosuite/experiments/reports/hieu1-server/evoTest-reports-hieu1server/report-branch",
				reportFolder + "/hieu1-server-branch-determined_3times.xlsx");
		mergeReports("_evotest_3times.xlsx", 
				"/Users/lylytran/Projects/Evosuite/experiments/reports/hieu1-server/evoTest-reports-hieu1server/report-fbranch",
				reportFolder + "/hieu1-server-fbranch-determined_3times.xlsx");
		mergeReports("_evotest_3times.xlsx", 
				"/Users/lylytran/Projects/Evosuite/experiments/reports/hieu1-server/evoTest-reports-hieu1server/report-branch-undetermined",
				reportFolder + "/branch-undetermined-3times.xlsx");
		mergeReports("_evotest_3times.xlsx", 
				"/Users/lylytran/Projects/Evosuite/experiments/reports/hieu1-server/evoTest-reports-hieu1server/report-fbranch-undetermined",
				reportFolder + "/fbranch-undetermined-3times.xlsx");
		mergeReports("_evotest_3times.xlsx", 
				"/Users/lylytran/Projects/Evosuite/experiments/reports/hieu1-server/evoTest-reports-hieu1server/report-branch-uninterested",
				reportFolder + "/branch-uninterested-3times.xlsx");
		mergeReports("_evotest_3times.xlsx", 
				"/Users/lylytran/Projects/Evosuite/experiments/reports/hieu1-server/evoTest-reports-hieu1server/report-fbranch-uninterested",
				reportFolder + "/fbranch-uninterested-3times.xlsx");
		
		mergeExcels("-branch-determined_3times.xlsx", reportFolder, 
				reportFolder + "/branch-determined-3times.xlsx");
		mergeExcels("-fbranch-determined_3times.xlsx", reportFolder, 
				reportFolder + "/fbranch-determined_3times.xlsx");
		
		System.out.println("Done!");
	}
	
	@Test
	public void merge4Projects() throws IOException {
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/reports/4PRJS";
		mergeReports("_evotest_3times.xlsx",
				"/Users/lylytran/Projects/Evosuite/experiments/reports/lyly-vm-1/report-branch-4prj-uninterested", 
				reportFolder + "/4prj-branch-uninterested.xlsx");
		mergeReports("_evotest_3times.xlsx",
				"/Users/lylytran/Projects/Evosuite/experiments/reports/lyly-vm-1/report-fbranch-4prj-uninterested", 
				reportFolder + "/4prj-fbranch-uninterested.xlsx");
	}
	
	public void mergeReports(String excelSubfix, String reportFolder, String outputFile) throws IOException {
		MergeExcels.excelSuffix = excelSubfix;
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		MergeExcels.mergeExcel(outputFile, inputFiles, 0, false);
		
		System.out.println("Done!");
	}
	
	public void mergeExcels(String excelSubfix, String reportFolder, String outputFile) throws IOException {
		MergeExcels.excelSuffix = excelSubfix;
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		ExcelUtils.mergeExcel(outputFile, inputFiles, 0);
		
		System.out.println("Done!");
	}
	
	@Test
	public void mergeExcelsDeterminedFbranch() throws IOException {
		MergeExcels.excelSuffix = "fbranch-determined_3times.xlsx";
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/reports/SF100/determined";
		String outputFile = reportFolder + "/fbranch-determined_3times_merge.xlsx";
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		
		ExcelUtils.mergeExcel(outputFile, inputFiles, 0);
		
		System.out.println("Done!");
	}
	
	@Test
	public void mergeExcelsDetermined() throws IOException {
		MergeExcels.excelSuffix = "branch-determined_3times.xlsx";
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/reports/SF100/determined";
		String outputFile = reportFolder + "/branch-determined_3times_merge.xlsx";
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		Iterator<String> it = inputFiles.iterator();
		while(it.hasNext()) {
			if (it.next().endsWith("fbranch-determined_3times.xlsx")) {
				it.remove();
			}
		}
		ExcelUtils.mergeExcel(outputFile, inputFiles, 0);
		
		System.out.println("Done!");
	}
	
	@Test
	public void mergeExcels1() throws IOException {
		MergeExcels.excelSuffix = "_flagMethodProfiles.xlsx";
		String reportFolder = Settings.getReportFolder();
		String outputFile = reportFolder + "/flagMethodProfiles.xlsx";
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		ExcelUtils.mergeExcel(outputFile, inputFiles, 0);
		
		System.out.println("Done!");
	}
	
	@Test
	public void mergeTxt() throws IOException {
		String root = "/Users/lylytran/Projects/Evosuite/experiments/SF100-testFilteredMethods/evoTest-reports";
		List<String> inclusiveFiles = Arrays.asList(
				root + "/targetMethods-100methods.txt"
				);
		List<String> exclusivesFiles = Arrays.asList(root + "/executed_methods.txt");
		
		String resultTxt = root + "/merge.txt";
		TargetMethodIOUtils.merge(inclusiveFiles, exclusivesFiles, resultTxt);
	}
	
	@Test
	public void collectMethods() {
		ExcelMethodCollector.main(new String[]{"/Users/lylytran/Projects/Evosuite/experiments/SF100-testFilteredMethods/evoTest-reports"});
	}

	@Test
	public void generateMethodDistributionExcel1() throws IOException {
		TargetMethodIOUtils.generateMethodDistributionExcel(
				baseDir + "/experiments/SF100/reports/interprocedure-flag-methods.txt",
				baseDir + "/experiments/SF100/reports/flag-filtered-methods-all.txt",
				baseDir + "/experiments/SF100/reports/has-branch-methods.txt",
				baseDir + "/experiments/SF100/reports/methods-distribution.xlsx");
	}
	
	@Test
	public void generateMethodDistributionExcel() throws IOException {
		TargetMethodIOUtils.generateMethodDistributionExcel(
				baseDir + "/experiments/SF100/reports/flag-filtered-methods-all.txt",
				baseDir + "/experiments/SF100/reports/has-branch-methods.txt",
				baseDir + "/experiments/SF100/reports/methods-distribution.xlsx");
	}
	
	@Test
	public void generateStatistic() throws IOException {
		TargetMethodIOUtils.generateStatisticExcel(
				baseDir + "/experiments/SF100/targetMethods.txt",
				baseDir + "/experiments/SF100/targetMethods.xlsx");
	}
}
