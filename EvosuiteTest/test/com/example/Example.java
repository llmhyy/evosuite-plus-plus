package com.example;

public class Example {

	Test test;
	static Test test1 = new Test(1, 2);
	private int c;
	private Helper x = new Helper();
	
	private int field1;
	private int field2;
	private Example example;

//	public boolean test(int a, int b) {
//		if (Util.isOk(a, b)) {
//			if (Util.isCornerCase(a, b)) {
//				return true;
//			}
//		}

	
//	public boolean test(int a, int b){
//		if(Util.isOk(a, b)){
//			if(Util.isCornerCase(a, b)){
//				return true;
//			}	
//		}
//		
//		return false;
//	}
//	
//	public boolean test2(Obj o, int a, int b) {
//		if(o.isbField()) {
//			if(Util.isOk(a, b)){
//				if(Util.isCornerCase(a, b)){
//					return true;
//				}	
//			}
//		}
//		
//		return false;
//	}

//	public int test(int a, int b) {
//		if (a < b) {
//			if (b - a < 2) {
//				return a;
//			}
//		}
//		throw new IllegalArgumentException("TEST");
//
//	}

//	public static int test(int a, int b) {
//		
//		if (a < b) {
//			rangeCheck(a, b);
//			rangeCheck1(a, b);
//			if (b - a < 10) {
//				if (b - a < 3) {
//					return a;
//				}
//			}
//		}
//		return -1;
//	}
//	
//	public static void rangeCheck(int a, int b) {
//		if (b <= 5000) {
//			throw new IllegalArgumentException("TEST");
//		}
//	}
//	
//	public static void rangeCheck1(int a, int b) {
//		if (b <= 7000) {
//			throw new IllegalArgumentException("TESTTEST");
//		}
//	}

//	public static boolean test(int a, int b) {
//
//		if (a >= b) {
//			if (a - b <= 100) {
//				if (a - b <= 10) {
//					a++;
//				}
//			}
//		}
//		return false;
//	}
//
//	public boolean test2(Obj o, int a, int b) {
//		if (o.isbField()) {
//			if (Util.isOk(a, b)) {
//				if (Util.isCornerCase(a, b)) {
//					return true;
//				}
//
//				if (a < b) {
//					if (b - a <= 100) {
//						if (b - a <= 10) {
//							b++;
//						}
//					}
//
//					return false;
//				}
//			}
//		}
//		return false;
//	}

//	public void test(Test testAA, int b) {
////		int e = test1.a;
////		int e = test1.helper(a);
////		double c = 5.0;
//		if (testAA.a > b) {
//			return;
//		}
//	}

//	public void test1(Test testAA, int b) {
////		int e = test1.a;
////		int e = test1.helper(a);
////		double c = 5.0;
//		int[] c = new int[10];
//		test1.helper1(c);
////		if (testAA.a > c[5]) {
//		if (c[5] > b) {
//			return;
//		}
//	}
	
	public void setTest() {
		this.test = new Test(1, 2);
	}

	public void setTest1() {
		test1 = new Test(2, 3);
	}

	public void test2(int num1) {
		int c = test1.getA();
		if (x.test.isOk(num1, c)) {
			return;
		}
	}

	public Example(int num1) {
		if (num1 < 15000) {
			throw new IllegalArgumentException("Constructor 1 : num1 needs to be larger than 10000");
		}
		this.field1 = num1;
	}
	
	public Example(int num1, int num2) {
		if (num1 < 10000) {
			throw new IllegalArgumentException("Constructor 2 : num2 needs to be larger than 10000");
		}
		this.field1 = num1;
		this.field2 = num2;
	}
	
	public int test(int a, int b) {
		if (example.field1 < b) {
			rangeCheck(a, b);
//			rangeCheck1(a, b);
			if (b - a < 10) {
				if (b - a < 3) {
					return a;
				}
			}
		}
		return -1;
	}

	public static void rangeCheck(int a, int b) {
		if (b <= 10000) {
			throw new IllegalArgumentException("TEST");
		}
		if (b <= 20000) {
			throw new IllegalArgumentException("TTT");
		}
	}

	public static void rangeCheck1(int a, int b) {
		if (b <= 7000) {
			throw new IllegalArgumentException("TESTTEST");
		}
	}

}