package regression.objectconstruction.testgeneration.example;

import regression.objectconstruction.testgeneration.example.cascadecall.ClassA;

public class ArrayGenerationExample {
	
	private ClassA[] arrayToTest;
	
	public void targetM() {
		if (arrayToTest[4].c.e.field > 10) {
			return;
		}
	}
	
	public void setArrayElement(ClassA newClass) {
		arrayToTest[3] = newClass;
	}
	
	public ClassA getArrayElement() {
		return arrayToTest[2];
	}
	
	public void setArray(ClassA[] newArray) {
		arrayToTest = newArray;
	}
	
	public ClassA[] getArray() {
		return arrayToTest;
	}
}
