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

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.cfg.BytecodeInstruction;

/**
 * Data object to collect exception based results.
 * Tracks the progress of a single branch.
 * @author Darien
 *
 */
public class ExceptionResultBranch<T extends Chromosome> implements Serializable {
	// generated serialVersionUID.
	private static final long serialVersionUID = -7781936582744596947L;
	
	// The branch that we are tracking.
	private FitnessFunction<T> fitnessFunction;
	
	private HashMap<Integer, ExceptionResultIteration<T>> iterationToExceptionResult = new HashMap<>();	
	
	// Whether this branch is covered.
	private boolean isCovered;
	
	// Whether this branch is a constant-reading branch
	// i.e. whether it has >= 1 constant operand
	private boolean isConstantReadingBranch;
	
	public ExceptionResultBranch(FitnessFunction<T> fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
		
		if (!(fitnessFunction instanceof BranchCoverageTestFitness)) {
			throw new IllegalArgumentException("The fitness function passed in must be a BranchCoverageTestFitness.");
		}
		
		this.isConstantReadingBranch = this.isConstantReadingBranch((BranchCoverageTestFitness) fitnessFunction);
	}
	
	// Each BranchCoverageTestFitness is associated with a target branch.
	// This method checks if that target branch is a constant-reading branch or not.
	private boolean isConstantReadingBranch(BranchCoverageTestFitness fitnessFunction) {
		Branch targetBranch = fitnessFunction.getBranch();
		BytecodeInstruction branchInstruction = targetBranch.getInstruction();
		List<BytecodeInstruction> branchOperands = branchInstruction.getOperands();
		for (BytecodeInstruction operand : branchOperands) {
			boolean isConstantOperand = operand.isConstant();
			if (isConstantOperand) {
				return true;
			}
		}
		return false;
	}
	
	public void addExceptionResultIteration(ExceptionResultIteration<T> exceptionResultIteration) {
		this.iterationToExceptionResult.put(exceptionResultIteration.getIteration(), exceptionResultIteration);
	}
	
	public FitnessFunction<T> getFitnessFunction() {
		return this.fitnessFunction;
	}
	
	public boolean isConstantReadingBranch() {
		return isConstantReadingBranch;
	}
	
	public void setIsConstantReadingBranch(boolean value) {
		isConstantReadingBranch = value;
	}
	
	public ExceptionResultIteration<T> getResultByIteration(int iteration) {
		return iterationToExceptionResult.get(iteration);
	}
	
	/*
	 * Returns a list of results sorted by iteration number.
	 * This is slightly slower, but preserves the order of iterations.
	 */
	public List<ExceptionResultIteration<T>> getAllResults() {
		List<ExceptionResultIteration<T>> results = new ArrayList<>(iterationToExceptionResult.values());
		results.sort((iteration, anotherIteration) -> {
			return Integer.compare(iteration.getIteration(), anotherIteration.getIteration());
		});
		return results;
	}
	
	public ExceptionResultIteration<T> getLastIteration() {
		int largestIteration = -1;
		for (Integer i : iterationToExceptionResult.keySet()) {
			if (i > largestIteration) {
				largestIteration = i;
			}
		}
		return iterationToExceptionResult.get(largestIteration);
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
			boolean isOutMethodException = exceptionResultIteration.isOutMethodException();
			if (isOutMethodException) {
				numberOfOutMethodExceptions++;
			}
		}
		return numberOfOutMethodExceptions;
	}
	
	public List<Throwable> getExceptions() {
		List<Throwable> exceptions = new ArrayList<>();
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isException = exceptionResultIteration.isExceptionOccurred();
			if (isException) {
				exceptions.add(exceptionResultIteration.getException());
			}
		}
		return exceptions;
	}
	
	public List<Throwable> getInMethodExceptions() {
		List<Throwable> inMethodExceptions = new ArrayList<>();
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isInMethodException = exceptionResultIteration.isInMethodException();
			if (isInMethodException) {
				inMethodExceptions.add(exceptionResultIteration.getException());
			}
		}
		return inMethodExceptions;
	}
	
	public List<Throwable> getOutMethodExceptions() {
		List<Throwable> outMethodExceptions = new ArrayList<>();
		for (ExceptionResultIteration<T> exceptionResultIteration : iterationToExceptionResult.values()) {
			boolean isOutMethodException = exceptionResultIteration.isOutMethodException();
			if (isOutMethodException) {
				outMethodExceptions.add(exceptionResultIteration.getException());
			}
		}
		return outMethodExceptions;
	}
	
	public boolean doesLastIterationHaveException() {
		List<ExceptionResultIteration<T>> iterations = this.getAllResults();
		ExceptionResultIteration<T> lastIteration = iterations.get(iterations.size() - 1);
		return lastIteration.isExceptionOccurred();
	}
	
	public boolean doesLastIterationHaveInMethodException() {
		if (!doesLastIterationHaveException()) {
			return false;
		}
		
		List<ExceptionResultIteration<T>> iterations = this.getAllResults();
		ExceptionResultIteration<T> lastIteration = iterations.get(iterations.size() - 1);
		return lastIteration.isInMethodException();
	}
	
	public boolean doesLastIterationHaveOutMethodException() {
		if (!doesLastIterationHaveException()) {
			return false;
		}
		
		List<ExceptionResultIteration<T>> iterations = this.getAllResults();
		ExceptionResultIteration<T> lastIteration = iterations.get(iterations.size() - 1);
		return lastIteration.isOutMethodException();
	}
}
