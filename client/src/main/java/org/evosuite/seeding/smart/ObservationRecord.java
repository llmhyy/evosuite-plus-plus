package org.evosuite.seeding.smart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

/**
 * This class represents the execution results (i.e., observation value) given each input.
 * 
 * @author Yun Lin
 *
 */
public class ObservationRecord {
	/**
	 * bytecode instruction --> value
	 */
	public MethodInputs inputs;
	
	/**
	 * bytecode instruction --> list<value>
	 */
	public Map<String, List<Object>> observationMap = new HashMap<>();
	
//	/**
//	 * value types
//	 */
//	public Object potentialOpernadType = new ArrayList<>();
	
	public ObservationRecord(MethodInputs recordInput, Map<String, List<Object>> observationMap) {
		this.inputs = recordInput;
		this.observationMap = observationMap;
	}

	
	/**
	 * compare the value of inputs and observations
	 * 
	 * @param j
	 * @param i
	 * @param b
	 */
	public MatchingResult checkSameValue(String inKey, String obKey) {
		if (isConstant(inKey) && isConstant(obKey))
			return null;
		
		if(!inputs.getInputVariables().containsKey(inKey)) return null;
		Object inputObj = inputs.getInputVariables().get(inKey).getAssignmentValue();
		for(Object obser: observationMap.get(obKey)) {
			boolean isSame = checkEquals(inputObj, obser);
			if(isSame) {
				return new MatchingResult(inputs.getInputVariables().get(inKey), obser);
			}
			else {
				if(isStringCorrelation(inputObj, obser)) {
					MatchingResult res = new MatchingResult(inputs.getInputVariables().get(inKey), obser);
					res.setCorrelation(true);
					return res;
				}
			}
		}
		
//		return false;
		
		if (inputObj == null)
			return null;
		if (inputObj.getClass().equals(Boolean.class))
			return null;
		
		// switch
		if (inKey instanceof String) {
			StringBuilder sb = new StringBuilder();
			if (observationMap.get(obKey).size() == 0)
				return null;
			for (Object ob : observationMap.get(obKey)) {
				if (ob instanceof Integer) {
					int value = (int) ob;
					sb.append((char) value);
				}
			}
			if (sb.length() > 0 &&sb.toString().equals(inputs.getInputVariables().get(inKey).getAssignmentValue())) {
				MatchingResult res = new MatchingResult(inputs.getInputVariables().get(inKey), sb.toString());
				res.setCorrelation(true);
				return res;
			}
		}

		return null;
	}
	
	
	private static boolean isNumberEquals(Object obj1, Object obj2) {
		if(obj1 == null || obj2 == null) return false;
		
		Number n1 = null;
		Number n2 = null;
		
		if(obj1 instanceof Character) {
			Character c = (Character) obj1;
			n1 = (int)c;
		}
		
		if(obj2 instanceof Character) {
			Character c = (Character) obj2;
			n2 = (int)c;
		}
		
		if ((obj1 instanceof Number) 
				&& (obj2 instanceof Number)) {
			n1 = (Number) obj1;
			n2 = (Number) obj2;
		}
		
		if(n1 != null && n2 != null) {
			return (n1.longValue() == n2.longValue() || n1.doubleValue() == n2.doubleValue());
//			return n1.toString().equals(n2.toString());			
		}
		
		return false;
	}

	/**
	 * use of constants
	 * 
	 * @param j
	 * @param constantInstruction
	 */
	public boolean isObservationUsingConstant(Object constantValue, String obKey) {
		if (observationIsNull(observationMap))
			return false;

		List<Object> valueList = observationMap.get(obKey);
		for(Object value: valueList) {			
			boolean isEqual = checkConstantEquals(value, constantValue);
			if(!isEqual) {
				return false;
			}
		}
		
		return true;
	}

	private boolean checkConstantEquals(Object value, Object constantValue) {
		if (value == constantValue)
			return true;

		if(value.toString().equals(constantValue.toString())) return true;
		if (isNumberEquals(value, constantValue))
			return true;
		return false;
}


	private boolean checkEquals(Object value, Object constantValue) {
		
		if(value == null || constantValue == null) return false;
		
		if(value == constantValue) return true;
		
		if(isNumberEquals(value, constantValue)) return true;
		if(isStringEquals(value, constantValue)) return true;
		
		return false;
	}

	private boolean isStringCorrelation(Object value, Object constantValue) {
		if(value == null || constantValue == null) return false;
		
		if ((value instanceof String || value instanceof Character)
				&& (constantValue instanceof String || constantValue instanceof Character)) {
			if (constantValue instanceof Character) {
				constantValue = constantValue.toString();
			}

			if (value != null && constantValue != null) {
				return value.toString().toLowerCase().contains(constantValue.toString().toLowerCase())
						|| StringEscapeUtils.unescapeJava(value.toString().toLowerCase())
								.contains(StringEscapeUtils.unescapeJava(constantValue.toString().toLowerCase()));
			}
		}

		return false;
	}


	private boolean isStringEquals(Object value, Object constantValue) {
		if (value != null && constantValue != null) {
			return value.toString().toLowerCase().equals(constantValue.toString().toLowerCase())
					|| StringEscapeUtils.unescapeJava(value.toString().toLowerCase())
					.equals(StringEscapeUtils.unescapeJava(constantValue.toString().toLowerCase()));
		}

		return false;
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
		String inKey = (String) inputs.getInputVariables().keySet().toArray()[i];
		Object in = inputs.getInputVariables().get(inKey).getAssignmentValue();
		
		if(in == null) {
			return false;
		}

		if (observationMap.keySet().size() == 0)
			return false;

		String obKey = (String) observationMap.keySet().toArray()[j];

		for (Object ob : observationMap.get(obKey)) {
			if (SensitivityMutator.checkValuePreserving(in, ob)) {
				return true;
			}
		}
		return false;
	}

	private static boolean observationIsNull(Map<String, List<Object>> observationMap) {
		for (String s : observationMap.keySet()) {
			if (observationMap.get(s).size() > 0)
				return false;
		}
		return true;
	}

}
