package instrumenter;

public class ExceptionMockExample {

//	public void method(int i) {
//		if (i == 0) {
//			throw new IllegalArgumentException();
//			// System.out.println("Exception!");
//		}
//		System.out.println("Finish!");
//	}

	public double nextDouble(double alpha, double lambda) {
		double a = alpha;
		if (a <= 0.0)
			throw new IllegalArgumentException();
		if (lambda <= 0.0)
			new IllegalArgumentException();
		return 1;
	}
	
	public void checkSize(int size, int bSize) {
		if (size != bSize) throw new IllegalArgumentException("Incompatible sizes: "+ size +" and "+ bSize);
	}
}
