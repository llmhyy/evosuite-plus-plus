package org.evosuite.ga.metaheuristics.mosa;

import java.util.HashMap;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.utils.generic.GenericAccessibleObject;
import org.evosuite.utils.generic.GenericMethod;

public class CallBlackList {
	
	public static final double INEVITABLE_THRES = 0.5;
	
	/**
	 * record the calls to trigger exceptions
	 */
	public static Map<String, Integer> exceptionTriggeringCall = new HashMap<>();
	
	/**
	 * record the calls during evolution
	 */
	public static Map<String, Integer> calledMethods = new HashMap<>();
	
	public static boolean isInBlackList(String methodName) {
		//TODO
		
		return false;
	}

	public static int getTotalNumberOfException() {
		int sum = 0;
		for(String method: exceptionTriggeringCall.keySet()) {
			int num = exceptionTriggeringCall.get(method);
			sum += num;
		}
		
		return sum;
	}
	
	/**
	 * TODO for ziheng, need to collect the number of method call not triggering exception
	 * @param choice
	 * @return
	 */
	public static boolean needToSkip(GenericAccessibleObject<?> choice) {

		if(exceptionTriggeringCall.isEmpty()) {
			return false;
		}
		
		if(choice.isMethod()) {
			GenericMethod method = (GenericMethod)choice;
			String methodName = method.getName() + method.getDescriptor();
			String className = method.getDeclaringClass().getCanonicalName();
			
			String fullName = className + "." + methodName;
			
			if(methodName.equals(Properties.TARGET_METHOD)) {
				return false;
			}
			
			Integer exceptionNum = exceptionTriggeringCall.get(fullName);
			Integer sum = calledMethods.get(fullName);
			if(exceptionNum != null && sum != null) {
				double ratio = exceptionNum * 1.0 / sum;
				return ratio > INEVITABLE_THRES;
			}
			
		}
		
		return false;
	}
	
}
