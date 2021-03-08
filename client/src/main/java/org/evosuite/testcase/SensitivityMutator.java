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
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.testcase.statements.ArrayStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.VariableReference;

public class SensitivityMutator {
	public static List<List<Object>> data = new ArrayList<List<Object>>();
//	public static int iter = 0;
	
	public static void testSensitity(Chromosome oldTest) {
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		TestChromosome oldTestChromosome = (TestChromosome)oldTest;
		Map<Branch, List<ComputationPath>> branchWithBDChanged = parseBranchWithBDChanged(oldTestChromosome.getFitnessValues().keySet(), branchesInTargetMethod);
		
		for(Branch branch: branchWithBDChanged.keySet()) {
			List<ComputationPath> paths = branchWithBDChanged.get(branch);
			for(ComputationPath path: paths) {
				DepVariable rootVariable = path.getComputationNodes().get(0);
				
				TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
				Statement relevantStartment = locateRelevantStatement(rootVariable, newTestChromosome);
				
				if(relevantStartment == null)continue;
				
				boolean isSuccessful = relevantStartment.mutate(newTestChromosome.getTestCase(), TestFactory.getInstance());
				
				if(isSuccessful) {
					Branch targetBranch = path.getBranch();
					FitnessFunction<Chromosome> newRelevantFitness = searchForRelevantFitness(targetBranch, newTestChromosome);
					newTestChromosome.clearCachedResults();
					double newFit = newRelevantFitness.getFitness(newTestChromosome);
					
					FitnessFunction<Chromosome> oldRelevantFitness = searchForRelevantFitness(targetBranch, oldTestChromosome);
					double oldFit = oldRelevantFitness.getFitness(oldTestChromosome);
					
					double changedFitness = newFit - oldFit;
					
					/**
					 * TODO aaron, record the changed for the path.
					 */
				}
				
//				if(isChangedInSourceCode(rootVariable, offspring, oldTest)) {
//					List<Object> rowData = new ArrayList<>();
//					rowData.add(branch.getClassName());
//					rowData.add(branch.getMethodName());
//					rowData.add(path.getComputationNodes().toString());
//					rowData.add(branch.getInstruction().toString());
//					rowData.add(oldTest.getFitnessValues().get(getFitness(branch, changedFitnesses)));
//					rowData.add(iter);
//					data.add(rowData);
//				}
			}
		}
		
		System.currentTimeMillis();
	}
	
	private static FitnessFunction<Chromosome> searchForRelevantFitness(Branch targetBranch,
			TestChromosome newTestChromosome) {
		// TODO Aaron
		return (FitnessFunction<Chromosome>) newTestChromosome.getFitnessValues().keySet().iterator().next();
	}

	private static Statement locateRelevantStatement(DepVariable rootVariable, TestChromosome newTestChromosome) {
		// TODO Aaron
		return newTestChromosome.getTestCase().getStatement(1);
	}

	private static Map<Branch, List<ComputationPath>> parseBranchWithBDChanged(
			Set<FitnessFunction<?>> changedFitnesses, Map<Branch, Set<DepVariable>> branchesInTargetMethod) {
		
		Map<Branch, List<ComputationPath>> map = new HashMap<>();
		
		for(FitnessFunction<?> ff: changedFitnesses) {
			if(ff instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)ff;
				
				Branch b = bf.getBranchGoal().getBranch();
				Set<DepVariable> rootVaribles = branchesInTargetMethod.get(b);
				for(DepVariable rootVar: rootVaribles) {
					List<ComputationPath> paths = ComputationPath.computePath(rootVar, b);
					
					for(ComputationPath path: paths) {
						List<ComputationPath> pathList = map.get(b);
						if(pathList == null) {
							pathList = new ArrayList<>();
						}
						
						if(!pathList.contains(path)) {
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
	
	private boolean isChangedInSourceCode(DepVariable rootVariable, Chromosome offspring, Chromosome parent) {
		TestChromosome child = (TestChromosome)offspring;
		TestChromosome par = (TestChromosome)parent;
		
//		List<TestMutationHistoryEntry> mutationList = child.mutationHistory.getMutations();
		
		MethodStatement callInNewTest = parseTargetMethodCall(child);
		MethodStatement callInOldTest = parseTargetMethodCall(par);
		
		if(rootVariable.isParameter()) {
			int paramIndex = rootVariable.getParamOrder() - 1;
			List<VariableReference> newParams = callInNewTest.getParameterReferences();
			VariableReference varRef1 = newParams.get(paramIndex);
			Statement statement1 = getStatementFromVariable(varRef1);
			
			List<VariableReference> oldParams = callInOldTest.getParameterReferences();
			VariableReference varRef2 = oldParams.get(paramIndex);
			Statement statement2 = getStatementFromVariable(varRef2);
			
			if (!statementsEqual(statement1, statement2)) {
				return true;
			}
			System.currentTimeMillis();
			System.currentTimeMillis();
			
		}
		else if(rootVariable.isInstaceField()) {
			// Have to check 'set' methods
			System.currentTimeMillis();
			
		}
		else if(rootVariable.isStaticField()) {
			System.currentTimeMillis();
		}
		
		
		
		return false;
	}
	
	private MethodStatement parseTargetMethodCall(TestChromosome test) {
		TestCase testcase = test.getTestCase();
		Statement statement = testcase.getStatement(testcase.size() - 1);
		if (statement instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) statement;
			String methodName = ms.getMethod().getNameWithDescriptor();

			if (methodName.equals(Properties.TARGET_METHOD)) {
				return ms;
			}
		}

		return null;
	}

	private FitnessFunction getFitness(Branch branch, Map<FitnessFunction, Boolean> changedFitnesses) {
		for (FitnessFunction<Chromosome> ff: changedFitnesses.keySet()) {
			if (ff instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)ff;
				if (bf.getBranchGoal().getBranch().equals(branch)) {
					return ff;
				}
			}
		}
		return null;
	}
}
