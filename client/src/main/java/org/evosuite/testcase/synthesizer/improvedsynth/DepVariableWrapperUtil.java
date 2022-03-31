package org.evosuite.testcase.synthesizer.improvedsynth;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.FieldVariableWrapper;
import org.evosuite.testcase.synthesizer.var.ThisVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;
import org.evosuite.testcase.variable.VariableReference;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.Opcodes;

/**
 * Helper class for manipulating DepVariableWrapper (OCG nodes).
 * TODO: Possibly move the non-DepVariableWrapper methods into another class (ReflectionUtil?)
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
	
	public static Field extractFieldFrom(DepVariableWrapper node) {
		if (!(node instanceof FieldVariableWrapper)) {
			return null;
		}
		
		try {
			String fieldName = extractFieldName(node);
			String fieldOwner = extractFieldOwner(node);
			String fieldType = extractFieldType(node);
			Class<?> fieldOwnerClass = extractClassFrom(fieldOwner);
			Field field = fieldOwnerClass.getDeclaredField(fieldName);
			
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
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
	private static String getSignatureOf(Method method) {
	    String signature;
	    try {
	        Field genericSignature = Method.class.getDeclaredField("signature");
	        genericSignature.setAccessible(true);
	        signature = (String) genericSignature.get(method);
	        if (signature != null) {
	        	// Sometimes the signature field will omit the method's name
	        	// If so, we need to add the method's name back on
	        	if (signature.length() > 0 && signature.charAt(0) == '(') {
	        		signature = method.getName() + signature;
	        	}
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
		
		// Typically this occurs when the method accepts a generic type
		// We need to strip the generic type information to match what's seen by the InstrumentingClassLoader
		if (instructions == null) {
			// Strip generic class types from the method
			int positionOfLeftAngleBracket = methodName.indexOf('<');
			int positionOfRightAngleBracket = methodName.lastIndexOf('>');
			
			if (positionOfLeftAngleBracket < 0 || positionOfRightAngleBracket < 0) {
				// It's not a generics issue, we can't deal with it
				return null;
			}
			
			String strippedMethodName = methodName.substring(0, positionOfLeftAngleBracket) + methodName.substring(positionOfRightAngleBracket + 1);
			instructions = bytecodeInstructionPool.getInstructionsIn(className, strippedMethodName);
		}
		
		return instructions;
	}
	
	private static boolean isInstructionGettingField(BytecodeInstruction instruction, DepVariableWrapper node) {
		String desiredFieldName = extractFieldName(node);
		String desiredFieldOwner = extractFieldOwner(node).replace(".", "/");
		return isInstructionGettingField(instruction, desiredFieldName, desiredFieldOwner);
	}
	
	private static boolean isInstructionGettingField(BytecodeInstruction instruction, Field field) {
		if (instruction == null || field == null) {
			return false;
		}
		
		String desiredFieldName = field.getName();
		String desiredFieldOwner = field.getDeclaringClass().getCanonicalName();
		return isInstructionGettingField(instruction, desiredFieldName, desiredFieldOwner);
	}
	
	private static boolean isInstructionGettingField(BytecodeInstruction instruction, String fieldName, String fieldOwner) {
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
			String nodeFieldOwner = fieldInsnNode.owner.replace("/", ".");
			boolean isFieldNameMatch = nodeFieldName.equals(fieldName);
			boolean isFieldOwnerMatch = nodeFieldOwner.equals(fieldOwner);
			if (isFieldNameMatch && isFieldOwnerMatch) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param method
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean testFieldGetter(Method method, DepVariableWrapper toNode) {	
		Field field = extractFieldFrom(toNode);
		return testFieldGetter(method, field);
	}
	
	public static boolean testFieldGetter(Method method, Field field) {
		List<BytecodeInstruction> instructions = getInstructionsFor(method);
		for (BytecodeInstruction instruction : instructions) {
			boolean isGetfieldForField = isInstructionGettingField(instruction, field);
			if (isGetfieldForField) {
				return true;
			}
			
			boolean isMethodCall = instruction.isMethodCall();
			if (isMethodCall) {
				// Check for case 2:
				// It's calling a getter that returns the field that we want
				boolean isGetter = isInvokedMethodDesiredGetter(instruction, field);
				if (isGetter) {
					// Do a lookahead to check if it's being returned
					// TODO: Check if it's being stored and returned later
					// For some reason, BytecodeInstruction#getNextInstruction returns null
					// in the specific case that we want to get the last instruction in the CFG
					// We have to retrieve the last instruction manually
					BytecodeInstruction nextInstruction = instruction.getActualCFG().getInstruction(instruction.getInstructionId() + 1);					
					if (nextInstruction != null && nextInstruction.isReturn()) {
						return true;
					}
				}
				
				// If it's not, continue searching
			}
		}
		
		return false;
	}
	
	public static Method getInvokedMethod(BytecodeInstruction instruction) {
		boolean isMethodCall = instruction.isMethodCall();
		if (!isMethodCall) {
			return null;
		}
		
		try {
			MethodInsnNode methodNode = (MethodInsnNode) instruction.getASMNode();
			String methodName = methodNode.name;
			String methodOwner = methodNode.owner;
			String methodSignature = methodName + methodNode.desc;
			Class<?> methodOwningClass = extractClassFrom(methodOwner.replace("/", "."));
			Method[] methods = methodOwningClass.getDeclaredMethods();
			for (Method currentMethod : methods) {
				// Just compare signatures
				String currentSignature = getSignatureOf(currentMethod);
				if (methodSignature.equals(currentSignature)) {
					return currentMethod;
				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		
		return null;
	}
	
	private static boolean isInvokedMethodDesiredGetter(BytecodeInstruction instruction, Field field) {
		boolean isMethodCall = instruction.isMethodCall();
		if (!isMethodCall) {
			return false;
		}
		
		Method method = getInvokedMethod(instruction);
		if (method == null) {
			return false;
		}
		
		return testFieldGetter(method, field);
	}
	
	// Assumes signature has already been stripped of method name (begins with '(')
	private static int getParameterCountFromSignature(String signature) {
		List<String> sigAsList = Arrays.asList(signature.split(""));
		int paramCount = 0;
		int index = 0;
		while (index < sigAsList.size()) {
			String character = sigAsList.get(index);
			if (character.equals(")")) {
				return paramCount;
			}
			
			if (character.equals("(")) {
				if (index > 0) {
					throw new IllegalArgumentException("Illegal character '(' detected at non-zero index in signature string (" + signature + ")!");
				}
				
				index++;
				continue;
			}
			
			switch (character) {
				/* Primitive types */
				case "Z":
				case "B":
				case "C":
				case "S":
				case "I":
				case "J":
				case "F":
				case "D":
					paramCount++;
					index++;
					continue;
				/* fully-qualified-class */
				case "L": 
					paramCount++;
					// Shift index over to after the next semicolon (end of type)
					index = signature.indexOf(";", index) + 1;
					continue;
				case "[":
					index++;
					continue;
				default:
					throw new IllegalStateException("Illegal state encountered, panic.");
			}
		}
		
		throw new IllegalStateException("Illegal state encountered, panic.");
	}
	
	/**
	 * 
	 * @param method
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean testFieldSetter(Method method, DepVariableWrapper toNode) {
		Field field = extractFieldFrom(toNode);
		return testFieldSetter(method, field);
	}
	
	/**
	 * 
	 * @param method
	 * @param field
	 * @return
	 */
	public static boolean testFieldSetter(Method method, Field field) {
		if (method == null || field == null) {
			return false;
		}
		
		// Sanity check, see if the method accepts a parameter of same/super-type as the field
		// Note: disabled sanity check since it's possible for a setter to not take in a parameter
		// of same type as the field e.g. the setter retrieves from a map via a key parameter
//		Class<?> fieldType = field.getType();
//		Class<?>[] parameterTypes = method.getParameterTypes();
//		boolean isMethodHasFieldTypeAsParameterType = false;
//		for (Class<?> parameterType : parameterTypes) {
//			if (parameterType.isAssignableFrom(fieldType)) {
//				isMethodHasFieldTypeAsParameterType = true;
//			}
//		}
//		if (!isMethodHasFieldTypeAsParameterType) {
//			return false;
//		}
		
		List<BytecodeInstruction> instructions = getInstructionsFor(method);
		if (instructions == null) {
			return false;
		}
		
		for (BytecodeInstruction instruction : instructions) {
			// Three cases
			// 1) Non-relevant instruction (ignore)
			// 2) Field definition instruction (check if field is correct)
			// 3) method call (recursively check if it's a setter)
			boolean isFieldDef = instruction.isFieldDefinition();
			if (isFieldDef) {
				// BytecodeInstruction#isFieldDefinition also includes certain cases for method calls
				// Check first if it's a method call
				boolean isMethodCall = instruction.isMethodCall();
				if (isMethodCall) {
					Method invokedMethod = getInvokedMethod(instruction);
					boolean isInvokedMethodDesiredSetter = testFieldSetter(invokedMethod, field);
					if (isInvokedMethodDesiredSetter) {
						return true;
					}
				}
				
				// Else check what field it sets
				// TODO: Check if this procedure works for PUTSTATIC/field array def
				AbstractInsnNode asmNode = instruction.getASMNode();
				if (!(asmNode instanceof FieldInsnNode)) {
					return false;
				}
				
				FieldInsnNode fieldInsnNode = (FieldInsnNode) asmNode;
				String fieldInsnNodeOwner = fieldInsnNode.owner.replace("/", ".");
				String fieldInsnNodeName = fieldInsnNode.name;
				if (fieldInsnNodeOwner == null || fieldInsnNodeName == null) {
					return false;
				}
				boolean isOwnersMatch = fieldInsnNodeOwner.equals(field.getDeclaringClass().getCanonicalName());
				boolean isNamesMatch = fieldInsnNodeName.equals(field.getName());
				if (isOwnersMatch && isNamesMatch) {
					return true;
				}
			}
		}
		
		return false;
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
	
	public static VarRelevance getVarRelevanceFrom(ThisVariableWrapper node, TestCase testCase, Map<DepVariableWrapper, VarRelevance> nodeToVarRelevance) {
		VariableReference generatedVariable = null;
		
		if (node.parents.isEmpty()) {
			MethodStatement methodStatement = testCase.findTargetMethodCallStatement();
			if(methodStatement != null) {
				generatedVariable = methodStatement.getCallee();
			}
		} else {
			for (DepVariableWrapper parentNode: node.parents) {
				if (nodeToVarRelevance.get(parentNode) != null) {
					generatedVariable = nodeToVarRelevance.get(parentNode).matchedVars.get(0);
				}
			}
		}
		List<VariableReference> variableReferences = new ArrayList<>();
		if (generatedVariable != null) {
			variableReferences.add(generatedVariable);
		}
		return new VarRelevance(variableReferences, variableReferences);
	}
	
	public static VarRelevance generateVarRelevanceFrom(VariableReference variableReference) {
		List<VariableReference> list = new ArrayList<>();
		if (variableReference != null) {
			list.add(variableReference);
		}
		
		return new VarRelevance(list, list);
	}
}
