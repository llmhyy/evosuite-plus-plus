package com.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ListMethods {

	public static final String OPT_NAME = ParameterOptions.LIST_METHODS_OPT;

	public static void execute(String[] targetClasses) throws ClassNotFoundException, IOException {
		for (String className : targetClasses) {
			Class<?> targetClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
			if (targetClass.isInterface()) {
				/* although Evosuite does filter to get only testable classes, listClasses still contains interface 
				 * which leads to error when executing Evosuite, that's why we need to add this additional check here */
				continue;
			}
			System.out.println("Class " + targetClass.getName());
			List<String> testableMethods = listTestableMethods(targetClass);
			StringBuilder sb = new StringBuilder();
			for (String methodName : testableMethods) {
				sb.append(getMethodId(className, methodName)).append("\n");
			}
			FileUtils.writeFile(EvosuiteForMethod.workingDir + File.pathSeparator + EvosuiteForMethod.LIST_METHODS_FILE_NAME,
					sb.toString(), true);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> listTestableMethods(Class<?> targetClass) throws IOException {
		InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getClassAsStream(targetClass.getName());
		List<String> validMethods = new ArrayList<String>();
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;
			for (MethodNode m : l) {
				/* methodName should be the same as declared in evosuite: String methodName = method.getName() + Type.getMethodDescriptor(method); */
				String methodName = m.name + m.desc; 
				boolean isValidMethod = false;
				if ((m.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
						|| (m.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED
						|| (m.access & Opcodes.ACC_PRIVATE) == 0 /* default */ ) {
					for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); ) {
						AbstractInsnNode instruction = it.next();
						if (instruction instanceof JumpInsnNode) {
							validMethods.add(methodName);
							isValidMethod = true;
							break;
						}
					}
				} 
				if (!isValidMethod) {
					System.out.println("ingore method: " + methodName);
				}
			}
		} finally {
			is.close(); 
		}
		return validMethods;
	}
	
	public static String getMethodId(String className, String methodName) {
		return className + "#" + methodName;
	}
}
