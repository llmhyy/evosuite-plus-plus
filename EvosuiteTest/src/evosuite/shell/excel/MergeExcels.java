package evosuite.shell.excel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.AlphanumComparator;

public class MergeExcels {

	public static void main(String[] args) throws Exception {
		String root = args[0];
		String reportFolder = root + "/evoTest-reports";
		String outputFile = reportFolder + "/allMethods.xlsx";
		File file = new File(outputFile);
		if (file.exists()) {
			file.delete();
		}
		List<String> inputFiles = FileUtils.toFilePath(listExcels(reportFolder));
		boolean filteredNoGAInvolved = false;
		if (CommonUtility.hasOpt(args, "-filteredNoGAInvolved")) {
			filteredNoGAInvolved = true;
		}
		mergeExcel(outputFile, inputFiles, 0, filteredNoGAInvolved);
		
		System.out.println("Done!");
	}

	@Before
	public void setup() {
//		SFConfiguration.sfBenchmarkFolder = "E:/lyly/Projects/evosuite/experiment/SF100-unittest";
	}
	
	@Test
	public void mergeExcels() throws IOException {
		String reportFolder = SFConfiguration.sfBenchmarkFolder + "/evoTest-reports";
		String outputFile = reportFolder + "/allMethods-filter.xlsx";
		List<String> inputFiles = FileUtils.toFilePath(listExcels(reportFolder));
		mergeExcel(outputFile, inputFiles, 0, false);
		
		System.out.println("Done!");
	}
	
	
	public static void mergeExcel(String outputFile, List<String> inputFiles, int headerRowNum,
			boolean filteredNoGAInvolved) throws IOException {
		if (CollectionUtils.isEmpty(inputFiles)) {
			return;
		}
		List<ExcelReader> excelReaders = new ArrayList<ExcelReader>();
		for (String inputFile : inputFiles) {
			try {
				excelReaders.add(new ExcelReader(new File(inputFile), headerRowNum));
			} catch (Exception e) {
			}
		}
		List<String> sheets = ExcelUtils.collectAllSheetNames(excelReaders);
		
		ExcelWriter excelWriter = new ExcelWriter(new File(outputFile));
		for (String sheet : sheets) {
			String[] headers = ExcelUtils.collectHeaders(excelReaders, sheet);
			String[] newHeaders = new String[] {"ProjectId"};
			newHeaders = ArrayUtils.addAll(newHeaders, headers);
			excelWriter.createSheet(sheet, newHeaders, headerRowNum);
			Set<String> methodIds = new HashSet<>();
			for (ExcelReader reader : excelReaders) {
				try {
					List<List<Object>> listData = reader.listData(sheet);
					List<List<Object>> newListData = new ArrayList<>(listData.size()); 
					for (List<Object> data : listData) {
						List<Object> newData = new ArrayList<>();
						String methodId = data.get(0) + "#" + data.get(1);
						double age = ((Number) data.get(4)).doubleValue();
						/* filter methods with 0 generation */
						if (filteredNoGAInvolved && age == 0.0) {
							continue;
						}
						if (!methodIds.contains(methodId)) {
							// projectId
							newData.add(reader.getName().substring(0, reader.getName().lastIndexOf("_")));
							// data
							newData.addAll(data);
							newListData.add(newData);
							methodIds.add(methodId);
						}
					}
					excelWriter.writeSheet(sheet, newListData);
				} catch (Exception e) {
					System.out.println("Error! " + reader.getName());
					e.printStackTrace();
				}
			}
		}
	}
	
	public static List<File> listExcels(String folder) {
		File[] files = new File(folder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("_evotest.xlsx") && !name.startsWith("~");
			}
		});
		List<File> excels = CollectionUtil.toArrayList(files);
		AlphanumComparator comparator = new AlphanumComparator();
		Collections.sort(excels, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return comparator.compare(o1.getName(), o2.getName());
			}
		});
		return excels;
	}
}
