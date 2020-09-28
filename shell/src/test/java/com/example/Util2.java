package com.example;

import java.util.ArrayList;
import java.util.List;

public class Util2 {
	public static boolean equal(int[] a, int[] b) {
		int size1 = a.length;
		int size2 = b.length;
		
		if(size1 != size2)
			return false;
		
		for(int i=0; i<size1; i++) {
			if(a[i] != b[i]) {
				return false;
			}
		}
		
		return true;
	}
	
	
	public static boolean test() {
		List<String> list = new ArrayList<String>();
		for(String str: list) {
			if(str.length() > 100) {
				return false;
			}
		}
		
		return true;
	}
}
