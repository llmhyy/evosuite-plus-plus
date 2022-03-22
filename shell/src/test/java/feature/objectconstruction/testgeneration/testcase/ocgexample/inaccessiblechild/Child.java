package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild;

public class Child {
	Grandchild grandchild = new Grandchild();
	
	public Grandchild getGrandchild() {
		return grandchild;
	}
}
