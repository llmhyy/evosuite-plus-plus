package evosuite.shell.listmethod;


import org.apache.poi.ss.formula.functions.T;
import org.evosuite.classpath.ResourceList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MethodPrimitiveFilter extends MethodHasBranchFilter implements IMethodFilter {

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader) throws IOException {
		InputStream is = ResourceList.getInstance(classLoader).getClassAsStream(targetClass.getName());
		List<String> validMethods = new ArrayList<>();
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;

			for (MethodNode m : l) {
				boolean isPrimParam = false;
				boolean isPrimField = false;
				boolean hasBranch = false;
				String methodName = m.name + m.desc;
				if ((m.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
						|| (m.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED
						|| (m.access & Opcodes.ACC_PRIVATE) == 0) {

					// The parameters of the method are all primitives
                    Type[] typeArgs = Type.getArgumentTypes(m.desc);
                    for (Type type : typeArgs) {
						System.out.println(type);
                        if (!(type.equals(Type.BYTE_TYPE) || type.equals(Type.CHAR_TYPE) || type.equals(Type.SHORT_TYPE) ||
								type.equals(Type.INT_TYPE) || type.equals(Type.LONG_TYPE) || type.equals(Type.FLOAT_TYPE) ||
								type.equals(Type.DOUBLE_TYPE) || type.equals(Type.BOOLEAN_TYPE)) || type.toString().equals("Ljava/lang/String")) {
                            break;
                        }
                        isPrimParam = true;
                    }

					// The method has branches
					for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); ) {
						AbstractInsnNode instruction = it.next();
						if (instruction instanceof JumpInsnNode) {
							hasBranch = true;
						}
						if (instruction.getOpcode() == Opcodes.GETFIELD) {
							Type type = Type.getType(((FieldInsnNode) instruction).desc);
							System.out.println(type);
							if (type.equals(Type.BYTE_TYPE) || type.equals(Type.CHAR_TYPE) || type.equals(Type.SHORT_TYPE) ||
									type.equals(Type.INT_TYPE) || type.equals(Type.LONG_TYPE) || type.equals(Type.FLOAT_TYPE) ||
									type.equals(Type.DOUBLE_TYPE) || type.equals(Type.BOOLEAN_TYPE) || type.toString().equals("Ljava/lang/String")) {
//								System.out.println(((FieldInsnNode) instruction).desc);
								isPrimField = true;
							}
						}

						// Global fields are all primitives
					}
				}
				if (isPrimParam && isPrimField && hasBranch) {
						validMethods.add(methodName);
					}
				}
			} finally {
				is.close();
			}
			return validMethods;
		}
	}
