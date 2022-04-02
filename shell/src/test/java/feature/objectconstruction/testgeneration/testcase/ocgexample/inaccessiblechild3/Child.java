package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild3;

public class Child {
	private Grandchild grandchild = new Grandchild();
	
	public Grandchild getGrandchild() {
		return grandchild;
	}
}
