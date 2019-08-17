package org.evosuite.testcase;

import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

public class MutationPositionDiscriminator <T extends Chromosome> {
	@SuppressWarnings("rawtypes")
	public static MutationPositionDiscriminator discriminator = new MutationPositionDiscriminator<Chromosome>();
	
	public int frozenIteration = Properties.PRE_ONLINE_LEARNING_ITERATION;
	
	public Set<FitnessFunction<T>> currentGoals = new java.util.HashSet<>();
	
	
	public void resetFrozenIteartion() {
		frozenIteration = Properties.PRE_ONLINE_LEARNING_ITERATION;
	}
	
	public void decreaseFrozenIteration() {
		if(frozenIteration>0) {
			frozenIteration--;			
		}
	}
	
	public boolean isFrozen() {
		return frozenIteration > 0;
	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setPurpose(Set<FitnessFunction<T>> dominateUncoveredGoals) {
		currentGoals = new java.util.HashSet<>();
		for(FitnessFunction<T> ff: dominateUncoveredGoals) {
			currentGoals.add(ff);
		}
	}
}
