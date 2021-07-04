package org.evosuite.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

/**
 * Data object to collect exception based results.
 * Tracks the progress of a single branch.
 * @author Darien
 *
 */
public class ExceptionResultBranch<T extends Chromosome> implements Serializable {
	// generated serialVersionUID.
	private static final long serialVersionUID = -7781936582744596947L;

	// The threshold before we consider an exception non-trivial.
	// This represents the number of iterations that an exception must persist for before 
	// we consider it as a non-trivial exception.
	private static final int NON_TRIVIAL_EXCEPTION_ITERATION_THRESHOLD = 5;
	
	// The branch that we are tracking.
	private FitnessFunction<T> fitnessFunction;
	
	private HashMap<Integer, ExceptionResultIteration<T>> iterationToExceptionResult = new HashMap<>();	
	
	public ExceptionResultBranch(FitnessFunction<T> fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}
	
	public void addExceptionResultIteration(ExceptionResultIteration<T> exceptionResultIteration) {
		this.iterationToExceptionResult.put(exceptionResultIteration.getIteration(), exceptionResultIteration);
	}
	
	public FitnessFunction<T> getFitnessFunction() {
		return this.fitnessFunction;
	}
	
	public ExceptionResultIteration<T> getResultByIteration(int iteration) {
		return iterationToExceptionResult.get(iteration);
	}
	
	public int getNumberOfIterations() {
		return iterationToExceptionResult.keySet().size();
	}
	
	public int getNumberOfExceptions() {
		int numberOfExceptions = 0;
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isExceptionOccurred = exceptionResultIteration.isExceptionOccurred();
			if (isExceptionOccurred) {
				numberOfExceptions++;
			}
		}
		return numberOfExceptions;
	}
	
	public int getNumberOfInMethodExceptions() {
		int numberOfInMethodExceptions = 0;
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isInMethodException = exceptionResultIteration.isInMethodException();
			if (isInMethodException) {
				numberOfInMethodExceptions++;
			}
		}
		return numberOfInMethodExceptions;
	}
	
	public int getNumberOfOutMethodExceptions() {
		int numberOfOutMethodExceptions = 0;
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isOutMethodException = exceptionResultIteration.isExceptionOccurred() && !exceptionResultIteration.isInMethodException();
			if (isOutMethodException) {
				numberOfOutMethodExceptions++;
			}
		}
		return numberOfOutMethodExceptions;
	}
	
	public Set<Throwable> getInMethodExceptions() {
		Set<Throwable> inMethodExceptions = new HashSet<>();
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isInMethodException = exceptionResultIteration.isInMethodException();
			if (isInMethodException) {
				inMethodExceptions.add(exceptionResultIteration.getException());
			}
		}
		return inMethodExceptions;
	}
	
	public Set<Throwable> getOutMethodExceptions() {
		Set<Throwable> outMethodExceptions = new HashSet<>();
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isOutMethodException = exceptionResultIteration.isOutMethodException();
			if (isOutMethodException) {
				outMethodExceptions.add(exceptionResultIteration.getException());
			}
		}
		return outMethodExceptions;
	}
	
	public List<Map<Integer, Throwable>> lookForNonTrivialExceptions() {
		List<Map<Integer, Throwable>> toReturn = new ArrayList<>();
		Map<Integer, Throwable> iterationToException = new HashMap<>();
		for (Entry<Integer, ExceptionResultIteration<T>> entry : iterationToExceptionResult.entrySet()) {
			Integer iteration = entry.getKey();
			Throwable possibleException = entry.getValue().isExceptionOccurred() ? entry.getValue().getException() : null;
			iterationToException.put(iteration, possibleException);
		}
		
		// We may not be guaranteed that the iterations will be consecutive
		// So to guard against this we just sort the keys and use those to iterate over the iterations
		// We want to look for cases where the types of the exceptions match for a number of iterations
		// We flag those as non-trivial exceptions, place them into a set, and return a list of these sets.
		Integer[] pseudoIterator = iterationToException.keySet().toArray(new Integer[0]);
		Arrays.sort(pseudoIterator);
		
		Class<?> lastIterationExceptionType = null;
		int consecutiveCounter = 0;
		
		// Used for tracking when the chain of consecutive exceptions started
		// We use this to grab all the consecutive exceptions.
		int firstIteration = 0;
		
		// Flag whether we start storing exceptions into our map of iteration to throwable.
		boolean isSavingExceptions = false;
		Map<Integer, Throwable> currentMap = new HashMap<>();
		
		for (int i = 0; i < pseudoIterator.length; i++) {
			Integer currentIteration = pseudoIterator[i];
			Throwable currentException = iterationToException.get(currentIteration);
			Class<?> currentIterationExceptionType = (currentException == null ? null : currentException.getClass());
			boolean isExceptionTypeSame = (
				lastIterationExceptionType == null ?
					false : 
					lastIterationExceptionType.equals(currentIterationExceptionType)
			);
			
			// Possibly coming off a change
			// Reset all flags and go back to looking for chains of consecutive exceptions.
			if (!isExceptionTypeSame) {
				isSavingExceptions = false;
				if (currentMap.size() > 0) {
					toReturn.add(currentMap);
					currentMap = new HashMap<>();
				}
				consecutiveCounter = 0;
				continue;
			}
			
			if (isExceptionTypeSame) {
				if (isSavingExceptions) {
					currentMap.put(currentIteration, currentException);
				}
				consecutiveCounter++;
			}
			
			boolean isReachedNonTrivialExceptionThreshold = (consecutiveCounter == NON_TRIVIAL_EXCEPTION_ITERATION_THRESHOLD);
			if (isReachedNonTrivialExceptionThreshold) {
				isSavingExceptions = true;
				// Record the exceptions from previous iterations.
				for (int j = firstIteration; j <= currentIteration; j++) {
					currentMap.put(j, iterationToException.get(j));
				}
			}
		}
		
		// Edge case: all the iterations had the same exception type
		// Since the chain was unbroken throughout, the map was never stored into the list to return.
		// Do a final check to store the last map in before returning.
		boolean isCurrentMapNonTrivial = (currentMap.size() > 0);
		boolean isCurrentMapNotInList = (!(toReturn.contains(currentMap)));
		if (isCurrentMapNonTrivial && isCurrentMapNotInList) {
			toReturn.add(currentMap);
		}
		
		return toReturn;
	}
}
