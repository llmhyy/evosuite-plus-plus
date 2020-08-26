package regression.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.junit.Test;

import com.test.TestUtility;

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

		Properties.TARGET_CLASS = AddressData.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("equals", AddressData.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

		Branch b = rankedList.get(10);
		generateCode(b);
	}

	protected TestCase generateCode(Branch b) {
		TestFactory testFactory = TestFactory.getInstance();
		TestCase test = initializeTest(b, testFactory);
		try {
			ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
			cpSynthesizer.constructDifficultObjectStatement(test, b);
			mutateNullStatements(test);
			
			PartialGraph graph = cpSynthesizer.getPartialGraph();
			Map<DepVariable, List<VariableReference>> graph2CodeMap = cpSynthesizer.getGraph2CodeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("random seed is " + Randomness.getSeed());
		return test;
	}
	
}
