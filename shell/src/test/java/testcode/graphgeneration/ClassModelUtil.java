package testcode.graphgeneration;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import testcode.graphgeneration.model.Field;
import testcode.graphgeneration.model.Method;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;

public class ClassModelUtil {
	public static final boolean IS_CONSTRUCTOR_USE_DEFAULT_VALUES = true;
	public static final int MAXIMUM_ARRAY_SIZE = 10;
	
	public static String getFieldNameFor(GraphNode node) {
		if (!GraphNodeUtil.isField(node)) {
			return null;
		}
		
		return GraphNodeUtil.getDeclaredClass(node).replace("[]", "Array") + "_" + node.getIndex();
	}
	
	public static String getMethodNameFor(GraphNode node) {
		if (!GraphNodeUtil.isMethod(node)) {
			return null;
		}
		
		return "method" + node.getIndex();
	}
	
	public static String getGetterNameFor(GraphNode node) {		
		return "getNode" + node.getIndex();
	}
	
	public static String getSetterNameFor(GraphNode node) {
		return "setNode" + node.getIndex();
	}
	
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

	public static boolean isObject(String dataType) {
		return dataType.startsWith("Class");
	}
	
	public static NumberLiteral randomByteExpression(AST ast) {
		return ast.newNumberLiteral(Byte.toString((byte) (OCGGenerator.RANDOM.nextInt(127 - (-128)) - 128)));
	}
	
	public static NumberLiteral randomShortExpression(AST ast) {
		return ast.newNumberLiteral(Short.toString((short) (OCGGenerator.RANDOM.nextInt(32767 - (-32768)) - 32767)));
	}
	
	public static NumberLiteral randomIntExpression(AST ast) {
		return ast.newNumberLiteral(Integer.toString(OCGGenerator.RANDOM.nextInt()));
	}
	
	public static NumberLiteral randomLongExpression(AST ast) {
		return ast.newNumberLiteral(Long.toString(OCGGenerator.RANDOM.nextLong()));
	}
	
	public static NumberLiteral randomFloatExpression(AST ast) {
		return ast.newNumberLiteral(Float.toString(OCGGenerator.RANDOM.nextFloat()));
	}
	
	public static NumberLiteral randomDoubleExpression(AST ast) {
		return ast.newNumberLiteral(Double.toString(OCGGenerator.RANDOM.nextDouble()));
	}
	
	public static CharacterLiteral randomCharExpression(AST ast) {
		CharacterLiteral characterLiteral = ast.newCharacterLiteral();
		characterLiteral.setCharValue((char) OCGGenerator.RANDOM.nextInt(65535));
		return characterLiteral;
	}
	
	public static BooleanLiteral randomBooleanExpression(AST ast) {
		return ast.newBooleanLiteral(OCGGenerator.RANDOM.nextBoolean());
	}
	
	public static String extractBaseTypeFromArray(String dataType) {
		return extractBaseTypeFrom(dataType);
	}
	
	public static Expression randomPrimitiveExpression(AST ast, String dataType) {
		if (!isPrimitive(dataType)) {
			return null;
		}
		
		switch (dataType) {
			case "byte":
				return ClassModelUtil.randomByteExpression(ast);
			case "short":
				return ClassModelUtil.randomShortExpression(ast);
			case "int":
				return ClassModelUtil.randomIntExpression(ast);
			case "long":
				return ClassModelUtil.randomLongExpression(ast);
			case "float":
				return ClassModelUtil.randomFloatExpression(ast);
			case "double":
				return ClassModelUtil.randomDoubleExpression(ast);
			case "char":
				return ClassModelUtil.randomCharExpression(ast);
			case "boolean":
				return ClassModelUtil.randomBooleanExpression(ast);
			default:
				throw new IllegalArgumentException("Found a primitive type that was unrecognised (" + dataType + ")");
		}
	}
	
	public static Expression defaultPrimitiveExpression(AST ast, String dataType) {
		if (!isPrimitive(dataType)) {
			return null;
		}
		
		switch (dataType) {
			case "byte":
				return ast.newNumberLiteral(Byte.toString((byte) 0));
			case "short":
				return ast.newNumberLiteral(Short.toString((short) 0));
			case "int":
				return ast.newNumberLiteral(Integer.toString(0));
			case "long":
				return ast.newNumberLiteral(Long.toString(0L));
			case "float":
				return ast.newNumberLiteral(Float.toString(0.0f));
			case "double":
				return ast.newNumberLiteral(Double.toString((double) 0.0));
			case "char":
				CharacterLiteral characterLiteral = ast.newCharacterLiteral();
				characterLiteral.setCharValue(Character.MIN_VALUE);
				return characterLiteral;
			case "boolean":
				return ast.newBooleanLiteral(false);
			default:
				throw new IllegalArgumentException("Found a primitive type that was unrecognised (" + dataType + ")");
		}
	}
	
	/**
	 * Generates an ArrayCreation instance that initialises an array with the given length
	 * Elements of the array are randomised instances if primitive, or new object instances if non-primitive.
	 * @param ast The AST to use for code generation.
	 * @param dataType The array datatype (including square braces)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayCreation randomArrayCreation(AST ast, String dataType, int arrayLength) {
		ArrayCreation arrayCreation = ast.newArrayCreation();
		arrayCreation.setType(ClassModelUtil.stringToArrayType(ast, dataType));
		
		String baseType = ClassModelUtil.extractBaseTypeFromArray(dataType);
		boolean isBaseTypePrimitive = ClassModelUtil.isPrimitive(baseType);
		ArrayInitializer arrayInitializer = ast.newArrayInitializer();
		for (int i = 0; i < arrayLength; i++) {
			if (isBaseTypePrimitive) {
				arrayInitializer.expressions().add(ClassModelUtil.randomPrimitiveExpression(ast, baseType));
			} else {
				ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
				classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(baseType)));
				arrayInitializer.expressions().add(classInstanceCreation);
			}
		}
		arrayCreation.setInitializer(arrayInitializer);
		return arrayCreation;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayCreation defaultArrayCreation(AST ast, String dataType, int arrayLength) {
		ArrayCreation arrayCreation = ast.newArrayCreation();
		arrayCreation.setType(ClassModelUtil.stringToArrayType(ast, dataType));
		
		String baseType = ClassModelUtil.extractBaseTypeFromArray(dataType);
		boolean isBaseTypePrimitive = ClassModelUtil.isPrimitive(baseType);
		ArrayInitializer arrayInitializer = ast.newArrayInitializer();
		for (int i = 0; i < arrayLength; i++) {
			if (isBaseTypePrimitive) {
				arrayInitializer.expressions().add(ClassModelUtil.defaultPrimitiveExpression(ast, baseType));
			} else {
				ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
				classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(baseType)));
				arrayInitializer.expressions().add(classInstanceCreation);
			}
		}
		arrayCreation.setInitializer(arrayInitializer);
		return arrayCreation;
	}
	
	public static ClassInstanceCreation newClassInstance(AST ast, String dataType) {
		ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
		classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(dataType)));
		return classInstanceCreation;
	}
	
	public static Expression generateGetterExpressionFromPath(AST ast, List<GraphNode> path) {
		Expression previousExpression = ast.newThisExpression();
		for (int i = 0; i < path.size(); i++) {
			GraphNode currentNode = path.get(i);
			boolean isParam = GraphNodeUtil.isParameter(currentNode);
			if (isParam) {
				continue;
			}
			
			boolean isField = GraphNodeUtil.isField(currentNode);
			if (isField) {
				FieldAccess fieldAccess = ast.newFieldAccess();
				try {
					fieldAccess.setExpression(previousExpression);
				} catch (Exception e) {
					System.currentTimeMillis();
				}
				fieldAccess.setName(ast.newSimpleName(ClassModelUtil.getFieldNameFor(currentNode)));
				previousExpression = fieldAccess;
			}
			
			boolean isMethod = GraphNodeUtil.isMethod(currentNode);
			if (isMethod) {
				MethodInvocation methodInvocation = ast.newMethodInvocation();
				methodInvocation.setExpression(previousExpression);
				methodInvocation.setName(ast.newSimpleName("method" + currentNode.getIndex()));
				previousExpression = methodInvocation;
			}
		}
		return previousExpression;
	}
	
	public static Expression generateArrayElementSetterExpressionFromPath(AST ast, List<GraphNode> path) {
		Expression previousExpression = ast.newThisExpression();
		for (int i = 0; i < path.size(); i++) {
			GraphNode currentNode = path.get(i);
			boolean isParam = GraphNodeUtil.isParameter(currentNode);
			if (isParam) {
				continue;
			}
			
			if (i < path.size() - 1) {
				// Non-terminal node
				boolean isField = GraphNodeUtil.isField(currentNode);
				if (isField) {
					// Assume all fields as package private
					// i.e. we can access it directly
					FieldAccess fieldAccess = ast.newFieldAccess();
					fieldAccess.setExpression(previousExpression);
					fieldAccess.setName(ast.newSimpleName(ClassModelUtil.getFieldNameFor(currentNode)));
					previousExpression = fieldAccess;
				}
				
				boolean isMethod = GraphNodeUtil.isMethod(currentNode);
				if (isMethod) {
					MethodInvocation methodInvocation = ast.newMethodInvocation();
					methodInvocation.setExpression(previousExpression);
					methodInvocation.setName(ast.newSimpleName("method" + currentNode.getIndex()));
					previousExpression = methodInvocation;
				}
			} else {
				// Terminal node
				// Last element should always be an array element
				if (!GraphNodeUtil.isArrayElement(currentNode)) {
					throw new IllegalArgumentException("Last node in path for array element setter must be an array element!");
				}
				
				ArrayAccess arrayAccess = ast.newArrayAccess();
				arrayAccess.setArray(previousExpression);
				arrayAccess.setIndex(ast.newNumberLiteral("0"));
				Assignment assignment = ast.newAssignment();
				assignment.setLeftHandSide(arrayAccess);
				assignment.setRightHandSide(ast.newSimpleName("arg0"));
				previousExpression = assignment;
			}
		}
		return previousExpression;
	}
	
	@SuppressWarnings("unchecked")
	public static MethodDeclaration generateConstructorFor(AST ast, testcode.graphgeneration.model.Class clazz) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(true);
		methodDeclaration.setName(ast.newSimpleName(clazz.getName()));
		methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		Block methodBody = ast.newBlock();
		methodDeclaration.setBody(methodBody);
		
		for (Field field : clazz.getFields()) {
			Assignment assignment = ast.newAssignment();
			FieldAccess fieldAccess = ast.newFieldAccess();
			fieldAccess.setExpression(ast.newThisExpression());
			fieldAccess.setName(ast.newSimpleName(field.getName()));
			assignment.setLeftHandSide(fieldAccess);
			if (field.isPrimitive()) {
				if (IS_CONSTRUCTOR_USE_DEFAULT_VALUES) {
					assignment.setRightHandSide(defaultPrimitiveExpression(ast, field.getDataType()));
				} else {
					assignment.setRightHandSide(randomPrimitiveExpression(ast, field.getDataType()));
				}
			} else if (field.isArray()) {
				if (IS_CONSTRUCTOR_USE_DEFAULT_VALUES) {
					assignment.setRightHandSide(
							defaultArrayCreation(
									ast, 
									field.getDataType(), 
									1 + OCGGenerator.RANDOM.nextInt(MAXIMUM_ARRAY_SIZE - 1)
							)
					);
				} else {
					assignment.setRightHandSide(
							randomArrayCreation(
									ast, 
									field.getDataType(), 
									1 + OCGGenerator.RANDOM.nextInt(MAXIMUM_ARRAY_SIZE - 1)
							)
					);
				}
			} else {
				assignment.setRightHandSide(newClassInstance(ast, field.getDataType()));
			}
			
			methodBody.statements().add(ast.newExpressionStatement(assignment));
		}
		
		return methodDeclaration;
	}
}
