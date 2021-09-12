package feature.smartseed.example.truecase;

import org.evosuite.runtime.System;

public class ParseStackTraceElementTest {
	static int UNKNOWN_SOURCE = -1;
	public static final String NATIVE_METHOD_STRING = "Native Method";
	public static final String UNKNOWN_SOURCE_STRING = "Unknown Source";
	
	public static void weirdDynamicPool(String ste) {
		int idx = ste.lastIndexOf('(');
	    int endIdx = ste.lastIndexOf(')');
	    String source = ste.substring(idx + 1, endIdx);
	    if(!source.equals(UNKNOWN_SOURCE_STRING)) {
	    	System.currentTimeMillis();
	    }
	}
	
	public static void L168_static_pool(String ste) {
		if (ste == null) {
			return;
		}
		
		int idx = ste.lastIndexOf('(');
		if(idx < 0) {
			return; // not a ste
		}
		
	    int endIdx = ste.lastIndexOf(')');
	    if(endIdx < 0) {
			return; // not a ste
		}
	    
	    String source = ste.substring(idx + 1, endIdx);
	    if(!source.equals(UNKNOWN_SOURCE_STRING)) {
	    	System.currentTimeMillis();
	    }
	}
	
	public static void L178_static_pool(String ste) {
		if (ste == null) {
			return;
		}
		
	    int endIdx = ste.lastIndexOf(')');
	    if(endIdx < 0) {
			return; // not a ste
		}
		
	    String remainder = ste.substring(endIdx + 1);
	    if (remainder.startsWith(" [")) {
	    	System.currentTimeMillis();
	    }
	}
	
	public static void L183_static_pool(String ste) {
		if (ste == null) {
			return;
		}
		
	    int endIdx = ste.lastIndexOf(')');
	    if(endIdx < 0) {
			return; // not a ste
		}
	    String remainder = ste.substring(endIdx + 1);
	    if (remainder.startsWith(" ~[")) {
	    	System.currentTimeMillis();
	    }
	}
}
