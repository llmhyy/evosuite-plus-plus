package com.test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.utils.ArrayUtil;
import org.slf4j.Logger;

public class ListMethods {
	private static Logger log = LoggerUtils.getLogger(ListMethods.class);
	
	public static final String OPT_NAME = ParameterOptions.LIST_METHODS_OPT;

	public static void execute(String[] targetClasses, ClassLoader classLoader) throws ClassNotFoundException, IOException {
		String projectMethodsFile = FileUtils.getFilePath(EvoSuite.base_dir_path, EvosuiteForMethod.LIST_METHODS_FILE_NAME);
		String allTargetMethodsFile = FileUtils.getFilePath(EvosuiteForMethod.outputFolder, EvosuiteForMethod.LIST_METHODS_FILE_NAME);
		StringBuilder sb = new StringBuilder();
		sb.append("#------------------------------------------------------------------------\n")
			.append("#Project=").append(EvosuiteForMethod.projectName).append("  -   ").append(EvosuiteForMethod.projectId).append("\n")
			.append("#------------------------------------------------------------------------\n");
		log.info(sb.toString());
		FileUtils.writeFile(allTargetMethodsFile, sb.toString(), true);
		if (!ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)) {
			Properties.CRITERION = ArrayUtils.addAll(Properties.CRITERION, Criterion.DEFUSE);
		}
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = classLoader.loadClass(className);
				if (targetClass.isInterface()) {
					/* although Evosuite does filter to get only testable classes, listClasses still contains interface 
					 * which leads to error when executing Evosuite, that's why we need to add this additional check here */
					continue;
				}
				System.out.println("Class " + targetClass.getName());
				List<String> testableMethods = MethodFilter.listTestableMethods(targetClass, classLoader);
				sb = new StringBuilder();
				for (String methodName : testableMethods) {
					sb.append(CommonUtility.getMethodId(className, methodName)).append("\n");
				}
				FileUtils.writeFile(projectMethodsFile, sb.toString(), true);
				FileUtils.writeFile(allTargetMethodsFile, sb.toString(), true);
			} catch (Throwable t) {
				sb = new StringBuilder();
				sb.append("Error when executing class ").append(className);
				sb.append(t.getMessage());
				log.error("Error", t);
			}
		}
	}
	
}
