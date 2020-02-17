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
				getAllInstructionsAtLineNumber(className, lineNumber);
		
		if(insList == null) {
			return null;
		}
		
		if(!insList.isEmpty()) {
			BytecodeInstruction instruction = insList.get(0);
			return className + "." + instruction.getMethodName();
		}
		
		return null;
	}
	
	/**
	 * return the method full name including class name 
	 * @param call
	 * @return
	 */
	public static String covert2Sig(Call call) {
		String className = call.getClassName();
		int lineNumber = call.getLineNumber();
		
		List<BytecodeInstruction> insList = 
				BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).
				getAllInstructionsAtLineNumber(className, lineNumber);
		
		if(insList == null) {
			System.currentTimeMillis();
		}
		
		if(!insList.isEmpty()) {
			BytecodeInstruction instruction = insList.get(0);
			return className + "." + instruction.getMethodName();
		}
		
		return null;
	}
	
}
