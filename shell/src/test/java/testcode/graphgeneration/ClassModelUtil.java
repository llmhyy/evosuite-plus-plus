package testcode.graphgeneration;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import testcode.graphgeneration.model.Field;
import testcode.graphgeneration.model.Method;

import org.eclipse.jdt.core.dom.AST;

public class ClassModelUtil {
	public static boolean isPrimitive(String dataType) {
		return dataType.equals("void") || Arrays.asList(Graph.PRIMITIVE_TYPES).contains(dataType);
	}
	
	public static boolean isArray(String dataType) {
		return dataType.endsWith("[]");
	}
	
	public static Type getCopyOfType(AST ast, Type type) {
		boolean isPrimitive = (type instanceof PrimitiveType);
		boolean isSimple = (type instanceof SimpleType);
		boolean isArray = (type instanceof ArrayType);
		
		if (isPrimitive) {
			PrimitiveType primitiveType = (PrimitiveType) type;
			return ast.newPrimitiveType(primitiveType.getPrimitiveTypeCode());
		}
		
		if (isSimple) { 
			SimpleType simpleType = (SimpleType) type;
			return ast.newSimpleType(simpleType.getName());
		}
		
		if (isArray) {
			ArrayType arrayType = (ArrayType) type;
			return ast.newArrayType(getCopyOfType(ast, arrayType.getComponentType()), arrayType.getDimensions());
		}
		
		// Unsupported otherwise
		return null;
	}
	
	public static Type extractReturnTypeFrom(AST ast, Method method) {
		String methodReturnTypeAsString = method.getReturnType();
		boolean isPrimitive = isPrimitive(methodReturnTypeAsString);
		boolean isArray = isArray(methodReturnTypeAsString);
		
		Type methodReturnType = null;
		if (isPrimitive) {
			methodReturnType = ClassModelUtil.stringToPrimitiveType(ast, methodReturnTypeAsString);
		} else if (isArray) {
			methodReturnType = ClassModelUtil.stringToArrayType(ast, methodReturnTypeAsString);
		} else {
			methodReturnType = ClassModelUtil.stringToSimpleType(ast, methodReturnTypeAsString);
		}
		return methodReturnType;
	}
	
	public static Modifier extractModifierFrom(AST ast, Method method) {
		if (method.getVisibility() == Visibility.DEFAULT) {
			return null;
		}
		Modifier.ModifierKeyword modifierKeyword = visibilityToModifierKeyword(method.getVisibility());
		Modifier modifier = ast.newModifier(modifierKeyword);
		return modifier;
	}
	
	public static Type extractTypeFrom(AST ast, Field field) {
		Type fieldType = null;
		if (field.isPrimitive()) {
			fieldType = ClassModelUtil.stringToPrimitiveType(ast, field.getDataType());
		} else if (field.isArray()) {
			fieldType = ClassModelUtil.stringToArrayType(ast, field.getDataType());
		} else {
			fieldType = ClassModelUtil.stringToSimpleType(ast, field.getDataType());
		}
		return fieldType;
	}
	
	public static PrimitiveType.Code stringToPrimitiveTypeCode(String dataType) {
		if (!isPrimitive(dataType)) {
			return null;
		}
		
		switch (dataType) {
			case "boolean":
				return PrimitiveType.BOOLEAN;
			case "byte":
				return PrimitiveType.BYTE;
			case "char":
				return PrimitiveType.CHAR;
			case "short":
				return PrimitiveType.SHORT;
			case "float":
				return PrimitiveType.FLOAT;
			case "double":
				return PrimitiveType.DOUBLE;
			case "int":
				return PrimitiveType.INT;
			case "long":
				return PrimitiveType.LONG;
			case "void":
				return PrimitiveType.VOID;
			default:
				return null;
		}
	}
	
	public static PrimitiveType stringToPrimitiveType(AST ast, String dataType) {
		PrimitiveType.Code primitiveTypeCode = stringToPrimitiveTypeCode(dataType);
		if (primitiveTypeCode == null) {
			System.err.println("Failed to get an appropriate PrimitiveType.Code for " + dataType);
			return null;
		}
		return ast.newPrimitiveType(primitiveTypeCode);
	}
	
	private static int findNumberOfOccurrencesInString(String occurrence, String string) {
		Pattern pattern = Pattern.compile(Pattern.quote(occurrence));
		Matcher matcher = pattern.matcher(string);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}
	
	private static String extractBaseTypeFrom(String dataType) {
		int firstIndexOfLeftSquareBracket = dataType.indexOf("[");
		if (firstIndexOfLeftSquareBracket < 0) {
			return dataType;
		}
		return dataType.substring(0, firstIndexOfLeftSquareBracket);
	}
	
	public static ArrayType stringToArrayType(AST ast, String dataType) {
		int dimensions = findNumberOfOccurrencesInString("[]", dataType);
		String baseTypeAsString = extractBaseTypeFrom(dataType);
		boolean isPrimitive = isPrimitive(baseTypeAsString);
		Type baseType = null;
		if (isPrimitive) {
			baseType = stringToPrimitiveType(ast, baseTypeAsString);
		} else {
			baseType = stringToSimpleType(ast, baseTypeAsString);
		}
		
		return ast.newArrayType(baseType, dimensions);
	}
	
	public static SimpleType stringToSimpleType(AST ast, String dataType) {
		return ast.newSimpleType(ast.newSimpleName(dataType));
	}
	
	public static Modifier.ModifierKeyword visibilityToModifierKeyword(Visibility visibility) {
		switch	(visibility) {
			case PUBLIC:
				return Modifier.ModifierKeyword.PUBLIC_KEYWORD;
			case PRIVATE:
				return Modifier.ModifierKeyword.PRIVATE_KEYWORD;
			case PROTECTED:
				return Modifier.ModifierKeyword.PROTECTED_KEYWORD;
			case DEFAULT:
				return null;
			default:
				return null;
		}
	}
}
