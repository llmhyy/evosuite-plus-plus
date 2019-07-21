package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.fbranch.FBranchTestFitness.AnchorInstruction;
import org.evosuite.coverage.fbranch.FBranchTestFitness.DistanceCondition;
import org.evosuite.graphs.cfg.BasicBlock;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.setup.Call;
import org.evosuite.testcase.execution.ExecutionResult;

public class ReturnFitnessEvaluator {
	public List<Double> calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result, List<Call> callContext, List<Integer> branchTrace, BranchCoverageGoal globalGoal)  {
		List<AnchorInstruction> sourceInsList = getAnchorInstructions(calledGraph, returnIns);
		List<Double> fitnessList = new ArrayList<>();
		
		List<BytecodeInstruction> exercisedMethodCalls = new ArrayList<>();
		fileterSourceInsList(result, sourceInsList, exercisedMethodCalls, callContext, branchTrace);
//		System.currentTimeMillis();
		
		for (AnchorInstruction anchorIns : sourceInsList) {
			/**
			 * no control dependency, we only care about method call
			 */
			BytecodeInstruction sourceIns = anchorIns.ins;
			if(sourceIns.getControlDependencies().isEmpty()) {
				if(sourceIns.isMethodCall()) {
					List<Call> newContext = updateCallContext(sourceIns, callContext);
					if(newContext.size()!=callContext.size()) {
						double f = FlagBranchEvaluator.calculateInterproceduralFitness(sourceIns, newContext, branchGoal, result, globalGoal);
						fitnessList.add(f);						
					}
					//stop for recursive call
					else {
						fitnessList.add(1.0);		
					}
				}
				
				continue;
			}
			
			/**
			 * has control dependency
			 */
			if(sourceIns.isConstant()) {
				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			else if(sourceIns.isFieldUse()) {
				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			else if(sourceIns.isLocalVariableUse()) {
				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			else if(sourceIns.isMethodCall()) {
				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			
		}
		
		/**
		 * for exercised methods
		 */
		for(BytecodeInstruction methodCall: exercisedMethodCalls) {
			List<Call> newContext = updateCallContext(methodCall, callContext);
			if(newContext.size()!=callContext.size()) {
				double f = FlagBranchEvaluator.calculateInterproceduralFitness(methodCall, newContext, branchGoal, result, globalGoal);
				fitnessList.add(f);						
			}
			//stop for recursive call
			else {
				fitnessList.add(1.0);		
			}
		}

		return fitnessList;
	}
	
	/**
	 * We do not further analyze recursive method calls.
	 */
	@SuppressWarnings("unchecked")
	private List<Call> updateCallContext(BytecodeInstruction sourceIns, List<Call> callContext){
		List<Call> newContext = (List<Call>) ((ArrayList<Call>)callContext).clone();
//		BytecodeInstruction ins = sourceIns.getCalledCFG().getInstruction(0);
		Call call = new Call(sourceIns.getClassName(), sourceIns.getMethodName(), sourceIns.getInstructionId());
		if(!newContext.contains(call)) {
			newContext.add(call);
		}
		
		return newContext;
	}
	
	
	private List<AnchorInstruction> getAnchorInstructions(RawControlFlowGraph calledGraph,
			BytecodeInstruction returnIns) {
//		List<BytecodeInstruction> sourceInsList = new ArrayList<>();

		BasicBlock block = returnIns.getBasicBlock();
		List<AnchorInstruction> sourceInsList = findSourceForReturnInstructionInBlock(block);
		
		System.currentTimeMillis();
		if(sourceInsList.isEmpty()) {
			Set<BytecodeInstruction> insParents = calledGraph.getParents(block.getFirstInstruction());
			for (BytecodeInstruction parentIns : insParents) {
				BasicBlock parentBlock = parentIns.getBasicBlock();
				List<AnchorInstruction> sources = findSourceForReturnInstructionInBlock(parentBlock);
				sourceInsList.addAll(sources);
			}

		}

		return sourceInsList;
		
	}
	
	private void handleUndeterminedAnchor(ExecutionResult result, List<Call> callContext, List<Integer> branchTrace,
			List<Double> fitnessList, BytecodeInstruction sourceIns) {
		// TODO Auto-generated method stub
		
	}

	private void handleAnchor(ExecutionResult result, List<Call> callContext, List<Integer> branchTrace,
			List<Double> fitnessList, BytecodeInstruction sourceIns) {
		Branch newDepBranch = null;
		boolean goalValue = false;
		double fitness = 0;
		for(ControlDependency cd: sourceIns.getControlDependencies()) {
			newDepBranch = cd.getBranch();
			
			// TODO I am not that clear about getControlDependency() method, but I find
			// reverse the direction make it correct.
			goalValue = sourceIns.getControlDependency(newDepBranch).getBranchExpressionValue();
			DistanceCondition dCondition = checkOverallDistance(result, goalValue, newDepBranch, callContext, branchTrace);
			fitness = dCondition.fitness;
			
			// in case the returned direction is wrong.
			if (fitness == 0) {
				goalValue = !goalValue;
				dCondition = checkOverallDistance(result, goalValue, newDepBranch, callContext, branchTrace);
				fitness = dCondition.fitness;
			}	
			
			if(fitness==0) {
				logger.error(this.goal + " is not exercised, but " +
						"both branches of " + newDepBranch + " have 0 branch distance");
				setInconsistencyHappen(true);
				continue;
			}

			BranchCoverageGoal newGoal = dCondition.goal;

			FlagEffectResult flagResult = isFlagMethod(newGoal);
			if (flagResult.isInterproceduralFlag) {
				List<Call> newContext = updateCallContext(flagResult.interproceduralFlagCall, callContext);
				if(newContext.size() != callContext.size()) {
					double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall, newContext,
							newGoal, result);
					fitnessList.add(interproceduralFitness);						
				}
				else {
					fitnessList.add(1.0);		
				}
			} else {
				fitnessList.add(fitness);
			}
//			break;
		}
	}

	/**
	 * only keep the source instruction need to be covered
	 * 
	 * @param result
	 * @param sourceInsList
	 * @param exercisedMethodCalls 
	 * @param cBranch
	 */
	private void fileterSourceInsList(ExecutionResult result,
			List<AnchorInstruction> sourceInsList, List<BytecodeInstruction> exercisedMethodCalls, List<Call> context, List<Integer> branchTrace) {
		
		List<BytecodeInstruction> coveredInsList = new ArrayList<>();
		for(AnchorInstruction anchor: sourceInsList) {
			BytecodeInstruction ins = anchor.ins;
			if(ins.getControlDependencies().isEmpty()) {
				coveredInsList.add(ins);
			}
			
			for(ControlDependency cd: ins.getControlDependencies()) {
				List<Call> newContext = updateCallContext(cd.getBranch().getInstruction(), context);
				
				boolean branchExpression = cd.getBranchExpressionValue();
				double distance = checkContextBranchDistance(result, cd.getBranch(), branchExpression, newContext, branchTrace);
				if(distance == 0) {
					coveredInsList.add(ins);
					break;
				}
			}
		}
		
		Iterator<AnchorInstruction> iter = sourceInsList.iterator();
		while(iter.hasNext()) {
			AnchorInstruction anchorIns = iter.next();
			BytecodeInstruction ins = anchorIns.ins;
			for(BytecodeInstruction coveredIns: coveredInsList) {
				if((ins.explain().equals(coveredIns.explain()) && ins.isConstant()) 
						|| ins.equals(coveredIns)) {
					if(ins.isMethodCall()) {
						exercisedMethodCalls.add(ins);
					}
					iter.remove();
					break;						
					
				}
			}
		}
	}
}
