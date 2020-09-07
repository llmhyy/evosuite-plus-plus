package regression.objectconstruction.testgeneration.testcase;

import java.io.File;
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
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.ga.metaheuristics.mosa.AbstractMOSA;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.ArrayStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.StringPrimitiveStatement;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectOrientedTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectOrientedTest.class);
	
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
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		BranchPool.getInstance(classLoader).reset();
	}
	
	protected TestCase generateCode(Branch b, boolean isDebugger) {
		TestFactory testFactory = TestFactory.getInstance();
		TestCase test = initializeTest(b, testFactory);
		try {
			ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
			cpSynthesizer.constructDifficultObjectStatement(test, b, isDebugger);
			mutateNullStatements(test);
			
			System.out.println(test);
			
//			PartialGraph graph = cpSynthesizer.getPartialGraph();
//			Map<DepVariable, List<VariableReference>> graph2CodeMap = cpSynthesizer.getGraph2CodeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("random seed is " + Randomness.getSeed());
		return test;
	}
	
	
	protected void mutateNullStatements(TestCase test) {
		for(int i=0; i<test.size(); i++) {
			Statement s = test.getStatement(i);
			if(s instanceof NullStatement) {
				TestFactory.getInstance().changeNullStatement(test, s);
				System.currentTimeMillis();
			}
		}
	}
	
	protected TestCase initializeTest(Branch b, TestFactory testFactory) {
		TestCase test = new DefaultTestCase();
		int success = -1;
		while (test.size() == 0 || success == -1) {
			test = new DefaultTestCase();
			success = testFactory.insertRandomStatement(test, 0);
			if(test.size() != 0 && success != -1) {
				mutateNullStatements(test);
			}
		}
		
		return test;
	}
	
	protected ArrayList<Branch> buildObjectConstructionGraph() throws ClassNotFoundException {
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
		return rankedList;
	}
	
	protected ArrayList<Branch> buildObjectConstructionGraph4SF100(List<String> classPaths) throws ClassNotFoundException {
		
//		String cp0 = ClassPathHandler.getInstance().getTargetProjectClasspath();

		Properties.APPLY_OBJECT_RULE = true;
		DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, classPaths);

		Map<Branch, Set<DepVariable>> interestedBranches = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
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
