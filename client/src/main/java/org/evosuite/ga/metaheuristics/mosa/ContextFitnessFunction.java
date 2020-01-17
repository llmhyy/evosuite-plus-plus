package org.evosuite.ga.metaheuristics.mosa;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.setup.Call;
import org.evosuite.setup.CallContext;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;

public class ContextFitnessFunction<T extends Chromosome> extends TestFitnessFunction implements BranchFitness {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CallContext context;
	private FitnessFunction<T> fitnessFunction;

	public ContextFitnessFunction(CallContext context, FitnessFunction<T> fitnessFunction) {
		super();
		this.context = context;
		this.setFitnessFunction(fitnessFunction);
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
		if(fitnessFunction instanceof BranchFitness) {
			((BranchFitness)fitnessFunction).setContext(this.context);
		}
	}
	

	@Override
	public String toString() {
		return "[context=" + context + "\n, fitnessFunction=" + fitnessFunction + "]";
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
		ContextFitnessFunction<T> other = (ContextFitnessFunction<T>) obj;
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
		String targetMethodSig = Properties.TARGET_CLASS + "." + Properties.TARGET_METHOD;			
			String callSig = BranchEnhancementUtil.covert2Sig(this.context.getContext().get(0));
			if(targetMethodSig.equals(callSig)) {
				return true;
			}
		return false;
	}
	
	/**
	 * @param exception
	 * @param exceptionOccuringProbability
	 * @return
	 */
	public boolean isAvoidable() {
		if(this.context.getContext().size() > 0) {
			Call call = this.context.getContext().get(0);
			return CallBlackList.isInBlackList(call);
		}
		
		return false;
	}

	private boolean isContextVisited(Set<CallContext> allContext) {
		mainLoop : for(CallContext context: allContext) {
			if(context.size() == this.context.size()) {
				if(context.size() == 0) {
					return true;
				}
				
				boolean isOverallMatch = true;
				for(int i=0; i<context.size(); i++) {
					Call thatCall = context.getContext().get(i); 
					Call thisCall = this.context.getContext().get(i);
					
					boolean match = thisCall.getClassName().equals(thatCall.getClassName()) &&
						thatCall.getMethodName().contains(thisCall.getMethodName());
					
					isOverallMatch = isOverallMatch && match;
					
					if(!isOverallMatch) {
						continue mainLoop;
					}
				}
				
				return true;
			}
		}
		return false;
	}

	private Set<CallContext> retrieveAllContext(ExecutionResult origResult) {
		
		Set<CallContext> set = new HashSet<CallContext>();
		
		for(Integer i: origResult.getTrace().getTrueDistancesContext().keySet()) {
			Map<CallContext, Double> value = origResult.getTrace().getTrueDistancesContext().get(i);
			for(CallContext context: value.keySet()) {
				set.add(context);
			}
		}
		
		for(Integer i: origResult.getTrace().getFalseDistancesContext().keySet()) {
			Map<CallContext, Double> value = origResult.getTrace().getFalseDistancesContext().get(i);
			for(CallContext context: value.keySet()) {
				set.add(context);
			}
		}
		
		return set;
	}

	@Override
	public boolean isMaximizationFunction() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		
		if(fitnessFunction instanceof BranchFitness) {
			BranchCoverageGoal goal = ((BranchFitness)fitnessFunction).getBranchGoal();
			if(goal != null) {
//				Set<CallContext> allContext = retrieveAllContext(result);
//				if(isContextVisited(allContext)) {
//				}
				double fitness = this.fitnessFunction.getFitness((T) individual);
				return fitness;
			}
		}
		
		
		return 100000d;
	}

	@Override
	public int compareTo(TestFitnessFunction other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTargetClass() {
		return null;
	}

	@Override
	public String getTargetMethod() {
		return null;
	}

	@Override
	public BranchCoverageGoal getBranchGoal() {
		
		if(fitnessFunction instanceof BranchFitness) {
			return ((BranchFitness)fitnessFunction).getBranchGoal();
		}
		
		return null;
	}

}
