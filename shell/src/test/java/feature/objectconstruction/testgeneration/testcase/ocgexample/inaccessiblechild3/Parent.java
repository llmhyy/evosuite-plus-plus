package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild3;

public class Parent {
	private Child child;
	
	public Parent(Child child) {
		this.child = child;
	}
	
	public Grandchild getGrandchild() {
		return this.child.grandchild;
	}
	
	public void method() {
		if ((this.child.grandchild.integer != 0) && (this.child.grandchild.greatGrandchild.integer != 0)) {
			System.currentTimeMillis();
		}
	}
}
