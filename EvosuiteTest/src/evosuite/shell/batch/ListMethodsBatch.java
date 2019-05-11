package evosuite.shell.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelUtils;
import evosuite.shell.excel.MergeExcels;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.TestUtils;
import evosuite.shell.listmethod.FlagMethodProfilesFilter;
import evosuite.shell.listmethod.MethodFilterOption;
import evosuite.shell.utils.TargetMethodIOUtils;

public class ListMethodsBatch {

	@Test
	public void justRun() throws IOException {
//		runListMethod(MethodFilterOption.FLAG_METHOD_PROFILES);
//		runListMethod(MethodFilterOption.HAS_BRANCH);
		runListMethod(MethodFilterOption.FLAG_PROCEDURE_METHOD);
//		runListMethod(MethodFilterOption.NO_FLAG_METHOD);
	}
	
	@Test
	public void listClassWithFlagProcedureFilter() throws IOException {
		TargetMethodIOUtils.listTestableClasses(
				TestUtils.getAbsolutePath("/experiments/SF100/reports/flag-filtered-methods-all.txt"), 
				TestUtils.getAbsolutePath("/experiments/SF100-ForClass/targetClasses-flagProc.txt"));
	}
	
	public void runListMethod(MethodFilterOption opt) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		for (String projectName : projectFolders.keySet()) {
			File projectFolder = projectFolders.get(projectName);
			String[] name = projectFolder.getName().split("_");
			if (NumberUtils.isCreatable(name[0]) && checkProject(name[0])) {
				SFBenchmarkUtils.setupProjectProperties(projectFolder);
				String[] args = new String[] {
						"-target",
						projectFolder.getAbsolutePath() + "/" + name[1] + ".jar",
						"-listMethods",
						"-mFilterOpt", opt.getText()
				};
				EvosuiteForMethod.execute(args);
			}
		}
		TargetMethodIOUtils.generateStatisticExcel(Settings.getTargetMethodFilePath(),
				evosuite.shell.FileUtils.getFilePath(Settings.getReportFolder(), "targetMethodsStatistic.xlsx"));
		
		MergeExcels.excelSuffix = FlagMethodProfilesFilter.excelProfileSubfix;
		List<String> inputFiles = evosuite.shell.FileUtils.toFilePath(MergeExcels.listExcels(Settings.getReportFolder()));
		ExcelUtils.mergeExcel(Settings.getReportFolder() + "/flag-methods-profiles.xlsx", inputFiles, 0);
		
		System.out.println("Done!");
	}
	
	@Test
	public void cleanupJdkFile() throws IOException {
		String listFile = Settings.getReportFolder() + "/jdkClasses.txt";
		List<String> lines = FileUtils.readLines(new File(listFile));
		Set<String> set = new HashSet<String>(lines);
		lines = new ArrayList<>(set);
		Collections.sort(lines);
		evosuite.shell.FileUtils.writeFile(Settings.getReportFolder() + "/jdkClasses1.txt", 
				StringUtils.join(lines, "\n"), false);
	}
	
	private boolean checkProject(String prjIdx) {
//		return Integer.valueOf(prjIdx) == 1 ||  Integer.valueOf(prjIdx) == 10;
		return true;
	}
	
}
