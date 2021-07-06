package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;

public class SensitivityPreservance {
	public double valuePreservingRatio = 0;
	public double sensivityPreserRatio = 0;
	public boolean useConstants = false;
	public List<Object> types = new ArrayList<>();

	public List<ObservationRecord> recordList = new ArrayList<>();
	
	public void addRecord(ObservationRecord value) {
		this.recordList.add(value);
	}
	
	public boolean isValuePreserving() {
		/**
		 * TODO Cheng Yan
		 */
		for (ObservationRecord ob : recordList) {
			boolean valuePreserving = ob.compare();
			if (valuePreserving) {
				valuePreservingRatio++;
				if (!types.contains(ob.type))
					types.add(ob.type);
			}
		}
		valuePreservingRatio = valuePreservingRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
		return valuePreservingRatio > Properties.VALUE_PRESERVING_THRESHOLD;
	}
	
	// String is just the bytecode instruction (toString)
	public List<String> getUseableConstants(){
		/**
		 * TODO Cheng Yan
		 * 
		 */
		useConstants = false;
		List<String> list = new ArrayList<>();
		for(ObservationRecord ob :recordList) {
			String li= ob.useOfConstants();
			if(li != null && !list.contains(li))
				list.add(li);
		}
		
		return list;
	}
	
	public boolean isSensitivityPreserving() {
		/**
		 * TODO Cheng Yan
		 */
		return sensivityPreserRatio > Properties.SENSITIVITY_PRESERVING_THRESHOLD;
	}
	
	
}