package org.evosuite.instrumentation.testability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.seeding.RuntimeSensitiveVariable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueRetrievalTransform {
	private static Logger logger = LoggerFactory.getLogger(ValueRetrievalTransform.class);

	private final ClassNode cn;
	private List<BytecodeInstruction> insList;

	/**
	 * <p>
	 * Constructor for StringTransformation.
	 * </p>
	 * 
	 * @param cn
	 *            a {@link org.objectweb.asm.tree.ClassNode} object.
	 */
	public ValueRetrievalTransform(ClassNode cn, List<BytecodeInstruction> insList) {
		this.cn = cn;
		this.insList = insList;
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
			
			List<BytecodeInstruction> list = countInstruction(this.insList, methodName);
			int size = list.size();
			if(size != 0) {
				if (transformMethod(cn, mn)) {
					mn.maxStack = mn.maxStack + 10 * size;
					System.currentTimeMillis();
				}	
			}
			
		}

		return cn;
	}

	private List<BytecodeInstruction> countInstruction(List<BytecodeInstruction> insList2, String methodName) {
		List<BytecodeInstruction> l = new ArrayList<>();
		for(BytecodeInstruction ins: insList2) {
			if(ins.getMethodName().equals(methodName)) {
				l.add(ins);
			}
		}
		return l;
	}

	/**
	 * Replace boolean-returning method calls on String classes
	 * 
	 * @param mn
	 */
	private boolean transformMethod(ClassNode cn, MethodNode mn) {
		logger.info("Current method: " + mn.name);
		
		String methodName = mn.name + mn.desc;
		List<BytecodeInstruction> observationList = countInstruction(this.insList, methodName);
		observationList.sort(new Comparator<BytecodeInstruction>() {
			@Override
			public int compare(BytecodeInstruction o1, BytecodeInstruction o2) {
				return o1.getInstructionId() - o2.getInstructionId();
			}
		});
		
		
		System.currentTimeMillis();
		
		List<BytecodeInstruction> allInstructions = observationList.get(0).getActualCFG().getAllInstructions();
		AbstractInsnNode[] instructions = mn.instructions.toArray();
		
		for(BytecodeInstruction observation: observationList) {
//			if (ins.getASMNode().getOpcode() == Opcodes.LDC) {
//				return false;
//			}
			
			int normalizedIndex = 0;
			
			for(int i=0; i<observation.getInstructionId(); i++) {
				BytecodeInstruction ins = allInstructions.get(i);
				AbstractInsnNode normalizedNode = instructions[normalizedIndex];
				if (ins.getASMNode().getOpcode() == normalizedNode.getOpcode()) {
					normalizedIndex++;
				}
				else {
					System.currentTimeMillis();
					System.currentTimeMillis();
				}
			}
			
			if (normalizedIndex < 0 || normalizedIndex >= instructions.length - 1) {
				continue;
			}
			
			if (instructions[normalizedIndex].getOpcode() == Opcodes.INSTANCEOF) {
				normalizedIndex -= 1;
			}
			
			AbstractInsnNode node = instructions[normalizedIndex];
			AbstractInsnNode nextNode = instructions[normalizedIndex+1];
			
			if (observation.getASMNode().getOpcode() != node.getOpcode()) {
				continue;
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
			if (isPrimitive(instructionType)) {
				Class<?> typeObjectClass = getTypeObjectClass(instructionType);
				System.currentTimeMillis();
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
			
//			System.currentTimeMillis();
			
			// 3. Insert RuntimeSensitiveVariable.setTailValue()
			
			LdcInsnNode ldcNode = new LdcInsnNode(observation.toString());
			mn.instructions.insertBefore(nextNode, ldcNode);
			MethodInsnNode setObservation = new MethodInsnNode(
			        Opcodes.INVOKESTATIC,
			        Type.getInternalName(RuntimeSensitiveVariable.class),
			        "setObservation",
			        Type.getMethodDescriptor(Type.VOID_TYPE,
			                                 new Type[] {
			                                		 Type.getType(Object.class),
			                                		 Type.getType(String.class), 
			        						}), 
			        false);
			mn.instructions.insertBefore(nextNode, setObservation);
			
//			break;
		}
		
		AbstractInsnNode[] newInstructions = mn.instructions.toArray();		
		return true;
	}
	
	private boolean isPrimitive(Type instructionType) {
		if(instructionType.equals(Type.INT_TYPE) ||
				instructionType.equals(Type.DOUBLE_TYPE) ||
				instructionType.equals(Type.LONG_TYPE) ||
				instructionType.equals(Type.BYTE_TYPE) ||
				instructionType.equals(Type.CHAR_TYPE) ||
				instructionType.equals(Type.SHORT_TYPE) ||
				instructionType.equals(Type.FLOAT_TYPE) ||
				instructionType.equals(Type.BOOLEAN_TYPE)
				) {
			return true;
		}
		
		return false;
	}
	
	
	private boolean isPrimitiveComplexType(Type instructionType) {
		String clazz = instructionType.getClassName();
		
		if(clazz.equals("java.lang.Integer") || 
				clazz.equals("java.lang.Double") ||
				clazz.equals("java.lang.Long") ||
				clazz.equals("java.lang.Byte") ||
				clazz.equals("java.lang.Character") ||
				clazz.equals("java.lang.Short") ||
				clazz.equals("java.lang.Float") ||
				clazz.equals("java.lang.Boolean") 
				) {
			return true;
		}
		
		return false;
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
		try {
			Class<?> clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(instructionType.getClassName());
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Void.class;
	}

	private Type getInstructionType(AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;
			return Type.getReturnType(min.desc);
		}
		
		if (node instanceof FieldInsnNode) {
			
			if(node.getOpcode() == Opcodes.GETFIELD || node.getOpcode() == Opcodes.GETSTATIC) {
				FieldInsnNode fNode = (FieldInsnNode)node;
				return Type.getType(fNode.desc);
			}
			else {
				return Type.VOID_TYPE;				
			}
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
				Opcodes.INSTANCEOF,Opcodes.ARRAYLENGTH}));
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
				Opcodes.INSTANCEOF,
				Opcodes.DCMPG,
				Opcodes.DCMPL,}));		
		if (opcodes.contains(nodeOpcode)) {
			return Type.BOOLEAN_TYPE;
		}
		
		return Type.VOID_TYPE;
	}
}
