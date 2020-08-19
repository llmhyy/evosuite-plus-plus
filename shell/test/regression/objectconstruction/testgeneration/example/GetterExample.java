package regression.objectconstruction.testgeneration.example;

import regression.objectconstruction.testgeneration.example.cascadecall.Field1;

public class GetterExample {
	private Field1 field;
	
	public GetterExample() {
		
	}
	
	public boolean targetM() {
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
