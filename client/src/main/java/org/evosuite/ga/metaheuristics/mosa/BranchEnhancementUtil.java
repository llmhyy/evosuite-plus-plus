package org.evosuite.ga.metaheuristics.mosa;

import java.util.List;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.setup.Call;

public class BranchEnhancementUtil {
	public static String covert2Sig(StackTraceElement elementToCallException) {
		String className = elementToCallException.getClassName();
		int lineNumber = elementToCallException.getLineNumber();
		
		List<BytecodeInstruction> insList = 
				BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).
				getAllInstructionsAtClass(className, lineNumber);
		
		if(insList == null) {
			System.currentTimeMillis();
		}
		
		if(!insList.isEmpty()) {
			BytecodeInstruction instruction = insList.get(0);
			return className + "." + instruction.getMethodName();
		}
		
		return null;
	}
	
	public static String covert2Sig(Call call) {
		String className = call.getClassName();
		int lineNumber = call.getLineNumber();
		
		List<BytecodeInstruction> insList = 
				BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).
				getAllInstructionsAtClass(className, lineNumber);
		
		if(insList == null) {
			System.currentTimeMillis();
		}
		
		if(!insList.isEmpty()) {
			BytecodeInstruction instruction = insList.get(0);
			return className + "." + instruction.getMethodName();
		}
		
		return null;
	}
	
//	@SuppressWarnings("rawtypes")
//	public static BranchCoverageGoal getBranchGoal(FitnessFunction ff) {
//		if (ff instanceof FBranchTestFitness) {
//			return ((FBranchTestFitness) ff).getBranchGoal();
//		} else if (ff instanceof BranchCoverageTestFitness) {
//			return ((BranchCoverageTestFitness) ff).getBranchGoal();
//		}
//
//		return null;
//	}
}
