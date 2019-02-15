package evosuite.shell.experiment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.excel.ExcelUtils;
import evosuite.shell.excel.MergeExcels;

public class ReportHandler {
	private String baseDir;

	@Before
	public void setup() {
		baseDir = System.getProperty("user.dir");
	}
	
	@Test
	public void mergeExcels() throws IOException {
		MergeExcels.excelSubfix = "_evotest_5times.xlsx";
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100-testFilteredMethods/evoTest-reports-fbranch-14Feb";
		String outputFile = reportFolder + "/14Feb-fbranch.xlsx";
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		MergeExcels.mergeExcel(outputFile, inputFiles, 0, false);
		
		System.out.println("Done!");
	}
	
	@Test
	public void mergeExcels1() throws IOException {
		MergeExcels.excelSubfix = "_flagMethodInsnStatistic.xlsx";
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports";
		String outputFile = reportFolder + "/flagMethodInsnStatistic.xlsx";
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
		TargetMethodTool.merge(inclusiveFiles, exclusivesFiles, resultTxt);
	}
	
	@Test
	public void selectMethods() throws IOException {
		String baseDir = System.getProperty("user.dir");
		String excelFile = baseDir + "/experiments/SF100/reports/flag-filtered-wth-GA-involved-branch.xlsx";
		String resultTxt = baseDir + "/experiments/SF100/reports/targetMethods-100methods.txt";
		String inclusiveTxt = baseDir + "/experiments/SF100/reports/targetMethods-invokedMethodFiltered.txt";
		new TargetMethodTool().selectMethods(excelFile, resultTxt, inclusiveTxt);
	}
	
	@Test
	public void collectMethods() {
		ExcelMethodCollector.main(new String[]{"/Users/lylytran/Projects/Evosuite/experiments/SF100-testFilteredMethods/evoTest-reports"});
	}

	@Test
	public void generateMethodDistributionExcel1() throws IOException {
		new TargetMethodTool().generateMethodDistributionExcel(
				baseDir + "/experiments/SF100/reports/interprocedure-flag-methods.txt",
				baseDir + "/experiments/SF100/reports/flag-filtered-methods-all.txt",
				baseDir + "/experiments/SF100/reports/has-branch-methods.txt",
				baseDir + "/experiments/SF100/reports/methods-distribution.xlsx");
	}
	
	@Test
	public void generateMethodDistributionExcel() throws IOException {
		new TargetMethodTool().generateMethodDistributionExcel(
				baseDir + "/experiments/SF100/reports/flag-filtered-methods-all.txt",
				baseDir + "/experiments/SF100/reports/has-branch-methods.txt",
				baseDir + "/experiments/SF100/reports/methods-distribution.xlsx");
	}
	
	@Test
	public void generateStatistic() throws IOException {
		new TargetMethodTool().generateStatisticExcel(
				baseDir + "/experiments/SF100/targetMethods.txt",
				baseDir + "/experiments/SF100/targetMethods.xlsx");
	}
}
