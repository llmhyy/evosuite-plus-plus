package org.evosuite.testcase.synthesizer.improvedsynth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.FieldVariableWrapper;
import org.evosuite.testcase.synthesizer.var.ThisVariableWrapper;

/**
 * Helper class for manipulating DepVariableWrapper (OCG nodes).
 */
public class DepVariableWrapperUtil {
	private DepVariableWrapperUtil() {
	}
	
	/**
	 * A FieldVariableWrapper or ThisVariableWrapper encloses a particular type.
	 * This method extracts and returns that type information.
	 * @param node
	 * @return
	 */
	public static Class<?> getClassOf(DepVariableWrapper node) {
		boolean isField = (node instanceof FieldVariableWrapper);
		boolean isThis = (node instanceof ThisVariableWrapper);
		if (!isField && !isThis) {
			return null;
		}
		
		return null;		
	}
	
	/**
	 * Returns the methods that we can call from an instance of the class
	 * enclosed in the node.
	 * @param node
	 * @return
	 */
	public static List<Method> extractMethodsFrom(DepVariableWrapper node) {
		Class<?> clazz = getClassOf(node);
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
	public static boolean testGetter(Method method, DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// TODO
		return false;
	}
	
	/**
	 * 
	 * @param method
	 * @param fromNode
	 * @param toNode
	 * @return
	 */
	public static boolean testSetter(Method method, DepVariableWrapper fromNode, DepVariableWrapper toNode) {
		// TODO
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
		
		Class<?> parentClass = getClassOf(parentNode);
		try {
			return parentClass.getField(fieldNode.var.getName());
		} catch (NoSuchFieldException e) {
			return null;
		}
	}
}
