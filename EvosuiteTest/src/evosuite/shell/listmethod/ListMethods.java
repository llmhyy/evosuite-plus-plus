package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.ParameterOptions;
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

	public static int execute(String[] targetClasses, ClassLoader classLoader, MethodFilterOption mFilterOpt,
			String targetMethodFilePath, String targetClassFilePath)
			throws ClassNotFoundException, IOException {
		StringBuilder headerSb = new StringBuilder();
		headerSb.append("#------------------------------------------------------------------------\n")
			.append("#Project=").append(EvosuiteForMethod.projectName).append("  -   ").append(EvosuiteForMethod.projectId).append("\n")
			.append("#------------------------------------------------------------------------\n");
		log.info(headerSb.toString());
		FileUtils.writeFile(targetMethodFilePath, headerSb.toString(), true);
		if (!ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)) {
			Properties.CRITERION = ArrayUtils.addAll(Properties.CRITERION, Criterion.DEFUSE);
		}
		IMethodFilter methodFilter = mFilterOpt.getCorrespondingFilter();
		int total = 0;
		StringBuilder tMethodSb = new StringBuilder(headerSb.toString());
		List<String> testableClasses = new ArrayList<String>();
		for (String className : targetClasses) {
			try {
				Class<?> targetClass = classLoader.loadClass(className);
				if (targetClass.isInterface()) {
					/* although Evosuite does filter to get only testable classes, listClasses still contains interface 
					 * which leads to error when executing Evosuite, that's why we need to add this additional check here */
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
	
}
