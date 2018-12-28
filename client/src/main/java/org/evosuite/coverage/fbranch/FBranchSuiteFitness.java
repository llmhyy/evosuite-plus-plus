package org.evosuite.coverage.fbranch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.archive.TestsArchive;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

public class FBranchSuiteFitness extends TestSuiteFitnessFunction {
	private static final long serialVersionUID = 4220214075514277335L;

	private final List<FBranchTestFitness> branchGoals;

	private int totGoals;

//	private final Map<Integer, Map<CallContext, Set<FBranchTestFitness>>> goalsMap;
//
//	/** Branchless methods map. */
//	private final Map<String, Map<CallContext, FBranchTestFitness>> methodsMap;

	private final Set<FBranchTestFitness> toRemoveBranchesT = new HashSet<>();
	private final Set<FBranchTestFitness> toRemoveBranchesF = new HashSet<>();
//	private final Set<FBranchTestFitness> toRemoveRootBranches = new HashSet<>();

	private final Set<FBranchTestFitness> removedBranchesT = new HashSet<>();
	private final Set<FBranchTestFitness> removedBranchesF = new HashSet<>();
	private final Set<FBranchTestFitness> removedRootBranches = new HashSet<>();

	public FBranchSuiteFitness() {
//		goalsMap = new HashMap<>();
//		methodsMap = new HashMap<>();
		FBranchFitnessFactory factory = new FBranchFitnessFactory();
		branchGoals = factory.getCoverageGoals();
		totGoals = branchGoals.size();
	}

	@Override
	public double getFitness(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		double fitness = 0.0; // branchFitness.getFitness(suite);
		List<ExecutionResult> results = runTestSuite(suite);

		Map<FBranchTestFitness, Double> distanceMap = new HashMap<>();
		Map<FBranchTestFitness, Integer> callCount = new HashMap<>();

		for (ExecutionResult result : results) {
			for (Integer branchId : result.getTrace().getTrueDistances().keySet()) {
				FBranchTestFitness goalT = getGoal(branchId, true);
				if (goalT == null || removedBranchesT.contains(goalT))
					continue;
				double distanceT = normalize(result.getTrace().getTrueDistances().get(branchId));
				if (distanceMap.get(goalT) == null || distanceMap.get(goalT) > distanceT) {
					distanceMap.put(goalT, distanceT);
				}
				if (Double.compare(distanceT, 0.0) == 0) {
					result.test.addCoveredGoal(goalT);
					if (Properties.TEST_ARCHIVE) {
						TestsArchive.instance.putTest(this, goalT, result);
						toRemoveBranchesT.add(goalT);
						suite.isToBeUpdated(true);
					}
				}
			}

			for (Integer branchId : result.getTrace().getFalseDistances().keySet()) {
				FBranchTestFitness goalF = getGoal(branchId, false);
				if (goalF == null || removedBranchesF.contains(goalF))
					continue;
				double distanceF = normalize(result.getTrace().getFalseDistances().get(branchId));
				if (distanceMap.get(goalF) == null || distanceMap.get(goalF) > distanceF) {
					distanceMap.put(goalF, distanceF);
				}
				if (Double.compare(distanceF, 0.0) == 0) {
					result.test.addCoveredGoal(goalF);
					if (Properties.TEST_ARCHIVE) {
						TestsArchive.instance.putTest(this, goalF, result);
						toRemoveBranchesF.add(goalF);
						suite.isToBeUpdated(true);
					}
				}
			}
		}

		int numCoveredGoals = 0;
		for (FBranchTestFitness goal : branchGoals) {
			Double distance = distanceMap.get(goal);
			if (distance == null)
				distance = 1.0;

			if (goal.getBranch() == null) {
				Integer count = callCount.get(goal);
				if (count == null || count == 0) {
					fitness += 1;
				} else {
					numCoveredGoals++;
				}
			} else {
				if (distance == 0.0) {
					numCoveredGoals++;
				}
				fitness += distance;
			}
		}

		numCoveredGoals += removedBranchesF.size();
		numCoveredGoals += removedBranchesT.size();
		numCoveredGoals += removedRootBranches.size();
		if (totGoals > 0) {
			suite.setCoverage(this, (double) numCoveredGoals / (double) totGoals);
		}
		suite.setNumOfCoveredGoals(this, numCoveredGoals);
		suite.setNumOfNotCoveredGoals(this, totGoals - numCoveredGoals);
		updateIndividual(this, suite, fitness);

		return fitness;
	}

	private FBranchTestFitness getGoal(Integer branchId, boolean b) {
		for(FBranchTestFitness ff: branchGoals){
			if(ff.getBranch().getActualBranchId()==branchId){
				if(ff.getBranchGoal().getValue()==b){
					return ff;
				}
			}
		}
		
		return null;
	}

}
