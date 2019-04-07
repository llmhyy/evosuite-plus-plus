package sf100;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import evosuite.shell.experiment.BenchmarkAddress;
import evosuite.shell.experiment.SFConfiguration;

public class TestSingleMethod {
	
	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		
//		Properties.SEARCH_BUDGET = 60000;
//		Properties.GLOBAL_TIMEOUT = 60000;
//		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	
	@Test
	public void runTest3() {
		String projectId = "102_colt";
		String projectName = "colt";
		String[] targetMethods = new String[]{
				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
//				"cern.colt.matrix.ObjectMatrix3D#assign(Lcern/colt/matrix/ObjectMatrix3D;)Lcern/colt/matrix/ObjectMatrix3D;"
//				"cern.colt.matrix.DoubleFactory2D#sample(Lcern/colt/matrix/DoubleMatrix2D;DD)Lcern/colt/matrix/DoubleMatrix2D;"
//				"cern.colt.matrix.linalg.Algebra#inverse(Lcern/colt/matrix/DoubleMatrix2D;)Lcern/colt/matrix/DoubleMatrix2D;"
//				"cern.jet.random.sampling.RandomSamplingAssistant#test(JJ)V"
//				"hep.aida.bin.DynamicBin1D#sample(IZLcern/jet/random/engine/RandomEngine;Lcern/colt/buffer/DoubleBuffer;)V"
//				"hep.aida.bin.DynamicBin1D#equals(Ljava/lang/Object;)Z"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 1;
		int budget = 100;
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
//		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
		System.out.println("fbranch" + ":");
		for(EvoTestResult lu: results0){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
			System.out.println(lu.getAge());
			System.out.println(lu.getDistribution());
		}
		
		System.out.println("branch" + ":");
		for(EvoTestResult lu: results1){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
			System.out.println(lu.getAge());
			System.out.println(lu.getDistribution());
		}
	}
	
	@Test
	public void runTest4() {
		String projectId = "105_math";
		String projectName = "math";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
				"org.apache.commons.math.stat.descriptive.SummaryStatistics#equals(Ljava/lang/Object;)Z"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 1;
		int budget = 2000000;
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
		System.out.println("fbranch" + ":");
		for(EvoTestResult lu: results0){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
		
		System.out.println("branch" + ":");
		for(EvoTestResult lu: results1){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
	}
	
}
