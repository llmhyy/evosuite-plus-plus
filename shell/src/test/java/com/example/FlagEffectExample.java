package com.example;

public class FlagEffectExample {
	public int CallerBasicEqualString() {
		String x = CalleeReturnConstantString(10);
		String y = "Test";
		if (x == y) {
			return 77;
		}
		
		return 88;
	}
	
	public int CallerBasicNotEqualString() {
		String x = CalleeReturnConstantString(10);
		String y = "Test";
		if (x != y) {
			return 77;
		}
		
		return 88;
	}
	
	public int CallerBasicGreaterInt() {
		int x = CalleeReturnConstantInt(10);
		int y = 999;
		if (x > y) {
			return 77;
		}
		
		return 88;
	}
	
	public int CallerBasicGreaterEqualInt() {
		int x = CalleeReturnConstantInt(10);
		int y = 999;
		if (x >= y) {
			return 77;
		}
		
		return 88;
	}
	
	public int CallerBasicLesserInt() {
		int x = CalleeReturnConstantInt(10);
		int y = 999;
		if (x < y) {
			return 77;
		}
		
		return 88;
	}
	
	public int CallerBasicLesserEqualInt() {
		int x = CalleeReturnConstantInt(10);
		int y = 999;
		if (x <= y) {
			return 77;
		}
		
		return 88;
	}
	
	public String CalleeReturnConstantString(int x) {
		if (x > 10) {
			return "Test1";
		}
		return "Test2";
	}
	
	public int CalleeReturnConstantInt(int x) {
		if (x > 10) {
			return 20;
		}
		return 30;
	}
	
	public int CallerBasicMethodCallOperand() {
		int y = 999;
		if (CalleeReturnConstantInt(10) == y) {
			return 77;
		}
		return 88;
	}
	
	public int CallerAdvanced() {
		int y = 999;
		if (CalleeReturnVariable(10) == y) {
			return 77;
		}
		return 88;
	}
	
	public int CalleeReturnVariable(int x) {
		int z;
		if (x > 10) {
			z = 10;;
		} else {
			z = 20;
		}
		return z;
	}
	
	public int CallerBasicObject() {
		Student x = CalleeReturnObject(10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}
	
	public Student CalleeReturnObject(int x) {
		if (x > 10) {
			return new Student("Test1", x);
		}
		return new Student("Test2", 10);
	}
	
	public class Student {
		public String name;
		public int age;
		
		public Student(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}
}
