package org.evosuite.testcase.synthesizer.improvedsynth;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.FieldVariableWrapper;
import org.evosuite.testcase.synthesizer.var.ThisVariableWrapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

import org.objectweb.asm.Opcodes;

/**
 * Helper class for manipulating DepVariableWrapper (OCG nodes).
 */
public class DepVariableWrapperUtil {
	private DepVariableWrapperUtil() {
	}
	
	private static Class<?> extractPrimitiveType(String fieldType) {
		switch (fieldType) {
			case "Z": 
				return boolean.class;
			case "B":
				return byte.class;
			case "C":
				return char.class;
			case "S":
				return short.class;
			case "I":
				return int.class;
			case "J":
				return long.class;
			case "F":
				return float.class;
			case "D":
				return double.class;
			default:
				return null;
		}
	}
	
	private static Class<?> extractPrimitiveType(DepVariableWrapper node) {
		FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
		String fieldType = fieldNode.desc;
		String formattedFieldType = fieldType.replace("/", ".");
		return extractPrimitiveType(formattedFieldType);
	}
	
	private static String extractNonPrimitiveType(DepVariableWrapper node) {
		try {
			FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
			String fieldType = fieldNode.desc;
			String formattedFieldType = fieldType.replace("/", ".");
			if (formattedFieldType.startsWith("L")) {
				formattedFieldType = formattedFieldType.substring(1, formattedFieldType.length() - 1);
			} else if (formattedFieldType.startsWith("[L")) {
				formattedFieldType = formattedFieldType.substring(2, formattedFieldType.length() - 1);
			}
			return formattedFieldType;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	private static String extractFieldType(DepVariableWrapper node) {
		try {
			FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
			String fieldType = fieldNode.desc;
			return fieldType;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	private static String extractFieldName(DepVariableWrapper node) {
		try {
			FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
			String fieldName = fieldNode.name;
			return fieldName;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	private static String extractFieldOwner(DepVariableWrapper node) {
		try {
			FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
			String fieldOwner = fieldNode.owner.replace("/", ".");
			return fieldOwner;
		} catch (NullPointerException e) {
			return null;
		}
		
	}
	
	
	private static boolean isArray(DepVariableWrapper node) {
		String fieldType = extractFieldType(node);
		if (fieldType == null) {
			return false;
		}
		
		return fieldType.startsWith("[");
	}
	
	private static int getDimensionOfArrayFieldType(String fieldType) {
		// Return the number of [ in the string
		return (int) fieldType.chars().filter(character -> character == '[').count();
	}
	
	private static Class<?> extractArrayType(String fieldType) {
		int dimension = getDimensionOfArrayFieldType(fieldType);
		Class<?> baseClass = extractClassFrom(fieldType.replace("[", ""));
		Class<?> toReturn = baseClass;
		for (int i = 0; i < dimension; i++) {
			toReturn = Array.newInstance(toReturn, 0).getClass();
		}
		return toReturn;
	}
	
	private static Class<?> extractArrayType(DepVariableWrapper node) {
		if (!isArray(node)) {
			return null;
		}
		
		String fieldType = extractFieldType(node);
		return extractArrayType(fieldType);
	}
	
	private static boolean isPrimitive(String fieldType) {
		if (fieldType == null) {
			return false;
		}
		
		return fieldType.equals("Z") ||
				fieldType.equals("B") ||
				fieldType.equals("C") ||
				fieldType.equals("S") ||
				fieldType.equals("I") ||
				fieldType.equals("J") ||
				fieldType.equals("F") ||
				fieldType.equals("D");
	}
	
	private static Class<?> extractClassFrom(String fieldType) {
		if (isPrimitive(fieldType)) {
			return extractPrimitiveType(fieldType);
		} else {
			try {
				return TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(fieldType);
			} catch (ClassNotFoundException | NullPointerException e) {
				return null;
			}
		}
	}
	
	/**
	 * A FieldVariableWrapper or ThisVariableWrapper encloses a particular type.
	 * This method extracts and returns that type information.
	 * @param node
	 * @return
	 */
	public static Class<?> extractClassFrom(DepVariableWrapper node) {
		boolean isField = (node instanceof FieldVariableWrapper);
		boolean isThis = (node instanceof ThisVariableWrapper);
		if (!isField && !isThis) {
			return null;
		}
		
		String classAsString = "";
		
		if (isField) {
			boolean isPrimitive = node.var.isPrimitive();
			boolean isArray = isArray(node);
			if (isPrimitive) {
				return extractPrimitiveType(node);
			} else if (isArray) {
				return extractArrayType(node);
			} else {
				classAsString = extractNonPrimitiveType(node);
			}
			
		}
		
		if (isThis) {
			classAsString = node.var.getClassName();	
		}
		
		try {
			return TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(classAsString);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Returns the methods that we can call from an instance of the class
	 * enclosed in the node.
	 * @param node
	 * @return
	 */
	public static List<Method> extractNonNativeMethodsFrom(DepVariableWrapper node) {
		Class<?> clazz = extractClassFrom(node);
		if (clazz == null) {
			return new ArrayList<>();
		}
		
		Method[] methods = clazz.getMethods();
		List<Method> nonNativeMethods = new ArrayList<>();
		for (Method method : methods) {
			if (!Modifier.isNative(method.getModifiers())) {
				nonNativeMethods.add(method);
			}
		}
		return nonNativeMethods;
	}
	
	/**
	 * Returns the methods that we can call from an instance of the class
	 * enclosed in the node, where the methods return an instance of the return type.
	 * @param node
	 * @param desiredReturnType
	 * @return
	 */
	public static List<Method> extractMethodsReturning(DepVariableWrapper node, Class<?> desiredReturnType) {
		List<Method> methodsToReturn = new ArrayList<>();
		List<Method> candidateMethods = extractNonNativeMethodsFrom(node);
		for (Method candidateMethod : candidateMethods) {
			Class<?> candidateMethodReturnType = candidateMethod.getReturnType();
			if (candidateMethodReturnType.equals(desiredReturnType)) {
				methodsToReturn.add(candidateMethod);
			}
		}
		return methodsToReturn;
	}
	
	/**
	 * Returns methods that we can call from an instance of the class enclosed in the node, where the methods
	 * accept a parameter of type desiredParameterType.
	 * @param node
	 * @param desiredParameterType
	 * @return
	 */
	public static List<Method> extractMethodsAccepting(DepVariableWrapper node, Class<?> desiredParameterType) {
		List<Method> methodsToReturn = new ArrayList<>();
		List<Method> candidateMethods = extractNonNativeMethodsFrom(node);
		for (Method candidateMethod : candidateMethods) {
			for (Class<?> parameterType : candidateMethod.getParameterTypes()) {
				if (parameterType.equals(desiredParameterType)) {
					methodsToReturn.add(candidateMethod);
					break;
				}
			}
		}
		return methodsToReturn;
	}
	
	/*
	 * See https://stackoverflow.com/questions/45072268/how-can-i-get-the-signature-field-of-java-reflection-method-object
	 */
	public static String getSignatureOf(Method method) {
	    String signature;
	    try {
	        Field genericSignature = Method.class.getDeclaredField("signature");
	        genericSignature.setAccessible(true);
	        signature = (String) genericSignature.get(method);
	        if (signature != null) {
	        	return signature;
	        }
	    } catch (IllegalAccessException | NoSuchFieldException e) { 
	        e.printStackTrace();
	    }

	    StringBuilder stringBuilder = new StringBuilder("(");
	    for(Class<?> clazz : method.getParameterTypes()) 
	        stringBuilder.append((signature = Array.newInstance(clazz, 0).toString())
	            .substring(1, signature.indexOf('@')));
	    String unformattedSignature = method.getName() + stringBuilder.append(')')
	        .append(
	            method.getReturnType() == void.class ? "V":
	            (signature = Array.newInstance(method.getReturnType(), 0).toString())
	            	.substring(1, signature.indexOf('@'))
	        )
	        .toString();
	    String formattedSignature = unformattedSignature.replace(".", "/");
	    return formattedSignature;
	}
	
	private static List<BytecodeInstruction> getInstructionsFor(Method method) {
		Class<?> clazz = method.getDeclaringClass();
		String className = clazz.getCanonicalName();
		String methodName = getSignatureOf(method);
		
		BytecodeInstructionPool bytecodeInstructionPool = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT());
		List<BytecodeInstruction> instructions = bytecodeInstructionPool.getInstructionsIn(className, methodName);
		
		return instructions;
	}
	
	private static boolean isInstructionGettingField(BytecodeInstruction instruction, DepVariableWrapper node) {
		if (!(node instanceof FieldVariableWrapper)) {
			return false;
		}
		
		boolean isGetField = instruction.getASMNode().getOpcode() == Opcodes.GETFIELD;
		boolean isGetStatic = instruction.getASMNode().getOpcode() == Opcodes.GETSTATIC;
		if (!(isGetField || isGetStatic)) {
			return false;
		}
		
		// TODO: Check if this logic works for GETSTATIC
		AbstractInsnNode insnNode = instruction.getASMNode();
		boolean isFieldInsnNode = insnNode instanceof FieldInsnNode;
		if (!isFieldInsnNode) {
			return false;
		}
		
		if (isFieldInsnNode) {
			FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNode;
			String nodeFieldName = fieldInsnNode.name;
			String nodeFieldOwner = fieldInsnNode.owner;
			String desiredFieldName = extractFieldName(node);
			String desiredFieldOwner = extractFieldOwner(node).replace(".", "/");
			boolean isFieldNameMatch = nodeFieldName.equals(desiredFieldName);
			boolean isFieldOwnerMatch = nodeFieldOwner.equals(desiredFieldOwner);
			if (isFieldNameMatch && isFieldOwnerMatch) {
				return true;
			}
		}
		
		return false;
	}
	
	private static Method getInvokedMethod(BytecodeInstruction instruction) {
		return null;
	}
	/**
	 * @param method
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean testFieldGetter(TestCase testCase, Method method, DepVariableWrapper fromNode, DepVariableWrapper toNode) {			
		List<BytecodeInstruction> instructions = getInstructionsFor(method);
		for (BytecodeInstruction instruction : instructions) {
			boolean isGetfieldForField = isInstructionGettingField(instruction, toNode);
			if (isGetfieldForField) {
				return true;
			}
			
			boolean isMethodCall = instruction.isMethodCall();
			if (isMethodCall) {
				// What to do if it's a method call?
				// TODO
				// Repeat this procedure one level down with the method
				Method invokedMethod = getInvokedMethod(instruction);
				boolean isInvokedMethodGetter = testFieldGetter(testCase, invokedMethod, fromNode, toNode);
				if (isInvokedMethodGetter) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param method
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean testFieldSetter(Method method, DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// Questions
		// 1) What values to put in the parameters?
		// 2) How to perform dataflow analysis?
		
		// For now, we ignore cases where the method has >1 parameter
		if (method.getParameterCount() != 1) {
			return false;
		}
		
		// TODO
		return true;
	}
	
	/**
	 * 
	 * @param parentNode
	 * @param fieldNode
	 * @return
	 */
	public static Field extractFieldFrom(DepVariableWrapper parentNode, DepVariableWrapper fieldNode) {
		if (!(fieldNode instanceof FieldVariableWrapper)) {
			return null;
		}
		
		Class<?> parentClass = extractClassFrom(parentNode);
		if (parentClass == null) {
			return null;
		}
		
		try {
			return parentClass.getField(fieldNode.var.getName());
		} catch (NoSuchFieldException e) {
			return null;
		}
	}
}
