package org.evosuite.graphs.interprocedural.var;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

public class DepVariableFactory {
	public static DepVariable createVariableInstance(BytecodeInstruction ins) {
		if(ins.loadsReferenceToThis()) {
			ThisVariable var = new ThisVariable(ins);
			var.setType(DepVariable.THIS);
			var.setName("this");
			
			return var;
		}
		else if(ins.isParameter()) {
			ParameterVariable var = new ParameterVariable(ins);
			var.setType(DepVariable.PARAMETER);
			var.setName(ins.getVariableName());
			
			return var;
		}
		else if(ins.getASMNode().getOpcode() == Opcodes.GETSTATIC) {
			StaticFieldVariable var = new StaticFieldVariable(ins);
			var.setType(DepVariable.STATIC_FIELD);
			var.setName(((FieldInsnNode)ins.getASMNode()).name);
			
			return var;
		}
		else if(ins.getASMNode().getOpcode() == Opcodes.GETFIELD) {
			InstanceFieldVariable var = new InstanceFieldVariable(ins);
			var.setType(DepVariable.INSTANCE_FIELD);
			var.setName(((FieldInsnNode)ins.getASMNode()).name);
			
			return var;
		}
		else if(ins.isArrayLoadInstruction()) {
			ArrayElementVariable var = new ArrayElementVariable(ins);
			var.setType(DepVariable.ARRAY_ELEMENT);
			var.setName("array index");
			
			return var;
		}
		else {
			OtherVariable var = new OtherVariable(ins);
			var.setType(DepVariable.OTHER);
			var.setName("$unknown");
			
			return var;
		}
		
		
	}
	
}
