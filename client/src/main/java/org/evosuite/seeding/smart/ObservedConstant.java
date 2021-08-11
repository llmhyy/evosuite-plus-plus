package org.evosuite.seeding.smart;

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

}
