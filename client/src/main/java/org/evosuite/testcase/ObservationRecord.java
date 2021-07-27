package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;

public class ObservationRecord {
	public ObservationRecord(Map<String, List<Object>> recordInput, Map<String, List<Object>> observationMap,
			Map<String, Boolean> inputConstant) {
		this.inputs = recordInput;
		this.observations = observationMap;
		this.inputConstant = inputConstant;
	}

	/**
	 * bytecode instruction --> value
	 */
	public Map<String, List<Object>> inputs = new HashMap<>();

	public Map<String, Boolean> inputConstant = new HashMap<>();

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
	public boolean compare(int i, int j, Branch b) {		
		String inKey = (String) inputs.keySet().toArray()[i];
		if (inputConstant.get(inKey))
			return false;
		List<Object> in = new ArrayList<>();
		in.addAll(inputs.get(inKey));

		String obKey = (String) observations.keySet().toArray()[j];

		in.retainAll(observations.get(obKey));
		if (in.size() != 0) {
			for (Object ob : in) {
				potentialOpernadType = ob.getClass();
				return true;
			}
		}
		
//		for (Object ob : observations.get(obKey)) {
//			if (in.equals(ob)) {
//				potentialOpernadType = ob.getClass();
//				return true;
//			}
//		}
		
		//switch
		if(inKey.equals(obKey)) {
			StringBuilder sb = new StringBuilder();
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

	/**
	 * use of constants
	 * 
	 * @param j
	 * @param i
	 */
	public Class<?> useOfConstants(int i, int j) {
		String inKey = (String) inputs.keySet().toArray()[i];
		if (!inputConstant.get(inKey))
			return null;
//		String constantValue = inputs.get(inKey).toString();
		List<Object> in = new ArrayList<>();
		in.addAll(inputs.get(inKey));
		
		

		if (observations.keySet().size() == 0)
			return null;

		String obKey = (String) observations.keySet().toArray()[j];
		
		
		if(inputs.get(inKey).toString().equals(observations.get(obKey).toString())) {
			potentialOpernadType = observations.get(obKey).get(0).getClass();
			return observations.get(obKey).get(0).getClass(); 
		}
		
		in.retainAll(observations.get(obKey));
		if (in.size() != 0) {
			for (Object ob : in) {
				potentialOpernadType = ob.getClass();
				return ob.getClass();
			}
		}

//		
//		for (Object ob : observations.get(obKey)) {
//			if (constantValue.equals(ob.toString())) {
//				return inputs.get(inKey).getClass();
//			}
//		}
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
