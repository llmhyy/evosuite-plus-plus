package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.cfg.BasicBlock;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.setup.Call;
import org.evosuite.setup.CallContext;
import org.evosuite.testcase.execution.ExecutionResult;

public class ReturnFitnessEvaluator {
	public List<Double> calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result, List<Call> callContext, 
			List<Integer> branchTrace, String coveredConstant
			/*, BranchCoverageGoal globalGoal*/)  {
//		List<BytecodeInstruction> anchorInsList = getAnchorInstructions(calledGraph, returnIns);
		List<BytecodeInstruction> anchorInsList = returnIns.getSourceOfStackInstructionList(0);
		List<Double> fitnessList = new ArrayList<>();
		
		List<BytecodeInstruction> exercisedMethodCalls = new ArrayList<>();
		removeIncorrectAnchors(coveredConstant, result, anchorInsList, exercisedMethodCalls, callContext, branchTrace);
		System.currentTimeMillis();
		
		for (BytecodeInstruction anchorIns : anchorInsList) {
			/**
			 * no control dependency, we only care about method call
			 */
			BytecodeInstruction sourceIns = anchorIns;
			if(sourceIns.getControlDependencies().isEmpty()) {
				if(sourceIns.isMethodCall()) {
					List<Call> newContext = FlagEffectEvaluator.updateCallContext(sourceIns, callContext);
					if(newContext.size()!=callContext.size()) {
						double f = FlagEffectEvaluator.calculateInterproceduralFitness(sourceIns, newContext, branchGoal, result);
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
			handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
//			if(sourceIns.isConstant()) {
//				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
//			}
//			else if(sourceIns.isFieldUse()) {
//				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
//			}
//			else if(sourceIns.isLocalVariableUse()) {
//				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
//			}
//			else if(sourceIns.isMethodCall()) {
//				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
//			}
		}
		
		/**
		 * for exercised methods
		 */
		for(BytecodeInstruction methodCall: exercisedMethodCalls) {
			List<Call> newContext = FlagEffectEvaluator.updateCallContext(methodCall, callContext);
			if(newContext.size()!=callContext.size()) {
				double f = FlagEffectEvaluator.calculateInterproceduralFitness(methodCall, newContext, branchGoal, result);
				fitnessList.add(f);						
			}
			//stop for recursive call
			else {
				fitnessList.add(1.0);		
			}
		}

		return fitnessList;
	}
	
	

//	private List<AnchorInstruction> getAnchorInstructions(RawControlFlowGraph calledGraph,
//			BytecodeInstruction returnIns) {
//		List<AnchorInstruction> anchorInsList = findSourceForReturnInstructionInBlock(returnIns);
//		System.currentTimeMillis();
//		
//		if(anchorInsList.isEmpty()) {
//			BasicBlock block = returnIns.getBasicBlock();
//			Set<BytecodeInstruction> insParents = calledGraph.getParents(block.getFirstInstruction());
//			for (BytecodeInstruction parentIns : insParents) {
////				BasicBlock parentBlock = parentIns.getBasicBlock();
//				List<AnchorInstruction> sources = findSourceForReturnInstructionInBlock(parentIns);
//				anchorInsList.addAll(sources);
//			}
//
//		}
//
//		return anchorInsList;
//		
//	}
	
	private DistanceCondition checkOverallDistance(ExecutionResult result, boolean goalValue, Branch branch, 
			List<Call> originContext, List<Integer> branchTrace) {
		/**
		 * look for the covered branch
		 */
		Set<Branch> visitedBranches = new HashSet<>();

		List<Call> callContext = FlagEffectEvaluator.updateCallContext(branch.getInstruction(), originContext);
		System.currentTimeMillis();
		int approachLevel = 0;
		double branchDistance = -1;
		
		/**
		 * keep finding the parent branch
		 */
		Branch prevBranch = branch;
		while (!checkCovered(result, branch, callContext, branchTrace) 
				&& !visitedBranches.contains(branch)) {
			approachLevel++;
			
			visitedBranches.add(branch);

			BytecodeInstruction originBranchIns = branch.getInstruction();
			
			/**
			 * it means that an exception happens on the way to originBranchIns, we should
			 * add a penalty to such test case.
			 */
			if (originBranchIns.getControlDependentBranch() == null) {
				approachLevel = 100000;
				break;
			}

			prevBranch = branch;
			
			Branch tmpBranch = null;
			for(ControlDependency cd: branch.getInstruction().getControlDependencies()) {
				if(visitedBranches.contains(cd.getBranch())) {
					continue;
				}
				else {
					tmpBranch = cd.getBranch();
					break;
				}
			}
//			branch = findUnvisitedControlDependencyBranch(branch.getInstruction(), visitedBranches);
			if(tmpBranch == null) {
				branch = branch.getInstruction().getControlDependentBranch();
			}
			else {
				branch = tmpBranch;
			}
			
			goalValue = originBranchIns.getControlDependency(branch).getBranchExpressionValue();
			
			callContext = FlagEffectEvaluator.updateCallContext(branch.getInstruction(), originContext);
			branchDistance = checkContextBranchDistance(result, branch, goalValue, callContext, branchTrace);
			if(branchDistance == 0) {
				goalValue = !goalValue;
			}	
		}

		System.currentTimeMillis();
		double finalBranchDistance = 0;
		if(approachLevel > 0) {
			List<Branch> visitedControlBranches = new ArrayList<>();
			List<Boolean> expression = new ArrayList<>();
			for(ControlDependency cd: prevBranch.getInstruction().getControlDependencies()) {
				Branch b = cd.getBranch();
				callContext = FlagEffectEvaluator.updateCallContext(b.getInstruction(), originContext);
				if(checkCovered(result, b, callContext, branchTrace)) {
					visitedControlBranches.add(b);
					expression.add(cd.getBranchExpressionValue());
				}
			}
			if(visitedControlBranches.isEmpty()) {
				finalBranchDistance = 1;
			}
			else {
				double average = 0;
				for(int i=0; i<visitedControlBranches.size(); i++) {
					Branch b = visitedControlBranches.get(i);
					boolean g = expression.get(i);
					
					callContext = FlagEffectEvaluator.updateCallContext(b.getInstruction(), originContext);
					double bd = checkContextBranchDistance(result, b, g, callContext, branchTrace);
					average += bd;
				}
				average /= visitedControlBranches.size();
				finalBranchDistance = average;
			}			
		}
		else {
			callContext = FlagEffectEvaluator.updateCallContext(branch.getInstruction(), originContext);
			finalBranchDistance = checkContextBranchDistance(result, branch, goalValue, callContext, branchTrace);
		}
		
//		System.currentTimeMillis();
		
		double fitness = approachLevel + finalBranchDistance;
		
		BranchCoverageGoal goal = new BranchCoverageGoal(branch, goalValue, branch.getClassName(), branch.getMethodName());
		return new DistanceCondition(fitness, goal);
	}

	/**
	 * 
	 * @param result
	 * @param callContext
	 * @param branchTrace
	 * @param fitnessList
	 * @param sourceIns
	 */
	private void handleAnchor(ExecutionResult result, List<Call> callContext, List<Integer> branchTrace,
			List<Double> fitnessList, BytecodeInstruction sourceIns) {
		Branch newDepBranch = null;
		boolean goalValue = false;
		double fitness = 0;
		for(ControlDependency cd: sourceIns.getControlDependencies()) {
			newDepBranch = cd.getBranch();
			
			goalValue = sourceIns.getControlDependency(newDepBranch).getBranchExpressionValue();
			DistanceCondition dCondition = checkOverallDistance(result, goalValue, newDepBranch, callContext, branchTrace);
			fitness = dCondition.fitness;
			
			System.currentTimeMillis();
			
			// in case the returned direction is wrong.
			if (fitness == 0) {
				goalValue = !goalValue;
				dCondition = checkOverallDistance(result, goalValue, newDepBranch, callContext, branchTrace);
				fitness = dCondition.fitness;
			}	
			
			if(fitness==0) {
//				logger.error(this.goal + " is not exercised, but " +
//						"both branches of " + newDepBranch + " have 0 branch distance");
//				setInconsistencyHappen(true);
				continue;
			}

			
			System.currentTimeMillis();
			BranchCoverageGoal newGoal = dCondition.goal;
			FlagEffectResult flagResult = FlagEffectEvaluator.checkFlagEffect(newGoal);
			
			if (flagResult.hasFlagEffect) {
				List<Call> newContext = FlagEffectEvaluator.updateCallContext(flagResult.interproceduralFlagCall, callContext);
				if(newContext.size() != callContext.size()) {
					double interproceduralFitness = FlagEffectEvaluator.calculateInterproceduralFitness(
							flagResult.interproceduralFlagCall, newContext, newGoal, result);
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
		
		System.currentTimeMillis();
	}

	/**
	 * only keep the anchor instruction need to be covered
	 * @param coveredConstant 
	 * 
	 * @param result
	 * @param sourceInsList
	 * @param exercisedMethodCalls 
	 * @param cBranch
	 */
	private void removeIncorrectAnchors(String coveredConstant, ExecutionResult result,
			List<BytecodeInstruction> sourceInsList, List<BytecodeInstruction> exercisedMethodCalls, 
			List<Call> context, List<Integer> branchTrace) {
		
		List<BytecodeInstruction> coveredInsList = new ArrayList<>();
		for(BytecodeInstruction ins: sourceInsList) {
			if(ins.getControlDependencies().isEmpty()) {
				coveredInsList.add(ins);
			}
			
			for(ControlDependency cd: ins.getControlDependencies()) {
				List<Call> newContext = FlagEffectEvaluator.updateCallContext(cd.getBranch().getInstruction(), context);
				
				boolean branchExpression = cd.getBranchExpressionValue();
				double distance = checkContextBranchDistance(result, cd.getBranch(), branchExpression, newContext, branchTrace);
				if(distance == 0) {
					coveredInsList.add(ins);
					break;
				}
			}
		}
		
		Iterator<BytecodeInstruction> iter = sourceInsList.iterator();
		while(iter.hasNext()) {
			BytecodeInstruction ins = iter.next();
			if(ins.explain().equals(coveredConstant)) {
				iter.remove();
			}			
			else {
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
	
	private double checkContextBranchDistance(ExecutionResult result, Branch branch, boolean goalValue, 
			List<Call> context, List<Integer> branchTrace) {
		
		Map<List<Integer>, Double> values = null;
		if (goalValue) {
			if(result.getTrace().getContextIterationTrueMap().get(branch.getActualBranchId()) != null) {
				values = result.getTrace().getContextIterationTrueMap().get(branch.getActualBranchId()).
						get(new CallContext(context));				
			}
			else {
				System.currentTimeMillis();
			}
		} else {
			if(result.getTrace().getContextIterationFalseMap().get(branch.getActualBranchId()) != null) {
				values = result.getTrace().getContextIterationFalseMap().get(branch.getActualBranchId()).
						get(new CallContext(context));				
			}
			else {
				System.currentTimeMillis();
			}
		}	
		
		if(values == null) {
			return 1;
		}
		else {
			Double value = values.get(branchTrace);
			if(value == null) {
				return 1;
			}
			else if(value == 0) {
				return value;
			}
			else {
				/**
				 *  handle internal flag problem 
				 */
				if(needDelegateBranch(branch, value)) {
					BytecodeInstruction load = branch.getInstruction().getSourceOfStackInstruction(0);
					if(load == null) {
						return FitnessFunction.normalize(value);
					}
					
					List<BytecodeInstruction> defs = findDefinitions(load);
					if(!defs.isEmpty() && defs.size()==1) {
						BytecodeInstruction store = defs.get(0);
						List<BytecodeInstruction> insList = store.getSourceOfStackInstructionList(0);
						
						if(insList != null && !insList.isEmpty()) {
							for(BytecodeInstruction ins: insList) {
								Branch delegateBranch = ins.getControlDependentBranch();
								boolean delegateGoalValue = ins.getControlDependentBranchExpressionValue();
								
								List<Call> replacedContext = FlagEffectEvaluator.replaceContext(context, delegateBranch);
								
								double distance = checkContextBranchDistance(result, delegateBranch, delegateGoalValue, 
										replacedContext, branchTrace);
								if(distance != 0) {
									return distance;
								}
							}
						}
					}
					else {
						return FitnessFunction.normalize(value);
					}
					
				}
				
				return FitnessFunction.normalize(value);
			}
		}
	}

	private boolean needDelegateBranch(Branch branch, Double value) {
		BytecodeInstruction branchIns = branch.getInstruction();
		if(value == 1 && (
				branchIns.explain().contains("IFEQ") ||
				branchIns.explain().contains("IFNE") ||
				branchIns.explain().contains("IFGE") ||
				branchIns.explain().contains("IFGT") ||
				branchIns.explain().contains("IFLE") ||
				branchIns.explain().contains("IFLT") 
				)) {
			return true;
		}
		return false;
	}

	private boolean checkCovered(ExecutionResult result, Branch branch, List<Call> callContext, List<Integer> branchTrace) {
		Map<List<Integer>, Double> values = null;
		
		if(result.getTrace().getContextIterationTrueMap().get(branch.getActualBranchId()) != null) {
			values = result.getTrace().getContextIterationTrueMap().get(branch.getActualBranchId()).
					get(new CallContext(callContext));				
		}
		if(values != null && values.get(branchTrace)!=null && values.get(branchTrace) == 0) {
			return true;
		}
		
		if(result.getTrace().getContextIterationFalseMap().get(branch.getActualBranchId()) != null) {
			values = result.getTrace().getContextIterationFalseMap().get(branch.getActualBranchId()).
					get(new CallContext(callContext));				
		}
		if(values != null && values.get(branchTrace)!=null && values.get(branchTrace) == 0) {
			return true;
		}
		
		return false;
	}

	
//	private List<BytecodeInstruction> findSourceForReturnInstructionInBlock(BytecodeInstruction returnIns) {
//		
//		List<BytecodeInstruction> anchors = returnIns.getSourceOfStackInstructionList(0);
//		
//		
//		BasicBlock block = returnIns.getBasicBlock();
//		
//		List<BytecodeInstruction> anchorList = new ArrayList<>();
//		
//		BytecodeInstruction lastIns = block.getLastInstruction();
//		BytecodeInstruction firstIns = block.getFirstInstruction();
//		BytecodeInstruction ins = lastIns;
//		
//		while (ins.getInstructionId() >= firstIns.getInstructionId()) {
//			if (ins.isConstant()) {
//				anchorList.add(new AnchorInstruction(ins, true));
//				break;
//			} else if (ins.isFieldUse()) {
//				anchorList.add(new AnchorInstruction(ins, false));
//				break;
//			} else if (ins.isMethodCall()) {
//				MethodInsnNode mNode = (MethodInsnNode) ins.getASMNode();
//				String desc = mNode.desc;
//				String returnType = FlagEffectEvaluator.getReturnType(desc);
//				boolean isInterprocedural = returnType.equals("Z");
//				
//				boolean determined = canMethodContainConstantReturn(ins.getCalledCFG());
//				
//				if(isInterprocedural) {
//					anchorList.add(new AnchorInstruction(ins, determined));
//					break;
//				}
//			} else if (ins.isLocalVariableUse()) {
//				List<BytecodeInstruction> istores = findDefinitions(ins);
//				
//				if(istores.isEmpty()) {
//					anchorList.add(new AnchorInstruction(ins, true));
//					break;
//				}
//				
//				for(BytecodeInstruction istore: istores) {
//					BytecodeInstruction prevIns = istore.getPreviousInstruction();
//					if(prevIns.isConstant()) {
//						anchorList.add(new AnchorInstruction(prevIns, true));
//					}
//					else {
//						anchorList.add(new AnchorInstruction(ins, false));
//					}
//				}
//				
//			}
//			ins = ins.getPreviousInstruction();
//		}
//
//		return anchorList;
//	}

//	private boolean canMethodContainConstantReturn(RawControlFlowGraph calledCFG) {
//		if(calledCFG==null) {
//			return false;
//		}
//		
//		for(BytecodeInstruction ins: calledCFG.determineExitPoints()) {
//			if(ins.isReturn()) {
//				BytecodeInstruction returnDef = ins.getSourceOfStackInstruction(0);
//				if(returnDef.isConstant()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	private List<BytecodeInstruction> findDefinitions(BytecodeInstruction iLoad) {
		List<BytecodeInstruction> defs = new ArrayList<>();
		if(!iLoad.explain().contains("ILOAD")) {
			return new ArrayList<>();
		}
		
		BasicBlock block = iLoad.getBasicBlock();
		Set<BasicBlock> visitedBlocks = new HashSet<>();
		
		findDefinitions(defs, block, iLoad, visitedBlocks);
		
		return defs;
	}

	private BytecodeInstruction findIStoreInBlock(BytecodeInstruction iLoad, BasicBlock block) {
		
		if(block.getFirstInstruction() == null) {
			return null;
		}
		
		BytecodeInstruction firstIns = block.getFirstInstruction();
		BytecodeInstruction lastIns = block.getLastInstruction();
		
		int varID = iLoad.getLocalVariableSlot();
		
		BytecodeInstruction ins = lastIns;
		while(ins.getInstructionId() >= firstIns.getInstructionId()) {
			if(ins.isLocalVariableDefinition()) {
				if(ins.getLocalVariableSlot() == varID) {
					return ins;
				}
			}
			ins = ins.getPreviousInstruction();
			//reach the first instruction
			if(ins == null) {
				return null;
			}
		}
		
		return null;
	}

	private void findDefinitions(List<BytecodeInstruction> defs, BasicBlock block, BytecodeInstruction iLoad,
			Set<BasicBlock> visitedBlocks) {
		BytecodeInstruction istore = findIStoreInBlock(iLoad, block);
		if(istore != null) {
			if(!defs.contains(istore)) {
				defs.add(istore);
			}
		}
		else {
			visitedBlocks.add(block);
			
			Set<BasicBlock> parents = iLoad.getActualCFG().getParents(block);
			for(BasicBlock parent: parents) {
				if(!visitedBlocks.contains(parent)) {
					findDefinitions(defs, parent, iLoad, visitedBlocks);					
				}
			}
		}
		
	}

	class DistanceCondition{
		double fitness;
		BranchCoverageGoal goal;
		public DistanceCondition(double fitness, BranchCoverageGoal goal) {
			super();
			this.fitness = fitness;
			this.goal = goal;
		}
		
	}
}
