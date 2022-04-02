package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild3;

public class Grandchild {
	private int integer = 0;
	private GreatGrandchild greatGrandchild = new GreatGrandchild();
	
	public void setInteger(int int0) {
		integer = int0;
	}
	
	public int getInteger() {
		return integer;
	}
	
	public GreatGrandchild getGreatGrandchild() {
		return greatGrandchild;
	}
}
