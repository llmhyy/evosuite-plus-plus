package com.test;

public class Example2 {
//	private int bigInt = 100000000;
	
	public int test(int a, int b){
		if(Util.isInScope(a, b)){
			int k = Util.multiply(100, 100);
			if(b<50){
				if(Util.compare(k, a)){
					return 1;
				}
			}
		}
		
		return 0;
	}

	
}
