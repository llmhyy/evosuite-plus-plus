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
	private ExcelWriter progressExcelWriter;
	private ExcelWriter unCovereddistribuitonExcelWriter;
	private ExcelWriter coverageExcelWriter;
	private ExcelWriter timeExcelWriter;
	private boolean firstit;
	
	public DistributionRecorder() {
		super();
		distributionExcelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_distribution.xlsx"));
		distributionExcelWriter.getSheet("distribution", new String[] {"Class", "Method", "distribution"}, 0);
		distributionExcelWriter.getSheet("unCovereddistribution", new String[] {"Class", "Method", "average", "map"}, 0);
		distributionExcelWriter.getSheet("time", new String[] {"Class", "Method", "time"}, 0);
		distributionExcelWriter.getSheet("coverage", new String[] {"Class", "Method", "coverage"}, 0);
		progressExcelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_progress.xlsx"));
		progressExcelWriter.getSheet("data", new String[] {"Class", "Method", ""}, 0);


	}
	
	@Override
	public void record(String className, String methodName, EvoTestResult result) {
		super.record(className, methodName, result);
	}
	
	
	@Override
	public void record(String className, String methodName, TestGenerationResult r) {
		if (r.getDistribution() == null || r.getDistribution().length == 0) {
			return;
		}
		log.info("" +r.getProgressInformation());
		for(int i=0; i<r.getDistribution().length; i++){
			log.info("" +r.getDistribution()[i]);					
		}	
		List<Object> progressRowData = new ArrayList<>();
		progressRowData.add(className);
		progressRowData.add(methodName);
		progressRowData.addAll(r.getProgressInformation());
		
		List<Object> distributionRowData = new ArrayList<>();
		distributionRowData.add(className);
		distributionRowData.add(methodName);
		String distrstr = Arrays.toString(r.getDistribution());
		distributionRowData.add(distrstr);
		
		List<Object> unCovereddistributionRowData = new ArrayList<>();
		unCovereddistributionRowData.add(className);
		unCovereddistributionRowData.add(methodName);
		double avedistribution = 0;
		String undistribution = "";
		Map <Integer, Double> map = r.getUncoveredBranchDistribution();
		int num = map.entrySet().size();
		if(!map.isEmpty()) {
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
		unCovereddistributionRowData.add(avedistribution);
		unCovereddistributionRowData.add(undistribution);
		
		List<Object> timeRowData = new ArrayList<>();
		timeRowData.add(className);
		timeRowData.add(methodName);
		timeRowData.add(r.getElapseTime());
		
		List<Object> coverageRowData = new ArrayList<>();
		coverageRowData.add(className);
		coverageRowData.add(methodName);
		coverageRowData.add(r.getCoverage());
		
		record(progressRowData, distributionRowData,unCovereddistributionRowData,timeRowData,coverageRowData);
		logSuccessfulMethods(className, methodName);
	}
	
	public void record(List<Object> progressRowData, List<Object> distributionRowData, List<Object> unCovereddistributionRowData, List<Object> timeRowData, List<Object> coverageRowData) {
		try {
			System.out.println(timeRowData);
			progressExcelWriter.writeSheet("data", Arrays.asList(progressRowData));
			distributionExcelWriter.writeSheet("distribution", Arrays.asList(distributionRowData));
			distributionExcelWriter.writeSheet("unCovereddistribution", Arrays.asList(unCovereddistributionRowData));
			distributionExcelWriter.writeSheet("time", Arrays.asList(timeRowData));
			distributionExcelWriter.writeSheet("coverage", Arrays.asList(coverageRowData));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getFinalReportFilePath() {
		return progressExcelWriter.getFile().getAbsolutePath();
	}
	

}

