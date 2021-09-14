package org.evosuite.seeding.smart;

import java.lang.reflect.Type;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.testcase.statements.ValueStatement;

public class ObservedConstant {
	private Object value;
	private Class<?> clazz;
	private BytecodeInstruction ins;

	public ObservedConstant(Object value, Class<?> clazz, BytecodeInstruction ins) {
		super();
		this.value = value;
		this.clazz = clazz;
		this.ins = ins;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public BytecodeInstruction getIns() {
		return ins;
	}

	public void setIns(BytecodeInstruction ins) {
		this.ins = ins;
	}

	public boolean isCompatible(ValueStatement sta) {
		if(sta.getAssignmentValue() == null) {
			return false;
		}
		Class<?> valueType = sta.getAssignmentValue().getClass();
		
		Class<?> clazz = value.getClass();

		if (valueType.getTypeName().equals(clazz.getTypeName())
				|| clazz.getTypeName().toLowerCase().contains(valueType.getTypeName())) {
			//type conversion
			value = clazz.cast(value);
			return true;
		} else {
			//correlation
			if((valueType.equals(String.class)) && (clazz.equals(Character.class) || clazz.equals(Integer.class))) {
				if (value instanceof Integer) {
					/// obj is a character
					int in = (Integer) value;
					Character c = (char) in;
					value = c.toString();
				}else
					value = value.toString();
				return true;
			}
			if((valueType.equals(Character.class)) && (clazz.equals(String.class) || clazz.equals(Integer.class))) {
				sta.setAssignmentValue((String)sta.getAssignmentValue().toString());
				if (value instanceof Integer) {
					/// obj is a character
					int in = (Integer) value;
					Character c = (char) in;
					value = c.toString();
				}else {
					value = value.toString();
				}
				return true;
			}
		}

		return false;
	}

}
