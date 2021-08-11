package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.DepVariable;

public class ValuePreservance {
	
	private List<BytecodeInstruction> observations;
	private List<DepVariable> inputVariables;
	
	public List<BytecodeInstruction> getConstantInputVariable(){
		List<BytecodeInstruction> list = new ArrayList<>();
		for(DepVariable var: inputVariables) {
			if(var.getInstruction().isConstant()) {
				list.add(var.getInstruction());
			}
		}
		
		return list;
	}
	
//	public Map<String, Boolean> inputConstant = new HashMap<>();
//	public Map<String, Boolean> observationsConstant = new HashMap<>();
	
	public ValuePreservance(List<BytecodeInstruction> observations, List<DepVariable> inputVariables) {
		this.observations = observations;
		this.inputVariables = inputVariables;
	}

//	public List<Object> pontentialBranchOperandTypes = new ArrayList<>();

	public List<ObservationRecord> recordList = new ArrayList<>();

	public void addRecord(ObservationRecord value) {
		this.recordList.add(value);
	}
	
	private MatchingResult matchingResult = null;
	
	/**
	 * return whether the branch can preserve the value of certain input to certain observation.
	 * 
	 * If yes, the field {@code matchingResult} will be set.
	 * @param b
	 * @return
	 */
	public boolean isValuePreserving() {
		double valuePreservingRatio = 0;
		
		if(this.recordList.isEmpty()) {
			return false;
		}
		
		ObservationRecord record0 = this.recordList.get(0);
		
		for(String inputKey: record0.inputs.getInputVariables().keySet()) {
			for(String obKey: record0.observationMap.keySet()) {
				valuePreservingRatio = 0;
				for (ObservationRecord record : recordList) {
					
					MatchingResult result = record.checkSameValue(inputKey, obKey);
					
					if (result != null) {
						valuePreservingRatio++;
						if ((valuePreservingRatio
								/ Properties.DYNAMIC_SENSITIVITY_THRESHOLD >= Properties.FAST_CHANNEL_SCORE_THRESHOLD)) {
							this.matchingResult = result;
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
	public List<ObservedConstant> getEstiamtedConstants() {
		List<ObservedConstant> list = new ArrayList<>();
		
		/**
		 *  得到所有的constant指令（在input中）
		 */
		List<BytecodeInstruction> inputContants = getConstantInputVariable();
		for(BytecodeInstruction constantIns0: inputContants) {
			Object value = SensitivityMutator.getConstantObject(constantIns0);
			Class<?> clazz = value.getClass();
			
			/**
			 *   获取constant的值， 对比observation中是否出现一样的值， 如果符合相关性，那么产生一条记录：
			 *   <value, type>
			 *   如果获取不到任何这样的记录，就是dynamic, 不然就是static
			 */
			for(BytecodeInstruction obIns: observations) {
				int count = 0;
				for (ObservationRecord record : recordList) {
					if (record.observationMap.keySet().size() == 0) break;
					
					boolean useConstant = record.isObservationUsingConstant(value, obIns.toString());
					if (useConstant) count++;
					
					if ((count / Properties.DYNAMIC_SENSITIVITY_THRESHOLD) >= Properties.FAST_CHANNEL_SCORE_THRESHOLD) {
						ObservedConstant constant = new ObservedConstant(value, clazz, constantIns0);
						list.add(constant);
						break;
					}
				}
			}
		}
		
		return list;
	}

	public boolean isSensitivityPreserving() {
		double sensivityPreserRatio = 0;
		int recordNum = getObservationRecordNum();
		if (recordNum == 0) {
			System.out.println("recordNum is 0!");
			return false;
		}
		
		List<BytecodeInstruction> inputContants = getConstantInputVariable();
		l: for (int i = 0; i < inputContants.size(); i++) {
			// input num
			for (int j = 0; j < observations.size(); j++) {
				sensivityPreserRatio = 0;
				// observation num
				for (ObservationRecord ob : recordList) {
					boolean sensivity = ob.isSensitive(i, j);
					if (sensivity) {
						sensivityPreserRatio++;
						if (((sensivityPreserRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD) >= 0.5
								|| sensivityPreserRatio == recordNum)) {
//							pontentialBranchOperandTypes.add(ob.potentialOpernadType);
							break l;
						}
					}
				}
			}
		}
		sensivityPreserRatio = sensivityPreserRatio / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
		return sensivityPreserRatio > 0; // Properties.VALUE_SIMILARITY_THRESHOLD;
	}

	public int getObservationRecordNum() {
		int i = 0;
		for (ObservationRecord r : recordList) {
			if (r.observationMap.size() > 0)
				i++;
		}
		return i;
	}

	public void clear() {
		this.recordList.clear();
	}

	public MatchingResult getMatchingResult() {
		return matchingResult;
	}
}