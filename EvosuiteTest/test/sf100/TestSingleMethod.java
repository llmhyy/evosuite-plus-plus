package sf100;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.experiment.BenchmarkAddress;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;

public class TestSingleMethod {
	
	@Before
	public void setup() {
		SFConfiguration.sfBenchmarkFolder = BenchmarkAddress.address;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		
		Properties.SEARCH_BUDGET = 60000;
		Properties.GLOBAL_TIMEOUT = 60000;
		Properties.TIMEOUT = 3000000;
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//		FileUtils.deleteFolder(new File("/Users/lylytran/Projects/Evosuite/experiments/SF100_unittest/evoTest-reports"));
	}
	
	@Test
	public void runPropertiesUtil() {
		String projectId = "38_javabullboard";
		String projectName = "javabullboard";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"framework.util.PropertyUtils#copyProperties(Ljava/lang/Object;Ljava/lang/Object;)V"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		String fitnessApproach = "fbranch";
		int repeatTime = 5;
		int budget = 100;
		List<EvoTestResult> results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
		fitnessApproach = "branch";
		List<EvoTestResult> results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
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
	
	@Test
	public void runMath() {
		String projectId = "38_javabullboard";
		String projectName = "javabullboard";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"framework.util.PropertyUtils#copyProperties(Ljava/lang/Object;Ljava/lang/Object;)V"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		String fitnessApproach = "fbranch";
		int repeatTime = 1;
		int budget = 100;
		List<EvoTestResult> results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
		fitnessApproach = "branch";
		List<EvoTestResult> results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
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
	
	@Test
	public void runTest() {
		String projectId = "105_math";
		String projectName = "math";
		String[] targetMethods = new String[]{
				"org.apache.commons.math.dfp.Dfp#trunc(Lorg/apache/commons/math/dfp/DfpField$RoundingMode;)Lorg/apache/commons/math/dfp/Dfp;"
				};
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 1;
		int budget = 100;
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		System.out.println("fbranch" + ":");
		for(EvoTestResult lu: results0){
			System.out.println(lu.getCoverage());
			System.out.println(lu.getProgress());
		}
		
//		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
//		System.out.println("branch" + ":");
//		for(EvoTestResult lu: results1){
//			System.out.println(lu.getCoverage());
//			System.out.println(lu.getProgress());
//		}
	}
	
	@Test
	public void runTest2() {
		String projectId = "102_colt";
		String projectName = "colt";
		String[] targetMethods = new String[]{
//				"com.ib.client.EClientSocket#placeOrder(ILcom/ib/client/Contract;Lcom/ib/client/Order;)V",
				"hep.aida.bin.MightyStaticBin1D#compareWith(Lhep/aida/bin/AbstractBin1D;)Ljava/lang/String;"
				
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 3;
		int budget = 100;
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
	
	@Test
	public void runTest3() {
		String projectId = "102_colt";
		String projectName = "colt";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
				"hep.aida.bin.MightyStaticBin1D#compareWith(Lhep/aida/bin/AbstractBin1D;)Ljava/lang/String;"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 1;
		int budget = 100000;
		results0 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
//		fitnessApproach = "branch";
//		results1 = CommonTestUtil.evoTestSingleMethod(projectId, projectName, targetMethods, fitnessApproach, repeatTime, budget, true);
		
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
	
	@Test
	public void runTest4() {
		String projectId = "105_math";
		String projectName = "math";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
				"org.apache.commons.math.linear.OpenMapRealMatrix#multiply(Lorg/apache/commons/math/linear/OpenMapRealMatrix;)Lorg/apache/commons/math/linear/OpenMapRealMatrix;"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 3;
		int budget = 100;
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
