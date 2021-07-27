package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.objectweb.asm.Opcodes;

public class SensitivityPreservance {
	
	private int observationSize;
	private int inputSize;
	
	public SensitivityPreservance(int observationSize, int inputSize) {
		super();
		this.observationSize = observationSize;
		this.inputSize = inputSize;
	}

	public boolean useConstants = false;
	public List<Object> pontentialBranchOperandTypes = new ArrayList<>();

	public List<ObservationRecord> recordList = new ArrayList<>();

	public void addRecord(ObservationRecord value) {
		this.recordList.add(value);
	}

	public boolean isValuePreserving(Branch b) {
		/**
		 * TODO Cheng Yan
		 */
		double valuePreservingRatio = 0;
		for (int i = 0; i < inputSize; i++) {
			// input num
			for (int j = 0; j < observationSize; j++) {
				valuePreservingRatio = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
					boolean valuePreserving = ob.compare(i, j, b);
					if (valuePreserving) {
						valuePreservingRatio++;
						if ((valuePreservingRatio
								/ Properties.DYNAMIC_SENSITIVITY_THRESHOLD >= Properties.FAST_CHANNEL_SCORE_THRESHOLD)) {
							pontentialBranchOperandTypes.add(ob.potentialOpernadType);
							return true;
						}
					}
				}
			}
		}

		return false;
		
//		return (valuePreservingRatio
//				/ Properties.DYNAMIC_SENSITIVITY_THRESHOLD >= Properties.FAST_CHANNEL_SCORE_THRESHOLD);
	}

	// String is just the bytecode instruction (toString)
	public List<Class<?>> getUseableConstants() {
		/**
		 * TODO Cheng Yan
		 * 
		 */
		useConstants = false;
		List<Class<?>> list = new ArrayList<>();
		for (int i = 0; i < inputSize; i++) {
			// input num
			for (int j = 0; j < observationSize; j++) {
				int count = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
//					System.currentTimeMillis();
					Class<?> li = ob.useOfConstants(i, j);
					if (li != null) {
						count++;
						if ((count / Properties.DYNAMIC_SENSITIVITY_THRESHOLD) >= Properties.FAST_CHANNEL_SCORE_THRESHOLD) {
							list.add(li);
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
		double sensivityPreserRatio = 0;
		int recordNum = getObservationRecordNum();
		if (recordNum == 0) {
			System.out.println("recordNum is 0!");
			return false;
		}
		l: for (int i = 0; i < inputSize; i++) {
			// input num
			for (int j = 0; j < observationSize; j++) {
				sensivityPreserRatio = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
					boolean sensivity = ob.isSensitive(i, j);
					if (sensivity) {
						sensivityPreserRatio++;
						if (((sensivityPreserRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD) >= 0.5
								|| sensivityPreserRatio == recordNum) && !pontentialBranchOperandTypes.contains(ob.potentialOpernadType)) {
							pontentialBranchOperandTypes.add(ob.potentialOpernadType);
							break l;
						}
					}
				}
			}
		}
		sensivityPreserRatio = sensivityPreserRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
		return sensivityPreserRatio > 0;// Properties.VALUE_SIMILARITY_THRESHOLD;
	}

	public int getObservationRecordNum() {
		int i = 0;
		for (ObservationRecord r : recordList) {
			if (r.observations.size() > 0)
				i++;
		}
		return i;
	}

	public void clear() {
		this.recordList.clear();
		this.pontentialBranchOperandTypes.clear();
	}
}