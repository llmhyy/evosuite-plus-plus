package org.evosuite.coverage.fbranch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.archive.TestsArchive;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestChromosome;
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
	
	private double getTestFitness(FBranchTestFitness fBranchFitness, TestChromosome tc, ExecutionResult result) {
		
		double f = fBranchFitness.getFitness(tc, result);
		double d = normalize(f);
//		System.currentTimeMillis();
		
		return d;
	}

	@Override
	public double getFitness(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		List<ExecutionResult> results = runTestSuite(suite);

//		Map<FBranchTestFitness, Double> distanceMap = new HashMap<>();
//		Map<FBranchTestFitness, Integer> callCount = new HashMap<>();

		double[][] fitnessMatrix = new double[results.size()][branchGoals.size()];
		
		for (int i=0; i<results.size(); i++) {
			ExecutionResult result = results.get(i);
			TestChromosome tc = new TestChromosome();
			tc.setTestCase(result.test);
			
			for(int j=0; j<branchGoals.size(); j++) {
				FBranchTestFitness fBranchFitness = branchGoals.get(j);
				double f = getTestFitness(fBranchFitness, tc, result);
				fitnessMatrix[i][j] = f;
			}
		}

		int numCoveredGoals = 0;
		double fitness = 0;
		for(int j=0; j<branchGoals.size(); j++) {
			double goalFitness = 1;
			for(int i=0; i<results.size(); i++) {
				goalFitness *= fitnessMatrix[i][j];
			}
			
			if(goalFitness==0) {
				numCoveredGoals++;
			}
			
			fitness += goalFitness;
		}
		
		if (totGoals > 0) {
			suite.setCoverage(this, (double) numCoveredGoals / (double) totGoals);
		}
		suite.setNumOfCoveredGoals(this, numCoveredGoals);
		suite.setNumOfNotCoveredGoals(this, totGoals - numCoveredGoals);
		updateIndividual(this, suite, fitness);
		
		if(suite.getCoverage()==1) {
			fitness = 0;
		}

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
	
	@Override
	public int getTotalGoalNum(){
		return this.branchGoals.size();
	}

}
