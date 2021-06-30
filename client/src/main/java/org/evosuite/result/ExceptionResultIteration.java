package org.evosuite.result;

import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.Chromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;

/**
 * Data object to collect exception based results.
 * Tracks the progress for a single iteration for a single FitnessFunction<T>.
 * @author Darien
 *
 */
public class ExceptionResultIteration<T extends Chromosome> {
	// The FitnessFunction<T> being tracked.
	private FitnessFunction<T> fitnessFunction;
	
	// The iteration number.
	private int iteration;
	
	// The best candidate for this FitnessFunction<T> at the specified iteration.
	private TestChromosome bestCandidate;
	
	/**
	 * Constructor.
	 * 
	 * @param FitnessFunction<T>
	 * @param iteration
	 * @param bestCandidate
	 */
	public ExceptionResultIteration(FitnessFunction<T> fitnessFunction, int iteration, TestChromosome bestCandidate) {
		this.fitnessFunction = fitnessFunction;
		this.iteration = iteration;
		this.bestCandidate = bestCandidate;
	}
	
	/**
	 * Checks if an exception occurred during test case execution.
	 * 
	 * @return True if an exception occurred, false otherwise.
	 */
	public boolean isExceptionOccurred() {
		ExecutionResult executionResult = bestCandidate.getLastExecutionResult();
		return executionResult.getAllThrownExceptions().size() > 0;
	}
	
	/**
	 * See {@return}.
	 * 
	 * @return True if an exception occurred during test case execution,
	 * and the exception is classed as an in-method exception i.e. it occurred during the 
	 * execution of the MUT.
	 */
	public boolean isInMethodException() {
		if (!isExceptionOccurred()) {
			return false;
		}
		
		ExecutionResult executionResult = bestCandidate.getLastExecutionResult();
		int numberOfExecutedStatements = executionResult.getFirstPositionOfThrownException();
		int lengthOfTest = bestCandidate.getTestCase().size();
		return numberOfExecutedStatements == lengthOfTest;
	}
	
	public FitnessFunction<T> getFitnessFunction() {
		return this.fitnessFunction;
	}
	
	public int getIteration() {
		return this.iteration;
	}
}
