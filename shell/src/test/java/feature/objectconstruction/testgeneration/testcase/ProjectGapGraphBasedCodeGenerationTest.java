package feature.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class ProjectGapGraphBasedCodeGenerationTest extends ObjectOrientedTest{
	@Test
	public void testGap1() throws ClassNotFoundException, RuntimeException {
		
		Properties.RANDOM_SEED = 1634620626101L;
		
		setup();

		Class<?> clazz = feature.objectconstruction.testgeneration.example.gap.ArrayElementExample.class;
		
		Properties.TARGET_CLASS = clazz.getCanonicalName();

		Method method = TestUtility.getTargetMethod("arrayElementAccess", clazz, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		Branch b = TestUtil.searchBranch(rankedList, 10);
		
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, false, false);
	}
}
