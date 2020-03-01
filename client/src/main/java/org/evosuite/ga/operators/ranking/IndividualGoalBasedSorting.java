package org.evosuite.ga.operators.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.PreferenceSortingComparator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.statements.ArrayStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.StringPrimitiveStatement;
import org.evosuite.testcase.variable.VariableReference;

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
		
		for(T t: solutions) {
			removeUnusedVariables(t);
			System.currentTimeMillis();
		}
		
		fronts = new ArrayList<List<T>>(solutions.size());
		
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
				List<T> partialFront = new ArrayList<>();
				List<T> rankedSolutionsCopy = new ArrayList<>(map.get(ff));
				Double bestValue = bestValues.get(ff);
				
				Iterator<T> iter = rankedSolutionsCopy.iterator();
				while(iter.hasNext() && partialFront.size() < Properties.LOCAL_OPTIMAL_NUMBER) {
					
					T individual = iter.next();
					if(!solutionsCopy.contains(individual)) {
						iter.remove();
						continue;
					}
					
					Double value = individual.getFitness(ff);
					
					if (value == Double.MAX_VALUE) {
						continue;
					}
					
					if(bestValue == null || value > bestValue) {
						if(!front.contains(individual) && checkDuplication(front, individual) < Properties.DUPLICATED_TESTCASE_LIMIT) {
							partialFront.add(individual);
							solutionsCopy.remove(individual);
							iter.remove();
						}
						
					}		
					bestValues.put(ff, value);
				}		
				front.addAll(partialFront);
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

	private int checkDuplication(List<T> front, T individual) {
		int count = 0;
		
		for(T t: front) {
			
			if(t.toString().equals(individual.toString())) {
				count ++;
			}
		}
		
		return count;
	}
	
	/**
	 * When a test case is changed via crossover and/or mutation, it can contains some
	 * primitive variables that are not used as input (or to store the output) of method calls.
	 * Thus, this method removes all these "trash" statements.
	 * 
	 * @param chromosome
	 * @return true or false depending on whether "unused variables" are removed
	 */
	private boolean removeUnusedVariables(T chromosome) {
		int sizeBefore = chromosome.size();
		TestCase t = ((TestChromosome) chromosome).getTestCase();
		List<Integer> to_delete = new ArrayList<Integer>(chromosome.size());
		boolean has_deleted = false;

		int num = 0;
		for (Statement s : t) {
			VariableReference var = s.getReturnValue();
			boolean delete = false;
			delete = delete || s instanceof PrimitiveStatement;
			delete = delete || s instanceof ArrayStatement;
			delete = delete || s instanceof StringPrimitiveStatement;
			if (!t.hasReferences(var) && delete) {
				to_delete.add(num);
				has_deleted = true;
			}
			num++;
		}
		Collections.sort(to_delete, Collections.reverseOrder());
		for (Integer position : to_delete) {
			t.remove(position);
		}
		int sizeAfter = chromosome.size();
		return has_deleted;
	}

	private List<T> rank(List<T> solutions, FitnessFunction<T> ff) {
		Collections.sort(solutions, new PreferenceSortingComparator<T>(ff));
		return solutions;
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
