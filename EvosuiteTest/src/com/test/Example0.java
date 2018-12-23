package com.test;

public class Example0 {
	public boolean test(int a, int b){
		if(Util.isOk(a, b)){
			if(Util0.isCornerCase(a, b)){
				return true;
			}	
		}
		
		return false;
	}
}
