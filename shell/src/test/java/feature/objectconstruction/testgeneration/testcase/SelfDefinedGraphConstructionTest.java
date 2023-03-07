package feature.objectconstruction.testgeneration.testcase;

import common.TestUtility;
import feature.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample;
import feature.objectconstruction.testgeneration.example.set1.Target;
import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static feature.objectconstruction.testgeneration.testcase.ObjectOrientedTest.setup;


public class SelfDefinedGraphConstructionTest {
    @Test
    public void testComputationGraphConstrutionSet1Method1() throws ClassNotFoundException {
        setup();

        Properties.TARGET_CLASS = CascadingCallExample.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("checkGrade", Target.class, 2);
        String targetMethod = method.getName() + MethodUtil.getSignature(method);

        Properties.TARGET_METHOD = targetMethod;

        ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
        String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();


        Properties.APPLY_OBJECT_RULE = true;
        DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));

//		TestFactory testFactory = TestFactory.getInstance();
        ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(false);
        Map<Branch, Set<DepVariable>> map = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);

        // For some reason, map is NULL here

        for (Branch b : map.keySet()) {
            PartialGraph partialGraph = cpSynthesizer.constructPartialComputationGraph(b);

            List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();

            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "set1method1");

            String branchName = partialGraph.getBranch().toString();
            System.out.println(branchName);
        }
    }
}
