package sf100;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import evosuite.shell.TempGlobalVariables;
import evosuite.shell.experiment.BenchmarkAddress;
import evosuite.shell.experiment.SFConfiguration;

public class TestSingleMethod {
	
	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
		
//		Properties.SEARCH_BUDGET = 60000;
//		Properties.GLOBAL_TIMEOUT = 60000;
//		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void runOther() {
		String projectId = "1_tullibee";
		String projectName = "tullibee";
		String[] targetMethods = new String[]{
				"com.ib.client.EClientSocket#cancelHistoricalData(I)V"
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
		String fitnessApproach = "branch";
		int repeatTime = 3;
		int budget = 10;
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
				targetMethods, fitnessApproach, repeatTime, budget, true, null);
		
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
	public void runColt() {
		String projectId = "102_colt";
		String projectName = "colt";
		String[] targetMethods = new String[]{
				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
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
		int repeatTime = 5;
		int budget = 100;
//		Long seed = 1556814527153L;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void runJBlas() {
		String projectId = "103_jblas";
		String projectName = "jblas";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
				"org.jblas.DoubleMatrix#min()D"
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
		int repeatTime = 5;
		int budget = 100;
//		Long seed = 1556171038486L;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void runMath() {
		String projectId = "105_math";
		String projectName = "math";
		String[] targetMethods = new String[]{
				"org.apache.commons.math.util.MathUtils#equalsIncludingNaN([F[F)Z"
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
//				"org.apache.commons.math.stat.descriptive.SummaryStatistics#equals(Ljava/lang/Object;)Z"
//				"org.apache.commons.math.linear.OpenMapRealVector#subtract(Lorg/apache/commons/math/linear/OpenMapRealVector;)Lorg/apache/commons/math/linear/OpenMapRealVector;"
//				"org.apache.commons.math.linear.OpenMapRealVector#add(Lorg/apache/commons/math/linear/OpenMapRealVector;)Lorg/apache/commons/math/linear/OpenMapRealVector;"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 10000;
		Long seed = null;
		seed = 1556192853402L;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, 
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	private List<Long> checkRandomSeeds(List<EvoTestResult> results0) {
		List<Long> randomSeeds = new ArrayList<>();
		for(EvoTestResult lu: results0){
			randomSeeds.add(lu.getRandomSeed());
		}
		
		return randomSeeds;
	}
	
	private void printResult(List<EvoTestResult> results0) {
		for(EvoTestResult lu: results0){
			System.out.println("coverage: " + lu.getCoverage() + ", age: " 
					+ lu.getAge() + ", seed: " + lu.getRandomSeed() + ", time: " + lu.getTime());
			System.out.println(lu.getProgress());
		}
		
	}
	
}
