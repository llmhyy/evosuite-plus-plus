package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;

public class SensitivityPreservance {
	public double valuePreservingRatio = 0;
	public double sensivityPreserRatio = 0;

	public List<ObservationRecord> recordList = new ArrayList<>();
	
	public void addRecord(ObservationRecord value) {
		this.recordList.add(value);
	}
	
	public boolean isValuePreserving() {
		/**
		 * TODO Cheng Yan
		 */
		return valuePreservingRatio > Properties.VALUE_PRESERVING_THRESHOLD;
	}
	
	// String is just the bytecode instruction (toString)
	public List<String> getUseableConstants(){
		/**
		 * TODO Cheng Yan
		 */
		return null;
	}
	
	public boolean isSensitivityPreserving() {
		/**
		 * TODO Cheng Yan
		 */
		return sensivityPreserRatio > Properties.SENSITIVITY_PRESERVING_THRESHOLD;
	}
	
	
}