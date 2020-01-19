package org.evosuite.graphs.dataflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

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
	
	public static final int PARAMETER = 1;
	public static final int STATIC_FIELD = 2;
	public static final int INSTANCE_FIELD = 3;
	public static final int THIS = 4;
	public static final int OTHER = 5;
	
	private Map<String, List<DepVariable>> relations = new HashMap<String, List<DepVariable>>();
	private Map<String, List<DepVariable>> reverseRelations = new HashMap<String, List<DepVariable>>();
	
	public DepVariable(String className, String varName, BytecodeInstruction insn) {
		this.className = className;
		this.varName = varName;
		this.setInstruction(insn);
		this.setType();
	}
	
	
	@Override
	public String toString() {
		return "DepVariable [className=" + getClassName()  + ", type=" + getTypeString() 
				+ ", varName=" + varName + ", instruction=" + getInstruction() + "]";
	}
	
	private void setType(int type) {
		this.type = type;
	}
	
	private void setType() {
		BytecodeInstruction ins = this.getInstruction();
		if(this.referenceToThis()) {
			this.setType(DepVariable.THIS);
			this.setName("this");
		}
		else if(this.isParameter()) {
			this.setType(DepVariable.PARAMETER);
			this.setName(ins.getVariableName());
		}
		else if(this.isStaticField()) {
			this.setType(DepVariable.STATIC_FIELD);
			this.setName(((FieldInsnNode)ins.getASMNode()).name);
		}
		else if(this.isInstaceField()) {
			this.setType(DepVariable.INSTANCE_FIELD);
			this.setName(((FieldInsnNode)ins.getASMNode()).name);
		}
		else {
			this.setType(DepVariable.OTHER);
			this.setName("$unknwon");
		}
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
		else {
			return "other";			
		}
		
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
			Type[] typeArgs = Type.getArgumentTypes(methodDesc);
			int paramNum = typeArgs.length;
			
			int slot = this.instruction.getLocalVariableSlot();
			
			if(this.instruction.getRawCFG().isStaticMethod()) {
				return slot < paramNum;
			}
			else {
				return slot < paramNum+1 && slot != 0;				
			}
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

	
	public void buildRelation(DepVariable outputVar) {
		BytecodeInstruction instruction = outputVar.getInstruction();
		AbstractInsnNode insNode = instruction.getASMNode();
		
		int type = insNode.getType();
		String relation = null;
		if(type == AbstractInsnNode.FIELD_INSN) {
			relation = Relation.FIELD;
		}
		else {
			relation = Relation.OTHER;
		}
		
		if(outputVar.getInstruction().getInstructionId()==3 && outputVar.getInstruction().getLineNumber()==41) {
			System.currentTimeMillis();
		}
		
		addRelation(relation, outputVar);
	}

	private void addRelation(String relation, DepVariable var) {
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
			if(var.getInstruction().equals(this.getInstruction())) {
				return true;
			}
			else {
				if(var.getType() == this.getType()) {
					if(var.getType() == DepVariable.INSTANCE_FIELD) {
						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName()) &&
								var.getInstruction().getName().equals(this.getInstruction().getName()) &&
								var.getInstruction().getClassName().equals(Properties.TARGET_CLASS) &&
								var.getInstruction().getMethodName().equals(Properties.TARGET_METHOD);
					}
					else if(var.getType() == DepVariable.STATIC_FIELD) {
						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName()) &&
								var.getInstruction().getName().equals(this.getInstruction().getName());
					}
					else if(var.getType() == DepVariable.PARAMETER) {
						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName()) &&
								var.getInstruction().getMethodName().equals(this.getInstruction().getMethodName()) &&
								var.getParamOrder() == this.getParamOrder();
					}
					else if(var.getType() == DepVariable.THIS) {
						
					}
				}
				
				return false;
			}
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


	public String getClassName() {
		return className;
	}


	private void getRootVar(List<DepVariable> roots, DepVariable parent, Set<DepVariable> visited) {
		if(visited.contains(parent)) {
			return;
		}
		visited.add(parent);
		
		
		if(parent.getType() == DepVariable.THIS) {
			roots.add(parent);
		}
		else if(parent.getType() == DepVariable.PARAMETER) {
			roots.add(parent);
		}
		else {
			List<DepVariable> parents = parent.getAllParents();
			if(parents != null && !parents.isEmpty()) {
				for(DepVariable par: parents) {
					getRootVar(roots, par, visited);					
				}
			}
		}
		
		
	}

	private List<DepVariable> getAllParents() {
		List<DepVariable> parents = new ArrayList<DepVariable>();
		for(String relation: this.reverseRelations.keySet()) {
			List<DepVariable> pars = this.reverseRelations.get(relation);
			parents.addAll(pars);
		}
		return parents;
	}

	private List<DepVariable> roots;

	public List<DepVariable> getRootVars() {
		
		List<DepVariable> directParents = this.reverseRelations.get(Relation.FIELD);
		
		List<DepVariable> roots = new ArrayList<DepVariable>();
		Set<DepVariable> visited = new HashSet<DepVariable>();
		for(DepVariable parent: directParents) {
			getRootVar(roots, parent, visited);
		}
//		this.roots = roots;
//		if(this.roots == null) {
//		}
		
		return roots;
	}

	public void setName(String name) {
		this.varName = name;
		
	}


	public int getParamOrder() {
		if(varName.contains("LV_")) {
			String order = varName.substring(varName.indexOf("LV_")+3, varName.length());
			return Integer.valueOf(order);
		}
		
		return -1;
	}


	/**
	 * find the path from this variable to the given variable
	 * return null if such a path does not exist
	 * 
	 * @param var
	 * @return
	 */
	public ConstructionPath findPath(DepVariable var) {
		ArrayList<DepVariable> path = new ArrayList<DepVariable>();
		path.add(this);
		
		List<DepVariable> linkPath = findPath(path, var);
		
		if(linkPath == null)
			return null;
		
		return new ConstructionPath(linkPath);
	}


	@SuppressWarnings("unchecked")
	private ArrayList<DepVariable> findPath(ArrayList<DepVariable> path, DepVariable var) {
		DepVariable lastNode = path.get(path.size()-1);
		
		ArrayList<DepVariable> foundPath = null;
		for(String relation: lastNode.getRelations().keySet()) {
			List<DepVariable> children = this.getRelations().get(relation);
			for(DepVariable child: children) {
				if(path.contains(child)) continue;
				
				if(child.equals(var)) {
					path.add(child);
					foundPath = path;
					break;
				}
				else {
					ArrayList<DepVariable> clonePath = (ArrayList<DepVariable>) path.clone();
					clonePath.add(child);
					
					ArrayList<DepVariable> p = findPath(clonePath, var);
					if(p != null) {
						foundPath = p;
						break;
					}
				}
			}
			
			if(foundPath != null) break;
		}
		
		return foundPath;
	}

}
