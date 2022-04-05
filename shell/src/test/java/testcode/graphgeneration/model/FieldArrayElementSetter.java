package testcode.graphgeneration.model;

public class FieldArrayElementSetter extends Method {
	private Field arraySource;
	
	public FieldArrayElementSetter(String declaringClass, String name, String returnType, Field arraySource) {
		super(declaringClass, name, returnType);
		this.arraySource = arraySource;
	}
	
	public Field getArray() {
		return this.arraySource;
	}
}
