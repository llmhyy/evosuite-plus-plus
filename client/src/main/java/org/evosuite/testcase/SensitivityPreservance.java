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
		getInputAndObservationsNum();
		int recordNum = getObservationRecordNum();
		System.currentTimeMillis();
		l: 
		for (int i = 0; i < ObservationRecord.inputNum; i++) {
			// input num
			for (int j = 0; j < ObservationRecord.observationNum; j++) {
				valuePreservingRatio = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
					boolean valuePreserving = ob.compare(i, j);
					if (valuePreserving) {
						valuePreservingRatio++;
						if ((valuePreservingRatio == Properties.DYNAMIC_SENSITIVITY_THRESHOLD
								|| valuePreservingRatio == recordNum) && !types.contains(ob.type)) {
							types.add(ob.type);
							break l;
						}
					}
				}
			}
		}
		valuePreservingRatio = valuePreservingRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
		return valuePreservingRatio > 0;
	}
	
	// String is just the bytecode instruction (toString)
	public List<Class<?>> getUseableConstants(){
		/**
		 * TODO Cheng Yan
		 * 
		 */
		useConstants = false;
		List<Class<?>> list = new ArrayList<>();
		int recordNum = getObservationRecordNum();
		l: 
		for (int i = 0; i < ObservationRecord.inputNum; i++) {
			// input num
			for (int j = 0; j < ObservationRecord.observationNum; j++) {
				int count = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
//					System.currentTimeMillis();
					Class<?> li = ob.useOfConstants(i, j);
					if (li != null && !list.contains(li)) {
						count++;
						if ((count == Properties.DYNAMIC_SENSITIVITY_THRESHOLD || count == recordNum)
								&& !list.contains(li)) {
							list.add(li);
							break l;
						}
					}
				}
			}
		}
				
		return list;
	}
	
	public boolean isSensitivityPreserving() {
		/**
		 * TODO Cheng Yan
		 */
		getInputAndObservationsNum();
		l: 
		for (int i = 0; i < ObservationRecord.inputNum; i++) {
			// input num
			for (int j = 0; j < ObservationRecord.observationNum; j++) {
				sensivityPreserRatio = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
					boolean valuePreserving = ob.isSensitive(i, j);
					if (valuePreserving) {
						sensivityPreserRatio++;
						double ratio = sensivityPreserRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
						if (ratio > Properties.SENSITIVITY_PRESERVING_THRESHOLD
								&& !types.contains(ob.type)) {
							types.add(ob.type);
							break l;
						}
					}
				}
			}
		}
		sensivityPreserRatio = sensivityPreserRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
		return sensivityPreserRatio > Properties.SENSITIVITY_PRESERVING_THRESHOLD;
	}
	
	public void getInputAndObservationsNum() {
		if(recordList.size() != 0) {
			recordList.get(0).getInputNum();
			
			int observationsNum = 0;
			for(ObservationRecord r : recordList) {
				if(r.getObservationsNum() > observationsNum)
					observationsNum = r.getObservationsNum();
			}
			ObservationRecord.observationNum = observationsNum;
		}
	}
	
	public int getObservationRecordNum() {
		int i = 0;
		for(ObservationRecord r : recordList) {
			if(r.observations.size() > 0)
				i++;
		}
		return i;
	}
}