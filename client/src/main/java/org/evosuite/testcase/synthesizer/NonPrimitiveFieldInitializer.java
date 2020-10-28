package org.evosuite.testcase.synthesizer;

import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.generic.GenericField;

public class NonPrimitiveFieldInitializer extends FieldInitializer{
	
	public VariableReference assignField(TestFactory testFactory, TestCase test, String fieldType, GenericField genericField,
			int insertionPosition, FieldReference fieldVar, boolean allowNullValue) throws ConstructionFailedException {
		
		VariableReference constructorVarRef = 
				ConstructionPathSynthesizer.addConstructorForClass(testFactory, test, insertionPosition, fieldType, allowNullValue);
		if (constructorVarRef == null) {
			return null;
		}
		
		return constructorVarRef;
	}
}
