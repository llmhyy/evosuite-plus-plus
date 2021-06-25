package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;

public class SensitivityPreservance {
//	public boolean valuePreserving = false;
//	public boolean sensivityPreserving = false;
	public double valuePreservingRatio = 0;
	public double sensivityPreserRatio = 0;

	public List<Object> tailValues = new ArrayList<>();
	public List<Object> headValues = new ArrayList<>();
	
	public void addTail(Object value) {
		this.tailValues.add(value);
	}
	
	public void addHead(Object value) {
		this.headValues.add(value);
	}
	
//	public boolean isFastChannel() {
//		if(isValuePreserving()) {
//			return true;
//		}
//		
//		if(isSensitivityPreserving()) {
//			boolean isValueSimilar()
//		}
//	}
	
	public boolean isValuePreserving() {
		return valuePreservingRatio > Properties.VALUE_PRESERVING_THRESHOLD;
	}
	
	public boolean isSensitivityPreserving() {
		return sensivityPreserRatio > Properties.SENSITIVITY_PRESERVING_THRESHOLD;
	}
}