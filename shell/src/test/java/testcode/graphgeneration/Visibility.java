package testcode.graphgeneration;

public enum Visibility {
	PUBLIC("public"),
	PRIVATE("private"),
	PROTECTED("protected"),
	DEFAULT("");
	
	String stringRepresentation;
	Visibility(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}
	
	@Override
	public String toString() {
		return this.stringRepresentation;
	}
}
