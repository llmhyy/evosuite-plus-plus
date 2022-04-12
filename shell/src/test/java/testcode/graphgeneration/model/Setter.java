package testcode.graphgeneration.model;

import java.util.ArrayList;
import java.util.List;

import testcode.graphgeneration.GraphNode;

public class Setter extends Method {
	private Field setField;
	
	// TODO: This probably shouldn't be GraphNode.
	private List<GraphNode> pathToSetField;
	
	public Setter(String declaringClass, String name, String returnType, Field setField, List<GraphNode> pathToSetField) {
		super(declaringClass, name, returnType);
		if (setField == null) {
			throw new IllegalArgumentException("Cannot have a null set field for a setter!");
		}
		
		if (pathToSetField == null) {
			throw new IllegalArgumentException("Cannot have a null path to the set field for a setter!");
		}
		// TODO Auto-generated constructor stub
		this.setField = setField;
		this.pathToSetField = new ArrayList<>(pathToSetField);
	}
	
	public Field getSetField() {
		return setField;
	}
	
	public List<GraphNode> getPathToSetField() {
		return new ArrayList<>(pathToSetField);
	}
}
