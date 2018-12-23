package com.test;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.Algorithm;
import org.evosuite.Properties.Criterion;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.statistics.SearchStatistics;

public class Test2 {

	class Tuple{
		int time;
		double coverage;
		public Tuple(int time, double coverage) {
			super();
			this.time = time;
			this.coverage = coverage;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Tuple evosuite(String targetClass, String targetMethod, String cp, int seconds, boolean instrumentContext) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		// Properties.TARGET_METHOD = targetClass+".test(DDI)V";
		Properties.ALGORITHM = Algorithm.MONOTONICGA;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };

		String[] command = new String[] { "-generateSuite", "-class", targetClass, "-projectCP", cp, "-Dsearch_budget",
				String.valueOf(seconds), "-criterion", "branch", "-Dinstrument_context",
				String.valueOf(instrumentContext) };

		// command = new String[] { "-generateSuite", "-class", targetClass,
		// "-projectCP", cp, "-Dsearch_budget",
		// String.valueOf(seconds), "-criterion", "branch"};

		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
//				System.out.println(r);
				return new Tuple(r.getElapseTime(), r.getCoverage());
			}
		}
		
		return null;
	}
	

	public static void main(String[] args) {
		String targetClass = Example0.class.getCanonicalName();
		// + ";" + Util.class.getCanonicalName() + ";" +
		// Math.class.getCanonicalName();
		String targetMethod = "test0";
		String cp = "bin";

		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
		Properties.PORT = 8000;
		// Properties.CLIENT_ON_THREAD = true;

		int timeBudget = 100;
		ETest t = new ETest();
		t.evosuite(targetClass, targetMethod, cp, timeBudget, true);

//		for (int i = 0; i < 10; i++) {
//			Test t = new Test();
//			t.evosuite(targetClass, targetMethod, cp, timeBudget, true);
//		}
	}

}
