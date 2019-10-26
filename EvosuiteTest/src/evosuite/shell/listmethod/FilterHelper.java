package evosuite.shell.listmethod;

import java.io.IOException;
import java.io.InputStream;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import evosuite.shell.listmethod.PrimitiveBasedFlagMethodFilter.Remarks;

public class FilterHelper {
	public static boolean isMethodAtLeastPrimitiveParameter(MethodNode node) {
		try {
			Type[] argTypes = Type.getArgumentTypes(node.desc);

			if(argTypes.length==0) {
				return false;
			}

			for (Type type : argTypes) {
				if (considerAsPrimitiveType(type) && type!=Type.BOOLEAN_TYPE) {
					return true;
				}
			}

			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean isMethodAtLeastStringParameter(MethodNode node) {
		try {
			Type[] argTypes = Type.getArgumentTypes(node.desc);

			if(argTypes.length==0) {
				return false;
			}

			for (Type type : argTypes) {
				if (type.getClassName().contains("java.lang.String")) {
					return true;
				}
			}

			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean isAllMethodParameterPrimitive(String desc) {
		try {
			Type[] argTypes = Type.getArgumentTypes(desc);

			if(argTypes.length==0) {
				return false;
			}

			for (Type type : argTypes) {
				if (!considerAsPrimitiveType(type)) {
					return false;
				}
			}

			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean isAtLeastMethodParameterPrimitive(String desc) {
		try {
			Type[] argTypes = Type.getArgumentTypes(desc);

			if(argTypes.length==0) {
				return false;
			}

			for (Type type : argTypes) {
				if (considerAsPrimitiveType(type)) {
					return true;
				}
			}

			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean considerAsPrimitiveType(Type type) {
		switch (type.getSort()) {
		case Type.BOOLEAN:
		case Type.CHAR:
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
		case Type.FLOAT:
		case Type.LONG:
		case Type.DOUBLE:
			return true;
		case Type.ARRAY:
			return considerAsPrimitiveType(type.getElementType());
		case Type.OBJECT:
			String className = type.getClassName();
			if (String.class.getName().equals(className)) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	public static boolean hasAtLeastOnePrimitiveParam(MethodNode mn, ClassNode cn) {
		try {
			Type[] argTypes = Type.getArgumentTypes(mn.desc);

			if(argTypes.length == 0) {
				return false;
			}

			for (Type type : argTypes) {
				if (FilterHelper.considerAsPrimitiveType(type)) {
					return true;
				}
			}
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasUnsupportedParam(MethodNode mn, ClassNode cn, ClassLoader cl) {
		try {
			Type[] argTypes = Type.getArgumentTypes(mn.desc);

			for (Type type : argTypes) {
				if (!RuntimeInstrumentation.checkIfCanInstrument(type.getClassName())) {
					return false;
				}

				if (FilterHelper.considerAsPrimitiveType(type)) {
					continue;
				}

				
				if (type.getClassName().contains("[]")) {
					continue;
				}

				Class<?> targetClass = cl.loadClass(type.getClassName());
				if (targetClass.isInterface()) {
					return true;
				}
				else {
					InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
							.getClassAsStream(targetClass.getName());
					ClassReader reader = new ClassReader(is);
					ClassNode classnode = new ClassNode();
					reader.accept(classnode, ClassReader.SKIP_FRAMES);
					if ((classnode.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
						return true;
					}
				}
			}
		}
		catch(ClassNotFoundException e) {
			return false;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
