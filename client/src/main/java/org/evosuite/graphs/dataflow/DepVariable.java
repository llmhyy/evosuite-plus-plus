package org.evosuite.graphs.dataflow;

import java.util.HashSet;
import java.util.Set;

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
	private Set<DepVariable> depVars;
	private BytecodeInstruction depIns;
	private int layer;

	public DepVariable(String className, String varName, BytecodeInstruction insn) {
		this.className = className;
		this.varName = varName;
		this.depIns = insn;
		depVars = new HashSet<DepVariable>();
	}
	
	public DepVariable(String className, String varName, BytecodeInstruction insn, int layer) {
		this.className = className;
		this.varName = varName;
		this.depIns = insn;
		depVars = new HashSet<DepVariable>();
		this.layer = layer;
	}
	
	public void addDepForVar(DepVariable depVar) {
		this.getDepVariables().add(depVar);
	}
	
	public Set<DepVariable> getDepVariables() {
		return this.depVars;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public int getLayer(int layer) {
		return this.layer;
	}
}
