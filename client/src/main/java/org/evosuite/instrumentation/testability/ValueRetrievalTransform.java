package org.evosuite.instrumentation.testability;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.instrumentation.TransformationStatistics;
import org.evosuite.seeding.RuntimeSensitiveVariable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;
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
	@SuppressWarnings("unchecked")
	private boolean transformMethod(ClassNode cn, MethodNode mn) {
		logger.info("Current method: " + mn.name);
		ListIterator<AbstractInsnNode> iterator = mn.instructions.iterator();
		int normalizedIndex = ins.getInstructionId();
		
		List<BytecodeInstruction> list = ins.getActualCFG().getAllInstructions();
		for(int i=0; i<ins.getInstructionId(); i++) {
			BytecodeInstruction ins0 = list.get(i);
			if(ins0.getASMNode().getOpcode() == Opcodes.PUTSTATIC) {
				if(ins0.getASMNodeString().contains("EnterMethodCallSite")) {
					normalizedIndex = normalizedIndex - 2;
				}
			}
			if(ins0.getASMNodeString().contains("MockFramework")) {
				normalizedIndex = normalizedIndex - 4;
			}
		}
		
//		normalizedIndex = 39; // aaload
//		normalizedIndex = 29; // invokestatic
		
		AbstractInsnNode[] instructions = mn.instructions.toArray();

		if (normalizedIndex < 0 || normalizedIndex >= instructions.length - 1) {
			return false;
		}
		
		AbstractInsnNode node = instructions[normalizedIndex];
		AbstractInsnNode nextNode = instructions[normalizedIndex+1];
		
		if (ins.getASMNode().getOpcode() != node.getOpcode()) {
			return false;
		}
		
		// 1. Duplicate value
		InsnNode originalNode = new InsnNode(Opcodes.DUP);
		mn.instructions.insertBefore(nextNode, originalNode);
		
		// 2. Cast primitive type values to its object type
		if(isPrimitiveInt(node)) {					
			// Integer.valueOf()
			MethodInsnNode integerValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Integer.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Integer.class),
			                                 new Type[] {
			                                         Type.getType(int.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, integerValueOf);
		}
		else if(isPrimitiveLong(node)) {
			// Long.valueOf()
			MethodInsnNode longValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Long.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Long.class),
			                                 new Type[] {
			                                         Type.getType(long.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, longValueOf);
		}
		else if(isPrimitiveDouble(node)) {
			// Double.valueOf()
			MethodInsnNode doubleValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Double.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Double.class),
			                                 new Type[] {
			                                         Type.getType(double.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, doubleValueOf);
		}
		else if(isPrimitiveFloat(node)) {
			// Float.valueOf()
			MethodInsnNode floatValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Float.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Float.class),
			                                 new Type[] {
			                                         Type.getType(float.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, floatValueOf);
		}
		else if(isPrimitiveByte(node)) {			
			// Byte.valueOf()
			MethodInsnNode byteValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Byte.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Byte.class),
			                                 new Type[] {
			                                         Type.getType(byte.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, byteValueOf);
		}
		else if(isPrimitiveShort(node)) {			
			// Short.valueOf()
			MethodInsnNode shortValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Short.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Short.class),
			                                 new Type[] {
			                                         Type.getType(short.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, shortValueOf);
		}
		else if(isPrimitiveChar(node)) {			
			// Char.valueOf()
			MethodInsnNode charValueOf = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(Character.class),
			        "valueOf",
			        Type.getMethodDescriptor(Type.getType(Character.class),
			                                 new Type[] {
			                                         Type.getType(char.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, charValueOf);
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
		System.currentTimeMillis();

		return true;
	}

	private boolean isArrayLoad(AbstractInsnNode originalNode) {
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.IALOAD, Opcodes.LALOAD, Opcodes.FALOAD, Opcodes.DALOAD, 
				Opcodes.AALOAD, Opcodes.BALOAD, Opcodes.CALOAD, Opcodes.SALOAD}));
		return opcodes.contains(originalNode.getOpcode());
	}
	
	private boolean isPrimitiveInt(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.INT_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.IADD, Opcodes.IALOAD, Opcodes.IAND, Opcodes.IDIV, 
				Opcodes.ILOAD, Opcodes.IMUL, Opcodes.INEG, Opcodes.IOR,
				Opcodes.IREM, Opcodes.ISHL, Opcodes.ISHR, Opcodes.ISUB,
				Opcodes.IUSHR, Opcodes.IXOR, Opcodes.D2I, Opcodes.F2I,
				Opcodes.L2I}));
		return opcodes.contains(node.getOpcode());
	}
	
	private boolean isPrimitiveLong(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.LONG_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.LADD, Opcodes.LALOAD, Opcodes.LAND, Opcodes.LDIV, 
				Opcodes.LLOAD, Opcodes.LMUL, Opcodes.LNEG, Opcodes.LOR,
				Opcodes.LREM, Opcodes.LSHL, Opcodes.LSHR, Opcodes.LSUB,
				Opcodes.LUSHR, Opcodes.LXOR, Opcodes.D2L, Opcodes.F2L,
				Opcodes.I2L}));
		return opcodes.contains(node.getOpcode());
	}
	
	private boolean isPrimitiveDouble(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.DOUBLE_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.DADD, Opcodes.DALOAD, Opcodes.DDIV, Opcodes.DLOAD, 
				Opcodes.DMUL, Opcodes.DNEG, Opcodes.DREM, Opcodes.DSUB,
				Opcodes.F2D, Opcodes.I2D, Opcodes.L2D}));
		return opcodes.contains(node.getOpcode());
	}
	
	private boolean isPrimitiveFloat(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.FLOAT_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.FADD, Opcodes.FALOAD, Opcodes.FDIV, Opcodes.FLOAD, 
				Opcodes.FMUL, Opcodes.FNEG, Opcodes.FREM, Opcodes.FSUB,
				Opcodes.I2F, Opcodes.D2F, Opcodes.L2F}));
		return opcodes.contains(node.getOpcode());
	}
	
	private boolean isPrimitiveChar(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.CHAR_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.CALOAD, Opcodes.I2C}));
		return opcodes.contains(node.getOpcode());
	}
	
	private boolean isPrimitiveByte(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.BYTE_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.BIPUSH, Opcodes.BALOAD, Opcodes.I2B}));
		return opcodes.contains(node.getOpcode());
	}
	
	private boolean isPrimitiveShort(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			Type t = Type.getReturnType(min.desc);
			return t.equals(Type.SHORT_TYPE);
		}
		
		Set<Integer> opcodes = new HashSet<Integer>();
		opcodes.addAll(Arrays.asList(new Integer[] {
				Opcodes.SIPUSH, Opcodes.SALOAD, Opcodes.I2S}));		
		return opcodes.contains(node.getOpcode());
	}

}
