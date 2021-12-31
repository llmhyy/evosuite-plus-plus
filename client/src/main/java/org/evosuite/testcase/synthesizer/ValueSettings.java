package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.graphs.cfg.BytecodeInstruction;
/**
 * all the settings for calling a method to set a field with a specific value-setting-instruction
 * 
 * @author linyun
 *
 */
public class ValueSettings {
	/**
	 * the instruction to set the value
	 */
	public BytecodeInstruction valueSettingInstruction;
	
	
	/**
	 * call chain required to set the value of the variable, <m_1, ..., m_n>
	 * m_n is the method to call field setter instruction
	 * m_1 is the method called by test
	 */
	public List<BytecodeInstruction> callChain = new ArrayList<BytecodeInstruction>();
	
	/**
	 * relevant parameters to make the function call
	 */
	public Set<Integer> releventPrams = new HashSet<>();
	
	/**
	 * whether the parameters can influence or directly set the value
	 */
	public int dataflowType;
	
	public ValueSettings(BytecodeInstruction ins, List<BytecodeInstruction> cascadingCallRelations,
			String targetClass, String signature) {
		this.valueSettingInstruction = ins;
		this.callChain = cascadingCallRelations;
		
		/**
		 * infer influence/match
		 */
		Set<Integer> validParamPos = DataDependencyUtil.checkValidParameterPositions(ins, 
				targetClass, signature, cascadingCallRelations);
		
		this.releventPrams = validParamPos;
		this.dataflowType = validParamPos.isEmpty() ? DataflowProperty.MATCH : DataflowProperty.INFLUENCE;
		
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ValueSettings) {
			ValueSettings v = (ValueSettings)obj;
			return this.valueSettingInstruction.equals(v.valueSettingInstruction);
		}
		
		return false;
	}
}
