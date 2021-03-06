package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservationRecord {
	public ObservationRecord(Map<String, Object> recordInput, Map<String, List<Object>> observationMap,
			Map<String, Boolean> inputConstant) {
		this.inputs = recordInput;
		this.observations = observationMap;
		this.inputConstant = inputConstant;
	}

	/**
	 * bytecode instruction --> value
	 */
	public Map<String, Object> inputs = new HashMap<>();

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
	 */
	public boolean compare(int i, int j) {
		String inKey = (String) inputs.keySet().toArray()[i];
		Object in = inputs.get(inKey);

		String obKey = (String) observations.keySet().toArray()[j];

		for (Object ob : observations.get(obKey)) {
			if (in.equals(ob)) {
				potentialOpernadType = ob.getClass();
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
		String constantValue;
		if (inKey.contains(" Type="))
			constantValue = inKey.split(" Type=")[0].split(" ", 4)[3];
		else
			constantValue = inKey.split(" ")[2].split("_")[1];

		if (observations.keySet().size() == 0)
			return null;

		String obKey = (String) observations.keySet().toArray()[j];
		for (Object ob : observations.get(obKey)) {
			if (constantValue.equals(ob.toString())) {
				if (ob instanceof Integer) {
					int o = (int) ob;
					if (o < 10)
						return null;
				}
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
