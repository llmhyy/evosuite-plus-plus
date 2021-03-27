package org.evosuite.instrumentation.testability;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.instrumentation.TransformationStatistics;
import org.evosuite.seeding.RuntimeSensitiveVariable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
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
	@SuppressWarnings("unchecked")
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
		boolean changed = false;
		ListIterator<AbstractInsnNode> iterator = mn.instructions.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			AbstractInsnNode node = iterator.next();
			index++;
			
			if(ins.getASMNode().getOpcode() != node.getOpcode()) {
				continue;
			}
			
			if(index-1 != ins.getInstructionId()) {
				continue;
			}
			
			changed = true;
			
			if (node instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode) node;
//				handleMethodCall();
			}
			else if(node instanceof VarInsnNode) {
				
				VarInsnNode recoveredNode = new VarInsnNode(node.getOpcode(), ((VarInsnNode)node).var);
				MethodInsnNode setTailValue = new MethodInsnNode(
				        Opcodes.INVOKESTATIC,
				        Type.getInternalName(RuntimeSensitiveVariable.class),
				        "setTailValue",
				        Type.getMethodDescriptor(Type.VOID_TYPE,
				                                 new Type[] {
				                                         Type.getType(Object.class), 
				        						}), 
				        false);
				
				mn.instructions.insertBefore(node, recoveredNode);
				mn.instructions.insertBefore(node, setTailValue);
			}
			
			
		}
		
		
		return changed;
	}

}
