package regression.branchenhancement.example;

/**
 * This example requires a very large number and multiple exceptions
 *
 */
public class MultipleExceptionsExample {

	public int test(int a, int b) throws Exception {
		if (a < b) {
			t(a, b);
			rangeCheck(a, b);
			rangeCheck1(a, b);
			if (b > 14000) {
				return 1;
			}
		} 
		
		return -1;
	}

	public void t(int a, int b) throws Exception {
		if (a != 0) {
			a();
		}
	}

	public void a() throws Exception {
		throw new Exception("Test");

	}

	public static void rangeCheck(int a, int b) {
		rangeCheck1(a, b);
		if (b <= 10000) {
			throw new IllegalArgumentException("b needs to be larger than 10000");
		}
		if (b <= 12000) {
			throw new IllegalArgumentException("b needs to be larger than 12000");
		}
	}

	public static void rangeCheck1(int a, int b) {
		if (b <= 7000) {
			throw new IllegalArgumentException("b needs to be larger than 7000");
		}
	}
}
