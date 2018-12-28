package org.evosuite.coverage.fbranch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.testsuite.AbstractFitnessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author linyun
 *
 */
public class FBranchFitnessFactory extends AbstractFitnessFactory<FBranchTestFitness> {
	private static Logger logger = LoggerFactory.getLogger(FBranchFitnessFactory.class);

	/* (non-Javadoc)
	 * @see org.evosuite.coverage.TestFitnessFactory#getCoverageGoals()
	 */
	@Override
	public List<FBranchTestFitness> getCoverageGoals() {
		//TODO this creates duplicate goals. Momentary fixed using a Set.
		Set<FBranchTestFitness> goals = new HashSet<FBranchTestFitness>();

		// retrieve set of branches
		BranchCoverageFactory branchFactory = new BranchCoverageFactory();
		List<BranchCoverageTestFitness> branchGoals = branchFactory.getCoverageGoalsForAllKnownClasses();

//		CallGraph callGraph = DependencyAnalysis.getCallGraph();

		// try to find all occurrences of this branch in the call tree
		for (BranchCoverageTestFitness branchGoal : branchGoals) {
			goals.add(new FBranchTestFitness(branchGoal.getBranchGoal()));	
//			logger.info("Adding context branches for " + branchGoal.toString());
//			for (CallContext context : callGraph.getAllContextsFromTargetClass(branchGoal.getClassName(),
//				                          branchGoal.getMethod())) {
//				//if is not possible to reach this branch from the target class, continue.
//				if(context.isEmpty()) continue; 				
//				goals.add(new FBranchTestFitness(branchGoal.getBranchGoal()));				
//			}
		}
		assert(goals.size()>=branchFactory.getCoverageGoals().size());
		logger.info("Created " + goals.size() + " goals");
		
		return new ArrayList<FBranchTestFitness>(goals);
	}
}
