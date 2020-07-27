package regression.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUtility;

import regression.objectconstruction.testgeneration.example.graphcontruction.BasicRules;

public class ComputationGraphGenerationTest {
	@Test
	public void testComputationGraphConstruction() throws ClassNotFoundException {
		Properties.CRITERION = new Criterion[] {Criterion.BRANCH};
		
		Properties.TARGET_CLASS = BasicRules.class.getCanonicalName();
		
		Method method = TestUtility.getTargetMethod("checkRules", BasicRules.class, 2);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		
		Properties.TARGET_METHOD = targetMethod;
		
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();
//		TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);
		
		List<String> classpath = new ArrayList<>();
		String cp = System.getProperty("user.dir") + "/target/test-classes";
		classpath.add(cp);
		ClassPathHandler.getInstance().addElementToTargetProjectClassPath(cp);
		
		Properties.APPLY_OBJECT_RULE = false;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));
		
		Dataflow.initializeDataflow();
		
		TestFactory testFactory = TestFactory.getInstance();
		ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
		
		for(Branch b: map.keySet()) {
			PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);
//			GraphVisualizer.visualizeComputationGraph(b, 10000);
			GraphVisualizer.visualizeComputationGraph(partialGraph, 5000);
		}
	}
}
