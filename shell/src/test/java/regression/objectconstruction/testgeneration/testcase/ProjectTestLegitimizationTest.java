package regression.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtility;
import regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules;
import regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged.InternalGmHeroFrame;

public class ProjectTestLegitimizationTest extends ObjectOrientedTest {
	
	@Before
	public void init() {
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
	}
	
//	@Test
//	public void testLegitimization0() throws ClassNotFoundException, RuntimeException {
//		
////		Properties.RANDOM_SEED = 1598286332193l;
//		
//		setup();
//
//		Properties.TARGET_CLASS = InternalGmHeroFrame.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("valueChanged", InternalGmHeroFrame.class, 1);
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//
//		Properties.TARGET_METHOD = targetMethod;
//
//		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
//
//		Branch b = rankedList.get(4);
//
//		assertLegitimization(b, false, false);
//	}
	
	@Test
	public void testLegitimizationCheckRules() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(19);

		assertLegitimization(b, false, false);
		
	}

	@Test
	public void testLegitimizationCheckRulesNullValue1() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();
		Properties.NULL_PROBABILITY = 0.5;

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(19);
		
		assertLegitimization(b, false, true);
		
	}


	@Test
	public void testLegitimizationCheckRulesNullValue2() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();
		Properties.NULL_PROBABILITY = 1.0;

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(19);
		
		assertLegitimization(b, false, true);
		
	}
	

//	@Test
//	public void testLegitimization9() throws ClassNotFoundException, RuntimeException {
//		setup();
//
//		Properties.TARGET_CLASS = FTPSender.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("execute", FTPSender.class, 2);
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//
//		Properties.TARGET_METHOD = targetMethod;
//
//		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
//		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();
//
//		Properties.APPLY_OBJECT_RULE = true;
//		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));
//
//		Map<Branch, Set<DepVariable>> interestedBranches = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//		ArrayList<Branch> rankedList = new ArrayList<>(interestedBranches.keySet());
//		Collections.sort(rankedList, new Comparator<Branch>() {
//			@Override
//			public int compare(Branch o1, Branch o2) {
//				return o1.getInstruction().getLineNumber() - o2.getInstruction().getLineNumber();
//			}
//		});
//
////			Branch b = Randomness.choice(interestedBranches.keySet());
//		Branch b = rankedList.get(0);
//
//		TestFactory testFactory = TestFactory.getInstance();
//		TestCase test = new DefaultTestCase();
//		while (test.size() == 0) {
//			testFactory.insertRandomStatement(test, 0);
//		}
//		try {
//			ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//			cpSynthesizer.constructDifficultObjectStatement(test, b);
//
//			PartialGraph graph = cpSynthesizer.getPartialGraph();
//			Map<DepVariable, List<VariableReference>> graph2CodeMap = cpSynthesizer.getGraph2CodeMap();
//
//			TestChromosome chromosome = TestCaseLegitimizer.getInstance().legitimize(test, graph, graph2CodeMap);
//			test = chromosome.getTestCase();
//			System.out.println(test);
//
//			MethodStatement targetStatement = test.findTargetMethodCallStatement();
//			ExecutionResult result = TestCaseExecutor.runTest(test);
//			int numExecuted = result.getExecutedStatements();
//			int legitimacyDistance = targetStatement.getPosition() - numExecuted + 1;
//
//			assert legitimacyDistance == 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//			assert false;
//		}
//		
//	}	


}
