package org.evosuite.ga.operators.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;

/**
 * for each goal, we keep its at most Properties.LOCAL_OPTIMAL_NUMBER individuals at each front.
 * 
 * @author linyun
 *
 * @param <T>
 */
public class IndividualGoalBasedSorting <T extends Chromosome> implements RankingFunction<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7561479409788881618L;
	private Map<FitnessFunction<T>, List<T>> map = new HashMap<FitnessFunction<T>, List<T>>();
	
	private List<List<T>> fronts = new ArrayList<List<T>>();
	
	@Override
	public void computeRankingAssignment(List<T> solutions, Set<FitnessFunction<T>> uncovered_goals) {
		map.clear();
		for(FitnessFunction<T> ff: uncovered_goals) {
			List<T> rankedSolutions = rank(solutions, ff);
			map.put(ff, rankedSolutions);
		}
		
		
		List<T> solutionsCopy = new ArrayList<>(solutions);
		Map<FitnessFunction<T>, Double> bestValues = new HashMap<FitnessFunction<T>, Double>();
		while(!solutionsCopy.isEmpty()) {
			List<T> front = new ArrayList<>();
			
			for(FitnessFunction<T> ff: map.keySet()) {
				List<T> rankedSolutions = map.get(ff);
				Double bestValue = bestValues.get(ff);
				
				Iterator<T> iter = rankedSolutions.iterator();
				int count = 0;
				while(iter.hasNext() && count < Properties.LOCAL_OPTIMAL_NUMBER) {
					T individual = iter.next();
					Double value = individual.getFitness(ff);
					
					if(bestValue == null || value > bestValue) {
						if(!solutionsCopy.contains(individual)) {
							front.add(individual);
							solutionsCopy.remove(individual);
							iter.remove();
						}
						
					}
					
					bestValues.put(ff, value);
					count++;
				}
			}
			
			if(!front.isEmpty()) {
				fronts.add(front);
			}
			else {
				fronts.add(solutionsCopy);
				solutionsCopy.clear();
			}
		}
		
	}

	private List<T> rank(List<T> solutions, FitnessFunction<T> ff) {
		//FIXME ziheng, please rank the solution according to ff.
		return null;
	}

	@Override
	public List<T> getSubfront(int rank) {
		return fronts.get(rank);
	}

	@Override
	public int getNumberOfSubfronts() {
		return fronts.size();
	}

}
