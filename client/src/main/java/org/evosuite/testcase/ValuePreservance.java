package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.cfg.BytecodeInstruction;

public class ValuePreservance {
	
	private int observationSize;
	private int inputSize;
	
	public ValuePreservance(int observationSize, int inputSize) {
		super();
		this.observationSize = observationSize;
		this.inputSize = inputSize;
	}

	public List<Object> pontentialBranchOperandTypes = new ArrayList<>();

	public List<ObservationRecord> recordList = new ArrayList<>();

	public void addRecord(ObservationRecord value) {
		this.recordList.add(value);
	}

	public boolean isValuePreserving(Branch b) {
		/**
		 * TODO Cheng Yan
		 */
//		if(recordList.size() < 3) return false;
		
		double valuePreservingRatio = 0;
		for (int i = 0; i < inputSize; i++) {
			// input num
			for (int j = 0; j < observationSize; j++) {
				valuePreservingRatio = 0;
				// observation num
				for (ObservationRecord record : recordList) {
					boolean valuePreserving = record.compare(i, j);
					if (valuePreserving) {
						valuePreservingRatio++;
						if ((valuePreservingRatio
								/ Properties.DYNAMIC_SENSITIVITY_THRESHOLD >= Properties.FAST_CHANNEL_SCORE_THRESHOLD)) {
							pontentialBranchOperandTypes.add(record.potentialOpernadType);
							return true;
						}
					}
				}
			}
		}

		return false;
		
	}

	/**
	 *  String is just the bytecode instruction (toString)
	 * @return
	 */
	public Map<Object, Class<?>> getUseableConstants() {
		/**
		 *  得到所有的constant指令（在input中）
		 */
		List<BytecodeInstruction> constantInstructionList = getConstants(inputs);
		for(BytecodeInstruction ins: constantInstructionList) {
			/**
			 *   获取constant的值， 对比observation中是否出现一样的值， 如果符合相关性，那么产生一条记录：
			 *   <value, type>
			 * 如果获取不到任何这样的记录，就是dynamic, 不然就是static
			 */
			Object value = getFrom(ins);
		}
		
		Map<Object, Class<?>> list = new HashMap<>();
		for (int i = 0; i < inputSize; i++) {
			// input num
			for (int j = 0; j < observationSize; j++) {
				int count = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
//					System.currentTimeMillis();
					Class<?> clazz = ob.useOfConstants(i, j);
					if (clazz != null) {
						count++;
						if ((count / Properties.DYNAMIC_SENSITIVITY_THRESHOLD) >= Properties.FAST_CHANNEL_SCORE_THRESHOLD) {
							list.add(clazz);
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