package org.evosuite.graphs.dataflow;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.tree.AbstractInsnNode;

public class RelationBuilder {

	public static void buildRelation(DepVariable outputVar, DepVariable inputVar) {
		BytecodeInstruction instruction = outputVar.getInstruction();
		AbstractInsnNode insNode = instruction.getASMNode();
		
		int type = insNode.getType();
		if(type == AbstractInsnNode.FIELD_INSN) {
			inputVar.addRelation(Relation.FIELD, outputVar);
//			outputVar.addReverseRelation(Relation.FIELD, inputVar);
		}
		else if(type == AbstractInsnNode.INVOKE_DYNAMIC_INSN || type == AbstractInsnNode.METHOD_INSN) {
			inputVar.addRelation(Relation.CALL, outputVar);
//			outputVar.addReverseRelation(Relation.CALL, inputVar);
		}
		else {
			inputVar.addRelation(Relation.OTHER, outputVar);
//			outputVar.addReverseRelation(Relation.OTHER, inputVar);
		}
		
	}
	
	public static void buildRelation(DepVariable outputVar, DepVariable inputVar, String relation) {
		inputVar.addRelation(relation, outputVar);
//		outputVar.addReverseRelation(relation, inputVar);
	}

}
