package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild;

public class Parent {
	private Child child = new Child();
	
	public Grandchild getGrandchild() {
		return child.grandchild;
	}
	
	public void method() {
		if ((this.child.grandchild.integer != 0) && (this.child.grandchild.greatGrandchild.integer != 0)) {
			System.currentTimeMillis();
		}
	}
}
