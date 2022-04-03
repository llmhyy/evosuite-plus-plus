package testcode.graphgeneration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import testcode.graphgeneration.GraphNode;

public class ClassModel {
	private List<Type> types = new ArrayList<>();

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}

	public void enhance(Map<GraphNode, List<GraphNode>> accessibilityMap) {
		// TODO Darien
		
	}

	public void transformToCode() {
		// TODO Auto-generated method stub
		
	}
	
	
}
