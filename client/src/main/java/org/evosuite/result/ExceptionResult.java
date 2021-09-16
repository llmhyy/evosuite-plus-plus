package org.evosuite.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.BranchCoverageTestFitness;
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
	
	// Although FitnessFunction<T> doesn't implement hashCode, the fitness function that we typically use
	// (BranchCoverageTestFitness) does, so here we are. Maybe we should change the types in the code?
	private HashMap<FitnessFunction<T>, ExceptionResultBranch<T>> fitnessFunctionIndexToExceptionResult = new HashMap<>();
	
	public ExceptionResultBranch<T> getResultByFitnessFunction(FitnessFunction<T> fitnessFunction) {
		return fitnessFunctionIndexToExceptionResult.get(fitnessFunction);
	}
	
	public void updateExceptionResult(int iteration, FitnessFunction<T> fitnessFunction, TestChromosome testChromosome) {
		ExceptionResultBranch<T> exceptionResultBranch = fitnessFunctionIndexToExceptionResult.get(fitnessFunction);
		boolean isResultNotFound = (exceptionResultBranch == null);
		if (isResultNotFound) {
			// Create a new ExceptionResultBranch and add it in.
			exceptionResultBranch = new ExceptionResultBranch<T>(fitnessFunction);
			fitnessFunctionIndexToExceptionResult.put(fitnessFunction, exceptionResultBranch);
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
	
	public int getNumberOfInMethodExceptions() {
		int count = 0;
		for (ExceptionResultBranch<T> exceptionResultBranch : this.getAllResults()) {
			count += exceptionResultBranch.getNumberOfInMethodExceptions();
		}
		return count;
	}
	
	public int getNumberOfOutMethodExceptions() {
		int count = 0;
		for (ExceptionResultBranch<T> exceptionResultBranch : this.getAllResults()) {
			count += exceptionResultBranch.getNumberOfOutMethodExceptions();
		}
		return count;
	}
	
	public String getBreakdownOfInMethodExceptions() {
		Map<String, Integer> exceptionCount = new HashMap<>();
		for (ExceptionResultBranch<T> exceptionResultBranch : this.getAllResults()) {
			List<Throwable> inMethodExceptions = exceptionResultBranch.getInMethodExceptions();
			for (Throwable inMethodException : inMethodExceptions) {
				String exceptionName = inMethodException.getClass().getCanonicalName();
				boolean isInMap = exceptionCount.containsKey(exceptionName);
				if (isInMap) {
					exceptionCount.put(exceptionName, exceptionCount.get(exceptionName) + 1);
				} else {
					exceptionCount.put(exceptionName, 1);
				}
			}
		}
		
		return stringifyMap(exceptionCount);
	}
	
	public String getBreakdownOfOutMethodExceptions() {
		Map<String, Integer> exceptionCount = new HashMap<>();
		for (ExceptionResultBranch<T> exceptionResultBranch : this.getAllResults()) {
			List<Throwable> outMethodExceptions = exceptionResultBranch.getOutMethodExceptions();
			for (Throwable outMethodException : outMethodExceptions) {
				String exceptionName = outMethodException.getClass().getCanonicalName();
				boolean isInMap = exceptionCount.containsKey(exceptionName);
				if (isInMap) {
					exceptionCount.put(exceptionName, exceptionCount.get(exceptionName) + 1);
				} else {
					exceptionCount.put(exceptionName, 1);
				}
			}
		}
		
		return stringifyMap(exceptionCount);
	}
	
	public int getNumberOfUncoveredBranchesWithException(List<BranchInfo> uncoveredBranches) {
		int count = 0;
		for (ExceptionResultBranch<T> exceptionResultBranch : this.getAllResults()) {
			ExceptionResultIteration<T> lastIteration = exceptionResultBranch.getLastIteration();
			boolean isException = lastIteration.isExceptionOccurred();
			boolean isCovered = isBranchCovered(exceptionResultBranch, uncoveredBranches);
			if (isException && !isCovered) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Turns a map into a multiline entry suitable for CSV use.
	 * Note that multiline values in CSV files must be surrounded by quotes.
	 * @param map
	 * @return
	 */
	private String stringifyMap(Map<?,?> map) {
		if (map.isEmpty()) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		String divider = ":";
		sb.append("\"");
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			sb.append(entry.getKey().toString());
			sb.append(divider);
			sb.append(entry.getValue().toString());
			sb.append("\n");
		}
		return sb.toString().trim() + "\"";
	}
	
	private boolean isBranchCovered(ExceptionResultBranch<T> exceptionResultBranch, List<BranchInfo> uncoveredBranches) {
		BranchCoverageTestFitness fitnessFunction = (BranchCoverageTestFitness) exceptionResultBranch.getFitnessFunction();
		boolean isCovered = true;
		for (BranchInfo uncoveredBranch : uncoveredBranches) {
			boolean isClassMatch = (fitnessFunction.getClassName() == uncoveredBranch.getClassName());
			boolean isMethodMatch = (fitnessFunction.getMethod() == uncoveredBranch.getMethodName());
			boolean isLineNumberMatch = (fitnessFunction.getBranch().getInstruction().getLineNumber() == uncoveredBranch.getLineNo());
			if (isClassMatch && isMethodMatch && isLineNumberMatch) {
				isCovered = false;
				break;
			}
		}
		return isCovered;
		
	}
}

