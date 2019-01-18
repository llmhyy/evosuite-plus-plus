package com.test;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.result.TestGenerationResult;


public class AbstractETest {
	class Tuple {
		int time;
		double coverage;
		int age;

		public Tuple(int time, double coverage, int age) {
			super();
			this.time = time;
			this.coverage = coverage;
			this.age = age;
		}

	}

	@SuppressWarnings("unchecked")
	public Tuple evosuite(String targetClass, String targetMethod, String cp, int seconds, boolean instrumentContext, String fitnessAppraoch) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		// Properties.TARGET_METHOD = targetClass+".test(DDI)V";
//		Properties.ALGORITHM = Algorithm.MONOTONICGA;
//		Properties.ALGORITHM = Algorithm.RANDOM;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
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
				"-Dinstrument_context", "true", 
				"-Dinsertion_uut", "0.1",
//				"-Dinstrument_method_calls", "true",
//				"-Dinstrument_libraries", "true",
				"-seed", "100"
				};

		// command = new String[] { "-generateSuite", "-class", targetClass,
		// "-projectCP", cp, "-Dsearch_budget",
		// String.valueOf(seconds), "-criterion", "branch"};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				// System.out.println(r);
				System.out.println(r.getProgressInformation());
				if(r.getDistribution() != null){
					for(int i=0; i<r.getDistribution().length; i++){
						System.out.println(r.getDistribution()[i]);					
					}					
				}
				
				int age = 0;
				if(r.getGeneticAlgorithm() != null){
					age = r.getGeneticAlgorithm().getAge();
				}
				
				return new Tuple(r.getElapseTime(), r.getCoverage(), age);
			}
		}

		return null;
	}

	public static Method getTragetMethod(String name, Class<?> clazz, int parameterNum){
		for(Method method: clazz.getMethods()){
			if(method.getName().equals(name)
					&& method.getParameterCount()==parameterNum){
				return method;
			}
		}
		
		return null;
	}
}
