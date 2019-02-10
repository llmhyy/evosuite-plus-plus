package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
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
	private double epsilon = 0.00001;

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
		
		System.currentTimeMillis();

		return fitness;
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
		
//		List<Call> newContext = updateCallContext(flagCallInstruction, callContext);
		Set<BytecodeInstruction> exits = calledGraph.determineExitPoints();
		System.currentTimeMillis();

		List<Double> returnFitnessList = new ArrayList<>();
		for (BytecodeInstruction returnIns : exits) {
			List<Double> fList = calculateReturnInsFitness(returnIns, branchGoal, calledGraph, result, callContext);
			returnFitnessList.addAll(fList);
		}

		double sum = this.epsilon;
		for (Double f : returnFitnessList) {
			sum += 1 / (f + this.epsilon);
		}

		return 1 / sum;
	}
	
	/**
	 * We do not further analyze recursive method calls.
	 */
	@SuppressWarnings("unchecked")
	private List<Call> updateCallContext(BytecodeInstruction sourceIns, List<Call> callContext){
		List<Call> newContext = (List<Call>) ((ArrayList<Call>)callContext).clone();
//		BytecodeInstruction ins = sourceIns.getCalledCFG().getInstruction(0);
		Call call = new Call(sourceIns.getClassName(), sourceIns.getMethodName(), sourceIns.getLineNumber());
		if(!newContext.contains(call)) {
			newContext.add(call);
		}
		
		return newContext;
	}
	
	private List<Double> calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result, List<Call> callContext) {
		List<BytecodeInstruction> sourceInsList = getReturnConstants(calledGraph, returnIns);
		List<Double> fitnessList = new ArrayList<>();
		
		fileterSourceInsList(result, sourceInsList, callContext);
		
		System.currentTimeMillis();

		for (BytecodeInstruction sourceIns : sourceInsList) {
			if (!sourceIns.isConstant() && !sourceIns.isMethodCall()) {
				logger.error("the source of ireturn is not a constant.");
				continue;
			}

			if (sourceIns.getControlDependencies().isEmpty() || sourceIns.isMethodCall()) {
				String calledMethod = sourceIns.getCalledMethod();
				if (calledMethod != null) {
					List<Call> newContext = updateCallContext(sourceIns, callContext);
					
					if(newContext.size()!=callContext.size()) {
						double f = calculateInterproceduralFitness(sourceIns, newContext, branchGoal, result);
						fitnessList.add(f);						
					}
					else {
						fitnessList.add(1.0);		
					}
					
					continue;
				}
			}
			
			Branch newDepBranch = null;
			boolean goalValue = false;
			double fitness = 0;
			for(ControlDependency cd: sourceIns.getControlDependencies()) {
				newDepBranch = cd.getBranch();
				
				// TODO I am not that clear about getControlDependency() method, but I find
				// reverse the direction make it correct.
				goalValue = sourceIns.getControlDependency(newDepBranch).getBranchExpressionValue();
				DistanceCondition dCondition = checkOverallDistance(result, goalValue, newDepBranch, callContext);
				fitness = dCondition.fitness;
				
				// in case the returned direction is wrong.
				if (fitness == 0) {
					goalValue = !goalValue;
					dCondition = checkOverallDistance(result, goalValue, newDepBranch, callContext);
					fitness = dCondition.fitness;
				}	
				
				if(fitness==0) {
					System.err.print("wrong direction judgement");
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

		return fitnessList;
	}

	/**
	 * only keep the source instruction need to be covered
	 * 
	 * @param result
	 * @param sourceInsList
	 * @param cBranch
	 */
	private void fileterSourceInsList(ExecutionResult result,
			List<BytecodeInstruction> sourceInsList, List<Call> context) {
		
		
		List<BytecodeInstruction> coveredInsList = new ArrayList<>();
		for(BytecodeInstruction ins: sourceInsList) {
			for(ControlDependency cd: ins.getControlDependencies()) {
				List<Call> newContext = updateCallContext(ins, context);
				
				boolean branchExpression = cd.getBranchExpressionValue();
				double distance = checkContextBranchDistance(result, cd.getBranch(), branchExpression, newContext);
				
				if(distance == 0) {
					coveredInsList.add(ins);
					break;
				}
			}
		}
		
		Iterator<BytecodeInstruction> iter = sourceInsList.iterator();
		while(iter.hasNext()) {
			BytecodeInstruction ins = iter.next();
			for(BytecodeInstruction coveredIns: coveredInsList) {
				if(ins.explain().equals(coveredIns.explain())) {
					iter.remove();
					break;
				}
			}
		}
	}

//	class ContextBranch {
//		Branch branch;
//		
//		int branchID;
//		int contextLine;
//		String method;
//
//		public ContextBranch(Branch branch, int contextLine, String method) {
//			super();
//			if(branch != null) {
//				this.branch = branch;
//				this.branchID = branch.getActualBranchId();				
//			}
//			this.contextLine = contextLine;
//			this.method = method;
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + getOuterType().hashCode();
//			result = prime * result + branchID;
//			result = prime * result + contextLine;
//			result = prime * result + ((method == null) ? 0 : method.hashCode());
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			ContextBranch other = (ContextBranch) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
//			if (branchID != other.branchID)
//				return false;
//			if (contextLine != other.contextLine)
//				return false;
//			if (method == null) {
//				if (other.method != null)
//					return false;
//			} else if (!method.equals(other.method))
//				return false;
//			return true;
//		}
//
//		private FBranchTestFitness getOuterType() {
//			return FBranchTestFitness.this;
//		}
//
//		public BytecodeInstruction getInstruction() {
//			return this.branch.getInstruction();
//		}
//
//	}

	private DistanceCondition checkOverallDistance(ExecutionResult result, boolean goalValue, Branch branch, List<Call> originContext) {
		/**
		 * look for the covered branch
		 */
		Set<Branch> visitedBranches = new HashSet<>();

		List<Call> callContext = updateCallContext(branch.getInstruction(), originContext);
		
		int approachLevel = 0;
		double branchDistance = -1;
		while (!checkCovered(result, branch, goalValue, callContext) && !visitedBranches.contains(branch)) {
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

			branch = branch.getInstruction().getControlDependentBranch();
			
			goalValue = originBranchIns.getControlDependency(branch).getBranchExpressionValue();
			
			callContext = updateCallContext(branch.getInstruction(), originContext);
			branchDistance = checkContextBranchDistance(result, branch, goalValue, callContext);
			if(branchDistance == 0) {
				goalValue = !goalValue;
			}
			
		}

		if(branchDistance == -1) {
			branchDistance = checkContextBranchDistance(result, branch, goalValue, callContext);
		}
		
		double fitness = approachLevel + branchDistance;
		
		BranchCoverageGoal goal = new BranchCoverageGoal(branch, goalValue, branch.getClassName(), branch.getMethodName());
		return new DistanceCondition(fitness, goal);
	}

	private double checkContextBranchDistance(ExecutionResult result, Branch branch, boolean goalValue, List<Call> context) {
		Double value = null;
		
		if (goalValue) {
			Map<CallContext, Double> trueContextMap = result.getTrace().getTrueDistancesContext().get(branch.getActualBranchId());
			value = getContextDistance(trueContextMap, context);
		} else {
			Map<CallContext, Double> falseContextMap = result.getTrace().getFalseDistancesContext().get(branch.getActualBranchId());
			value = getContextDistance(falseContextMap, context);
		}	
		
		if(value == null) {
			if(goalValue) {
				value = result.getTrace().getTrueDistances().get(branch.getActualBranchId());
			}
			else {
				value = result.getTrace().getFalseDistances().get(branch.getActualBranchId());
			}
		}
		
		if (value == null) {
			value = 1000000d;
		}

		return FitnessFunction.normalize(value);
	}

	private boolean checkCovered(ExecutionResult result, Branch branch, boolean goalValue, List<Call> callContext) {
		Map<CallContext, Double> falseContextMap = result.getTrace().getFalseDistancesContext().get(branch.getActualBranchId());
		Double value = getContextDistance(falseContextMap, callContext);
		if(value != null && value == 0) {
			return true;
		}
		
		Map<CallContext, Double> trueContextMap = result.getTrace().getTrueDistancesContext().get(branch.getActualBranchId());
		value = getContextDistance(trueContextMap, callContext);
		if(value != null && value == 0) {
			return true;
		}
		
		if(value == null) {
			Double v = result.getTrace().getFalseDistances().get(branch.getActualBranchId());
			if(v!=null && v==0) {
				return true;
			}
			
			v = result.getTrace().getTrueDistances().get(branch.getActualBranchId());
			if(v!=null && v==0) {
				return true;
			}
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
			
			
			
//			if(calls.size() >= 2) {
//				Call lastCall = calls.get(calls.size()-2);
//				if(lastCall.getLineNumber() == cBranch.contextLine) {
//					System.currentTimeMillis();
//					return contextMap.get(key);
//				}				
//			}
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
			callInfo = new Call(instruction.getClassName(), instruction.getMethodName(), interproceduralFlagCall.getLineNumber());
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

	private BytecodeInstruction findSourceForReturnInstructionInBlock(BasicBlock block) {
		BytecodeInstruction lastIns = block.getLastInstruction();
		BytecodeInstruction firstIns = block.getFirstInstruction();
		BytecodeInstruction ins = lastIns;
		
		while (ins.getInstructionId() >= firstIns.getInstructionId()) {
			if (ins.isConstant()) {
				return ins;
			} else if (ins.isFieldUse()) {
				return ins;
			} else if (ins.isMethodCall()) {
				MethodInsnNode mNode = (MethodInsnNode) ins.getASMNode();
				String desc = mNode.desc;
				String returnType = getReturnType(desc);
				boolean isInterprocedural = returnType.equals("Z");
				
				if(isInterprocedural) {
					return ins;
				}
			}
			ins = ins.getPreviousInstruction();
		}

		return null;
	}

	private List<BytecodeInstruction> getReturnConstants(RawControlFlowGraph calledGraph,
			BytecodeInstruction returnIns) {
		List<BytecodeInstruction> sourceInsList = new ArrayList<>();

		BasicBlock block = returnIns.getBasicBlock();
		BytecodeInstruction sourceIns = findSourceForReturnInstructionInBlock(block);
		if (sourceIns != null) {
			if (sourceIns.isConstant()) {
				sourceInsList.add(sourceIns);
			} else if (sourceIns.isFieldUse()) {
				// TODO need to handle dynamic definition
				System.currentTimeMillis();
			} else if (sourceIns.isMethodCall()) {
				sourceInsList.add(sourceIns);
			}
			
			return sourceInsList;
		}

		Set<BytecodeInstruction> insParents = calledGraph.getParents(block.getFirstInstruction());
		for (BytecodeInstruction parentIns : insParents) {
			BasicBlock parentBlock = parentIns.getBasicBlock();
			sourceIns = findSourceForReturnInstructionInBlock(parentBlock);
			if (sourceIns != null) {
				sourceInsList.add(sourceIns);
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
