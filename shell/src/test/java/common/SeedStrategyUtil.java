package common;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.result.BranchInfo;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.result.seedexpr.BranchCoveringEvent;
import org.evosuite.result.seedexpr.Event;
import org.evosuite.testcase.TestFitnessFunction;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;

import evosuite.shell.EvoTestResult;
import evosuite.shell.ExperimentRecorder;
import evosuite.shell.FileUtils;
import evosuite.shell.FitnessEffectiveRecorder;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class SeedStrategyUtil {
	private static Logger log = LoggerUtils.getLogger(FitnessEffectiveRecorder.class);
	private static ExcelWriter excelWriter;
	private static OutputStreamWriter jsonWriter;
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteDynaMOSA(String targetClass, String targetMethod, String cp,  String fitnessAppraoch,
			int iteration,long seconds,boolean instrumentContext, Long seed,
			boolean applyObjectRule,
			double primitivePool,
			double dynamicPool) throws IOException {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "flase",
				"-Denable_branch_enhancement", "true",
			
				"-generateMOSuite",
				"-Dstrategy","MOSUITE",
				"-Dalgorithm","DynaMOSA",
				"-Dselection_function","RANK_CROWD_DISTANCE_TOURNAMENT",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", String.valueOf(primitivePool),
				"-Ddynamic_pool", String.valueOf(dynamicPool),
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				
			};
			if(seed != null) {
				command = ArrayUtils.add(command, "-seed");
				command = ArrayUtils.add(command,  seed.toString());
			}
			EvoTestResult result = null;

			recordSegmentationList();
			for(int i = 0;i < iteration;i++) {
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				result = new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
				result.setAvailableCalls(r.getAvailableCalls());
				result.setUnavailableCalls(r.getUnavailableCalls());
				result.setBranchInformation(r.getBranchInformation());
				result.setInitialCoverage(r.getInitialCoverage());
				result.setMissingBranches(r.getMissingBranches());
				result.setInitializationOverhead(r.getInitializationOverhead());
				result.setCoveredBranchWithTest(r.getCoveredBranchWithTest());
				result.setEventSequence(r.getEventSequence());
				
				record(targetClass, targetMethod, result);
				recordSeedingToJson(targetClass, targetMethod, result);
					
			}
		}
			}

		return null;
		}
	
	private static void recordSegmentationList() throws IOException {
		String path = "D:\\linyun\\experiment\\";

		excelWriter = new ExcelWriter(FileUtils.newFile(path + "Seed_evotest.xlsx"));
		excelWriter.getSheet("data",
				new String[] { "Class", "Method", "Execution Time", "Coverage", "Age", "Call Availability",
						"IP Flag Coverage", "Uncovered IF Flag", "Random Seed", "Unavailable Call", "Initial Coverage",
						"Initialization Overhead", "CoveredBranchWithTest","Missing Branches","Missing InstructID"},
				0);
			    
		//json
		File jsonFile = null;
		jsonFile = new File(path + "Seed_evotest.json");
		jsonFile.createNewFile();
		if (jsonFile.isFile()) {
		    // create jsonWriter
			System.out.println("file:" + jsonFile);
			jsonWriter = new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8");
		}

	}
	
	private void initSegmentationListExcel(String file) {
		XSSFWorkbook segmentationList_wb = new XSSFWorkbook();
		XSSFSheet segmentationList_ws = segmentationList_wb.createSheet("segmentationList");
		String[] headers = { "Goals", "Segmentation", "Strategy", "Timeout", "Suite" };
		XSSFRow row = segmentationList_ws.createRow(0);
//		addHeader(row, headers);

		try {
			OutputStream fileOut = new FileOutputStream(file);
			segmentationList_wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void record(String className, String methodName, EvoTestResult r) {
		List<Object> rowData = new ArrayList<Object>();
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(r.getTime());
		rowData.add(r.getCoverage());
		rowData.add(r.getAge());
		rowData.add(r.getRatio());
		rowData.add(r.getIPFlagCoverage());
		rowData.add(r.getUncoveredFlags());
		rowData.add(r.getRandomSeed());
//		String unavailableString = getUnaviableCall(r.getMethodCallAvailability());
//		rowData.add(unavailableString);
		rowData.add(r.getInitialCoverage());
		rowData.add(r.getInitializationOverhead());
		rowData.add(r.getCoveredBranchWithTest().toString());

		StringBuffer sb = new StringBuffer();
		if (r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
			for (BranchInfo b : r.getMissingBranches()) {
				sb.append(b.toString() + "\\n");
			}
		}
		String missingBranches = sb.toString();
		if (missingBranches.isEmpty()) {
			missingBranches = "NA";
		}
		rowData.add(missingBranches);
		
		StringBuffer si = new StringBuffer();
		if (r.getMissingBranches() != null && !r.getMissingBranches().isEmpty()) {
			for (BranchInfo b : r.getMissingBranches()) {
				si.append(b.getStringValue() + ":" + b.getTruthValue() + "\\n");
			}
		}
		String missingInstructID = si.toString();
		if (missingInstructID.isEmpty()) {
			missingInstructID = "NA";
		}
		rowData.add(missingInstructID);
		
		try {
			excelWriter.writeSheet("data", Arrays.asList(rowData));
//			logSuccessfulMethods(className, methodName);
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
	
	public static void recordSeedingToJson(String className, String methodName, EvoTestResult r) throws IOException {
		List<Object> rowData = new ArrayList<Object>();		
		rowData.add("Events");
		if (r.getEventSequence() != null && !r.getEventSequence().isEmpty()) {
			for (Event e: r.getEventSequence()) {
				Map<String, Object> eventMap = new HashMap<>();
				if(e.toString().contains("branchCovering")) {
					BranchCoveringEvent bc = (BranchCoveringEvent)e;
					BranchInfo b = bc.getBranch();
					String testcase = bc.getTestCode();
					eventMap.put("Type",e.toString());
					eventMap.put("BranchInfo",b);
					eventMap.put("Testcode",testcase);
					rowData.add(eventMap);
				}else
				{
					eventMap.put("Type",e.toString());
					eventMap.put("DataType",e.getDataType());
					eventMap.put("oldValue",e.getOldValue());
					eventMap.put("value",e.getValue());
					rowData.add(eventMap);
				}
					
							
			}
		}
		rowData.add("EndSeed");
		String json = JSON.toJSONString(rowData,true);

		try {
			jsonWriter.flush();
			jsonWriter.write(json);
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
}
