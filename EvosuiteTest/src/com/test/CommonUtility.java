package com.test;

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
	
	public static boolean hasOpt(String[] args, String opt) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (opt.equals(args[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static String getOptValue(String[] args, String opt) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (opt.equals(args[i])) {
				if (i == args.length - 1 || args[i + 1].startsWith("-")) {
					return "";
				}
				return args[i + 1];
			}
		}
		return null;
	}
	
	public static boolean isInvokeMethodInsn(AbstractInsnNode condDefinition) {
		return CollectionUtils.existIn(condDefinition.getOpcode(), 
				Opcodes.INVOKESPECIAL,
				Opcodes.INVOKESTATIC,
				Opcodes.INVOKEINTERFACE,
				Opcodes.INVOKEDYNAMIC,
				Opcodes.INVOKEVIRTUAL);
	}

}
