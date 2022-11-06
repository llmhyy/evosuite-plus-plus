package evosuite.shell.listmethod;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.GraphPool;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.ParameterOptions;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.TargetMethodIOUtils;

/**
 * 
 * @author lyly
 * cmd: java -jar [EvosuiteTest.jar] -target !PROJECT!.jar -listMethods
 * return: a txt file which contain list of methods.[/evoTest-reports/targetMethods.txt]
 */
public class ListMethods {
	private static Logger log = LoggerUtils.getLogger(ListMethods.class);
	
	public static final String OPT_NAME = ParameterOptions.LIST_METHODS_OPT;

	public static List<String> interestedMethods = new ArrayList<>();

	public static int execute(String[] targetClasses, ClassLoader classLoader, MethodFilterOption mFilterOpt,
			String targetMethodFilePath, String targetClassFilePath)
			throws ClassNotFoundException, IOException {
		
		
		//TODO Cheng Yan, read the excel/csv file to include a method list
		//interestedMethods = readCSVFile();
		
		StringBuilder headerSb = new StringBuilder();
//		headerSb.append("\n");
		headerSb.append("#------------------------------------------------------------------------\n")
			.append("#Project=").append(EvosuiteForMethod.projectName).append("  -   ").append(EvosuiteForMethod.projectId).append("\n")
			.append("#------------------------------------------------------------------------\n");
		log.info(headerSb.toString());
		FileUtils.writeFile(targetMethodFilePath, headerSb.toString(), true);
		if (!ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)) {
			Properties.CRITERION = ArrayUtils.addAll(Properties.CRITERION, Criterion.DEFUSE);
		}
		
		DependencyAnalysis.clear();
		
		/**
		 * we clear the branch pool and graph pool when analyzing a new project.
		 */
		BranchPool.getInstance(classLoader).reset();
		GraphPool.getInstance(classLoader).clear();
		
		IMethodFilter methodFilter = mFilterOpt.getCorrespondingFilter();
		int total = 0;
		StringBuilder tMethodSb = new StringBuilder(headerSb.toString());
		List<String> testableClasses = new ArrayList<String>();
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = classLoader.loadClass(className);
				// Filter out interface
				if (targetClass.isInterface()) {
					continue;
				}
				System.out.println("Class " + targetClass.getName());
				List<String> testableMethods = methodFilter.listTestableMethods(targetClass, classLoader);
				
				if (!CollectionUtil.isEmpty(testableMethods)) {
					testableClasses.add(className);
				}
				total += CollectionUtil.getSize(testableMethods);
				tMethodSb = new StringBuilder();
				for (String methodName : testableMethods) {
					//TODO Cheng Yan shall comment this code later
					//String methodSig = className + "#" + methodName;
					//if(interestedMethods.contains(methodSig)) {
					//	tMethodSb.append(CommonUtility.getMethodId(className, methodName)).append("\n");
					//}
					
					//TODO Cheng Yan shall comment this code later
					//tMethodSb.append(CommonUtility.getMethodId(className, methodName)).append("\n");
				}
				
				System.currentTimeMillis();
				/* log to targetMethod.txt file */
				FileUtils.writeFile(targetMethodFilePath, tMethodSb.toString(), true);
			} catch (Throwable t) {
				tMethodSb = new StringBuilder();
				tMethodSb.append("Error when executing class ").append(className);
				tMethodSb.append(t.getMessage());
				log.error("Error", t);
			}
		}
		/* log target classes */
		TargetMethodIOUtils.writeTargetClassOrMethodTxt(EvosuiteForMethod.projectName, EvosuiteForMethod.projectId, 
				testableClasses, targetClassFilePath);
		return total;
	}
	
	private static List<String> readCSVFile() throws IOException {
		String path = "D:\\linyun\\git_space\\SF100-clean\\constantMethods-60.xls";
		File f = new File(path);
//		ExcelReader excelReader = new ExcelReader(f, 3);
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(path));
//		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), projectId + "_evotest.xlsx"));
	    HSSFWorkbook wb = new HSSFWorkbook(fs);
	    HSSFSheet sheet = wb.getSheetAt(0);
	    HSSFRow row;
	    HSSFCell cell;
	    List<String> interestedMethods = new ArrayList<>();
	    
	    String pid = null;
	    String cla = null;
	    String met = null;
	    
	    int rows; // No of rows
	    rows = sheet.getPhysicalNumberOfRows();
	    int cols = 3; // No of columns

	    for(int r = 0; r < rows; r++) {
	        row = sheet.getRow(r);
	        if(row != null) {
	            for(int c = 0; c < cols; c++) {
	            	cell = row.getCell((short)c);
	                if(cell != null) {
	                    // Your code here
	                	if(c == 0) {
		            		pid = cell.toString();
//		            		System.out.println(cell.toString());
//		            		System.out.println(pid);
		            	}
		            	if(c == 1) {
		            		cla = cell.toString();
//		            		System.out.println(cla);
		            	}
		            	if(c == 2) {
		            		met = cell.toString();
		            		String allName =cla + "#" + met;
		            		interestedMethods.add(allName);
//		            		System.out.println(met);
		            	}
	                }
	            }
	        }
	    }
	    
//	    System.out.println(interestedMethods);
		return interestedMethods;
		
	}

	//test
	public static int execute(String[] targetClasses, ClassLoader classLoader, MethodFilterOption mFilterOpt,
			String cp )
			throws ClassNotFoundException, IOException {
		StringBuilder headerSb = new StringBuilder();
		if (!ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)) {
			Properties.CRITERION = ArrayUtils.addAll(Properties.CRITERION, Criterion.DEFUSE);
		}
		
		/**
		 * we clear the branch pool and graph pool when analyzing a new project.
		 */
		BranchPool.getInstance(classLoader).reset();
		GraphPool.getInstance(classLoader).clear();
		
		IMethodFilter methodFilter = mFilterOpt.getCorrespondingFilter();
		int total = 0;
		StringBuilder tMethodSb = new StringBuilder(headerSb.toString());
		List<String> testableClasses = new ArrayList<String>();
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = classLoader.loadClass(className);
				// Filter out interface
				if (targetClass.isInterface()) {
					continue;
				}
				System.out.println("Class " + targetClass.getName());
				List<String> testableMethods = methodFilter.listTestableMethods(targetClass, classLoader);
				
				if (!CollectionUtil.isEmpty(testableMethods)) {
					testableClasses.add(className);
				}
				total += CollectionUtil.getSize(testableMethods);
				tMethodSb = new StringBuilder();
				for (String methodName : testableMethods) {
					tMethodSb.append(CommonUtility.getMethodId(className, methodName)).append("\n");
				}
				
				System.currentTimeMillis();
				/* log to targetMethod.txt file */
//				FileUtils.writeFile(targetMethodFilePath, tMethodSb.toString(), true);
			} catch (Throwable t) {
				tMethodSb = new StringBuilder();
				tMethodSb.append("Error when executing class ").append(className);
				tMethodSb.append(t.getMessage());
				log.error("Error", t);
			}
		}
		/* log target classes */
//		TargetMethodIOUtils.writeTargetClassOrMethodTxt(EvosuiteForMethod.projectName, EvosuiteForMethod.projectId, 
//				testableClasses, targetClassFilePath);
		return total;
	}
	
}
