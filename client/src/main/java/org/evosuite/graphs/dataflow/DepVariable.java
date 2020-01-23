package org.evosuite.graphs.dataflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	
	private String recommendedImplementation;
	
	private int type;
	
	public static final int PARAMETER = 1;
	public static final int STATIC_FIELD = 2;
	public static final int INSTANCE_FIELD = 3;
	public static final int THIS = 4;
	public static final int OTHER = 5;
	
	public static int OPERAND_NUM_LIMIT = 30;
	
	/**
	 * this variable take a child in the ith operand (assume that no instruction takes over 30 operands)
	 */
	@SuppressWarnings("unchecked")
	private List<DepVariable>[] relations = new ArrayList[OPERAND_NUM_LIMIT];
	
	/**
	 * this variable is used for the ith position of the parent (assume that no instruction takes over 30 operands).
	 */
	@SuppressWarnings("unchecked")
	private List<DepVariable>[] reverseRelations = new ArrayList[OPERAND_NUM_LIMIT];
	
	public DepVariable(String className, String varName, BytecodeInstruction insn) {
		this.className = className;
		this.varName = varName;
		this.setInstruction(insn);
		this.setType();
	}
	
	public String inferRelation(DepVariable var) {
		BytecodeInstruction instruction = var.getInstruction();
		AbstractInsnNode insNode = instruction.getASMNode();
		
		int type = insNode.getType();
		String relation = null;
		if(type == AbstractInsnNode.FIELD_INSN) {
			relation = Relation.FIELD;
		}
		else {
			relation = Relation.OTHER;
		}
		
		return relation;
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

	
	public void buildRelation(DepVariable outputVar, int position) {
//		if(outputVar.getInstruction().getInstructionId()==3 && outputVar.getInstruction().getLineNumber()==41) {
//			System.currentTimeMillis();
//		}
		
		List<DepVariable> list = this.relations[position];
		if(list == null) {
			list = new ArrayList<DepVariable>();
		}
		
		if(!list.contains(outputVar)) {
			list.add(outputVar);
		}
		
		this.relations[position] = list;
		
		if(this.getInstruction().getInstructionId()==20 && outputVar.getInstruction().getInstructionId()==25) {
			System.currentTimeMillis();
		}
		
		outputVar.addReverseRelation(this, position);
	}

	
	private void addReverseRelation(DepVariable inputVar, int position) {
		List<DepVariable> list = this.reverseRelations[position];
		if(list == null) {
			list = new ArrayList<DepVariable>();
		}
		
		if(!list.contains(inputVar)) {
			list.add(inputVar);
		}
		
		this.reverseRelations[position] = list;
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
		for(int i=0; i<this.reverseRelations.length; i++) {
			List<DepVariable> pars = this.reverseRelations[0];
			if(pars != null) {
				parents.addAll(pars);				
			}
		}
		return parents;
	}

	public List<DepVariable> getRootVars() {
		List<DepVariable> roots = new ArrayList<DepVariable>();
		Set<DepVariable> visited = new HashSet<DepVariable>();
		
		for(int i=0; i<this.reverseRelations.length; i++) {
			List<DepVariable> directParents = this.reverseRelations[i];
			if(directParents != null && !directParents.isEmpty()) {
				DepVariable p = directParents.get(0);
				String relation = p.inferRelation(this);
				if(relation.equals(Relation.FIELD)) {
					for (DepVariable parent : directParents) {
						getRootVar(roots, parent, visited);
					}
				}
			}
		}
		
		if(roots.isEmpty()) {
			roots.add(this);
		}
		
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
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		path.add(this);
		
		ConstructionPath constructionPath = findPath(path, positions, var);
		return constructionPath;
	}


	@SuppressWarnings("unchecked")
	private ConstructionPath findPath(ArrayList<DepVariable> path, ArrayList<Integer> positions, DepVariable var) {
		DepVariable lastNode = path.get(path.size()-1);
		
		if(lastNode.equals(var)) {
			return new ConstructionPath(path, positions);
		}
		
		ConstructionPath foundPath = null;
		for(int i=0; i<lastNode.relations.length; i++) {
			List<DepVariable> children = lastNode.relations[i];
			if (children != null) {
				for(DepVariable child: children) {
					if(path.contains(child)) continue;
					
					if(child.equals(var)) {
						path.add(child);
						positions.add(i);
						foundPath = new ConstructionPath(path, positions);
						break;
					}
					else {
						ArrayList<DepVariable> clonePath = (ArrayList<DepVariable>) path.clone();
						ArrayList<Integer> clonePositions = (ArrayList<Integer>) positions.clone();
						clonePath.add(child);
						clonePositions.add(i);
						
						ConstructionPath p = findPath(clonePath, clonePositions, var);
						if(p != null) {
							foundPath = p;
							break;
						}
					}
				}
				
				if(foundPath != null) break;
			}
		}
		
		return foundPath;
	}

	public String getRecommendedImplementation() {
		return recommendedImplementation;
	}

	public void setRecommendedImplementation(String recommendedImplementation) {
		this.recommendedImplementation = recommendedImplementation;
	}

}
