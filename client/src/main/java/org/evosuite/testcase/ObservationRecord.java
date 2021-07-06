package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;

public class ObservationRecord {
	public ObservationRecord(Map<String, Object> recordInput, Map<String, List<Object>> observationMap) {
		this.inputs = recordInput;
		this.observations = observationMap;
	}

	/**
	 * bytecode instruction --> value
	 */
	public Map<String, Object> inputs = new HashMap<>();
	
	/**
	 * bytecode instruction --> list<value>
	 */
	public Map<String, List<Object>> observations = new HashMap<>();
	
	/**
	 * value types
	 */
	public Object type = new ArrayList<>();
	
	/**
	 * compare the value of inputs and observations
	 */
	public boolean compare() {
		for(String in : inputs.keySet()) {
			if(inputs.get(in).toString().equals("N/A")) {
				continue;
			}
			
			for(String ob:observations.keySet()) {
				if(!inputs.get(in).getClass().equals(observations.get(ob).get(0).getClass()))
					continue;
				List<Object> li= observations.get(ob);
				for(Object o : li) {
					if(inputs.get(in).equals(o)) {
						type = o.getClass();
						return true;
					}
						
				}
			}
		}
		return false;
	}
	
	/**
	 * use of constants
	 */
	public String useOfConstants(){
		for(String in : inputs.keySet()) {
			if(!inputs.get(in).toString().equals("N/A")) {
				continue;
			}
			
			if(!isConstant(in))
				continue;				
			
			boolean isString = in.contains("LDC");
				
			for(String ob:observations.keySet()) {
				for(Object o :observations.get(ob)) {
					//string
					if(isString && o.getClass().equals(String.class)) {
						String value = in.split(" Type=")[0].split(" ", 4)[3];
						if(value.equals(o.toString()))
							return o.toString();
					}
					//other constants
				}

			}
		}
		return null;
	}
	
	public boolean isConstant(String in) {
		String[] list = in.split(" ");
		if(list.length > 2) {
			switch(list[2]) {
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
}
