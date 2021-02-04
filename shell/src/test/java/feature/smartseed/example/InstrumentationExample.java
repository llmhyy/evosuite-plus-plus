package feature.smartseed.example;

public class InstrumentationExample {
	public static String[] noise = 
		{"abc0", "abc1", "abc2", "abc3", "abc4", "abc5", "abc6", "abc7", "abc8", "abc9", "abc10"};
	
	
	public void equalsExample(String x, int y) {
		String a = x.substring(0, x.length() - 3);
		if (a.equals("it is a difficult string")) {
			System.currentTimeMillis();
		}
	}
}
