package org.evosuite.testcase.synthesizer.improvedsynth;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public class ArrayElementAccess extends Operation {
	// The instruction directly preceding the array load instruction
	// This is assumed to be the instruction indicating the array index
	// To be used during construction later
	private final BytecodeInstruction indexInstruction;
	
	public ArrayElementAccess(BytecodeInstruction indexInstruction) {
		this.indexInstruction = indexInstruction;
	}
	
	public BytecodeInstruction getIndexInstruction() {
		return indexInstruction;
	}
	
	@Override
	public String toString() {
		return "[ArrayElementAccess]: " + indexInstruction.toString();
	}
}
