package org.evosuite.testcase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
