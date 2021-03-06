package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.setup.Call;
import org.evosuite.setup.CallContext;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;

/**
 * F stands for flag fitness
 * 
 * @author linyun
 *
 */
public class FBranchTestFitness extends BranchCoverageTestFitness {

	private static final long serialVersionUID = -3538507758440177708L;
//	private static Logger logger = LoggerFactory.getLogger(FBranchTestFitness.class);

//	private final BranchCoverageGoal goal;
//	private boolean inconsistencyHappen = false;

	public FBranchTestFitness(BranchCoverageGoal branchGoal) {
		super(branchGoal);
	}

	public Branch getBranch() {
		return this.goal.getBranch();
	}

	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		/**
		 * if the result does not exercise this branch node, we do not further process the detailed
		 * branch distance as we need to pass its parent branch node.
		 */

		Double value = getContextSensitiveBranchDistance(result, this.goal.getValue(), this.goal);	
		if(value != null && value == 0) {
			return 0;
		}
		
		System.currentTimeMillis();
		
		FlagEffectResult r = FlagEffectEvaluator.checkFlagEffect(goal);
		if (r == null || !r.hasFlagEffect || value==null) {
			if (value == null) {
				Set<BranchCoverageGoal> set = new HashSet<BranchCoverageGoal>();
				set.add(this.goal);
				Double branchDisntanceWithApproachLevel = getBranchDisntanceWithApproachLevel(result, this.goal, set);
				if (branchDisntanceWithApproachLevel != null) {
					return branchDisntanceWithApproachLevel;
				}

				return 1;
			} else {
				return normalize(value);
			}
		} else {
			List<Call> callContext = new ArrayList<>();
			callContext.add(r.call);

			double interproceduralFitness = FlagEffectEvaluator
					.calculateInterproceduralFitness(r.interproceduralFlagCall, callContext, goal, result);
			double normalizedFitness = normalize(interproceduralFitness);

			System.currentTimeMillis();
			return normalizedFitness;
		}
	}

	private Double getContextSensitiveBranchDistance(ExecutionResult result, boolean expressionValue, BranchCoverageGoal goal) {
		Double value = null;
		if (this.callContext == null) {
			Call call = new Call(goal.getClassName(), goal.getMethodName(), goal.getLineNumber());
			List<Call> callList = new ArrayList<Call>();
			callList.add(call);
			CallContext context = new CallContext(callList);
			this.callContext = context;
		}
		
		if(Properties.REQUIRE_MAX_BRANCH_DISTANCE) {
			Map<CallContext, Map<List<Integer>, Double>> contextDistanceMap = expressionValue
					? result.getTrace().getContextIterationTrueMap().get(goal.getBranch().getActualBranchId())
							: result.getTrace().getContextIterationFalseMap().get(goal.getBranch().getActualBranchId());
			if (contextDistanceMap != null) {
				for (Entry<CallContext, Map<List<Integer>, Double>> contextMap : contextDistanceMap.entrySet()) {
					if (isSameContextVisited(contextMap.getKey())) {
						double sum = 0;
						int count = 0;
						for(List<Integer> trace: contextMap.getValue().keySet()) {
							double v = contextMap.getValue().get(trace);
							if(v!=0) {
//								sum += v;
								if(sum < v) {
									sum = v;
								}
								
								count ++;
							}
						}
						
						if(sum > 0) {
//							sum = sum/count;
							value = sum;
						}
						
						break;
					}
				}
			}
		}
		else {
			Map<CallContext, Double> contextDistanceMap = expressionValue
					? result.getTrace().getTrueDistancesContext().get(goal.getBranch().getActualBranchId())
							: result.getTrace().getFalseDistancesContext().get(goal.getBranch().getActualBranchId());
			if (contextDistanceMap != null) {
				for (Entry<CallContext, Double> contextMap : contextDistanceMap.entrySet()) {
					if (isSameContextVisited(contextMap.getKey())) {
						value = contextMap.getValue();
						break;
					}
				}
			}
			
		}
		
		return value;
	}
	
	private Double getBranchDisntanceWithApproachLevel(ExecutionResult result, BranchCoverageGoal goal, Set<BranchCoverageGoal> set) {
		Set<ControlDependency> cds = goal.getBranch().getInstruction().getControlDependencies();
		
		if(cds.isEmpty()) {
			return null;
		}
		else {
			ControlDependency cd = cds.iterator().next();
			String className = cd.getBranch().getInstruction().getClassName();
			String methodName = cd.getBranch().getInstruction().getMethodName();
			BranchCoverageGoal newGoal = new BranchCoverageGoal(cd, className, methodName);
			
			if(set.contains(newGoal)) {
				return null;
			}
			
			set.add(newGoal);
			
			Double value = getContextSensitiveBranchDistance(result, cd.getBranchExpressionValue(), newGoal);		
			if(value != null) {
				return 1 + normalize(value);
			}
			else {	
				Double subValue = getBranchDisntanceWithApproachLevel(result, newGoal, set);
				if(subValue != null) {
					return 1 + subValue;
				}
				else {
					System.currentTimeMillis();
					return null;
				}
			}
		}
		
		
	}

	@Override
	public int compareTo(TestFitnessFunction other) {
		if (other instanceof FBranchTestFitness) {
			FBranchTestFitness otherBranchFitness = (FBranchTestFitness) other;
			return getBranchGoal().compareTo(otherBranchFitness.getBranchGoal());
		} else if (other instanceof BranchCoverageTestFitness) {
			BranchCoverageTestFitness otherBranchFitness = (BranchCoverageTestFitness) other;
			return getBranchGoal().compareTo(otherBranchFitness.getBranchGoal());
		}
		return compareClassName(other);
	}

	public BranchCoverageGoal getBranchGoal() {
		return goal;
	}

//	public boolean isInconsistencyHappen() {
//		return inconsistencyHappen;
//	}
//
//	public void setInconsistencyHappen(boolean inconsistencyHappen) {
//		this.inconsistencyHappen = inconsistencyHappen;
//	}

	public String getClassName() {
		return this.goal.getClassName();
	}

	public String getMethod() {
		return this.goal.getMethodName();
	}

	public boolean getBranchExpressionValue() {
		return this.goal.getValue();
	}
	
	private boolean isSameContextVisited(CallContext context) {
		if (context.size() == this.callContext.size()) {
			if (context.size() == 0) {
				return true;
			}

			boolean isOverallMatch = true;
			for (int i = 0; i < context.size(); i++) {
				Call thatCall = context.getContext().get(i);
				Call thisCall = this.callContext.getContext().get(i);

				boolean match = thisCall.getClassName().equals(thatCall.getClassName())
						&& thatCall.getMethodName().equals(thisCall.getMethodName());

				isOverallMatch = isOverallMatch && match;

				if (!isOverallMatch) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
