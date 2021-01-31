package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class BranchedMethodFilter extends MethodFlagCondFilter {
	public static final String excelProfileSubfix = "_branchedMethodProfiles.xlsx";
	private static Logger log = LoggerUtils.getLogger(BranchedMethodFilter.class);
	private ExcelWriter writer;

	public BranchedMethodFilter() {
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data",
				new String[] { "ProjectId", "ProjectName", "Target Method", "Flag Method", "branch", "const0/1",
						"branch", "getfield", "branch", "iLoad", "branch", "invokemethod", "other", "Remarks",
						"has Primitve type", "hasPrimitiveComparison", "isValid" },
				0);
	}

	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug(String.format("#Method %s#%s", className, methodName));
		return true;
	}
}
