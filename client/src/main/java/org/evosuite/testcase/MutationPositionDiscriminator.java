package org.evosuite.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.utils.Randomness;

public class MutationPositionDiscriminator <T extends Chromosome> {
	@SuppressWarnings("rawtypes")
	public static MutationPositionDiscriminator discriminator = new MutationPositionDiscriminator<Chromosome>();
	
	public int frozenIteration = Properties.PRE_ONLINE_LEARNING_ITERATION;
	
	public Set<FitnessFunction<T>> currentGoals = new java.util.HashSet<>();
	
	private MutationPositionDiscriminator() {}
	
	/**
	 * Use this constant to calculate the relevance of statement. 
	 */
	private static final double MAX_POWER = 100;
	
	
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
		currentGoals.clear();
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
			
			//TODO aaron
			Object diff = parseMutation(newTest, oldTest);
			
			Set<FitnessFunction<?>> relvantBranches = analyzeRelevantBranch(diff, newTest.getFitnessValues().keySet());
			Map<Branch, List<ComputationPath>> map = parseComputationPath(newTest);
			
			updateChangeRelevanceMap(changedFitnesses, newTest.getTestCase());
			updateChangeRelevanceMap(changedFitnesses, oldTest.getTestCase());
		}
	}

	private static Map<Branch, List<ComputationPath>> parseComputationPath(TestChromosome newTest) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Set<FitnessFunction<?>> analyzeRelevantBranch(Object diff, Set<FitnessFunction<?>> keySet) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Object parseMutation(TestChromosome newTest, TestChromosome oldTest) {
		// TODO Auto-generated method stub
		return null;
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
	
	@SuppressWarnings({ "unchecked"})
	/**
	 * generate a mutation probability matrix, return a relevance matrix on how likely a statement is going to change.
	 * @param size
	 * @return
	 */
	public static double[] calculateMutationProbability(TestChromosome test) {
		int size = test.size() - 1;
		if(size<=0) {
			return new double[0];
		}
		
		Set<FitnessFunction<? extends Chromosome>> currentGoalSet = MutationPositionDiscriminator.discriminator.currentGoals;
		
		if(currentGoalSet.isEmpty()) {
			double[] distribution = new double[size];
			for(int i=0; i<size; i++) {
				distribution[i] = 1d/size;
			}
			return distribution;
		}
		
		List<FitnessFunction<? extends Chromosome>> currentGoals = new ArrayList<>(currentGoalSet);
		
		double[][] relevanceMatrix = constructRelevanceMatrix(size, test, currentGoals);
		List<List<Integer>> clusters = clusterGoals(relevanceMatrix);
		
//		System.currentTimeMillis();
		
		List<double[]> mutationProbabilityList = extractMutationProbabilityList(size, test,
				currentGoals, clusters);
		
		/**
		 * The distribution correspond to the size of cluster.
		 */
		double[] probabilityDistribution = deriveProbabilityDistributionFromClusters(clusters);
		
		int index = pickRandomIndex(probabilityDistribution);
		
		return mutationProbabilityList.get(index);
	}

	private static int pickRandomIndex(double[] probabilityDistribution) {
		double random = Randomness.nextDouble();
		int index = 0;
		for(int i=0; i<probabilityDistribution.length; i++) {
			double lowerBound = (i==0) ? 0 : probabilityDistribution[i-1];
			double upperBound = probabilityDistribution[i];
			if(lowerBound < random && random <= upperBound) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * The distribution correspond to the size of cluster.
	 * @param clusters
	 * @return
	 */
	private static double[] deriveProbabilityDistributionFromClusters(List<List<Integer>> clusters) {
		double[] probability = new double[clusters.size()];
		double sum = 0d;
		for(int i=0; i<probability.length; i++) {
			probability[i] = (double) (clusters.get(i).size());
			sum += probability[i];
		}
		
		for(int i=0; i<probability.length; i++) {
			probability[i] /= sum;
		}
		
		for(int i=0; i<probability.length-1; i++) {
			probability[i+1] += probability[i];
		}
		return probability;
	}

	private static List<List<Integer>> clusterGoals(double[][] relevanceMatrix) {
		List<List<Integer>> clusters = new ArrayList<>();
		List<Integer> markedGoals = new ArrayList<>();
		
		int totalGoalNum = relevanceMatrix[0].length; 
		while(markedGoals.size() < totalGoalNum) {
			List<Integer> cluster = new ArrayList<>();
			for(int j=0; j<totalGoalNum; j++) {
				if(markedGoals.contains(j)) {
					continue;
				}
				
				if(cluster.isEmpty()) {
					cluster.add(j);
					markedGoals.add(j);
				}
				else {
					int goalIndex = cluster.get(0);
					boolean isSimilar = compareSimilarity(goalIndex, j, relevanceMatrix);
					if(isSimilar) {
						cluster.add(j);
						markedGoals.add(j);
					}
					
				}
			}		
			
			clusters.add(cluster);
		}
		
		return clusters;
	}

	private static boolean compareSimilarity(int goalIndex, int j, double[][] relevanceMatrix) {
		double delta = 0.1;
		
		for(int i=0; i<relevanceMatrix.length; i++) {
			double goalIndexValue = relevanceMatrix[i][goalIndex];
			double jValue = relevanceMatrix[i][j];
			
			if(1-delta<goalIndexValue && goalIndexValue < 1+delta &&
					1-delta<jValue && jValue < 1+delta) {
				continue;
			}
			else if(goalIndexValue>1 && jValue<1) {
				return false;
			}
			else if(goalIndexValue<1 && jValue>1) {
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * we choose the two largest position as force mutation position
	 * 
	 * @param mutationProbability
	 * @return
	 */
	public static List<Integer> checkForceMutationPosition(double[] mutationProbability) {
		List<Integer> indexes = new ArrayList<Integer>();
		List<Double> values = new ArrayList<Double>();
		
		for(int i=0; i<mutationProbability.length; i++) {
			if(values.size() < 2) {
				indexes.add(i);
				values.add(mutationProbability[i]);
			}
			
			if(values.size()==2) {
				if(values.get(0) < values.get(1)) {
					Double tmp = values.get(1);
					values.set(1, values.get(0));
					values.set(0, tmp);
					
					Integer iTemp = indexes.get(1);
					indexes.set(1, indexes.get(0));
					indexes.set(0, iTemp);
				}
				
				if(i>=2 && mutationProbability[i] > values.get(1)) {
					indexes.set(1, i);
					values.set(1, mutationProbability[i]);
				}
			}
		}
		
		return indexes;
	}

	@SuppressWarnings("rawtypes")
	public static List<double[]> extractMutationProbabilityList(int lastMutatableStatement,
			TestChromosome test, List<FitnessFunction<? extends Chromosome>> currentGoals, List<List<Integer>> clusters) {
		List<double[]> mutationProbabilityList = new ArrayList<>();
		for(List<Integer> cluster: clusters) {
			double[] mutationProbabililty = new double[lastMutatableStatement+1];
			
			Double sum = 0d;
			for(int i=0; i<lastMutatableStatement+1; i++) {
				mutationProbabililty[i] = 1;
				
				Statement statement = test.getTestCase().getStatement(i);
				Map<FitnessFunction, Pair<Double, Double>> map = statement.getChangeRelevanceMap();
				for(Integer index: cluster) {
					FitnessFunction<?> ff = currentGoals.get(index);
					Pair<Double, Double> effectFrequency = map.get(ff);
					if(effectFrequency != null) {
						Double positiveEffect = effectFrequency.getLeft();
						Double negativeEffect = effectFrequency.getRight();
						
						double base = positiveEffect + negativeEffect;
						double alpha = 1;
						if(base > 10) {
							if(negativeEffect==0 && positiveEffect != 0) {
								alpha = MAX_POWER;
							}
							else {
								alpha = positiveEffect / negativeEffect;
							}
						}
						
						mutationProbabililty[i] += base * alpha * alpha;
						sum += mutationProbabililty[i];
					}
				}
			}
			
			if(sum==0) {
				sum = (double) (lastMutatableStatement+1);
			}
			
			for(int i=0; i<lastMutatableStatement+1; i++) {
				mutationProbabililty[i] =  mutationProbabililty[i] / sum;
			}
			
			mutationProbabilityList.add(mutationProbabililty);
		}
		return mutationProbabilityList;
	}

	@SuppressWarnings("rawtypes")
	private static double[][] constructRelevanceMatrix(int testcaseSize,
			TestChromosome test, List<FitnessFunction<? extends Chromosome>> currentGoals) {
		double[][] relevanceMatrix = new double[test.size()][currentGoals.size()];
		
		for(int i=0; i<testcaseSize; i++) {
			Statement statement = test.getTestCase().getStatement(i);
			Map<FitnessFunction, Pair<Double, Double>> map = statement.getChangeRelevanceMap();
			for(int j=0; j<currentGoals.size(); j++) {
				FitnessFunction<?> ff = currentGoals.get(j);
				Pair<Double, Double> effectFrequency = map.get(ff);
				if(effectFrequency != null) {
					Double positiveEffect = effectFrequency.getLeft();
					Double negativeEffect = effectFrequency.getRight();
					
					double base = positiveEffect + negativeEffect;
					if(base > 10) {
						if(negativeEffect==0 && positiveEffect != 0) {
							relevanceMatrix[i][j] = MAX_POWER;
						}
						else {
							relevanceMatrix[i][j] = positiveEffect / negativeEffect;
						}
					}
					else {
						relevanceMatrix[i][j] = 1;
					}
					
				}
			}
		}
		return relevanceMatrix;
	}
	
	public static double[] normalizeProbability(double[] mutationProbabililty) {
		
		double max = max(mutationProbabililty);
		double ratio = 1/max;
		
		Double sum = 0d;
		for(int i=0; i<mutationProbabililty.length; i++) {
			double v = Math.pow(Math.E, mutationProbabililty[i] * ratio * 5);
			mutationProbabililty[i] = v;
			sum += mutationProbabililty[i];
		}
		
		/**
		 * get software max results
		 */
		for(int i=0; i<mutationProbabililty.length; i++) {
			mutationProbabililty[i] = mutationProbabililty[i]/sum;
		}
		
		/**
		 * normalize
		 */
		double newMax = max(mutationProbabililty);
		double newRatio = 1/newMax;
		for(int i=0; i<mutationProbabililty.length; i++) {
			mutationProbabililty[i] = mutationProbabililty[i] * newRatio;
		}
		
		
		return mutationProbabililty;
	}

	private static double max(double[] mutationProbabililty) {
		double max = 0;
		for(int i=0; i<mutationProbabililty.length; i++) {
			if(max < mutationProbabililty[i]) {
				max = mutationProbabililty[i];
			}
		}
		return max;
	}

	public void setPurpose(Map<FitnessFunction<T>, Double> fitnessValues) {
		currentGoals.clear();
		for(FitnessFunction<T> ff: fitnessValues.keySet()) {
			if(fitnessValues.get(ff) != 0) {
				if(ff instanceof BranchFitness) {
					BranchFitness bf = (BranchFitness)ff;
					currentGoals.add(ff);
				}
			}
		}
		
	}
}
