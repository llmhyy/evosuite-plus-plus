package evosuite.shell.listmethod;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class FilterHelper {
	public static boolean isMethodHasAllPrimitiveParameter(MethodNode node) {
		try {
			Type[] argTypes = Type.getArgumentTypes(node.desc);
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
}
