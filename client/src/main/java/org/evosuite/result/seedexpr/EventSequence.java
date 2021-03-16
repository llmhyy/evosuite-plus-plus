package org.evosuite.result.seedexpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.coverage.branch.BranchPool;

import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.result.BranchInfo;
import org.evosuite.seeding.smart.BranchSeedInfo;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.testcase.TestChromosome;

public class EventSequence {
	public static List<Event> events = new ArrayList<Event>();
	public static HashMap<Branch,Integer> branchType = new HashMap<Branch,Integer>();
//	public static boolean enabled = false;
	
	
	public static boolean LOCAL_ENABLE = false;
	
	public static void addEvent(Event e) {
		if(Properties.ENABLE_TRACEING_EVENT && LOCAL_ENABLE && e != null) {
			events.add(e);			
		}
	}
	
	public static void clear() {
		events.clear();
	}

	public static void enableRecord() {
		LOCAL_ENABLE = true;
	}

	public static void disableRecord() {
		LOCAL_ENABLE = false;
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
			if(!(b.getClassName().equals(Properties.TARGET_CLASS) &&
					b.getMethodName().equals( Properties.TARGET_METHOD)))
				continue;
			BranchInfo branchInfo = new BranchInfo(b, conditionValue);
			
			boolean isCovered = isCovered(b, conditionValue, uncoveredGoals);
			
			BranchCoveringEvent e = new BranchCoveringEvent(System.currentTimeMillis(), branchInfo, 
					offspring.getTestCase().toCode(),
					parent.getTestCase().toCode(),
					isCovered);
//			addEvent(e);
			
			analyzeEvent(e,b,offspring,isCovered);
		}
	}

	private static void analyzeEvent(BranchCoveringEvent e, Branch b, TestChromosome offspring, boolean isCovered) {
		// TODO 
		if(isCovered) {
			return;
		}
		
		List<Event> eventsList = new ArrayList<Event>();
		HashMap<Integer, Integer> localBranchType = new HashMap<Integer, Integer>();
		if(Properties.ENABLE_TRACEING_EVENT && LOCAL_ENABLE && e != null) {
			//get eventsList
			for(int i = events.size() - 1;i >= 0;i--) {
				if((i != events.size() - 1) && events.get(i).getClass().equals(e.getClass()))
					break;
				eventsList.add(events.get(i));
			}
			if(eventsList.size() <= 1)
				return;
//			System.out.println("Target branch:" + b.toString() +" "+  e.getBranch().getTruthValue());
//			for(int i = 0; i < eventsList.size();i++) {
//				System.out.print(eventsList.get(i).toString() + "->");
//			}
//			System.out.println();
		}
		
		//find branch input type
		List<String> inputsType = new ArrayList<String>();
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);
		for(DepVariable input : methodInputs) {
			if(input.isMethodInput()) {
				inputsType.add(input.getDataType());
			}
		}
		
		//find target event
		for(int i = 1; i < eventsList.size();i++) {
			if(i > 15)
				break;
			for(int j = 0;j < inputsType.size();j++) {
				String str;
				if(!inputsType.get(j).equals(BranchSeedInfo.OTHER)) {
					str = SeedingApplicationEvaluator.finalType(eventsList.get(i).getDataType());
					if(str.toLowerCase().contains("string"))
						str = "java.lang.String";
					if(str.equals(BranchSeedInfo.OTHER))
						break;
					if(inputsType.get(j).toLowerCase().contains("string") && str.toLowerCase().contains("string")) {
						//target event:if offspring contain event
//						System.out.println("offspring:\n" + offspring.toString());
						int statementsSize = offspring.getTestCase().size();
						for(int m = 0; m < statementsSize;m++) {
							String statementType = offspring.getTestCase().getStatement(m).getReturnType().toString();
							String statementValue = offspring.getTestCase().getStatement(m).toString();
							if(eventsList.get(i).getValue().equals(""))
								break;
							
							if(statementType.toLowerCase().contains("string") && statementValue.contains(eventsList.get(i).getValue().toString())) {
								System.out.println("Find target event");
								System.out.println(eventsList.get(i).getDataType());
								System.out.println(eventsList.get(i).getValue());
								
								//analyze the branch type
								int branchType = findBranchType(i,eventsList);
								switch(branchType) {
								case Event.staticPoolSampling:
								case Event.staticContextPoolSampling:
									recordType(localBranchType,Event.staticContextPoolSampling);	
									System.out.println("STATIC_POOL");
									break;
								case Event.dynamicPoolSampling:
									recordType(localBranchType,Event.dynamicPoolSampling);
									System.out.println("DYNAMIC_POOL");
									break;
								case Event.randomSampling:
									recordType(localBranchType,Event.randomSampling);
									System.out.println("NO_POOL");
									break;
								}
								System.currentTimeMillis();
							}
						}
						
					}
					else {
						if(ComputationPath.isCompatible(inputsType.get(j),str)) {
//							System.out.println("offspring:\n" + offspring.toString());
							int statementsSize = offspring.getTestCase().size();
							for(int m = 0; m < statementsSize;m++) {
								String statementType = offspring.getTestCase().getStatement(m).getReturnType().toString();
								String statementValue = offspring.getTestCase().getStatement(m).toString();
								if(eventsList.get(i).getValue().equals(""))
									break;
								
								if(ComputationPath.isCompatible(statementType,str) && statementValue.contains(eventsList.get(i).getValue().toString())) {
									System.out.println("Find target event");
									System.out.println(eventsList.get(i).getDataType());
									System.out.println(eventsList.get(i).getValue());
									
									//analyze the branch type
									int branchType = findBranchType(i,eventsList);
									switch(branchType) {
									case Event.staticPoolSampling:
									case Event.staticContextPoolSampling:
										recordType(localBranchType,Event.staticContextPoolSampling);	
										System.out.println("STATIC_POOL");
										break;
									case Event.dynamicPoolSampling:
										recordType(localBranchType,Event.dynamicPoolSampling);
										System.out.println("DYNAMIC_POOL");
										break;
									case Event.randomSampling:
										recordType(localBranchType,Event.randomSampling);
										System.out.println("NO_POOL");
										break;
									}
									System.currentTimeMillis();
								}
							}
							
						}
					}
					
					
						
				}
			}
			
		}
		
		System.out.println();
		
	}

	private static void recordType(HashMap<Integer, Integer> localBranchType, int type) {
		if(localBranchType.containsKey(type)) {
			int temp = localBranchType.get(type);
			localBranchType.put(type, temp + 1);
		}else {
			localBranchType.put(type, 1);
		}
	}

	private static int findBranchType(int position, List<Event> eventsList) {
		int branchType = 0;
		String oldValue = null;
		for(int i = position;i >= 0;i--) {
			if(oldValue != null && !oldValue.contains(eventsList.get(i).getValue()))
				continue;
			if(eventsList.get(i).getType() == Event.staticContextPoolSampling || eventsList.get(i).getType() == Event.staticPoolSampling)
				return Event.staticContextPoolSampling;
			if(eventsList.get(i).getType() == Event.dynamicPoolSampling)
				return Event.dynamicPoolSampling;
			if(eventsList.get(i).getType() == Event.randomSampling)
				return Event.randomSampling;
			if(eventsList.get(i).getType() == Event.search && i != 0) {
				oldValue = eventsList.get(i).getOldValue();
			}
		}
		return Event.randomSampling;
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
