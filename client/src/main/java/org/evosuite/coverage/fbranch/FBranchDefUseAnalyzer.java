package org.evosuite.coverage.fbranch;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;

public class FBranchDefUseAnalyzer {
	public static Set<String> analyzedMethods = new HashSet<String>();
	
	public static void analyze(RawControlFlowGraph completeCFG) {
		String methodName = completeCFG.getClassName() + "#" + completeCFG.getMethodName();
		if(analyzedMethods.contains(methodName)) {
			return;
		}
		else {
			analyzedMethods.add(methodName);
		}
		
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
							DefUsePool.addAsUse(v);						
						}
						// keep track of definitions
						if (v.isDefinition()) {
							DefUsePool.addAsDefinition(v);
						}						
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
