package com.test;

import java.util.Iterator;
import java.util.List;

import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;


public class DefUseAnalyzer {

	public void analyze(ClassLoader classLoader, MethodNode mn, String className,
	        String methodName, int access) {
		RawControlFlowGraph completeCFG = GraphPool.getInstance(classLoader).getRawCFG(className,
		                                                                               methodName);
		Iterator<AbstractInsnNode> j = mn.instructions.iterator();
		while (j.hasNext()) {
			AbstractInsnNode in = j.next();
			for (BytecodeInstruction v : completeCFG.vertexSet()) {
			    if (in.equals(v.getASMNode()) && v.isDefUse()) {
					boolean isValidDU = false;

//					if(v.isLocalArrayDefinition()) {
//						LoggingUtils.getEvoLogger().info(
//							"LOCAL ARRAY VAR DEF " + v.toString()+" loaded by "+v.getSourceOfStackInstruction(2).toString());
//					}
					
					if (v.isMethodCallOfField()) {
						// keep track of field method calls, though we do not
						// know
						// how to handle them at this point during the analysis
						// (need complete CCFGs first)
						isValidDU = DefUsePool.addAsFieldMethodCall(v);
					} else {
						// keep track of uses
						if (v.isUse())
							isValidDU = DefUsePool.addAsUse(v);
						// keep track of definitions
						if (v.isDefinition())
							isValidDU = DefUsePool.addAsDefinition(v) || isValidDU;
					}
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private int getNextLocalVariable(MethodNode mn) {
		int var = 1;
		List<LocalVariableNode> nodes = mn.localVariables;
		for(LocalVariableNode varNode : nodes) {
			if(varNode.index >= var) {
				var = varNode.index + 1;
			}
		}
		return var;
	}
	

}
