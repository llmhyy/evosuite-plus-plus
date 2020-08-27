package org.evosuite.testcase.synthesizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;

public class TestCaseLegitimizer {

	public static long startTime;
	
	public static int optimizionPopluationSize = 20;
	public static int localFuzzBudget = Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET;
	
	private PartialGraph graph;
	private Map<DepVariableWrapper, List<VariableReference>> graph2CodeMap;
	
	private static TestCaseLegitimizer legitimizer = new TestCaseLegitimizer();
	private TestCaseLegitimizer(){}
	
	public static TestCaseLegitimizer getInstance(){
		return legitimizer;
	}
	
	public TestChromosome legitimize(TestChromosome testChromosome, PartialGraph graph, Map<DepVariableWrapper, 
			List<VariableReference>> graph2CodeMap) {
		this.graph = graph;
		this.graph2CodeMap = graph2CodeMap;
		
		TestCase test = testChromosome.getTestCase();
		MethodStatement targetCallStat = test.findTargetMethodCallStatement();
		if(targetCallStat == null) return null;
		
		double legitimacyDistance = testChromosome.getLegitimacyDistance();
		if(legitimacyDistance == 0) return testChromosome;
		/**
		 * initializing the population
		 */
		List<TestChromosome> population = initializePopulation(test);
		
		long t1 = System.currentTimeMillis();
		long t2 = System.currentTimeMillis();
		while (legitimacyDistance != 0 && (t2-t1) <= Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET * 1000){
//			MutationPositionDiscriminator.discriminator.setPurpose(relevantBranches);
			
			evolve(population);
			legitimacyDistance = population.get(0).getLegitimacyDistance();
			t2 = System.currentTimeMillis();
		}
		
		return population.get(0);
	}



	private List<TestChromosome> initializePopulation(TestCase test) {
		List<TestChromosome> population = new ArrayList<TestChromosome>();
		for(int i=0; i<optimizionPopluationSize; i++){
			TestCase copy = test.clone();
			TestChromosome t = new TestChromosome();
			t.setTestCase(copy);
			population.add(t);
		}
		return population;
	}
	


//	private List<TestChromosome> randomSample(List<TestChromosome> population) {
//		List<TestChromosome> p = new ArrayList<>();
//		
//		while(p.size() < population.size()/2){
//			int index = Randomness.nextInt(population.size());
//			TestChromosome t = population.remove(index);
//			p.add(t);			
//		}
//		
//		return p;
//	}



	private void evolve(List<TestChromosome> population) {
		List<TestChromosome> newPopulation = breedOffSpring(population);
		population.addAll(newPopulation);
		select(population);
	}

	private void select(List<TestChromosome> population) {
		population.sort(new Comparator<TestChromosome>() {
			@Override
			public int compare(TestChromosome o1, TestChromosome o2) {
				if(o1.getLegitimacyDistance() - o2.getLegitimacyDistance() < 0){
					return -1;
				}
				else if(o1.getLegitimacyDistance() - o2.getLegitimacyDistance() > 0){
					return 1;
				}
				else{
					return 0;
				}
			}
		});
		
		population.get(1).updateLegitimacyDistance();
		System.currentTimeMillis();
		
		List<TestChromosome> newPop = new ArrayList<>();
		for(TestChromosome individual: population){
			if(!newPop.contains(individual)){
				newPop.add(individual);
				
				if(newPop.size() >= optimizionPopluationSize/2){
					break;
				}
			}
		}
		
		while(newPop.size() < optimizionPopluationSize){
			TestChromosome individual = Randomness.choice(population);
			newPop.add(individual);
		}
		
		population.clear();
		population.addAll(newPop);
	}

//	/**
//	 * return null if we still cannot find them.
//	 * @param statOfExp
//	 * @param excep
//	 * @param test
//	 * @param result
//	 * @return
//	 */
//	private TestChromosome applyLocalFuzz(Statement statOfExp, Throwable excep, TestCase test,
//			ExecutionResult result) {
//		
//		List<Statement> influencingStatements = checkInfluencingStatements(test, statOfExp);
//		
//		long t1 = System.currentTimeMillis();
//		long t2 = t1;
//		List<TestChromosome> population = initializePopulation(test);
//		
//		while(t2 - t1 < localFuzzBudget*100000){
//			List<TestChromosome> newPop = new ArrayList<>();
//			for(TestChromosome individual: population){
//				TestChromosome offspring = (TestChromosome) individual.clone();
//				offspring.mutate(influencingStatements);
//				ExecutionResult origResult = TestCaseExecutor.runTest(offspring.getTestCase());
//				offspring.setLastExecutionResult(origResult);
//				
//				offspring.updateLegitimacyDistance();
//				if(offspring.getLegitimacyDistance() == 0){
//					return offspring;
//				}
//				else{
//					newPop.add(offspring);
//				}
//			}
//			
//			population.addAll(newPop);
//			population = randomSample(population);
//			
//			t2 = System.currentTimeMillis();
//		}
//		
//		
//		return null;
//	}
	
	private List<TestChromosome> breedOffSpring(List<TestChromosome> population) {
		
		List<TestChromosome> newPop = new ArrayList<>();
		
		for(TestChromosome parent: population){
//			relevantBranches.forEach(fitnessFunction -> fitnessFunction.getFitness(parent));
			parent.updateLegitimacyDistance();
			TestChromosome offspring = (TestChromosome) parent.clone();
			offspring.clearCachedResults();
//			offspring.mutationChangePrimitiveStatement();
			offspring.mutateRelevantStatements();
			
//			relevantBranches.forEach(fitnessFunction -> fitnessFunction.getFitness(offspring));
			offspring.updateLegitimacyDistance();
			MutationPositionDiscriminator.identifyRelevantMutations(offspring, parent);
			
			newPop.add(offspring);
		}
		return newPop;
	}

	
}
