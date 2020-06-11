package regression.branchenhancement.testcase;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;

public class BranchEnhancementTestSetup {
	@Before
	public void setup() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = true;
		Properties.APPLY_OBJECT_RULE = false;
		Properties.ADOPT_SMART_MUTATION = false;
	}
}
