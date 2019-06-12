package org.evosuite.instrumentation;

import org.evosuite.PackageInfo;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCallAdapter extends MethodVisitor {

	private int index;
	
	protected MethodCallAdapter(final int api, final MethodVisitor mv) {
        super(api, mv);
    }
	
	public MethodCallAdapter(final MethodVisitor mv) {
        this(Opcodes.ASM6, mv);
    }
	
	public int getIndex() {
		return index;
	}
	
	@Override
    public void visitInsn(final int opcode) {
        index++;
        if (mv != null) {
            mv.visitInsn(opcode);
        }
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
    	index++;
        if (mv != null) {
            mv.visitIntInsn(opcode, operand);
        }
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
    	index++;
        if (mv != null) {
            mv.visitVarInsn(opcode, var);
        }
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
    	index++;
        if (mv != null) {
            mv.visitTypeInsn(opcode, type);
        }
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
            final String name, final String desc) {
    	index++;
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, desc);
        }
    }

    @Deprecated
    @Override
    public void visitMethodInsn(final int opcode, final String owner,
            final String name, final String desc) {
    	index++;
    	if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        doVisitMethodInsn(opcode, owner, name, desc,
                opcode == Opcodes.INVOKEINTERFACE);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
            final String name, final String desc, final boolean itf) {
    	index++;
    	if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        doVisitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void doVisitMethodInsn(int opcode, final String owner,
            final String name, final String desc, final boolean itf) {
        
    	this.visitIntInsn(Opcodes.SIPUSH, this.index);
//    	this.visitLdcInsn(this.index);
    	this.visitFieldInsn(Opcodes.PUTSTATIC, PackageInfo.getNameWithSlash(ExecutionTracer.class), "callSite", "I");
//    	mv.visitFieldInsn(Opcodes.GETSTATIC, PackageInfo.getNameWithSlash(ExecutionTracer.class), "callSite", "I");
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
            Object... bsmArgs) {
    	index++;
        if (mv != null) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
    	index++;
        if (mv != null) {
            mv.visitJumpInsn(opcode, label);
        }
    }

    @Override
    public void visitLdcInsn(final Object cst) {
    	index++;
        if (mv != null) {
            mv.visitLdcInsn(cst);
        }
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
    	index++;
        if (mv != null) {
            mv.visitIincInsn(var, increment);
        }
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
            final Label dflt, final Label... labels) {
    	index++;
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
            final Label[] labels) {
    	index++;
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
    	index++;
        if (mv != null) {
            mv.visitMultiANewArrayInsn(desc, dims);
        }
    }
    
    @Override
    public void visitLineNumber(int line, Label start) {
    	index++;
        if (mv != null) {
            mv.visitLineNumber(line, start);
        }
    }

    @Override
    public void visitLabel(Label label) {
    	index++;
        if (mv != null) {
            mv.visitLabel(label);
        }
    }
}
