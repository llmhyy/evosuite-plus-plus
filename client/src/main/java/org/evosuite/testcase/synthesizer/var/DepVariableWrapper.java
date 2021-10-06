package org.evosuite.testcase.synthesizer.var;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.VariableInTest;
import org.evosuite.testcase.variable.VariableReference;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public abstract class DepVariableWrapper {
	public DepVariable var;
	public List<DepVariableWrapper> children = new ArrayList<>();
	public List<DepVariableWrapper> parents = new ArrayList<>();
	
	/**
	 * It means the variable has been accessed by the code of the enriching test code
	 */
	public boolean processed = false;
	
	protected DepVariableWrapper(DepVariable var) {
		this.var = var;
	}
	
	public abstract List<VariableReference> generateOrFindStatement(TestCase test, boolean isLeaf, VariableInTest variable,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue);
	
	public List<VariableReference> findCorrespondingVariables(TestCase test, boolean isLeaf, VariableReference callerObject, 
			Map<DepVariableWrapper, List<VariableReference>> map) {
		List<VariableReference> vars = new ArrayList<>();
		VariableReference var = find(test, isLeaf, callerObject, map);
		if(var != null) {
			vars.add(var);			
		}
		return vars;
	}
	
	public abstract VariableReference find(TestCase test, boolean isLeaf, VariableReference callerObject, Map<DepVariableWrapper, List<VariableReference>> map);
	
	@Override
	public int hashCode() {
		return this.var.getType();
	}
	
	public boolean equals(Object obj) {
		
		if(obj instanceof DepVariableWrapper) {
			DepVariableWrapper varWrapper = (DepVariableWrapper)obj;
			if(var.equals(varWrapper.var)) {
				return true;
			}
			else {
				DepVariable other = varWrapper.var;
				if(var.getType() == other.getType()) {
					if(var.getType() == DepVariable.INSTANCE_FIELD) {
						FieldInsnNode thatField = (FieldInsnNode)(var.getInstruction().getASMNode());
						FieldInsnNode thisField = (FieldInsnNode)(other.getInstruction().getASMNode());
						return thisField.desc.equals(thatField.desc) &&
								thisField.owner.equals(thatField.owner) &&
								thisField.name.equals(thatField.name);
					}
					else if(var.getType() == DepVariable.STATIC_FIELD) {
						FieldInsnNode thatField = (FieldInsnNode)(var.getInstruction().getASMNode());
						FieldInsnNode thisField = (FieldInsnNode)(other.getInstruction().getASMNode());
						return thisField.desc.equals(thatField.desc) &&
								thisField.owner.equals(thatField.owner) &&
								thisField.name.equals(thatField.name);
					}
					else if(var.getType() == DepVariable.PARAMETER) {
						return var.getInstruction().getClassName().equals(other.getInstruction().getClassName()) &&
								var.getInstruction().getMethodName().equals(other.getInstruction().getMethodName()) &&
								var.getParamOrder() == other.getParamOrder();
					}
					else if(var.getType() == DepVariable.THIS) {
						return var.getInstruction().getClassName().equals(other.getInstruction().getClassName());
					}
					else if(var.getType() == DepVariable.ARRAY_ELEMENT) {
						//FIXME ziheng
					}
					else if(var.getType() == DepVariable.OTHER) {
						//FIXME ziheng
						/**
						 * we need to come up with how to define the equivalence of two instruction like method call, etc.
						 */
						
						if(var.getInstruction().toString().contains("ALOAD") && other.getInstruction().toString().contains("ALOAD")) {
							
							if(var.getInstruction().getClassName().equals(other.getInstruction().getClassName()) &&
									var.getInstruction().getMethodName().equals(other.getInstruction().getMethodName())) {
								
								AbstractInsnNode node1 = var.getInstruction().getASMNode();
								AbstractInsnNode node2 = other.getInstruction().getASMNode();
								if(node1 instanceof VarInsnNode && node2 instanceof VarInsnNode) {
									VarInsnNode v1 = (VarInsnNode)node1;
									VarInsnNode v2 = (VarInsnNode)node2;
									
									return (v1.var == v2.var);
								}
							}
							
						}
					}
				}
				
				return false;
			}
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
	
	public int findRelationPosition(DepVariableWrapper node) {
		for(int i=0; i<var.getRelations().length; i++) {
			if(var.getRelations()[i] == null || var.getRelations()[i].isEmpty()) continue;
			
			for(DepVariable child: var.getRelations()[i]) {
				DepVariableWrapper childWrapper = DepVariableWrapperFactory.createWrapperInstance(child);
				if (childWrapper.equals(node)) {
					return i;
				}				
			}
			
		}
		
		return -1;
	}
	
	public String toString() {
		return var.toString();
	}

	public List<DepVariableWrapper> getCallerNode() {
		if(this.parents.isEmpty()){
			return null;
		}
		
		if(this.var.isMethodCall()){
			for(DepVariableWrapper par: this.parents){
				if(par.isSupportOperandFor(this.var, 0)){
					List<DepVariableWrapper> list = new ArrayList<>();
					list.add(par);
					return list;
				}
			}
		}
		else{
			return this.parents;
		}
		
		return null;
	}

	private boolean isSupportOperandFor(DepVariable var2, int index) {
		return this.var.isSupportOperandFor(var2, index);
	}

	public String getShortName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(var.getShortMethodName() + "#" + var.getInstruction().getInstructionId());
		return buffer.toString();
	}

	public DepVariableWrapper getFirstParent() {
		for(DepVariableWrapper par: this.parents) {
			int order = this.var.getInputOrder(par.var);
			if(order == 0) {
				return par;
			}
		}
		return null;
	}

	public DepVariableWrapper findTaintedParent() {
		
		DepVariableWrapper firstPar = this.getFirstParent(); 
		if(firstPar == null) return null;
		
		while(!firstPar.processed) {
			firstPar = firstPar.getFirstParent();
			
			if(firstPar == null) return null;
		}
		
		return firstPar;
	}
	
}
