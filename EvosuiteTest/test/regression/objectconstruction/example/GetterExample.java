package regression.objectconstruction.example;

public class GetterExample {
	private Field1 field;
	
	public GetterExample() {
		
	}
	
	public boolean target() {
		if (field.getField2().getValue() > 1) {
			return true;
		}
		return false;
	}
	
	public Field1 getF1(Field1 f) {
		return getF2(f);
	}
	
	private Field1 getF2(Field1 f) {
		return field;
	}
}
