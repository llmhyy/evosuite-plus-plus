package com.test;

import com.example.Util;

public class MutileCallTest {
	public boolean test(int a, int b, int c) {
		for(int i=0; i<c; i++) {
			if(Util.isOk(a, b)) {
				return true;
			}
			else {
				a = a/2;
				b = b/2;
			}
		}
		
		return false;
	}
}
