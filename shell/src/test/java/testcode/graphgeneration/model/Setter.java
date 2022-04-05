package testcode.graphgeneration.model;

public class Setter extends Method {
	private Field setField;
	
	public Setter(String declaringClass, String name, String returnType, Field setField) {
		super(declaringClass, name, returnType);
		if (setField == null) {
			throw new IllegalArgumentException("Cannot have a null set field for a setter!");
		}
		// TODO Auto-generated constructor stub
		this.setField = setField;
	}
	
	public Field getSetField() {
		return setField;
	}
}
