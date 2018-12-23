package com.test;

public class ExampleInline {
	public boolean test(int a, int b){
		if(Util.isOk(a, b)){
			if(a-b < -49){
				return true;
			}	
		}
		
		return false;
	}
}
