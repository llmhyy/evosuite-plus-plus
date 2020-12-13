package org.evosuite.result.seedexpr;

import java.util.ArrayList;
import java.util.List;

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
			
			o.getLastExecutionResult().getTrace().getCoveredTrueBranches();
			
		}
		return null;
	}
}
