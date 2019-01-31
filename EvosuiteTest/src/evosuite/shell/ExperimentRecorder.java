package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.outputFolder;
import static evosuite.shell.EvosuiteForMethod.projectId;

import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.CommonUtility;

public class ExperimentRecorder {
	private String successfulMethodsFile;
	
	public ExperimentRecorder() {
		successfulMethodsFile = outputFolder + "/successfulMethods.txt";
		FileUtils.writeFile(successfulMethodsFile, "#Project " + projectId + "\n", false);
	}
	

	public void logSuccessfulMethods(String className, String methodName) {
		FileUtils.writeFile(successfulMethodsFile, CommonUtility.getMethodId(className, methodName) + "\n", true);
	}


	public void record(String className, String methodName, TestGenerationResult testResult) {
		// override when needed.
	}

	public void recordError(String className, String methodName, Exception e) {
		// override when needed.
	}
}
