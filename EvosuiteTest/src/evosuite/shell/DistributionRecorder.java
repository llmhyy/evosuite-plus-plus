package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.evosuite.result.TestGenerationResult;
import org.mockito.internal.util.StringUtil;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

public class DistributionRecorder extends ExperimentRecorder {
	private Logger log = LoggerUtils.getLogger(DistributionRecorder.class);
	private ExcelWriter distributionExcelWriter;
	public ArrayList<Double> distances = new ArrayList<Double>();
	public List<EvoTestResult> allresults = new ArrayList<EvoTestResult>();

	
	public DistributionRecorder() {
		super();
		distributionExcelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_distribution.xlsx"));
		distributionExcelWriter.getSheet("distribution", new String[] {"Class", "Method", "distribution","averagedistance","map","time","coverage"}, 0);
		distributionExcelWriter.getSheet("progress", new String[] {"Class", "Method", ""}, 0);



	}
	public DistributionRecorder(String strategy) {
		super();
		distributionExcelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_" + strategy + "_distribution.xlsx" ));
		distributionExcelWriter.getSheet("distribution", new String[] {"Class", "Method", "distribution","averagedistance","map","time","coverage"}, 0);
		distributionExcelWriter.getSheet("progress", new String[] {"Class", "Method", ""}, 0);

	}
	

	
	@Override
	public void record(String className, String methodName, EvoTestResult result) {
		if (result == null) {
			return;
		}
		log.info("" +result.getProgress());
//		for(int i=0; i<result.getDistribution().length; i++){
//			log.info("" +result.getDistribution()[i]);					
//		}	
		List<Object> progressRowData = new ArrayList<>();
		progressRowData.add(className);
		progressRowData.add(methodName);
		progressRowData.addAll(result.getProgress());
		
		List<Object> distributionRowData = new ArrayList<>();
		distributionRowData.add(className);
		distributionRowData.add(methodName);
		String distrstr = "";

		
		Map<Integer, Integer> distributionMap = result.getDistributionMap();
		if(distributionMap!=null && !distributionMap.isEmpty()){
			for(Integer branch0 : distributionMap.keySet()) {
				distrstr = distrstr.concat(branch0.toString() + ":" + distributionMap.get(branch0).toString() + ",");
			}
			distrstr = distrstr.substring(0, distrstr.length()-1);
		}
		distributionRowData.add(distrstr);
		
		double avedistribution = 0;
		String undistribution = "";
		Map <Integer, Double> map = result.getUncoveredBranchDistribution();
		int num = 0;
		if(map!=null && !map.isEmpty()) {
			num = map.entrySet().size();
					for (Integer branch : map.keySet()) {
			avedistribution = avedistribution + map.get(branch);
			undistribution = undistribution.concat(branch.toString() + ":" + map.get(branch).toString() + ",");
		}
					undistribution = undistribution.substring(0, undistribution.length()-1);
		}

		if(num == 0) {
			avedistribution = 0;
		}
		else{
			avedistribution = avedistribution / num;
		}
		distances.add(avedistribution);
		distributionRowData.add(avedistribution);
		distributionRowData.add(undistribution);
		distributionRowData.add(result.getTime());
		distributionRowData.add(result.getCoverage());
		
		record(progressRowData, distributionRowData);
		logSuccessfulMethods(className, methodName);
	}
	
	public void record(List<Object> progressRowData, List<Object> distributionRowData) {
		try {
			//System.out.println(timeRowData);
			distributionExcelWriter.writeSheet("progress", Arrays.asList(progressRowData));
			distributionExcelWriter.writeSheet("distribution", Arrays.asList(distributionRowData));			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getFinalReportFilePath() {
		return distributionExcelWriter.getFile().getAbsolutePath();
	}
	

}

