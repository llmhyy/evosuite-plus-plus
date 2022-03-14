package feature.objectconstruction.testgeneration.testcase.ocgexample.longgettercase;

public class Parent {
	private Child child = new Child();
	
	public GreatGrandchild getGreatGrandchild() {
		return this.child.grandchild.greatGrandchild;
	}
	
	public void method() {
		if (this.child.grandchild.greatGrandchild.integer != 0) {
			System.currentTimeMillis();
		}
	}
}
