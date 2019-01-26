package com.example;

public class Example {
	public boolean test(int a, int b){
		Util u = new Util();
		if(u.isOk(a, b)){
			if(u.isCornerCase(a, b)){
				return true;
			}	
		}
		
		return false;
	}
	
	
}
