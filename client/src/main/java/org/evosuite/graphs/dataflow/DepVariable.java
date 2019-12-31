package org.evosuite.graphs.dataflow;

import org.evosuite.graphs.cfg.BytecodeInstruction;

/**
 * This class should detailedly describe the dependent variables, including whether it is
 * a parameter, field, global variables, etc.
 * @author linyun
 *
 */
public class DepVariable {
	private String className;
	private String varName;
	private BytecodeInstruction depIns;
	
	private DepVariable parent;
	
//	private int layer;

	public DepVariable(String className, String varName, BytecodeInstruction insn, DepVariable parent) {
		this.className = className;
		this.varName = varName;
		this.depIns = insn;
		this.parent = parent;
	}
	
	
	@Override
	public String toString() {
		return "DepVariable [className=" + className + ", varName=" + varName + ", depIns=" + depIns + "]";
	}


	public DepVariable getParent() {
		return parent;
	}


	public void setParent(DepVariable parent) {
		this.parent = parent;
	}
	
	
}
