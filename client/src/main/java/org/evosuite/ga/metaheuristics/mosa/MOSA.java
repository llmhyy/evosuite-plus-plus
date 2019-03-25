/**
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.ga.metaheuristics.mosa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.CoverageProgressGetter;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.StatisticChecker;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.exception.ExceptionCoverageFactory;
import org.evosuite.coverage.exception.ExceptionCoverageTestFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.mosa.comparators.OnlyCrowdingComparator;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.DistributionUtil;
import org.objectweb.asm.tree.MethodInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the MOSA (Many-Objective Sorting Algorithm) described in the ICST'15 paper ...
 * 
 * @author Annibale Panichella, Fitsum M. Kifetew
 *
 * @param <T>
 */
public class MOSA<T extends Chromosome> extends AbstractMOSA<T> {

	private static final long serialVersionUID = 146182080947267628L;

	private static final Logger logger = LoggerFactory.getLogger(MOSA.class);

	/** Map used to store the covered test goals (keys of the map) and the corresponding covering test cases (values of the map) **/
	protected Map<FitnessFunction<T>, T> archive = new LinkedHashMap<FitnessFunction<T>, T>();

	/** Boolean vector to indicate whether each test goal is covered or not. **/
	protected Set<FitnessFunction<T>> uncoveredGoals = new LinkedHashSet<FitnessFunction<T>>();

	protected CrowdingDistance<T> distance = new CrowdingDistance<T>();
	
	/**
	 * Constructor based on the abstract class {@link AbstractMOSA}
	 * @param factory
	 */
	public MOSA(ChromosomeFactory<T> factory) {
		super(factory);
	}
	
	class FitnessWrapper{
		FitnessFunction<T> ff;
		Branch branch;
		FitnessWrapper parent;
		public FitnessWrapper(FitnessFunction<T> ff, Branch branch, MOSA<T>.FitnessWrapper parent) {
			super();
			this.ff = ff;
			this.branch = branch;
			this.parent = parent;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Set<FitnessFunction<T>> findDominateUncoveredGoals(Set<FitnessFunction<T>> uncoveredGoals){
		List<FitnessWrapper> list = new ArrayList<>();
		for(FitnessFunction<T> ff: uncoveredGoals) {
			Branch branch = null;
			if(ff instanceof FBranchTestFitness) {
				branch = ((FBranchTestFitness)ff).getBranch();
			}
			else if(ff instanceof BranchCoverageTestFitness) {
				branch = ((BranchCoverageTestFitness)ff).getBranch();
			}
			
			FitnessWrapper fWrapper = new FitnessWrapper(ff, branch, null);
			list.add(fWrapper);
		}
		
		for(int i=0; i<list.size(); i++) {
			FitnessWrapper w1 = list.get(i);
			for(int j=i; j<list.size(); j++) {
				FitnessWrapper w2 = list.get(j);
				if(i!=j && !w1.branch.equals(w2.branch)) {
					if(w1.branch.getInstruction().getControlDependentBranch()!=null && 
							w1.branch.getInstruction().getControlDependentBranch().equals(w2.branch)) {
						w1.parent = w2;
					}
					
					if(w2.branch.getInstruction().getControlDependentBranch()!=null &&
							w2.branch.getInstruction().getControlDependentBranch().equals(w1.branch)) {
						w2.parent = w1;
					}
				}
			}
		}
		
		Set<FitnessFunction<T>> set = new HashSet<>();
		for(FitnessWrapper w: list) {
			if(w.parent == null) {
				set.add(w.ff);
			}
			else if(!uncoveredGoals.contains(w.parent.ff)) {
				set.add(w.ff);
			}
		}
		
		return set;
	}

	/** {@inheritDoc} */
	@Override
	protected void evolve() {
		this.newGeneratedIndividuals.clear();
		
		Set<FitnessFunction<T>> dominateUncoveredGoals = findDominateUncoveredGoals(uncoveredGoals);
		List<T> offspringPopulation = breedNextGeneration(dominateUncoveredGoals);

		// Create the union of parents and offSpring
		List<T> union = new ArrayList<T>();
		union.addAll(population);
		union.addAll(offspringPopulation);

		
		// Ranking the union
		logger.debug("Union Size =" + union.size());
		// Ranking the union using the best rank algorithm (modified version of the non dominated sorting algorithm
		ranking.computeRankingAssignment(union, dominateUncoveredGoals);

		// add to the archive the new covered goals (and the corresponding test cases)
		//this.archive.putAll(ranking.getNewCoveredGoals());

		
		List<T> reservedIndividuals = getReservedIndividual(union, dominateUncoveredGoals);
		int remain = population.size();
		remain = remain - reservedIndividuals.size();
		
		population.clear();
		population.addAll(reservedIndividuals);

		int index = 0;
		List<T> front = null;
		// Obtain the next front
		front = ranking.getSubfront(index);
		
		while ((remain > 0) && (remain >= front.size()) && !front.isEmpty()) {
			// Assign crowding distance to individuals
			distance.fastEpsilonDominanceAssignment(front, dominateUncoveredGoals);
			// Add the individuals of this front
			population.addAll(front);

			// Decrement remain
			remain = remain - front.size();

			// Obtain the next front
			index++;
			if (remain > 0) {
				front = ranking.getSubfront(index);
			} // if
		} // while

		// Remain is less than front(index).size, insert only the best one
		if (remain > 0 && !front.isEmpty()) { // front contains individuals to insert
			distance.fastEpsilonDominanceAssignment(front, dominateUncoveredGoals);
			Collections.sort(front, new OnlyCrowdingComparator());
			for (int k = 0; k < remain; k++) {
				population.add(front.get(k));
			} // for

			remain = 0;
		} // if
		currentIteration++;
		
		printBestIndividualForUncoveredGoals(dominateUncoveredGoals);
		
		//logger.error("");
		//logger.error("N. fronts = "+ranking.getNumberOfSubfronts());
		//logger.debug("1* front size = "+ranking.getSubfront(0).size());
		//logger.debug("2* front size = "+ranking.getSubfront(1).size());
		//logger.error("Covered goals = "+this.archive.size());
		//logger.error("Uncovered goals = "+uncoveredGoals.size());
		//logger.debug("Generation=" + currentIteration + " Population Size=" + population.size() + " Archive size=" + archive.size());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBestIndividuals() {
		//get final test suite (i.e., non dominated solutions in Archive)
		TestSuiteChromosome bestTestCases = new TestSuiteChromosome();
		for (T test : getFinalTestSuite()) {
			bestTestCases.addTest((TestChromosome) test);
		}
		for (FitnessFunction<T> f : this.getCoveredGoals()){
			bestTestCases.getCoveredGoals().add((TestFitnessFunction) f);
		}
		// compute overall fitness and coverage
		double fitness = this.fitnessFunctions.size() - numberOfCoveredTargets();
		double coverage = ((double) numberOfCoveredTargets()) / ((double) this.fitnessFunctions.size());
		for (TestSuiteFitnessFunction suiteFitness : suiteFitnesses){
			bestTestCases.setFitness(suiteFitness, fitness);
			bestTestCases.setCoverage(suiteFitness, coverage);
			bestTestCases.setNumOfCoveredGoals(suiteFitness, (int) numberOfCoveredTargets());
			bestTestCases.setNumOfNotCoveredGoals(suiteFitness, (int) (this.fitnessFunctions.size()-numberOfCoveredTargets()));
		}
		
		bestTestCases.setAge(this.currentIteration);
		bestTestCases.setProgressInfomation(getProgressInformation());
		bestTestCases.setUncoveredBranchDistribution(this.getUncoveredBranchDistribution());
		
		Set<FitnessFunction<T>> IPFlags = findIPFlagBranches();
		Set<FitnessFunction<T>> uncoveredIPFlags = findUncoveredIPFlags(IPFlags);
		
		bestTestCases.setUncoveredIPFlags(toIPFlagString(uncoveredIPFlags));
		
		double IPFlagCoverage = (double)uncoveredIPFlags.size()/IPFlags.size();
		bestTestCases.setIPFlagCoverage(1-IPFlagCoverage);
		
		List<T> bests = new ArrayList<T>(1);
		bests.add((T) bestTestCases);
		return bests;
	}
	
	private String toIPFlagString(Set<FitnessFunction<T>> uncoveredIPFlags) {
		StringBuffer buffer = new StringBuffer();
		for(FitnessFunction<T> ff: uncoveredIPFlags) {
			BranchCoverageGoal goal = transformBranchCoverage(ff);
			buffer.append(goal + "\n");
		}
		return buffer.toString();
	}
	
	private Set<FitnessFunction<T>> findUncoveredIPFlags(Set<FitnessFunction<T>> iPFlags) {
		Set<FitnessFunction<T>> uncoveredIPFlags = new HashSet<>();
		for(FitnessFunction<T> goal: iPFlags) {
			if(uncoveredGoals.contains(goal)) {
				uncoveredIPFlags.add(goal);
			}
		}
		return uncoveredIPFlags;
	}

	private String getReturnType(String signature) {
		String r = signature.substring(signature.indexOf(")") + 1);
		return r;
	}
	
	private boolean isInterproceduralFlagProblem(BranchCoverageGoal goal) {
		BytecodeInstruction instruction = goal.getBranch().getInstruction();
		
		BytecodeInstruction interproceduralFlagCall = instruction.getSourceOfStackInstruction(0);
		boolean isInterproceduralFlag = false;
		if (interproceduralFlagCall != null && interproceduralFlagCall.getASMNode() instanceof MethodInsnNode) {
			MethodInsnNode mNode = (MethodInsnNode) interproceduralFlagCall.getASMNode();
			String desc = mNode.desc;
			String returnType = getReturnType(desc);
			isInterproceduralFlag = returnType.equals("Z");
		}

		
		return isInterproceduralFlag;
	}
	
	private BranchCoverageGoal transformBranchCoverage(FitnessFunction<T> fitnessFunction) {
		BranchCoverageGoal goal = null;
		if(fitnessFunction instanceof FBranchTestFitness) {
			goal = ((FBranchTestFitness)fitnessFunction).getBranchGoal();
		}
		else if(fitnessFunction instanceof BranchCoverageTestFitness) {
			goal = ((BranchCoverageTestFitness)fitnessFunction).getBranchGoal();
		}
		
		return goal;
	}
	
	private Set<FitnessFunction<T>> findIPFlagBranches(){
		Set<FitnessFunction<T>> IPFlagBranches = new HashSet<>();
		for(FitnessFunction<T> fitnessFunction: fitnessFunctions) {
			BranchCoverageGoal goal = transformBranchCoverage(fitnessFunction);
			if(goal != null && isInterproceduralFlagProblem(goal)) {
				IPFlagBranches.add(fitnessFunction);
			}
			
		}
		
		return IPFlagBranches;
	}

	private List<T> getReservedIndividual(List<T> population, Set<FitnessFunction<T>> uncoveredGoals) {
		List<T> reservedIndividuals = new ArrayList<>();
		for(FitnessFunction<T> uncoveredGoal: uncoveredGoals) {
			double[] values = new double[] {-1, -1};
			List<T> tmpReservedIndividuals = new ArrayList<>();
			for(int i=0; i<population.size(); i++) {
				T individual = population.get(i);
				if(reservedIndividuals.contains(individual)) {
					continue;
				}
				
				double value = uncoveredGoal.getFitness(individual);
				if(tmpReservedIndividuals.isEmpty()) {
					tmpReservedIndividuals.add(individual);
					values[0] = value;
				}
				else if(tmpReservedIndividuals.size() == 1) {
					if(value > values[0]) {
						values[1] = value;
						tmpReservedIndividuals.add(1, individual);
					}
					else if(value < values[0]) {
						values[1] = values[0];
						tmpReservedIndividuals.add(1, tmpReservedIndividuals.get(0));
						values[0] = value;
						tmpReservedIndividuals.set(0, individual);
					}
				}
				else {
					if(value < values[0]) {
						values[1] = values[0];
						tmpReservedIndividuals.set(1, tmpReservedIndividuals.get(0));
						values[0] = value;
						tmpReservedIndividuals.set(0, individual);
					}
					else if(value > values[0] && value < values[1]) {
						values[1] = value;
						tmpReservedIndividuals.set(1, individual);
					}
				}
			}
			
			reservedIndividuals.addAll(tmpReservedIndividuals);			
		}
		
		return reservedIndividuals;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	protected void calculateFitness(T c) {
		for (FitnessFunction<T> fitnessFunction : this.fitnessFunctions) {
			double value = fitnessFunction.getFitness(c);
			if (value == 0.0) {
				//((TestChromosome)c).addCoveredGoals(fitnessFunction);
				updateArchive(c, fitnessFunction);
			}
		}
		if (ArrayUtil.contains(Properties.CRITERION, Criterion.EXCEPTION)){
			// if one of the coverage criterion is Criterion.EXCEPTION,
			// then we have to analyze the results of the execution do look
			// for generated exceptions
			List<ExceptionCoverageTestFitness> list = deriveCoveredExceptions(c);
			for (ExceptionCoverageTestFitness exp : list){
				// new covered exceptions (goals) have to be added to the archive
				updateArchive(c, (FitnessFunction<T>) exp);
				if (!fitnessFunctions.contains(exp)){
					// let's update the list of fitness functions 
					this.fitnessFunctions.add((FitnessFunction<T>) exp);
					// let's update the newly discovered exceptions to ExceptionCoverageFactory 
					ExceptionCoverageFactory.getGoals().put(exp.toString(), exp);
				}
			}
		}
		notifyEvaluation(c);
	}

	public double getCurrentCoverage() {
		double coveredBranches = numberOfCoveredTargets();
		double coverage = coveredBranches / (coveredBranches + uncoveredGoals.size());
		return coverage;
	}
	
	private void updateDistribution(Map<Integer, Integer> distributionMap, boolean firstTime) {
		if (distributionMap.keySet().size() == 0) {
			return;
		}

		List<T> individuals = firstTime ? this.population : this.newGeneratedIndividuals;
		for (T individual : individuals) {
			TestChromosome test = (TestChromosome) individual;
			ExecutionResult result = test.getLastExecutionResult();
			if (result != null) {
				for (Integer branchID : result.getTrace().getCoveredTrue().keySet()) {
					if (distributionMap.get(branchID) != null) {
						int count = distributionMap.get(branchID) + 1;
						distributionMap.put(branchID, count);
					}
				}

				for (Integer branchID : result.getTrace().getCoveredFalse().keySet()) {
					if (distributionMap.get(-branchID) != null) {
						int count = distributionMap.get(-branchID) + 1;
						distributionMap.put(-branchID, count);
					}
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void generateSolution() {
		logger.info("executing generateSolution function");

		// keep track of covered goals
		for (FitnessFunction<T> goal : fitnessFunctions) {
			uncoveredGoals.add(goal);
		}

		//initialize population
		if (population.isEmpty())
			initializePopulation();

		// Calculate dominance ranks and crowding distance
		ranking.computeRankingAssignment(population, this.uncoveredGoals);
		for (int i = 0; i<ranking.getNumberOfSubfronts(); i++){
			distance.fastEpsilonDominanceAssignment(ranking.getSubfront(i), this.uncoveredGoals);
		}

		BranchCoverageFactory branchFactory = new BranchCoverageFactory();
		List<BranchCoverageTestFitness> branchGoals = branchFactory.getCoverageGoals();
		Map<Integer, Integer> distributionMap = DistributionUtil.constructDistributionMap(branchGoals);
		updateDistribution(distributionMap, true);
		
		double currentCoverage = getCurrentCoverage();
		this.getProgressInformation().add(currentCoverage);
		
		StatisticChecker timer = new StatisticChecker(getProgressInformation(),
			new CoverageProgressGetter() {
				@Override
				public double getCoverage() {
					return getCurrentCoverage();
				}
		});
		Thread timerThread = new Thread(timer);
		timerThread.start();
		
		// TODO add here dynamic stopping condition
		while (!isFinished() && this.getNumberOfCoveredGoals()<this.fitnessFunctions.size()) {
			this.currentIteration++;
			evolve();
			updateDistribution(distributionMap, false);
			notifyIteration();
		}

		notifySearchFinished();
		updateDistribution(distributionMap, false);
		
		Map<Integer, Double> uncoveredBranchDistribution = DistributionUtil.computeBranchDistribution(distributionMap, branchGoals);
		this.setUncoveredBranchDistribution(uncoveredBranchDistribution);
	}

	private void printBestIndividualForUncoveredGoals(Set<FitnessFunction<T>> dominateUncoveredGoals) {
		logger.error("============");
		List<T> firstFront = ranking.getSubfront(0);
		for(FitnessFunction<T> ff: dominateUncoveredGoals) {
			if(ff instanceof TestFitnessFunction) {
				TestFitnessFunction tff = (TestFitnessFunction)ff;
				TestChromosome bestIndividual = getBestTest(firstFront, tff);
				if(bestIndividual != null) {
					double fit = tff.getFitness(bestIndividual);
					if(tff instanceof FBranchTestFitness) {
						logger.error(((FBranchTestFitness)tff).getBranchGoal() + ": " + fit);	
					}
					else {
						logger.error(tff + ": " + fit);											
					}
				}
			}
		}
		int total = (int) (numberOfCoveredTargets() + uncoveredGoals.size());
		logger.error("uncovered goals: " + uncoveredGoals.size() + "/" + total);		
	}

	private TestChromosome getBestTest(List<T> firstFront, TestFitnessFunction tff) {
		TestChromosome best = null;
		double fit = -1;
		for(T t: firstFront) {
			if(t instanceof TestChromosome) {
				TestChromosome test = (TestChromosome)t;
				if(best == null) {
					fit = tff.getFitness(test);
					best = test;
				}
				else {
					double f = tff.getFitness(test);
					if(f < fit) {
						best = test;
						fit = f;
					}
				}
			}
		}
		return best;
	}

	/** This method is used to print the number of test goals covered by the test cases stored in the current archive **/
	private int getNumberOfCoveredGoals() {
		int n_covered_goals = this.archive.keySet().size();
		logger.debug("# Covered Goals = " + n_covered_goals);
		return n_covered_goals;
	}

	/** This method return the test goals covered by the test cases stored in the current archive **/
	public Set<FitnessFunction<T>> getCoveredGoals() {
		return this.archive.keySet();
	}

	/**
	 * This method update the archive by adding test cases that cover new test goals, or replacing the
	 * old tests if the new ones are smaller (at the same level of coverage).
	 * 
	 * @param solutionSet is the list of Chromosomes (population)
	 */
	private void updateArchive(T solution, FitnessFunction<T> covered) {
		// the next two lines are needed since that coverage information are used
		// during EvoSuite post-processing
		TestChromosome tch = (TestChromosome) solution;
		tch.getTestCase().getCoveredGoals().add((TestFitnessFunction) covered);

		// store the test cases that are optimal for the test goal in the
		// archive
		if (archive.containsKey(covered)){
			int bestSize = this.archive.get(covered).size();
			int size = solution.size();
			if (size < bestSize)
				this.archive.put(covered, solution);
		} else {
			archive.put(covered, solution);
			this.uncoveredGoals.remove(covered);
		}
	}

	protected List<T> getArchive() {
		Set<T> set = new LinkedHashSet<T>(); 
		set.addAll(archive.values());
		List<T> arch = new ArrayList<T>();
		arch.addAll(set);
		return arch;
	}

	protected List<T> getFinalTestSuite() {
		// trivial case where there are no branches to cover or the archive is empty
		if (this.getNumberOfCoveredGoals()==0) {
			return getArchive();
		}
		if (archive.size() == 0)
			if (population.size() > 0) {
				ArrayList<T> list = new ArrayList<T>();
				list.add(population.get(population.size() - 1));
				return list;
			} else
				return getArchive();
		List<T> final_tests = getArchive();
		List<T> tests = this.getNonDominatedSolutions(final_tests);
		return tests;
	}

	/**
	 * This method is used by the Progress Monitor at the and of each generation to show the totol coverage reached by the algorithm.
	 * Since the Progress Monitor need a "Suite", this method artificially creates a "SuiteChromosome" (see {@link MOSA#suiteFitness}) 
	 * as the union of all test cases stored in {@link MOSA#archive}. 
	 * 
	 * The coverage score of the "SuiteChromosome" is given by the percentage of test goals covered (goals in {@link MOSA#archive})
	 * onto the total number of goals <code> this.fitnessFunctions</code> (see {@link GeneticAlgorithm}).
	 * 
	 * @return "SuiteChromosome" directly consumable by the Progress Monitor.
	 */
	@Override @SuppressWarnings("unchecked")
	public T getBestIndividual() {
		TestSuiteChromosome best = new TestSuiteChromosome();
		for (T test : getArchive()) {
			best.addTest((TestChromosome) test);
		}
		// compute overall fitness and coverage
		double coverage = ((double) this.getNumberOfCoveredGoals()) / ((double) this.fitnessFunctions.size());
		for (TestSuiteFitnessFunction suiteFitness : suiteFitnesses){
			best.setCoverage(suiteFitness, coverage);
			best.setFitness(suiteFitness,  this.fitnessFunctions.size() - this.getNumberOfCoveredGoals());
		}
		//suiteFitness.getFitness(best);
		return (T) best;
	}

	protected double numberOfCoveredTargets(){
		return this.archive.keySet().size();
	}
}
