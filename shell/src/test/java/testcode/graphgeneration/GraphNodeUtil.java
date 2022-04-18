package testcode.graphgeneration;

import java.util.Arrays;

public class GraphNodeUtil {
	private GraphNodeUtil() {}
	
	/**
	 * @param node The given node.
	 * @return {@code true} if the node represents a method, {@code false} otherwise.
	 */
	public static boolean isMethod(GraphNode node) {
		return node.getNodeType().getName().startsWith("METHOD");
	}
	
	/**
	 * @param node The given node.
	 * @return {@code true} if the node represents a parameter, {@code false} otherwise.
	 */
	public static boolean isParameter(GraphNode node) {
		return node.getNodeType().getName().startsWith("PARAM");
	}
	
	/**
	 * @param node The given node.
	 * @return {@code true} if the node represents a field, {@code false} otherwise.
	 */
	public static boolean isField(GraphNode node) {
		return node.getNodeType().getName().startsWith("FIELD");
	}
	
	/**
	 * @param node The given node.
	 * @return A {@code String} representation of the declared class of the {@code GraphNode}.
	 */
	public static String getDeclaredClass(GraphNode node) {
		return node.getNodeType().getType();
	}
	
	/**
	 * @param node The given node.
	 * @return {@code true} if the node represents a custom class, {@code false} otherwise.
	 */
	public static boolean isObject(GraphNode node) {
		return node.getNodeType().getType().startsWith("Class");
	}
	
	/**
	 * @param node The given node.
	 * @return {@code true} if the node represents an array, {@code false} otherwise.
	 */
	public static boolean isArray(GraphNode node) {
		return node.getNodeType().getType().endsWith("[]");
	}
	
	/**
	 * @param node The given node.
	 * @return {@code true} if the node represents an array element, {@code false} otherwise.
	 */
	public static boolean isArrayElement(GraphNode node) {
		return node.getNodeType().getName().startsWith("ARRAY_ELEMENT");
	}

	/**
	 * @param node The given node.
	 * @return {@code true} if the node declares a primitive class, {@code false} otherwise.
	 */
	public static boolean isPrimitive(GraphNode node) {
		return getDeclaredClass(node).equals("void") || Arrays.asList(Graph.PRIMITIVE_TYPES).contains(getDeclaredClass(node));
	}
}
