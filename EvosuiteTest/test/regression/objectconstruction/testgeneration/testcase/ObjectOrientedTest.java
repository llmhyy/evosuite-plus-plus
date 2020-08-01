package regression.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import org.evosuite.testcase.synthesizer.DepVariableWrapper;
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
			
			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();

//			GraphVisualizer.visualizeComputationGraph(b, 10000);
			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000);
			
			String branchName = partialGraph.getBranch().toString();
			System.out.println(branchName);
			Set<String> labelStrings = new HashSet<String>();
			
			switch (branchName) {
			case "I29 Branch 1 IF_ICMPNE L7": {
				// Parameters: GameState
				assert topLayer.size() == 1;
				assert topLayer.get(0).var.getUniqueLabel().equals("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"2\n");

				// GameState -> gamestate
				assert topLayer.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"I\n" + 
						"gamestate\n");
				break;
			}
			case "I37 Branch 2 IFNULL L9":
			case "I42 Branch 3 IFNE L9": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"1\n");
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"2\n");
				
				// Action -> actor
				assert topLayer.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"actor\n");
				
				// GameState -> players, gamestate
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 2;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"[Lregression/objectconstruction/testgeneration/example/graphcontruction/Player;\n" + 
						"players\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"I\n" + 
						"gamestate\n");

				// GameState -> players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel().equals("array element\n" + 
						"I10 (16) AALOAD l185\n");
				break;
			}
			case "I50 Branch 4 IF_ICMPEQ L10": 
			case "I56 Branch 5 IF_ICMPEQ L10": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"1\n");
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"2\n");
				
				// Action -> actor, action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 2;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"actor\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"action\n");
				
				// GameState -> players, gamestate
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 2;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"[Lregression/objectconstruction/testgeneration/example/graphcontruction/Player;\n" + 
						"players\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"I\n" + 
						"gamestate\n");

				// GameState -> players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel().equals("array element\n" + 
						"I10 (16) AALOAD l185\n");				
				break;
			}
			case "I58 Branch 6 IFNULL L10":
			case "I63 Branch 7 IFEQ L10": 
			case "I74 Branch 8 TABLESWITCH L13 Case 0": 
			case "I74 Branch 9 TABLESWITCH L13 Case 1":
			case "I74 Branch 10 TABLESWITCH L13 Case 2":
			case "I74 Branch 11 TABLESWITCH L13 Case 3":
			case "I74 Branch 12 TABLESWITCH L13 Case 4":
			case "I74 Branch 13 TABLESWITCH L13 Case 5":
			case "I74 Branch 14 TABLESWITCH L13 Case 6":
			case "I74 Branch 15 TABLESWITCH L13 Case 7":
			case "I74 Branch 16 TABLESWITCH L13 Case 8":
			case "I74 Branch 17 TABLESWITCH L13 Default-Case": 
			case "I79 Branch 18 IF_ACMPNE L15" :
			case "I90 Branch 19 IFNE L19" :
			case "I113 Branch 21 IFEQ L28":
			case "I125 Branch 22 IF_ICMPNE L33": 
			case "I134 Branch 23 IF_ACMPNE L37":
			case "I145 Branch 24 IFNE L41":
			case "I156 Branch 25 IFNE L45":
			case "I165 Branch 26 IF_ACMPNE L50":
			case "I176 Branch 27 IFNE L54":
			case "I197 Branch 29 IF_ACMPNE L63":
			case "I208 Branch 30 IFNE L67":
			case "I229 Branch 32 IF_ACMPNE L76":
			case "I240 Branch 33 IFNE L80":
			case "I253 Branch 34 IFNE L84":
			case "I262 Branch 35 IF_ACMPNE L89":
			case "I273 Branch 36 IFNE L93":
			case "I286 Branch 37 IFNE L97":
			case "I298 Branch 38 IF_ACMPNE L104": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"1\n");
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"2\n");
			
				// Action -> target, actor, action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 3;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"target\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"actor\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"action\n");
				
				// GameState -> players, gamestate
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 2;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"[Lregression/objectconstruction/testgeneration/example/graphcontruction/Player;\n" + 
						"players\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"I\n" + 
						"gamestate\n");

				// GameState -> players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel().equals("array element\n" + 
						"I10 (16) AALOAD l185\n");	
				break;
			}
			case "I102 Branch 20 IF_ACMPNE L23":
			case "I188 Branch 28 IF_ACMPNE L58":
			case "I220 Branch 31 IF_ACMPNE L71": {
				// TODO
				assert true;
				break;
			}
			case "I308 Branch 39 IF_ACMPEQ L108": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"1\n");
				assert labelStrings.contains("parameter\n" + 
						"regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules\n" + 
						"checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/GameState;)Z\n" + 
						"2\n");
				
				// Action -> target, actor, action, other
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 4;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"target\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"actor\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Action\n" + 
						"I\n" + 
						"action\n");
				assert labelStrings.contains("other\n" + 
						"I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/Action.getTarget()I l6\n");
				
				// Action -> other -> other -> other -> boss
				assert topLayer.get(0).children.get(3).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).var.getUniqueLabel().equals("other\n" + 
						"I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/Player; l6\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).var.getUniqueLabel().equals("other\n" + 
						"I306 (427) ALOAD 4 l108\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/Player\n" + 
						"Lregression/objectconstruction/testgeneration/example/graphcontruction/Party;\n" + 
						"boss\n");
				
				// GameState -> players, gamestate, other
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 3;
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"[Lregression/objectconstruction/testgeneration/example/graphcontruction/Player;\n" + 
						"players\n");
				assert labelStrings.contains("instance field\n" + 
						"regression/objectconstruction/testgeneration/example/graphcontruction/GameState\n" + 
						"I\n" + 
						"gamestate\n");
				assert labelStrings.contains("other\n" + 
						"I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/Player; l6\n");
				
				// GameState -> players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel().equals("array element\n" + 
						"I10 (16) AALOAD l185\n");	
				
				break;
			}
			default: {
				// All branches have been covered
				assert false;
			}
			}
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
