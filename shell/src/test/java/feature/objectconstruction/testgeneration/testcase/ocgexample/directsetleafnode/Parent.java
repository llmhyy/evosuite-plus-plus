package feature.objectconstruction.testgeneration.testcase.ocgexample.directsetleafnode;

public class Parent {
	private Child child = new Child();
	
	public void setInteger1(int int0) {
		this.child.grandchild.integer = int0;
	}
	
	public void setInteger2(int int0) {
		this.child.grandchild.greatGrandchild.integer = int0;
	}
	
	public void method() {
		if ((this.child.grandchild.integer != 0) && (this.child.grandchild.greatGrandchild.integer != 0)) {
			System.currentTimeMillis();
		}
	}
}
