package org.evosuite.graphs.interprocedural.var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ConstructionPath;
import org.evosuite.graphs.interprocedural.Relation;
import org.evosuite.seeding.smart.BranchSeedInfo;
import org.evosuite.testcase.TestCase;
import org.evosuite.utils.MethodUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.bytecode.Opcode;

/**
 * This class should detailedly describe the dependent variables, including whether it is
 * a parameter, field, global variables, etc.
 * @author linyun
 *
 */
public abstract class DepVariable {
	
	private static final Logger logger = LoggerFactory.getLogger(DepVariable.class);
	
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
	
	protected DepVariable(BytecodeInstruction ins) {
		this.className = ins.getClassName();
		this.setInstruction(ins);
	}
	
	
	public boolean isMethodInput() {
		if(this.isParameter() && 
				this.getInstruction().getMethodName().equals(Properties.TARGET_METHOD) &&
				this.getClassName().equals(Properties.TARGET_CLASS)) {
			return true;
		}
		
		if(this.isStaticField()) {
			return true;
		}
		
		if(this.isInstaceField() && this.getClassName().equals(Properties.TARGET_CLASS)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isConstant() {
		return this.instruction.isConstant();
	}
	
	public boolean isStateVariable() {
		return this.isStaticField() || this.isInstaceField() || this.isLoadArrayElement();
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
			if(instruction.isArrayLoadInstruction() && operandPosition == 0) {
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
		return "DepVariable [\nclassName=" + getClassName()  + "\ntype=" + getTypeString() 
				+ "\nvarName=" + varName + "\ninstruction=" + getInstruction() + "]";
	}
	
	protected void setType(int type) {
		this.type = type;
	}
	
	public boolean isParameter(){
		return this.instruction.isParameter();
	}
	
	public boolean isMethodCall(){
		return this.instruction.isMethodCall();
	}
	
	public String getTypeString() {
		if(type == DepVariable.PARAMETER) {
			return "PARAMETER";
		}
		else if(type == DepVariable.INSTANCE_FIELD) {
			return "FIELD_i";
		}
		else if(type == DepVariable.STATIC_FIELD) {
			return "FIELD_S";
		}
		else if(type == DepVariable.THIS) {
			return "THIS";
		}
		else if(type == DepVariable.ARRAY_ELEMENT) {
			return "ARRAY_ELE";
		}
		else {
			return "OTHER";			
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
		return this.instruction.isArrayLoadInstruction();
//		return this.instruction.getASMNode().getOpcode() == Opcodes.AALOAD;
	}
	
	public boolean hasNoParent() {
		for(int i=0; i<this.reverseRelations.length; i++) {
			if(this.reverseRelations[i] != null) {
				return false;
			}
		}
		
		return true;
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
	
	public String getShortLabel() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTypeString() + "\n");
		String className = this.getInstruction().getClassName();
		String owner = className.substring(className.lastIndexOf("."), className.length());
		buffer.append("OWNER: " + owner + "\n");
		
		if(this.getType() == DepVariable.INSTANCE_FIELD || this.getType() == DepVariable.STATIC_FIELD) {
			FieldInsnNode thisField = (FieldInsnNode)(this.getInstruction().getASMNode());
			String desc = thisField.desc;
			if(thisField.desc.contains(";")) {
				desc = thisField.desc.substring(thisField.desc.lastIndexOf("/"), thisField.desc.length());
			}
			buffer.append("TYPE_f: " + desc + "\n");
			buffer.append("NAME: " + thisField.name + "\n");
			
		}
		else if(this.getType() == DepVariable.PARAMETER) {
			String shortMethodName = extractShortMethodName(this.getInstruction().getMethodName());
			buffer.append("METHOD: " + shortMethodName + "\n");
			buffer.append("ORDER: " + this.getParamOrder() + "\n");
		}
		else if(this.getType() == DepVariable.THIS) {
//			String className = this.getInstruction().getClassName();
//			String owner = className.substring(className.lastIndexOf("."), className.length());
//			buffer.append("OWNER: " + owner + "\n");
		}
		else if(this.getType() == DepVariable.ARRAY_ELEMENT) {
			buffer.append(this.getInstruction() + "\n");
		}
		else if(this.getType() == DepVariable.OTHER) {
			if(this.getInstruction().checkInstanceOf()) {
//				String className = this.getInstruction().getInstanceOfCheckingType();
//				String owner = className.substring(className.lastIndexOf("."), className.length());
//				buffer.append("OWNER: " + owner + "\n");
			}
			else if(this.getInstruction().toString().contains("CHECKCAST") 
					&& this.getInstruction().getASMNode() instanceof TypeInsnNode) {
				TypeInsnNode n = (TypeInsnNode)this.getInstruction().getASMNode();
				String desc = n.desc;
				desc = desc.substring(desc.lastIndexOf("/"), desc.length());
				buffer.append("checkcast " + desc + "\n");
			}
			else if(this.getInstruction().isMethodCall()) {
				String methodName = this.instruction.getCalledMethod();
				String shortMethodName = extractShortMethodName(methodName);
				buffer.append("invoke " + shortMethodName + "\n");
			}
			else {
				buffer.append(this.getInstruction() + "\n");				
			}
		}
		
		buffer.append("ID: " + this.getInstruction().getInstructionId() + "\n");
		buffer.append("LINE:" + this.getInstruction().getLineNumber() + "\n");
		
		return buffer.toString();
	}

	private String extractShortMethodName(String methodName) {
		String returnType = methodName.substring(methodName.indexOf(")")+1, methodName.length());
		if(returnType.contains("/")) {
			returnType = returnType.substring(returnType.lastIndexOf("/")+1, returnType.length());
		}
		String name = methodName.substring(0, methodName.indexOf("("));
		String shortMethodName = name + "()" + returnType;
		return shortMethodName;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof DepVariable) {
			DepVariable var = (DepVariable)obj;
			if(var.getInstruction().equals(this.getInstruction())) {
				return true;
			}
//			else {
//				if(var.getType() == this.getType()) {
//					if(var.getType() == DepVariable.INSTANCE_FIELD) {
//						FieldInsnNode thatField = (FieldInsnNode)(var.getInstruction().getASMNode());
//						FieldInsnNode thisField = (FieldInsnNode)(this.getInstruction().getASMNode());
//						return thisField.desc.equals(thatField.desc) &&
//								thisField.owner.equals(thatField.owner) &&
//								thisField.name.equals(thatField.name);
//					}
//					else if(var.getType() == DepVariable.STATIC_FIELD) {
//						FieldInsnNode thatField = (FieldInsnNode)(var.getInstruction().getASMNode());
//						FieldInsnNode thisField = (FieldInsnNode)(this.getInstruction().getASMNode());
//						return thisField.desc.equals(thatField.desc) &&
//								thisField.owner.equals(thatField.owner) &&
//								thisField.name.equals(thatField.name);
//					}
//					else if(var.getType() == DepVariable.PARAMETER) {
//						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName()) &&
//								var.getInstruction().getMethodName().equals(this.getInstruction().getMethodName()) &&
//								var.getParamOrder() == this.getParamOrder();
//					}
//					else if(var.getType() == DepVariable.THIS) {
//						return var.getInstruction().getClassName().equals(this.getInstruction().getClassName());
//					}
//					else if(var.getType() == DepVariable.ARRAY_ELEMENT) {
//						//FIXME ziheng
//					}
//					else if(var.getType() == DepVariable.OTHER) {
//						//FIXME ziheng
//						/**
//						 * we need to come up with how to define the equivalence of two instruction like method call, etc.
//						 */
//					}
//				}
//				
//				return false;
//			}
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
		System.currentTimeMillis();
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

	/**
	 * return the exact order of the parameter regarding static method, starting at 1
	 * @return
	 */
	public int getParamOrder() {
		if(this.instruction.isParameter()) {
//			String orderString = varName.substring(varName.indexOf("LV_")+3, varName.length());
			int order = this.instruction.getParameterPosition() + 1;
			return order;
			
//			/**
//			 * e.g., iload_0 in static method should be changed to the order of 1
//			 */
//			if(this.instruction.getActualCFG().isStaticMethod()) {
//				return order + 1;
//			}
//			/**
//			 * here, iload_0 will point to this, so here the order must be larger than 0, 
//			 * hence, there is no need to change the order
//			 */
//			else {
//				return order;
//			}
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

	public String getMethodName() {
		return this.getClassName() + "#" + this.getInstruction().getMethodName();
	}
	
	public String getShortMethodName() {
		String clazz = this.getClassName();
		String shortClass = clazz.substring(clazz.lastIndexOf(".")+1, clazz.length());
		String method = this.getInstruction().getMethodName();
		String shortMethod = method.substring(0, method.indexOf("("));
		
		return shortClass + "#" + shortMethod;
	}

	public int getInputOrder(DepVariable prevNode) {
		for(int i=0; i<this.reverseRelations.length; i++) {
			List<DepVariable> list = this.reverseRelations[i];
			if(list != null) {
				for(DepVariable node: list) {
					if(node.getInstruction().equals(prevNode.getInstruction())) {
						return i;
					}
				}
			}
			
		}
		
		return -1;
	}


	public String getDataType() {
		if(this.referenceToThis()) {
			return MethodUtil.convertType(this.className);
		}
		else if(this.isParameter()) {
			String methodSig = this.instruction.getMethodName();
			String[] splitted = MethodUtil.parseSignature(methodSig);
			int order = this.getParamOrder();
			if(order != -1) {
				String parameterType = splitted[order-1];
				return parameterType;
			}
			else {
				return BranchSeedInfo.OTHER;
			}
		}
		else if(this.isStaticField()) {
			return MethodUtil.convertType(this.instruction.getFieldType());
		}
		else if(this.isInstaceField()) {
			return MethodUtil.convertType(this.instruction.getFieldType());
		}
		else if(this.isLoadArrayElement()) {
			return BranchSeedInfo.OTHER;
		}
		else if(this.isMethodCall()){
			if(this.getInstruction().getCalledMethodsClass().contains("StringHelper")) {
				return "java.lang.String";
			}
			
			if(this.getInstruction().getCalledMethodsClass().contains("BooleanHelper") ||
					this.getInstruction().getCalledMethodsClass().contains("ContainerHelper")) {
				return "java.lang.Object";
			}
			
			String methodSig = this.getInstruction().getCalledMethod();
			String[] splitted = MethodUtil.parseSignature(methodSig);
			
			return splitted[splitted.length-1];
		}
		else {
			int slotNum = this.getInstruction().getLocalVariableSlot();
			if(slotNum != -1) {
				String methodSig = this.instruction.getMethodName();
				String[] splitted = MethodUtil.parseSignature(methodSig);
				int paramNum = splitted.length - 1;
				if(slotNum < paramNum) {
					return splitted[slotNum];
				}
				else {
					BytecodeInstruction ins = extractedDefinition(slotNum);
					if(ins != null) {
						List<BytecodeInstruction> sourceInsList = ins.getSourceOfStackInstructionList(0);
						if(sourceInsList != null && !sourceInsList.isEmpty()) {
							BytecodeInstruction sourceIns = sourceInsList.get(0);
							DepVariable node = DepVariableFactory.createVariableInstance(sourceIns);
//							DepVariable node = new DepVariable(sourceIns);
							String type = node.getDataType();
							return type;
						}
					}
				}
			}
			
			String type = inferType(this.getInstruction().getInstructionType());
			return type;
			
		}
	}

	private BytecodeInstruction extractedDefinition(int slotNum) {
		BytecodeInstruction ins = this.getInstruction();
		while(ins != null) {
			if(ins.explain().toLowerCase().contains("store")) {
				int storeSlot = ins.getLocalVariableSlot();
				if(storeSlot == slotNum) {
					return ins;
				}
			}
			ins = ins.getPreviousInstruction();
		}
		return null;
	}


	private String inferType(String instructionType) {
		switch(instructionType) {
		case "ALOAD":
		case "ASTORE":
		case "AALOAD":
		case "AASTORE":
		case "ACONST_NULL":
		case "ALOAD_0":
		case "ALOAD_1":
		case "ALOAD_2":
		case "ALOAD_3":
		case "ANEWARRAY":
		case "ARETURN":
		case "ARRAYLENGTH":
		case "ASTORE_0":
		case "ASTORE_1":
		case "ASTORE_2":
		case "ASTORE_3":
		case "BREAKPOINT":
		case "ATHROW":
			return BranchSeedInfo.OTHER;
		case "BLOAD":
		case "BSTORE":
			return "byte";
		case "BIPUSH":
			return "int";
		case "CALOAD":
			return "char";
		case "CASTORE":
			return "char";
		case "CHECKCAST":
			AbstractInsnNode node = this.instruction.getASMNode();
			if(node instanceof TypeInsnNode) {
				TypeInsnNode tN = (TypeInsnNode)node;
				String type = tN.desc.contains("/") ? "L" + tN.desc : tN.desc;
				return MethodUtil.convertType(type);
			}
			return BranchSeedInfo.OTHER;
		case "D2F":
			return "doube";
		case "D2I":
			return "int";
		case "D2L":
			return "long";
		case "DADD":
			return "double";
		case "DASTORE":
			return "double";
		case "DCMPG":
		case "DCMPL":
		case "DCONST_0":
		case "DCONST_1":
		case "DDIV":
		case "DLOAD":
		case "DLOAD_0":
		case "DLOAD_1":
		case "DLOAD_2":
		case "DLOAD_3":
		case "DMUL":
		case "DNEG":
		case "DREM":
		case "DRETURN":
		case "DSTORE":
		case "DSTORE_0":
		case "DSTORE_1":
		case "DSTORE_2":
		case "DSTORE_3":
		case "DSUB":
			return "double";
		case "DUP":
		case "DUP_X1":
		case "DUP_X2":
		case "DUP2":
		case "DUP2_X1":
		case "DUP2_X2":
			return BranchSeedInfo.OTHER;
		case "F2D":
			return "double";
		case "F2I":
			return "int";
		case "F2L":
			return "long";
		case "FADD":
		case "FALOAD":
		case "FASTORE":
		case "FCMPG":
		case "FCMPL":
		case "FCONST_0":
		case "FCONST_1":
		case "FCONST_2":
		case "FDIV":
		case "FLOAD":
		case "FLOAD_0":
		case "FLOAD_1":
		case "FLOAD_2":
		case "FLOAD_3":
		case "FMUL":
		case "FNEG":
		case "FREM":
		case "FRETURN":
		case "FSTORE":
		case "FSTORE_0":
		case "FSTORE_1":
		case "FSTORE_2":
		case "FSTORE_3":
		case "FSUB":
			return "float";
		case "I2B":
			return "byte";
		case "I2C":
			return "char";
		case "I2D":
			return "double";
		case "I2F":
			return "float";
		case "I2L":
			return "long";
		case "I2S":
			return "short";
		case "IADD":
		case "IALOAD":
		case "IAND":
		case "IASTORE":
		case "ICONST_M1":
		case "ICONST_0":
		case "ICONST_1":
		case "ICONST_2":
		case "ICONST_3":
		case "ICONST_4":
		case "ICONST_5":
		case "IDIV":
		case "IINC":
		case "ILOAD":
		case "ILOAD_0":
		case "ILOAD_1":
		case "ILOAD_2":
		case "ILOAD_3":
		case "IMPDEP1":
		case "IMPDEP2":
		case "IMUL":
		case "INEG":
			return "int";
		case "IOR":
		case "IREM":
		case "ISHL":
		case "ISHR":
		case "ISTORE":
		case "ISTORE_0":
		case "ISTORE_1":
		case "ISTORE_2":
		case "ISTORE_3":
		case "ISUB":
		case "IUSHR":
		case "IXOR":
			return "int";
		case "L2D":
			return "double";
		case "L2F":
			return "float";
		case "L2I":
			return "int";
		case "LADD":
		case "LALOAD":
		case "LAND":
		case "LASTORE":
		case "LCMP":
		case "LCONST_0":
		case "LCONST_1":
			return "long";
		case "LDC":
		case "LDC_W":
		case "LDC2_W":
			node = this.instruction.getASMNode();
			if(node instanceof LdcInsnNode) {
				LdcInsnNode lNode = (LdcInsnNode)node;
				Class<?> clazz = lNode.cst.getClass();
				return clazz.getCanonicalName();
			}
			return "java.lang.Object";
		case "LDIV":
		case "LLOAD":
		case "LLOAD_0":
		case "LLOAD_1":
		case "LLOAD_2":
		case "LLOAD_3":
		case "LMUL":
		case "LNEG":
		case "LOR":
		case "LREM":
		case "LSHL":
		case "LSHR":
		case "LSTORE":
		case "LSTORE_0":
		case "LSTORE_1":
		case "LSTORE_2":
		case "LSTORE_3":
		case "LSUB":
		case "LUSHR":
		case "LXOR":
			return "long";
		case "SALOAD":
			return "short";
		case "GETFIELD":
		case "GETSTATIC":
			FieldInsnNode fNode = (FieldInsnNode) this.instruction.getASMNode();
			String desc = fNode.desc;
			return MethodUtil.convertType(desc);
		}
		
		return BranchSeedInfo.OTHER;
	}


	public boolean incurZeroInformation() {
		if(this.isLoadArrayElement() || 
				this.getInstruction().explain().toLowerCase().contains("load") ||
				this.getInstruction().explain().toLowerCase().contains("cast") ||
				this.getInstruction().explain().toLowerCase().contains("store") ||
				this.getInstruction().explain().toLowerCase().contains("dup")) {
			return true;
		}
		
		return false;
	}


	public boolean isPrimitive() {
		String type = inferType(this.getInstruction().getInstructionType());
		
		if(type.equals(BranchSeedInfo.BYTE) ||
				type.equals(BranchSeedInfo.CHARACTER) ||
				type.equals(BranchSeedInfo.DOUBLE) ||
				type.equals(BranchSeedInfo.FLOAT) ||
				type.equals(BranchSeedInfo.INT) ||
				type.equals(BranchSeedInfo.LONG) ||
				type.equals(BranchSeedInfo.SHORT) ||
				type.equals(BranchSeedInfo.STRING) ||
				type.equals(BranchSeedInfo.TBYTE) ||
				type.equals(BranchSeedInfo.TCHARACTER) ||
				type.equals(BranchSeedInfo.TDOUBLE) ||
				type.equals(BranchSeedInfo.TFLOAT) ||
				type.equals(BranchSeedInfo.TINT) ||
				type.equals(BranchSeedInfo.TLONG) ||
				type.equals(BranchSeedInfo.TSHORT) ||
				type.equals(BranchSeedInfo.TSTRING)) {
			return true;
		}
		
		return false;
	}


	public boolean isCompare() {
		switch(this.instruction.getASMNode().getOpcode()) {
		case Opcode.IF_ACMPEQ:
		case Opcode.IF_ACMPNE:
		case Opcode.IF_ICMPEQ:
		case Opcode.IF_ICMPGE:
		case Opcode.IF_ICMPGT:
		case Opcode.IF_ICMPLE:
		case Opcode.IF_ICMPLT:
		case Opcode.IF_ICMPNE:
		case Opcode.LCMP:
		case Opcode.FCMPL:
		case Opcode.FCMPG:
		case Opcode.DCMPL:
		case Opcode.DCMPG:
        	return true;
		}
		return false;
	}
	
	public List<DepVariable> getAllChildrenNodesIncludingItself() {
		
		List<DepVariable> primitiveSet = new ArrayList<>();
		List<DepVariable> allSet = new ArrayList<>();
		
		primitiveSet.add(this);
		collectChildren(this, primitiveSet, allSet);
		
		return primitiveSet;
	}


	private void collectChildren(DepVariable depVariable, 
			List<DepVariable> primitiveSet, List<DepVariable> allSet) {
		
		for(List<DepVariable> children: depVariable.getRelations()) {
			if(children == null) continue;
			
			for(DepVariable child: children) {
				if(!allSet.contains(child)) {
					allSet.add(child);
					if(child.isPrimitive()) {
						primitiveSet.add(child);
					}
					else {
						collectChildren(child, primitiveSet, allSet);
					}
				}
			}
		}
		
	}

	public void printConstructionError(TestCase test, Branch b) {
		logger.error("exception happens when processing branch " + b);
		logger.error("working on node" + this);		
		logger.error("partial test case:");
		logger.error(test.toString());
	}
	
}
