package com.test;

public class Util {
//	private static int bigInt = 100000000;
	
	static int log(int x, int base){
		return (int) (Math.log(x) / Math.log(base));
	}
	
	
	
	public static boolean compare(int k, int a){
		return a>k && a<k+100;
	}
	
	public static int multiply(int k1, int k2){
		return k1 * k2;
	}
	
	public static boolean isInScope(int a, int b) {
		int k = Util.multiply(100, 100);
		
		if(b<150 && b>0){
			if(a>0 && a<k+100){
//				return true;
				if(a>100 && a<k){
					if(b>100){
						return true;
					}
				}
				else{
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isCornerCase(int a, int b){
//		return a-b>8;
		return a-b <= -49;
	}
	
	public static boolean isOk(int a, int b){
		if(Math.pow(a, 2)<=625){
			if(Math.pow(b, 2)<=625){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isInScope0(int a, int b){
		if(Math.pow(a, 2)*100<=2500){
			if(Math.pow(b, 2)*100<=2500){
				return true;
			}
		}
		
		return false;
	}

	public static boolean compare1(int a, int b) {
//		return a*100>b*100+98;
		return a==12341;
	}

	public static boolean compare2(int a, int b) {
//		return a*100<b*100+300;
		return b==2345;
	}
}
