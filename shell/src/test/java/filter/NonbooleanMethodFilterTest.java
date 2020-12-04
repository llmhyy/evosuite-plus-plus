package filter;

import java.net.MalformedURLException;

import org.junit.Test;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import evosuite.shell.listmethod.InterproceduralNonBooleanFlagMethodFilter;

public class NonbooleanMethodFilterTest {
	InterproceduralNonBooleanFlagMethodFilter filter = new InterproceduralNonBooleanFlagMethodFilter();
	String className = "com.example.FlagEffectExample";

	@Test
	public void testBasicPrimitive() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		String methodName = "CallerBasicEqualString()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerBasicNotEqualString()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerBasicGreaterInt()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerBasicGreaterEqualInt()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerBasicLesserInt()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerBasicLesserEqualInt()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerBasicMethodCallOperand()I";
		assert filter.checkMethod(className, methodName);
	}
	
	@Test
	public void testBasicObject() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		String methodName = "CallerBasicObject()I";
		assert filter.checkMethod(className, methodName);
	}

	@Test
	public void testAdvanced() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		String className = "com.example.FlagEffectExample";
		String methodName = "CallerAdvanced()I";

		InterproceduralNonBooleanFlagMethodFilter filter = new InterproceduralNonBooleanFlagMethodFilter();
		assert filter.checkMethod(className, methodName);
	}

}
