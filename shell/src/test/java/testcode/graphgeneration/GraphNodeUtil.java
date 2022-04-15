package testcode.graphgeneration;

import java.util.Arrays;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Type;

public class GraphNodeUtil {
	private GraphNodeUtil() {}
	
	public static boolean isMethod(GraphNode node) {
		return node.getNodeType().getName().startsWith("METHOD");
	}
	
	public static boolean isParameter(GraphNode node) {
		return node.getNodeType().getName().startsWith("PARAM");
	}
	
	public static boolean isField(GraphNode node) {
		return node.getNodeType().getName().startsWith("FIELD");
	}
	
	public static String getDeclaredClass(GraphNode node) {
		return node.getNodeType().getType();
	}
	
	public static boolean isObject(GraphNode node) {
		return node.getNodeType().getType().startsWith("Class");
	}
	
	public static boolean isArray(GraphNode node) {
		return node.getNodeType().getType().endsWith("[]");
	}
	
	public static boolean isArrayElement(GraphNode node) {
		return node.getNodeType().getName().startsWith("ARRAY_ELEMENT");
	}

	public static boolean isPrimitive(GraphNode node) {
		return Arrays.asList(Graph.PRIMITIVE_TYPES).contains(getDeclaredClass(node));
	}
}
