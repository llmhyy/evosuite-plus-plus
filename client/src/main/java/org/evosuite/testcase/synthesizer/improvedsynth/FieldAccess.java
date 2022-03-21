package org.evosuite.testcase.synthesizer.improvedsynth;

import java.lang.reflect.Field;

public class FieldAccess extends Operation {
	private final Field field;
	
	public FieldAccess(Field field) {
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
}
