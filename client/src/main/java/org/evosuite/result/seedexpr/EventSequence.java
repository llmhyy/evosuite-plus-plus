package org.evosuite.result.seedexpr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.result.BranchInfo;
import org.evosuite.testcase.TestChromosome;

public class EventSequence {
	public static List<Event> events = new ArrayList<Event>();
//	public static boolean enabled = false;
	
	public static void addEvent(Event e) {
		if(Properties.ENABLE_TRACEING_EVENT && e != null) {
			events.add(e);			
		}
	}
	
	public static void clear() {
		events.clear();
	}

	public static void enableRecord() {
		Properties.ENABLE_TRACEING_EVENT = true;
		
	}

	public static void disableRecord() {
		Properties.ENABLE_TRACEING_EVENT = false;
	}

	public static BranchCoveringEvent deriveCoveredBranch(Object offspring, Object parent, Set<?> uncoveredGoals) {
		if(offspring instanceof TestChromosome && parent instanceof TestChromosome) {
			TestChromosome off = (TestChromosome)offspring;
			TestChromosome par = (TestChromosome)parent;
			
			Set<Integer> newTrueBranches = off.getLastExecutionResult().getTrace().getCoveredTrueBranches();
			Set<Integer> oldTrueBranches = par.getLastExecutionResult().getTrace().getCoveredTrueBranches();
			Set<Integer> diffTrueBranches = diff(oldTrueBranches, newTrueBranches);
			
			Set<Integer> newFalseBranches = off.getLastExecutionResult().getTrace().getCoveredFalseBranches();
			Set<Integer> oldFalseBranches = par.getLastExecutionResult().getTrace().getCoveredFalseBranches();
			Set<Integer> diffFalseBranches = diff(oldFalseBranches, newFalseBranches);
			
			extractEvents(diffTrueBranches, true, off, par, uncoveredGoals);
			extractEvents(diffFalseBranches, false, off, par, uncoveredGoals);
		}
		return null;
	}

	private static void extractEvents(Set<Integer> diffTrueBranches, boolean conditionValue, 
			TestChromosome offspring, TestChromosome parent, Set<?> uncoveredGoals) {
		for(Integer branchId: diffTrueBranches) {
			Branch b = BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranch(branchId);	
			BranchInfo branchInfo = new BranchInfo(b, conditionValue);
			
			boolean isCovered = isCovered(b, conditionValue, uncoveredGoals);
			
			BranchCoveringEvent e = new BranchCoveringEvent(System.currentTimeMillis(), branchInfo, 
					offspring.getTestCase().toCode(),
					parent.getTestCase().toCode(),
					isCovered);
			addEvent(e);
		}
	}

	private static boolean isCovered(Branch b, boolean conditionValue, Set<?> uncoveredGoals) {
		for(Object goal: uncoveredGoals) {
			if(goal instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)goal;
				Branch b0 = bf.getBranchGoal().getBranch();
				
				if(b0.getClassName().equals(b.getClassName()) &&
					b0.getMethodName().equals(b.getMethodName()) &&
					b0.getActualBranchId()==b.getActualBranchId()) {
					if(bf.getBranchGoal().getValue()==conditionValue) {
						return false;						
					}
					
				}
			}
		}
		return true;
	}

	private static Set<Integer> diff(Set<Integer> oldBranches, Set<Integer> newBranches) {
		Set<Integer> diffSet = new HashSet<Integer>();
		for(Integer branchId: newBranches) {
			if(!oldBranches.contains(branchId)) {
				diffSet.add(branchId);
			}
		}
		
		return diffSet;
	}
}
