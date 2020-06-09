package org.evosuite.testcase.synthesizer;

import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.generic.GenericField;

public abstract class FieldInitializer {
	
	public abstract VariableReference assignField(TestFactory testFactory, TestCase test, String fieldType, GenericField genericField,
			int insertionPosition, FieldReference fieldVar) throws ConstructionFailedException;
}
