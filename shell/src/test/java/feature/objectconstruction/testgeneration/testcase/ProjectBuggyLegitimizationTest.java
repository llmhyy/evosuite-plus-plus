package feature.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import feature.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample;
import feature.objectconstruction.testgeneration.example.graphcontruction.PngEncoderB;
import feature.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump.MUXFilter;

public class ProjectBuggyLegitimizationTest extends ObjectOrientedTest{
	@Before
	public void init() {
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
	}
	
	@Test
	public void testLegitimizationPublicField() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = CascadingCallExample.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("targetM", CascadingCallExample.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//				Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(0);

		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\test";
		assertLegitimization(b, true, false);
		
	}
	
	@Test
	public void testLegitimization2() throws ClassNotFoundException, RuntimeException {
//		Properties.RANDOM_SEED = 1598472350678l;
		
		setup();

		Properties.TARGET_CLASS = PngEncoderB.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("pngEncode", PngEncoderB.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(2);

		assertLegitimization(b, false, false);
		
	}
	
	@Test
	public void testLegitimization8() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.RANDOM_SEED = 1598289457901l;
		Properties.TARGET_CLASS = MUXFilter.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("pump", MUXFilter.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(0);

		assertLegitimization(b, false, false);
		
	}
}
