package org.evosuite.result;

import org.evosuite.ga.FitnessFunction;

import java.io.Serializable;

import org.evosuite.ga.Chromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;

/**
 * Data object to collect exception based results.
 * Tracks the progress for a single iteration for a single FitnessFunction<T>.
 * @author Darien
 *
 */
public class ExceptionResultIteration<T extends Chromosome> implements Serializable {
	// generated serialVersionUID.
	private static final long serialVersionUID = 6137032932754134807L;

	// The FitnessFunction<T> being tracked.
	private FitnessFunction<T> fitnessFunction;
	
	// The iteration number.
	private int iteration;
	
	// The best candidate for this FitnessFunction<T> at the specified iteration.
	private TestChromosome bestCandidate;
	
	// Need to pre-compute these and store it because ExecutionResult is not serializable.
	private boolean isExceptionOccurred;
	private boolean isInMethodException;
	private boolean isOutMethodException;
	private Throwable exception;
	
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
		
		computeIsExceptionOccurred();
		computeIsInMethodException();
		computeIsOutMethodException();
		computeException();
	}
	
	
	private void computeIsExceptionOccurred() {
		ExecutionResult executionResult = bestCandidate.getLastExecutionResult();
		isExceptionOccurred = executionResult.getAllThrownExceptions().size() > 0;
	}
	
	private void computeIsInMethodException() {
		computeIsExceptionOccurred();
		
		if (!isExceptionOccurred) {
			isInMethodException = false;
			return;
		}
		
		ExecutionResult executionResult = bestCandidate.getLastExecutionResult();
		int numberOfExecutedStatements = executionResult.getFirstPositionOfThrownException();
		int lengthOfTest = bestCandidate.getTestCase().size();
		// ExecutionResult#getFirstPositionOfThrownException returns us the line position
		// zero-indexed, hence we need to add one to the line number.
		isInMethodException = ((numberOfExecutedStatements + 1) == lengthOfTest);
	}
	
	private void computeIsOutMethodException() {
		computeIsExceptionOccurred();
		computeIsInMethodException();
		isOutMethodException = isExceptionOccurred && !isInMethodException;
	}
	
	private void computeException() {
		if (!isExceptionOccurred()) {
			exception = null;
		}
		
		ExecutionResult lastExecutionResult = this.bestCandidate.getLastExecutionResult();
		exception = lastExecutionResult.getExceptionThrownAtPosition(lastExecutionResult.getFirstPositionOfThrownException());
	}
	
	/**
	 * Checks if an exception occurred during test case execution.
	 * 
	 * @return True if an exception occurred, false otherwise.
	 */
	public boolean isExceptionOccurred() {
		return isExceptionOccurred;		
	}
	
	/**
	 * See {@return}.
	 * 
	 * @return True if an exception occurred during test case execution,
	 * and the exception is classed as an in-method exception i.e. it occurred during the 
	 * execution of the MUT.
	 */
	public boolean isInMethodException() {
		return isInMethodException;
	}
	
	/**
	 * See {@return}.
	 * 
	 * @return True if an exception occurred during test case execution,
	 * and the exception is classed as an out-method exception i.e. it occurred during data preparation
	 * and not during execution of the MUT.
	 */
	public boolean isOutMethodException() {
		return isOutMethodException;
	}
	
	public FitnessFunction<T> getFitnessFunction() {
		return this.fitnessFunction;
	}
	
	public int getIteration() {
		return this.iteration;
	}
	
	public TestChromosome getTestCase() {
		return this.bestCandidate;
	}
	
	public Throwable getException() {
		return exception;
	}
}
