package evosuite.shell.listmethod;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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
}
