package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.utils.Randomness;

public class ValuePreservance {
	
	private List<BytecodeInstruction> observations;
	private List<DepVariable> inputVariables;
	private List<ObservationRecord> recordList = new ArrayList<>();
	private TestChromosome testSeed;
	
	public List<ObservedConstant> getEstiamtedDynamicConstants() {
		List<ObservedConstant> list = new ArrayList<>();
		
		if(recordList == null || recordList.isEmpty()) {
			return list;
		}
		
		/**
		 * 1. 首先获得第一个record
		 * 
		 * 2. 找到这个record的所有非constant的observation值
		 * 
		 * 3. 随机挑选一个值，去替换record.test中对应的一个位置
		 * 
		 * m(x, y1, y2, ...), 如果x是fast channel， 就去设置y
		 */
		for(ObservationRecord record : recordList) {
			if(observationIsNull(record.observationMap)) continue;
			Object matchedObs= this.matchingResults.get(0).getMatchedObservation();
			Object matchedObsKey = this.matchingResults.get(0).getMatchedObservationKey();
			for(String observationIns: record.observationMap.keySet()) {
				if (!record.isConstant(observationIns) && !observationIns.equals(matchedObsKey)) {
					if(record.observationMap.get(observationIns).isEmpty()) continue;
					Object ob = Randomness.choice(record.observationMap.get(observationIns));
					if (ob != null && ob.getClass().isPrimitive() && ob.getClass().equals(matchedObs.getClass())) {
						ObservedConstant constant = new ObservedConstant(ob, matchedObs.getClass(), null);
						list.add(constant);
					}
				}
			}
			
			return list;
		}
		return list;
		
	}
	private static  boolean observationIsNull(Map<String, List<Object>> observationMap) {
		for(String s : observationMap.keySet()) {
			if(observationMap.get(s).size() > 0)
				return false;
		}
		return true;
	}
	
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

	public void addRecord(ObservationRecord value) {
		this.recordList.add(value);
	}
	
	private List<MatchingResult> matchingResults = new ArrayList<>();
	
	/**
	 * return whether the branch can preserve the value of certain input to certain observation.
	 * 
	 * If yes, the field {@code matchingResult} will be set.
	 * @param b
	 * @return
	 */
	public boolean isValuePreserving() {
		
		if(this.recordList.isEmpty()) {
			return false;
		}
		
		ObservationRecord record0 = this.recordList.get(0);
		
		for(String inputKey: record0.inputs.getInputVariables().keySet()) {
			System.currentTimeMillis();
			
			for(String obKey: record0.observationMap.keySet()) {
				double valuePreservingCount = 0;
				for (ObservationRecord record : recordList) {
					MatchingResult result = record.checkSameValue(inputKey, obKey);
					
					if (result != null) {
						valuePreservingCount++;
						double valuePreservingRatio = ((double)valuePreservingCount)/recordList.size();
						if (valuePreservingRatio >= Properties.FAST_CHANNEL_SCORE_THRESHOLD) {
							this.matchingResults.add(result);
							break;
						}
					}
				}
			}
		}
		System.currentTimeMillis();
		return !matchingResults.isEmpty();
		
	}

	/**
	 *  String is just the bytecode instruction (toString)
	 * @return
	 */
	public List<ObservedConstant> getEstiamtedStaticConstants() {
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
					
					if (((double)count / Properties.DYNAMIC_SENSITIVITY_THRESHOLD) >= Properties.FAST_CHANNEL_SCORE_THRESHOLD) {
						ObservedConstant constant = new ObservedConstant(value, clazz, constantIns0);						
						list.add(constant);
						break;
					}
				}
			}
		}
//		System.currentTimeMillis();
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

//	public void clear() {
//		this.recordList.clear();
//	}

	public List<MatchingResult> getMatchingResults() {
		return matchingResults;
	}
	
	public void setTest(TestChromosome testSeed) {
		this.testSeed = testSeed;
	}

}