package org.evosuite.testcase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.statements.Statement;

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
	
	public void setPurpose(List<FitnessFunction<T>> dominateUncoveredGoals) {
		currentGoals = new java.util.HashSet<>();
		for(FitnessFunction<T> ff: dominateUncoveredGoals) {
			currentGoals.add(ff);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void identifyRelevantMutations(Chromosome offspring, Chromosome parent) {
		if(offspring instanceof TestChromosome && parent instanceof TestChromosome) {
			TestChromosome newTest = (TestChromosome)offspring;
			TestChromosome oldTest = (TestChromosome)parent;
			
			Map<FitnessFunction, Boolean> changedFitnesses = identifyChangedFitness(newTest, oldTest);
			
//			System.currentTimeMillis();
			
			updateChangeRelevanceMap(changedFitnesses, newTest.getTestCase());
			updateChangeRelevanceMap(changedFitnesses, oldTest.getTestCase());
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static void updateChangeRelevanceMap(Map<FitnessFunction, Boolean> changedFitnesses, TestCase test) {
		for(int pos=0; pos<test.size()-1; pos++) {
			Statement statement = test.getStatement(pos);
			if(statement.isChanged()) {
				for(FitnessFunction ff: changedFitnesses.keySet()) {
					Pair<Double, Double> pair = statement.getChangeRelevanceMap().get(ff);
					if(pair==null) {
						pair = MutablePair.of(0d, 0d);
					}
					
					if(changedFitnesses.get(ff)) {
						pair = MutablePair.of(pair.getLeft()+1, pair.getRight());
					}
					else {
						pair = MutablePair.of(pair.getLeft(), pair.getRight()+1);
					}
					statement.getChangeRelevanceMap().put(ff, pair);
				}
			}
		}
	}

	/**
	 * Lin Yun: If the new test is better than the old test, we record the map <fitness, true>, 
	 * else we record the map <fitness, false>. 
	 * 
	 * We distinguish the positive/negative effect because we would like to know whether the
	 * mutation on specific position always has negative effect. by our observation, the negative
	 * effect happens because of local optima, i.e., the mutation causes that we can no longer
	 * cover the parent branch. If it does, we can cancel out such mutation during search.
	 * 
	 * @param newTest
	 * @param oldTest
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<FitnessFunction, Boolean> identifyChangedFitness(TestChromosome newTest, TestChromosome oldTest) {
		Map<FitnessFunction, Boolean> map = new HashMap<>();
		
		for(FitnessFunction changedFit: newTest.getFitnessValues().keySet()) {
			double d1 = newTest.getFitness(changedFit);
			double d2 = oldTest.getFitness(changedFit);
			
			if(d1 != d2) {
				map.put(changedFit, d1 < d2);
			}
		}
		
		return map;
	}
}
