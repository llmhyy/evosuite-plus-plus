package testcode.graphgeneration.model;

import java.util.ArrayList;
import java.util.List;

import testcode.graphgeneration.GraphNode;

public class FieldArrayElementSetter extends Method {
	private Field arraySource;
	private List<GraphNode> path;
	
	public FieldArrayElementSetter(String declaringClass, String name, String returnType, Field arraySource, List<GraphNode> path) {
		super(declaringClass, name, returnType);
		this.arraySource = arraySource;
		this.path = new ArrayList<>(path);
	}
	
	public Field getArray() {
		return this.arraySource;
	}
	
	public List<GraphNode> getPath() {
		return new ArrayList<>(path);
	}
}
