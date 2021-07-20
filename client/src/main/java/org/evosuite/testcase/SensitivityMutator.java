package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.mosa.AbstractMOSA;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.seeding.RuntimeSensitiveVariable;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.statements.ArrayStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.DepVariableWrapper;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;

public class SensitivityMutator {
	public static List<List<Object>> data = new ArrayList<List<Object>>();
	public static Object HeadValue;
	public static Object TailValue;
//	public static int iter = 0;
	public static String projectId;
	public static String className;
	public static String methodName;

	public static TestCase initializeTest(TestFactory testFactory, boolean allowNullValue) {
		TestCase test = new DefaultTestCase();
		int success = -1;
		long t1 = System.currentTimeMillis();
		while (test.size() == 0 || success == -1) {
			long t2 = System.currentTimeMillis();
			if ((t2 - t1) / 1000 > 10)
				return null;
			test = new DefaultTestCase();
			success = testFactory.insertRandomStatement(test, 0);
			if (test.size() != 0 && success != -1 && !allowNullValue) {
				mutateNullStatements(test);
			}
		}

		return test;
	}

	public static void mutateNullStatements(TestCase test) {
		for (int i = 0; i < test.size(); i++) {
			Statement s = test.getStatement(i);
			if (s instanceof NullStatement) {
				TestFactory.getInstance().changeNullStatement(test, s);
				System.currentTimeMillis();
			}
		}
	}

	public static void testSensitity(Set<FitnessFunction<?>> fitness)
			throws ClassNotFoundException, ConstructionFailedException {
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		ComputationPath path = null;
		for (Branch branch : branchesInTargetMethod.keySet()) {
			testBranchSensitivity(branchesInTargetMethod, branch, path);
		}

		System.currentTimeMillis();
	}

	public static SensitivityPreservance testBranchSensitivity(ComputationPath path) {
		Branch branch = path.getBranch();

		TestCase test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		if (test == null) {
			return new SensitivityPreservance(0, 0);
		}
		String methodCall = test.getStatement(test.size() - 1).toString();
		while (!methodCall.equals(Properties.TARGET_METHOD)) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
			methodCall = test.getStatement(test.size() - 1).toString();
		}
		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);

		SensitivityPreservance preservingList = new SensitivityPreservance(0, 0);

		TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
		DepVariable rootVariable = path.getComputationNodes().get(0);
		BytecodeInstruction tailInstruction = path.getRelevantTailInstruction();

		List<BytecodeInstruction> observations = new ArrayList<>();
		observations.add(tailInstruction);

		List<DepVariable> vars = new ArrayList<>();
		vars.add(rootVariable);
		preservingList = checkPreservance(branch, newTestChromosome, vars, observations, synthensizer);
		return preservingList;
	}

	public static SensitivityPreservance testBranchSensitivity(List<DepVariable> rootVariables,
			List<BytecodeInstruction> observingValues, Branch branch) {
		TestCase test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		if (test == null) {
			return new SensitivityPreservance(0, 0);
		}
		String methodCall = test.getStatement(test.size() - 1).toString();
		while (!methodCall.equals(Properties.TARGET_METHOD)) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
			methodCall = test.getStatement(test.size() - 1).toString();
		}

		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);

//		SensitivityPreservance preservingList = new SensitivityPreservance();
		System.currentTimeMillis();
		TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();

		SensitivityPreservance preservingList = checkPreservance(branch, newTestChromosome, rootVariables,
				observingValues, synthensizer);
		
		System.currentTimeMillis();
		
		return preservingList;
	}

	private static SensitivityPreservance checkPreservance(Branch branch, TestChromosome newTestChromosome,
			List<DepVariable> rootVariables, List<BytecodeInstruction> observations,
			ConstructionPathSynthesizer synthensizer) {
		System.currentTimeMillis();
		SensitivityPreservance preservance = new SensitivityPreservance(observations.size(), rootVariables.size());
		
		Map<DepVariableWrapper, List<VariableReference>> map = synthensizer.getGraph2CodeMap();

		for (int i = 0; i < Properties.DYNAMIC_SENSITIVITY_THRESHOLD; i++) {

//			long t1 = System.currentTimeMillis();
			Map<String, List<Object>> observationMap = evaluateObservations(branch, observations, newTestChromosome);
			Map<String, Object> recordInput = constructInputValues(rootVariables, newTestChromosome, map);
			Map<String, Boolean> InputConstant = constructInputType(rootVariables, newTestChromosome, map);
//			long t2 = System.currentTimeMillis();
//			AbstractMOSA.getFirstTailValueTime += t2 - t1;

			ObservationRecord record = new ObservationRecord(recordInput, observationMap, InputConstant);
			preservance.addRecord(record);
			
			//TODO Cheng Yan refactor this, now the obsevation will always have a complete size
			if (observationIsNull(observationMap) && i == 1) {
				if (observationIsNull(preservance.recordList.get(0).observations)) {
					System.out.println("observations is null!");
					return preservance;
				}
			}
		}

		return preservance;
	}

	private static  boolean observationIsNull(Map<String, List<Object>> observationMap) {
		for(String s : observationMap.keySet()) {
			if(observationMap.get(s).size() > 0)
				return false;
		}
		return true;
	}

	private static Map<String, Boolean> constructInputType(List<DepVariable> rootVariables,
			TestChromosome newTestChromosome, Map<DepVariableWrapper, List<VariableReference>> map) {
		Map<String, Boolean> m = new HashMap<>();
		for (DepVariable var : rootVariables) {
			m.put(var.getInstruction().toString(), var.getInstruction().isConstant());
		}

		return m;
	}

	private static Map<String, Object> constructInputValues(List<DepVariable> rootVariables,
			TestChromosome newTestChromosome, Map<DepVariableWrapper, List<VariableReference>> map) {
		Map<String, Object> m = new HashMap<>();
		/**
		 * TODO for Cheng Yan, need to change it to multiple heads
		 */
		for (DepVariable var : rootVariables) {
			Statement relevantStatement = locateRelevantStatement(var, newTestChromosome, map);
			Object headValue = retrieveHeadValue(relevantStatement);
			/**
			 * TODO what if the head value is null?
			 */
			if (headValue == null) {
				headValue = "N/A";
			}
			if (relevantStatement != null) {
				relevantStatement.mutate(newTestChromosome.getTestCase(), TestFactory.getInstance());
			}

			m.put(var.getInstruction().toString(), headValue);
			System.currentTimeMillis();
		}

		return m;
	}

	public static SensitivityPreservance testBranchSensitivity(/* Set<FitnessFunction<?>> fitness, */
			Map<Branch, Set<DepVariable>> branchesInTargetMethod, Branch branch, ComputationPath path0) {
		TestCase test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		if (test == null) {
			return new SensitivityPreservance(0, 0);
		}

		System.currentTimeMillis();
		String methodCall = test.getStatement(test.size() - 1).toString();
		while (!methodCall.equals(Properties.TARGET_METHOD)) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
			methodCall = test.getStatement(test.size() - 1).toString();
		}
		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);
//		oldTestChromosome.setTestCase(test);
//		for (FitnessFunction<?> ff : fitness) {
//			oldTestChromosome.addFitness(ff);
//		}

		Map<Branch, List<ComputationPath>> branchWithBDChanged = parseComputationPaths(branch, branchesInTargetMethod);

		List<ComputationPath> paths = new ArrayList<ComputationPath>();
		if (path0 != null) {
			paths.add(path0);
		} else {
			paths = branchWithBDChanged.get(branch);
		}

		SensitivityPreservance preservingList = new SensitivityPreservance(0, 0);
		if (paths == null) {
			return preservingList;
		}

		// Favor 'field' root variables
		List<ComputationPath> sortedPaths = new ArrayList<ComputationPath>();
		for (ComputationPath path : paths) {
			DepVariable rootVariable = path.getComputationNodes().get(0);
			if (rootVariable.isInstaceField() || rootVariable.isStaticField()) {
				sortedPaths.add(0, path);
			} else {
				sortedPaths.add(path);
			}
		}
		paths = sortedPaths;

		for (ComputationPath path : paths) {
			TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
			DepVariable rootVariable = path.getComputationNodes().get(0);
			BytecodeInstruction tailInstruction = path.getRelevantTailInstruction();

			List<BytecodeInstruction> observations = new ArrayList<>();
			observations.add(tailInstruction);
			List<DepVariable> vars = new ArrayList<>();
			vars.add(rootVariable);
			preservingList = checkPreservance(branch, newTestChromosome, vars, observations, synthensizer);
//			if (preservingList.isSensitivityPreserving() || preservingList.isValuePreserving()) {
//				return preservingList;
//			}
		}

		return preservingList;
	}

//	private static SensitivityPreservance testHeadTailValue(Branch branch, TestChromosome newTestChromosome, DepVariable rootVariable,
//			BytecodeInstruction tailInstruction, ConstructionPathSynthesizer synthensizer) {
//		SensitivityPreservance preservance = new SensitivityPreservance();
//		Map<DepVariableWrapper, List<VariableReference>> map = synthensizer.getGraph2CodeMap();
//		Statement relevantStatement = locateRelevantStatement(rootVariable, newTestChromosome, map);
//		Object headValue = retrieveHeadValue(relevantStatement);
//		long t1 = System.currentTimeMillis();
//		Object tailValue = evaluateTailValue(branch, tailInstruction, newTestChromosome);
//		long t2 = System.currentTimeMillis();
//		AbstractMOSA.getFirstTailValueTime += t2 - t1;
//		boolean valuePreserving = checkValuePreserving(headValue, tailValue);
//
//		HeadValue = headValue;
//		TailValue = tailValue;
//		preservance.addHead(headValue);
//		preservance.addTail(tailValue);
//
//		if (tailValue == null) {
//			return preservance;
//		}
//
//		if (relevantStatement == null) {
//			return preservance;
//		}
//
//		double valuePreservingNum = 0;
//		double sensivityPreservingNum = 0;
//		for (int i = 0; i < Properties.DYNAMIC_SENSITIVITY_THRESHOLD; i++) {
//			boolean isSuccessful = relevantStatement.mutate(newTestChromosome.getTestCase(), TestFactory.getInstance());
//			if (isSuccessful) {
//				relevantStatement = locateRelevantStatement(rootVariable, newTestChromosome, map);
//				Object newHeadValue = retrieveHeadValue(relevantStatement);
//				t1 = System.currentTimeMillis();
//				Object newTailValue = evaluateTailValue(branch, tailInstruction, newTestChromosome);
//				t2 = System.currentTimeMillis();
//				AbstractMOSA.all10MutateTime += t2 - t1;
//				preservance.addHead(newHeadValue);
//				preservance.addTail(newTailValue);
//				
//				valuePreserving = checkValuePreserving(newHeadValue, newTailValue);
//
//				boolean sensivityPreserving = false;
//				if (newTailValue == null || tailValue == null) {
//					sensivityPreserving = false;
//				} else if (newHeadValue == null || headValue == null) {
//					sensivityPreserving = !newTailValue.equals(tailValue);
//				} else {
//					sensivityPreserving = !newHeadValue.equals(headValue) && !newTailValue.equals(tailValue);
//				}
//
//				if (valuePreserving) {
//					valuePreservingNum += 1;
//				}
//
//				if (sensivityPreserving) {
//					sensivityPreservingNum += 1;
//				}
//			}
//		}
//		double vpRatio = valuePreservingNum / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
//		double spRatio = sensivityPreservingNum / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
//
//		preservance.sensivityPreserRatio = spRatio;
//		preservance.valuePreservingRatio = vpRatio;
//
//		return preservance;
//	}

	public static boolean checkValuePreserving(Object headValue, Object tailValue) {
		// TODO need to define the similarity of different value
		if (headValue == null || tailValue == null) {
			return false;
		}
		if (headValue.equals(tailValue))
			return true;
		// if headValue and tailValue are primitive type,compare the data value
		if ((headValue.getClass().isPrimitive() || isPrimitiveClass(headValue))
				&& (tailValue.getClass().isPrimitive() || isPrimitiveClass(tailValue))) {
			if (headValue instanceof Character) {
				headValue = Character.getNumericValue((char) headValue);
			}
			if (tailValue instanceof Character) {
				tailValue = Character.getNumericValue((char) tailValue);
			}
			Number head = (Number) headValue;
			Number tail = (Number) tailValue;
			if (Math.abs(head.longValue() - tail.longValue()) <= 10) {
				return true;
			}
			if (Math.abs(head.doubleValue() - tail.doubleValue()) <= 10) {
				return true;
			}
			return false;
		}
		// compare the similarity of string
		String head = headValue.toString();
		String tail = tailValue.toString();
		if (getSimilarityRatio(head, tail) >= Properties.VALUE_SIMILARITY_THRESHOLD) {
			return true;
		}
//		else {
//			int changedLength = 0;
//			for(int i = 0;i < preservance.headValues.size();i++) {
//				head = preservance.headValues.get(i).toString();
//				tail = preservance.tailValues.get(i).toString();
//				
//				if(i == 0)
//					changedLength = head.toCharArray().length - tail.toCharArray().length;
//				if(changedLength != head.toCharArray().length - tail.toCharArray().length)
//					return false;
//			}
//			if(preservance.headValues.size() != 0)
//				return true;			
//		}
		return headValue.equals(tailValue);
	}

	private static boolean isPrimitiveClass(Object object) {
		if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long
				|| object instanceof Float || object instanceof Double || object instanceof Character) {
			return true;
		}
		return false;
	}

	private static float getSimilarityRatio(String head, String tail) {
		// TODO Edit Distance
		if (head == null || tail == null)
			return 0;
		int max = Math.max(head.length(), tail.length());
		float score = 1 - (float) compare(head, tail) / max;

		/**
		 * normalize the score when the max length is too small, which results in that
		 * 0.5, 0.67, 0.75 are large score.
		 */
		if (max <= 3) {
			score = score * (1 + 1.0f / (float) max);
		}

		return score;
	}

	private static float compare(String head, String tail) {
		char ch1, ch2;
		int temp;
		if (head.length() == 0) {
			return tail.length();
		} else if (tail.length() == 0) {
			return head.length();
		}
		int d[][] = new int[head.length() + 1][tail.length() + 1];
		for (int i = 0; i <= head.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= tail.length(); j++) {
			d[0][j] = j;
		}
		for (int i = 1; i <= head.length(); i++) {
			ch1 = head.charAt(i - 1);
			for (int j = 1; j <= tail.length(); j++) {
				ch2 = tail.charAt(j - 1);
				if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
			}
		}
		return d[head.length()][tail.length()];
	}

	private static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}

	private static Map<String, List<Object>> evaluateObservations(Branch branch, List<BytecodeInstruction> observations,
			TestChromosome newTestChromosome) {
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(branch, true);
		set.add(ff);
		for (FitnessFunction<?> f : set) {
			newTestChromosome.addFitness(f);
		}
		FitnessFunction<Chromosome> fitness = searchForRelevantFitness(branch, newTestChromosome);

		InstrumentingClassLoader newClassLoader = new InstrumentingClassLoader(observations);

		for (BytecodeInstruction ins : observations) {
			DependencyAnalysis.addTargetClass(ins.getClassName());
		}
		for (Statement s : newTestChromosome.getTestCase()) {
			String className = s.getReturnType().getTypeName();
			if (!className.contains("."))
				continue;
			if (className.contains("[")) {
				className = className.substring(0, className.indexOf("["));
			}

			DependencyAnalysis.addTargetClass(className);
		}

		try {
			for (BytecodeInstruction ins : observations) {
				newClassLoader.loadClass(ins.getClassName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		RuntimeSensitiveVariable.observations.clear();
		for(BytecodeInstruction ins: observations) {
			RuntimeSensitiveVariable.observations.put(ins.toString(), new ArrayList<>());
		}
		
		System.currentTimeMillis();
		((DefaultTestCase) newTestChromosome.getTestCase()).changeClassLoader(newClassLoader);
		newTestChromosome.addFitness(fitness);
		newTestChromosome.clearCachedResults();
		fitness.getFitness(newTestChromosome);

		Map<String, List<Object>> res = new HashMap<>();
		for (String s : RuntimeSensitiveVariable.observations.keySet()) {
			res.put(s, RuntimeSensitiveVariable.observations.get(s));
		}

		RuntimeSensitiveVariable.observations.clear();
		return res;

	}

	@SuppressWarnings("rawtypes")
	private static Object retrieveHeadValue(Statement relevantStartment) {
		if (relevantStartment instanceof PrimitiveStatement) {
			PrimitiveStatement pStat = (PrimitiveStatement) relevantStartment;
			return pStat.getValue();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static FitnessFunction<Chromosome> searchForRelevantFitness(Branch targetBranch,
			TestChromosome newTestChromosome) {
		Map<FitnessFunction<?>, Double> fitnessValues = newTestChromosome.getFitnessValues();
		for (FitnessFunction<?> ff : fitnessValues.keySet()) {
			if (ff instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness) ff;
				if (bf.getBranchGoal().getBranch().equals(targetBranch)) {
					return (FitnessFunction<Chromosome>) ff;
				}
			}
		}

		return null;
	}

	// Returns the statement in the test case that modifies rootVariable
	private static Statement locateRelevantStatement(DepVariable rootVariable, TestChromosome newTestChromosome,
			Map<DepVariableWrapper, List<VariableReference>> map) {
		Statement rootValueStatement = null;
		TestCase tc = newTestChromosome.getTestCase();

		// Find root statement using object map
		DepVariableWrapper rootVarWrapper = new DepVariableWrapper(rootVariable);
		List<VariableReference> varRefs = map.get(rootVarWrapper);
		if (varRefs != null) {
			int minPos = tc.size();
			for (VariableReference varRef : varRefs) {
				int pos = varRef.getStPosition();
				if (pos < minPos) {
					minPos = pos;
				}
			}

			rootValueStatement = getRootValueStatement(tc, tc.getStatement(minPos));
			if (rootValueStatement != null)
				return rootValueStatement;
		}

		if (rootVariable.isParameter()) {
			if (newTestChromosome.getTestCase().size() == 0)
				return rootValueStatement;
			int paramIndex = rootVariable.getParamOrder();
			List<VariableReference> methodCallParams = getMethodCallParams(tc);
			VariableReference paramRef = methodCallParams.get(paramIndex);
			Statement relevantStatement = getStatementModifyVariable(paramRef);
			rootValueStatement = getRootValueStatement(tc, relevantStatement);

		} else if (rootVariable.isInstaceField() || rootVariable.isStaticField()) {
			Statement fieldStatement = getFieldStatement(tc, rootVariable);
			rootValueStatement = getRootValueStatement(tc, fieldStatement);
		}

		// rootVariable's statement not in test case, return root value statement of
		// a target method parameter
		if (rootValueStatement == null) {
			List<VariableReference> methodCallParams = getMethodCallParams(tc);
			for (VariableReference paramRef : methodCallParams) {
				Statement relevantStatement = getStatementModifyVariable(paramRef);
				rootValueStatement = getRootValueStatement(tc, relevantStatement);

				if (rootValueStatement != null) {
					break;
				}
			}
		}

		return rootValueStatement;
	}

	private static List<VariableReference> getMethodCallParams(TestCase testCase) {
		List<VariableReference> params = new ArrayList<>();

		Statement methodStatement = testCase.getStatement(testCase.size() - 1);
		if (methodStatement instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) methodStatement;

			// Ensure last statement is target method call
			String methodName = ms.getMethod().getNameWithDescriptor();
			if (methodName.equals(Properties.TARGET_METHOD)) {
				params = ms.getParameterReferences();
			}
		}

		return params;
	}

	private static Statement getRootValueStatement(TestCase testCase, Statement statement) {
		// 1. PrimitiveStatement: int int0 = (-2236);
		if (statement == null || statement instanceof PrimitiveStatement) {
			return statement;
		}

		// 2. ArrayStatement: int[] intArray0 = new int[0];
		if (statement instanceof ArrayStatement) {
			// mutate one random element

			// get array variable name
			ArrayStatement as = (ArrayStatement) statement;
			String code = as.getCode();
			int start = code.indexOf("[] ");
			int end = code.indexOf(" = ");
			String varName = as.getCode().substring(start + 3, end);

			// array dimension > 1 or array length == 0
			List<Integer> lengths = as.getLengths();
			if (lengths.size() != 1 || lengths.get(0) == 0) {
				return statement;
			}

			int indexToMutate = lengths.get(0) - 1;
			// chance to return array statement to mutate to change its length
			if (indexToMutate == lengths.get(0)) {
				return statement;
			}

			// return 1 random element root statement to mutate
			for (int i = 0; i < testCase.size(); i++) {
				Statement st = testCase.getStatement(i);
				if (st instanceof AssignmentStatement && st.getCode().contains(varName + "[" + indexToMutate + "] =")) {
					return getRootValueStatement(testCase, st);
				}
			}

			return statement;
		}

		// 3. ConstructorStatement: BooleanFlagExample1 booleanFlagExample1_0 = new
		// BooleanFlagExample1();
		// 4. MethodStatement: Integer integer11 =
		// booleanFlagExample1_0.targetM2(intArray0, int5);
		if (statement instanceof ConstructorStatement || statement instanceof MethodStatement) {
			// modify 1 random parameter
			EntityWithParametersStatement ps = (EntityWithParametersStatement) statement;
			List<VariableReference> params = ps.getParameterReferences();

			if (params.size() == 0) {
				return statement;
			}

			int indexToMutate = Randomness.nextInt(params.size());
			// chance to return whole constructor/method statement
			if (indexToMutate == params.size()) {
				return statement;
			}

			// return 1 random parameter root statement to mutate
			return getRootValueStatement(testCase, getStatementModifyVariable(params.get(indexToMutate)));
		}

		// 5. AssignmentStatement: booleanFlagExample1_0.hashValues = intArray0;
		if (statement instanceof AssignmentStatement) {
			AssignmentStatement as = (AssignmentStatement) statement;
			VariableReference rightVar = as.getValue();
			Statement rightVarStatement = testCase.getStatement(rightVar.getStPosition());

			return getRootValueStatement(testCase, rightVarStatement);
		}

		return statement;
	}

	private static Statement getStatementModifyVariable(VariableReference varRef) {
		return varRef.getTestCase().getStatement(varRef.getStPosition());
	}

	private static Statement getFieldStatement(TestCase testCase, DepVariable rootVariable) {
		for (int i = testCase.size() - 1; i >= 0; i--) {
			Statement statement = testCase.getStatement(i);
			if (statementModifiesVar(statement, rootVariable)) {
				return statement;
			}
		}
		return null;
	}

	private static boolean statementModifiesVar(Statement statement, DepVariable rootVariable) {
		String statementFieldName = statement.getReturnValue().getName();
		String rootVarName = rootVariable.getName();
		return statementFieldName.equals(rootVarName) || statementFieldName.endsWith("." + rootVarName);
	}

	private static Map<Branch, List<ComputationPath>> parseComputationPaths(Branch branch,
			Map<Branch, Set<DepVariable>> branchesInTargetMethod) {

		Map<Branch, List<ComputationPath>> map = new HashMap<>();

		Set<DepVariable> rootVaribles = branchesInTargetMethod.get(branch);
		for (DepVariable rootVar : rootVaribles) {
			List<ComputationPath> paths = ComputationPath.computePath(rootVar, branch);

			for (ComputationPath path : paths) {
				List<ComputationPath> pathList = map.get(branch);
				if (pathList == null) {
					pathList = new ArrayList<>();
				}

				if (!pathList.contains(path)) {
					pathList.add(path);
				}

				map.put(branch, pathList);
			}

		}

		return map;
	}

}
