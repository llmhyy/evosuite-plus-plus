package org.evosuite.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CommonUtility {
	public static String getMethodId(String className, String methodName) {
		return className + "#" + methodName;
	}
	
	public static String getMethodName(MethodNode m) {
		String methodName = m.name + m.desc; 
		return methodName;
	}
	
	public static String getMethodName(String methodName, String methodDesc) {
		return methodName + methodDesc;
	}
	
	public static boolean isInvokeMethodInsn(AbstractInsnNode condDefinition) {
		return CollectionUtil.existIn(condDefinition.getOpcode(), 
				Opcodes.INVOKESPECIAL,
				Opcodes.INVOKESTATIC,
				Opcodes.INVOKEINTERFACE,
				Opcodes.INVOKEDYNAMIC,
				Opcodes.INVOKEVIRTUAL);
	}

}
