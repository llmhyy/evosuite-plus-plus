package org.evosuite.ga.metaheuristics.mosa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.operators.selection.SelectionFunction;
import org.evosuite.utils.Randomness;

public class MOSADominateGoalSelection<T extends Chromosome> extends SelectionFunction<T> {
	private static final long serialVersionUID = -7465418404056357932L;

	private Set<FitnessFunction<T>> dominateUncoveredGoals;
	
	public MOSADominateGoalSelection(Set<FitnessFunction<T>> dominateUncoveredGoals) {
		super();
		this.dominateUncoveredGoals = dominateUncoveredGoals;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Perform the tournament on the population, return one index
	 */
	@Override
	public int getIndex(List<T> population) {
		
		int new_num = Randomness.nextInt(population.size());
		if(this.dominateUncoveredGoals.isEmpty()) {
			return new_num;
		}
		
		int winner = new_num;

		int round = 0;

		FitnessFunction<T> randomFitness = selectRandomFitness(this.dominateUncoveredGoals);
		
		while (round < Properties.TOURNAMENT_SIZE - 1) {
			new_num = Randomness.nextInt(population.size());
			if (new_num == winner)
				new_num = (new_num+1) % population.size();
			T selected = population.get(new_num);
			int flag = compare(selected, population.get(winner), randomFitness);
			if (flag==-1) {
				winner = new_num;
			} 
			round++;
		}

		return winner;
	}

	private FitnessFunction<T> selectRandomFitness(Set<FitnessFunction<T>> dominateUncoveredGoals) {
		int random = Randomness.nextInt(dominateUncoveredGoals.size());
		
		Iterator<FitnessFunction<T>> iter = dominateUncoveredGoals.iterator();
		int count = 0;
		while(iter.hasNext()) {
			if(count++ == random) {
				return iter.next();
			}
		}
		return null;
	}

	private int compare(T selected, T t, FitnessFunction<T> randomFitness) {
		double d1 = randomFitness.getFitness(selected);
		double d2 = randomFitness.getFitness(t);
		
		if(d1 < d2) {
			return -1;
		}
		else if(d1 > d2) {
			return 1;
		}
		return 0;
	}

	@Override
	public T select(List<T> population) {
		return population.get(getIndex(population));
	}
}
