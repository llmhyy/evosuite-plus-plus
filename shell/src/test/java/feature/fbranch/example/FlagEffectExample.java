package feature.fbranch.example;

import feature.objectconstruction.testgeneration.example.Student;

public class FlagEffectExample {
	public int example1(int x, int y) {
		int a = x + y;
		if (callFlag(a)) {
			return 123;
		}
		return 456;
	}

	public boolean callFlag(int x) {
		if (x / 1000 > 100) {
			return true;
		} else {
			return false;
		}
	}

	public int example2(int x, int y) {
		int a = x % y;
		int b = a / 5;
		if (callFlag(b)) {
			return 0;
		}
		return 1;
	}

	public int example3(int x, int y) {
		int a = x % y;
		a /= 5;
		if (callFlag(a)) {
			return 0;
		}
		return 1;
	}

	public int example4(double x, double y) {
		int a = (int) x;
		if (callFlag(a)) {
			return 0;
		}
		return 1;
	}

	public int example5(int x, int y) {
		int a = x + y;
		if (x > 10) {
			a = x / y;
		} else {
			a = x - y;
		}
		if (callFlag(a)) {
			return 0;
		}
		return 1;
	}

	public int example6(int x, int y) {
		int a = x + y;
		if (a > 10) {
			return 0;
		}
		return 1;
	}

	public int example7(Student x, int y) {
		int a = x.getHeight() + y;
		if (a > 10) {
			return 999;
		}
		return 1;
	}

	public int example8(Student x, int y) {
		int a = x.getHeight() + y;
		a %= 10;
		a -= x.getAge();
		if (callFlag(a)) {
			return 0;
		}
		return 1;
	}
	
	public int example9(Student x, int y) {
		x.setHeight(y);
		int a = x.getHeight();
		
		if (a > 100000) {
			return 0;
		}
		return 1;
	}
}
