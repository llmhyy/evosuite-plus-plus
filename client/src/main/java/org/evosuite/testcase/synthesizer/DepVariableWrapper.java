package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.graphs.dataflow.DepVariable;

public class DepVariableWrapper {
	public DepVariable var;
	public List<DepVariableWrapper> children = new ArrayList<>();
	public List<DepVariableWrapper> parents = new ArrayList<>();
	
	public DepVariableWrapper(DepVariable var) {
		this.var = var;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof DepVariableWrapper) {
			DepVariableWrapper varWrapper = (DepVariableWrapper)obj;
			return varWrapper.var.equals(this.var);
		}
		
		return false;
	}
	
	public void addParent(DepVariableWrapper parent) {
		if(!this.parents.contains(parent)) {
			this.parents.add(parent);
		}
	}
	
	public void addChild(DepVariableWrapper child) {
		if(!this.children.contains(child)) {
			this.children.add(child);
		}
	}
	
	public String toString() {
		return var.toString();
	}
}
