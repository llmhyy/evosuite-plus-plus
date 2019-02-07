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
		
		if(value != null && value > 1) {
			return value;
		}
		else if(value == null){
			return 10000d;
		}
		else if(value == 0) {
			return value;
		}
		
//		ContextBranch cBranch = new ContextBranch(this.branchGoal.getBranch(), -1, "null");
//		DistanceCondition dCondition = checkOverallDistance(result, this.branchGoal.getValue(), cBranch);
//		double fitness = dCondition.fitness;
//		if (fitness == 0) {
//			return fitness;
//		}
//		
//		BranchCoverageGoal goal = this.branchGoal;
//		int approachLevel = (int)fitness;
//		if(approachLevel != 0) {
//			goal = dCondition.goal;
//		}

		double fitness = 0;
		BranchCoverageGoal goal = this.branchGoal;
		InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(goal);
		if (flagResult.isInterproceduralFlag) {
			double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall,
					goal, result);
//			System.currentTimeMillis();
			return interproceduralFitness;
			
		}
		
//		System.currentTimeMillis();

		return fitness;
	}

	private double calculateInterproceduralFitness(BytecodeInstruction flagCallInstruction,
			BranchCoverageGoal branchGoal, ExecutionResult result) {
		RawControlFlowGraph calledGraph = flagCallInstruction.getCalledCFG();
		String signature = flagCallInstruction.getCalledMethodsClass() + "." + flagCallInstruction.getCalledMethod();
		if (calledGraph == null) {
			RuntimeRecord.methodCallAvailabilityMap.put(signature, false);
			return 1;
		}
		RuntimeRecord.methodCallAvailabilityMap.put(signature, true);

		Set<BytecodeInstruction> exits = calledGraph.determineExitPoints();

		ContextBranch cBranch = new ContextBranch(null, flagCallInstruction.getLineNumber(), flagCallInstruction.getMethodName());
		
		List<Double> returnFitnessList = new ArrayList<>();
		for (BytecodeInstruction returnIns : exits) {
			List<Double> fList = calculateReturnInsFitness(returnIns, branchGoal, calledGraph, result, cBranch);
			returnFitnessList.addAll(fList);
		}

		double sum = this.epsilon;
		for (Double f : returnFitnessList) {
			sum += 1 / (f + this.epsilon);
		}

		return 1 / sum;
	}

	private List<Double> calculateReturnInsFitness(BytecodeInstruction returnIns, BranchCoverageGoal branchGoal,
			RawControlFlowGraph calledGraph, ExecutionResult result, ContextBranch cBranch) {
		List<BytecodeInstruction> sourceInsList = getReturnConstants(calledGraph, returnIns);
		// System.currentTimeMillis();
		List<Double> fitnessList = new ArrayList<>();
		
		fileterSourceInsList(result, sourceInsList, cBranch);

		for (BytecodeInstruction sourceIns : sourceInsList) {
			if (!sourceIns.isConstant()) {
				logger.error("the source of ireturn is not a constant.");
				continue;
			}

			if (sourceIns.getControlDependencies().isEmpty()) {
				String calledMethod = sourceIns.getCalledMethod();
				if (calledMethod != null) {
					double f = calculateInterproceduralFitness(sourceIns, branchGoal, result);
					fitnessList.add(f);
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
				cBranch.branch = newDepBranch;
				cBranch.branchID = newDepBranch.getActualBranchId();
				DistanceCondition dCondition = checkOverallDistance(result, goalValue, cBranch);
				fitness = dCondition.fitness;
				if (fitness == 0) {
					goalValue = !goalValue;
					dCondition = checkOverallDistance(result, goalValue, cBranch);
					fitness = dCondition.fitness;
				}	
				
				if(fitness==0) {
					System.err.print("wrong direction judgement");
					continue;
				}

				BranchCoverageGoal newGoal = dCondition.goal;

				InterproceduralFlagResult flagResult = isInterproceduralFlagProblem(newGoal);
				if (flagResult.isInterproceduralFlag) {
					double interproceduralFitness = calculateInterproceduralFitness(flagResult.interproceduralFlagCall,
							newGoal, result);
					fitnessList.add(interproceduralFitness);
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
			List<BytecodeInstruction> sourceInsList, ContextBranch cBranch) {
		List<BytecodeInstruction> coveredInsList = new ArrayList<>();
		for(BytecodeInstruction ins: sourceInsList) {
			for(ControlDependency cd: ins.getControlDependencies()) {
				ContextBranch newCBranch = new ContextBranch(cd.getBranch(), cBranch.contextLine, cBranch.method);
				
				boolean branchExpression = cd.getBranchExpressionValue();
				double distance = checkContextBranchDistance(result, branchExpression, newCBranch);
				
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

	class ContextBranch {
		Branch branch;
		
		int branchID;
		int contextLine;
		String method;

		public ContextBranch(Branch branch, int contextLine, String method) {
			super();
			if(branch != null) {
				this.branch = branch;
				this.branchID = branch.getActualBranchId();				
			}
			this.contextLine = contextLine;
			this.method = method;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + branchID;
			result = prime * result + contextLine;
			result = prime * result + ((method == null) ? 0 : method.hashCode());
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
			ContextBranch other = (ContextBranch) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (branchID != other.branchID)
				return false;
			if (contextLine != other.contextLine)
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			return true;
		}

		private FBranchTestFitness getOuterType() {
			return FBranchTestFitness.this;
		}

		public BytecodeInstruction getInstruction() {
			return this.branch.getInstruction();
		}

	}

	private DistanceCondition checkOverallDistance(ExecutionResult result, boolean goalValue, ContextBranch cBranch) {
		/**
		 * look for the covered branch
		 */
		Set<ContextBranch> visitedBranches = new HashSet<>();

		int approachLevel = 0;
		double branchDistance = -1;
		while (!checkCovered(result, cBranch) && !visitedBranches.contains(cBranch)) {
			approachLevel++;
			
			visitedBranches.add(cBranch);

			BytecodeInstruction originBranchIns = cBranch.getInstruction();
			
			/**
			 * it means that an exception happens on the way to originBranchIns, we should
			 * add a penalty to such test case.
			 */
			if (originBranchIns.getControlDependentBranch() == null) {
				approachLevel = 100000;
				break;
			}

			Branch newDepBranch = cBranch.getInstruction().getControlDependentBranch();
			cBranch = new ContextBranch(newDepBranch, cBranch.contextLine, cBranch.method);
			
			goalValue = originBranchIns.getControlDependency(newDepBranch).getBranchExpressionValue();
			branchDistance = checkContextBranchDistance(result, goalValue, cBranch);
			if(branchDistance == 0) {
				goalValue = !goalValue;
			}
			
		}

		if(branchDistance == -1) {
			branchDistance = checkContextBranchDistance(result, goalValue, cBranch);
		}
		
		double fitness = approachLevel + branchDistance;
		
		BranchCoverageGoal goal = new BranchCoverageGoal(cBranch.branch, goalValue, cBranch.branch.getClassName(), cBranch.branch.getMethodName());
		return new DistanceCondition(fitness, goal);
	}

	private double checkContextBranchDistance(ExecutionResult result, boolean goalValue, ContextBranch cBranch) {
		Double value;
		
		if(cBranch.contextLine == -1 && cBranch.method.equals("null")) {
			if(goalValue) {
				value = result.getTrace().getTrueDistances().get(cBranch.branchID);
			}
			else {
				value = result.getTrace().getFalseDistances().get(cBranch.branchID);
			}
			
		}
		else {
			if (goalValue) {
				Map<CallContext, Double> trueContextMap = result.getTrace().getTrueDistancesContext().get(cBranch.branchID);
				value = getContextDistance(trueContextMap, cBranch);
			} else {
				Map<CallContext, Double> falseContextMap = result.getTrace().getFalseDistancesContext().get(cBranch.branchID);
				value = getContextDistance(falseContextMap, cBranch);
			}			
		}
		
		if (value == null) {
			value = 1000000d;
		}

		return FitnessFunction.normalize(value);
	}

	private boolean checkCovered(ExecutionResult result, ContextBranch cBranch) {
		if(cBranch.contextLine == -1 && cBranch.method.equals("null")) {
			Double v = result.getTrace().getFalseDistances().get(cBranch.branchID);
			if(v!=null && v==0) {
				return true;
			}
			
			v = result.getTrace().getTrueDistances().get(cBranch.branchID);
			if(v!=null && v==0) {
				return true;
			}
		}
		
		
		Map<CallContext, Double> falseContextMap = result.getTrace().getFalseDistancesContext().get(cBranch.branchID);
		Double value = getContextDistance(falseContextMap, cBranch);
		if(value != null && value == 0) {
			return true;
		}
		
		Map<CallContext, Double> trueContextMap = result.getTrace().getTrueDistancesContext().get(cBranch.branchID);
		value = getContextDistance(trueContextMap, cBranch);
		if(value != null && value == 0) {
			return true;
		}

		return false;
	}

	private Double getContextDistance(Map<CallContext, Double> contextMap, ContextBranch cBranch) {
		if(contextMap == null) {
			return null;
		}
		
		if(cBranch.contextLine == -1) {
			for(CallContext key: contextMap.keySet()) {
				return contextMap.get(key);
			}
		}
		
		
		for(CallContext key: contextMap.keySet()) {
			List<Call> calls = key.getContext();
			if(calls.size() >= 2) {
				Call lastCall = calls.get(calls.size()-2);
				if(lastCall.getLineNumber() == cBranch.contextLine) {
					System.currentTimeMillis();
					return contextMap.get(key);
				}				
			}
		}
		
		return null;
	}

	class InterproceduralFlagResult {
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
		if (interproceduralFlagCall != null && interproceduralFlagCall.getASMNode() instanceof MethodInsnNode) {
			MethodInsnNode mNode = (MethodInsnNode) interproceduralFlagCall.getASMNode();
			String desc = mNode.desc;
			String returnType = getReturnType(desc);
			isInterproceduralFlag = returnType.equals("Z");
		}

		InterproceduralFlagResult result = new InterproceduralFlagResult(interproceduralFlagCall,
				isInterproceduralFlag);
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

	private BytecodeInstruction findLastConstantInstructionInBlock(BasicBlock block) {
		BytecodeInstruction lastIns = block.getLastInstruction();
		BytecodeInstruction firstIns = block.getFirstInstruction();
		BytecodeInstruction ins = lastIns;
		while (ins.getInstructionId() >= firstIns.getInstructionId()) {
			if (ins.isConstant()) {
				return ins;
			} else if (ins.isFieldUse()) {
				return ins;
			}
			ins = ins.getPreviousInstruction();
		}

		return null;
	}

	private List<BytecodeInstruction> getReturnConstants(RawControlFlowGraph calledGraph,
			BytecodeInstruction returnIns) {
		List<BytecodeInstruction> sourceInsList = new ArrayList<>();

		BasicBlock block = returnIns.getBasicBlock();
		BytecodeInstruction constantIns = findLastConstantInstructionInBlock(block);
		if (constantIns != null) {
			if (constantIns.isConstant()) {
				sourceInsList.add(constantIns);
			} else if (constantIns.isFieldUse()) {
				// TODO need to handle dynamic definition
				System.currentTimeMillis();
			}
			return sourceInsList;
		}

		Set<BytecodeInstruction> insParents = calledGraph.getParents(block.getFirstInstruction());
		for (BytecodeInstruction parentIns : insParents) {
			BasicBlock parentBlock = parentIns.getBasicBlock();
			constantIns = findLastConstantInstructionInBlock(parentBlock);
			if (constantIns != null) {
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
