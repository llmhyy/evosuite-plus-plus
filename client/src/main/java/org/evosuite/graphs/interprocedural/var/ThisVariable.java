package org.evosuite.graphs.interprocedural.var;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public class ThisVariable extends DepVariable{

	public ThisVariable(BytecodeInstruction insn) {
		super(insn);
	}

}
