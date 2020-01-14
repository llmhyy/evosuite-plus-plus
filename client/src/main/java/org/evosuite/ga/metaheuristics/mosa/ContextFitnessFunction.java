package org.evosuite.ga.metaheuristics.mosa;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.setup.Call;
import org.evosuite.setup.CallContext;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;

public class ExceptionFitnessFunction<T extends Chromosome> extends FitnessFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CallContext context;
	private FitnessFunction<T> fitnessFunction;

	public ExceptionFitnessFunction(CallContext context, FitnessFunction<T> fitnessFunction) {
		super();
		this.context = context;
		this.fitnessFunction = fitnessFunction;
	}

	public CallContext getContext() {
		return context;
	}

	public void setContext(CallContext context) {
		this.context = context;
	}

	public FitnessFunction<T> getFitnessFunction() {
		return fitnessFunction;
	}

	public void setFitnessFunction(FitnessFunction<T> fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((fitnessFunction == null) ? 0 : fitnessFunction.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExceptionFitnessFunction<T> other = (ExceptionFitnessFunction<T>) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (fitnessFunction == null) {
			if (other.fitnessFunction != null)
				return false;
		} else if (!fitnessFunction.equals(other.fitnessFunction))
			return false;
		return true;
	}

	public boolean isInTarget() {
		for(Call call: this.context.getContext()) {
			if(call.getClassName().equals(Properties.TARGET_CLASS) &&
					call.getMethodName().equals(call.getMethodName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * TODO (high) ziheng, check whether the goal's context is in blacklist.
	 * 
	 * @param exception
	 * @param exceptionOccuringProbability
	 * @return
	 */
	public boolean isContextAvoidable() {
		//TODO
		return false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public double getFitness(Chromosome individual) {
		//TODO check whether the result can match the call context
		if(individual instanceof TestChromosome) {
			TestChromosome test = (TestChromosome)individual;
			ExecutionResult origResult = test.getLastExecutionResult();
			
			return this.fitnessFunction.getFitness((T) individual);
		}
		
		return 1;
	}

	@Override
	public boolean isMaximizationFunction() {
		// TODO Auto-generated method stub
		return false;
	}

}
