package feature.objectconstruction.testgeneration.testcase;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import common.TestUtil;
import common.TestUtility;

public class ObjectConstructionIssueTest extends ObjectOrientedTest {
	@Test
	public void wheelWebTool_visitEnd_example() throws ClassNotFoundException {
		Class<?> clazz = feature.objectconstruction.testgeneration.example.wheelwebtool.WheelFieldVisitor.class;
		String methodName = "visitEnd";
		int numParams = 0;
		int branchLineNumber = 39;
		
		test(clazz, methodName, numParams, branchLineNumber);
	}
	
	@Test
	public void xbus_getDetailsAsTable_example() throws ClassNotFoundException {
		Class<?> clazz = feature.objectconstruction.testgeneration.example.xbus.JournalBean.class;
		String methodName = "getDetailsAsTable";
		int numParams = 0;
		int branchLineNumber = 651;
		
		test(clazz, methodName, numParams, branchLineNumber);
	}
	
	private void test(Class<?> clazz, String methodName, int numParams, int branchLineNumber) throws ClassNotFoundException {
		Properties.RANDOM_SEED = 1634620626101L;
		setup();
		Properties.TARGET_CLASS = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, numParams);
		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		Properties.TARGET_METHOD = targetMethod;
		
		ArrayList<Branch> rankedList = buildObjectConstructionGraph();
		Branch b = TestUtil.searchBranch(rankedList, branchLineNumber);
		
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, true, false);
	}
}
