package feature.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtility;
import feature.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample;
import feature.objectconstruction.testgeneration.example.graphcontruction.ArjArchiveEntry.isDirectory.ArjArchiveEntry;
import feature.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules;

public class ProjectGraphConstructionTest extends ObjectOrientedTest {

	public DepVariableWrapper getChild(List<DepVariableWrapper> children, String name) {
		for (DepVariableWrapper v : children) {
			if (v.var.getUniqueLabel().equals(name)) {
				return v;
			}
		}

		return null;
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

//		TestFactory testFactory = TestFactory.getInstance();
		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(false);
		Map<Branch, Set<DepVariable>> map = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);

		for (Branch b : map.keySet()) {
			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);

			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();

//			GraphVisualizer.visualizeComputationGraph(b, 10000);
			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest1");

			String branchName = partialGraph.getBranch().toString();
			System.out.println(branchName);
			Set<String> labelStrings = new HashSet<String>();

			switch (branchName) {
			case "I29 Branch 4 IF_ICMPNE L7": {
				// Parameters: GameState
				assert topLayer.size() == 1;
				assert topLayer.get(0).var.getUniqueLabel().equals("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// GameState -> gamestate
				assert topLayer.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				break;
			}
			case "I37 Branch 5 IFNULL L9": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor
				assert topLayer.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");

				// GameState -> players, gamestate
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 2;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");

				// GameState -> players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");
				break;
			}
			case "I42 Branch 6 IFNE L9": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget()
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 3;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");

				// other getActor() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(1).children.size() == 1;
				assert topLayer.get(0).children.get(1).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert topLayer.get(0).children.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// other getTarget() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(2).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			case "I50 Branch 7 IF_ICMPEQ L10":
			case "I56 Branch 8 IF_ICMPEQ L10": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget(), action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 4;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "action\n");

				// other getActor() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(1).children.size() == 1;
				assert topLayer.get(0).children.get(1).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert topLayer.get(0).children.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// other getTarget() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(2).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			case "I58 Branch 9 IFNULL L10":
			case "I63 Branch 10 IFEQ L10":
			case "I79 Branch 21 IF_ACMPNE L15":
			case "I134 Branch 26 IF_ACMPNE L37":
			case "I165 Branch 29 IF_ACMPNE L50":
			case "I197 Branch 32 IF_ACMPNE L63":
			case "I229 Branch 35 IF_ACMPNE L76":
			case "I262 Branch 38 IF_ACMPNE L89":
			case "I298 Branch 41 IF_ACMPNE L104": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget(), action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 5;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "action\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "target\n");

				// other getActor() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(2).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// other getTarget() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(3).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			case "I74 Branch 11 TABLESWITCH L13 Case 0":
			case "I74 Branch 12 TABLESWITCH L13 Case 1":
			case "I74 Branch 13 TABLESWITCH L13 Case 2":
			case "I74 Branch 14 TABLESWITCH L13 Case 3":
			case "I74 Branch 15 TABLESWITCH L13 Case 4":
			case "I74 Branch 16 TABLESWITCH L13 Case 5":
			case "I74 Branch 17 TABLESWITCH L13 Case 6":
			case "I74 Branch 18 TABLESWITCH L13 Case 7":
			case "I74 Branch 19 TABLESWITCH L13 Case 8":
			case "I74 Branch 20 TABLESWITCH L13 Default-Case":
			case "I125 Branch 25 IF_ICMPNE L33": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget(), action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 5;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "action\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "target\n");

				// other getActor() -> other gamestate.player() -> other ALOAD -> alive
				DepVariableWrapper getActor = getChild(topLayer.get(0).children, "other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert getActor.children.size() == 1;
				assert getActor.children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert getActor.children.get(0).children.size() == 1;
				assert getActor.children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert getActor.children.get(0).children.get(0).children.size() == 1;
				assert getActor.children.get(0).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// other getTarget() -> other gamestate.player() -> other ALOAD -> alive
				DepVariableWrapper getTarget = getChild(topLayer.get(0).children, "other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert getTarget.children.size() == 1;
				assert getTarget.children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert getTarget.children.get(0).children.size() == 1;
				assert getTarget.children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert getTarget.children.get(0).children.get(0).children.size() == 1;
				assert getTarget.children.get(0).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			case "I90 Branch 22 IFNE L19":
			case "I102 Branch 23 IF_ACMPNE L23":
			case "I113 Branch 24 IFEQ L28":
			case "I145 Branch 27 IFNE L41":
			case "I176 Branch 30 IFNE L54":
			case "I188 Branch 31 IF_ACMPNE L58":
			case "I273 Branch 39 IFNE L93": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget(), action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 5;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "action\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "target\n");

				// other getActor() -> other gamestate.player() ->
				// (other ALOAD -> alive, other ALOAD -> boss)
				DepVariableWrapper getActor = getChild(topLayer.get(0).children, "other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert getActor.children.size() == 1;
				assert getActor.children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert getActor.children.get(0).children.size() == 2;
				assert getActor.children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert getActor.children.get(0).children.get(1).var.getUniqueLabel()
						.equals("other\n" + "I86 (149) ALOAD 3 l19\n");
				assert getActor.children.get(0).children.get(0).children.size() == 1;
				assert getActor.children.get(0).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");
				assert getActor.children.get(0).children.get(1).children.size() == 1;
				assert getActor.children.get(0).children.get(1).children.get(0).var.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party\n"
								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party;\n"
								+ "boss\n");

				// other getTarget() -> other gamestate.player() ->
				// (other ALOAD -> alive, other ALOAD -> boss)
				DepVariableWrapper getTarget = getChild(topLayer.get(0).children, "other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert getTarget.children.size() == 1;
				assert getTarget.children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert getTarget.children.get(0).children.size() == 2;
				assert getTarget.children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert getTarget.children.get(0).children.get(1).var.getUniqueLabel()
						.equals("other\n" + "I152 (235) ALOAD 4 l45\n");
				assert getTarget.children.get(0).children.get(0).children.size() == 1;
				assert getTarget.children.get(0).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");
				assert getTarget.children.get(0).children.get(1).children.size() == 1;
				assert getTarget.children.get(0).children.get(1).children.get(0).var.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party\n"
								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party;\n"
								+ "boss\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			case "I156 Branch 28 IFNE L45":
			case "I208 Branch 33 IFNE L67":
			case "I220 Branch 34 IF_ACMPNE L71":
			case "I240 Branch 36 IFNE L80":
			case "I253 Branch 37 IFNE L84":
			case "I286 Branch 40 IFNE L97": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget(), action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 5;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "action\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "target\n");

				// other getActor() -> other gamestate.player() ->
				// (other ALOAD -> alive, other ALOAD -> boss)
				assert topLayer.get(0).children.get(2).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.size() == 2;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(1).var.getUniqueLabel()
						.equals("other\n" + "I86 (149) ALOAD 3 l19\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(1).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(1).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party\n"
								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party;\n"
								+ "boss\n");

				// other getTarget() -> other gamestate.player() ->
				// (other ALOAD -> alive, other ALOAD -> boss)
				assert topLayer.get(0).children.get(3).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.size() == 2;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(1).var.getUniqueLabel()
						.equals("other\n" + "I152 (235) ALOAD 4 l45\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(1).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(1).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party\n"
								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party;\n"
								+ "boss\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			case "I308 Branch 42 IF_ACMPEQ L108": {
				// Parameters: Action, GameState
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.size() == 2;
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "1\n");
				assert labelStrings.contains("parameter\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules.checkRules.BasicRules\n"
						+ "checkRules(Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action;Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState;)Z\n"
						+ "2\n");

				// Action -> actor, other getActor(), other getTarget(), action
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(0).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(0).children.size() == 5;
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "actor\n");
				assert labelStrings.contains("other\n"
						+ "I6 (8) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getActor()I l5\n");
				assert labelStrings.contains("other\n"
						+ "I17 (29) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action.getTarget()I l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "action\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Action\n"
						+ "I\n" + "target\n");

				// other getActor() -> other gamestate.player() -> other ALOAD -> alive
				assert topLayer.get(0).children.get(2).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I38 (63) ALOAD 3 l9\n");
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(2).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");

				// other getTarget() -> other gamestate.player() ->
				// (other ALOAD -> alive, other ALOAD -> boss)
				assert topLayer.get(0).children.get(3).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).var.getUniqueLabel().equals("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.size() == 2;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("other\n" + "I59 (110) ALOAD 4 l10\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(1).var.getUniqueLabel()
						.equals("other\n" + "I306 (427) ALOAD 4 l108\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(0).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Z\n" + "alive\n");
				assert topLayer.get(0).children.get(3).children.get(0).children.get(1).children.size() == 1;
				assert topLayer.get(0).children.get(3).children.get(0).children.get(1).children.get(0).var
						.getUniqueLabel()
						.equals("instance field\n"
								+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player\n"
								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Party;\n"
								+ "boss\n");

				// GameState -> other player(target), players, gamestate, other player(actor)
				labelStrings = new HashSet<String>();
				for (DepVariableWrapper v : topLayer.get(1).children) {
					labelStrings.add(v.var.getUniqueLabel());
				}
				assert topLayer.get(1).children.size() == 4;
				assert labelStrings.contains("other\n"
						+ "I20 (38) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l6\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "[Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player;\n"
						+ "players\n");
				assert labelStrings.contains("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState\n"
						+ "I\n" + "gamestate\n");
				assert labelStrings.contains("other\n"
						+ "I9 (17) INVOKEVIRTUAL regression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/GameState.player(I)Lregression/objectconstruction/testgeneration/example/graphcontruction/BasicRules/checkRules/Player; l5\n");

				// players -> array
				assert topLayer.get(1).children.get(0).children.size() == 1;
				assert topLayer.get(1).children.get(0).children.get(0).var.getUniqueLabel()
						.equals("array element\n" + "I10 (16) AALOAD l185\n");

				break;
			}
			default: {
				assert false;
			}
			}
		}
	}

//	@Test
//	public void testComputationGraphConstruction2() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = PngEncoderB.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("pngEncode", PngEncoderB.class, 1);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest2");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I41 Branch 4 IFNONNULL L14": {
//				// Instance: PngEncoderB
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.PngEncoderB\n");
//
////				for (DepVariableWrapper v : topLayer.get(0).children) {
////					System.out.println(v.var.getUniqueLabel());
////				}
//
//				// PngEncoder -> image
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel()
//						.equals("instance field\n"
//								+ "regression/objectconstruction/testgeneration/example/graphcontruction/PngEncoderB\n"
//								+ "Ljava/awt/image/BufferedImage;\n" + "image\n");
//				break;
//			}
//			case "I78 Branch 5 IFNE L19":
//			case "I138 Branch 6 IFEQ L27": {
//				// Instance: PngEncoderB
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.PngEncoderB\n");
//
//				// PngEncoder -> image
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel()
//						.equals("instance field\n"
//								+ "regression/objectconstruction/testgeneration/example/graphcontruction/PngEncoderB\n"
//								+ "Ljava/awt/image/BufferedImage;\n" + "image\n");
//
//				// TODO
//				// should have image -> other getRaster() (Line 37)
//				assert topLayer.get(0).children.get(0).children.size() > 0;
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction3() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = InternalGmHeroFrame.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("valueChanged", InternalGmHeroFrame.class, 1);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest3");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I14 Branch 7 IFEQ L20": {
//				// TODO
////				for (DepVariableWrapper v : topLayer) {
////					System.out.println(v.var.getUniqueLabel());
////				}
//
//				// Parameters: TreeSelectionEvent; Instance: InternalGmHeroFrame
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.size() == 2;
//				assert labelStrings.contains("parameter\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged.InternalGmHeroFrame\n"
//						+ "valueChanged(Ljavax/swing/event/TreeSelectionEvent;)V\n" + "1\n");
//				assert labelStrings.contains("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged.InternalGmHeroFrame\n");
//
//				// this InternalGmHeroFrame -> model
//				// TODO: should not have instance field tree
//				assert topLayer.get(1).children.size() == 1;
//
//				// model -> root
//				// TODO: should not have other getClass()
//				assert topLayer.get(1).children.get(0).children.size() == 1;
//
//				break;
//			}
//			case "I31 Branch 8 IFEQ L21": {
//				// TODO
//				break;
//			}
//			case "I40 Branch 9 IFLE L22": {
//				// TODO
//				break;
//			}
//			case "I78 Branch 10 IFGT L28": {
//				// Instance: InternalGmHeroFrame -> frameName
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged.InternalGmHeroFrame\n");
//
//				// InternalGmHeroFrame -> frameName
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/InternalGmHeroFrame/valueChanged/InternalGmHeroFrame\n"
//						+ "Ljava/lang/String;\n" + "frameName\n");
//			}
//			case "I96 Branch 11 IFEQ L30": {
//				// TODO
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction4() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = HandballModel.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("setMoveName", HandballModel.class, 1);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest4");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I10 Branch 1 IFNULL L46": {
//				// TODO
//				// Static class/method not shown
//				// should be Static Main -> window
//				assert topLayer.size() > 0;
//
//				break;
//			}
//			case "I31 Branch 2 IFNULL L49":
//			case "I37 Branch 3 IFLE L49": {
//				// TODO
//				// Static class/method not shown, primitive parameter not shown
//				// should be Static Main -> window, Parameter: paramString
//				assert topLayer.size() > 0;
//
//				break;
//			}
//			case "I63 Branch 4 IFNE L54": {
//				// TODO
//				// Static class/method not shown, instance methods not shown
//				// should be Static Main -> window, this.isSaved() -> this.lastSavedModel
//				assert topLayer.size() > 0;
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction5() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = Article.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("getRevisionId", Article.class, 0);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest5");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I7 Branch 1 IFEQ L27": {
//				// TODO: instance method not checked
//				// should be this Article -> bot, reload
//				assert topLayer.size() > 0;
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction6() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = ExpressionNodeList.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("addExpressionList", ExpressionNodeList.class, 1);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest6");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I21 Branch 1 IFNE L11": {
//				// Parameter: list
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("parameter\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ExpressionNodeList.addExpressionList.ExpressionNodeList\n"
//						+ "addExpressionList(Lregression/objectconstruction/testgeneration/example/graphcontruction/ExpressionNodeList/addExpressionList/ExpressionNodeList;)V\n"
//						+ "1\n");
//
//				// list -> other get(), list -> items
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 2;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ExpressionNodeList/addExpressionList/ExpressionNodeList\n"
//						+ "Ljava/util/ArrayList;\n" + "items\n");
//				assert labelStrings.contains(
//						"other\n" + "I18 (32) INVOKEVIRTUAL java/util/ArrayList.get(I)Ljava/lang/Object; l30\n");
//
//				// items -> size, items -> other get()
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.get(0).children.size() == 2;
//				assert labelStrings.contains("instance field\n" + "java/util/ArrayList\n" + "I\n" + "size\n");
//				assert labelStrings.contains(
//						"other\n" + "I18 (32) INVOKEVIRTUAL java/util/ArrayList.get(I)Ljava/lang/Object; l30\n");
//
//				// other get() -> other CHECKCAST -> value
//				assert topLayer.get(0).children.get(0).children.get(1).children.size() == 1;
//				assert topLayer.get(0).children.get(0).children.get(1).children.get(0).var.getUniqueLabel()
//						.equals("other\n" + "I19 (35) CHECKCAST java/lang/Integer l30\n");
//				assert topLayer.get(0).children.get(0).children.get(1).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).children.get(1).children.get(0).children.get(0).var
//						.getUniqueLabel().equals("instance field\n" + "java/lang/Integer\n" + "I\n" + "value\n");
//				break;
//			}
//			case "I38 Branch 2 IF_ICMPLT L9": {
//				// Parameter: list -> items -> size
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("parameter\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ExpressionNodeList.addExpressionList.ExpressionNodeList\n"
//						+ "addExpressionList(Lregression/objectconstruction/testgeneration/example/graphcontruction/ExpressionNodeList/addExpressionList/ExpressionNodeList;)V\n"
//						+ "1\n");
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ExpressionNodeList/addExpressionList/ExpressionNodeList\n"
//						+ "Ljava/util/ArrayList;\n" + "items\n");
//				assert topLayer.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).children.get(0).var.getUniqueLabel()
//						.equals("instance field\n" + "java/util/ArrayList\n" + "I\n" + "size\n");
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction7() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = RMIManagedConnectionAcceptor.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("close", RMIManagedConnectionAcceptor.class, 0);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest7");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I4 Branch 5 IFNULL L17": {
//				// this RMIManagedConnectionAcceptor -> _registry
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close.RMIManagedConnectionAcceptor\n");
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIManagedConnectionAcceptor\n"
//						+ "Ljava/rmi/registry/Registry;\n" + "_registry\n");
//
//				break;
//			}
//			case "I24 Branch 6 IFNE L20": {
//				// this RMIManagedConnectionAcceptor -> _registry, _factory
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close.RMIManagedConnectionAcceptor\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 2;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIManagedConnectionAcceptor\n"
//						+ "Ljava/rmi/registry/Registry;\n" + "_registry\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIManagedConnectionAcceptor\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIInvokerFactory;\n"
//						+ "_factory\n");
//
//				break;
//			}
//			case "I36 Branch 7 IFEQ L22":
//			case "I42 Branch 8 IFNE L22":
//			case "I51 Branch 9 IFNE L23": {
//				// this RMIManagedConnectionAcceptor -> _registry, _factory, _created
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close.RMIManagedConnectionAcceptor\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 3;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIManagedConnectionAcceptor\n"
//						+ "Ljava/rmi/registry/Registry;\n" + "_registry\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIManagedConnectionAcceptor\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIInvokerFactory;\n"
//						+ "_factory\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/RMIManagedConnectionAcceptor/close/RMIManagedConnectionAcceptor\n"
//						+ "Z\n" + "_created\n");
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction8() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = MUXFilter.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("pump", MUXFilter.class, 0);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest8");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I6 Branch 1 IFNE L16": {
////				for (DepVariableWrapper v : topLayer.get(0).children) {
////				System.out.println(v.var.getUniqueLabel());
////			}
//				// TODO: instance method not checked
//				// should be this MUXFilter -> availablePayload, eofReached, outqueue
//				assert topLayer.size() > 0;
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}
//
//	@Test
//	public void testComputationGraphConstruction9() throws ClassNotFoundException {
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest9");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			// The branch number is different when you execute this test only or with
//			// multiple tests, not sure why
//			if (branchName.endsWith("IFLE L29")) {
//				// this FTPSender -> mConfiguration -> mResolution
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute.FTPSender\n");
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration;\n"
//						+ "mConfiguration\n");
//				assert topLayer.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration\n"
//						+ "Ljava/lang/String;\n" + "mResolution\n");
//			} else if (branchName.endsWith("IFEQ L30") || branchName.endsWith("IFLE L37")
//					|| branchName.endsWith("IFEQ L38")) {
//				// this FTPSender -> mConfiguration, mFTPConnection, mWorkDir, mFileName
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute.FTPSender\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 4;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration;\n"
//						+ "mConfiguration\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPConnection;\n"
//						+ "mFTPConnection\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mWorkDir\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mFileName\n");
//
//				// mConfiguration -> mResolution
//				assert topLayer.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration\n"
//						+ "Ljava/lang/String;\n" + "mResolution\n");
//
//				// mFTPConnection -> mFTPClient -> other listNames() -> other ALOAD -> array
//				assert topLayer.get(0).children.get(1).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPConnection\n"
//						+ "Lorg/apache/commons/net/ftp/FTPClient;\n" + "mFTPClient\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).var.getUniqueLabel()
//						.equals("other\n"
//								+ "I14 (22) INVOKEVIRTUAL org/apache/commons/net/ftp/FTPClient.listNames(Ljava/lang/String;)[Ljava/lang/String; l133\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).var
//						.getUniqueLabel().equals("other\n" + "I25 (37) ALOAD 3 l134\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).children
//						.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).children
//						.get(0).var.getUniqueLabel().equals("array element\n" + "I27 (39) AALOAD l134\n");
//
//				// mWorkDir -> other listNames()
//				assert topLayer.get(0).children.get(2).children.size() == 1;
//				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
//						+ "I14 (22) INVOKEVIRTUAL org/apache/commons/net/ftp/FTPClient.listNames(Ljava/lang/String;)[Ljava/lang/String; l133\n");
//			} else if (branchName.endsWith("IFEQ L35")) {
//				// this FTPSender
//				// -> mConfiguration, mFTPConnection, mWorkDir, mFileName, mTmpFileName
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute.FTPSender\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 5;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration;\n"
//						+ "mConfiguration\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPConnection;\n"
//						+ "mFTPConnection\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mWorkDir\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mFileName\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mTmpFileName\n");
//
//				// mConfiguration -> mResolution
//				assert topLayer.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration\n"
//						+ "Ljava/lang/String;\n" + "mResolution\n");
//
//				// mFTPConnection -> mFTPClient -> other listNames() -> other ALOAD -> array
//				assert topLayer.get(0).children.get(1).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPConnection\n"
//						+ "Lorg/apache/commons/net/ftp/FTPClient;\n" + "mFTPClient\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).var.getUniqueLabel()
//						.equals("other\n"
//								+ "I14 (22) INVOKEVIRTUAL org/apache/commons/net/ftp/FTPClient.listNames(Ljava/lang/String;)[Ljava/lang/String; l133\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).var
//						.getUniqueLabel().equals("other\n" + "I25 (37) ALOAD 3 l134\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).children
//						.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).children
//						.get(0).var.getUniqueLabel().equals("array element\n" + "I27 (39) AALOAD l134\n");
//
//				// mWorkDir -> other listNames()
//				assert topLayer.get(0).children.get(2).children.size() == 1;
//				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
//						+ "I14 (22) INVOKEVIRTUAL org/apache/commons/net/ftp/FTPClient.listNames(Ljava/lang/String;)[Ljava/lang/String; l133\n");
//			} else if (branchName.endsWith("IFLE L41") || branchName.endsWith("IF_ICMPEQ L42")) {
//				// this FTPSender -> mConfiguration, mFTPConnection, mWorkDir, mFileName
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute.FTPSender\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 4;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration;\n"
//						+ "mConfiguration\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPConnection;\n"
//						+ "mFTPConnection\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mWorkDir\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPSender\n"
//						+ "Ljava/lang/String;\n" + "mFileName\n");
//
//				// mConfiguration -> mResolution, mEncoding
//				// TODO: should have mConfiguration -> mEncoding
//				assert topLayer.get(0).children.get(0).children.size() == 2;
//				assert topLayer.get(0).children.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FileSenderConfiguration\n"
//						+ "Ljava/lang/String;\n" + "mResolution\n");
//
//				// mFTPConnection -> mFTPClient -> other listNames() -> other ALOAD -> array
//				assert topLayer.get(0).children.get(1).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/FTPSender/execute/FTPConnection\n"
//						+ "Lorg/apache/commons/net/ftp/FTPClient;\n" + "mFTPClient\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).var.getUniqueLabel()
//						.equals("other\n"
//								+ "I14 (22) INVOKEVIRTUAL org/apache/commons/net/ftp/FTPClient.listNames(Ljava/lang/String;)[Ljava/lang/String; l133\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).var
//						.getUniqueLabel().equals("other\n" + "I25 (37) ALOAD 3 l134\n");
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).children
//						.size() == 1;
//				assert topLayer.get(0).children.get(1).children.get(0).children.get(0).children.get(0).children
//						.get(0).var.getUniqueLabel().equals("array element\n" + "I27 (39) AALOAD l134\n");
//
//				// mWorkDir -> other listNames()
//				assert topLayer.get(0).children.get(2).children.size() == 1;
//				assert topLayer.get(0).children.get(2).children.get(0).var.getUniqueLabel().equals("other\n"
//						+ "I14 (22) INVOKEVIRTUAL org/apache/commons/net/ftp/FTPClient.listNames(Ljava/lang/String;)[Ljava/lang/String; l133\n");
//			} else {
//				assert false;
//			}
//		}
//	}

	@Test
	public void testComputationGraphConstruction10() throws ClassNotFoundException {
		setup();

		Properties.TARGET_CLASS = ArjArchiveEntry.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("isDirectory", ArjArchiveEntry.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();

		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));

//		TestFactory testFactory = TestFactory.getInstance();
		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(false);
		Map<Branch, Set<DepVariable>> map = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);

		for (Branch b : map.keySet()) {
			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);

			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();

			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest10");

			String branchName = partialGraph.getBranch().toString();
			System.out.println(branchName);

			switch (branchName) {
			case "I6 Branch 1 IF_ICMPNE L7": {
				// this ArjArchiveEntry -> localFileHeader -> fileType
				assert topLayer.size() == 1;
				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ArjArchiveEntry.isDirectory.ArjArchiveEntry\n");
				assert topLayer.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ArjArchiveEntry/isDirectory/ArjArchiveEntry\n"
						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ArjArchiveEntry/isDirectory/LocalFileHeader;\n"
						+ "localFileHeader\n");
				assert topLayer.get(0).children.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ArjArchiveEntry/isDirectory/LocalFileHeader\n"
						+ "I\n" + "fileType\n");

				break;
			}
			default: {
				assert false;
			}
			}
		}
	}

	@Test
	public void testComputationGraphConstruction11() throws ClassNotFoundException {
		setup();

		Properties.TARGET_CLASS = CascadingCallExample.class.getCanonicalName();

		Method method = TestUtility.getTargetMethod("targetM", CascadingCallExample.class, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();

		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));

//		TestFactory testFactory = TestFactory.getInstance();
		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer( false);
		Map<Branch, Set<DepVariable>> map = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);

		for (Branch b : map.keySet()) {
			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);

			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();

			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest11");

			String branchName = partialGraph.getBranch().toString();
			System.out.println(branchName);

			switch (branchName) {
			case "I5 Branch 1 IF_ICMPLE L16": {
				// this CascadingCallExample -> fieldToSet
				assert topLayer.size() == 1;
				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
						+ "regression.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample\n");
				assert topLayer.get(0).children.size() == 1;
				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
						+ "regression/objectconstruction/testgeneration/example/cascadecall/CascadingCallExample\n"
						+ "I\n" + "fieldToSet\n");

				break;
			}
			default: {
				assert false;
			}
			}
		}
	}

//	@Test
//	public void testComputationGraphConstruction12() throws ClassNotFoundException {
//		setup();
//
//		Properties.TARGET_CLASS = ChkOrdAudRs_TypeSequence2.class.getCanonicalName();
//
//		Method method = TestUtility.getTargetMethod("equals", ChkOrdAudRs_TypeSequence2.class, 1);
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
//		TestFactory testFactory = TestFactory.getInstance();
//		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
//		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
//
//		for (Branch b : map.keySet()) {
//			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//
//			List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
//
//			GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "computationGraphTest12");
//
//			String branchName = partialGraph.getBranch().toString();
//			System.out.println(branchName);
//			Set<String> labelStrings = new HashSet<String>();
//
//			switch (branchName) {
//			case "I4 Branch 37 IF_ACMPNE L12":
//			case "I16 Branch 38 IFNE L14":
//			case "I25 Branch 39 IFEQ L16": {
//				// TODO
//				// should have Parameter: obj
////				assert topLayer.size() > 0;
//				break;
//			}
//			case "I35 Branch 40 IFNULL L18":
//			case "I40 Branch 41 IFNONNULL L19":
//			case "I63 Branch 43 IFNULL L23": {
//				// this ChkOrdAudRs_TypeSequence2 -> _recCtrlOut
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals.ChkOrdAudRs_TypeSequence2\n");
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//
//				break;
//			}
//			case "I54 Branch 42 IFNE L21": {
//				// this ChkOrdAudRs_TypeSequence2 -> _recCtrlOut
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals.ChkOrdAudRs_TypeSequence2\n");
//				assert topLayer.get(0).children.size() == 1;
//				assert topLayer.get(0).children.get(0).var.getUniqueLabel().equals("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//
//				// _recCtrlOut
//				// -> _matchedRec, _has_matchedRec, _sentRec, _has_sentRec, _cursor
//				for (DepVariableWrapper v : topLayer.get(0).children.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.get(0).children.size() == 5;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/Cursor;\n"
//						+ "_cursor\n");
//
//				break;
//			}
//			case "I72 Branch 44 IFNULL L26":
//			case "I77 Branch 45 IFNONNULL L27":
//			case "I100 Branch 47 IFNULL L31": {
//				// this ChkOrdAudRs_TypeSequence2
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals.ChkOrdAudRs_TypeSequence2\n");
//
//				// this -> _recCtrlOut, _chkOrdAudRs_TypeSequence2Sequence
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 2;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence;\n"
//						+ "_chkOrdAudRs_TypeSequence2Sequence\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//
//				// _recCtrlOut
//				// -> _matchedRec, _has_matchedRec, _sentRec, _has_sentRec, _cursor
//				for (DepVariableWrapper v : topLayer.get(0).children.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.get(0).children.size() == 5;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/Cursor;\n"
//						+ "_cursor\n");
//
//				break;
//			}
//			case "I91 Branch 46 IFNE L29": {
//				// this ChkOrdAudRs_TypeSequence2
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals.ChkOrdAudRs_TypeSequence2\n");
//
//				// this -> _recCtrlOut, _chkOrdAudRs_TypeSequence2Sequence
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 2;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence;\n"
//						+ "_chkOrdAudRs_TypeSequence2Sequence\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//
//				// _recCtrlOut
//				// -> _matchedRec, _has_matchedRec, _sentRec, _has_sentRec, _cursor
//				DepVariableWrapper _recCtrlOut = getChild(topLayer.get(0).children, "instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//				for (DepVariableWrapper v : _recCtrlOut.children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert _recCtrlOut.children.size() == 5;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/Cursor;\n"
//						+ "_cursor\n");
//
//				// _chkOrdAudRs_TypeSequence2Sequence
//				// -> _selRangeDt, _methodList, _chkOrdIdList, _recChkOrdIdList
//				DepVariableWrapper _chkOrdAudRs_TypeSequence2Sequence = getChild(topLayer.get(0).children,
//						"instance field\n"
//								+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence;\n"
//								+ "_chkOrdAudRs_TypeSequence2Sequence\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : _chkOrdAudRs_TypeSequence2Sequence.children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert _chkOrdAudRs_TypeSequence2Sequence.children.size() == 4;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/SelRangeDt;\n"
//						+ "_selRangeDt\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Ljava/util/ArrayList;\n" + "_methodList\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Ljava/util/ArrayList;\n" + "_chkOrdIdList\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Ljava/util/ArrayList;\n" + "_recChkOrdIdList\n");
//
//				break;
//			}
//			case "I109 Branch 48 IFNULL L34":
//			case "I114 Branch 49 IFNONNULL L35":
//			case "I128 Branch 50 IFNE L37":
//			case "I137 Branch 51 IFNULL L39": {
//				// this ChkOrdAudRs_TypeSequence2
//				assert topLayer.size() == 1;
//				assert topLayer.get(0).var.getUniqueLabel().equals("this\n"
//						+ "regression.objectconstruction.testgeneration.example.graphcontruction.ChkOrdAudRs_TypeSequence2.equals.ChkOrdAudRs_TypeSequence2\n");
//
//				// this -> _recCtrlOut, _chkOrdAudRs_TypeSequence2Sequence,
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : topLayer.get(0).children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert topLayer.get(0).children.size() == 3;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence;\n"
//						+ "_chkOrdAudRs_TypeSequence2Sequence\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Ljava/util/ArrayList;\n" + "_chkOrdMsgRecList\n");
//
//				// _recCtrlOut
//				// -> _matchedRec, _has_matchedRec, _sentRec, _has_sentRec, _cursor
//				DepVariableWrapper _recCtrlOut = getChild(topLayer.get(0).children, "instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut;\n"
//						+ "_recCtrlOut\n");
//				for (DepVariableWrapper v : _recCtrlOut.children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert _recCtrlOut.children.size() == 5;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_matchedRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "J\n" + "_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Z\n" + "_has_sentRec\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/RecCtrlOut\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/Cursor;\n"
//						+ "_cursor\n");
//
//				// _chkOrdAudRs_TypeSequence2Sequence
//				// -> _selRangeDt, _methodList, _chkOrdIdList, _recChkOrdIdList
//				DepVariableWrapper _chkOrdAudRs_TypeSequence2Sequence = getChild(topLayer.get(0).children,
//						"instance field\n"
//								+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2\n"
//								+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence;\n"
//								+ "_chkOrdAudRs_TypeSequence2Sequence\n");
//				labelStrings = new HashSet<String>();
//				for (DepVariableWrapper v : _chkOrdAudRs_TypeSequence2Sequence.children) {
//					labelStrings.add(v.var.getUniqueLabel());
//				}
//				assert _chkOrdAudRs_TypeSequence2Sequence.children.size() == 4;
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Lregression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/SelRangeDt;\n"
//						+ "_selRangeDt\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Ljava/util/ArrayList;\n" + "_methodList\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Ljava/util/ArrayList;\n" + "_chkOrdIdList\n");
//				assert labelStrings.contains("instance field\n"
//						+ "regression/objectconstruction/testgeneration/example/graphcontruction/ChkOrdAudRs_TypeSequence2/equals/ChkOrdAudRs_TypeSequence2Sequence\n"
//						+ "Ljava/util/ArrayList;\n" + "_recChkOrdIdList\n");
//
//				break;
//			}
//			default: {
//				assert false;
//			}
//			}
//		}
//	}

}
