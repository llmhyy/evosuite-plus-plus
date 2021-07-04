package org.evosuite.seeding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.graphs.cfg.BytecodeInstruction;

public class RuntimeSensitiveVariable {
//	public static Object headValue;
//	public static Object tailValue;
	/**
	 * map: bytecode-instruction --> list<value> (multiple iterations)
	 */
	public static Map<String, List<Object>> observations = new HashMap<>();
	
//	public static void setHeadValue(Object obj) {
//		headValue = obj;
//		int a = 0;
//		
//		setTailValue(a);
//	}
	
	public static void setObservation(Object obj, String key) {
		List<Object> list = observations.get(key);
		if(list == null) {
			list = new ArrayList<>();
		}
		list.add(obj);
		observations.put(key, list);
	}
	
//	public static void setTailValue() {
//	
//	}
}
