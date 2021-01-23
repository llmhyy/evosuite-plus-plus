package feature.smartseed.example;

public class SmartSeedExample {
	
	public static String[] noise = 
		{"abc0", "abc1", "abc2", "abc3", "abc4", "abc5", "abc6", "abc7", "abc8", "abc9", "abc10"};
	
	public void dynamicExample1(int x, int y) {
		double z = Math.floor((y + 999999) / 1333);
		if (x == z) {
			System.currentTimeMillis();
		}
	}

	public void dynamicExample2(int x, int y) {
		if (x / 20000 == 345) {
			System.currentTimeMillis();
		}
	}

	public void staticExample1(int x, int y) {
		if (x == 34500000) {
			System.currentTimeMillis();
		}
	}

	public void staticExample2(String x, int y) {
		if (x.equals("34500000")) {
			System.currentTimeMillis();
		}
	}

	public void staticExample3(String x, int y) {
		String a = x.substring(0, x.length() - 3);
		if (a.equals("it is a difficult string")) {
			System.currentTimeMillis();
		}
	}

	
}
