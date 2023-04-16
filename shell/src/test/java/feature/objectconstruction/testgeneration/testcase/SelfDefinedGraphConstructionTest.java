package feature.objectconstruction.testgeneration.testcase;

import common.TestUtility;
import feature.objectconstruction.testgeneration.example.set2.Target2;
import feature.objectconstruction.testgeneration.example.set3.Target3;
import feature.objectconstruction.testgeneration.example.set4.Target4;
import feature.objectconstruction.testgeneration.example.set5.Target5;
import feature.objectconstruction.testgeneration.example.set6.ListClass;
import feature.objectconstruction.testgeneration.example.set7.ArrayClass;
import feature.objectconstruction.testgeneration.example.set8.Complicated;
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
    public void testSet2() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = Target2.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("crossLayer", Target2.class, 1);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set2ComputationGraphTest");
        }
    }

    @Test
    public void testSet3() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = Target3.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("crossLayer", Target3.class, 1);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set3ComputationGraphTest");
        }
    }

    @Test
    public void testSet4() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = Target4.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("crossLayer", Target4.class, 1);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set4ComputationGraphTest");
        }
    }

    @Test
    public void testSet5() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = Target5.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("checkValueAtIndex5", Target5.class, 1);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set5ComputationGraphTest");
        }
    }

    @Test
    public void testSet6() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = ListClass.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("checkLength", ListClass.class, 0);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set6ComputationGraphTest");
        }
    }

    @Test
    public void testSet7() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = ArrayClass.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("checkValueAtIndex1", ArrayClass.class, 0);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set7ComputationGraphTest");
        }
    }

    @Test
    public void testSet8() throws ClassNotFoundException {

        setup();

        Properties.TARGET_CLASS = Complicated.class.getCanonicalName();

        Method method = TestUtility.getTargetMethod("check", Complicated.class, 0);
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
            GraphVisualizer.visualizeComputationGraph(partialGraph, 1000, "Self_set8ComputationGraphTest");
        }
    }

}
