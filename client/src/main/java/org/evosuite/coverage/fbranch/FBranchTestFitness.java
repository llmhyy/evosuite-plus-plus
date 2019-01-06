package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.evosuite.coverage.ControlFlowDistance;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * F stands for flag fitness
 * 
 * @author linyun
 *
 */
public class FBranchTestFitness extends TestFitnessFunction {

	private static final long serialVersionUID = -3538507758440177708L;

	private final BranchCoverageGoal branchGoal;
	private double epsilon = 0.001;

	public FBranchTestFitness(BranchCoverageGoal branchGoal) {
		this.branchGoal = branchGoal;
	}

	public Branch getBranch() {
		return this.branchGoal.getBranch();
	}

	private String getReturnType(String signature) {
		String r = signature.substring(signature.indexOf(")") + 1);
		return r;
	}

	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		
		double fitness = getDistance(result);
		if(fitness == 0){
			return fitness;
		}
		
		InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(this.branchGoal);
		if (flagResult.isInterproceduralFlag) {
			double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall,
					this.branchGoal, result);
			return interproceduralFitness;
		}
		
		return fitness;
	}

	private double calculateInterproceduralFitness(BytecodeInstruction flagCallInstruction,
			BranchCoverageGoal branchGoal, ExecutionResult result) {
		RawControlFlowGraph calledGraph = flagCallInstruction.getCalledCFG();
		if(calledGraph == null){
			return 1;
		}
		
//		System.currentTimeMillis();
		Set<BytecodeInstruction> exits = calledGraph.determineExitPoints();

		List<Double> returnFitnessList = new ArrayList<>();
		for (BytecodeInstruction returnIns : exits) {
			double f = calculateReturnInsFitness(returnIns, branchGoal, calledGraph, result);
			
			if(f != -1){
				returnFitnessList.add(f);				
			}
			
		}

		double sum = this.epsilon;
		for(Double f: returnFitnessList){
			sum += 1/(f+this.epsilon);
		}
		
		return 1/sum;
	}

	private double calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result) {
		BytecodeInstruction sourceIns = getSource(calledGraph, returnIns);
		Branch newDepBranch = sourceIns.getControlDependentBranch();
		boolean goalValue = sourceIns.getControlDependency(newDepBranch).getBranchExpressionValue();
//		System.currentTimeMillis();
		while(!checkCovered(result, newDepBranch)){
			BytecodeInstruction originBranchIns = newDepBranch.getInstruction();
			if(newDepBranch.getInstruction().getControlDependentBranch()==null){
				return -1;
			}
			
			newDepBranch = newDepBranch.getInstruction().getControlDependentBranch();
			goalValue = originBranchIns.getControlDependency(newDepBranch).getBranchExpressionValue(); 
		}
		
		double fitness = checkDistance(result, goalValue, newDepBranch);
		if(fitness==0){
			return -1;
		}
		
		BranchCoverageGoal newGoal = new BranchCoverageGoal(newDepBranch, goalValue, 
				newDepBranch.getClassName(), newDepBranch.getMethodName(), newDepBranch.getInstruction().getLineNumber());
		
		//when return a constant 0/1
		if (sourceIns.isConstant()) {
			InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(newGoal);
			if (flagResult.isInterproceduralFlag) {
				double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall,
						newGoal, result);
				return interproceduralFitness;
			}
			else{
				return fitness;
			}			
		} else {
			// compute
		}

		System.currentTimeMillis();
		return 0;
	}

	private double checkDistance(ExecutionResult result, boolean goalValue, Branch newDepBranch) {
		if(goalValue){
			return result.getTrace().getTrueDistances().get(newDepBranch.getActualBranchId());
		}
		else{
			return result.getTrace().getFalseDistances().get(newDepBranch.getActualBranchId());
		}
	}
	
	private boolean checkCovered(ExecutionResult result, Branch newDepBranch){
		if(result.getTrace().getCoveredTrueBranches().contains(newDepBranch.getActualBranchId())){
			return true;
		}
		else if(result.getTrace().getCoveredFalseBranches().contains(newDepBranch.getActualBranchId())){
			return true;
		}
		
		return false;
	}

	private boolean checkBranchDirection(ExecutionResult result, Branch newDepBranch) {
		if(result.getTrace().getTrueDistance(newDepBranch.getActualBranchId())==0){
			return false;
		}
		else if(result.getTrace().getFalseDistance(newDepBranch.getActualBranchId())==0){
			return true;
		}
		
		return false;
	}

	class InterproceduralFlagResult{
		BytecodeInstruction interproceduralFlagCall;
		boolean isInterproceduralFlag;
		public InterproceduralFlagResult(BytecodeInstruction interproceduralFlagCall, boolean isInterproceduralFlag) {
			super();
			this.interproceduralFlagCall = interproceduralFlagCall;
			this.isInterproceduralFlag = isInterproceduralFlag;
		}
	}
	
	private InterproceduralFlagResult isInterproceduralFlagProblem(BranchCoverageGoal goal) {
		BytecodeInstruction instruction = goal.getBranch().getInstruction();
		BytecodeInstruction interproceduralFlagCall = instruction.getSourceOfStackInstruction(0);
		boolean isInterproceduralFlag = false;
		if (interproceduralFlagCall != null && 
				interproceduralFlagCall.getASMNode() instanceof MethodInsnNode) {
			MethodInsnNode mNode = (MethodInsnNode)interproceduralFlagCall.getASMNode();
			String desc = mNode.desc;
			String returnType = getReturnType(desc);
			isInterproceduralFlag = returnType.equals("Z");
		}
		
		InterproceduralFlagResult result = new InterproceduralFlagResult(interproceduralFlagCall, isInterproceduralFlag);
		return result;
	}

	private double getDistance(ExecutionResult result) {
		Double fitness = null;
		if (branchGoal.getValue() == true) {
			fitness = result.getTrace().getTrueDistances().get(branchGoal.getBranch().getActualBranchId());
		} else {
			fitness = result.getTrace().getFalseDistances().get(branchGoal.getBranch().getActualBranchId());
		}

		if (fitness == null) {
			fitness = 1.0;
		}

		return fitness;
	}

	private BytecodeInstruction getSource(RawControlFlowGraph calledGraph, BytecodeInstruction returnIns) {
		// TODO we need more fine analysis technique to determine the source.
		return returnIns.getPreviousInstruction();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getBranchGoal() == null) ? 0 : getBranchGoal().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FBranchTestFitness other = (FBranchTestFitness) obj;
		if (getBranchGoal() == null) {
			if (other.getBranchGoal() != null)
				return false;
		} else if (!getBranchGoal().equals(other.getBranchGoal()))
			return false;
		return true;
	}

	@Override
	public String getTargetClass() {
		return getBranchGoal().getClassName();
	}

	@Override
	public String getTargetMethod() {
		return getBranchGoal().getMethodName();
	}

	public BranchCoverageGoal getBranchGoal() {
		return branchGoal;
	}

}
