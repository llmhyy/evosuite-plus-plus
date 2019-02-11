package com.example;

public class LoopUtil {

	public static boolean contains(int[] a, int index, int b) {
		int key = (int) (b - (index + 365) * a.length + Math.pow(a.length, b));
		
		for(int i=0; i<a.length; i++) {
			if((key != 0 || a.length<10) && a[i] == key) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkPrecondition(int[] a, int b) {
		if(checkArrayRange(a)
				&& Math.pow(b, 2) < 100) {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkArrayRange(int[] a) {
		return a.length < 10;
	}

}
