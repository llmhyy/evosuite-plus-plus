package org.evosuite.runtime.instrumentation;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class MethodReplacementSupporter {

	private List<Integer> insnOpcodes = new ArrayList<>();
	private List<NewInsnInfo> newInsnInfos = new ArrayList<>();
	private NewInsnInfo currentNewInsnInfo;
	
	/**
	 * Return whether the constructor the NEW instruction of the init method invocation is followed by a DUP instruction.
	 * */
	public boolean onReplaceConstructor(String owner) {
		int i = newInsnInfos.size() - 1;
		for (; i >= 0; i--) {
			NewInsnInfo newInsnInfo = newInsnInfos.get(i);
			if (newInsnInfo.type.equals(owner)) {
				break;
			}
		}
		NewInsnInfo info = newInsnInfos.remove(i);
		return info.followedByADupInsn;
	}
	
	public void visitTypeInsn(int opcode, String type) {
		insnOpcodes.add(opcode);
		if (Opcodes.NEW == opcode) {
			currentNewInsnInfo = new NewInsnInfo();
			currentNewInsnInfo.type = type;
			newInsnInfos.add(currentNewInsnInfo);
		}
	}
	
	public void visitInsn(int opcode) {
		if (Opcodes.DUP == opcode && (insnOpcodes.get(insnOpcodes.size() - 1) == Opcodes.NEW)) {
			currentNewInsnInfo.followedByADupInsn = true;
		}
		insnOpcodes.add(opcode);
	}

	public void visitIntInsn(final int opcode, final int operand) {
		insnOpcodes.add(opcode);
	}

	public void visitVarInsn(final int opcode, final int var) {
		insnOpcodes.add(opcode);
	}

	public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
		insnOpcodes.add(opcode);
	}

	@Deprecated
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		insnOpcodes.add(opcode);
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		insnOpcodes.add(opcode);
	}

	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		insnOpcodes.add(Opcodes.INVOKEDYNAMIC);
	}

	public void visitJumpInsn(final int opcode, final Label label) {
		insnOpcodes.add(opcode);
	}

	public void visitLdcInsn(final Object cst) {
		insnOpcodes.add(Opcodes.LDC);
	}

	public void visitIincInsn(final int var, final int increment) {
		insnOpcodes.add(Opcodes.IINC);
	}

	public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
		insnOpcodes.add(Opcodes.TABLESWITCH);
	}

	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		insnOpcodes.add(Opcodes.LOOKUPSWITCH);
	}

	public void visitMultiANewArrayInsn(final String desc, final int dims) {
		insnOpcodes.add(Opcodes.ANEWARRAY);
	}

	private static class NewInsnInfo {
		String type;
		boolean followedByADupInsn;
	}
}
