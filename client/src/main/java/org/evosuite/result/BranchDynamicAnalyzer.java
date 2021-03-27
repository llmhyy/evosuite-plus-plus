package org.evosuite.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.VariableReference;

public class BranchDynamicAnalyzer {
	public static HashMap<Branch,HashMap<Object,Integer>> branchHead = new HashMap<Branch,HashMap<Object,Integer>>();
	
	public static final String Parameter = "Parameter";
	public static final String StaticField = "StaticField";
	public static final String InstaceField = "InstaceField";
	
	public static void analyzeBranch(Object offspring, Object parent, Set<?> uncoveredGoals) {
		if(offspring instanceof TestChromosome && parent instanceof TestChromosome) {
//			HashMap<Branch,HashMap<Object,Integer>> branchHeadPar = new HashMap<Branch,HashMap<Object,Integer>>();
//			HashMap<Branch,HashMap<Object,Integer>> branchHeadOff = new HashMap<Branch,HashMap<Object,Integer>>();
			TestChromosome off = (TestChromosome)offspring;
			TestChromosome par = (TestChromosome)parent;
			
			extractTestcase(off,uncoveredGoals,branchHead);
//			for (Entry<Branch, HashMap<Object, Integer>> entry : branchHead.entrySet()) {
//				System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//			}
			System.currentTimeMillis();
			
		}
	}
	
	private static void extractTestcase(TestChromosome offspring, Set<?> uncoveredGoals, HashMap<Branch, HashMap<Object, Integer>> branchHeadOff) {
		//TODO
		for(Object goal: uncoveredGoals) {
			if(goal instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness)goal;
				Branch b0 = bf.getBranchGoal().getBranch();
				
				if(!(b0.getClassName().equals(Properties.TARGET_CLASS) &&
						b0.getMethodName().equals( Properties.TARGET_METHOD)))
					continue;
				
				HashMap<String,Object> inputsInfo = new HashMap<String,Object>();
				findBranchInputInfo(inputsInfo,b0);
				
				for (Entry<String, Object> input : inputsInfo.entrySet()) {
					if(input.getKey().equals(Parameter)) {
						int statementsSize = offspring.getTestCase().size();
//						System.out.println("in this testcase:");
						for(int m = statementsSize - 1; m > 0;m--) {
							String statementType = offspring.getTestCase().getStatement(m).getReturnType().toString();
							String statementValue = offspring.getTestCase().getStatement(m).toString();
							
							if(statementValue.contains(Properties.TARGET_METHOD)) {
								
								Statement s = offspring.getTestCase().getStatement(m);
								if(s instanceof MethodStatement) {
//									System.out.println("invoke method position :" + m);
									MethodStatement staement0 = (MethodStatement) s;
									List<VariableReference> parameters = staement0.getParameterReferences();
									for(int i = 0; i < parameters.size();i++) {
										int p = parameters.get(i).getStPosition();
										Object ty = parameters.get(i).getType();
										if(i == (int)input.getValue()) {
											HashMap<Object,Integer> value = new HashMap<Object,Integer>();
											value.put(ty, p);
											branchHeadOff.put(b0, value);
										}
											
//										System.out.println("parameter statement:" + offspring.getTestCase().getStatement(p).toString());
//										System.out.println(" invokeStatement: " + staement0.toString() +
//												" parameter position:" + p + " parameter type " + ty);
									}
								}				
							}
						}
					}
				}
				
				
			}
		}
		
	}

	
	private static void findBranchInputInfo(HashMap<String, Object> inputsInfo, Branch b) {
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		Set<DepVariable> methodInputs = branchesInTargetMethod.get(b);
		for(DepVariable input : methodInputs) {
			if(input.isParameter() && 
					input.getInstruction().getMethodName().equals(Properties.TARGET_METHOD) &&
					input.getClassName().equals(Properties.TARGET_CLASS)) {
				int position = input.getParamOrder() - 1;
				inputsInfo.put(Parameter, position);
				return;
			}
			
			if(input.isStaticField()) {
				String type = null;
				inputsInfo.put(StaticField, type);
				return;
			}
			
			if(input.isInstaceField() && input.getClassName().equals(Properties.TARGET_CLASS)) {
				String type = null;
				inputsInfo.put(InstaceField, type);
				return;
			}
			
		}
	}
}
