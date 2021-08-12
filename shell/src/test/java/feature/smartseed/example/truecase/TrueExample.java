package feature.smartseed.example.truecase;

public class TrueExample {
	
	private int a[] = new int[]{1, 
			12321, 11312, 112312, 141321, 112312, 134,
			22321, 21312, 212312, 241321, 212312,234,
			32321, 31312, 312312, 341321, 312312,334,
			42321, 41312, 412312, 441321, 412312,434,
			52321, 51312, 512312, 541321, 512312,534,
			62321, 61312, 612312, 641321, 612312,634,
			72321, 71312, 712312, 741321, 712312,734,
			82321, 81312, 812312, 841321, 812312,834,
			92321, 91312, 912312, 941321, 912312,934,
			102321, 101312, 1012312, 41321, 12312,34,
			3};
	public static String[] noise = 
		{"abc0", "abc1", "abc2", "abc3", "abc4", "abc5", "abc6", "abc7", "abc8", "abc9", "abc10"};
	
	public static void test(int x, int y, int z) {
		if(x==123456789) {
			if(y==987654321) {
				if(z==555555555) {
					System.currentTimeMillis();
				}
			}
		}
	}
	
	public static void testArray(int a[], int b) {
		if (b == 38461965) {
			for (int ai : a) {
				if (ai == 123456789) {
					System.currentTimeMillis();
				}
			}
		}

	}
	
	public static void test(Obj o, int a) {
		if(a == 123456789) {
			if(o.getAttribute() == 101312) {
				System.currentTimeMillis();
			}
		}
	}
	
	public static void fieldTest(Obj o, int a) {
		if(a == 123456789) {
			if(o.name.equals("NoIse".toLowerCase())) {
				System.currentTimeMillis();
			}
		}
	}
}
