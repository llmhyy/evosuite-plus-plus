package feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild;

import java.util.ArrayList;
import java.util.List;

public class Grandchild {
	int integer = 0;
	List<Object> someField;
	GreatGrandchild greatGrandchild = new GreatGrandchild();
	
	public void setGrandchildInteger(int int0) {
		greatGrandchild.setInteger(int0);
	}
	
	public void setInteger(int int0) {
		integer = int0;
	}
	
	public void setInteger2(Integer int0) {
		integer = int0;
	}
	
	public void setSomeField(ArrayList<Object> list0) {
		someField = list0;
	}
	
	public void notSetter(int int0) {
		int0 = int0 + 5;
		return;
	}
	
	public GreatGrandchild getGreatGrandchild() {
		return greatGrandchild;
	}
}
