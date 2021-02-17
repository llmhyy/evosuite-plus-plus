package feature.smartseed.example;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstrumentationExample {
	public static String[] noise = 
		{"abc0", "abc1", "abc2", "abc3", "abc4", "abc5", "abc6", "abc7", "abc8", "abc9", "abc10"};
	
	
	public void equalsExample(String x, int y) {
		String s ="it is a difficult string" ;
		String a = x.substring(0, x.length());
		String b = "Equal";
		if (a.equals(s)) {
			System.currentTimeMillis();
		}
	}
	
	public void equalsIgnoreCaseExample(String x, int y) {
		String a = "iGNOREcASE" + x;
		String b = "example";
		if (a.equalsIgnoreCase("IGNORECASE " + b)) {
			System.currentTimeMillis();
		}
//		if (a.equalsIgnoreCase("IGNORECASE String " + b)) {
//			System.currentTimeMillis();
//		}
	}
	
	public void stratWithExample(String x, int index) {
		//SeedingApplicationEvaluator type : NO_POOL
		//starWith has three parameters
		String a = x.substring(2, x.length() - 3);
		String b = "example";
		if (a.startsWith(b, index)) {
			System.currentTimeMillis();
		}
	}
	
	public void stratWithExample(String x) {
		String a = x.replace('a', 'A');
		String b = "noise String";
		if (a.startsWith("Find the right String")) {
			System.currentTimeMillis();
		}
	}
	
	public void endWithExample(String args[],String suffix) {
		String endString = suffix + "end";
		for(String a : args) {
			if (a.startsWith(endString)) {
				System.currentTimeMillis();
			}
		}
	}
	
	public void isEmptyExample(String x) {
		String[] a = x.split("s");
		for(String s : a) {
			if(s.isEmpty()) {//one operand
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
		String regex ="colour+s";
		if(Pattern.matches(regex, x)) {
			System.currentTimeMillis();
		}
	}
	
	public void matcherMatchesExample(String x) {
		Pattern pattern = Pattern.compile("(\\d{3,4})\\-(\\d{7,8})");
        // get Matcher object
        Matcher matcher = pattern.matcher(x);
		if(matcher.matches()) {//one operand
			System.currentTimeMillis();
		}
	}
	
	private ArrayList<String> list;
	
	public InstrumentationExample(ArrayList<String> list) {
		this.list = list;
	}
	
	public void containExample(String str) {
		String k = "kkkkk";
		if(list.contains(k)) {
			System.currentTimeMillis();
		}
	}
}
