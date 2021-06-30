package org.evosuite.result;

import java.util.HashMap;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestChromosome;

/**
 * Data object to collect exception based results.
 * @author Darien
 *
 */
public class ExceptionResult<T extends Chromosome> {
	public HashMap<FitnessFunction<T>, ExceptionResultBranch<T>> fitnessFunctionToExceptionResult = new HashMap<>();
	
	public ExceptionResultBranch<T> getResultByFitnessFunction(FitnessFunction<T> fitnessFunction) {
		return fitnessFunctionToExceptionResult.get(fitnessFunction);
	}
	
	public void updateExceptionResult(int iteration, FitnessFunction<T> fitnessFunction, TestChromosome testChromosome) {
		ExceptionResultBranch<T> exceptionResultBranch = fitnessFunctionToExceptionResult.get(fitnessFunction);
		boolean isResultNotFound = (exceptionResultBranch == null);
		if (isResultNotFound) {
			// Create a new ExceptionResultBranch and add it in.
			exceptionResultBranch = new ExceptionResultBranch<T>(fitnessFunction);			
			fitnessFunctionToExceptionResult.put(fitnessFunction, exceptionResultBranch);
		}
		
		ExceptionResultIteration<T> currentResultIteration = new ExceptionResultIteration<T>(fitnessFunction, iteration, testChromosome);
		exceptionResultBranch.addExceptionResultIteration(currentResultIteration);
	}
}

