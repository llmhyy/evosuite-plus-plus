package testcode.graphgeneration.model;

import java.util.Arrays;
import java.util.Objects;

import testcode.graphgeneration.Graph;
import testcode.graphgeneration.Visibility;

public class Field extends CodeElement{
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

	@Override
	public int hashCode() {
		return Objects.hash(dataType, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		return Objects.equals(dataType, other.dataType) && Objects.equals(name, other.name);
	}
	
	
}
