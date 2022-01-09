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
import org.evosuite.result.seedexpr.Event;
import org.evosuite.result.seedexpr.EventSequence;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

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
	
	//constants of constant-reading branch.
	private String constant;
	
	//Whether constant is sampled.
	private boolean isSampled;
	
	//Test case
	private String branchTestCase;
	
	
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
				constantAndSample(operand,branchInstruction);
				return true;
			}
		}
		return false;
	}
	
	private void constantAndSample(BytecodeInstruction operand, BytecodeInstruction branchInstruction) {
		AbstractInsnNode cons = operand.getASMNode();
		Object val = getValue(cons);
		constant = val.toString();
		
		if(EventSequence.events.isEmpty()) {
			setSampled(false);
		}else {
			for(Event e: EventSequence.events) {
				if(e.getType() != Event.branchCovering && e.getValue().equals(constant)) {
					setSampled(true);
				    break;
				}
			}
		}
		
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
	
	private Object getValue(AbstractInsnNode constant) {
		switch (constant.getOpcode()) {
		case Opcodes.LDC:
			return ((LdcInsnNode) constant).cst;
		case Opcodes.ICONST_0:
			return 0;
		case Opcodes.ICONST_1:
			return 1;
		case Opcodes.ICONST_2:
			return 2;
		case Opcodes.ICONST_3:
			return 3;
		case Opcodes.ICONST_4:
			return 4;
		case Opcodes.ICONST_5:
			return 5;
		case Opcodes.ICONST_M1:
			return -1;
		case Opcodes.LCONST_0:
			return 0L;
		case Opcodes.LCONST_1:
			return 1L;
		case Opcodes.DCONST_0:
			return 0.0;
		case Opcodes.DCONST_1:
			return 1.0;
		case Opcodes.FCONST_0:
			return 0.0F;
		case Opcodes.FCONST_1:
			return 1.0F;
		case Opcodes.FCONST_2:
			return 2.0F;
		case Opcodes.SIPUSH:
			return ((IntInsnNode) constant).operand;
		case Opcodes.BIPUSH:
			return ((IntInsnNode) constant).operand;
		default:
			throw new RuntimeException("Unknown constant: " + constant.getOpcode());
		}
	}

	public boolean isSampled() {
		return isSampled;
	}

	public void setSampled(boolean isSampled) {
		this.isSampled = isSampled;
	}
	
	public Object getConstant() {
		return constant;
	}

}
