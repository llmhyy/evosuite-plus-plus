package org.evosuite.graphs.interprocedural.var;

import org.evosuite.graphs.cfg.BytecodeInstruction;

/**
 * a parameter of the target method call
 * 
 * @author Yun Lin
 *
 */
public class ParameterVariable extends DepVariable{

	public ParameterVariable(BytecodeInstruction insn) {
		super(insn);
	}

	
}
