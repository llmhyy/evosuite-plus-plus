package feature.smartseed.example.truecase;

import java.util.HashMap;

public class TrueExample {
	private HashMap map = new HashMap<>();
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
		{"abc0", "abc1", "abc2", "abc3", "abc4", "abc5", "abc6", "abc7", "abc8", "abc9", "abc10",
		"5abc0", "5abc1", "5abc2", "5abc3", "5abc4", "5abc5", "5abc6", "5abc7", "5abc8", "5abc9", "5abc10",
		"6abc0", "6abc1", "6abc2", "6abc3", "6abc4", "6abc5", "6abc6", "6abc7", "6abc8", "6abc9", "6abc10",
		"7abc0", "7abc1", "7abc2", "7abc3", "7abc4", "7abc5", "7abc6", "7abc7", "7abc8", "7abc9", "7abc10",
		"8abc0", "8abc1", "8abc2", "8abc3", "8abc4", "8abc5", "8abc6", "8abc7", "8abc8", "8abc9", "8abc10",
		"9abc0", "9abc1", "9abc2", "9abc3", "9abc4", "9abc5", "9abc6", "9abc7", "9abc8", "9abc9", "9abc10",};
	
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
		for (int ai : a) {
			if (ai == 11111111) {
				System.currentTimeMillis();
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
			if(o.name.equals("noise".toUpperCase())) {
				System.currentTimeMillis();
			}
		}
	}

	public void paralleltest(double x, int y) {
		if (x == 35.4 * 300) {
			System.currentTimeMillis();
		}
		if (y == -2147483646) {
			System.currentTimeMillis();
		} else if (y == 32700) {
			System.currentTimeMillis();
		} else if (y == 4) {
			System.currentTimeMillis();
		} else if (y == 120) {
			System.currentTimeMillis();
		}
	}
	
	public void stringCompare(String[] args) {
		if ("MERGE".equalsIgnoreCase(args[0])) {
			System.currentTimeMillis();
		}else if ("MIGRATE".equalsIgnoreCase(args[0])) {
			System.currentTimeMillis();
		}
	}
	
	public void specialPoint(String[] arsv) {
		if(arsv.length == 0) return;
		String op = arsv[0];
		if (arsv[arsv.length - 1].equals("-debug")) {
			System.currentTimeMillis();
		} else
			System.currentTimeMillis();

		if (op.equals("-add"))
			System.currentTimeMillis();
		else if (op.equals("-subtract"))
			System.currentTimeMillis();
		else if (op.equals("-multiply"))
			System.currentTimeMillis();
		else if (op.equals("-divide"))
			System.currentTimeMillis();
		else if (op.equals("-addconst"))
			System.currentTimeMillis();
		else if (op.equals("-subtractconst"))
			System.currentTimeMillis();
		else if (op.equals("-multiplyconst"))
			System.currentTimeMillis();
		else if (op.equals("-divideconst"))
			System.currentTimeMillis();
		else if (op.equals("-clear"))
			System.currentTimeMillis();
		else if (op.equals("-clip"))
			System.currentTimeMillis();
		else if (op.equals("-bytesize"))
			System.currentTimeMillis();
		else if (op.equals("-diff"))
			System.currentTimeMillis();
		else {
			System.currentTimeMillis();
		}
	}
}
