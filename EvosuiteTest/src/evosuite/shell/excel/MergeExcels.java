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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.utils.CollectionUtil;
import org.junit.Test;

import evosuite.shell.FileUtils;
import evosuite.shell.utils.AlphanumComparator;

/**
 * 
 * @author linyun
 * 
 * cmd:
 * MergeExcels -o .. -workingDir .. -excelSuffix
 */
public class MergeExcels {
	public static String excelSuffix = "_evotest.xlsx";
	private static Options options;
	private static final String OUTPUT = "o";
	private static final String REPORT_FOLDER = "workingDir";
	private static final String EXCEL_SUFFIX = "excelSuffix";
	
	static {
		options = new Options()
				.addOption(new Option(OUTPUT, true, "out put file path"))
				.addOption(new Option(REPORT_FOLDER, true, "working dir"))
				.addOption(new Option(EXCEL_SUFFIX, true, "excelSuffix"));
	}
	
	@Test
	public void test() throws Exception {
		main(new String[] {
				"-o", "E:\\linyun\\git_space\\SF100-clean\\report-branch\\all-methods-evotest.xlsx",
				"-workingDir", "E:\\linyun\\git_space\\SF100-clean\\report-branch",
				"-excelSuffix", "_evotest.xlsx"
		});
	}
	
	public static void main(String[] args) throws Exception {
		CommandLine cmd = new DefaultParser().parse(options, args);
		String outputFile = cmd.getOptionValue(OUTPUT);
		String reportFolder = cmd.getOptionValue(REPORT_FOLDER);
		String excelSuffix = cmd.getOptionValue(EXCEL_SUFFIX);
		mergeExcel(outputFile, reportFolder, excelSuffix);
	}
	
	public static void mergeExcel(String outputFile, String reportFolder, String excelSuffix) throws IOException {
		List<String> inputFiles = FileUtils.toFilePath(listExcels(reportFolder));
		MergeExcels.excelSuffix = excelSuffix;
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
		
		ExcelReader.close(excelReaders);
	}
	
	public static List<File> listExcels(String folder) {
		File[] files = new File(folder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(excelSuffix) && !name.startsWith("~");
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
