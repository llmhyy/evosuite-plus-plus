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
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.graphs.dataflow.GraphVisualizer;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.factories.RandomLengthTestFactory;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUtility;

import regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules;

public class ObjectOrientedTest {
	
	public static void setup() {
		Properties.CRITERION = new Criterion[] {Criterion.BRANCH};
	}
	
	
	@Test
	public void testComputationGraphConstruction1() throws ClassNotFoundException {
		
		setup();
		
		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();
//		TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);

//		List<String> classpath = new ArrayList<>();
//		String cp = System.getProperty("user.dir") + "/target/test-classes";
//		classpath.add(cp);
//		ClassPathHandler.getInstance().addElementToTargetProjectClassPath(cp);

		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));

//		Dataflow.initializeDataflow();

		TestFactory testFactory = TestFactory.getInstance();
		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);

		for(Branch b: map.keySet()) {
			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//			GraphVisualizer.visualizeComputationGraph(b, 10000);
			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000);
		}
	}
	
	@Test
	public void testComputationGraphConstruction2() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction3() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction4() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction5() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction6() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction7() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction8() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction9() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testComputationGraphConstruction10() throws ClassNotFoundException {
		
	}
	
	@Test
	public void testLegitimization() throws ClassNotFoundException, RuntimeException {
		setup();
		
		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();

		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));
		
		Map<Branch, Set<DepVariable>> interestedBranches = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
		ArrayList<Branch> rankedList = new ArrayList<>(interestedBranches.keySet());
		Collections.sort(rankedList, new Comparator<Branch>() {
			@Override
			public int compare(Branch o1, Branch o2) {
				return o1.getInstruction().getLineNumber() - o2.getInstruction().getLineNumber();
			}
		});
		
//		Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(19);
		
		TestFactory testFactory = TestFactory.getInstance();
		TestCase test = new DefaultTestCase();
		while(test.size() == 0) {
			testFactory.insertRandomStatement(test, 0);				
		}
		try {
			ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
			cpSynthesizer.constructDifficultObjectStatement(test, b);
			
			PartialGraph graph = cpSynthesizer.getPartialGraph();
			Map<DepVariable, List<VariableReference>> graph2CodeMap = cpSynthesizer.getGraph2CodeMap();
			
			TestChromosome chromosome = TestCaseLegitimizer.getInstance().legitimize(test, graph, graph2CodeMap);
			test = chromosome.getTestCase();
			
			System.out.println(test);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
