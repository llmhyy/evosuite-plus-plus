package sf100;

import java.io.File;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.experiment.SFConfiguration;

public class TestCFGExtraction {
	@Before
	public void setup() {
//		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.SEARCH_BUDGET = 60000;
//		Properties.GLOBAL_TIMEOUT = 60000;
//		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void run10() {
		String projectId = "10_water-simulator";
		String oneBranchFile = SFConfiguration.sfBenchmarkFolder + File.separator + "one_branch.xlsx";
		
		System.setProperty("user.dir", SFConfiguration.sfBenchmarkFolder);
		
		CommonTestUtil.getBranchFeatures(projectId, oneBranchFile);

		
		
	}
}
