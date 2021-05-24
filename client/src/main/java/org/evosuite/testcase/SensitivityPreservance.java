package org.evosuite.testcase;

import org.evosuite.Properties;

public class SensitivityPreservance {
//	public boolean valuePreserving = false;
//	public boolean sensivityPreserving = false;
	public double valuePreservingRatio = 0;
	public double sensivityPreserRatio = 0;

	public boolean isValuePreserving() {
		return valuePreservingRatio > Properties.VALUE_PRESERVING_THRESHOLD;
	}
	
	public boolean isSensitivityPreserving() {
		return sensivityPreserRatio > Properties.SENSITIVITY_PRESERVING_THRESHOLD;
	}
}