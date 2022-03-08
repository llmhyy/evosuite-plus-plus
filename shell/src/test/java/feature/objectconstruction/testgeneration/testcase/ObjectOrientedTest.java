package feature.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.runtime.LoopCounter;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.setup.ExceptionMapGenerator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer0;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.TestUtil;


public class ObjectOrientedTest {

	public static final Logger logger = LoggerFactory.getLogger(ObjectOrientedTest.class);

	public static void setup() {
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		Properties.ASSERTIONS = false;
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.ADOPT_SMART_MUTATION = true;
		Properties.P_FUNCTIONAL_MOCKING = 0;
		Properties.MOCK_IF_NO_GENERATOR = false;
		Properties.FUNCTIONAL_MOCKING_PERCENT = 0;
		Properties.PRIMITIVE_POOL = 0.5;
		Properties.DYNAMIC_POOL = 0.5;
		Properties.ASSERTIONS = false;

		Properties.TIMEOUT = 300000000;

		// Clear all branches so that branch numbers do not change
//		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
//		BranchPool.getInstance(classLoader).reset();
	}

	protected TestCase generateCode(Branch b, boolean isDebugger, boolean allowNullValue) {
		TestFactory testFactory = TestFactory.getInstance();
		TestCase test = TestUtil.initializeTest(b, testFactory, allowNullValue);
		try {
			ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(isDebugger);
			cpSynthesizer.buildNodeStatementCorrespondence(test, b, allowNullValue);
			if(!allowNullValue) {
				TestUtil.mutateNullStatements(test);				
			}

			System.out.println(test);

//			PartialGraph graph = cpSynthesizer.getPartialGraph();
			Map<DepVariableWrapper, VarRelevance> graph2CodeMap = cpSynthesizer.getGraph2CodeMap();
			
			for(DepVariableWrapper node: graph2CodeMap.keySet()) {
				System.out.println("key node: " + node.var.getInstruction());
				
				VarRelevance rel = graph2CodeMap.get(node);
				
				for(VariableReference variable: rel.matchedVars) {					
					System.out.println("  variable: " + test.getStatement(variable.getStPosition()) + 
							" at statement " + variable.getStPosition());					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("random seed is " + Randomness.getSeed());
		return test;
	}

	protected void assertLegitimization(Branch b, boolean isDebug, boolean allowNullValue) {
		if (Properties.APPLY_OBJECT_RULE) {
			Properties.PRIMITIVE_REUSE_PROBABILITY = 0;
			System.currentTimeMillis();
		}

		TestFactory testFactory = TestFactory.getInstance();
		TestCase test = TestUtil.initializeTest(b, testFactory, allowNullValue);
		try {
			long t1 = System.currentTimeMillis();
			ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(isDebug);
			cpSynthesizer.buildNodeStatementCorrespondence(test, b, allowNullValue);
			if(!allowNullValue) {
				TestUtil.mutateNullStatements(test);		
				System.currentTimeMillis();
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Time to generate code template: " + (t2-t1)/1000 + "s");
			
			PartialGraph graph = cpSynthesizer.getPartialGraph();
			Map<DepVariableWrapper, VarRelevance> graph2CodeMap = cpSynthesizer.getGraph2CodeMap();

			TestChromosome templateTestChromosome = new TestChromosome();
			templateTestChromosome.setTestCase(test);
			ExecutionResult result = TestCaseExecutor.getInstance().execute(test);
			templateTestChromosome.setLastExecutionResult(result);
			
			double legitimacyDistance = templateTestChromosome.getLegitimacyDistance();
			if(legitimacyDistance == 0) {
				System.out.println("****no need to legitimize");
			}
			else {
				System.out.println("****start legitimization");
			}

			TestChromosome chromosome = TestCaseLegitimizer.getInstance().legitimize(templateTestChromosome, graph, graph2CodeMap);
			System.out.println(chromosome.getTestCase());

			System.out.println("random seed is " + Randomness.getSeed());
			System.out.println("legitimacy distance is " + chromosome.getLegitimacyDistance());
			assert chromosome.getLegitimacyDistance() == 0;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("random seed is " + Randomness.getSeed());
			assert false;
		}

		if (Properties.APPLY_OBJECT_RULE) {
			Properties.PRIMITIVE_REUSE_PROBABILITY = 0.5;
		}

	}

	protected ArrayList<Branch> buildObjectConstructionGraph() throws ClassNotFoundException {
		ClassPathHandler.getInstance().changeTargetCPtoTheSameAsEvoSuite();
		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();

		Properties.APPLY_OBJECT_RULE = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp0.split(File.pathSeparator)));

		Map<Branch, Set<DepVariable>> interestedBranches = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		ArrayList<Branch> rankedList = new ArrayList<>(interestedBranches.keySet());
		Collections.sort(rankedList, new Comparator<Branch>() {
			@Override
			public int compare(Branch o1, Branch o2) {
				return o1.getInstruction().getLineNumber() - o2.getInstruction().getLineNumber();
			}
		});
		return rankedList;
	}

	protected ArrayList<Branch> buildObjectConstructionGraph4SF100(List<String> classPaths)
			throws ClassNotFoundException {

//		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();

		// Deactivate loop counter to make sure classes initialize properly
		LoopCounter.getInstance().setActive(false);
		ExceptionMapGenerator.initializeExceptionMap(Properties.TARGET_CLASS);

		TestCaseExecutor.initExecutor();
		
		ClassLoader loader = TestGenerationContext.getInstance().getClassLoaderForSUT().getClassLoader();
		Class<?> c = Class.forName("sun.misc.Launcher$AppClassLoader");
		try {
			Field f = c.getDeclaredField("ucp");
			f.setAccessible(true);
			Object o = f.get(loader);
			sun.misc.URLClassPath path = (sun.misc.URLClassPath)o;
			for(String classpath: classPaths) {
				path.addURL(new File(classpath).toURI().toURL());
			}
			
			System.currentTimeMillis();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Properties.APPLY_OBJECT_RULE = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, classPaths);

		Map<Branch, Set<DepVariable>> interestedBranches = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		ArrayList<Branch> rankedList = new ArrayList<>(interestedBranches.keySet());
		Collections.sort(rankedList, new Comparator<Branch>() {
			@Override
			public int compare(Branch o1, Branch o2) {
				return o1.getInstruction().getLineNumber() - o2.getInstruction().getLineNumber();
			}
		});
		return rankedList;
	}
	
}
