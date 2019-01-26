package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.graphs.cfg.BasicBlock;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.objectweb.asm.tree.MethodInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * F stands for flag fitness
 * 
 * @author linyun
 *
 */
public class FBranchTestFitness extends TestFitnessFunction {

	private static final long serialVersionUID = -3538507758440177708L;
	private static Logger logger = LoggerFactory.getLogger(FBranchTestFitness.class);

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
		double fitness = checkOverallDistance(result, this.branchGoal.getValue(), this.branchGoal.getBranch());
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
		String signature = flagCallInstruction.getCalledMethodsClass() + "." + flagCallInstruction.getCalledMethod();
		if(calledGraph == null){
			RuntimeRecord.methodCallAvailabilityMap.put(signature, false);
			return 1;
		}
		RuntimeRecord.methodCallAvailabilityMap.put(signature, true);
		
		Set<BytecodeInstruction> exits = calledGraph.determineExitPoints();

		List<Double> returnFitnessList = new ArrayList<>();
		for (BytecodeInstruction returnIns : exits) {
			List<Double> fList = calculateReturnInsFitness(returnIns, branchGoal, calledGraph, result);
			returnFitnessList.addAll(fList);
		}

		double sum = this.epsilon;
		for(Double f: returnFitnessList){
			sum += 1/(f+this.epsilon);
		}
		
		return 1/sum;
	}

	private List<Double> calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result) {
		List<BytecodeInstruction> sourceInsList = getReturnConstants(calledGraph, returnIns);
//		System.currentTimeMillis();
		List<Double> fitnessList = new ArrayList<>();
		
		for(BytecodeInstruction sourceIns: sourceInsList) {
			if (!sourceIns.isConstant()) {
				logger.error("the source of ireturn is not a constant.");
				continue;
			}
			
			String explain = sourceIns.explain();
			if(explain.contains("CONST_1") && !branchGoal.getValue() ||
					explain.contains("CONST_0") && branchGoal.getValue()) {
				continue;
			}
			
			Branch newDepBranch = sourceIns.getControlDependentBranch();
			if(newDepBranch == null) {
				String calledMethod = sourceIns.getCalledMethod();
				if(calledMethod != null) {
					double f = calculateInterproceduralFitness(sourceIns, branchGoal, result);
					fitnessList.add(f);
					continue;
				}
			}
			
			//TODO I am not that clear about getControlDependency() method, but I find reverse the direction make it correct.
			boolean goalValue = !sourceIns.getControlDependency(newDepBranch).getBranchExpressionValue();
			System.currentTimeMillis();
			double fitness = checkOverallDistance(result, goalValue, newDepBranch);
			if(fitness==0){
				continue;
			}
			
			BranchCoverageGoal newGoal = new BranchCoverageGoal(newDepBranch, goalValue, 
					newDepBranch.getClassName(), newDepBranch.getMethodName(), newDepBranch.getInstruction().getLineNumber());
			
			InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(newGoal);
			if (flagResult.isInterproceduralFlag) {
				double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall,
						newGoal, result);
				fitnessList.add(interproceduralFitness);
			}
			else{
				fitnessList.add(fitness);
			}	
		}
		
		return fitnessList;
	}
	
	private double checkOverallDistance(ExecutionResult result, boolean goalValue, Branch newDepBranch) {
		/**
		 * look for the covered branch
		 */
		Set<Integer> visitedBranches = new HashSet<>();
		
		int approachLevel = 0;
		while(!checkCovered(result, newDepBranch) && !visitedBranches.contains(newDepBranch.getActualBranchId())){
			visitedBranches.add(newDepBranch.getActualBranchId());
			
			BytecodeInstruction originBranchIns = newDepBranch.getInstruction();
			if(newDepBranch.getInstruction().getControlDependentBranch()==null){
				break;
			}
			
			newDepBranch = newDepBranch.getInstruction().getControlDependentBranch();
			goalValue = originBranchIns.getControlDependency(newDepBranch).getBranchExpressionValue(); 
			approachLevel++;
		}
		
		double fitness = approachLevel + checkBranchDistance(result, goalValue, newDepBranch);
		
		return fitness;
	}

	private double checkBranchDistance(ExecutionResult result, boolean goalValue, Branch newDepBranch) {
		Double value;
		if(goalValue){
			value = result.getTrace().getTrueDistances().get(newDepBranch.getActualBranchId());
		}
		else{
			value = result.getTrace().getFalseDistances().get(newDepBranch.getActualBranchId());
		}
		
		if(value == null) {
			value = 1000000d;
		}
		
		return FitnessFunction.normalize(value);
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

//	private double getDistance(ExecutionResult result) {
//		Double fitness = null;
//		if (branchGoal.getValue() == true) {
//			fitness = result.getTrace().getTrueDistances().get(branchGoal.getBranch().getActualBranchId());
//		} else {
//			fitness = result.getTrace().getFalseDistances().get(branchGoal.getBranch().getActualBranchId());
//		}
//
//		if (fitness == null) {
//			fitness = 1.0;
//		}
//
//		return fitness;
//	}

	private BytecodeInstruction findLastConstantInstructionInBlock(BasicBlock block) {
		BytecodeInstruction lastIns = block.getLastInstruction();
		BytecodeInstruction firstIns = block.getFirstInstruction();
		BytecodeInstruction ins = lastIns;
		while(ins.getInstructionId()>=firstIns.getInstructionId()) {
			if(ins.isConstant()) {
				return ins;
			}
			else if(ins.isFieldUse()) {
				return ins;
			}
			ins = ins.getPreviousInstruction();
		}
		
		return null;
	}
	
	private List<BytecodeInstruction> getReturnConstants(RawControlFlowGraph calledGraph, BytecodeInstruction returnIns) {
		List<BytecodeInstruction> sourceInsList = new ArrayList<>();
		
		
		BasicBlock block = returnIns.getBasicBlock();
		BytecodeInstruction constantIns = findLastConstantInstructionInBlock(block);
		if(constantIns != null) {
			if(constantIns.isConstant()) {
				sourceInsList.add(constantIns);				
			}
			else if(constantIns.isFieldUse()) {
				//TODO need to handle dynamic definition
				System.currentTimeMillis();
			}
			return sourceInsList;
		}
		
		
		Set<BytecodeInstruction> insParents = calledGraph.getParents(block.getFirstInstruction());
		for(BytecodeInstruction parentIns: insParents) {
			BasicBlock parentBlock = parentIns.getBasicBlock();
			constantIns = findLastConstantInstructionInBlock(parentBlock);
			if(constantIns != null) {
				sourceInsList.add(constantIns);
			}
		}
		
		return sourceInsList;
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
