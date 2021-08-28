package org.evosuite.seeding.smart;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.ValueStatement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;

/**
 * 
 * A data structure to capture the method inputs of a method
 * 
 * @author Yun Lin
 *
 */
public class MethodInputs {
	
	private Map<String, ValueStatement> inputVariables;
	private Map<String, Object> inputConstants;

	public MethodInputs(Map<String, ValueStatement> inputVariables, Map<String, Object> inputConstants) {
		super();
		this.inputVariables = inputVariables;
		this.inputConstants = inputConstants;
	}

	public Map<String, ValueStatement> getInputVariables() {
		return inputVariables;
	}

	public void setInputVariables(Map<String, ValueStatement> inputVariables) {
		this.inputVariables = inputVariables;
	}

	public Map<String, Object> getInputConstants() {
		return inputConstants;
	}

	public void setInputConstants(Map<String, Object> inputConstants) {
		this.inputConstants = inputConstants;
	}

	
	private List<ValueStatement> fixedPoints = new ArrayList<>();
	
	public void mutate(Branch branch, TestChromosome startPoint, BranchFitness bf) {
		
//		Set<FitnessFunction<?>> set = new HashSet<>();
//		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(branch, bf.getBranchGoal().getValue());
//		set.add(ff);
//		for (FitnessFunction<?> f : set) {
//			startPoint.addFitness(f);
//		}
		startPoint.clearCachedResults();
		startPoint.addFitness((FitnessFunction<?>) bf);
		FitnessFunction<Chromosome> fitness = SensitivityMutator.searchForRelevantFitness(branch, startPoint);
		startPoint.getFitness(fitness);
		double fitnessValue = fitness.getFitness(startPoint);
		
		if(fitnessValue > 1) {
			System.currentTimeMillis();
		}
		
		assert fitnessValue < 1.0;
		
		for(String key: inputVariables.keySet()) {
			
			ValueStatement statement = inputVariables.get(key);
			
			if(fixedPoints.contains(statement)) {
				continue;
			}
			
			TestCase test = statement.getTestCase();
			Object oldValue = statement.getAssignmentValue();
			VariableReference oldParam = null;
			
			if(oldValue != null) {
				mutateValueStatement(statement);
			}
			else if(statement instanceof AssignmentStatement) {
				AssignmentStatement aStat = (AssignmentStatement)statement;
				oldParam = aStat.getValue();
				mutateValueStatement(statement);
			}

			TestChromosome trial = new TestChromosome();
			trial.setTestCase(test);
			
			trial.addFitness((FitnessFunction<?>) bf);
			FitnessFunction<Chromosome> fitness2 = SensitivityMutator.searchForRelevantFitness(branch, trial);
			double fitnessValue2 = fitness2.getFitness(trial);
			if(fitnessValue2 > 1) {
				if(oldParam != null) {
					((AssignmentStatement)statement).setValue(oldParam);
				}
				else {
					statement.setAssignmentValue(oldValue);					
				}
				
				fixedPoints.add(statement);
			}
			
		}
		
		System.currentTimeMillis();
	}

	private void mutateValueStatement(ValueStatement statement) {
		Type t = statement.getReturnType();
		if(t.equals(Integer.class)) {
			Object obj = Randomness.nextInt();
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(String.class)) {
			Object obj = Randomness.nextString(10);
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(Short.class)) {
			Object obj = Randomness.nextShort();
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(Long.class)) {
			Object obj = Randomness.nextLong();
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(Character.class)) {
			Object obj = Randomness.nextChar();
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(Byte.class)) {
			Object obj = Randomness.nextByte();
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(Double.class)) {
			Object obj = Randomness.nextDouble();
			statement.setAssignmentValue(obj);
		}
		else if(t.equals(Float.class)) {
			Object obj = Randomness.nextFloat();
			statement.setAssignmentValue(obj);
		}
	}

	public MethodInputs identifyInputs(TestChromosome newTestChromosome) {
		Map<String, ValueStatement> inputVariables = new HashMap<>();
		for(String key: this.inputVariables.keySet()) {
			ValueStatement vStatement = this.inputVariables.get(key);
			Statement s = newTestChromosome.getTestCase().getStatement(vStatement.getPosition());
			if(s instanceof ValueStatement) {
				inputVariables.put(key, (ValueStatement)s);
			}
		}
		
		return new MethodInputs(inputVariables, inputConstants);
	}
	

}
