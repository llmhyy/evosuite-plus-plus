package com.example;

import java.util.Arrays;

public class SearchExample {
	public boolean search(int x, int[] a) {
		Arrays.sort(a);
		
		if(x<a[0] || x>a[a.length-1]) {
			return false;
		}
		
		int mid = a[a.length/2];
		if(x==mid) {
			return true;
		}
		else if(x>mid) {
			int[] b = new int[mid];
			System.arraycopy(a, 0, b, 0, a.length/2);
			if(search(x, b))
				return true;
			else
				return false;
		}
		else if(x<mid) {
			int[] b = new int[mid];
			System.arraycopy(a, mid, b, mid, a.length/2);
			if(search(x, b))
				return true;
			else 
				return false;
		}
		
		return false;
	}
	
	
}
