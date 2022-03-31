package feature.objectconstruction.testgeneration.testcase.ocgexample.arraylistexample;

import java.util.ArrayList;
import java.util.List;

public class Parent {
	List<Boolean> values = new ArrayList<>();

	public void callMe() {
		values.add(0, true);
	}
	
	public void method() {
		if (values.get(0)) {
			System.currentTimeMillis();
		}
	}
}
