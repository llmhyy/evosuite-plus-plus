package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.utils.MethodUtil;
import org.objectweb.asm.Opcodes;

public class ObservationRecord {
	public ObservationRecord(Map<String, List<Object>> recordInput, Map<String, List<Object>> observationMap,
			Map<String, Boolean> inputConstant, Map<String, Boolean> observationConstant) {
		this.inputs = recordInput;
		this.observations = observationMap;
		this.inputConstant = inputConstant;
		this.observationsConstant = observationConstant;
	}

	/**
	 * bytecode instruction --> value
	 */
	public Map<String, List<Object>> inputs = new HashMap<>();

	public Map<String, Boolean> inputConstant = new HashMap<>();
	public Map<String, Boolean> observationsConstant = new HashMap<>();

	/**
	 * bytecode instruction --> list<value>
	 */
	public Map<String, List<Object>> observations = new HashMap<>();

	/**
	 * value types
	 */
	public Object potentialOpernadType = new ArrayList<>();

	/**
	 * compare the value of inputs and observations
	 * 
	 * @param j
	 * @param i
	 * @param b 
	 */
	public boolean compare(int i, int j) {
		if(inputs.size() <= i || observations.size() <= j) return false;
		String inKey = (String) inputs.keySet().toArray()[i];
//		in.addAll(inputs.get(inKey));

		String obKey = (String) observations.keySet().toArray()[j];

		if (inputConstant.get(inKey) && observationsConstant.get(obKey))
			return false;
//		in.retainAll(observations.get(obKey));
		
		//numeric
		if(numericEquals(inKey,obKey)) return true;

		//String
		if(StringEquals(inKey,obKey)) return true;

		for(Object ob:inputs.get(inKey)) {
			if(ob.getClass().equals(Boolean.class))
				return false;
			if(observations.get(obKey).contains(ob)) {
				potentialOpernadType = ob.getClass();
				return true;
			}
		}
		
		
		//switch
		if(inKey.equals(obKey)) {
			StringBuilder sb = new StringBuilder();
			if(observations.get(obKey).size() == 0) return false;
			for (Object ob : observations.get(obKey)) {
				if (ob instanceof Integer) {
					int value = (int) ob;
					sb.append((char)value);
				}
			}
			if(sb.toString().equals(inputs.get(inKey).get(0))) {
				potentialOpernadType = String.class;
				return true;
			}
		}
		
		return false;
	}

	private boolean StringEquals(String inKey, String obKey) {
		if(inputs.get(inKey).size() == 0 || observations.get(obKey).size() == 0) return false;
		if (inputs.get(inKey).get(0) instanceof String && observations.get(obKey).get(0) instanceof String) {
			List<String> list1 = new ArrayList<String>();
			List<String> list2 = new ArrayList<String>();
			for (Object ob : inputs.get(inKey)) {
				list1.add(ob.toString().toLowerCase());
			}
			for (Object ob : observations.get(obKey)) {
				list2.add(ob.toString().toLowerCase());
			}
			for (Object ob : list1) {
				if (list2.contains(ob)) {
					potentialOpernadType = String.class;
					return true;
				}
			}
		}
		return false;
	}

	private boolean numericEquals(String inKey, String obKey) {
		if(inputs.get(inKey).isEmpty() || observations.get(obKey).isEmpty()) return false;
		
		Class<?> clsInput = inputs.get(inKey).get(0).getClass();
		Class<?> clsObser = observations.get(obKey).get(0).getClass();

		if ((inputs.get(inKey).get(0) instanceof Number || inputs.get(inKey).get(0) instanceof Character)
				&& (observations.get(obKey).get(0) instanceof Number)
				|| observations.get(obKey).get(0) instanceof Character) {
			List<String> list1 = new ArrayList<String>();
			List<String> list2 = new ArrayList<String>();
			for (Object ob : inputs.get(inKey)) {
				if (ob instanceof Character) {
					Character c = (Character) ob;
					Integer i = (int) c;
					list1.add(i.toString());
				} else
					list1.add(ob.toString());
			}
			for (Object ob : observations.get(obKey)) {
				if (ob instanceof Character) {
					Character c = (Character) ob;
					Integer i = (int) c;
					list2.add(i.toString());
				} else
					list2.add(ob.toString());
			}
			for (Object ob : list1) {
				if (list2.contains(ob)) {
					potentialOpernadType = clsObser;
					return true;
				}
			}
		}

//		if (inputs.get(inKey).get(0) instanceof Number && observations.get(obKey).get(0) instanceof Number) {
//
//			List<String> list1 = new ArrayList<String>();
//			List<String> list2 = new ArrayList<String>();
//			for (Object ob : inputs.get(inKey)) {
//				list1.add(ob.toString());
//			}
//			for (Object ob : observations.get(obKey)) {
//				list2.add(ob.toString());
//			}
//			for (Object ob : list1) {
//				if (list2.contains(ob)) {
//					potentialOpernadType = clsObser;
//					return true;
//				}
//			}
//		}
		return false;
		
	}

	/**
	 * use of constants
	 * 
	 * @param j
	 * @param i
	 */
	public Class<?> useOfConstants(int i, int j) {
		if(inputs.size() <= i || observations.size() <= j) return null;
		String inKey = (String) inputs.keySet().toArray()[i];
		if (!inputConstant.get(inKey))
			return null;

		if (observations.keySet().size() == 0)
			return null;

		String obKey = (String) observations.keySet().toArray()[j];
		
		
//		if(inputs.get(inKey).toString().equals(observations.get(obKey).toString())) {
//			potentialOpernadType = observations.get(obKey).get(0).getClass();
//			return observations.get(obKey).get(0).getClass(); 
//		}
		for(Object ob:inputs.get(inKey)) {
			
			if(observations.get(obKey).contains(ob)) {
				potentialOpernadType = ob.getClass();
				return ob.getClass();
			}
			
			//class equals [Ljava/lang/Boolean;  -- java.lang.Boolean]
			if(observations.get(obKey).isEmpty()) return null;
			if(observations.get(obKey).get(0) instanceof Class<?>) {
				String[] localSeparateTypes = ob.toString().split(";");
				for (int m = 0; m < localSeparateTypes.length; m++) {
					if (localSeparateTypes[m].startsWith("L")) {
						localSeparateTypes[m] = localSeparateTypes[m].substring(1, localSeparateTypes[m].length());
						localSeparateTypes[m] = localSeparateTypes[m].replace("/", ".");
					}
				}
				System.currentTimeMillis();
				for(Object clazz:observations.get(obKey)) {
					if(clazz instanceof Class<?>) {
						String clazzName = clazz.toString().split("class ")[1];
						if(Arrays.asList(localSeparateTypes).contains(clazzName)) {
							potentialOpernadType = ob.getClass();
							return ob.getClass();
						}
					}
				}
			}
			
			if(numericEquals(inKey, obKey)) {
				potentialOpernadType = ob.getClass();
				return ob.getClass();
			}
			
			
		}
	
		return null;

	}

	public boolean isConstant(String in) {
		String[] list = in.split(" ");
		if (list.length > 2) {
			switch (list[2]) {
			case "LDC":
			case "ICONST_0":
			case "ICONST_1":
			case "ICONST_2":
			case "ICONST_3":
			case "ICONST_4":
			case "ICONST_5":
			case "ICONST_M1":
			case "LCONST_0":
			case "LCONST_1":
			case "DCONST_0":
			case "DCONST_1":
			case "FCONST_0":
			case "FCONST_1":
			case "FCONST_2":
			case "BIPUSH":
			case "SIPUSH":
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	public boolean isSensitive(int i, int j) {
		String inKey = (String) inputs.keySet().toArray()[i];
		Object in = inputs.get(inKey);

		if (observations.keySet().size() == 0)
			return false;

		String obKey = (String) observations.keySet().toArray()[j];

		for (Object ob : observations.get(obKey)) {
			if (SensitivityMutator.checkValuePreserving(in, ob)) {
				potentialOpernadType = ob.getClass();
				return true;
			}
		}
		return false;
	}

}
