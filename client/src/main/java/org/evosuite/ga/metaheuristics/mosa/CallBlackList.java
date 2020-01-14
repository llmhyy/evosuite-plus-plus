package org.evosuite.ga.metaheuristics.mosa;

import java.util.HashMap;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.setup.Call;
import org.evosuite.utils.generic.GenericAccessibleObject;
import org.evosuite.utils.generic.GenericMethod;

public class CallBlackList {
	
	public static final double RATIO_THRES = 0.5;
	public static final int CARDINATE_THRES = 100;
	
	/**
	 * record the calls to trigger exceptions
	 */
	public static Map<String, Integer> exceptionTriggeringCall = new HashMap<>();
	
	/**
	 * record the calls during evolution
	 */
	public static Map<String, Integer> calledMethods = new HashMap<>();
	
	
	public static boolean isInBlackList(Call call) {
		String methodSig = BranchEnhancementUtil.covert2Sig(call);
		return needToSkip(methodSig);
	}
	
	public static int getTotalNumberOfException() {
		int sum = 0;
		for(String method: exceptionTriggeringCall.keySet()) {
			int num = exceptionTriggeringCall.get(method);
			sum += num;
		}
		
		return sum;
	}
	
	public static boolean needToSkip(GenericAccessibleObject<?> choice) {

		if(exceptionTriggeringCall.isEmpty()) {
			return false;
		}
		
		if(choice.isMethod()) {
			GenericMethod method = (GenericMethod)choice;
			String methodName = method.getName() + method.getDescriptor();
			String className = method.getDeclaringClass().getCanonicalName();
			
			String fullName = className + "." + methodName;
			
			return needToSkip(fullName);
			
		}
		
		return false;
	}

	/**
	 * TODO for ziheng, here is the part to think, what kind of a method is inevitable?
	 * 
	 * For now, I draft it like the following criteria, we need a smarter way to do this.
	 * 
	 * 1. the method should be likely to trigger exception
	 * 2. the method has been called for many times
	 * 3. the method should not be a constructor or target method
	 * 
	 * @param choice
	 * @return
	 */
	public static boolean needToSkip(String methodFullName) {
		int index = methodFullName.lastIndexOf(".");
		String methodName = methodFullName.substring(index+1, methodFullName.length());
		String className = methodFullName.substring(0, index);
		String classShortName = className.substring(className.lastIndexOf(".")+1, className.length());
		
		if(methodName.equals(Properties.TARGET_METHOD)) {
			return false;
		}
		
		boolean isContructor = methodName.contains(classShortName);
		if(isContructor) {
			return false;
		}
		
		Integer exceptionNum = exceptionTriggeringCall.get(methodFullName);
		exceptionNum = exceptionNum==null ? 0 : exceptionNum;
		Integer sum = calledMethods.get(methodFullName);
		sum = sum==null ? 0 : sum;
		
		if(exceptionNum==0) {
			return false;
		}
		
		double ratio = sum==0 ? 1 : exceptionNum * 1.0 / sum;
		if(exceptionNum > CARDINATE_THRES && ratio < RATIO_THRES) {
			return false;
		}
		else {
			return true;
		}
		
	}
	
}
