package com.example;

public class FlagEffectExample {
	/** PRIMITIVE RETURN CONSTANT **/

	public int CallerStringReturnConstantEqual() {
		String x = CalleeStringReturnConstant(10);
		String y = "Test";
		if (x == y) {
			return 77;
		}
		return 88;
	}

	public int CallerStringReturnConstantNotEqual() {
		String x = CalleeStringReturnConstant(10);
		String y = "Test";
		if (x != y) {
			return 77;
		}
		return 88;
	}

	public int CallerIntReturnConstantGreater() {
		int x = CalleeIntReturnConstant(10);
		int y = 999;
		if (x > y) {
			return 77;
		}
		return 88;
	}

	public int CallerIntReturnConstantGreaterEqual() {
		int x = CalleeIntReturnConstant(10);
		int y = 999;
		if (x >= y) {
			return 77;
		}
		return 88;
	}

	public int CallerIntReturnConstantLesser() {
		int x = CalleeIntReturnConstant(10);
		int y = 999;
		if (x < y) {
			return 77;
		}
		return 88;
	}

	public int CallerIntReturnConstantGreaterLesserEqual() {
		int x = CalleeIntReturnConstant(10);
		int y = 999;
		if (x <= y) {
			return 77;
		}
		return 88;
	}

	public int CallerIntReturnConstantMethodCall() {
		int y = 999;
		if (CalleeIntReturnConstant(10) == y) {
			return 77;
		}
		return 88;
	}

	public String CalleeStringReturnConstant(int x) {
		if (x > 10) {
			return "Test1";
		}
		return "Test2";
	}

	public int CalleeIntReturnConstant(int x) {
		if (x > 10) {
			return 20;
		}
		return 30;
	}

	/** PRIMITIVE RETURN VARIABLE **/

	public int CallerIntReturnVariablePositive() {
		int y = 999;
		if (CalleeReturnVariablePositive(10) == y) {
			return 77;
		}
		return 88;
	}

	public int CalleeReturnVariablePositive(int x) {
		int z;
		if (x > 10) {
			z = 10;
		} else {
			z = 20;
		}
		return z;
	}

	public int CallerIntReturnVariableNegative() {
		int y = 999;
		if (CalleeReturnVariableNegative(10) == y) {
			return 77;
		}
		return 88;
	}

	public int CalleeReturnVariableNegative(int x) {
		int z;
		if (x > 10) {
			z = x * x;
		} else {
			z = x + x + x;
		}
		return z;
	}

	/** OBJECT RETURN CONSTANT **/

	public int CallerObjectReturnConstantLeft() {
		Student x = CalleeObjectReturnConstantLeft(10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}

	public Student CalleeObjectReturnConstantLeft(int x) {
		if (x > 10) {
			return new Student("Test1", x);
		}
		return new Student("Test2", x);
	}

	public int CallerObjectReturnConstantRight() {
		Student x = CalleeObjectReturnConstantRight("Test1", 10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}

	public Student CalleeObjectReturnConstantRight(String x, int y) {
		if (y > 10) {
			return new Student(x, 20);
		}
		return new Student(x, 10);
	}

	/** OBJECT RETURN VARIABLE PARAM **/

	public int CallerObjectReturnVariableParamPositive() {
		Student x = CalleeObjectReturnVariableParamPositive(10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}

	public Student CalleeObjectReturnVariableParamPositive(int x) {
		String y;
		if (x > 10) {
			y = "test1";
		} else {
			y = "test2";
		}

		return new Student(y, x);
	}

	public int CallerObjectReturnVariableParamNegative() {
		Student x = CalleeObjectReturnVariableParamNegative("Test", 10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}

	public Student CalleeObjectReturnVariableParamNegative(String x, int y) {
		int age = y;
		if (y > 10) {
			age += 20;
		} else {
			age += 10;
		}
		return new Student(x, age);
	}

	/** OBJECT RETURN VARIABLE **/

	public int CallerObjectReturnVariablePositive() {
		Student x = CalleeObjectReturnVariablePositive(10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}

	public Student CalleeObjectReturnVariablePositive(int x) {
		Student y;
		if (x > 10) {
			y = new Student("test1", x);
		} else {
			y = new Student("test2", x);
		}

		return y;
	}

	public int CallerObjectReturnVariableNegative() {
		Student x = CalleeObjectReturnVariableNegative("Test", 10);
		int y = 999;
		if (x.age == y) {
			return 77;
		}
		return 88;
	}

	public Student CalleeObjectReturnVariableNegative(String x, int y) {
		Student z;
		if (y > 10) {
			y += 10;
			z = new Student(x, y);
		} else {
			z = new Student(x, y);
		}
		return z;
	}

	/** RETURN METHOD CALL **/

	public int CallerReturnMethodCall() {
		int y = CalleeIntReturnMethodCall(10);
		if (y > 1000) {
			return 77;
		}
		return 88;
	}

	public int CalleeIntReturnMethodCall(int x) {
		if (x > 10) {
			return CalleeIntReturnConstant(100);
		}
		return CalleeIntReturnConstant(10);
	}

	public int CallerReturnVariableMethodCall() {
		int y = CalleeIntReturnVariableMethodCall(10);
		if (y > 1000) {
			return 77;
		}
		return 88;
	}

	public int CalleeIntReturnVariableMethodCall(int x) {
		int y;
		if (x > 10) {
			y = CalleeIntReturnConstant(100);
		} else {
			y = CalleeIntReturnConstant(1000);
		}
		return y;
	}

	/** EXAMPLE CLASSES **/

	public class Student {
		public String name;
		public int age;

		public Student(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}
}
