package regression.objectconstruction.graphgeneration.testcase.staticfield;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.evosuite.Properties;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import com.test.TestUtility;

public class PublicStaticFieldExampleTest {
	@Test
	public void testPrivateInstanceFieldExample() {
		Class<?> clazz = regression.objectconstruction.graphgeneration.example.staticfield.PublicStaticFieldExample.class;

		String methodName = "targetM";
		int parameterNum = 0;

		String targetClass = clazz.getCanonicalName();
		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		
		Properties.TARGET_CLASS = targetClass;
		Properties.TARGET_METHOD = targetMethod;
		
		String cp = "target/test-classes";
		
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
	}
}
