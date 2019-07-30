package com.example;

public class Example3 {
	//basic operation, invoke function, recursive, loop
	// == != >= <= < > >> << pow abs 
	public boolean test(int a,int b,int e,int f,String str,int c,int d,int[] keys,int value,int re) {
		//if(Util.checkN(re)) {
			//return true;
		//}
		//basic operation
		if(a!=b) {
			//a>?
			if(Util.checkD(a)) {
					//e==100000
					if(Util.checkB(e)) {
					//String operation
						if(str.indexOf('.')==6){
							//c^2<=10000 && d^2<=9801
							if(Util.isOk(c, d)){
								//a+b>=199,a==100 && b==99
								if(Util.isCornerCase(c, d)){
									if(b==10000) {
										return true;
									}	
								}
									
							}
						}
					}
			}
			
		}
		
		
		
		return false;
	}

}
