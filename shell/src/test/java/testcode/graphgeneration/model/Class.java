package testcode.graphgeneration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract representation of concrete class?
 * Might need to support interface in the future ("abstract" classes)
 */
public class Class extends CodeElement{
	private final String name;
	
	private List<Field> fields;
	private List<Method> methods;
	
	public Class(String name) {
		super();
		this.name = name;
		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();
	}

	public String getName() {
		return name;
	}
	
	public void addField(Field field) {
		if (field == null) {
			System.err.println("WARNING: Attempted to add a null field to " + this.getName() + "!");
			return;
		}
		this.fields.add(field);
	}
	
	public void addMethod(Method method) {
		if (method == null) {
			System.err.println("WARNING: Attempted to add a null method to " + this.getName() + "!");
			return;
		}
		this.methods.add(method);
	}
	
	public List<Field> getFields() {
		return new ArrayList<>(fields);
	}
	
	public List<Method> getMethods() {
		return new ArrayList<>(methods);
	}
	
	@Override
	public String toString() {
		return "(" + this.getName() + ",\n fields: " + this.getFields() + ",\n methods: " + this.getMethods() + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Class other = (Class) obj;
		return Objects.equals(name, other.name);
	}
	
	
}
