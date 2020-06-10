package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {
	public static String getSignature(Executable m){
	    String sig;
	    try {
	    	if(m instanceof Method){
	    		Field gSig = Method.class.getDeclaredField("signature");
	    		gSig.setAccessible(true);
	    		sig = (String) gSig.get(m);
	    		if(sig!=null) return sig;	    		
	    	}
	    } catch (Exception e) { 
	        e.printStackTrace();
	    }

	    StringBuilder sb = new StringBuilder("(");
	    for(Class<?> c : m.getParameterTypes()) 
	        sb.append((sig=Array.newInstance(c, 0).toString())
	            .substring(1, sig.indexOf('@')));
	    if(m instanceof Method){
	    	Class<?> returnType = ((Method)m).getReturnType();
	    	String typeName = returnType.getCanonicalName();
	    	if(typeName.contains(".")){
	    		typeName = "L" + typeName.replace(".", "/") + ";";
	    	}
	    	else if(returnType == void.class){
	    		typeName = "V";
	    	}
	    	else{
	    		try{
	    			String siga = Array.newInstance(returnType, 0).toString();
	    			typeName = siga.substring(1, siga.indexOf('@'));	    			
	    		}
	    		catch(IllegalArgumentException e){
	    			e.printStackTrace();
	    		}
	    	}
	    	return sb.append(')')
	    			.append(typeName).toString();	    	
	    }
	    else{
	    	return sb.append(')').append("V").toString();	
	    }
	    
	}
}
