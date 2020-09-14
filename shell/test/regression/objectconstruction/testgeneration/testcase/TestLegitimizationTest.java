package regression.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUtility;

import regression.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample;
import regression.objectconstruction.testgeneration.example.graphcontruction.Article;
import regression.objectconstruction.testgeneration.example.graphcontruction.HandballModel;
import regression.objectconstruction.testgeneration.example.graphcontruction.PngEncoderB;
import regression.objectconstruction.testgeneration.example.graphcontruction.ArjArchiveEntry.isDirectory.ArjArchiveEntry;
import regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules;
import regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals.ChkOrdAudRs_TypeSequence2;
import regression.objectconstruction.testgeneration.example.graphcontruction.ExpressionNodeList.addExpressionList.ExpressionNodeList;
import regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged.InternalGmHeroFrame;
import regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump.MUXFilter;
import regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close.RMIManagedConnectionAcceptor;

public class TestLegitimizationTest extends ObjectOrientedTest {
	
	@Before
	public void init() {
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 100;
	}
	
	@Test
	public void testLegitimization1() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(19);

		assertLegitimization(b, false);
		
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

		assertLegitimization(b, false);
		
	}

	@Test
	public void testLegitimization3() throws ClassNotFoundException, RuntimeException {
		
		Properties.RANDOM_SEED = 1598286332193l;
		
		setup();

		Properties.TARGET_CLASS = InternalGmHeroFrame.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("valueChanged", InternalGmHeroFrame.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

		Branch b = rankedList.get(4);

		assertLegitimization(b, false);
	}


	@Test
	public void testLegitimization4() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = HandballModel.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("setMoveName", HandballModel.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(0);

		assertLegitimization(b, false);
		
	}
	
	

	@Test
	public void testLegitimization5() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = Article.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("getRevisionId", Article.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(0);

		assertLegitimization(b, false);
		
	}

	@Test
	public void testLegitimization6() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = ExpressionNodeList.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("addExpressionList", ExpressionNodeList.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(1);

		assertLegitimization(b, false);
		
	}

	@Test
	public void testLegitimization7() throws ClassNotFoundException, RuntimeException {
		setup();

//		Properties.RANDOM_SEED = 1598288297694l;
		Properties.TARGET_CLASS = RMIManagedConnectionAcceptor.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("close", RMIManagedConnectionAcceptor.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(4);

		assertLegitimization(b, false);
		
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

		assertLegitimization(b, false);
		
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

	@Test
	public void testLegitimization10() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = ArjArchiveEntry.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("isDirectory", ArjArchiveEntry.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//				Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(0);

		assertLegitimization(b, false);
		
	}

	@Test
	public void testLegitimization11() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = CascadingCallExample.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("targetM", CascadingCallExample.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//				Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(0);

		assertLegitimization(b, false);
		
	}

	@Test
	public void testLegitimization12() throws ClassNotFoundException, RuntimeException {
		setup();

		Properties.TARGET_CLASS = ChkOrdAudRs_TypeSequence2.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("equals", ChkOrdAudRs_TypeSequence2.class, 1);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();

//			Branch b = Randomness.choice(interestedBranches.keySet());
		Branch b = rankedList.get(14);

		assertLegitimization(b, false);
		
	}

}
