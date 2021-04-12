package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.FitnessFunction;
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
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;

public class SensitivityMutator {
	public static List<List<Object>> data = new ArrayList<List<Object>>();
//	public static int iter = 0;

	public static TestCase initializeTest(Branch b, TestFactory testFactory, boolean allowNullValue) {
		TestCase test = new DefaultTestCase();
		int success = -1;
		while (test.size() == 0 || success == -1) {
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
	
	public static void testSensitity(Set<FitnessFunction<?>> fitness) throws ClassNotFoundException, ConstructionFailedException {
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		for (Branch branch : branchesInTargetMethod.keySet()) {
			testBranchSensitivity(fitness, branchesInTargetMethod, branch);
		}

		System.currentTimeMillis();
	}

	public static boolean testBranchSensitivity(Set<FitnessFunction<?>> fitness,
			Map<Branch, Set<DepVariable>> branchesInTargetMethod, Branch branch) {
		TestCase test = SensitivityMutator.initializeTest(branch, TestFactory.getInstance(), false);
		data.clear();
		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);
		for(FitnessFunction<?> ff: fitness) {
			oldTestChromosome.addFitness(ff);
		}
		
		Map<Branch, List<ComputationPath>> branchWithBDChanged = parseComputationPaths(
				fitness, branchesInTargetMethod);
		
		List<ComputationPath> paths = branchWithBDChanged.get(branch);
		
		if (paths == null) {
			return false;
		}
		for (ComputationPath path : paths) {
			TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
			DepVariable rootVariable = path.getComputationNodes().get(0);
			Statement relevantStatement = locateRelevantStatement(rootVariable, newTestChromosome);

			Object headValue = retrieveHeadValue(relevantStatement);
			Object tailValue = evaluateTailValue(path, newTestChromosome);
			boolean valuePreserving = checkValuePreserving(headValue, tailValue);
			
			if (tailValue == null) {
				return false;
			}
//			List<Object> row = new ArrayList<Object>();
//			row.add(path.getComputationNodes().toString());
//			row.add(tailValue.toString());
//			data.add(row);

//				Object headValue = retrieveRuntimeValueForHead(newTestChromosome);
//				Object tailValue = retrieveRuntimeValueForTail(newTestChromosome);

			if (relevantStatement == null)
				continue;
			int sensivityNum = 0;
			for(int i = 0; i < 3;i++){
				boolean isSuccessful = relevantStatement.mutate(newTestChromosome.getTestCase(),
						TestFactory.getInstance());
				if (isSuccessful) {
					relevantStatement = locateRelevantStatement(rootVariable, newTestChromosome);
					Object newHeadValue = retrieveHeadValue(relevantStatement);
					Object newTailValue = evaluateTailValue(path, newTestChromosome);
					
					valuePreserving = checkValuePreserving(newHeadValue, newTailValue);
					
					boolean sensivityPreserving = false;
					if(newTailValue == null && tailValue == null) {
						 sensivityPreserving = false;
					}else
						 sensivityPreserving = !newHeadValue.equals(headValue) && !newTailValue.equals(tailValue);
					
					recordList(branch,path,headValue,tailValue,newHeadValue,newTailValue,
							valuePreserving,sensivityPreserving,oldTestChromosome,newTestChromosome);
					
	
					if(sensivityPreserving) {
						sensivityNum += 1;
						if(sensivityNum >= 2) {	
							return true;
						}		
					}
						
				}
		}
		}
		
		return false;
	}

	private static void countNumInfo(Map<Integer, Integer> num, int i, boolean valuePreserving) {
		if(num.containsKey(i)) {
			if(valuePreserving)
				num.put(i, num.get(i) + 1);
		}else {
			if(valuePreserving)
				num.put(i, 1);
		}
	}

	private static void recordList(Branch branch, ComputationPath path, Object headValue, Object tailValue,
			Object newHeadValue, Object newTailValue, boolean valuePreserving, boolean sensivityPreserving, TestChromosome oldTestChromosome, TestChromosome newTestChromosome) {
		List<Object> row = new ArrayList<Object>();
		row.add(Properties.TARGET_CLASS);
		row.add(Properties.TARGET_METHOD);
		row.add(branch.toString());
		row.add(path.getComputationNodes().toString());
		row.add(oldTestChromosome.toString());
		row.add(headValue.toString());
		row.add(tailValue.toString());
		row.add(newTestChromosome.toString());
		row.add(newHeadValue.toString());
		row.add(newTailValue.toString());
		row.add(valuePreserving);
		row.add(sensivityPreserving);
		row.add(valuePreserving || sensivityPreserving);
		data.add(row);
	}
	
	private static boolean checkValuePreserving(Object headValue, Object tailValue) {
		// TODO need to define the similarity of different value
		if(headValue == null || tailValue == null) {
			return false;
			}
		if(headValue.equals(tailValue))
			return true;
		//if headValue and tailValue are primitive type,compare the data value
		if((headValue.getClass().isPrimitive() || isPrimitiveClass(headValue)) &&
				(tailValue.getClass().isPrimitive() || isPrimitiveClass(tailValue))) {
			if(headValue instanceof Character) {
				headValue = Character.getNumericValue((char) headValue);
			}
			if(tailValue instanceof Character) {
				tailValue = Character.getNumericValue((char) tailValue);
			}
			Number head = (Number)headValue;
			Number tail = (Number)tailValue;
			if(Math.abs(head.longValue() - tail.longValue()) <= 10) {
				return true;
			}
			if(Math.abs(head.doubleValue() - tail.doubleValue()) <= 10) {
				return true;
			}
			return false;
		}
		//compare the similarity of string
		String head = headValue.toString();
		String tail = tailValue.toString();
		if(Math.abs(getSimilarityRatio(head,tail)) >= 0.9) {
			return true;
		}
		return headValue.equals(tailValue);
	}

	private static boolean isPrimitiveClass(Object object) {
		if(object instanceof Byte ||
		   object instanceof Short ||
		   object instanceof Integer ||
		   object instanceof Long ||
		   object instanceof Float ||
		   object instanceof Double ||
		   object instanceof Character
				) {
			return true;
		}
		return false;
	}

	private static float getSimilarityRatio(String head, String tail) {
		// TODO Edit Distance
		int max = Math.max(head.length(), tail.length());
		return 1 - (float) compare(head, tail) / max;
	}

	private static float compare(String head, String tail) {
		char ch1,ch2; 
		int temp; 
		if (head.length() == 0) {
			return tail.length();
		}else if (tail.length() == 0) {
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

	private static Object evaluateTailValue(ComputationPath path, TestChromosome newTestChromosome) {
		BytecodeInstruction tailInstruction = path.getRelevantTailInstruction();
		FitnessFunction<Chromosome> fitness = searchForRelevantFitness(path.getBranch(), newTestChromosome);
		InstrumentingClassLoader newClassLoader = new InstrumentingClassLoader(tailInstruction);
		DependencyAnalysis.addTargetClass(tailInstruction.getClassName());
		try {
			newClassLoader.loadClass(tailInstruction.getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		((DefaultTestCase) newTestChromosome.getTestCase()).changeClassLoader(newClassLoader);
		newTestChromosome.addFitness(fitness);
		newTestChromosome.clearCachedResults();
		fitness.getFitness(newTestChromosome);

		Object tailValue = RuntimeSensitiveVariable.tailValue;
		System.out.println("Tail Value: " + tailValue);
		return tailValue;
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
	private static Statement locateRelevantStatement(DepVariable rootVariable, TestChromosome newTestChromosome) {
		TestCase tc = newTestChromosome.getTestCase();

		if (rootVariable.isParameter()) {
			MethodStatement callStatement = parseTargetMethodCall(tc);
			if (callStatement == null) {
				return null;
			}

			int paramIndex = rootVariable.getParamOrder() - 1;
			List<VariableReference> newParams = callStatement.getParameterReferences();
			VariableReference paramRef = newParams.get(paramIndex);
			Statement relevantStatement = getStatementModifyVariable(paramRef);
			Statement rootValueStatement = getRootValueStatement(tc, relevantStatement);

			return rootValueStatement;

		} else if (rootVariable.isInstaceField() || rootVariable.isStaticField()) {
			Statement fieldStatement = getFieldStatement(tc, rootVariable);
			Statement rootValueStatement = getRootValueStatement(tc, fieldStatement);

			return rootValueStatement;
		}

		return null;
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
			
			int indexToMutate = Randomness.nextInt(lengths.get(0));
			// chance to return array statement to mutate to change its length
			if (indexToMutate == lengths.get(0)) {
				return statement;
			}

			// return 1 random element root statement to mutate
			for (int i = 0; i < testCase.size(); i++) {
				Statement st = testCase.getStatement(i);
				if (st instanceof AssignmentStatement
						&& st.getCode().contains(varName + "[" + indexToMutate + "] =")) {
					return getRootValueStatement(testCase, st);
				}
			}

			return statement;
		}

		// 3. ConstructorStatement: BooleanFlagExample1 booleanFlagExample1_0 = new BooleanFlagExample1();
		// 4. MethodStatement: Integer integer11 = booleanFlagExample1_0.targetM2(intArray0, int5);
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
	
	private static MethodStatement parseTargetMethodCall(TestCase testCase) {
		Statement statement = testCase.getStatement(testCase.size() - 1);
		if (statement instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) statement;
			String methodName = ms.getMethod().getNameWithDescriptor();

			if (methodName.equals(Properties.TARGET_METHOD)) {
				return ms;
			}
		}

		return null;
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

	private static Map<Branch, List<ComputationPath>> parseComputationPaths(Set<FitnessFunction<?>> changedFitnesses,
			Map<Branch, Set<DepVariable>> branchesInTargetMethod) {

		Map<Branch, List<ComputationPath>> map = new HashMap<>();

		for (FitnessFunction<?> ff : changedFitnesses) {
			if (ff instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness) ff;

				Branch b = bf.getBranchGoal().getBranch();
				Set<DepVariable> rootVaribles = branchesInTargetMethod.get(b);
				for (DepVariable rootVar : rootVaribles) {
					List<ComputationPath> paths = ComputationPath.computePath(rootVar, b);

					for (ComputationPath path : paths) {
						List<ComputationPath> pathList = map.get(b);
						if (pathList == null) {
							pathList = new ArrayList<>();
						}

						if (!pathList.contains(path)) {
							pathList.add(path);
						}

						map.put(b, pathList);
					}

				}
			}
		}

		return map;
	}

	private boolean statementsEqual(Statement statement1, Statement statement2) {
		if (statement1 == null || statement2 == null) {
			return false;
		}

		// 1. ArrayStatement
		if (statement1 instanceof ArrayStatement && statement2 instanceof ArrayStatement) {
			ArrayStatement as1 = (ArrayStatement) statement1;
			ArrayStatement as2 = (ArrayStatement) statement2;

			if (as1.getClass() != as2.getClass())
				return false;
			if (!as1.getLengths().equals(as2.getLengths()))
				return false;
		}

		// 2. PrimitiveStatement
		if (statement1 instanceof PrimitiveStatement && statement2 instanceof PrimitiveStatement) {
			PrimitiveStatement ps1 = (PrimitiveStatement) statement1;
			PrimitiveStatement ps2 = (PrimitiveStatement) statement2;

			if (ps1.getClass() != ps2.getClass())
				return false;
			if (!ps1.getValue().equals(ps2.getValue()))
				return false;
		}

		// 3. MethodStatement
		if (statement1 instanceof MethodStatement && statement2 instanceof MethodStatement) {
			MethodStatement ms1 = (MethodStatement) statement1;
			MethodStatement ms2 = (MethodStatement) statement2;

			if (ms1.getClass() != ms2.getClass())
				return false;
			if (!ms1.getMethod().equals(ms2.getMethod()))
				return false;

			// Check if callee is the same
			if ((ms1.getCallee() == null && ms2.getCallee() != null)
					|| (ms1.getCallee() != null && ms2.getCallee() == null)) {
				return false;
			} else {
				if (ms1.getCallee() == null)
					return true;

				Statement stmt1 = getStatementFromVariable(ms1.getCallee());
				Statement stmt2 = getStatementFromVariable(ms2.getCallee());
				if (!statementsEqual(stmt1, stmt2)) {
					return false;
				}
			}

			// Check if all the method arguments are the same
			List<VariableReference> params1 = ms1.getParameterReferences();
			List<VariableReference> params2 = ms2.getParameterReferences();

			if (params1.size() != params2.size())
				return false;

			for (int i = 0; i < params1.size(); i++) {
				Statement stmt1 = getStatementFromVariable(params1.get(i));
				Statement stmt2 = getStatementFromVariable(params2.get(i));

				if (!statementsEqual(stmt1, stmt2))
					return false;
			}
		}

		// 4. ConstructorStatement
		if (statement1 instanceof ConstructorStatement && statement2 instanceof ConstructorStatement) {
			ConstructorStatement cs1 = (ConstructorStatement) statement1;
			ConstructorStatement cs2 = (ConstructorStatement) statement2;

			if (cs1.getClass() != cs2.getClass())
				return false;
			if (cs1.getParameterReferences().size() != cs2.getParameterReferences().size())
				return false;

			if (!cs1.getConstructor().equals(cs2.getConstructor()))
				return false;

			// Check if all the method arguments are the same
			List<VariableReference> params1 = cs1.getParameterReferences();
			List<VariableReference> params2 = cs2.getParameterReferences();

			if (params1.size() != params2.size())
				return false;

			for (int i = 0; i < params1.size(); i++) {
				Statement stmt1 = getStatementFromVariable(params1.get(i));
				Statement stmt2 = getStatementFromVariable(params2.get(i));

				if (!statementsEqual(stmt1, stmt2))
					return false;
			}
		}

		return true;
	}

	private Statement getStatementFromVariable(VariableReference varRef) {
		return varRef.getTestCase().getStatement(varRef.getStPosition());
	}
}
