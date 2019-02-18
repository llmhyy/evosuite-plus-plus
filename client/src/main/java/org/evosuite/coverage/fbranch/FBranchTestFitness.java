package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.graphs.cfg.BasicBlock;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.setup.Call;
import org.evosuite.setup.CallContext;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.objectweb.asm.tree.AbstractInsnNode;
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
	private boolean inconsistencyHappen = false;

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

	class DistanceCondition{
		double fitness;
		BranchCoverageGoal goal;
		public DistanceCondition(double fitness, BranchCoverageGoal goal) {
			super();
			this.fitness = fitness;
			this.goal = goal;
		}
		
	}
	
	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		this.inconsistencyHappen = false;
		
		/**
		 * if the result does not exercise this branch node, we do not further process the detailed
		 * branch distance as we need to pass its parent branch node.
		 */
		Double value = null;
		if(this.branchGoal.getValue()) {
			value = result.getTrace().getTrueDistances().get(this.branchGoal.getBranch().getActualBranchId());
		}
		else {
			value = result.getTrace().getFalseDistances().get(this.branchGoal.getBranch().getActualBranchId());
		}
		
		
		if(value == null){
			return 10000d;
		}
		else if(value != 1){
			return normalize(value);
		}
		
		double fitness = value;
		BranchCoverageGoal goal = this.branchGoal;
		InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(goal);
		if (flagResult.isInterproceduralFlag) {
			List<Call> callContext = new ArrayList<>();
			callContext.add(flagResult.call);
			
			double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall, callContext, goal, result);
			
			double normalizedFitness = normalize(interproceduralFitness);
			
			return normalizedFitness;
			
		}
		
		return fitness;
	}
	
	/**
	 * for debugging reason
	 * @param loopContext
	 */
	private void sortLength(List<List<Integer>> loopContext) {
		Collections.sort(loopContext, new Comparator<List<Integer>>() {

			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {
				return o1.size() - o2.size();
			}
		});
	}

	private double calculateInterproceduralFitness(BytecodeInstruction flagCallInstruction, List<Call> callContext,
			BranchCoverageGoal branchGoal, ExecutionResult result) {
		RawControlFlowGraph calledGraph = flagCallInstruction.getCalledCFG();
		String signature = flagCallInstruction.getCalledMethodsClass() + "." + flagCallInstruction.getCalledMethod();
		if (calledGraph == null) {
			RuntimeRecord.methodCallAvailabilityMap.put(signature, false);
			return 1;
		}
		RuntimeRecord.methodCallAvailabilityMap.put(signature, true);

		Set<BytecodeInstruction> exits = calledGraph.determineExitPoints();

		List<Double> iteratedFitness = new ArrayList<>();
		List<List<Integer>> loopContext = identifyLoopContext(result, callContext);
		sortLength(loopContext);
		for(List<Integer> branchTrace: loopContext) {
			
			List<Double> returnFitnessList = new ArrayList<>();
			for (BytecodeInstruction returnIns : exits) {
				List<Double> fList = calculateReturnInsFitness(returnIns, branchGoal, calledGraph, result, callContext, branchTrace);
				returnFitnessList.addAll(fList);
			}
			
			double fit = FitnessAggregator.aggreateFitenss(returnFitnessList);
//			return fit;
			iteratedFitness.add(fit);
		}
		
//		System.currentTimeMillis();
		
		if(iteratedFitness.isEmpty()) {
			return 10000000d;
		}
		
//		return Collections.min(iteratedFitness);
		return FitnessAggregator.aggreateFitenss(iteratedFitness);
	}
	
	private List<List<Integer>> identifyLoopContext(ExecutionResult result, List<Call> callContext) {
		List<List<Integer>> loopContext = new ArrayList<>();
		for(Map<CallContext, Map<List<Integer>, Double>> context: result.getTrace().getContextIterationTrueMap().values()) {
			for(CallContext key: context.keySet()) {
				boolean isCompatible = getCompatible(key, callContext);
				if(isCompatible) {
					Map<List<Integer>, Double> iterationTable = context.get(key);
					for(List<Integer> brancTrace: iterationTable.keySet()){
						if(!loopContext.contains(brancTrace)) {
							loopContext.add(brancTrace);
						}
					}
				}
				
			}
		}
		System.currentTimeMillis();
		
		return loopContext;
	}

	private boolean getCompatible(CallContext key, List<Call> callContext) {
		if(key.getContext().size() == callContext.size()+1) {
			for(int i=0; i<callContext.size(); i++) {
				if(!key.getContext().get(i).equals(callContext.get(i))) {
					return false;
				}
			}			
			return true;
		}
		
		return false;
	}

	/**
	 * We do not further analyze recursive method calls.
	 */
	@SuppressWarnings("unchecked")
	private List<Call> updateCallContext(BytecodeInstruction sourceIns, List<Call> callContext){
		List<Call> newContext = (List<Call>) ((ArrayList<Call>)callContext).clone();
//		BytecodeInstruction ins = sourceIns.getCalledCFG().getInstruction(0);
		Call call = new Call(sourceIns.getClassName(), getSimpleMethod(sourceIns.getMethodName()), sourceIns.getLineNumber());
		if(!newContext.contains(call)) {
			newContext.add(call);
		}
		
		return newContext;
	}
	
	private String getSimpleMethod(String methodName) {
		String sName = methodName.substring(0, methodName.indexOf("("));
		
		return sName;
	}
	
	private List<Double> calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result, List<Call> callContext, List<Integer> branchTrace)  {
		List<AnchorInstruction> sourceInsList = getAnchorInstructions(calledGraph, returnIns);
		List<Double> fitnessList = new ArrayList<>();
		
		List<BytecodeInstruction> exercisedMethodCalls = new ArrayList<>();
		fileterSourceInsList(result, sourceInsList, exercisedMethodCalls, callContext, branchTrace);
		
		for (AnchorInstruction anchorIns : sourceInsList) {
			/**
			 * no control dependency, we only care about method call
			 */
			BytecodeInstruction sourceIns = anchorIns.ins;
			if(sourceIns.getControlDependencies().isEmpty()) {
				if(sourceIns.isMethodCall()) {
					List<Call> newContext = updateCallContext(sourceIns, callContext);
					if(newContext.size()!=callContext.size()) {
						double f = calculateInterproceduralFitness(sourceIns, newContext, branchGoal, result);
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
			 * for exercised methods
			 */
			for(BytecodeInstruction methodCall: exercisedMethodCalls) {
				List<Call> newContext = updateCallContext(methodCall, callContext);
				if(newContext.size()!=callContext.size()) {
					double f = calculateInterproceduralFitness(methodCall, newContext, branchGoal, result);
					fitnessList.add(f);						
				}
				//stop for recursive call
				else {
					fitnessList.add(1.0);		
				}
			}
			
			/**
			 * has control dependency
			 */
			if(sourceIns.isConstant()) {
				handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			else if(sourceIns.isFieldUse()) {
				//TODO
				//handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			else if(sourceIns.isLocalVariableUse()) {
				//TODO
				//handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			else if(sourceIns.isMethodCall()) {
				//TODO
				//handleAnchor(result, callContext, branchTrace, fitnessList, sourceIns);
			}
			
		}

		return fitnessList;
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
				logger.error(this.branchGoal + " is not exercised, but " +
						"both branches of " + newDepBranch + " have 0 branch distance");
				setInconsistencyHappen(true);
				continue;
			}

			BranchCoverageGoal newGoal = dCondition.goal;

			InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(newGoal);
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
			break;
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
					if(!ins.isMethodCall()) {
						exercisedMethodCalls.add(ins);
					}
					iter.remove();
					break;						
					
				}
			}
		}
	}

	private DistanceCondition checkOverallDistance(ExecutionResult result, boolean goalValue, Branch branch, 
			List<Call> originContext, List<Integer> branchTrace) {
		/**
		 * look for the covered branch
		 */
		Set<Branch> visitedBranches = new HashSet<>();

		List<Call> callContext = updateCallContext(branch.getInstruction(), originContext);
		
		int approachLevel = 0;
		double branchDistance = -1;
		
		Branch prevBranch = branch;
		while (!checkCovered(result, branch, callContext, branchTrace) && !visitedBranches.contains(branch)) {
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
			branch = branch.getInstruction().getControlDependentBranch();
			goalValue = originBranchIns.getControlDependency(branch).getBranchExpressionValue();
			
			callContext = updateCallContext(branch.getInstruction(), originContext);
			branchDistance = checkContextBranchDistance(result, branch, goalValue, callContext, branchTrace);
			if(branchDistance == 0) {
				goalValue = !goalValue;
			}	
		}

		
		double finalBranchDistance = 0;
		if(approachLevel > 0) {
			List<Branch> visitedControlBranches = new ArrayList<>();
			List<Boolean> expression = new ArrayList<>();
			for(ControlDependency cd: prevBranch.getInstruction().getControlDependencies()) {
				Branch b = cd.getBranch();
				callContext = updateCallContext(b.getInstruction(), originContext);
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
					
					callContext = updateCallContext(b.getInstruction(), originContext);
					double bd = checkContextBranchDistance(result, b, g, callContext, branchTrace);
					average += bd;
				}
				average /= visitedControlBranches.size();
				finalBranchDistance = average;
			}			
		}
		else {
			callContext = updateCallContext(branch.getInstruction(), originContext);
			finalBranchDistance = checkContextBranchDistance(result, branch, goalValue, callContext, branchTrace);
		}
		
//		System.currentTimeMillis();
		
		double fitness = approachLevel + finalBranchDistance;
		
		BranchCoverageGoal goal = new BranchCoverageGoal(branch, goalValue, branch.getClassName(), branch.getMethodName());
		return new DistanceCondition(fitness, goal);
	}

	private double checkContextBranchDistance(ExecutionResult result, Branch branch, boolean goalValue, 
			List<Call> context, List<Integer> branchTrace) {
		
		Map<List<Integer>, Double> values = null;
		if (goalValue) {
//			Map<CallContext, Double> trueContextMap = result.getTrace().getTrueDistancesContext().get(branch.getActualBranchId());
//			value = getContextDistance(trueContextMap, context);
			
			if(result.getTrace().getContextIterationTrueMap().get(branch.getActualBranchId()) != null) {
				values = result.getTrace().getContextIterationTrueMap().get(branch.getActualBranchId()).
						get(new CallContext(context));				
			}
			else {
				System.currentTimeMillis();
			}
			
			
		} else {
//			Map<CallContext, Double> falseContextMap = result.getTrace().getFalseDistancesContext().get(branch.getActualBranchId());
//			value = getContextDistance(falseContextMap, context);
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
			else {
				return FitnessFunction.normalize(value);
			}
		}
	}

	private boolean checkCovered(ExecutionResult result, Branch branch, List<Call> callContext, List<Integer> branchTrace) {
//		Map<CallContext, Double> falseContextMap = result.getTrace().getFalseDistancesContext().get(branch.getActualBranchId());
//		Double value = getContextDistance(falseContextMap, callContext);
//		if(value != null && value == 0) {
//			return true;
//		}
//		
//		Map<CallContext, Double> trueContextMap = result.getTrace().getTrueDistancesContext().get(branch.getActualBranchId());
//		value = getContextDistance(trueContextMap, callContext);
//		if(value != null && value == 0) {
//			return true;
//		}
//		
//		return false;
		
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

	private Double getContextDistance(Map<CallContext, Double> contextMap, List<Call> context) {
		if(contextMap == null) {
			return null;
		}
		
		if(context.isEmpty()) {
			for(CallContext key: contextMap.keySet()) {
				return contextMap.get(key);
			}
		}
		
		
		for(CallContext key: contextMap.keySet()) {
			List<Call> calls = key.getContext();
			if(calls.hashCode() == context.hashCode()) {
				return contextMap.get(key);
			}
		}
		
		return null;
	}

	class InterproceduralFlagResult {
		BytecodeInstruction interproceduralFlagCall;
		boolean isInterproceduralFlag;
		Call call;

		public InterproceduralFlagResult(BytecodeInstruction interproceduralFlagCall, boolean isInterproceduralFlag, Call call) {
			super();
			this.interproceduralFlagCall = interproceduralFlagCall;
			this.isInterproceduralFlag = isInterproceduralFlag;
			this.call = call;
		}
	}

	private InterproceduralFlagResult isInterproceduralFlagProblem(BranchCoverageGoal goal) {
		BytecodeInstruction instruction = goal.getBranch().getInstruction();
		
		
		BytecodeInstruction interproceduralFlagCall = instruction.getSourceOfStackInstruction(0);
		boolean isInterproceduralFlag = false;
		Call callInfo = null;
		if (interproceduralFlagCall != null && interproceduralFlagCall.getASMNode() instanceof MethodInsnNode) {
			MethodInsnNode mNode = (MethodInsnNode) interproceduralFlagCall.getASMNode();
			String desc = mNode.desc;
			String returnType = getReturnType(desc);
			isInterproceduralFlag = returnType.equals("Z");
			callInfo = new Call(instruction.getClassName(), getSimpleMethod(instruction.getMethodName()), interproceduralFlagCall.getLineNumber());
		}

		InterproceduralFlagResult result = new InterproceduralFlagResult(interproceduralFlagCall,
				isInterproceduralFlag, callInfo);
		return result;
	}

	// private double getDistance(ExecutionResult result) {
	// Double fitness = null;
	// if (branchGoal.getValue() == true) {
	// fitness =
	// result.getTrace().getTrueDistances().get(branchGoal.getBranch().getActualBranchId());
	// } else {
	// fitness =
	// result.getTrace().getFalseDistances().get(branchGoal.getBranch().getActualBranchId());
	// }
	//
	// if (fitness == null) {
	// fitness = 1.0;
	// }
	//
	// return fitness;
	// }

	class AnchorInstruction{
		BytecodeInstruction ins;
		boolean isDetermined = true;
		public AnchorInstruction(BytecodeInstruction ins, boolean isDetermined) {
			super();
			this.ins = ins;
			this.isDetermined = isDetermined;
		}
		
		public String toString() {
			String msg = this.ins.toString() + ": isDetermined: " + this.isDetermined;
			return msg;
		}
	}
	
	private List<AnchorInstruction> findSourceForReturnInstructionInBlock(BasicBlock block) {
		List<AnchorInstruction> anchorList = new ArrayList<>();
		
		BytecodeInstruction lastIns = block.getLastInstruction();
		BytecodeInstruction firstIns = block.getFirstInstruction();
		BytecodeInstruction ins = lastIns;
		
		while (ins.getInstructionId() >= firstIns.getInstructionId()) {
			if (ins.isConstant()) {
				anchorList.add(new AnchorInstruction(ins, true));
				break;
			} else if (ins.isFieldUse()) {
				anchorList.add(new AnchorInstruction(ins, false));
				break;
			} else if (ins.isMethodCall()) {
				MethodInsnNode mNode = (MethodInsnNode) ins.getASMNode();
				String desc = mNode.desc;
				String returnType = getReturnType(desc);
				boolean isInterprocedural = returnType.equals("Z");
				
				boolean determined = canMethodContainConstantReturn(ins.getCalledCFG());
				
				if(isInterprocedural) {
					anchorList.add(new AnchorInstruction(ins, determined));
					break;
				}
			} else if (ins.isLocalVariableUse()) {
				List<BytecodeInstruction> istores = findDefinitions(ins);
				
				if(istores.isEmpty()) {
					anchorList.add(new AnchorInstruction(ins, true));
					break;
				}
				
				for(BytecodeInstruction istore: istores) {
					BytecodeInstruction prevIns = istore.getPreviousInstruction();
					if(prevIns.isConstant()) {
						anchorList.add(new AnchorInstruction(prevIns, true));
					}
					else {
						anchorList.add(new AnchorInstruction(ins, false));
					}
				}
				
			}
			ins = ins.getPreviousInstruction();
		}

		return anchorList;
	}

	private boolean canMethodContainConstantReturn(RawControlFlowGraph calledCFG) {
		for(BytecodeInstruction ins: calledCFG.determineExitPoints()) {
			if(ins.isReturn()) {
				BytecodeInstruction returnDef = ins.getSourceOfStackInstruction(0);
				if(returnDef.isConstant()) {
					return true;
				}
			}
		}
		return false;
	}

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

	private BytecodeInstruction findInstruction(RawControlFlowGraph rawCFG, AbstractInsnNode node) {
		for(BytecodeInstruction ins: rawCFG.vertexSet()) {
			if(ins.getASMNode().equals(node)) {
				return ins;
			}
		}
		return null;
	}

	private List<AnchorInstruction> getAnchorInstructions(RawControlFlowGraph calledGraph,
			BytecodeInstruction returnIns) {
//		List<BytecodeInstruction> sourceInsList = new ArrayList<>();

		BasicBlock block = returnIns.getBasicBlock();
		List<AnchorInstruction> sourceInsList = findSourceForReturnInstructionInBlock(block);
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

	public boolean isInconsistencyHappen() {
		return inconsistencyHappen;
	}

	public void setInconsistencyHappen(boolean inconsistencyHappen) {
		this.inconsistencyHappen = inconsistencyHappen;
	}

}
