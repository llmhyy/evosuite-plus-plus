package org.evosuite.graphs.dataflow;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * This class should detailedly describe the dependent variables, including whether it is
 * a parameter, field, global variables, etc.
 * @author linyun
 *
 */
public class DepVariable {
	private String className;
	private String varName;
	private BytecodeInstruction instruction;
	
	private Pair<String, DepVariable> relation;
	
	private DepVariable parent;
	
	public DepVariable(String className, String varName, BytecodeInstruction insn, DepVariable parent) {
		this.className = className;
		this.varName = varName;
		this.setInstruction(insn);
		this.parent = parent;
	}
	
	
	@Override
	public String toString() {
		return "DepVariable [className=" + className + ", varName=" + varName + ", instruction=" + getInstruction() + "]";
	}


	public DepVariable getParent() {
		return parent;
	}


	public void setParent(DepVariable parent) {
		this.parent = parent;
	}


	public BytecodeInstruction getInstruction() {
		return instruction;
	}


	public void setInstruction(BytecodeInstruction instruction) {
		this.instruction = instruction;
	}


	public Pair<String, DepVariable> getRelation() {
		return relation;
	}


	public void setRelation(Pair<String, DepVariable> relation) {
		this.relation = relation;
	}

	public boolean isParameter() {
		if(this.instruction.isLocalVariableUse()) {
			int paramNum = this.instruction.getCalledMethodsArgumentCount();
			int slot = this.instruction.getLocalVariableSlot();
			
			return slot < paramNum+1 && slot != 0;
		}
		return false;
	}

	public boolean isStaticField() {
		return this.instruction.getASMNode().getOpcode() == Opcodes.GETSTATIC;
	}

	public boolean isInstaceField() {
		return this.instruction.getASMNode().getOpcode() == Opcodes.GETFIELD;
	}
}
