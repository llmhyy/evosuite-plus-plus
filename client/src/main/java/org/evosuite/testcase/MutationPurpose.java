package org.evosuite.testcase;

import java.util.Set;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

public class MutationPurpose <T extends Chromosome> {
	@SuppressWarnings("rawtypes")
	public static MutationPurpose currentPurpose = new MutationPurpose<Chromosome>();
	
	public Set<FitnessFunction<T>> goals = new java.util.HashSet<>();
	
	
	public static void reset() {
		currentPurpose = new MutationPurpose<Chromosome>();
	}


//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setPurpose(Set<FitnessFunction<T>> dominateUncoveredGoals) {
		goals = new java.util.HashSet<>();
		for(FitnessFunction<T> ff: dominateUncoveredGoals) {
			goals.add(ff);
		}
	}
}
