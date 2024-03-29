package org.evosuite.seeding.smart;

import java.lang.reflect.Type;

import org.evosuite.graphs.cfg.BytecodeInstruction;

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

	public boolean isCompatible(Object val) {
		if(val == null) {
			return false;
		}
		Class<?> valueType = val.getClass();
		
		Class<?> clazz = value.getClass();
		
		if (valueType.getTypeName().equals(clazz.getTypeName())
				|| clazz.getTypeName().toLowerCase().contains(valueType.getTypeName())) {
			return true;
		} else {
			System.currentTimeMillis();
		}

		return false;
	}

}
