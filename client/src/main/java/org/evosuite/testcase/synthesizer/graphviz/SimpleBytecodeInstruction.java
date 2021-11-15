package org.evosuite.testcase.synthesizer.graphviz;

import java.io.Serializable;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public class SimpleBytecodeInstruction implements Serializable {
	private static final long serialVersionUID = -7667999648326452311L;
	
	// identification of a byteCode instruction inside EvoSuite
	public String className;
	public String methodName;
	public int instructionId;
	public int bytecodeOffset;

	// auxiliary information
	public int lineNumber = -1;
	
	// Might be useful
	public String stringRepresentation;
	
	public SimpleBytecodeInstruction() {
	}
	
	public static SimpleBytecodeInstruction from(BytecodeInstruction instr) {
		SimpleBytecodeInstruction toReturn = new SimpleBytecodeInstruction();
		toReturn.className = instr.getClassName();
		toReturn.methodName = instr.getMethodName();
		toReturn.instructionId = instr.getInstructionId();
		toReturn.bytecodeOffset = instr.getBytecodeOffset();
		toReturn.lineNumber = instr.getLineNumber();
		toReturn.stringRepresentation = instr.toString();
		
		return toReturn;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bytecodeOffset;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + instructionId;
		result = prime * result + lineNumber;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + ((stringRepresentation == null) ? 0 : stringRepresentation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleBytecodeInstruction other = (SimpleBytecodeInstruction) obj;
		if (bytecodeOffset != other.bytecodeOffset)
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (instructionId != other.instructionId)
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (stringRepresentation == null) {
			if (other.stringRepresentation != null)
				return false;
		} else if (!stringRepresentation.equals(other.stringRepresentation))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return stringRepresentation;
	}
}
