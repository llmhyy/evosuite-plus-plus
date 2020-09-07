package regression.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.junit.Test;

import com.test.TestUtility;

import regression.objectconstruction.testgeneration.example.graphcontruction.AcctInqRq.AcctInqRq;
import regression.objectconstruction.testgeneration.example.graphcontruction.JNFE.AddressData;

public class GraphBasedCodeGenerationTest extends ObjectOrientedTest {
	
	
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
		generateCode(b);
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
		generateCode(b);
	}

}
