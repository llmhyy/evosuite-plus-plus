package com.test;

public class Example {
	public static void main(String[] args) throws Exception {
		Example ex = new Example();
		//ex.foo(2, 1);
		ex.test(0, 0, 0);
	}

//	public int foo(int x, int y) {
//		int z = x + y;
//		if (z > 0) {
//			z = 1;
//		}
//		if (x < 5) {
//			z = -z;
//		} else if (x < 4) {
//			z++;
//		}
//		return z;
//	}
	
//	public void test(int x) {
//		int y = 1+2*x;
//		if(y==5000001) {
//			System.currentTimeMillis();
//		}
//		else {
//			System.currentTimeMillis();
//		}
//		
////		if(1/x+1==1.01) {
////			System.currentTimeMillis();
////		}
////		else {
////			System.currentTimeMillis();
////		}
//	}
	
	public void ttest0(double x, double y, int z){
		if(x-y<0){
			return;
		}
	}
	
	public void test(double x, double y, int z){
		if(x-y<0){
			if(y>10){
				if(y<16){
					if(x>5){
						if(x<20){
							if(z<3){
								if(z>0){
									System.currentTimeMillis();
									System.currentTimeMillis();
								}
							}
						}
					}
				}
			}
		}
	}
//	
//	public void test0(double x, double y, int z){
//		double k = x;
//		if(k>5 && 
//				y>100000){
//			if(k<5.0001 && 
//					y<100001){
//				System.currentTimeMillis();
//			}
//		}
//	}
//	
//	private double f(double x) {
//		return x;
//	}

}
