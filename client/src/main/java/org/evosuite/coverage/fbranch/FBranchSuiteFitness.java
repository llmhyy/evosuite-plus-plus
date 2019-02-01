package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.archive.TestsArchive;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageSuiteFitness;
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
		
//		double d = normalize(f);
		System.currentTimeMillis();
		
		return f;
	}

	@Override
	public double getFitness(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		
//		BranchCoverageSuiteFitness bFitness = new BranchCoverageSuiteFitness();
//		return bFitness.getFitness(suite);
		
		List<ExecutionResult> results = runTestSuite(suite);

		double[][] fitnessMatrix = new double[results.size()][branchGoals.size()];
		
		for (int i=0; i<results.size(); i++) {
			ExecutionResult result = results.get(i);
			TestChromosome tc = new TestChromosome();
			tc.setTestCase(result.test);
			
			for(int j=0; j<branchGoals.size(); j++) {
				FBranchTestFitness fBranchFitness = branchGoals.get(j);
				if(fBranchFitness.getBranch().getInstruction().getLineNumber()==1497) {
					System.currentTimeMillis();
				}
				
				
				double f = getTestFitness(fBranchFitness, tc, result);
				if(f<1 && f>0) {
					System.currentTimeMillis();
					getTestFitness(fBranchFitness, tc, result);
				}
				fitnessMatrix[i][j] = f;
			}
		}

		int numCoveredGoals = 0;
		double fitness = 0;
		for(int j=0; j<branchGoals.size(); j++) {
			List<Double> fitnessList = new ArrayList<>();
			for(int i=0; i<results.size(); i++) {
				fitnessList.add(fitnessMatrix[i][j]);
			}
			
			Collections.sort(fitnessList);
			
			double goalFitness = fitnessList.get(0);
			
			if(goalFitness > 1) {
				goalFitness = 1;
			}
			
//			goalFitness = 1;
//			for(double i=0; i<fitnessList.size(); i++) {
//				double d = fitnessList.get((int)i);
//				double transformedFitness = Math.pow(d, 1/(i+1));
//				goalFitness *= transformedFitness;
//			}
			
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
