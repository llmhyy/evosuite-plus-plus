package regression.smartmutation.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;

import evosuite.shell.EvoTestResult;

public class SmartMutationTestSetup {
	@Before
	public void setup() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
	}

	protected void printResult(List<EvoTestResult> results0) {
		for (EvoTestResult lu : results0) {
			System.out.println("coverage: " + lu.getCoverage() + ", age: " + lu.getAge() + ", seed: "
					+ lu.getRandomSeed() + ", time: " + lu.getTime());
			System.out.println(lu.getProgress());
		}

	}

	protected List<Long> checkRandomSeeds(List<EvoTestResult> results0) {
		List<Long> randomSeeds = new ArrayList<>();
		for (EvoTestResult lu : results0) {
			randomSeeds.add(lu.getRandomSeed());
		}

		return randomSeeds;
	}
}
