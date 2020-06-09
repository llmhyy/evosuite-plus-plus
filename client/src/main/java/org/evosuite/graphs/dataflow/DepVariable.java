package org.evosuite.graphs.dataflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;
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
	public static final int ARRAY_ELEMENT = 5;
	public static final int OTHER = 6;
	
	public static int OPERAND_NUM_LIMIT = 30;
	
	/**
	 * storing children
	 * given a child, this variable is its i-th operand (assume that no instruction takes over OPERAND_NUM_LIMIT operands).
	 */
	@SuppressWarnings("unchecked")
	private List<DepVariable>[] relations = new ArrayList[OPERAND_NUM_LIMIT];
	
	/**
	 * storing parents
	 * given a parent, this variable takes it its i-th operand (assume that no instruction takes over OPERAND_NUM_LIMIT operands).
	 */
	@SuppressWarnings("unchecked")
	private List<DepVariable>[] reverseRelations = new ArrayList[OPERAND_NUM_LIMIT];
	
	public DepVariable(String className, BytecodeInstruction insn) {
		this.className = className;
		this.setInstruction(insn);
		this.setType();
	}
	
	public String inferRelationWithChild(DepVariable childVar, int operandPosition) {
		BytecodeInstruction instruction = childVar.getInstruction();
		AbstractInsnNode insNode = instruction.getASMNode();
		
		int type = insNode.getType();
		String relation = null;
		if(type == AbstractInsnNode.FIELD_INSN) {
			relation = Relation.FIELD;
		}
		else {
			if(instruction.getASMNode().getOpcode() == Opcodes.AALOAD && operandPosition == 0) {
				relation = Relation.ARRAY_ELEMENT;
			}
			else {
				relation = Relation.OTHER;				
			}
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
	
	public boolean isParameter(){
		return this.instruction.isParameter();
	}
	
	public boolean isMethodCall(){
		return this.instruction.isMethodCall();
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
		else if(this.isLoadArrayElement()) {
			this.setType(DepVariable.ARRAY_ELEMENT);
			this.setName("array index");
		}
		else {
			this.setType(DepVariable.OTHER);
			this.setName("$unknown");
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
		else if(type == DepVariable.ARRAY_ELEMENT) {
			return "array element";
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

	public boolean isStaticField() {
		return this.instruction.getASMNode().getOpcode() == Opcodes.GETSTATIC;
	}

	public boolean isInstaceField() {
		return this.instruction.getASMNode().getOpcode() == Opcodes.GETFIELD;
	}
	
	public boolean isLoadArrayElement() {
		return this.instruction.getASMNode().getOpcode() == Opcodes.AALOAD;
	}


	public boolean referenceToThis() {
		return this.instruction.loadsReferenceToThis();
	}

	
	public void buildRelation(DepVariable outputVar, int position) {
//		if(outputVar.getInstruction().getInstructionId()==3 && outputVar.getInstruction().getLineNumber()==41) {
//			System.currentTimeMillis();
//		}
		
		List<DepVariable> list = this.getRelations()[position];
		if(list == null) {
			list = new ArrayList<DepVariable>();
		}
		
		if(!list.contains(outputVar)) {
			list.add(outputVar);
		}
		
		this.getRelations()[position] = list;
		
		if(this.getInstruction().getInstructionId()==14 && this.varName.equals("checkRules(Lstate/Action;Lstate/GameState;)Z_LV_1")) {
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

	@Override
	public int hashCode() {
		return type;
	}
	
	public String getUniqueLabel() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTypeString() + "\n");
		
		if(this.getType() == DepVariable.INSTANCE_FIELD || this.getType() == DepVariable.STATIC_FIELD) {
			FieldInsnNode thisField = (FieldInsnNode)(this.getInstruction().getASMNode());
			buffer.append(thisField.owner + "\n");
			buffer.append(thisField.desc + "\n");
			buffer.append(thisField.name + "\n");
			
		}
		else if(this.getType() == DepVariable.PARAMETER) {
			buffer.append(this.getInstruction().getClassName() + "\n");
			buffer.append(this.getInstruction().getMethodName() + "\n");
			buffer.append(this.getParamOrder() + "\n");
		}
		else if(this.getType() == DepVariable.THIS) {
			buffer.append(this.getInstruction().getClassName() + "\n");
		}
		else if(this.getType() == DepVariable.ARRAY_ELEMENT) {
			buffer.append(this.getInstruction() + "\n");
		}
		else if(this.getType() == DepVariable.OTHER) {
			buffer.append(this.getInstruction() + "\n");
		}
		
		return buffer.toString();
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
						FieldInsnNode thatField = (FieldInsnNode)(var.getInstruction().getASMNode());
						FieldInsnNode thisField = (FieldInsnNode)(this.getInstruction().getASMNode());
						return thisField.desc.equals(thatField.desc) &&
								thisField.owner.equals(thatField.owner) &&
								thisField.name.equals(thatField.name);
					}
					else if(var.getType() == DepVariable.STATIC_FIELD) {
						FieldInsnNode thatField = (FieldInsnNode)(var.getInstruction().getASMNode());
						FieldInsnNode thisField = (FieldInsnNode)(this.getInstruction().getASMNode());
						return thisField.desc.equals(thatField.desc) &&
								thisField.owner.equals(thatField.owner) &&
								thisField.name.equals(thatField.name);
					}
					else if(var.getType() == DepVariable.PARAMETER) {
						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName()) &&
								var.getInstruction().getMethodName().equals(this.getInstruction().getMethodName()) &&
								var.getParamOrder() == this.getParamOrder();
					}
					else if(var.getType() == DepVariable.THIS) {
						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName());
					}
					else if(var.getType() == DepVariable.ARRAY_ELEMENT) {
						//FIXME ziheng
					}
					else if(var.getType() == DepVariable.OTHER) {
						//FIXME ziheng
						/**
						 * we need to come up with how to define the equivalence of two instruction like method call, etc.
						 */
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


	@SuppressWarnings("unchecked")
	private void getRootVar(Map<DepVariable, ArrayList<ConstructionPath>> roots, DepVariable parent, DepVariable child,
			Map<DepVariable, List<DepVariable>> visited, ArrayList<DepVariable> partialPath, ArrayList<Integer> operandPosList) {
		List<DepVariable> visitedDirections = visited.get(parent);
		if(visitedDirections == null) {
			visitedDirections = new ArrayList<DepVariable>();
		}
		
		if(visitedDirections.contains(child)) {
			return;
		}
		
		visitedDirections.add(child);
		visited.put(parent, visitedDirections);
		
		if(parent.getType() == DepVariable.THIS ||
				parent.getType() == DepVariable.PARAMETER ||
				parent.getType() == DepVariable.STATIC_FIELD) {
			ArrayList<ConstructionPath> pathList = roots.get(parent);
			if(pathList == null) {
				pathList = new ArrayList<ConstructionPath>();
			}
			
			ConstructionPath path = new ConstructionPath(partialPath, operandPosList);
			if(!pathList.contains(path)) {
				pathList.add(path);				
				roots.put(parent, pathList);
			}
			
		}
		else {
			for(int i=0; i<parent.reverseRelations.length; i++) {
				
				if(parent.reverseRelations[i]==null) continue;
				
				for(DepVariable grandPar: parent.reverseRelations[i]) {
					ArrayList<DepVariable> newParialPath = (ArrayList<DepVariable>) partialPath.clone();
					ArrayList<Integer> newOperandPosList = (ArrayList<Integer>) operandPosList.clone();
					
					newParialPath.add(grandPar);
					newOperandPosList.add(i);
					getRootVar(roots, grandPar, parent, visited, newParialPath, newOperandPosList);		
				}
			}
		}
	}

	/**
	 * return a root along with the path from root variable to the variable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<DepVariable, ArrayList<ConstructionPath>> getRootVars() {
		Map<DepVariable, ArrayList<ConstructionPath>> roots = new HashMap<>();
		/**
		 * keep a map to avoid circle in the graph, <a node, its child>
		 */
		Map<DepVariable, List<DepVariable>> visited = new HashMap<>();
		
		ArrayList<DepVariable> path = new ArrayList<DepVariable>();
		path.add(this);
		
		for(int i=0; i<this.reverseRelations.length; i++) {
			List<DepVariable> directParents = this.reverseRelations[i];
			if(directParents != null && !directParents.isEmpty()) {
				DepVariable p = directParents.get(0);
				String relation = p.inferRelationWithChild(this, i);
				if(relation.equals(Relation.FIELD) || relation.equals(Relation.ARRAY_ELEMENT)) {
					for (DepVariable parent : directParents) {
						ArrayList<DepVariable> partialPath = (ArrayList<DepVariable>) path.clone();
						partialPath.add(parent);
						
						ArrayList<Integer> operandPosList = new ArrayList<Integer>();
						operandPosList.add(i);
						
						getRootVar(roots, parent, this, visited, partialPath, operandPosList);
					}
				}
			}
		}
		
		if(roots.isEmpty()) {
			ArrayList<ConstructionPath> list = new ArrayList<>();
			ArrayList<DepVariable> partialPath = new ArrayList<>();
			partialPath.add(this);
			ConstructionPath p = new ConstructionPath(partialPath, new ArrayList<>());
			list.add(p);
			roots.put(this, list);
		}
		
		return roots;
	}

	public void setName(String name) {
		this.varName = name;
	}

	public String getName(){
		return this.varName;
	}

	public int getParamOrder() {
		if(varName.contains("LV_")) {
			String orderString = varName.substring(varName.indexOf("LV_")+3, varName.length());
			int order = Integer.valueOf(orderString);
			
			if(this.instruction.getActualCFG().isStaticMethod()) {
				return order + 1;
			}
			else {
				return order;
			}
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
		for(int i=0; i<lastNode.getRelations().length; i++) {
			List<DepVariable> children = lastNode.getRelations()[i];
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

	public List<DepVariable>[] getRelations() {
		return relations;
	}
	
	public List<DepVariable>[] getReverseRelations() {
		return reverseRelations;
	}

	public int findRelationPosition(DepVariable var) {
		for(int i=0; i<relations.length; i++) {
			if(this.relations[i] == null || this.relations[i].isEmpty()) continue;
			
			for(DepVariable child: this.relations[i]) {
				if (child.equals(var)) {
					return i;
				}				
			}
			
		}
		
		return -1;
	}

	/**
	 * check whether this variable will be used as an index-th operand for a given node childVar.
	 * @param childVar
	 * @param index
	 * @return
	 */
	public boolean isSupportOperandFor(DepVariable childVar, int index) {
		List<DepVariable> list = this.relations[index];
		
		if(list == null){
			return false;
		}
		
		for(DepVariable v: list){
			if(v.equals(childVar)){
				return true;
			}
		}
		
		return false;
	}

}
