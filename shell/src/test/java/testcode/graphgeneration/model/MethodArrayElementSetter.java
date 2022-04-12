package testcode.graphgeneration.model;

import java.util.ArrayList;
import java.util.List;

import testcode.graphgeneration.GraphNode;

public class MethodArrayElementSetter extends Method {
	private Method arraySource;
	private List<GraphNode> path;
	
	public MethodArrayElementSetter(String declaringClass, String name, String returnType, Method arraySource, List<GraphNode> path) {
		super(declaringClass, name, returnType);
		this.arraySource = arraySource;
		this.path = new ArrayList<>(path);
	}
	
	public Method getArray() {
		return this.arraySource;
	}
	
	public List<GraphNode> getPath() {
		return new ArrayList<>(path);
	}
}
