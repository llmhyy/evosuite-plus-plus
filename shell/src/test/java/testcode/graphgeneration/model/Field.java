package testcode.graphgeneration.model;

import java.util.Arrays;

import testcode.graphgeneration.Graph;
import testcode.graphgeneration.Visibility;

public class Field {
	private Visibility visibility = Visibility.PRIVATE;
	private String name;
	private String dataType;

	public Field(String name, String dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public String toString() {
		return this.getVisibility() + " " + this.getDataType() + " " + this.getName(); 
	}
	
	public boolean isArray() {
		return this.dataType.endsWith("[]");
	}
	
	public boolean isPrimitive() {
		return Arrays.asList(Graph.PRIMITIVE_TYPES).contains(dataType);
	}
	
}
