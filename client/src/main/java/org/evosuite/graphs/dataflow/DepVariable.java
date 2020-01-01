package org.evosuite.graphs.dataflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

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
	
	private int type;
	
	public static int PARAMETER = 0;
	public static int STATIC_FIELD = 1;
	public static int INSTANCE_FIELD = 2;
	public static int THIS = 3;
	public static int OTHER = 4;
	
	private Map<String, List<DepVariable>> relations = new HashMap<String, List<DepVariable>>();
	private Map<String, List<DepVariable>> reverseRelations = new HashMap<String, List<DepVariable>>();
	
	public DepVariable(String className, String varName, int type, BytecodeInstruction insn) {
		this.className = className;
		this.varName = varName;
		this.setType(type);
		this.setInstruction(insn);
	}
	
	
	@Override
	public String toString() {
		return "DepVariable [className=" + getClassName()  + ", type=" + getTypeString() 
				+ ", varName=" + varName + ", instruction=" + getInstruction() + "]";
	}

	public String getTypeString() {
		if(type == DepVariable.PARAMETER) {
			return "parameter";
		}
		else if(type == DepVariable.INSTANCE_FIELD) {
			return "instance field";
		}
		else if(type == DepVariable.STATIC_FIELD) {
			return "static field";
		}
		else if(type == DepVariable.THIS) {
			return "this";
		}
		
		return "other";
	}

	public BytecodeInstruction getInstruction() {
		return instruction;
	}


	public void setInstruction(BytecodeInstruction instruction) {
		this.instruction = instruction;
	}


	public boolean isParameter() {
		if(this.instruction.isLocalVariableUse()) {
			String methodName = this.instruction.getRawCFG().getMethodName();
			String methodDesc = methodName.substring(methodName.indexOf("("), methodName.length());
			Type[] typeArgs1 = Type.getArgumentTypes(methodDesc);
			int paramNum = typeArgs1.length;
			
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


	public boolean referenceToThis() {
		return this.instruction.loadsReferenceToThis();
	}


	public void addRelation(String relation, DepVariable var) {
		List<DepVariable> list = this.getRelations().get(relation);
		if(list == null) {
			list = new ArrayList<DepVariable>();
		}
		
		if(!list.contains(var)) {
			list.add(var);
		}
		
		this.getRelations().put(relation, list);
		var.addReverseRelation(relation, this);
	}
	
	private void addReverseRelation(String relation, DepVariable var) {
		List<DepVariable> list = this.reverseRelations.get(relation);
		if(list == null) {
			list = new ArrayList<DepVariable>();
		}
		
		if(!list.contains(var)) {
			list.add(var);
		}
		
		this.reverseRelations.put(relation, list);
	}

	
	public boolean equals(Object obj) {
		if(obj instanceof DepVariable) {
			DepVariable var = (DepVariable)obj;
			return var.getInstruction().equals(this.getInstruction());
		}
		
		return false;
	}


	public Map<String, List<DepVariable>> getRelations() {
		return relations;
	}


	public void setRelations(Map<String, List<DepVariable>> relations) {
		this.relations = relations;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getClassName() {
		return className;
	}


	public DepVariable getRootVar() {
		List<DepVariable> parents = this.reverseRelations.get(Relation.FIELD);
		while(parents != null && !parents.isEmpty()) {
			for(DepVariable parent: parents) {
				if(parent.getType() == DepVariable.THIS) {
					return parent;
				}
				else if(parent.getType() == DepVariable.PARAMETER) {
					return parent;
				}
				else {
					parents = parent.reverseRelations.get(Relation.FIELD);
				}
				
				break;
			}
		}
		
		return null;
	}


	public void setName(String name) {
		this.varName = name;
		
	}

}
