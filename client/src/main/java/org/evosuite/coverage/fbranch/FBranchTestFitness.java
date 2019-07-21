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
public class FBranchTestFitness extends BranchCoverageTestFitness {

	private static final long serialVersionUID = -3538507758440177708L;
	private static Logger logger = LoggerFactory.getLogger(FBranchTestFitness.class);

//	private final BranchCoverageGoal goal;
	private boolean inconsistencyHappen = false;

	public FBranchTestFitness(BranchCoverageGoal branchGoal) {
		super(branchGoal);
	}

	public Branch getBranch() {
		return this.goal.getBranch();
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
			return normalize(value);
		}
		
		double fitness = value;
		BranchCoverageGoal goal = this.goal;
		FlagEffectResult flagResult = isFlagMethod(goal);
		if (flagResult.isInterproceduralFlag) {
			List<Call> callContext = new ArrayList<>();
			callContext.add(flagResult.call);
			
			double interproceduralFitness = FlagBranchEvaluator.calculateInterproceduralFitness(flagResult.interproceduralFlagCall, 
					callContext, goal, result, this.goal);
			double normalizedFitness = normalize(interproceduralFitness);
			
			return normalizedFitness;
		}
		else {
			FlagEffectResult r = FlagEffectChecker.checkFlagEffect(goal);
			if(r.isInterproceduralFlag) {
				
			}
		}
		
		return fitness;
	}
	
	private String getSimpleMethod(String methodName) {
		String sName = methodName.substring(0, methodName.indexOf("("));
		return sName;
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
			
			callContext = updateCallContext(branch.getInstruction(), originContext);
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
								
								List<Call> replacedContext = replaceContext(context, delegateBranch);
								
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

	private List<Call> replaceContext(List<Call> context, Branch delegateBranch) {
		List<Call> replacedContext = new ArrayList<>();
		for(int i=0; i<context.size()-1; i++) {
			replacedContext.add(context.get(i));
		}
		
		replacedContext = updateCallContext(delegateBranch.getInstruction(), replacedContext);
		return replacedContext;
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

	private FlagEffectResult isFlagMethod(BranchCoverageGoal goal) {
		BytecodeInstruction instruction = goal.getBranch().getInstruction();
		
		BytecodeInstruction interproceduralFlagCall = instruction.getSourceOfStackInstruction(0);
		boolean isInterproceduralFlag = false;
		Call callInfo = null;
		if (interproceduralFlagCall != null && interproceduralFlagCall.getASMNode() instanceof MethodInsnNode) {
			MethodInsnNode mNode = (MethodInsnNode) interproceduralFlagCall.getASMNode();
			String desc = mNode.desc;
			String returnType = getReturnType(desc);
			isInterproceduralFlag = returnType.equals("Z");
			callInfo = new Call(instruction.getClassName(), instruction.getMethodName(), 
					interproceduralFlagCall.getInstructionId());
		}

		FlagEffectResult result = new FlagEffectResult(interproceduralFlagCall,
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
		if(calledCFG==null) {
			return false;
		}
		
		for(BytecodeInstruction ins: calledCFG.determineExitPoints()) {
			if(ins.isReturn()) {
				if(ins.getFrame()==null) {
					System.currentTimeMillis();
				}
				
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

	private BytecodeInstruction findInstruction(RawControlFlowGraph rawCFG, AbstractInsnNode node) {
		for(BytecodeInstruction ins: rawCFG.vertexSet()) {
			if(ins.getASMNode().equals(node)) {
				return ins;
			}
		}
		return null;
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

	public boolean isInconsistencyHappen() {
		return inconsistencyHappen;
	}

	public void setInconsistencyHappen(boolean inconsistencyHappen) {
		this.inconsistencyHappen = inconsistencyHappen;
	}

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
