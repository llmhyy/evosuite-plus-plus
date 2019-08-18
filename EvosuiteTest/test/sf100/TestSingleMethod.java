package sf100;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import evosuite.shell.TempGlobalVariables;

public class TestSingleMethod {
	
	@Before
	public void setup() {
//		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		
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
	public void runJDBACL() {
		String projectId = "13_jdbacl";
		String[] targetMethods = new String[]{
//				"org.databene.jdbacl.model.DefaultDatabase#getTable(Ljava/lang/String;Z)Lorg/databene/jdbacl/model/DBTable;" 
				"org.databene.jdbacl.SQLUtil#mutatesDataOrStructure(Ljava/lang/String;)Ljava/lang/Boolean;"
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
	public void runSbmlreader2() {
		String projectId = "34_sbmlreader2";
		String[] targetMethods = new String[]{
//				"org.databene.jdbacl.model.DefaultDatabase#getTable(Ljava/lang/String;Z)Lorg/databene/jdbacl/model/DBTable;" 
				"jigcell.sbml2.KineticLaw#setSubstanceUnits(Ljava/lang/String;)V"
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
	public void runCorina() {
		String projectId = "35_corina";
		String[] targetMethods = new String[]{
				"corina.editor.DecadalModel#isCellEditable(II)Z" 
//				"corina.site.Location#setLocation(Ljava/lang/String;)V"
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
	public void runSchemaspy() {
		String projectId = "36_schemaspy";
		String[] targetMethods = new String[]{
				"net.sourceforge.schemaspy.util.Dot#setRenderer(Ljava/lang/String;)V" 
//				"corina.site.Location#setLocation(Ljava/lang/String;)V"
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
		int repeatTime = 5;
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
//				"java_cup.runtime.lr_parser#error_recovery(Z)Z",
				"weka.Run#main([Ljava/lang/String;)V"
//				"weka.knowledgeflow.Data#setConnectionName(Ljava/lang/String;)V"
//				"org.apache.commons.compress.compressors.CompressorStreamFactory#createCompressorInputStream(Ljava/io/InputStream;)Lorg/apache/commons/compress/compressors/CompressorInputStream;"
//				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
//				"weka.associations.Apriori#buildAssociations(Lweka/core/Instances;)V"
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
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
//				"org.apache.commons.math.util.OpenIntToDoubleHashMap#get(I)D"
				"org.apache.commons.math.linear.OpenMapRealVector#setEntry(ID)V"
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
	
	@Test
	public void runJEdit() {
		String projectId = "129_jedit";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
				"org.gjt.sp.jedit.jEdit#main([Ljava/lang/String;)V"
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
		int budget = 10000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		TempGlobalVariables.seeds = checkRandomSeeds(results0);
//		
//		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		System.out.println("fbranch" + ":");
		printResult(results0);
		System.out.println("branch" + ":");
		printResult(results1);
	}
	
	@Test
	public void runJScience() {
		String projectId = "104_jscience";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
				"org.jscience.mathematics.number.LargeInteger#shiftRight(I)Lorg/jscience/mathematics/number/LargeInteger;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runBerkeleydb() {
		String projectId = "107_berkeleydb";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.sleepycat.collections.StoredList#listIterator(I)Ljava/util/ListIterator;"
				"com.sleepycat.je.tree.IN#deleteEntry(IZ)Z"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runBouncycastle() {
		String projectId = "108_bouncycastle-jce";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.bouncycastle.crypto.params.DESedeParameters#isWeakKey([BII)Z"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runEdenlib() {
		String projectId = "109_edenlib";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"com.anthonyeden.lib.util.TextUtilities#hasNonAlphaNumericCharacters(Ljava/lang/String;[C)Z"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runLdapsdk() {
		String projectId = "110_ldapsdk";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"netscape.ldap.LDAPAttribute#removeValue([B)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runJython() {
		String projectId = "111_jython";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.python.modules.ucnhash#getValue(Ljava/lang/String;II)I"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runCvslib() {
		String projectId = "112_cvslib";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.netbeans.lib.cvsclient.CVSRoot#setMethod(Ljava/lang/String;)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runQuartz() {
		String projectId = "113_quartz";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.quartz.SchedulerContext#setAllowsTransientData(Z)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runSpring() {
		String projectId = "114_spring";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.springframework.beans.propertyeditors.ClassEditor#setAsText(Ljava/lang/String;)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runTurbine() {
		String projectId = "115_turbine";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.apache.turbine.util.FormMessages#getFormMessages(Ljava/lang/String;)[Lorg/apache/turbine/util/FormMessage;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runFindbugs() {
		String projectId = "116_findbugs";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"edu.umd.cs.findbugs.Project#read(Ljava/lang/String;)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runMx4j() {
		String projectId = "117_mx4j";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"mx4j.util.Base64Codec#isArrayByteBase64([B)Z"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runOpenjms() {
		String projectId = "118_openjms";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.exolab.jms.server.JmsServerSession#unsubscribe(Ljava/lang/String;)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runAptconvert() {
		String projectId = "119_aptconvert";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"fr.pixware.apt.convert.DocBookSink#link(Ljava/lang/String;)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runCocoon() {
		String projectId = "120_cocoon";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.apache.cocoon.transformation.pagination.Pagesheet#isInPage(IILjava/lang/String;)Z"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runHttpclient() {
		String projectId = "121_HTTPClient";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"HTTPClient.Codecs#quotedPrintableEncode(Ljava/lang/String;)Ljava/lang/String;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runKawa() {
		String projectId = "122_kawa";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"gnu.expr.FindCapturedVars#allocUnboundDecl(Ljava/lang/String;)Lgnu/expr/Declaration;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runProxool() {
		String projectId = "123_proxool";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.logicalcobwebs.proxool.configuration.XMLConfigurator#endElement(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runAspectj() {
		String projectId = "124_aspectj";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.aspectj.asm.StructureModel#findRootNodeForSourceFile(Ljava/lang/String;)Lorg/aspectj/asm/StructureNode;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runJedit() {
		String projectId = "125_jedit";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.gjt.sp.jedit.BufferHistory#getEntry(Ljava/lang/String;)Lorg/gjt/sp/jedit/BufferHistory$Entry;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runActivemq() {
		String projectId = "126_activemq";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.apache.derby.database.UserUtility#getPermission(Ljava/lang/String;)Ljava/lang/String;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runHibernate() {
		String projectId = "127_hibernate";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.hibernate.cfg.Mappings#locatePersistentClassByEntityName(Ljava/lang/String;)Lorg/hibernate/mapping/PersistentClass;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runJdiff() {
		String projectId = "128_jdiff";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"jdiff.API#convertHTMLTagsToXHTML(Ljava/lang/String;)Ljava/lang/String;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runClover() {
		String projectId = "129_clover";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.oro.text.awk.k#c(C)C"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
	public void runGuava() {
		String projectId = "130_guava";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"com.google.common.net.HostAndPort#fromString(Ljava/lang/String;)Lcom/google/common/net/HostAndPort;"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
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
