package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import evosuite.shell.ComparativeRecorder;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

public class ListFeatures {
	private static Logger log = LoggerUtils.getLogger(ListFeatures.class);

	private ExcelWriter excelWriter;
	
	public static String[] header = new String[]{
			"project_id", 
			"class",
			"method", 
			"branch_id",
			"value", 
			"is_in_loop", 
			"depth", 
			"path_condition"
			};
	
	public ListFeatures() {
		excelWriter = new ExcelWriter(new File("branch_features.xlsx"));
		excelWriter.getSheet("feature", header, 0);
	}
	
	public void execute(String branchFile, URLClassLoader classLoader) {
		File file = new File(branchFile);
		if(!file.exists()) {
			log.error("The branch file " + file + " does not exist!");
		}
		
		ExcelReader reader = new ExcelReader(file, 0);
		
		List<BranchFeature> featureList = new ArrayList<>();
		
		List<List<Object>> bc = reader.listData("branch");
		for(List<Object> row: bc) {
			String projectId = (String) row.get(0);
			String className = (String) row.get(1);
			String methodName = (String) row.get(2);
			String branchId = (String) row.get(3);
			String branchValue = (String) row.get(4);
			
			BranchFeature feature = parseBranchFeature(projectId, classLoader, className, methodName, branchId, branchValue);
			
			featureList.add(feature);
		}
		
		List<List<Object>> featureLabel = transformFeatureToRawData(featureList);
		
		try {
			this.excelWriter.writeSheet("feature", featureLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BranchFeature parseBranchFeature(String projectId, URLClassLoader classLoader, String className, String methodName, String branchId,
			String branchValue) {
		try {
			Class<?> targetClass = classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<List<Object>> transformFeatureToRawData(List<BranchFeature> featureList) {
		// TODO Auto-generated method stub
		return null;
	}
}
