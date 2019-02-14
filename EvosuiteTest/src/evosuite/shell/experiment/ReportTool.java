package evosuite.shell.experiment;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.excel.MergeExcels;

public class ReportTool {

	
	@Test
	public void mergeExcels() throws IOException {
		MergeExcels.excelSubfix = "_evotest_5times.xlsx";
		String reportFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100-testFilteredMethods/evoTest-reports-fbranch-14Feb";
		String outputFile = reportFolder + "/14Feb-fbranch.xlsx";
		List<String> inputFiles = FileUtils.toFilePath(MergeExcels.listExcels(reportFolder));
		MergeExcels.mergeExcel(outputFile, inputFiles, 0, false);
		
		System.out.println("Done!");
	}
	
	
}
