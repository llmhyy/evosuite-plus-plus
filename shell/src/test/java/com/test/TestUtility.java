package com.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.ga.metaheuristics.RuntimeRecord;
import org.evosuite.result.TestGenerationResult;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;

public class TestUtility {
	public static void evoTestSingleMethod(String targetClass, String targetMethod, 
			int timeBudget, 
			boolean instrumentContext,
			boolean aor,
			String cp,
			String fitnessApproach, 
			String option, 
			String strategy,
			String algorithm) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
				"-Dapply_object_rule", String.valueOf(aor),
				"-"+option,
				"-Dstrategy", strategy,
				"-Dalgorithm", algorithm,
				
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(timeBudget),
				"-Dcriterion", fitnessApproach,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.5",
				"-Ddynamic_pool", "0.5",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				
				};

		@SuppressWarnings("unchecked")
		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());
			}
		}

	}
	
	
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuite(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String seed) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
//				"-generateTests",
//				"-Dstrategy", "ONEBRANCH",
				
//				"-generateTests",
//				"-Dstrategy", "EMPIRICAL_HYBRID_COLLECTOR",
//				"-Dstrategy", "LEARNING_BASED_HYBRID",
//				"-Dapply_object_rule", "true",
//				"-Denable_branch_enhancement", "true",
//				"-Dadopt_smart_mutation", "true",
				
				"-generateMOSuite",
				"-Dstrategy", "MOSUITE",
				"-Dalgorithm", "DYNAMOSA",
				
//				"-Dalgorithm", "random",

//				"-generateSuiteUsingDSE",
//				"-Dstrategy", "DSE",
				
//				"-Ddse_solver", "z3_solver", "-Dz3_path", "C:\\\"Program Files (x86)\"\\\"Microsoft Research\"\\Z3-4.1\\bin\\z3.exe",
//				"-Ddse_solver", "cvc4_solver", "-Dcvc4_path", "C:\\\"Program Files (x86)\"\\\"Microsoft Research\"\\Z3-4.1\\bin\\cvc4.exe",
//				"-generateMOSuite",
//				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "DYNAMOSA",
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.5",
				"-Ddynamic_pool", "0.5",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length() == 0) ? "1578927395578" : seed
				};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteHybrid(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
//				"-generateTests",
//				"-Dstrategy", "ONEBRANCH",
				
				"-generateTests",
				"-Dstrategy", "EMPIRICAL_HYBRID_COLLECTOR",
//				"-Dstrategy", "LEARNING_BASED_HYBRID",
				"-Dapply_object_rule", "false",
//				"-Denable_branch_enhancement", "true",
//				"-Dadopt_smart_mutation", "true",
				
//				"-generateMOSuite",
//				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "DYNAMOSA",
				
//				"-Dalgorithm", "random",

//				"-generateSuiteUsingDSE",
//				"-Dstrategy", "DSE",
				
//				"-Ddse_solver", "z3_solver", "-Dz3_path", "C:\\\"Program Files (x86)\"\\\"Microsoft Research\"\\Z3-4.1\\bin\\z3.exe",
//				"-Ddse_solver", "cvc4_solver", "-Dcvc4_path", "C:\\\"Program Files (x86)\"\\\"Microsoft Research\"\\Z3-4.1\\bin\\cvc4.exe",
//				"-generateMOSuite",
//				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "DYNAMOSA",
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuite(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
//				"-generateTests",
//				"-Dstrategy", "ONEBRANCH",
				
//				"-generateTests",
//				"-Dstrategy", "EMPIRICAL_HYBRID_COLLECTOR",
//				"-Dstrategy", "LEARNING_BASED_HYBRID",
//				"-Dapply_object_rule", "true",
//				"-Denable_branch_enhancement", "true",
//				"-Dadopt_smart_mutation", "true",
				
				"-generateMOSuite",
				"-Dstrategy", "MOSUITE",
				"-Dalgorithm", "DYNAMOSA",
				
//				"-Dalgorithm", "random",

//				"-generateSuiteUsingDSE",
//				"-Dstrategy", "DSE",
				
//				"-Ddse_solver", "z3_solver", "-Dz3_path", "C:\\\"Program Files (x86)\"\\\"Microsoft Research\"\\Z3-4.1\\bin\\z3.exe",
//				"-Ddse_solver", "cvc4_solver", "-Dcvc4_path", "C:\\\"Program Files (x86)\"\\\"Microsoft Research\"\\Z3-4.1\\bin\\cvc4.exe",
//				"-generateMOSuite",
//				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "DYNAMOSA",
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}

	public static String getAvailableCalls() {
		List<String> calls = new ArrayList<>();
		for (String method : RuntimeRecord.methodCallAvailabilityMap.keySet()) {
			if (RuntimeRecord.methodCallAvailabilityMap.get(method)) {
				calls.add(method);
			}
		}
		return calls.toString();
	}

	public static String getUnavailableCalls() {
		List<String> calls = new ArrayList<>();
		for (String method : RuntimeRecord.methodCallAvailabilityMap.keySet()) {
			if (!RuntimeRecord.methodCallAvailabilityMap.get(method)) {
				calls.add(method);
			}
		}
		return calls.toString();
	}

	public static Method getTargetMethod(String name, Class<?> clazz, int parameterNum) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(name) && method.getParameterCount() == parameterNum) {
				return method;
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteRandomTest(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
//				"-generateTests",
//				"-Dstrategy", "ONEBRANCH",
				
				"-generateTests",
				"-Dstrategy", "ONEBRANCH",
				"-generateRandom",
				"-Dalgorithm","RANDOM_SEARCH",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				
				EvosuiteForMethod.printResult(r);
				
				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteEvosuiteDSE(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "false",
//				"-Denable_branch_enhancement", "false",
//			
//				"-generateTests",
			
				"-generateSuiteUsingDSE",
				"-Dstrategy","DSE",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",				
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				
			};
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteZ3DSE(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "false",
//				"-Denable_branch_enhancement", "false",
//			
//				"-generateTests",
			
				"-generateSuiteUsingDSE",
				"-Dstrategy","DSE",
				"-Ddse_solver", "Z3_SOLVER",
				"-Dz3_path", "D:\\linyun\\program\\z3-4.8.9-x64-win\\bin\\z3.exe",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",				
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				
			};
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteCVC4DSE(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "false",
//				"-Denable_branch_enhancement", "false",
//			
//				"-generateTests",
			
				"-generateSuiteUsingDSE",
				"-Dstrategy","DSE",
				"-Ddse_solver", "CVC4_SOLVER",
				"-Dcvc4_path", "D:\\linyun\\program\\cvc4-1.8-win64-opt.exe",
				
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",				
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				
			};
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteGA(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "flase",
				"-Denable_branch_enhancement", "true",
			
				"-generateSuite",
				"-Dstrategy","Evosuite",
				"-Dalgorithm","MONOTONIC_GA",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				
			};
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
		}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteMOSA(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "flase",
				"-Denable_branch_enhancement", "true",
			
				"-generateMOSuite",
				"-Dstrategy","MOSUITE",
				"-Dalgorithm","MOSA",
				"-Dselection_function","RANK_CROWD_DISTANCE_TOURNAMENT",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				
			};
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
		}
	
	@SuppressWarnings("unchecked")
	public static EvoTestResult evosuiteDynaMOSA(String targetClass, String targetMethod, String cp, int seconds,
			boolean instrumentContext, String fitnessAppraoch, String... seed) {
			EvoSuite evo = new EvoSuite();
			Properties.TARGET_CLASS = targetClass;
			Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
			
			String[] command = new String[]{
				
//				"-Dapply_object_rule", "flase",
				"-Denable_branch_enhancement", "true",
			
				"-generateMOSuite",
				"-Dstrategy","MOSUITE",
				"-Dalgorithm","DynaMOSA",
				"-Dselection_function","RANK_CROWD_DISTANCE_TOURNAMENT",
				
				"-criterion", fitnessAppraoch, 
				"-class", targetClass, 
				"-projectCP", cp,
				"-Dtarget_method", targetMethod,
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(instrumentContext), 
//				"-Dinsertion_uut", "0.1",
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.0",
//				"-Dheadless_chicken_test", "true",
				"-Dp_change_parameter", "0.7",
//				"-Dlocal_search_rate", "30",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dprimitive_reuse_probability", "0",
				"-Dmin_initial_tests", "10",
				"-Dmax_initial_tests", "20",
				"-Ddse_probability", "0",
//				"-Dinstrument_method_calls", "true",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
//				"-Dmax_length", "1",
//				"-Dmax_size", "1",
				"-Dmax_attempts", "100",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", "0.0",
				"-Ddynamic_pool", "0.0",
				"-Dlocal_search_ensure_double_execution", "false",
//				"-Dchromosome_length", "100",
//				"-Dstopping_condition", "maxgenerations",
//				"-DTT", "true",
//				"-Dtt_scope", "target",
				"-seed", (seed == null || seed.length == 0) ? "1578927395578" : seed[0]
				
			};
			List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
			for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());

				return new EvoTestResult(r.getElapseTime(), r.getCoverage(), r.getAge(), r.getAvailabilityRatio(),
						r.getProgressInformation(), r.getIPFlagCoverage(), r.getUncoveredIPFlags(),
						r.getDistributionMap(), r.getUncoveredBranchDistribution(), r.getRandomSeed(), r.getMethodCallAvailabilityMap());
			}
		}

		return null;
		}

}
