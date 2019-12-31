package org.evosuite.graphs.dataflow;

import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.tree.AbstractInsnNode;

public class RelationBuilder {

	public static void buildRelation(DepVariable outputVar, DepVariable inputVar) {
		BytecodeInstruction instruction = outputVar.getInstruction();
		AbstractInsnNode insNode = instruction.getASMNode();
		
		int type = insNode.getType();
		if(type == AbstractInsnNode.FIELD_INSN) {
			Pair<String, DepVariable> relation = Pair.of(Relation.FIELD, outputVar);
			inputVar.setRelation(relation);
			
//			FieldInsnNode fieldInsNode = (FieldInsnNode)insNode;
//			if(fieldInsNode.getOpcode() == Opcodes.GETFIELD) {
//			}
//			else if(fieldInsNode.getOpcode() == Opcodes.GETSTATIC) {
//				
//			}
		}
		else if(type == AbstractInsnNode.FRAME) {
			
		}
		else if(type == AbstractInsnNode.IINC_INSN) {
			
		}
		else if(type == AbstractInsnNode.INSN) {
			
		}
		else if(type == AbstractInsnNode.INT_INSN) {
			
		}
		else if(type == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
			
		}
		else if(type == AbstractInsnNode.JUMP_INSN) {
			
		}
		else if(type == AbstractInsnNode.LABEL) {
			
		}
		else if(type == AbstractInsnNode.LDC_INSN) {
			
		}
		else if(type == AbstractInsnNode.LINE) {
			
		}
		else if(type == AbstractInsnNode.LOOKUPSWITCH_INSN) {
			
		}
		else if(type == AbstractInsnNode.METHOD_INSN) {
			
		}
		else if(type == AbstractInsnNode.MULTIANEWARRAY_INSN) {
			
		}
		else if(type == AbstractInsnNode.TABLESWITCH_INSN) {
			
		}
		else if(type == AbstractInsnNode.TYPE_INSN) {
			
		}
		else if(type == AbstractInsnNode.VAR_INSN) {
			
		}
		
	}

}
