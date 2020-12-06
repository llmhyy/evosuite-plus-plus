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
import feature.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq.AcctInqRq;
import feature.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules;
import feature.objectconstruction.testgeneration.example.graphcontruction.JNFE.AddressData;

public class ProjectGraphBasedCodeGenerationTest extends ObjectOrientedTest {
	
	
	@Test
	public void testGeneration4CheckRule() throws ClassNotFoundException, RuntimeException {
		
		Properties.RANDOM_SEED = 1600102372406l;
		//1598462133372
		
		setup();

		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		Branch b = TestUtil.searchBranch(rankedList, 15);
		
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, true, false);
	}
	
	@Test
	public void testGeneration4Equal1() throws ClassNotFoundException, RuntimeException {
		
		Properties.RANDOM_SEED = 1598462235539l;
		//1598462133372
		
		setup();

		Properties.TARGET_CLASS = AddressData.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("equals", AddressData.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

		Branch b = rankedList.get(10);
		generateCode(b, false, false);
	}
	
	@Test
	public void testGeneration4Equal2() throws ClassNotFoundException, RuntimeException {
		
		Properties.RANDOM_SEED = 1598376776401l;
		
		setup();

		Properties.TARGET_CLASS = AcctInqRq.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("equals", AddressData.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

		//29
		Branch b = rankedList.get(12);
		System.out.println(b);
		generateCode(b, false, false);
	}

}
