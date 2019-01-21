package com.test.experiment;

import java.io.File;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import com.test.EvosuiteForMethod;
import com.test.FileUtils;

public class EvoTestSingleMethod {

	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = "/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest";
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
	}
	
	@Test
	public void evoTestSingleMethod() {
		/* configure */
		String projectId = "1_tullibee";
		String projectName = "tullibee";
		String[] targetMethods = new String[]{
				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		/* run */
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, 
				targetMethods);

		String[] args = new String[] {
				"-criterion",
				"branch",
				"-target",
				FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile",
				file.getAbsolutePath()
		};
		SFBenchmarkUtils.setupProjectProperties(projectId);
		EvosuiteForMethod.main(args);
	}
	
	public void evoSuiteSingleMethod() {
		String projectId = "1_tullibee";
		SFBenchmarkUtils.setupProjectProperties(projectId);
	}
}
