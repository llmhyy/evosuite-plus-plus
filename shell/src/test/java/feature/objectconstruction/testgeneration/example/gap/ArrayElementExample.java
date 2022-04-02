package feature.objectconstruction.testgeneration.example.gap;

import java.util.ArrayList;

public class ArrayElementExample {
	public ArrayList<String> strList;
	
	public void arrayElementAccess() {
		boolean b = strList.remove("a very special string");
		if (b) {
			System.currentTimeMillis();
		}
	}
	
}
