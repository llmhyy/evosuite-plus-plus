package org.evosuite.testcase.synthesizer.var;

import java.util.List;

import org.evosuite.testcase.variable.VariableReference;

public class VarRelevance {
	/**
	 * Given an instruction (or operand), the source code variables in test which can influence its value. 
	 */
	public List<VariableReference> influentialVars;
	
	/**
	 * Given an instruction (or operand), the source code variable which can exactly correspond to it. 
	 * 
	 * This matched variable can typically be used to construct the children variable.
	 */
	public List<VariableReference> matchedVars;

	public VarRelevance(List<VariableReference> influentialVars, List<VariableReference> matchedVars) {
		super();
		this.influentialVars = influentialVars;
		this.matchedVars = matchedVars;
	}

	public void merge(VarRelevance relevance) {
		if(influentialVars == null) {
			this.influentialVars = relevance.influentialVars;
		}
		else {
			for(VariableReference variable: relevance.influentialVars) {
				if(!this.influentialVars.contains(variable)) {
					this.influentialVars.add(variable);
				}
			}
		}
		
		if(matchedVars == null) {
			this.matchedVars = relevance.matchedVars;
		}
		else {
			for(VariableReference variable: relevance.matchedVars) {
				if(!this.matchedVars.contains(variable)) {
					this.matchedVars.add(variable);
				}
			}
		}
		
	}

	
}
