package regression.objectconstruction.testgeneration.testcase;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.instrumentation.InstrumentingClassLoader;

public class ObjectOrientedTest {
	public static void setup() {
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		Properties.ASSERTIONS = false;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.ADOPT_SMART_MUTATION = true;
		Properties.P_FUNCTIONAL_MOCKING = 0;
		Properties.MOCK_IF_NO_GENERATOR = false;
		Properties.FUNCTIONAL_MOCKING_PERCENT = 0;
		Properties.PRIMITIVE_POOL = 0.5;
		Properties.DYNAMIC_POOL = 0.5;
		Properties.ASSERTIONS = false;
		
		Properties.TIMEOUT = 300000000;
		
		// Clear all branches so that branch numbers do not change
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		BranchPool.getInstance(classLoader).reset();
	}
}
