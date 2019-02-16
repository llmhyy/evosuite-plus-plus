package com.example;

public class LoopExample {
	public boolean test(int[] a, int b) {
		if(!LoopUtil.checkPrecondition(a, b)) {
			return false;
		}
		
		for(int i=0; i<a.length; i++) {
			if(LoopUtil.contains(a, i, b)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		LoopExample example = new LoopExample();
		example.test(new int[] {0, 1}, 0);
	}
}
