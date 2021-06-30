package evosuite.shell;

import java.util.HashMap;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

/**
 * Data object to collect exception based results.
 * Tracks the progress of a single branch.
 * @author Darien
 *
 */
public class ExceptionResultBranch<T extends Chromosome> {
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
}
