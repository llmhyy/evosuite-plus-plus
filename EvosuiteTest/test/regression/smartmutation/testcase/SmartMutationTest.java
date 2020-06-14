package regression.smartmutation.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import com.test.TestUtility;

import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class SmartMutationTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
		Properties.APPLY_OBJECT_RULE = false;
		Properties.ADOPT_SMART_MUTATION = true;
	}
	
	@Test
	public void testStringArrayExample() {
		Class<?> clazz = regression.smartmutation.example.StringArrayExample.class;

		String methodName = "main";
		int parameterNum = 1;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		String cp = "target/test-classes";

		String fitnessApproach = "fbranch";

		int timeBudget = 100;
		EvoTestResult resultT = null;
		EvoTestResult resultF = null;
		
		try {
			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		} catch (Exception e1) {
			try {
				resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			} catch (Exception e2) {
				resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			}
		}

		Properties.ADOPT_SMART_MUTATION = false;
		try {
			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		} catch (Exception e1) {
			try {
				resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			} catch (Exception e2) {
				resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
			}
		}

		if (resultT == null) {
			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		}

		if (resultF == null) {
			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
		}

		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		
		assert ageT <= 70;
		assert timeT <= 15;
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT == 1.0;
	}
	
	@Test
	public void runWekaJ48Example() {
		String projectId = "101_weka";
		String[] targetMethods = new String[]{
				"weka.classifiers.trees.J48#buildClassifier(Lweka/core/Instances;)V"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "fbranch";
		Properties.APPLY_OBJECT_RULE = true;
		Properties.ENABLE_BRANCH_ENHANCEMENT = true;
		
		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		Properties.ADOPT_SMART_MUTATION = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		EvoTestResult resultT = resultsT.get(0);
		EvoTestResult resultF = resultsF.get(0);
		
		int ageT = resultT.getAge();
		int timeT = resultT.getTime();
		double coverageT = resultT.getCoverage();
		int ageF = resultF.getAge();
		int timeF = resultF.getTime();
		double coverageF = resultF.getCoverage();
		
		assert ageT < ageF;
		assert timeT < timeF;
		assert coverageT > coverageF;
	}

}
