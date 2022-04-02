package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild2;

public class Child {
	Grandchild grandchild;
	
	public Child(Grandchild grandchild) {
		this.grandchild = grandchild;
	}
	
	public Grandchild getGrandchild() {
		return grandchild;
	}
}
