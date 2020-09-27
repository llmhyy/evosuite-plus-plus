package sf100;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.EvoTestResult;

public class RegressionTests {
	@Before
	public void setup() {
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
	public void runMultiplyTest() {
		String projectId = "105_math";
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
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  targetMethods, fitnessApproach, repeatTime, budget, true, null);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  targetMethods, fitnessApproach, repeatTime, budget, true, null);
		
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
	public void runSummaryStatisticsEquals() {
		String projectId = "105_math";
		String[] targetMethods = new String[]{
//				"cern.colt.matrix.impl.Benchmark#benchmark(IILjava/lang/String;ZIDDD)V"
				"org.apache.commons.math.stat.descriptive.SummaryStatistics#equals(Ljava/lang/Object;)Z"
				};
//				"com.ib.client.OrderState#equals(Ljava/lang/Object;)Z"};
		
		List<EvoTestResult> results0 = new ArrayList<EvoTestResult>();
		List<EvoTestResult> results1 = new ArrayList<EvoTestResult>();
		String fitnessApproach = "fbranch";
		int repeatTime = 3;
		int budget = 100;
		results0 = CommonTestUtil.evoTestSingleMethod(projectId,  targetMethods, fitnessApproach, repeatTime, budget, true, null);
		
		fitnessApproach = "branch";
		results1 = CommonTestUtil.evoTestSingleMethod(projectId,  targetMethods, fitnessApproach, repeatTime, budget, true, null);
		
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
