package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.CallContext;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.ConstraintVerifier;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.TestMutationHistoryEntry;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTraceImpl;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.ArrayIndex;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Opcodes;

public class TestCaseLegitimizer {

	public static long startTime;
	
	public static int optimizionPopluationSize = 20;
	public static int localFuzzBudget = Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET;
	
	private InstrumentingClassLoader auxilaryLoader = new InstrumentingClassLoader();
	
	private PartialGraph graph;
	private Map<DepVariableWrapper, List<VariableReference>> graph2CodeMap;
	
	private static TestCaseLegitimizer legitimizer = new TestCaseLegitimizer();
	private TestCaseLegitimizer(){
		if(Properties.TARGET_CLASS != null || !Properties.TARGET_CLASS.isEmpty()) {
			try {
				auxilaryLoader.loadClass(Properties.TARGET_CLASS);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
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
	
	public void updateLegitimacyDistance(TestChromosome individual){
		TestCase test = individual.getTestCase();
		MethodStatement targetCallStat = test.findTargetMethodCallStatement();
		
		if(individual.getLastExecutionResult() == null) {
			ExecutionResult result = TestCaseExecutor.runTest(individual.getTestCase());
			individual.setLastExecutionResult(result);			
		}
		
		int numOfExecutedStatements = individual.getLastExecutionResult().getExecutedStatements();
		double legitimacyDistance = targetCallStat.getPosition() - numOfExecutedStatements + 1;
		individual.setLegitimacyDistance(legitimacyDistance);
		
		if(individual.getLastExecutionResult() == null) {
			System.currentTimeMillis();
		}
		
		Integer exceptionPosition = individual.getLastExecutionResult().getFirstPositionOfThrownException();
		if(exceptionPosition!=null 
				&& exceptionPosition == targetCallStat.getPosition()) {
			individual.setLegitimacyDistance(0);
		}
		
		if(individual.getLegitimacyDistance() > 0){
			if(numOfExecutedStatements > test.size()-1){
				System.currentTimeMillis();
			}
			
			Statement statOfExp = test.getStatement(numOfExecutedStatements);
			Throwable excep = individual.getLastExecutionResult().getExceptionThrownAtPosition(statOfExp.getPosition());
			
			if(excep == null){
				System.currentTimeMillis();
			}
			
			/**
			 * locate the relevant branches from the method call return null value
			 */
			List<BranchCoverageTestFitness> relevantBranches = locateRelevantBranches(statOfExp, excep, test, individual.getLastExecutionResult());
			
			if(!relevantBranches.isEmpty()){
				
				InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
				
				InstrumentingClassLoader auxilaryLoader = TestCaseLegitimizer.getInstance().getAuxilaryLoader();
				double average = 0;
				for(BranchCoverageTestFitness ftt: relevantBranches){
					String className = ftt.getClassName();
					DependencyAnalysis.addTargetClass(className);
					Class<?> cl = null;
					try {
						cl = auxilaryLoader.loadClass(className);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}	
					
					Properties.FULLY_INSTRUMENT_DEPENDENCIES = true;
					((DefaultTestCase)individual.getTestCase()).changeClassLoader(auxilaryLoader);
					individual.addFitness(ftt);	
					individual.clearCachedResults();
					double fit = ftt.getFitness(individual);
					
					if(fit == 0.0) {
						Properties.RECORD_ITERATION_CONTEXT = true;
						Properties.REQUIRE_AVERAGE_BRANCH_DISTANCE = true;
						ExecutionTraceImpl.enableTraceCalls();
						individual.clearCachedResults();
						fit = ftt.getFitness(individual);
//						System.currentTimeMillis();
						ExecutionTraceImpl.disableTraceCalls();
						Properties.REQUIRE_AVERAGE_BRANCH_DISTANCE = false;
						Properties.RECORD_ITERATION_CONTEXT = false;		
					}
					
					((DefaultTestCase)individual.getTestCase()).changeClassLoader(classLoader);
					Properties.FULLY_INSTRUMENT_DEPENDENCIES = false;
					average += fit;
				}
				
				average = average/(double)relevantBranches.size();
				double newDistance = individual.getLegitimacyDistance() + average;
				individual.setLegitimacyDistance(newDistance);
			}
		}
		else {
			individual.setLegitimacyDistance(0);
		}
		
		
		System.currentTimeMillis();
		
	}
	
	private List<BranchCoverageTestFitness> locateRelevantBranches(Statement statOfExp, Throwable excep, 
			TestCase test, ExecutionResult result) {
		CallContext callContext = new CallContext(excep.getStackTrace(), true);
		/**
		 * e.g., "a.m();" where a is null.
		 */
		if(isMethodCallIncurExplicitNullPointerException(statOfExp, excep, result)){
			MethodStatement mStat = (MethodStatement)statOfExp;
			VariableReference callee = mStat.getCallee();
			Statement defStat = test.getStatement(callee.getStPosition());
			
			if(defStat instanceof MethodStatement){
				MethodStatement defMethodStat = (MethodStatement)defStat;
				GenericMethod method = defMethodStat.getMethod();
				
				List<BranchCoverageTestFitness> nonNullBranches = locateNonNullBranches(method, callContext);
				return nonNullBranches;
			}
		}
		/**
		 * e.g., "a[0] = b" or "a.f = b" where a is null.
		 */
		else if(isAssignmentIncurExplicitNullPointerException(statOfExp, excep, result)){
			VariableReference ref = statOfExp.getReturnValue();
			if(ref instanceof FieldReference){
				FieldReference fieldRef = (FieldReference)ref;
				VariableReference source = fieldRef.getSource();
				
				Statement defStat = test.getStatement(source.getStPosition());
				if(defStat instanceof MethodStatement){
					MethodStatement defMethodStat = (MethodStatement)defStat;
					GenericMethod method = defMethodStat.getMethod();
					
					List<BranchCoverageTestFitness> nonNullBranches = locateNonNullBranches(method, callContext);
					return nonNullBranches;
				}
			}
		}
		
		if(excep.getStackTrace().length != 0) {
			StackTraceElement element = excep.getStackTrace()[0];
			List<BranchCoverageTestFitness> branches = getExceptionalBranches(element, callContext);
			
			return branches;
		}
		
		if(excep.getStackTrace().length == 0) {
			ActualControlFlowGraph graph = null;
			if(statOfExp instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement)statOfExp;
				GenericMethod method = mStat.getMethod();
				graph = getControlFlowGraph(method, TestCaseLegitimizer.getInstance().getAuxilaryLoader());
			}
			else if(statOfExp instanceof ConstructorStatement) {
				ConstructorStatement cStat = (ConstructorStatement)statOfExp;
				GenericConstructor constructor = cStat.getConstructor();
				graph = getControlFlowGraph(constructor, TestCaseLegitimizer.getInstance().getAuxilaryLoader());
			}
			
			if(graph != null) {
				List<BranchCoverageTestFitness> branches = locateBranches(graph, excep, callContext);
				return branches;
			}			
		}
		
		
		return new ArrayList<BranchCoverageTestFitness>();
	}

	private List<BranchCoverageTestFitness> getExceptionalBranches(StackTraceElement element, CallContext callContext) {
		String className = element.getClassName();
		
		InstrumentingClassLoader loader = TestCaseLegitimizer.getInstance().getAuxilaryLoader();
		BytecodeInstructionPool pool = BytecodeInstructionPool.getInstance(loader);
		
		List<BytecodeInstruction> insList = null;
		if(element.getLineNumber()>0) {
			insList = pool.getAllInstructionsAtLineNumber(className, element.getLineNumber());
			if(insList == null) {
				DependencyAnalysis.addTargetClass(className);
				try {
					loader.loadClass(className);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
//				GraphPool.getInstance(loader).retrieveAllRawCFGs(className, loader);
				insList = pool.getAllInstructionsAtLineNumber(className, element.getLineNumber());
			}
		}
		
		List<BranchCoverageTestFitness> fitnessList = new ArrayList<>();
		if(insList != null && insList.size() > 0) {
			BytecodeInstruction ins = insList.get(0);
			
			Branch b = null;
			try {
				b = ins.getControlDependentBranch();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(b != null) {
				boolean value = ins.getControlDependentBranchExpressionValue();
				BranchCoverageTestFitness fitness = BranchCoverageFactory.createBranchCoverageTestFitness(b, !value);
//				fitnessList.add(fitness);	
				FBranchTestFitness fFit = new FBranchTestFitness(fitness.getBranchGoal());
				fFit.setContext(callContext);
				fitnessList.add(fFit);				
			}
		}
		
		return fitnessList;
		
	}

	private ActualControlFlowGraph getControlFlowGraph(GenericMethod method, InstrumentingClassLoader loader) {
		String methodName = method.getName() + MethodUtil.getSignature(method.getMethod());
		ActualControlFlowGraph cfg = GraphPool.getInstance(loader)
				.getActualCFG(method.getOwnerClass().getClassName(), methodName);
		
		return cfg;
	}

	private ActualControlFlowGraph getControlFlowGraph(GenericConstructor constructor, InstrumentingClassLoader loader) {
		String methodName = "<init>" + MethodUtil.getSignature(constructor.getConstructor());
		ActualControlFlowGraph cfg = GraphPool.getInstance(loader)
				.getActualCFG(constructor.getOwnerClass().getClassName(), methodName);
		
		return cfg;
	}
	
	
	private List<BranchCoverageTestFitness> locateBranches(ActualControlFlowGraph graph, Throwable excep, CallContext callContext){
		List<BranchCoverageTestFitness> list = new ArrayList<>();
		
		for(BytecodeInstruction ins: graph.getAllInstructions()) {
			if(ins.isThrow()) {
				Branch b = ins.getControlDependentBranch();
				
				if(b != null) {
					boolean value = ins.getControlDependentBranchExpressionValue();
					
					BranchCoverageTestFitness fitness = BranchCoverageFactory.createBranchCoverageTestFitness(b, !value);
					FBranchTestFitness fFit = new FBranchTestFitness(fitness.getBranchGoal());
					fFit.setContext(callContext);
					list.add(fFit);				
				}
			}
		}	
		
		return list;
		
	}
	
	private List<BranchCoverageTestFitness> locateNonNullBranches(GenericMethod method, CallContext callContext) {
		List<BranchCoverageTestFitness> list = new ArrayList<>();
		
		String methodName = method.getName() + MethodUtil.getSignature(method.getMethod());
		String className = method.getOwnerClass().getClassName();
		try {
			TestCaseLegitimizer.getInstance().getAuxilaryLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return list;
		}
		
		ActualControlFlowGraph cfg = GraphPool.getInstance(TestCaseLegitimizer.getInstance().getAuxilaryLoader())
				.getActualCFG(className, methodName);
		
		
		for(BytecodeInstruction exit: cfg.getExitPoints()){
			
			List<BytecodeInstruction> l = exit.getSourceListOfStackInstruction(0);
			for(BytecodeInstruction ins: l) {
				System.currentTimeMillis();
				
				if(ins.getASMNode().getOpcode() == Opcodes.ACONST_NULL){
					Branch b = ins.getControlDependentBranch();
					boolean value = exit.getControlDependentBranchExpressionValue();
					
					BranchCoverageTestFitness fitness = BranchCoverageFactory.createBranchCoverageTestFitness(b, !value);
					FBranchTestFitness fFit = new FBranchTestFitness(fitness.getBranchGoal());
					fFit.setContext(callContext);
					list.add(fFit);
				}
				
			}
			
		}
		
		return list;
	}

	private boolean isMethodCallIncurExplicitNullPointerException(Statement statOfExp, Throwable excep, ExecutionResult result) {
		if(excep == null || excep.getMessage() == null) {
			System.currentTimeMillis();
		}
		
		return statOfExp instanceof MethodStatement 
				&&  result.explicitExceptions.get(statOfExp.getPosition()) != null
				&& 
				(excep instanceof NullPointerException ||
						excep.getCause() instanceof NullPointerException);
	}
	
	private boolean isAssignmentIncurExplicitNullPointerException(Statement statOfExp, Throwable excep,
			ExecutionResult result) {
		return statOfExp instanceof AssignmentStatement 
				&&  result.explicitExceptions.get(statOfExp.getPosition()) != null
				&& excep instanceof NullPointerException;
	}
	
	private boolean isIncurExplicitNullPointerException(Statement statOfExp, Throwable excep, ExecutionResult result){
		return statOfExp instanceof MethodStatement 
			&&  result.explicitExceptions.get(statOfExp.getPosition()) != null
			&& excep.getMessage().equals("java.lang.NullPointerException");
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
			updateLegitimacyDistance(parent);
			TestChromosome offspring = (TestChromosome) parent.clone();
//			offspring.mutationChangePrimitiveStatement();
			mutateRelevantStatements(offspring);
			offspring.clearCachedResults();
			
//			relevantBranches.forEach(fitnessFunction -> fitnessFunction.getFitness(offspring));
			updateLegitimacyDistance(offspring);
			MutationPositionDiscriminator.identifyRelevantMutations(offspring, parent);
			
			newPop.add(offspring);
		}
		return newPop;
	}
	
	private List<Statement> checkInfluencingStatements(TestCase test, Statement statOfExp) {
		//TODO need a perfect relevance check
		List<Statement> influencingStatements = new ArrayList<>();
		for(int i=0; i<test.size(); i++){
			Statement statement = test.getStatement(i);
			if((statement instanceof PrimitiveStatement || statement instanceof AssignmentStatement)  
					&& statement.getPosition() <= statOfExp.getPosition()){
				influencingStatements.add(statement);
			}
		}
		
		return influencingStatements;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void mutateRelevantStatements(TestChromosome individual) {
		Statement statOfExp = individual.getStatementReportingException();
		if(statOfExp == null) {
			return;
		}
		
		TestCase test = individual.getTestCase();
		List<Statement> influencingStatements = checkInfluencingStatements(test, statOfExp);
		for(int i=0; i<influencingStatements.size(); i++){
			Statement refStatement = influencingStatements.get(i);
			Statement statement = individual.getTestCase().getStatement(refStatement.getPosition());
			
			TestFactory testFactory = TestFactory.getInstance();
			
			statement.setChanged(false);
			assert (test.isValid());

			if(statement.isReflectionStatement())
				continue;

			boolean changed = false; 
			int oldDistance = statement.getReturnValue().getDistance();
			if (statement instanceof NullStatement) {
				int pos = statement.getPosition();
				Statement nextStatement = test.getStatement(pos+1);
				if (testFactory.changeNullStatement(test, statement)) {
					statement = test.getStatement(nextStatement.getPosition()-1);
					
					changed = true;
					individual.getMutationHistory().addMutationEntry(new TestMutationHistoryEntry(
							TestMutationHistoryEntry.TestMutation.CHANGE, statement));
					assert ConstraintVerifier.verifyTest(test);		
				}
			}
			
			boolean isMutated = false;
			boolean reuse = Randomness.nextBoolean();
			if(reuse){
				if(statement instanceof PrimitiveStatement){
					PrimitiveStatement pStatement = (PrimitiveStatement)statement;
					List<Object> candidates = searchForReusableVariables(influencingStatements, pStatement.getReturnType());
					if(!candidates.isEmpty()){
						Object value = Randomness.choice(candidates);
						pStatement.setValue(value);
						System.currentTimeMillis();
					}					
				}
			}
			else{
				isMutated = statement.mutate(test, testFactory);				
			}
			
			if (isMutated) {
				individual.getMutationHistory().addMutationEntry(new TestMutationHistoryEntry(
				        TestMutationHistoryEntry.TestMutation.CHANGE, statement));
				assert (test.isValid());
				assert ConstraintVerifier.verifyTest(test);

			} 
			
			statement.getReturnValue().setDistance(oldDistance);
			
			if(changed){
				assert ConstraintVerifier.verifyTest(test);
			}

		}
		
	}

	@SuppressWarnings("rawtypes")
	private List<Object> searchForReusableVariables(List<Statement> influencingStatements, Type returnType) {
		List<Object> list = new ArrayList<>();
		for(Statement stat: influencingStatements){
			if(stat.getReturnType().equals(returnType)){
				if(stat instanceof PrimitiveStatement){
					list.add(((PrimitiveStatement) stat).getValue());					
				}
			}
			else if(stat instanceof AssignmentStatement){
				AssignmentStatement aStat = (AssignmentStatement)stat;
				VariableReference varRef = aStat.getReturnValue();
				if(varRef instanceof ArrayIndex && returnType.toString().equals("int")){
					ArrayIndex index = (ArrayIndex)varRef;
					list.add(index.getArrayIndex());
				}
			}
		}
		return list;
	}

	public InstrumentingClassLoader getAuxilaryLoader() {
		return auxilaryLoader;
	}

	
}
