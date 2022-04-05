package testcode.graphgeneration.model;

public class MethodArrayElementSetter extends Method {
	private Method arraySource;
	
	public MethodArrayElementSetter(String declaringClass, String name, String returnType, Method arraySource) {
		super(declaringClass, name, returnType);
		this.arraySource = arraySource;
	}
	
	public Method getArray() {
		return this.arraySource;
	}
}
