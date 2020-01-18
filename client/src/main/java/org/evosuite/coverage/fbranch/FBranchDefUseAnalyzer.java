package org.evosuite.coverage.fbranch;

import java.util.HashMap;
import java.util.Map;

import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;

public class FBranchDefUseAnalyzer {
	public static Map<String, Boolean> analyzedMethods = new HashMap<>();
	
	public static boolean analyze(RawControlFlowGraph completeCFG) {
		String methodName = completeCFG.getClassName() + "#" + completeCFG.getMethodName();
		if(analyzedMethods.containsKey(methodName)) {
			return analyzedMethods.get(methodName);
		}
		
		boolean isValid = true;
		for (BytecodeInstruction v : completeCFG.vertexSet()) {
		    if (v.isDefUse()) {

				if (v.isMethodCallOfField()) {
					// keep track of field method calls, though we do not
					// know
					// how to handle them at this point during the analysis
					// (need complete CCFGs first)
					DefUsePool.addAsFieldMethodCall(v);
				} else {
					// keep track of uses
					try {
						if (v.isUse()) {
							boolean success = DefUsePool.addAsUse(v);		
							if(!success) isValid = false;
						}
						// keep track of definitions
						if (v.isDefinition()) {
							DefUsePool.addAsDefinition(v);
						}						
					}
					catch(Exception e) {
						isValid = false;
					}
				}
			}
		}
		
		
		analyzedMethods.put(methodName, isValid);
		
		return isValid;
	}
}
