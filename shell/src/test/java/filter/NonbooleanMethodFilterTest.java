package filter;

import java.net.MalformedURLException;

import org.junit.Test;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import evosuite.shell.listmethod.InterproceduralNonBooleanFlagMethodFilter;

public class NonbooleanMethodFilterTest {
	InterproceduralNonBooleanFlagMethodFilter filter = new InterproceduralNonBooleanFlagMethodFilter();
	String className = "com.example.FlagEffectExample";
	String methodName = "";

	@Test
	public void testPrimitiveReturnConstant() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		methodName = "CallerStringReturnConstantEqual()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerStringReturnConstantNotEqual()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerIntReturnConstantGreater()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerIntReturnConstantGreaterEqual()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerIntReturnConstantLesser()I";
		assert filter.checkMethod(className, methodName);

		methodName = "CallerIntReturnConstantGreaterLesserEqual()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerIntReturnConstantMethodCall()I";
		assert filter.checkMethod(className, methodName);
	}

	@Test
	public void testPrimitiveReturnVariable() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		methodName = "CallerIntReturnVariablePositive()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerIntReturnVariableNegative()I";
		assert !filter.checkMethod(className, methodName);
	}
	
	@Test
	public void testObjectReturnConstant() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		methodName = "CallerObjectReturnConstantLeft()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerObjectReturnConstantRight()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerObjectReturnConstantNoParam()I";
		assert filter.checkMethod(className, methodName);
	}
	
	@Test
	public void testObjectReturnVariable() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		methodName = "CallerObjectReturnVariablePositive()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerObjectReturnVariableNegative()I";
		assert !filter.checkMethod(className, methodName);
	}
	
	@Test
	public void testObjectReturnVariableParam() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		methodName = "CallerObjectReturnVariableParamPositive()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerObjectReturnVariableParamNegative()I";
		assert !filter.checkMethod(className, methodName);
	}
	
	@Test
	public void testReturnMethodCall() throws ClassNotFoundException, AnalyzerException, MalformedURLException {
		methodName = "CallerReturnMethodCall()I";
		assert filter.checkMethod(className, methodName);
		
		methodName = "CallerReturnVariableMethodCall()I";
		assert filter.checkMethod(className, methodName);
	}

}
