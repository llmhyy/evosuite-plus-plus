package feature.smartseed.testcase;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.junit.Before;

public class SmartSeedNegativeBranchEvaluatorTest {
	@Before
	public void init() {
		Properties.TIMEOUT = 300000000;
//		Properties.RANDOM_SEED = 1606757586999l;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
	}
	
	//TODO for Cheng Yan, add 10 test cases
}
