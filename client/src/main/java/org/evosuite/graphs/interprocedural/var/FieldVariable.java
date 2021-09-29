package org.evosuite.graphs.interprocedural.var;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public abstract class FieldVariable extends DepVariable{

	protected FieldVariable(BytecodeInstruction ins) {
		super(ins);
	}
	
}
