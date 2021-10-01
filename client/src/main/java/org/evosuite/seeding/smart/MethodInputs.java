package org.evosuite.seeding.smart;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
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

	/**
	 * a set of statements which does not allow to mutate.
	 */
	private List<ValueStatement> fixedPoints = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public boolean mutate(Branch branch, TestChromosome startPoint, BranchFitness bf) {
		
//		Set<FitnessFunction<?>> set = new HashSet<>();
//		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(branch, bf.getBranchGoal().getValue());
//		set.add(ff);
//		for (FitnessFunction<?> f : set) {
//			startPoint.addFitness(f);
//		}
		startPoint.clearCachedResults();
		startPoint.addFitness((FitnessFunction<?>) bf);
		FitnessFunction<Chromosome> fitness = (FitnessFunction<Chromosome>)bf;
//		startPoint.getFitness(fitness);
		double fitnessValue = fitness.getFitness(startPoint);
		
		if(fitnessValue >= 1) {
			System.currentTimeMillis();
			System.currentTimeMillis();
		}
		
		assert fitnessValue <= 1.0;
		
		boolean change = false;
		
		for(String key: inputVariables.keySet()) {
			
			ValueStatement statement = inputVariables.get(key);
			
			if(fixedPoints.contains(statement)) {
				continue;
			}
			
			TestCase test = statement.getTestCase();
			Object oldValue = statement.getAssignmentValue();
			
			int oldMax = Properties.MAX_DELTA;
			Properties.MAX_DELTA = 3;
			statement.mutateValue();
			Properties.MAX_DELTA = oldMax;

			TestChromosome trial = new TestChromosome();
			trial.setTestCase(test);
			
			trial.addFitness((FitnessFunction<?>) bf);
			FitnessFunction<Chromosome> fitness2 = (FitnessFunction<Chromosome>)bf;
			double fitnessValue2 = fitness2.getFitness(trial);
			if(fitnessValue2 >= 1) {
				statement.setAssignmentValue(oldValue);			
				fixedPoints.add(statement);
			}
			else {
				change = true;
			}
			
		}
		
		return change;
	}

//	private void mutateValueStatement(ValueStatement statement) {
//		/**
//		 * It is better to consider both delta and random at the same time, just as what evosuite does.
//		 * TODO: Lin Yun
//		 */
//		Type t = statement.getReturnType();
//		if(t.equals(Integer.class) || t.getTypeName().equals("int")) {
//			Object obj = (int)(Randomness.nextGaussian() * Properties.MAX_INT);
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(String.class)) {
//			Object obj = Randomness.nextString(10);
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(Short.class) || t.getTypeName().equals("short")) {
//			short max = (short) Math.min(Properties.MAX_INT, 32767);
//			Object obj = (short) ((Randomness.nextGaussian() * max));
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(Long.class) || t.getTypeName().equals("long")) {
//			Object obj = (long)(Randomness.nextGaussian() * Properties.MAX_INT);
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(Character.class) || t.getTypeName().equals("char")) {
//			Object obj = Randomness.nextChar();
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(Byte.class) || t.getTypeName().equals("byte")) {
//			Object obj = (byte) (Randomness.nextInt(256) - 128);
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(Double.class) || t.getTypeName().equals("double")) {
//			Object obj = Randomness.nextGaussian() * Properties.MAX_INT;
//			statement.setAssignmentValue(obj);
//		}
//		else if(t.equals(Float.class) || t.getTypeName().equals("float")) {
//			Object obj = (float)(Randomness.nextGaussian() * Properties.MAX_INT);
//			statement.setAssignmentValue(obj);
//		}
//	}

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
