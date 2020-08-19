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

		// Clear all branches so that branch numbers do not change
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		BranchPool.getInstance(classLoader).reset();
	}
}
