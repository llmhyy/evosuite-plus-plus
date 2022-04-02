package feature.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.improvedsynth.DepVariableWrapperUtil;
import org.evosuite.utils.MethodUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;
import feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Parent;

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
		generateCode0(b, false, false);
	}
	
	
	@Test
	public void inaccessibleChild() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Parent.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 29;
//		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode0(b, false, false);
	}
	
	@Test
	public void inaccessibleChild2() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild2.Parent.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 15;
//		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode0(b, false, false);
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
		
		generateCode0(b, false, false);
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
		
		generateCode0(b, false, false);
	}
	
	@Test
	public void arrayElementAccessCase() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.arrayelementaccess.Parent.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 11;
		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
//		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode0(b, false, false);
	}
	
	@Test
	public void recursiveCase() throws ClassNotFoundException, RuntimeException {
		Class<?> clazz = feature.objectconstruction.testgeneration.testcase.ocgexample.recursivecase.LinkedListNode.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(clazz, methodName, numParams);

		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		int branchConditionLineNumber = 24;
		Branch b = TestUtil.searchBranch(rankedList, branchConditionLineNumber);
//		Branch b = TestUtil.getLongestBranch(rankedList, branchConditionLineNumber);
		
		generateCode0(b, false, false);
	}
	
	@Test
	public void testGetterDetector() throws NoSuchMethodException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		Class<?> parentClass = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Parent.class;
		Class<?> childClass = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Child.class;
		String methodName = "method";
		int numParams = 0;
		setupClassAndMethod(parentClass, methodName, numParams);
		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		Method method = getMethod(parentClass, "getGrandchild2", 0);
		Field field = childClass.getDeclaredField("grandchild");
		System.out.println(method.getName() + " is getter? " + DepVariableWrapperUtil.testFieldGetter(method, field));
	}
	
	@Test
	public void testSetterDetector() throws NoSuchMethodException, NoSuchFieldException, SecurityException, ClassNotFoundException {
		Class<?> parentClass = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Parent.class;
		Class<?> grandchildClass = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.Grandchild.class;
		Class<?> greatGrandchildClass = feature.objectconstruction.testgeneration.testcase.ocgexample.inaccessiblechild.GreatGrandchild.class;
		String methodName = "method";
		
		int numParams = 0;
		setupClassAndMethod(parentClass, methodName, numParams);
		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		
		Method method = getMethod(grandchildClass, "setSomeField", 1);
		Field field = grandchildClass.getDeclaredField("someField");
		System.out.println(method.getName() + " is setter? " + DepVariableWrapperUtil.testFieldSetter(method, field));
	}
	
	private Method getMethod(Class<?> callerClass, String methodName, int numParams) throws NoSuchMethodException {
		Method[] methods = callerClass.getDeclaredMethods();
		for (Method method : methods) {
			int currentNumParams = method.getParameterCount();
			String currentMethodName = method.toGenericString();
			boolean isNameMatch = currentMethodName.contains(methodName);
			boolean isNumParamsMatch = currentNumParams == numParams;
			
			if (isNameMatch && isNumParamsMatch) {
				return method;
			}
		}
		throw new NoSuchMethodException("No method with name (" + methodName + ") and " + numParams + " params found!");
	}
}
