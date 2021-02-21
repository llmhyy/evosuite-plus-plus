package org.evosuite.graphs.interprocedural;

import org.evosuite.TestGenerationContext;

public class SensitiveCallRules {
	
	/**
	 * if we call some method like str1 = str0.substring(),
	 * here, we build a knowledge base so that we know that there is some continuity between str0 and str1.
	 * 
	 * In a more general form, we should define a call like o1 = o0.m(),
	 * o1 and o0 should form such as relation that a change on o0 can induce a change on o1.
	 * @param className
	 * @param methodName
	 * @return
	 */
	public static double getSensitivity(String className, String methodName) {
		
		if(methodName.equals("toString")) {
			return 1;
		}
		
		if(className.equals("java.lang.Double") || 
				className.equals("java.lang.Integer") || 
				className.equals("java.lang.Boolean") || 
				className.equals("java.lang.Byte") || 
				className.equals("java.lang.Short") || 
				className.equals("java.lang.Float")) {
			return 1;
		}
		
		if(className.equals("java.lang.String")) {
			if(methodName.equals("toString") ||
					methodName.equals("equals") ||
					methodName.equals("toLowerCase") ||
					methodName.equals("toUpperCase") ||
					methodName.equals("split") ||
					methodName.equals("of") ||
					methodName.equals("replace")) {
				return 1;
			}
			else if(methodName.equals("getFirst")) {
				return 0.8;
			}else if(methodName.equals("substring")) {
				return 0.9;
			}
		}
		
		ClassLoader loader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		try {
			Class<?> collectionInterface = loader.loadClass("java.util.Collection");
			Class<?> IteractorInterface = loader.loadClass("java.util.Iterator");
			
			Class<?> clazz = loader.loadClass(className);
			
			if(collectionInterface.isAssignableFrom(clazz)) {
				if(methodName.equals("size") ||
						methodName.equals("iterator") ||
						methodName.equals("toArray") ||
						methodName.equals("finishToArray") ||
						methodName.equals("remove") ||
						methodName.equals("toString") ||
						methodName.equals("finishToArray") ||
						methodName.equals("add")) {
					return 1;
				}
				else if(methodName.equals("isEmpty") ||
						methodName.equals("contains") ||
						methodName.equals("hugeCapacity") ||
						methodName.equals("containsAll") ||
						methodName.equals("retainAll") ||
						methodName.equals("removeAll") ||
						methodName.equals("getFirst") ||
						methodName.equals("getFirst") ||
						methodName.equals("addAll")) {
					return 0.8;
				}
			}
			
			if(IteractorInterface.isAssignableFrom(clazz)) {
				if(methodName.equals("next")) {
					return 1;
				}
			}
			
			System.currentTimeMillis();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		return 0.7;
		
	}
}
