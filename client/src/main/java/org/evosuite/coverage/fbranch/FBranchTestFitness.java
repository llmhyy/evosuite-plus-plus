package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.setup.Call;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//		this.inconsistencyHappen = false;
		
		/**
		 * if the result does not exercise this branch node, we do not further process the detailed
		 * branch distance as we need to pass its parent branch node.
		 */
		Double value = null;
		if(this.goal.getValue()) {
			value = result.getTrace().getTrueDistances().get(this.goal.getBranch().getActualBranchId());
		}
		else {
			value = result.getTrace().getFalseDistances().get(this.goal.getBranch().getActualBranchId());
		}
		
		
		if(value == null){
			//TODO
			/**
			 * dynamosa should not execute here
			 */
//			value = findParentDistance(this.goal, result);
			return 1;
		}
		else if(value != 1){
			FlagEffectResult r = FlagEffectChecker.checkFlagEffect(goal);
			if(r.isInterproceduralFlag) {
				//TODO return modified value
				
			}
			else {
				return normalize(value);				
			}
		}
		
		double fitness = value;
		BranchCoverageGoal goal = this.goal;
		FlagEffectResult flagResult = FlagBranchEvaluator.isFlagMethod(goal);
		if (flagResult.isInterproceduralFlag) {
			List<Call> callContext = new ArrayList<>();
			callContext.add(flagResult.call);
			
			double interproceduralFitness = FlagBranchEvaluator.calculateInterproceduralFitness(
					flagResult.interproceduralFlagCall, 
					callContext, goal, result);
			double normalizedFitness = normalize(interproceduralFitness);
			
			return normalizedFitness;
		}
		
		return fitness;
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

}
