package org.evosuite.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.TestChromosome;

/**
 * Data object to collect exception based results.
 * @author Darien
 *
 */
public class ExceptionResult<T extends Chromosome> implements Serializable {
	// Generated serialVersionUID.
	private static final long serialVersionUID = -3348202727990137906L;
	
	// Used to provide a 1-1 map from index to FitnessFunction, since FitnessFunction isn't suitable
	// as the key for a HashMap.
	private List<FitnessFunction<T>> indexList = new ArrayList<>();
	private HashMap<Integer, ExceptionResultBranch<T>> fitnessFunctionIndexToExceptionResult = new HashMap<>();
	
	// Quite slow, consider other approaches?
	private int getIndexOfFitnessFunction(FitnessFunction<T> fitnessFunction) {
		boolean isInList = indexList.contains(fitnessFunction);
		if (!isInList) {
			return -1;
		}
		
		return indexList.indexOf(fitnessFunction);
	}
	
	public ExceptionResultBranch<T> getResultByFitnessFunction(FitnessFunction<T> fitnessFunction) {
		return fitnessFunctionIndexToExceptionResult.get(getIndexOfFitnessFunction(fitnessFunction));
	}
	
	public void updateExceptionResult(int iteration, FitnessFunction<T> fitnessFunction, TestChromosome testChromosome) {
		ExceptionResultBranch<T> exceptionResultBranch = fitnessFunctionIndexToExceptionResult.get(getIndexOfFitnessFunction(fitnessFunction));
		boolean isResultNotFound = (exceptionResultBranch == null);
		if (isResultNotFound) {
			// Create a new ExceptionResultBranch and add it in.
			exceptionResultBranch = new ExceptionResultBranch<T>(fitnessFunction);
			indexList.add(fitnessFunction);
			fitnessFunctionIndexToExceptionResult.put(getIndexOfFitnessFunction(fitnessFunction), exceptionResultBranch);
		}
		
		ExceptionResultIteration<T> currentResultIteration = new ExceptionResultIteration<T>(fitnessFunction, iteration, testChromosome);
		exceptionResultBranch.addExceptionResultIteration(currentResultIteration);
	}
	
	public List<ExceptionResultBranch<T>> getAllResults() {
		return new ArrayList<ExceptionResultBranch<T>>(fitnessFunctionIndexToExceptionResult.values());
	}
	
	/**
	 * Returns the number of exceptions incurred.
	 * This *should* be the number of exceptions for a given method (uniquely identified by class + method name).
	 * @return
	 */
	public int getNumberOfExceptions() {
		int count = 0;
		for (ExceptionResultBranch<T> exceptionResultBranch : this.getAllResults()) {
			count += exceptionResultBranch.getNumberOfExceptions();
		}
		return count;
	}
}

