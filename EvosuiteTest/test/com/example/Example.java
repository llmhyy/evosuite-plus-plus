package com.example;

public class Example {

	Test test;
	static Test test1 = new Test(1, 2);
	private int c;
	private Helper x = new Helper();
//	
//	private int field1;
//	private int field2;
//	private Example example;
//
//	public void setTest() {
//		this.test = new Test(1, 2);
//	}
//
//	public void setTest1() {
//		test1 = new Test(2, 3);
//	}
//
	public void test2(int num1, Student s) {
		int c = test1.getA() + s.getAge();
		if (x.test.isOk(num1, c)) {
			return;
		}
	}
	
	public boolean test(Student student){
		if(Util.isOk(student.getAge(), 
				student.getFriend().getAge())){
			if(Util.isCornerCase(student.getAge(), 
					student.getFriend().getAge())){
				return true;
			}	
		}
		
		return false;
	}

//	public Example(int num1) {
//		if (num1 < 15000) {
//			throw new IllegalArgumentException("Constructor 1 : num1 needs to be larger than 10000");
//		}
//		this.field1 = num1;
//	}
//	
//	public Example(int num1, int num2) {
//		if (num1 < 10000) {
//			throw new IllegalArgumentException("Constructor 2 : num2 needs to be larger than 10000");
//		}
//		this.field1 = num1;
//		this.field2 = num2;
//	}
	
//	public int test0(int a, int b) {
//		if (example.field1 < b) {
//			rangeCheck(a, b);
////			rangeCheck1(a, b);
//			if (b - a < 10) {
//				if (b - a < 3) {
//					return a;
//				}
//			}
//		}
//		return -1;
//	}
	
//	public int test(int a, int b) {
//		if (a < b) {
//			rangeCheck(a, b);
////			rangeCheck1(a, b);
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
//		rangeCheck1(a, b);
//		if (b <= 8000) {
//			throw new IllegalArgumentException("TEST");
//		}
//		if (b <= 9000) {
//			throw new IllegalArgumentException("TTT");
//		}
//	}
//
//	public static void rangeCheck1(int a, int b) {
//		if (b <= 7000) {
//			throw new IllegalArgumentException("TESTTEST");
//		}
//	}

}