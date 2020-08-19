package evosuite.shell.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.utils.AlphanumComparator;

public class ExcelMethodCollector {

	public static void main(String[] args) {
//		String root = "/Users/lylytran/Projects/Evosuite/experiments/windows-reports";
//		String methodsFile = root + "/filtered_methods.txt";
//		String failMethodsFile = root + "/fail_methods.txt";
//		String folder = root + "/evoTest-reports";
		String root = args[0];
		if (!new File(root).getName().contains("report")) {
			root = args[0] + "/evoTest-reports";
		}
		String methodsFile = root + "/pass_methods.txt";
		String failMethodsFile = root + "/fail_methods.txt";
		String allMethodsFile = root + "/executed_methods.txt";
		String folder = root;
		
		evosuite.shell.FileUtils.delete(methodsFile, failMethodsFile, allMethodsFile);
		
		List<File> reports = new ArrayList<>(FileUtils.listFiles(new File(folder), 
				new IOFileFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return false;
					}
					
					@Override
					public boolean accept(File file) {
						if (file.getName().endsWith("evotest_5times.xlsx")
								&& !file.getName().startsWith("~")) {
							return true;
						}
						return false;
					}
				}, TrueFileFilter.INSTANCE));
		collectMethods(methodsFile, failMethodsFile, allMethodsFile, reports);
	}
	
	@Test
	public void execute() {
		main(new String[]{"/Users/lylytran/Projects/Evosuite/experiments/SF100-testFilteredMethods/evoTest-reports"});
	}

	private static void collectMethods(String methodsFile, String failMethodsFile, String allMethodsFile,
			List<File> reports) {
		Set<String> methods = new HashSet<>();
		String curProject = "";
		AlphanumComparator comparator = new AlphanumComparator();
		Collections.sort(reports, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return comparator.compare(o1.getName(), o2.getName());
			}
		});
		for (File report : reports) {
			String[] fileName = report.getName().split("_");
			if (!curProject.equals(fileName[1])) {
				curProject = fileName[1];
				StringBuilder sb = new StringBuilder()
						.append("#---------------------------------------------------------------------------\n")
						.append("#Project=").append(fileName[1]).append("  -  ").append(fileName[0]).append("_").append(fileName[1]).append("\n")
						.append("#---------------------------------------------------------------------------\n");
				evosuite.shell.FileUtils.writeFile(methodsFile, sb.toString(), true);
				evosuite.shell.FileUtils.writeFile(failMethodsFile, sb.toString(), true);
				evosuite.shell.FileUtils.writeFile(allMethodsFile, sb.toString(), true);
			}
			try {
				System.out.println(report.getName());
				ExcelReader reader = new ExcelReader(report, 0);
				List<List<Object>> rows = reader.listData("data");
				for (List<Object> row : rows) {
					if (row.size() <= 2) {
						continue;
					}
					String methodId = row.get(0) + "#" + row.get(1);
					int i = 4;
					for (; i < row.size(); i++) {
						Object value = row.get(i);
						if (value instanceof Number) {
							double num = ((Number) value).doubleValue();
							if (num > 0) {
								if (!methods.contains(methodId)) {
									evosuite.shell.FileUtils.writeFile(methodsFile, methodId + "\n", true);
									methods.add(methodId);
								}
								break;
							}
						}
					}
					if (i == row.size()) {
						StringBuilder sb = new StringBuilder(methodId).append("\n").append("# ");
						for (i = 2; i < row.size(); i++) {
							sb.append(row.get(i)).append(";\t");
						}
						sb.append("\n");
						evosuite.shell.FileUtils.writeFile(failMethodsFile, sb.toString(), true);
					}
					evosuite.shell.FileUtils.writeFile(allMethodsFile, methodId + "\n", true);
				}
				reader.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
