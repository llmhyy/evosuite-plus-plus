package org.evosuite.testcase.synthesizer.improvedsynth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.evosuite.TestGenerationContext;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.FieldVariableWrapper;
import org.evosuite.testcase.synthesizer.var.ThisVariableWrapper;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Helper class for manipulating DepVariableWrapper (OCG nodes).
 */
public class DepVariableWrapperUtil {
	private DepVariableWrapperUtil() {
	}
	
	private static Class<?> extractPrimitiveType(DepVariableWrapper node) {
		try {
			FieldInsnNode fieldNode = (FieldInsnNode) node.var.getInstruction().getASMNode();
			String fieldType = fieldNode.desc;
			String formattedFieldType = fieldType.replace("/", ".");
			if (formattedFieldType.startsWith("L")) {
				formattedFieldType = formattedFieldType.substring(1, formattedFieldType.length() - 1);
			} else if (formattedFieldType.startsWith("[L")) {
				formattedFieldType = formattedFieldType.substring(2, formattedFieldType.length() - 1);
			}
			
			switch (formattedFieldType) {
				case "Z": 
					return Boolean.class;
				case "B":
					return Byte.class;
				case "C":
					return Character.class;
				case "S":
					return Short.class;
				case "I":
					return Integer.class;
				case "J":
					return Long.class;
				case "F":
					return Float.class;
				case "D":
					return Double.class;
				default:
					return null;
			}
		} catch (NullPointerException e) {
			return null;
		}
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
			if (isPrimitive) {
				return extractPrimitiveType(node);
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
	public static List<Method> extractMethodsFrom(DepVariableWrapper node) {
		Class<?> clazz = extractClassFrom(node);
		if (clazz == null) {
			return new ArrayList<>();
		}
		
		return Arrays.asList(clazz.getMethods());
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
		List<Method> candidateMethods = extractMethodsFrom(node);
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
		List<Method> candidateMethods = extractMethodsFrom(node);
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
	
	/**
	 * @param method
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean testFieldGetter(TestCase testCase, Method method, DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// For now, we ignore cases where the method has a non-zero number of parameters.
		if (method.getParameterCount() > 0) {
			return false;
		}
		
		// TODO
		return true;
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
		
		// For now, we ignore cases where the method has a >1 parameter
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
