package com.example;

public class LoopExample {
	public boolean test(int[] keys, int value) {
		if(!LoopUtil.checkPrecondition(keys, value)) {
			return false;
		}
		
		for(int i=0; i<keys.length; i++) {
			if(LoopUtil.contains(keys, i, value)) {
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
