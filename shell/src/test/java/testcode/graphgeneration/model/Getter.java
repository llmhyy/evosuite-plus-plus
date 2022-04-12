package testcode.graphgeneration.model;

import java.util.ArrayList;
import java.util.List;

import testcode.graphgeneration.GraphNode;

public class Getter extends Method {
	private Field returnedField;
	
	// TODO: This probably shouldn't be GraphNode.
	private List<GraphNode> pathToReturnedField;
	
	public Getter(String declaringClass, String name, String returnType, Field returnedField, List<GraphNode> pathToReturnedField) {
		super(declaringClass, name, returnType);
		if (returnedField == null) {
			throw new IllegalArgumentException("Cannot have a null returned field for a getter!");
		}
		
		if (pathToReturnedField == null) {
			throw new IllegalArgumentException("Cannot have a null path to the returned field for a getter!");
		}
		
		// TODO Auto-generated constructor stub
		this.returnedField = returnedField;
		this.pathToReturnedField = new ArrayList<>(pathToReturnedField);
	}
	
	public Field getReturnedField() {
		return returnedField;
	}
	
	public List<GraphNode> getPathToReturnedField() {
		return new ArrayList<>(pathToReturnedField);
	}
}
