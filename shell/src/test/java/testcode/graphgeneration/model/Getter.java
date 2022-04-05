package testcode.graphgeneration.model;

public class Getter extends Method {
	private Field returnedField;
	
	public Getter(String declaringClass, String name, String returnType, Field returnedField) {
		super(declaringClass, name, returnType);
		if (returnedField == null) {
			throw new IllegalArgumentException("Cannot have a null returned field for a getter!");
		}
		// TODO Auto-generated constructor stub
		this.returnedField = returnedField;
	}
	
	public Field getReturnedField() {
		return returnedField;
	}
}
