package evosuite.shell;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.slf4j.Logger;

import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;

/**
 * 
 * @author lyly
 * cmd: java -jar [EvosuiteTest.jar] -target !PROJECT!.jar -listMethods
 * return: a txt file which contain list of methods.[/evoTest-reports/targetMethods.txt]
 */
public class ListMethods {
	private static Logger log = LoggerUtils.getLogger(ListMethods.class);
	
	public static final String OPT_NAME = ParameterOptions.LIST_METHODS_OPT;

	public static int execute(String[] targetClasses, ClassLoader classLoader) throws ClassNotFoundException, IOException {
		String allTargetMethodsFile = getTargetFilePath();
		StringBuilder sb = new StringBuilder();
		sb.append("#------------------------------------------------------------------------\n")
			.append("#Project=").append(EvosuiteForMethod.projectName).append("  -   ").append(EvosuiteForMethod.projectId).append("\n")
			.append("#------------------------------------------------------------------------\n");
		log.info(sb.toString());
		FileUtils.writeFile(allTargetMethodsFile, sb.toString(), true);
		if (!ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)) {
			Properties.CRITERION = ArrayUtils.addAll(Properties.CRITERION, Criterion.DEFUSE);
		}
		IMethodFilter methodFilter = new FlagMethodProfilesFilter();
		int total = 0;
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
				total += CollectionUtil.getSize(testableMethods);
				sb = new StringBuilder();
				for (String methodName : testableMethods) {
					sb.append(CommonUtility.getMethodId(className, methodName)).append("\n");
				}
				FileUtils.writeFile(allTargetMethodsFile, sb.toString(), true);
			} catch (Throwable t) {
				sb = new StringBuilder();
				sb.append("Error when executing class ").append(className);
				sb.append(t.getMessage());
				log.error("Error", t);
			}
		}
		return total;
	}

	public static String getTargetFilePath() {
		return FileUtils.getFilePath(SFConfiguration.getReportFolder(), EvosuiteForMethod.LIST_METHODS_FILE_NAME);
	}
	
}
