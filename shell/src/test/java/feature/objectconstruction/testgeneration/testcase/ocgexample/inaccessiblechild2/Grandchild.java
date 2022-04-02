package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild2;


public class Grandchild {
	int integer = 0;
	GreatGrandchild greatGrandchild;
	
	public Grandchild(GreatGrandchild greatGrandchild) {
		this.greatGrandchild = greatGrandchild;
	}
	
	public void setInteger(int int0) {
		integer = int0;
	}
	
	public GreatGrandchild getGreatGrandchild() {
		return greatGrandchild;
	}
}
