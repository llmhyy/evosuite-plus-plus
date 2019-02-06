package com.test;

import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.result.TestGenerationResult;


public class AbstractETest {
	@SuppressWarnings("unchecked")
	public EvoTestResult evosuite(String targetClass, String targetMethod, String cp, int seconds, boolean instrumentContext, String fitnessAppraoch) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
//		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
//		Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] { 
//				"-generateRandom",
				"-generateSuite",
				// "-generateMOSuite",
//				"-generateSuiteUsingDSE",
//				"-Dstrategy", "random",
				"-class", targetClass, 
				"-projectCP", cp, //;lib/commons-math-2.2.jar
//				"-setup", "bin", "lib/commons-math-2.2.jar",
				"-Dtarget_method", targetMethod, 
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.1",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.1",
//				"-Dlocal_search_rate", "3",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "30",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
				"-Dmax_length", "1",
				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
//				"-seed", "100"
				};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if(r.getDistribution() != null){
					for(int i=0; i<r.getDistribution().length; i++){
						System.out.println(r.getDistribution()[i]);					
					}					
				}
				
				int age = 0;
				if(r.getGeneticAlgorithm() != null){
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}
				
				System.out.println("Used time: " + r.getElapseTime());
				
				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), age, r.getAvailabilityRatio(), r.getProgressInformation());
			}
		}

		return null;
	}

	public static Method getTragetMethod(String name, Class<?> clazz, int parameterNum){
		for(Method method: clazz.getDeclaredMethods()){
			if(method.getName().equals(name)
					&& method.getParameterCount()==parameterNum){
				return method;
			}
		}
		
		return null;
	}
}
