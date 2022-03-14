package feature.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class ProjectGapGraphBasedCodeGenerationTest extends ObjectOrientedTest {
	@Before
	public void setupTestEnvironmentParameters() {
		Properties.RANDOM_SEED = 1634620626101L;
		setup();
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
	}
	
	private void setupClassAndMethod(Class<?> clazz, String methodName, int numParams) {
		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, numParams);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		Properties.TARGET_METHOD = targetMethod;
	}
	
	@Test
	public void testGap1() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.example.gap.ArrayElementExample.class;
		
		Properties.TARGET_CLASS = clazz.getCanonicalName();

		Method method = TestUtility.getTargetMethod("arrayElementAccess", clazz, 0);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);

		Properties.TARGET_METHOD = targetMethod;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		Branch b = TestUtil.searchBranch(rankedList, 10);
		
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, false, false);
	}
	
	
	@Test
	public void inaccessibleChild() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Parent.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 11;
//		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode(b, false, false);
	}
	
	@Test
	public void directLeafNodeSetter() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.directsetleafnode.Parent.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 15;
//		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode(b, false, false);
	}
	
	@Test
	public void longGetterCase() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.longgettercase.Parent.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 11;
//		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode(b, false, false);
	}
}
