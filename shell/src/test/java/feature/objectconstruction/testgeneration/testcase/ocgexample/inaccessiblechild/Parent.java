package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild;

public class Parent {
	private Child child = new Child();
	
	public Grandchild getGrandchild() {
		return child.grandchild;
	}
	
	public Grandchild getGrandchild2() {
		return this.child.getGrandchild();
	}
	
	public Grandchild getGrandchild3() {
		Grandchild grandchild = this.child.getGrandchild();
		return grandchild;
	}
	
	public Grandchild notGetter() {
		return new Grandchild();
	}
	
	public Grandchild notGetter2() {
		Grandchild grandchild = this.child.getGrandchild();
		return new Grandchild();
	}
	
	public void method() {
		if ((this.child.grandchild.integer != 0) 
				&& (this.child.grandchild.greatGrandchild.integer != 0)) {
			System.currentTimeMillis();
		}
	}
}
