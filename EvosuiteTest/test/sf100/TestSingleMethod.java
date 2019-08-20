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
	public void runTullibee() {
		String projectId = "1_tullibee";
		String[] targetMethods = new String[]{
				"com.ib.client.EReader#processMsg(I)Z"
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
	public void runSumma() {
		String projectId = "44_summa";
		String[] targetMethods = new String[]{
//				"org.databene.jdbacl.model.DefaultDatabase#getTable(Ljava/lang/String;Z)Lorg/databene/jdbacl/model/DBTable;" 
				"dk.statsbiblioteket.summa.ingest.split.XMLSplitterHandler#startElement(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Attributes;)V"
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
				"corina.site.Location#setLocation(Ljava/lang/String;)V" 
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
				"net.sourceforge.schemaspy.util.Inflection#pluralize(Ljava/lang/String;)Ljava/lang/String;" 
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
//				"weka.gui.boundaryvisualizer.KDDataGenerator#generateInstances([I)[[D"
//				"weka.knowledgeflow.Data#setConnectionName(Ljava/lang/String;)V"
//				"org.apache.commons.compress.compressors.CompressorStreamFactory#createCompressorInputStream(Ljava/io/InputStream;)Lorg/apache/commons/compress/compressors/CompressorInputStream;"
//				"cern.colt.matrix.DoubleMatrix1D#assign(Lcern/colt/matrix/DoubleMatrix1D;)Lcern/colt/matrix/DoubleMatrix1D;"
//				"weka.associations.Apriori#buildAssociations(Lweka/core/Instances;)V"
//				"cern.colt.matrix.ObjectMatrix3D#assign(Lcern/colt/matrix/ObjectMatrix3D;)Lcern/colt/matrix/ObjectMatrix3D;"
//				"cern.colt.matrix.DoubleFactory2D#sample(Lcern/colt/matrix/DoubleMatrix2D;DD)Lcern/colt/matrix/DoubleMatrix2D;"
//				"cern.colt.matrix.linalg.Algebra#inverse(Lcern/colt/matrix/DoubleMatrix2D;)Lcern/colt/matrix/DoubleMatrix2D;"
//				"cern.jet.random.sampling.RandomSamplingAssistant#test(JJ)V"
//				"hep.aida.bin.DynamicBin1D#sample(IZLcern/jet/random/engine/RandomEngine;Lcern/colt/buffer/DoubleBuffer;)V"
//				"org.apache.commons.compress.archivers.zip.Simple8BitZipEncoding#canEncode(Ljava/lang/String;)Z"
//				"weka.associations.FPGrowth#main([Ljava/lang/String;)V"
//				"weka.associations.FPGrowth#mineTree(Lweka/associations/FPGrowth$FPTreeRoot;Lweka/associations/FPGrowth$FrequentItemSets;ILweka/associations/FPGrowth$FrequentBinaryItemSet;I)V"
//				"weka.classifiers.bayes.net.search.SearchAlgorithm#reverseArcMakesSense(Lweka/classifiers/bayes/BayesNet;Lweka/core/Instances;II)Z"
//				"weka.classifiers.trees.j48.GainRatioSplitCrit#splitCritValue(Lweka/classifiers/trees/j48/Distribution;DD)D"
//				"weka.core.ContingencyTables#entropyConditionedOnColumns([[D)D"
//				"weka.core.FindWithCapabilities#setOptions([Ljava/lang/String;)V"
//				"weka.core.TechnicalInformation#main([Ljava/lang/String;)V"
//				"weka.core.TestInstances#main([Ljava/lang/String;)V"
//				"weka.gui.beans.SubstringReplacer#connectionNotification(Ljava/lang/String;Ljava/lang/Object;)V"
				"weka.gui.scripting.SyntaxDocument#checkForTokens(Ljava/lang/String;II)V"
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
	public void runSpring() {
		String projectId = "114_spring";
		String[] targetMethods = new String[]{
//				"java_cup.runtime.lr_parser#error_recovery(Z)Z",
//				"weka.Run#main([Ljava/lang/String;)V"
				"org.springframework.beans.propertyeditors.CharacterEditor#setAsText(Ljava/lang/String;)V"
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
		int budget = 10000;
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
//				"org.apache.commons.math.util.MathUtils#equalsIncludingNaN(FF)Z"
//				"org.jfree.data.Range#constrain(D)D"
				"org.jfree.data.statistics.SimpleHistogramDataset#addObservation(DZ)V"
//				"org.apache.commons.math.util.OpenIntToDoubleHashMap#get(I)D"
//				"org.apache.commons.math.linear.OpenMapRealVector#setEntry(ID)V"
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
//				"org.jfree.chart.plot.XYPlot#drawHorizontalLine(Ljava/awt/Graphics2D;Ljava/awt/geom/Rectangle2D;DLjava/awt/Stroke;Ljava/awt/Paint;)V"
//				"org.jfree.chart.renderer.xy.XYDifferenceRenderer#drawItemPass0(Ljava/awt/Graphics2D;Ljava/awt/geom/Rectangle2D;Lorg/jfree/chart/plot/PlotRenderingInfo;Lorg/jfree/chart/plot/XYPlot;Lorg/jfree/chart/axis/ValueAxis;Lorg/jfree/chart/axis/ValueAxis;Lorg/jfree/data/xy/XYDataset;IILorg/jfree/chart/plot/CrosshairState;)V"
				"org.jfree.data.Range#constrain(D)D"
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
//				"com.sleepycat.je.log.JEFileFilter#accept(Ljava/io/File;Ljava/lang/String;)Z"
				"com.sleepycat.je.log.CheckpointFileReader#isTargetEntry(BB)Z"
		};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 1000000;
//		Long seed = 1556171038486L;
		Long seed = null;
		
		String fitnessApproach = "branch";
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
	public void runGlengineer() {
		String projectId = "40_glengineer";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"glengineer.agents.GroupAgent#findDependingGroupByNames(Ljava/lang/String;Ljava/lang/String;)Lglengineer/agents/GroupAgent;"
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
	public void runJavabullboard() {
		String projectId = "38_javabullboard";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"framework.util.FileUtils#listFiles(Ljava/lang/String;Ljava/lang/String;Z)Ljava/util/Collection;"
//				"framework.util.StringUtils#matchPattern(Ljava/lang/String;Ljava/lang/String;Z)Z"
				"framework.persistence.jdbc.ComponentManager#getFullName(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
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
	
//	@Test
//	public void runSpring() {
//		String projectId = "114_spring";
//		String[] targetMethods = new String[]{
////				"org.jscience.JScience#multiplyMatrices([[D)V"
////				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
////				};
////				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
////				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"org.springframework.beans.propertyeditors.ClassEditor#setAsText(Ljava/lang/String;)V"
//		};
//		
//		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 1000000;
////		Long seed = 1556171038486L;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		TempGlobalVariables.seeds = checkRandomSeeds(results0);
//		
//		fitnessApproach = "branch";
////		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  
////				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		System.out.println("fbranch" + ":");
//		printResult(results0);
//		System.out.println("branch" + ":");
//		printResult(results1);
//	}
	
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
	public void runInstagram() {
		String projectId = "118_instagram";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.json.JSONTokener#next(I)Ljava/lang/String;"
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
	public void runCaloriecount() {
		String projectId = "78_caloriecount";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.lts.io.archive.AbstractNestedArchive#getEntryType(Ljava/lang/String;)I"
//				"com.lts.io.DirectoryScanner#matchPath(Ljava/lang/String;Ljava/lang/String;)Z"
//				"com.lts.io.DirectoryScanner#couldHoldIncluded(Ljava/lang/String;)Z"
				"com.lts.io.DirectoryScanner#isExcluded(Ljava/lang/String;)Z"
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
	public void runCrimson() {
		String projectId = "121_crimson";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.apache.crimson.tree.XmlDocument#createProcessingInstruction(Ljava/lang/String;Ljava/lang/String;)Lorg/w3c/dom/ProcessingInstruction;"
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
	public void runPoi() {
		String projectId = "126_poi";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
				"org.apache.poi.hssf.model.LinkTable#checkExternSheet(II)I"
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
	public void runDbEverywhere() {
		String projectId = "54_dbeverywhere";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"nu.staldal.lagoon.core.EntryWithSource#fileHasBeenUpdated(Ljava/lang/String;J)Z"
//				"com.gbshape.dbe.mysql.MysqlTableStructure#getStatus(Lcom/gbshape/dbe/struts/bean/DBDataBean;Ljava/lang/String;Z)Lcom/gbshape/dbe/struts/bean/TableStatusBean;"
				"com.gbshape.dbe.utils.DBEHelper#appendValues(Lcom/gbshape/dbe/struts/bean/DBDataBean;Ljava/lang/StringBuffer;Lcom/gbshape/dbe/struts/bean/ColumnBean;Ljava/lang/String;)V"
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
	public void runLagoon() {
		String projectId = "52_lagoon";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"nu.staldal.lagoon.core.EntryWithSource#fileHasBeenUpdated(Ljava/lang/String;J)Z"
				"nu.staldal.lagoon.core.LagoonProcessor#getFileURLRelativeTo(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
//				"nu.staldal.lagoon.core.EntryWithSource#getFileAsJAXPSource(Ljava/lang/String;Lnu/staldal/lagoon/core/Target;)Ljavax/xml/transform/Source;"
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
	
	@Test
	public void runJmf() {
		String projectId = "131_jmf";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"com.ibm.media.bean.multiplayer.MultiPlayerBean#addLink(ILjava/lang/String;JJ)Z"
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
	public void runAshkelon() {
		String projectId = "132_ashkelon";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.ashkelon.ClassType#addInterface(Ljava/lang/String;)V"
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
	public void runAspectwerkz() {
		String projectId = "133_aspectwerkz";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.codehaus.aspectwerkz.annotation.AspectAnnotationParser#getExpressionElseValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
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
	public void runBsf() {
		String projectId = "134_bsf";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.bsf.util.StringUtils#isValidPackageName(Ljava/lang/String;)Z"
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
	public void runBurlap() {
		String projectId = "135_burlap";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"burlap.behavior.stochasticgames.agents.twoplayer.singlestage.equilibriumplayer.BimatrixEquilibriumSolver#solve([[D[[D)V"
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
	public void runCastor() {
		String projectId = "136_castor";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.exolab.castor.jdo.drivers.SapDbFactory#quoteName(Ljava/lang/String;)Ljava/lang/String;"
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
	public void runCommonsMath() {
		String projectId = "137_commons-math";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.commons.math.linear.RealMatrixImpl#getRow(I)[D"
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
	public void runDwr() {
		String projectId = "138_dwr";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.directwebremoting.util.Base64#isArrayByteBase64([B)Z"
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
	public void runHsqldb() {
		String projectId = "139_hsqldb";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.hsqldb.HsqlDateTime#toJavaDatePattern(Ljava/lang/String;)Ljava/lang/String;"
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
	public void runHtmlUnit() {
		String projectId = "140_htmlunit";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"com.gargoylesoftware.htmlunit.util.EncodingSniffer#extractEncodingFromContentType(Ljava/lang/String;)Ljava/nio/charset/Charset;"
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
	public void runHivemind() {
		String projectId = "141_hivemind";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.hivemind.HiveMind#isNonBlank(Ljava/lang/String;)Z"
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
	public void runEsper() {
		String projectId = "142_esper";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"com.espertech.esper.epl.view.OutputConditionPolledCount#updateOutputCondition(II)Z"
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
	public void runExist() {
		String projectId = "143_exist";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.dbxml.core.filer.BTree#create(S)Z"
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
	public void runExolabcore() {
		String projectId = "144_exolabcore";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.exolab.core.messenger.PacketChannel#doReceive(J)Ljava/lang/Object;"
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
	public void runFesi() {
		String projectId = "145_fesi";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"FESI.Extensions.ESRowSet#getProperty(Ljava/lang/String;I)LFESI/Data/ESValue;"
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
	public void runFop() {
		String projectId = "146_fop";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.fop.area.MainReference#createSpan(Z)Lorg/apache/fop/area/Span;"
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
	public void runGrizzly() {
		String projectId = "147_grizzly";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.coyote.Response#setHeader(Ljava/lang/String;Ljava/lang/String;)V"
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
	public void runCommonsLang() {
		String projectId = "148_commons-lang";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.apache.commons.lang.CharSet#contains(C)Z"
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
	public void runHttpunit() {
		String projectId = "149_httpunit";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"com.meterware.httpunit.javascript.ScriptingEngineImpl#runScript(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
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
	public void runIdb() {
		String projectId = "150_idb";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.enhydra.instantdb.db.Database#dbOpen(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/enhydra/instantdb/db/Transaction;"
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
	public void runJanino() {
		String projectId = "151_janino";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.codehaus.janino.Descriptor#size(Ljava/lang/String;)S"
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
	public void runIsorelax() {
		String projectId = "152_isorelax";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"jp.gr.xml.relax.sax.SimpleEntityResolver#resolveEntity(Ljava/lang/String;Ljava/lang/String;)Lorg/xml/sax/InputSource;"
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
	public void runItext() {
		String projectId = "153_itext";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"com.lowagie.text.markup.Parser#endElement(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
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
	public void runJacl() {
		String projectId = "154_jacl";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"tcl.lang.Util#stringMatch(Ljava/lang/String;Ljava/lang/String;)Z"
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
	public void runJalopy() {
		String projectId = "155_jalopy";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"antlr.actions.cpp.ActionLexer#mSTUFF(Z)V"
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
	public void runJavaCup() {
		String projectId = "158_java-cup";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"java_cup.production#is_id_char(C)Z"
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
	public void runJavacc() {
		String projectId = "159_javacc";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.javacc.jjtree.TokenUtils#remove_escapes_and_quotes(Lorg/javacc/jjtree/Token;Ljava/lang/String;)Ljava/lang/String;"
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
	public void runJavagroups() {
		String projectId = "161_javagroups";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.javagroups.log.Trace#setOutput(Ljava/lang/String;ILjava/lang/String;)V"
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
	public void runJaxen() {
		String projectId = "162_jaxen";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"org.jaxen.saxpath.base.Verifier#isXMLNCNameCharacter(C)Z"
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
	public void runJcifs() {
		String projectId = "163_jcifs";
		String[] targetMethods = new String[]{
//				"org.jscience.JScience#multiplyMatrices([[D)V"
//				"org.jscience.mathematics.function.Polynomial#valueOf(Lorg/jscience/mathematics/structure/Ring;Lorg/jscience/mathematics/function/Term;)Lorg/jscience/mathematics/function/Polynomial;"
//				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
//				"com.sleepycat.collections.DataCursor#getLockMode(Z)Lcom/sleepycat/je/LockMode;"
//				"com.google.common.base.Ascii#toLowerCase(Ljava/lang/String;)Ljava/lang/String;"
				"jcifs.UniAddress#getAllByName(Ljava/lang/String;Z)[Ljcifs/UniAddress;"
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
