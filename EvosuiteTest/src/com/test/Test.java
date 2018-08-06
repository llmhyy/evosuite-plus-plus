package com.test;

import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.Algorithm;
import org.evosuite.result.TestGenerationResult;

public class Test {

	@SuppressWarnings("unchecked")
	public void evosuite(String targetClass, String targetMethod, String cp) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
//		Properties.TARGET_METHOD = targetClass+".test(DDI)V";
		Properties.ALGORITHM = Algorithm.MONOTONICGA;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		
		String[] command = new String[] {"-generateSuite", "-class", targetClass, 
//				"-Dtarget_method", "ttest0(DDI)V", 
				"-projectCP", cp, "-Dsearch_budget", "2"};
//		String[] command = new String[] {"-generateRandom", "-class", targetClass,
//				"-projectCP", cp, "-Dsearch_budget", "10"};
//		String[] command = new String[] {"-generateSuiteUsingDSE", "-class", targetClass,
//				"-projectCP", cp, "-Dsearch_budget", "10"};
		
		List<List<TestGenerationResult>> list 
			= (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for(List<TestGenerationResult> l: list) {
			for(TestGenerationResult r: l) {
				System.out.println(r);
			}
		
		}
		
	}
	
	public static void main(String[] args) {
		String targetClass = "com.test.Example";
		String targetMethod = "test0";
		String cp = "bin";
		
		Properties.DEBUG = true;
		
		Test t = new Test();
		t.evosuite(targetClass, targetMethod, cp);

	}

}
