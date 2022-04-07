package testcode.graphgeneration;

public class CodeUtil {

	public static boolean isPrimitive(String dataType) {
		GraphNode node = new GraphNode(0);
		node.setNodeType(new NodeType(dataType, "na"));
		
		return GraphNodeUtil.isPrimitive(node);
		
	}

}
