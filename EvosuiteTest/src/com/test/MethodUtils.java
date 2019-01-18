package com.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodUtils {
	private static Logger log = LoggerUtils.getLogger(MethodUtils.class);
	private MethodUtils() {}
	
	public List<String> getInvokedMethods(String targetClass, String methodName) {
		InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getClassAsStream(targetClass);
		List<String> validMethods = new ArrayList<String>();
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> methods = cn.methods;
			for (MethodNode method : methods) {
				if (methodName.equals(CommonUtility.getMethodName(method))) {
					for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
						AbstractInsnNode insnNode = it.next();
						if (CommonUtility.isInvokeMethodInsn(insnNode)) {
							if (insnNode instanceof MethodInsnNode) {
								MethodInsnNode invokedNode = (MethodInsnNode) insnNode;
								validMethods.add(CommonUtility.getMethodId(invokedNode.owner, invokedNode.name + invokedNode.desc));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("error", e);
		}
		return validMethods;
	}
}
