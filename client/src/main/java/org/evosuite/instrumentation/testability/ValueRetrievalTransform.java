package org.evosuite.instrumentation.testability;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.seeding.RuntimeSensitiveVariable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueRetrievalTransform {
	private static Logger logger = LoggerFactory.getLogger(ValueRetrievalTransform.class);

	private final ClassNode cn;
	private BytecodeInstruction ins;

	/**
	 * <p>
	 * Constructor for StringTransformation.
	 * </p>
	 * 
	 * @param cn
	 *            a {@link org.objectweb.asm.tree.ClassNode} object.
	 */
	public ValueRetrievalTransform(ClassNode cn, BytecodeInstruction ins) {
		this.cn = cn;
		this.ins = ins;
	}

	/**
	 * <p>
	 * transform
	 * </p>
	 * 
	 * @return a {@link org.objectweb.asm.tree.ClassNode} object.
	 */
	public ClassNode transform() {
		List<MethodNode> methodNodes = cn.methods;
		for (MethodNode mn : methodNodes) {
			
			String methodName = mn.name + mn.desc;
			if(methodName.equals(ins.getMethodName())) {
				if (transformMethod(cn, mn)) {
					mn.maxStack = mn.maxStack + 10;
					System.currentTimeMillis();
				}				
			}
		}

		return cn;
	}

	/**
	 * Replace boolean-returning method calls on String classes
	 * 
	 * @param mn
	 */
	private boolean transformMethod(ClassNode cn, MethodNode mn) {
		logger.info("Current method: " + mn.name);
		if (ins.getASMNode().getOpcode() == Opcodes.LDC) {
			return false;
		}
		
		List<BytecodeInstruction> list = ins.getActualCFG().getAllInstructions();
		AbstractInsnNode[] instructions = mn.instructions.toArray();
		
		int normalizedIndex = 0;
		for(int i=0; i<ins.getInstructionId(); i++) {
			BytecodeInstruction ins0 = list.get(i);
			AbstractInsnNode node0 = instructions[normalizedIndex];
			
			if (ins0.getASMNode().getOpcode() == node0.getOpcode()) {
				normalizedIndex++;
			}
		}
		
		if (normalizedIndex < 0 || normalizedIndex >= instructions.length - 1) {
			return false;
		}
		
		AbstractInsnNode node = instructions[normalizedIndex];
		AbstractInsnNode nextNode = instructions[normalizedIndex+1];
		
		if (ins.getASMNode().getOpcode() != node.getOpcode()) {
			return false;
		}
		
		// 1. Duplicate value
		Type instructionType = getInstructionType(node);
		InsnNode dupNode;
		if (instructionType == Type.DOUBLE_TYPE || instructionType == Type.LONG_TYPE) {
			dupNode = new InsnNode(Opcodes.DUP2);
		} else {
			dupNode = new InsnNode(Opcodes.DUP);
		}
		mn.instructions.insertBefore(nextNode, dupNode);
		
		// 2. Instruction is primitive typed - cast value to its Object type
		if (instructionType != Type.VOID_TYPE) {
			Class<?> typeObjectClass = getTypeObjectClass(instructionType);
			
			MethodInsnNode valueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(typeObjectClass),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(typeObjectClass),
			                                 new Type[] {
			                                         instructionType, 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, valueOf);
		}
		
		// 3. Insert RuntimeSensitiveVariable.setTailValue()
		MethodInsnNode setTailValue = new MethodInsnNode(
		        Opcodes.INVOKESTATIC,
		        Type.getInternalName(RuntimeSensitiveVariable.class),
		        "setTailValue",
		        Type.getMethodDescriptor(Type.VOID_TYPE,
		                                 new Type[] {
		                                         Type.getType(Object.class), 
		        						}), 
		        false);
		mn.instructions.insertBefore(nextNode, setTailValue);
		
		AbstractInsnNode[] newInstructions = mn.instructions.toArray();		
		return true;
	}
	
	private Class<?> getTypeObjectClass(Type instructionType) {
		if (instructionType == Type.BOOLEAN_TYPE) {
			return Boolean.class;
		}
		
		if (instructionType == Type.BYTE_TYPE) {
			return Byte.class;
		}
		
		if (instructionType == Type.CHAR_TYPE) {
			return Character.class;
		}
		
		if (instructionType == Type.DOUBLE_TYPE) {
			return Double.class;
		}
		
		if (instructionType == Type.FLOAT_TYPE) {
			return Float.class;
		}
		
		if (instructionType == Type.INT_TYPE) {
			return Integer.class;
		}
		
		if (instructionType == Type.LONG_TYPE) {
			return Long.class;
		}
		
		if (instructionType == Type.SHORT_TYPE) {
			return Short.class;
		}
		
		return Void.class;
	}

	private Type getInstructionType(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			return Type.getReturnType(min.desc);
		}
		
		if (node instanceof FieldInsnNode) {
			return Type.VOID_TYPE;
		}
		
		int nodeOpcode = node.getOpcode();
		// Integer
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.IADD, Opcodes.IALOAD, Opcodes.IAND, Opcodes.IDIV, 
				Opcodes.ILOAD, Opcodes.IMUL, Opcodes.INEG, Opcodes.IOR,
				Opcodes.IREM, Opcodes.ISHL, Opcodes.ISHR, Opcodes.ISUB,
				Opcodes.IUSHR, Opcodes.IXOR, Opcodes.D2I, Opcodes.F2I,
				Opcodes.L2I, Opcodes.ICONST_0, Opcodes.ICONST_1, Opcodes.ICONST_2,
				Opcodes.ICONST_3, Opcodes.ICONST_4, Opcodes.ICONST_5, Opcodes.ICONST_M1,
				Opcodes.INSTANCEOF}));
		if (opcodes.contains(nodeOpcode)) {
			return Type.INT_TYPE;
		}
		
		// Long
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.LADD, Opcodes.LALOAD, Opcodes.LAND, Opcodes.LDIV, 
				Opcodes.LLOAD, Opcodes.LMUL, Opcodes.LNEG, Opcodes.LOR,
				Opcodes.LREM, Opcodes.LSHL, Opcodes.LSHR, Opcodes.LSUB,
				Opcodes.LUSHR, Opcodes.LXOR, Opcodes.D2L, Opcodes.F2L,
				Opcodes.I2L, Opcodes.LCONST_0, Opcodes.LCONST_1}));
		if (opcodes.contains(nodeOpcode)) {
			return Type.LONG_TYPE;
		}
		
		// Double
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.DADD, Opcodes.DALOAD, Opcodes.DDIV, Opcodes.DLOAD, 
				Opcodes.DMUL, Opcodes.DNEG, Opcodes.DREM, Opcodes.DSUB,
				Opcodes.F2D, Opcodes.I2D, Opcodes.L2D, Opcodes.DCONST_0,
				Opcodes.DCONST_1}));
		if (opcodes.contains(nodeOpcode)) {
			return Type.DOUBLE_TYPE;
		}
		
		// Float
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.FADD, Opcodes.FALOAD, Opcodes.FDIV, Opcodes.FLOAD, 
				Opcodes.FMUL, Opcodes.FNEG, Opcodes.FREM, Opcodes.FSUB,
				Opcodes.I2F, Opcodes.D2F, Opcodes.L2F, Opcodes.FCONST_0,
				Opcodes.FCONST_1, Opcodes.FCONST_2}));
		if (opcodes.contains(nodeOpcode)) {
			return Type.FLOAT_TYPE;
		}
		
		// Char
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.CALOAD, Opcodes.I2C}));
		if (opcodes.contains(nodeOpcode)) {
			return Type.CHAR_TYPE;
		}
		
		// Byte
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.BIPUSH, Opcodes.BALOAD, Opcodes.I2B}));
		if (opcodes.contains(nodeOpcode)) {
			return Type.BYTE_TYPE;
		}
		
		// Short
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.SIPUSH, Opcodes.SALOAD, Opcodes.I2S}));		
		if (opcodes.contains(nodeOpcode)) {
			return Type.SHORT_TYPE;
		}
		
		// Boolean
		opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.INSTANCEOF}));		
		if (opcodes.contains(nodeOpcode)) {
			return Type.BOOLEAN_TYPE;
		}
		
		return Type.VOID_TYPE;
	}
}
