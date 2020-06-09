package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AbstractStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericField;

public class NonPrimitiveFieldInitializer extends FieldInitializer{
	protected VariableReference addConstructorForClass(TestFactory testFactory, TestCase test, int position, String desc)
			throws ConstructionFailedException {
		try {
			String fieldType;
			if (desc.contains("/")) {
				fieldType = desc.replace("/", ".").substring(desc.indexOf("L") + 1, desc.length() - 1);
			} else {
				fieldType = desc;
			}
			Class<?> fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(fieldType);

			if (fieldClass.isInterface() || Modifier.isAbstract(fieldClass.getModifiers())) {
				Set<String> subclasses = DependencyAnalysis.getInheritanceTree()
						.getSubclasses(fieldClass.getCanonicalName());
				String subclass = Randomness.choice(subclasses);
				fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(subclass);
			}

			Constructor<?> constructor = Randomness.choice(fieldClass.getConstructors());
			if (constructor != null) {
				GenericConstructor genericConstructor = new GenericConstructor(constructor, fieldClass);
				VariableReference variableReference = testFactory.addConstructor(test, genericConstructor, position + 1,
						2);
				return variableReference;
			}
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public AbstractStatement assignField(TestFactory testFactory, TestCase test, String fieldType, GenericField genericField,
			int insertionPosition, FieldReference fieldVar) throws ConstructionFailedException {
		AbstractStatement stmt;
		VariableReference constructorVarRef = addConstructorForClass(testFactory, test, insertionPosition, fieldType);
		if (constructorVarRef == null) {
			return null;
		}
		if (genericField.isFinal()) {
			return null;
		}
		stmt = new AssignmentStatement(test, fieldVar, constructorVarRef);
		return stmt;
	}
}
