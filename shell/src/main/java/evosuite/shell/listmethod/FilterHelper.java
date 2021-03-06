package evosuite.shell.listmethod;

import java.lang.reflect.Modifier;

import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class FilterHelper {
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

	public static boolean methodHasAtLeastOnePrimitiveParameter(String desc) {
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

			Type[] argTypes = Type.getArgumentTypes(mn.desc);

			for (Type type : argTypes) {

				if (FilterHelper.considerAsPrimitiveType(type)) {
					continue;
				}
				
				if (type.getClassName().contains("[]")) {
					String elementClass = type.getClassName().substring(0, type.getClassName().length() - 2);
					if (!RuntimeInstrumentation.checkIfCanInstrument(elementClass)) {
						return true;
					}
				}
				
				if (!RuntimeInstrumentation.checkIfCanInstrument(type.getClassName())) {
					return true;
				}
			}
			
		return false;
	}
	
	public static boolean parameterIsInterfaceOrAbstract(String desc, ClassLoader classLoader) {
		Type[] argTypes = Type.getArgumentTypes(desc);
		Class<?> clazz = null;
		if (argTypes.length != 0) {
			for (Type type : argTypes) {
				if (!FilterHelper.considerAsPrimitiveType(type)) {
					try {
						if (type.getSort() == Type.ARRAY) {
							clazz = classLoader.loadClass((type.getElementType().getClassName()));
						} else {
							clazz = classLoader.loadClass((type.getClassName()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean containNonPrimitiveParameter(String desc) {
		try {
			Type[] argTypes = Type.getArgumentTypes(desc);

			for (Type type : argTypes) {
				if (!FilterHelper.considerAsPrimitiveType(type)) {
					return true;
				}
			}
			return false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean containStringArrayParameter(String desc) {
		try {
			Type[] argsTypes = Type.getArgumentTypes(desc);

			for (Type type : argsTypes) {
				if (type.getSort() == Type.ARRAY) {
					if (type.getElementType().getClassName().equals("java.lang.String")) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
