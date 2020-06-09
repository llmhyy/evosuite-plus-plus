package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Opcodes;

public class TestCaseLegitimizer {

	public static int optimizionPopluationSize = 20;
	
	@SuppressWarnings("unchecked")
	public static TestCase legitimize(TestCase test) {
		ExecutionResult result = TestCaseExecutor.getInstance().execute(test);
		MethodStatement targetCallStat = test.findTargetMethodCallStatement();
		if(targetCallStat == null) return null;
		
		if(isExecuteTargetMethod(test, result)) return test;
		/**
		 * initializing the population
		 */
		List<TestChromosome> population = new ArrayList<TestChromosome>();
		for(int i=0; i<optimizionPopluationSize; i++){
			TestCase copy = test.clone();
			TestChromosome t = new TestChromosome();
			t.setTestCase(copy);
			population.add(t);
		}
		
		int counter = 0;
		while (!isExecuteTargetMethod(test, result) && counter <= 5){
			counter ++;
			int numOfExecutedStatements = result.getExecutedStatements();
			Statement statOfExp = test.getStatement(numOfExecutedStatements);
			Throwable excep = result.getExceptionThrownAtPosition(statOfExp.getPosition());
			
			if(excep == null){
				System.currentTimeMillis();
			}
			
			/**
			 * locate the relevant branches 
			 */
			List<FBranchTestFitness> relevantBranches = locateRelevantBranches(statOfExp, excep, test, result);
			
//			boolean isBranchExecuted = isBranchExecuted(test, relevantBranches);
//			if(isBranchExecuted){
//				
//			}
			
			MutationPositionDiscriminator.discriminator.setPurpose(relevantBranches);
			for(FBranchTestFitness ftt: relevantBranches){
				for(TestChromosome chromosome: population){
					chromosome.addFitness(ftt);
				}
			}
			
			TestCase t = evolve(population, relevantBranches);
			if(t != null){
				test = t;
			}
		}
		
		return null;
	}
	
	private static TestCase evolve(List<TestChromosome> population, List<FBranchTestFitness> relevantBranches) {
		int iteration = 0;
		while(iteration < 100){
			List<TestChromosome> newPopulation = breedOffSpring(population, relevantBranches);
			population.addAll(newPopulation);
			
			select(population, relevantBranches);
			
			for(TestChromosome chromosome: population){
				for(FitnessFunction<?> ftt: relevantBranches){
					if(chromosome.getFitness(ftt) == 0.0){
						return chromosome.getTestCase();
					}					
				}
			}
			
			iteration++;
		}
		
		return null;
	}

	private static void select(List<TestChromosome> population, List<FBranchTestFitness> relevantBranches) {
		int topNum = optimizionPopluationSize / relevantBranches.size();
		
		List<TestChromosome> newPop = new ArrayList<>();
		for (final FBranchTestFitness fit: relevantBranches){
			population.sort(new Comparator<TestChromosome>() {
				@Override
				public int compare(TestChromosome o1, TestChromosome o2) {
					if(o1.getFitness(fit) - o2.getFitness(fit) < 0){
						return -1;
					}
					else if(o1.getFitness(fit) - o2.getFitness(fit) > 0){
						return 1;
					}
					else{
						return 0;
					}
				}
			});
			
			for(int i=0; i<topNum; i++){
				TestChromosome individual = population.get(i);
				if(!newPop.contains(individual)){
					newPop.add(individual);
				}
			}
		}
		
		if(newPop.size() < optimizionPopluationSize){
			for(TestChromosome individual: population){
				if(!newPop.contains(individual)){
					newPop.add(individual);
					
					if(newPop.size() >= optimizionPopluationSize){
						break;
					}
				}
			}
		}
		
		population.clear();
		population.addAll(newPop);
	}

	private static List<TestChromosome> breedOffSpring(List<TestChromosome> population, List<FBranchTestFitness> relevantBranches) {
		List<TestChromosome> newPop = new ArrayList<>();
		
		for(TestChromosome parent: population){
			relevantBranches.forEach(fitnessFunction -> fitnessFunction.getFitness(parent));
			TestChromosome offspring = (TestChromosome) parent.clone();
			offspring.clearCachedResults();
			offspring.mutationChangePrimitiveStatement();
			relevantBranches.forEach(fitnessFunction -> fitnessFunction.getFitness(offspring));
			MutationPositionDiscriminator.identifyRelevantMutations(offspring, parent);
			
			System.currentTimeMillis();
			
			newPop.add(offspring);
		}
		System.currentTimeMillis();
		return newPop;
	}

	private static List<FBranchTestFitness> locateRelevantBranches(Statement statOfExp, Throwable excep, 
			TestCase test, ExecutionResult result) {
		if(isMethodCallIncurExplicitNullPointerException(statOfExp, excep, result)){
			MethodStatement mStat = (MethodStatement)statOfExp;
			VariableReference callee = mStat.getCallee();
			Statement defStat = test.getStatement(callee.getStPosition());
			
			if(defStat instanceof MethodStatement){
				MethodStatement defMethodStat = (MethodStatement)defStat;
				GenericMethod method = defMethodStat.getMethod();
				
				List<FBranchTestFitness> nonNullBranches = locateNonNullBranches(method);
				return nonNullBranches;
			}
		}
		else if(isAssignmentIncurExplicitNullPointerException(statOfExp, excep, result)){
			VariableReference ref = statOfExp.getReturnValue();
			if(ref instanceof FieldReference){
				FieldReference fieldRef = (FieldReference)ref;
				VariableReference source = fieldRef.getSource();
				
				Statement defStat = test.getStatement(source.getStPosition());
				if(defStat instanceof MethodStatement){
					MethodStatement defMethodStat = (MethodStatement)defStat;
					GenericMethod method = defMethodStat.getMethod();
					
					List<FBranchTestFitness> nonNullBranches = locateNonNullBranches(method);
					return nonNullBranches;
				}
			}
			System.currentTimeMillis();
		}
		else if (isIncurInplicitNullPointerException(statOfExp, excep, result)){
			// Fixme for ziheng
		}
		else {
			
		}
		
		System.currentTimeMillis();
		
		return null;
	}

	private static boolean isIncurInplicitNullPointerException(Statement statOfExp, Throwable excep,
			ExecutionResult result) {
		// TODO Auto-generated method stub
		return false;
	}

	private static List<FBranchTestFitness> locateNonNullBranches(GenericMethod method) {
		String methodName = method.getName() + MethodUtil.getSignature(method.getMethod());
		ActualControlFlowGraph cfg = GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getActualCFG(method.getOwnerClass().getClassName(), methodName);
		
		List<FBranchTestFitness> list = new ArrayList<>();
		for(BytecodeInstruction exit: cfg.getExitPoints()){
			
			BytecodeInstruction ins = exit.getSourceOfStackInstruction(0);
			
			if(ins.getASMNode().getOpcode() == Opcodes.ACONST_NULL){
				Branch b = exit.getControlDependentBranch();
				boolean value = exit.getControlDependentBranchExpressionValue();
				
				BranchCoverageTestFitness fitness = BranchCoverageFactory.createBranchCoverageTestFitness(b, !value);
				FBranchTestFitness fFit = new FBranchTestFitness(fitness.getBranchGoal());
				list.add(fFit);
			}
			
		}
		
		return list;
	}

	private static boolean isMethodCallIncurExplicitNullPointerException(Statement statOfExp, Throwable excep, ExecutionResult result) {
		return statOfExp instanceof MethodStatement 
				&&  result.explicitExceptions.get(statOfExp.getPosition()) != null
				&& excep.getMessage().equals("java.lang.NullPointerException");
	}
	
	private static boolean isAssignmentIncurExplicitNullPointerException(Statement statOfExp, Throwable excep,
			ExecutionResult result) {
		return statOfExp instanceof AssignmentStatement 
				&&  result.explicitExceptions.get(statOfExp.getPosition()) != null
				&& excep.getMessage().equals("java.lang.NullPointerException");
	}
	
	private static boolean isExecuteTargetMethod(TestCase test, ExecutionResult result){
		MethodStatement targetCallStat = test.findTargetMethodCallStatement();
		int numOfExecutedStatements = result.getExecutedStatements();
		return numOfExecutedStatements >= targetCallStat.getPosition();
	}

	private static boolean isIncurExplicitNullPointerException(Statement statOfExp, Throwable excep, ExecutionResult result){
		return statOfExp instanceof MethodStatement 
			&&  result.explicitExceptions.get(statOfExp.getPosition()) != null
			&& excep.getMessage().equals("java.lang.NullPointerException");
	}
}
