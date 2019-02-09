package com.example;


public class Example {
	public boolean test(int a, int b){
		if(Util.isOk(a, b)){
			if(Util.isCornerCase(a, b)){
				return true;
			}	
		}
		
		return false;
	}
	
	public boolean test2(Obj o, int a, int b) {
		if(o.isbField()) {
			if(Util.isOk(a, b)){
				if(Util.isCornerCase(a, b)){
					return true;
				}	
			}
		}
		
		return false;
	}
}
