package org.evosuite.result.seedexpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.testcase.TestChromosome;

public class EventSequence {
	public static List<Event> events = new ArrayList<Event>();
	public static boolean enabled = false;
	
	public static void addEvent(Event e) {
		if(enabled && e != null) {
			events.add(e);			
		}
	}

	public static void enableRecord() {
		enabled = true;
		
	}

	public static void disableRecord() {
		enabled = false;
	}

	public static BranchCoveringEvent deriveCoveredBranch(Object offspring, Object parent) {
		if(offspring instanceof TestChromosome && parent instanceof TestChromosome) {
			TestChromosome o = (TestChromosome)offspring;
			TestChromosome p = (TestChromosome)parent;
			
			Set<Integer> newTrueBranches = o.getLastExecutionResult().getTrace().getCoveredTrueBranches();
			Set<Integer> oldTrueBranches = p.getLastExecutionResult().getTrace().getCoveredTrueBranches();
			Set<Integer> diffTrueBranches = diff(oldTrueBranches, newTrueBranches);
			
			Set<Integer> newFalseBranches = o.getLastExecutionResult().getTrace().getCoveredFalseBranches();
			Set<Integer> oldFalseBranches = p.getLastExecutionResult().getTrace().getCoveredFalseBranches();
			Set<Integer> diffFalseBranches = diff(oldFalseBranches, newFalseBranches);
			
			for(Integer branchId: diffTrueBranches) {
				
				
				Branch b = BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranch(branchId);				
			}
		}
		return null;
	}

	private static Set<Integer> diff(Set<Integer> oldTrueBranches, Set<Integer> newTrueBranches) {
		// TODO Auto-generated method stub
		return null;
	}
}
