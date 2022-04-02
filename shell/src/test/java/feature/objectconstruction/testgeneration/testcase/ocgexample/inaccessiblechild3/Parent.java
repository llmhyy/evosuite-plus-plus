package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild3;

public class Parent {
	private Child child;
	
	public Parent(Child child) {
		this.child = child;
	}
	
	public Grandchild getGrandchild() {
		return this.child.getGrandchild();
	}
	
	public void method() {
		if ((this.child.getGrandchild().getInteger() != 0) && (this.child.getGrandchild().getGreatGrandchild().getInteger() != 0)) {
			System.currentTimeMillis();
		}
	}
}
