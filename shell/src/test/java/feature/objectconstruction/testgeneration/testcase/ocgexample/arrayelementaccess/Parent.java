package feature.objectconstruction.testgeneration.testcase.ocgexample.arrayelementaccess;

public class Parent {
	private Child child = new Child();
	
	public Grandchild getGrandchild() {
		return child.grandchild;
	}
	
	public void method(int index) {
		if (this.child.grandchild.integerArray[index] != 0) {
			System.currentTimeMillis();
		}
	}
}
