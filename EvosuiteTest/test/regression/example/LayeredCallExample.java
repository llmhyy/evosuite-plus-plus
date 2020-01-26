package regression.example;

/**
 * This example involves multi-layered method call
 *
 */
public class LayeredCallExample {

	public int targetM(int f1, int f2, int f3) throws Exception {

		m10(f1, f2, f3);
		m20(f2, f3);

		if (f2 - f1 < 5) {
			if (f2 == f1) {
				return 1;
			}
		}
		return -1;
	}

	public void m10(int f1, int f2, int f3) throws Exception {
		m11(f1, f2, f3);
	}

	public void m11(int f1, int f2, int f3) throws Exception {
		m12(f1, f2, f3);
		if (f1 + f2 + f3 < 16000) {
			throw new Exception("m11 exception");
		}
	}

	public void m12(int f1, int f2, int f3) throws Exception {
		if (f1 + f2 + f3 < 8000) {
			throw new Exception("m12 exception");
		}
	}

	public void m20(int f2, int f3) throws Exception {
		m21(f2, f3);
	}

	public void m21(int f2, int f3) throws Exception {
		if (Math.abs(f3 - f2) > 20) {
			throw new Exception("m21 exception");
		}
	}

	public void m22() {
		// TODO Auto-generated method stub
	}
}
