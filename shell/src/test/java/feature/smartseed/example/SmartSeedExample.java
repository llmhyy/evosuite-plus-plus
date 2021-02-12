package feature.smartseed.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartSeedExample {
	
	public static String[] noise = 
		{"abc0", "abc1", "abc2", "abc3", "abc4", "abc5", "abc6", "abc7", "abc8", "abc9", "abc10"};
	public static int[] noiseInt = {
		-233, 15, 20, 90, 2000, 673, -992, 67, 500, 999999,34500010, 34500024, 34500009, 34500008, 34500006, 33500000	
	};
	public static long[] noiseLong = {
		8989l, 2147483690l, -5146483690l, 9223372036854775806l, -3372036854775808l 	
	};
	public static float[] noiseFloat = 
		{800f, -0.9f, -45.0999f, 0.9873f, 30000f, 0.989f, 0.963f};
	public static double[] noiseDouble = 
		{0.99, -23.66, 89, -100, 50.09 };
	
	public void dynamicExample1(int x, int y) {
		double z = Math.floor((y + 999999) / 1333);
		if (x == z) {
			System.currentTimeMillis();
		}
	}

	public void staticExample(int x, int y) {
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
	
	public void staticExample4(double x, int y, float f,long l) {
		if(x == 35.4 * 300) {
			System.currentTimeMillis();
		}
		if(y == -2147483646) {//ldc
			System.currentTimeMillis();
		}else if(y == 32700) {//sipush
			System.currentTimeMillis();
		}else if(y == 4) {//iconst
			System.currentTimeMillis();
		}else if(y == 120) {//bipush
			System.currentTimeMillis();
		}
		if(f == 0.98f) {
			System.currentTimeMillis();
		}else if(l == 2147483688l) {
			System.currentTimeMillis();
		}
	}

	public void equalsIgnoreCaseExample(String x, int y) {
		String a = "ignoreCase" + x;
		String b = "example";
		if (a.equalsIgnoreCase("IGNORECASEString" + b)) {
			System.currentTimeMillis();
		}
	}
	
	public void stratWithExample(String x, int index) {
		String a = x.substring(2, x.length() - 3);
		String b = "example";
		if (a.startsWith(b, index)) {
			System.currentTimeMillis();
		}
	}
	
	public void stratWithExample(String x) {
		String a = x.replace('a', 'A');
		String b = "example";
		if (a.startsWith("Find the right String")) {
			System.currentTimeMillis();
		}
	}
	
	public void endWithExample(String args[],String suffix) {
		String endString = "end";
		for(String a : args) {
			if (a.endsWith(endString)) {
				System.currentTimeMillis();
			}
		}
	}
	
	public void matchesExample(String x) {
		String regex = "^tr[A-F0-3]";
		if(x.matches(regex)) {
			System.currentTimeMillis();
		}
	}
	
	public void patternMatchesExample(String x) {
		String regex ="a*b";
		if(Pattern.matches(regex, x)) {
			System.currentTimeMillis();
		}
	}
	
	public void matcherMatchesExample(String x) {
		Pattern pattern = Pattern.compile("(\\d{3,4})\\-(\\d{7,8})");
        // get Matcher object
        Matcher matcher = pattern.matcher(x);
		if(matcher.matches()) {
			System.currentTimeMillis();
		}
	}
	
}
