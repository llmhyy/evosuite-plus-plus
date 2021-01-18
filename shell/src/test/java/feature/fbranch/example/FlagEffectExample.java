package feature.fbranch.example;

public class FlagEffectExample {
	public int example1(int x, int y) {
		int a = x + y;
		if (callFlag(a)) {
			return 0;
		}
		return 1;
	}

	public boolean callFlag(int x) {
		if (x/1000 > 100) {
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
}
