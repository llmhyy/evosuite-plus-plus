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
		org.evosuite.Properties.ALWAYS_REGISTER_BRANCH = true;
		
//		runListMethod(MethodFilterOption.FLAG_PRIMITIVE_PARAMETER_FIELD);
//		runListMethod(MethodFilterOption.IPF_EASY_OBJECT);
//		runListMethod(MethodFilterOption.FLAG_METHOD_PROFILES);
//		runListMethod(MethodFilterOption.HAS_BRANCH);
//		runListMethod(MethodFilterOption.FLAG_PROCEDURE_METHOD);
//		runListMethod(MethodFilterOption.NO_FLAG_METHOD);
//		runListMethod(MethodFilterOption.PRIMITIVE_PARAMETER);
//		runListMethod(MethodFilterOption.AT_LEAST_FOUR_BRANCHES);
//		runListMethod(MethodFilterOption.PRIMITIVE_BASED_METHOD_WITH_CONSTRAINT);
//		runListMethod(MethodFilterOption.PRIMITIVE_BASED_METHOD);
//		runListMethod(MethodFilterOption.BRANCHED_METHOD);
//		runListMethod(MethodFilterOption.STRING_ARRAY_INPUT_METHOD);
//		runListMethod(MethodFilterOption.STRING_ARRAY_CONDITION_RELATED_METHOD);
//		runListMethod(MethodFilterOption.PRIMITIVE_ARRAY_CONDITION_RELATED_METHOD);
//		runListMethod(MethodFilterOption.MAIN_METHOD);
//		runListMethod(MethodFilterOption.CALLS_INT_METHOD);
		runListMethod(MethodFilterOption.CALLS_RECURSIVE_METHOD);

//	}
	}

	@Test
	public void listClassWithFlagProcedureFilter() throws IOException {
		TargetMethodIOUtils.listTestableClasses(
				TestUtils.getAbsolutePath("/experiments/SF100/reports/flag-filtered-methods-all.txt"), 
				TestUtils.getAbsolutePath("/experiments/SF100-ForClass/targetClasses-flagProc.txt"));
	}
	
	public void runListMethod(MethodFilterOption opt) throws IOException {
		Map<String, File> projectFolders = SFBenchmarkUtils.listProjectFolders();
		File dependentLibFolder = null;
		File[] dependentLibJars = null;
		for (String projectName : projectFolders.keySet()) {
			File projectFolder = projectFolders.get(projectName);
			if (projectFolder.isDirectory()) {
				File[] files = projectFolder.listFiles();
				for (File file : files) {
					if (file.getAbsolutePath().equals(projectFolder + "\\" + "lib")) {
						dependentLibFolder = file;
						if (dependentLibFolder.isDirectory()) {
							dependentLibJars = dependentLibFolder.listFiles();
							break;
						}
					}
				}
			}

			String[] name = projectFolder.getName().split("_");
			if (NumberUtils.isCreatable(name[0]) && checkProject(name[0])) {
				SFBenchmarkUtils.setupProjectProperties(projectFolder);
				List<String> libJarPaths = new ArrayList<>();
				if (dependentLibJars != null) {
					for (int i = 1; i <= dependentLibJars.length; i++) {
						String jarPath = dependentLibJars[i - 1].getAbsolutePath();
						if (jarPath.endsWith(".txt")) {
							continue;
						}
						libJarPaths.add(jarPath);
					}
				}

				StringBuilder argJar = new StringBuilder(projectFolder.getAbsolutePath() + "/" + name[1] + ".jar");
				for (String libPath : libJarPaths) {
					argJar.append(";");
					argJar.append(libPath);
				}
				String[] args = new String[] {
						"-target",
						argJar.toString(),
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
