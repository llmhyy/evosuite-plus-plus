package org.evosuite.utils;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodUtil {
	private static Logger log = LoggerFactory.getLogger(MethodUtil.class);
	
	public static List<String> getInvokedMethods(String targetClass, String methodName) {
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
	
	public static String getSignature(Method m) {
		String sig;
		try {
			Field gSig = Method.class.getDeclaredField("signature");
			gSig.setAccessible(true);
			sig = (String) gSig.get(m);
			if (sig != null)
				return sig;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder("(");
		for (Class<?> c : m.getParameterTypes())
			sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
		String str = sb.append(')')
				.append(m.getReturnType() == void.class ? "V"
						: (sig = Array.newInstance(m.getReturnType(), 0).toString()).substring(1, sig.indexOf('@')))
				.toString();
		return str.replace(".", "/");
	}
}
