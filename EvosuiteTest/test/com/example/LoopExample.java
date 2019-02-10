package com.example;

public class LoopExample {
	public boolean test(int[] a, int[] b) {
		if(a==b) {
			return false;
		}
		
		if(a.length < 3 || b.length < 3) {
			return false;
		}
		
		for(int i=0; i<2; i++) {
			if(Util2.equal(a, b)) {
				return true;
			}			
		}
		
		return false;
	}
}
