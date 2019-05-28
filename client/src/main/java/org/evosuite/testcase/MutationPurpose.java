package org.evosuite.testcase;

import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

public class MutationPurpose <T extends Chromosome> {
	public static Set<FitnessFunction<? extends Chromosome>> purpose = null;
	
	
	public static void reset() {
		purpose = null;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setPurpose(Set<FitnessFunction<T>> dominateUncoveredGoals) {
		purpose = new java.util.HashSet<>();
		for(FitnessFunction<T> ff: dominateUncoveredGoals) {
			purpose.add(ff);
		}
	}
}
