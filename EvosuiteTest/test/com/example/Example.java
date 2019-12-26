package com.example;

public class Example {
//	public boolean test(int a, int b) {
//		if (Util.isOk(a, b)) {
//			if (Util.isCornerCase(a, b)) {
//				return true;
//			}
//		}
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
	
	public void test(int a, int b) {
		double c = Math.pow(a, 2);
		if (c > a) {
			return;
		}
	}
}