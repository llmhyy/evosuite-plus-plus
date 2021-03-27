package org.evosuite.seeding;

public class RuntimeSensitiveVariable {
	public static Object headValue;
	public static Object tailValue;
	
	
	public static void setHeadValue(Object obj) {
		headValue = obj;
		int a = 0;
		
		setTailValue(a);
	}
	
	public static void setTailValue(Object obj) {
		tailValue = obj;
	}
	
	public static void setTailValue() {
	
	}
}
