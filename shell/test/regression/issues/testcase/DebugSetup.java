package regression.issues.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;

import evosuite.shell.EvoTestResult;

public class DebugSetup {
	@Before
	public void setup() {
//		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.SEARCH_BUDGET = 60000;
//		Properties.GLOBAL_TIMEOUT = 60000;
//		Properties.TIMEOUT = 30000000;
		
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	protected void printResult(List<EvoTestResult> results0) {
		for(EvoTestResult lu: results0){
			System.out.println("coverage: " + lu.getCoverage() + ", age: " 
					+ lu.getAge() + ", seed: " + lu.getRandomSeed() + ", time: " + lu.getTime());
			System.out.println(lu.getProgress());
		}
		
	}
	
	protected List<Long> checkRandomSeeds(List<EvoTestResult> results0) {
		List<Long> randomSeeds = new ArrayList<>();
		for(EvoTestResult lu: results0){
			randomSeeds.add(lu.getRandomSeed());
		}
		
		return randomSeeds;
	}
}
