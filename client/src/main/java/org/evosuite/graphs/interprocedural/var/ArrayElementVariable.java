package org.evosuite.graphs.interprocedural.var;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public class ArrayElementVariable extends DepVariable{

	public ArrayElementVariable(BytecodeInstruction insn) {
		super(insn);
	}

}
