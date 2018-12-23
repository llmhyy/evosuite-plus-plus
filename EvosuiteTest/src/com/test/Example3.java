package com.test;

public class Example3 {
	public int test(int a, int b) {
		if (Util.compare1(a, b)) {
			if (Util.compare2(a, b)) {
				return 0;
			}
		}

		return 1;
	}

//	public int test(int a, int b) {
//		if (a==1234) {
//			if (b==2345) {
//				return 0;
//			}
//		}
//
//		return 1;
//	}
}
