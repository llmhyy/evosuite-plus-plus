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
		
		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void run10() {
		String projectId = "10_water-simulator";
		String[] targetMethods = new String[]{
				"simulator.CA.BehaviourReplyNeighbour#action()V"
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
		int repeatTime = 1;
		int budget = 10;
		Long seed = 1556814527153L;
//		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void run75() {
		String projectId = "75_openhre";
		String[] targetMethods = new String[]{
				"com.browsersoft.openhre.hl7.impl.regular.ExpressionNodeList#addExpressionList(Lcom/browsersoft/openhre/hl7/impl/regular/ExpressionNodeList;)V"
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
		int repeatTime = 1;
		int budget = 100000;
		Long seed = 1556814527153L;
//		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void run84() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctBal#equals(Ljava/lang/Object;)Z"
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
		int repeatTime = 1;
		int budget = 100000;
		Long seed = 1556814527153L;
//		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void run85() {
		String projectId = "85_shop";
		String[] targetMethods = new String[]{
				"umd.cs.shop.JSListConjuncts#<init>(Ljava/io/StreamTokenizer;)V"
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
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1557413456518L;
//		seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	
	@Test
	public void runColt() {
		String projectId = "102_colt";
		String[] targetMethods = new String[]{
				"cern.jet.random.sampling.RandomSamplingAssistant#test(JJ)V"
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
		int repeatTime = 1;
		int budget = 100;
		Long seed = 1557418276377L;
//		seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	
	@Test
	public void runWeka() {
		String projectId = "101_weka";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
				"weka.associations.Apriori#buildAssociations(Lweka/core/Instances;)V"
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
		int repeatTime = 1;
		int budget = 100;
//		Long seed = 1556171038486L;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void runJBlas() {
		String projectId = "103_jblas";
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
		int repeatTime = 1;
		int budget = 10;
//		Long seed = 1556171038486L;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void runMath() {
		String projectId = "105_math";
		String[] targetMethods = new String[]{
				"org.apache.commons.math.util.OpenIntToDoubleHashMap#get(I)D"
//				"org.apache.commons.math.util.MathUtils#equals([D[D)Z"
//				"org.apache.commons.math.util.MathUtils#equalsIncludingNaN([F[F)Z"
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
//				"org.apache.commons.math.stat.descriptive.SummaryStatistics#equals(Ljava/lang/Object;)Z"
//				"org.apache.commons.math.linear.OpenMapRealVector#subtract(Lorg/apache/commons/math/linear/OpenMapRealVector;)Lorg/apache/commons/math/linear/OpenMapRealVector;"
//				"org.apache.commons.math.linear.OpenMapRealVector#add(Lorg/apache/commons/math/linear/OpenMapRealVector;)Lorg/apache/commons/math/linear/OpenMapRealVector;"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100000;
		Long seed = null;
//		seed = 1557106055943L;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void runJFreechart() {
		String projectId = "106_jfreechart";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
				"org.jfree.chart.ChartPanel#createPopupMenu(ZZZZZ)Ljavax/swing/JPopupMenu;"
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
		int repeatTime = 1;
		int budget = 100;
//		Long seed = 1556171038486L;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		TempGlobalVariables.seeds = checkRandomSeeds(results0);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
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
