package evosuite.shell;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.evosuite.classpath.ResourceList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodHasBranchFilter {
	
	@SuppressWarnings("unchecked")
	public static List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader) throws IOException {
		InputStream is = ResourceList.getInstance(classLoader).getClassAsStream(targetClass.getName());
		List<String> validMethods = new ArrayList<String>();
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;
			for (MethodNode m : l) {
				/* methodName should be the same as declared in evosuite: String methodName = method.getName() + Type.getMethodDescriptor(method); */
				String methodName = m.name + m.desc; 
				if ((m.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
						|| (m.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED
						|| (m.access & Opcodes.ACC_PRIVATE) == 0 /* default */ ) {
					for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); ) {
						AbstractInsnNode instruction = it.next();
						if (instruction instanceof JumpInsnNode) {
							validMethods.add(methodName);
							break;
						}
					}
				} 
			}
		} finally {
			is.close(); 
		}
		return validMethods;
	}
}
