package testcode.graphgeneration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import testcode.graphgeneration.model.Field;
import testcode.graphgeneration.model.Method;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;

public class ClassModelUtil {
	// Whether constructor generation uses default or random values to initialise fields.
	public static final boolean IS_CONSTRUCTOR_USE_DEFAULT_VALUES = true;
	// The maximum possible size of arrays for array initialisation.
	public static final int MAXIMUM_ARRAY_SIZE = 10;
	
	/**
	 * Generates a field name for the given {@code GraphNode}. This is used to identify the field that the node represents.
	 * @param node The given node.
	 * @return {@code null} if the given node does not represent a field, a {@code String} representation of a field name otherwise.
	 */
	public static String getFieldNameFor(GraphNode node) {
		return getFieldNameFor(node, false);
	}
	
	/**
	 * Generates a field name for the given {@code GraphNode}. Overloaded version that allows disabling of sanity checks.
	 * @param node The given node.
	 * @param isOverrideSafetyChecks Whether sanity checks should be overriden or not.
	 * @return {@code null} if the given node does not represent a field, a {@code String} representation of a field name otherwise.
	 */
	public static String getFieldNameFor(GraphNode node, boolean isOverrideSafetyChecks) {
		if (!isOverrideSafetyChecks && !GraphNodeUtil.isField(node)) {
			return null;
		}
		
		return GraphNodeUtil.getDeclaredClass(node).replace("[]", "Array") + "_" + node.getIndex();
	}
	
	/**
	 * Generates a method name for the given {@code GraphNode}. This is used to identify the method that the node represents.
	 * @param node The given node.
	 * @return {@code null} if the given node does not represent a method, a {@code String} representation of a method name otherwise.
	 */
	public static String getMethodNameFor(GraphNode node) {
		if (!GraphNodeUtil.isMethod(node)) {
			return null;
		}
		
		return "method" + node.getIndex();
	}
	
	/**
	 * Generates a getter name for the given {@code GraphNode}.
	 * @param node The given node.
	 * @return A {@code String} representation of a getter name.
	 */
	public static String getGetterNameFor(GraphNode node) {		
		return "get" + capitalise(getFieldNameFor(node, true));
	}
	
	/**
	 * Generates a setter name for the given {@code GraphNode}.
	 * @param node The given node.
	 * @return A {@code String} representation of a setter name.
	 */
	public static String getSetterNameFor(GraphNode node) {
		return "set" + capitalise(getFieldNameFor(node, true));
	}
	
	private static String capitalise(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		
		if (string.length() == 1) {
			return string.toUpperCase();
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(string.substring(0,1).toUpperCase());
		builder.append(string.substring(1));
		return builder.toString();
	}
	
	/**
	 * Generates a parameter name for the given {@code GraphNode}.
	 * @param node The given node.
	 * @return A {@code String} representation of a parameter name.
	 */
	public static String getParameterNameFor(GraphNode node) {
		return "arg" + node.getIndex();
	}
	
	/**
	 * @param dataType The {@code String} representation of the given datatype.
	 * @return {@code true} if the given datatype is primitive, {@code false} otherwise.
	 */
	public static boolean isPrimitive(String dataType) {
		return dataType.equals("void") || Arrays.asList(Graph.PRIMITIVE_TYPES).contains(dataType);
	}
	
	/**
	 * @param dataType The {@code String} representation of the given datatype.
	 * @return {@code true} if the given datatype is an array type, {@code false} otherwise.
	 */
	public static boolean isArray(String dataType) {
		return dataType.endsWith("[]");
	}
	
	/**
	 * Generates a copy of the given {@code Type} from the given {@code AST}. Only supports {@code PrimitiveType, SimpleType, ArrayType}.
	 * @param ast The given {@code AST} instance.
	 * @param type The given {@code Type} instance.
	 * @return A copy of the given {@code Type} instance, relative to the given {@code AST}, or {@code null} if the given {@code Type} is unsupported.
	 */
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
	
	/**
	 * Returns a {@code Type} instance representing the return type of the given method.
	 * @param ast The given {@code AST} instance.
	 * @param method The given {@code Method} instance.
	 * @return A {@code Type} instance representing the return type of the given method.
	 */
	public static Type extractReturnTypeFrom(AST ast, Method method) {
		String methodReturnTypeAsString = method.getReturnType();
		return extractReturnTypeFrom(ast, methodReturnTypeAsString);
	}
	
	private static Type extractReturnTypeFrom(AST ast, String methodReturnTypeAsString) {
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
	
	/**
	 * Generates a {@code Modifier} instance representing the access modifier of the given method.
	 * @param ast The given {@code AST} instance.
	 * @param method The given {@code Method} instance.
	 * @return A {@code Modifier} instance representing the access modifier of the given method.
	 */
	public static Modifier extractModifierFrom(AST ast, Method method) {
		if (method.getVisibility() == Visibility.DEFAULT) {
			return null;
		}
		Modifier.ModifierKeyword modifierKeyword = visibilityToModifierKeyword(method.getVisibility());
		Modifier modifier = ast.newModifier(modifierKeyword);
		return modifier;
	}
	
	/**
	 * Returns a {@code Type} instance representing the type of the given field.
	 * @param ast The given {@code AST} instance.
	 * @param field The given {@code Field} instance.
	 * @return A {@code Type} instance representing the type of the given field.
	 */
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
	
	/**
	 * Returns a {@code Type} instance representing the type of the given {@code GraphNode}. This can be the return type of a method, the type of a field, etc.
	 * @param ast The given {@code AST} instance.
	 * @param node The given {@code GraphNode} instance.
	 * @return A {@code Type} instance representing the type of the given {@code GraphNode}.
	 */
	public static Type extractTypeFrom(AST ast, GraphNode node) {
		// This can represent different things depending on the node
		// If the node represents a method, then the return type
		// If the node represents a field, then the field type
		// If the node represents an array element, then the type of the array element
		Type nodeType = null;
		String declaredClass = GraphNodeUtil.getDeclaredClass(node);
		if (GraphNodeUtil.isParameter(node) 
				|| GraphNodeUtil.isField(node) 
				|| GraphNodeUtil.isArrayElement(node)) {
			if (GraphNodeUtil.isPrimitive(node)) {
				nodeType = ClassModelUtil.stringToPrimitiveType(ast, declaredClass);
			} else if (GraphNodeUtil.isArray(node)) {
				nodeType = ClassModelUtil.stringToArrayType(ast, declaredClass);
			} else {
				nodeType = ClassModelUtil.stringToSimpleType(ast, declaredClass);
			}
		} else if (GraphNodeUtil.isMethod(node)) {
			nodeType = ClassModelUtil.extractReturnTypeFrom(ast, declaredClass);
		}
		return nodeType;
	}
	
	/**
	 * Helper method to convert a primitive datatype into a {@code PrimitiveType.Code} instance.
	 * @param dataType The given primitive datatype.
	 * @return {@code null} if the given datatype is non-primitive, and the appropriate {@code PrimitiveType.Code} instance otherwise.
	 */
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
	
	/**
	 * Helper method to convert a primitive datatype into a {@code PrimitiveType} instance.
	 * @param ast The given {@code AST} instance.
	 * @param dataType The given primitive datatype.
	 * @return {@code null} if the given datatype is non-primitive, and the appropriate {@code PrimitiveType} instance otherwise.
	 */
	public static PrimitiveType stringToPrimitiveType(AST ast, String dataType) {
		PrimitiveType.Code primitiveTypeCode = stringToPrimitiveTypeCode(dataType);
		if (primitiveTypeCode == null) {
			System.err.println("[ClassModelUtil#stringToPrimitiveType]: Failed to get an appropriate PrimitiveType.Code for " + dataType);
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
	
	/**
	 * Helper method to convert a string representation of an array type into an {@code ArrayType} instance.
	 * @param ast The given {@code AST} instance.
	 * @param dataType The given datatype.
	 * @return {@code null} if the given datatype is not an array type, and an {@code ArrayType} instance corresponding to the given datatype otherwise.
	 */
	public static ArrayType stringToArrayType(AST ast, String dataType) {
		if (!dataType.contains("[]")) {
			return null;
		}
		
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
	
	/**
	 * Helper method to convert a String representation of a custom datatype into a {@code SimpleType} instance.
	 * @param ast The given {@code AST} instance.
	 * @param dataType The given datatype. 
	 * @return A {@code SimpleType} instance corresponding to the given datatype.
	 */
	public static SimpleType stringToSimpleType(AST ast, String dataType) {
		return ast.newSimpleType(ast.newSimpleName(dataType));
	}
	
	/**
	 * Helper method to translate a given {@code Visibility} instance into a {@code Modifier.ModifierKeyword} instance.
	 * @param visibility The given {@code Visibility} instance. 
	 * @return The corresponding {@code Modifier.ModifierKeyword} instance.
	 */
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

	/**
	 * @param dataType The given datatype.
	 * @return {@code true} if the datatype represents a custom class, {@code false} otherwise.
	 */
	public static boolean isObject(String dataType) {
		return dataType.startsWith("Class");
	}
	
	private static NumberLiteral randomByteExpression(AST ast) {
		return ast.newNumberLiteral(Byte.toString((byte) (RandomNumberGenerator.getInstance().nextInt(127 - (-128)) - 128)));
	}
	
	private static NumberLiteral randomShortExpression(AST ast) {
		return ast.newNumberLiteral(Short.toString((short) (RandomNumberGenerator.getInstance().nextInt(32767 - (-32768)) - 32767)));
	}
	
	private static NumberLiteral randomIntExpression(AST ast) {
		return ast.newNumberLiteral(Integer.toString(RandomNumberGenerator.getInstance().nextInt()));
	}
	
	private static NumberLiteral randomLongExpression(AST ast) {
		return ast.newNumberLiteral(Long.toString(RandomNumberGenerator.getInstance().nextLong()));
	}
	
	private static NumberLiteral randomFloatExpression(AST ast) {
		return ast.newNumberLiteral(Float.toString(RandomNumberGenerator.getInstance().nextFloat()));
	}
	
	private static NumberLiteral randomDoubleExpression(AST ast) {
		return ast.newNumberLiteral(Double.toString(RandomNumberGenerator.getInstance().nextDouble()));
	}
	
	private static CharacterLiteral randomCharExpression(AST ast) {
		CharacterLiteral characterLiteral = ast.newCharacterLiteral();
		characterLiteral.setCharValue((char) RandomNumberGenerator.getInstance().nextInt(65535));
		return characterLiteral;
	}
	
	private static BooleanLiteral randomBooleanExpression(AST ast) {
		return ast.newBooleanLiteral(RandomNumberGenerator.getInstance().nextBoolean());
	}
	
	/**
	 * Returns a {@code String} representation of the base type of the given array type.
	 * @param dataType The given array type.
	 * @return A {@code String} representation of the base type of the given array type.
	 */
	public static String extractBaseTypeFromArray(String dataType) {
		return extractBaseTypeFrom(dataType);
	}
	
	/**
	 * Returns an {@code Expression} instance representing a randomly initialized primitive type.
	 * @param ast The given {@code AST} instance.
	 * @param dataType The given datatype.
	 * @return {@code null} if the datatype is non-primitive, and an {@code Expression} instance representing a randomly initialized primitive type corresponding to the given datatype otherwise.
	 */
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
	
	/**
	 * Returns an {@code Expression} instance representing a primitive type initialised to its default value.
	 * @param ast The given {@code AST} instance.
	 * @param dataType The given datatype.
	 * @return {@code null} if the datatype is non-primitive, and an {@code Expression} instance representing a default-initialized primitive type corresponding to the given datatype otherwise.
	 */
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
				// We have to manually specify here that it's 0.0f to avoid lossy conversion errors.
				return ast.newNumberLiteral("0.0f");
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
	 * Generates an {@code ArrayCreation} instance that initialises an array with the given length
	 * Elements of the array are randomised instances if primitive, or new object instances if non-primitive.
	 * @param ast The {@code AST} to use for code generation.
	 * @param dataType The array datatype (including square braces)
	 * @return An {@code ArrayCreation} instance that initialises an array with the given length
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
	
	/**
	 * Generates an {@code ArrayCreation} instance that initialises an array with the given length
	 * Elements of the array are default instances if primitive, or new object instances if non-primitive.
	 * @param ast The {@code AST} to use for code generation.
	 * @param dataType The array datatype (including square braces)
	 * @return An {@code ArrayCreation} instance that initialises an array with the given length
	 */
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
	
	/**
	 * Generates a {@code ClassInstanceCreation} instance corresponding to an initialisation of the given custom class.
	 * @param ast The given {@code AST} instance.
	 * @param dataType The given datatype.
	 * @return A {@code ClassInstanceCreation} instance corresponding to an initialisation of the given custom class.
	 */
	public static ClassInstanceCreation newClassInstance(AST ast, String dataType) {
		ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
		classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(dataType)));
		return classInstanceCreation;
	}
	
	/**
	 * Generates an {@code Expression} instance corresponding to a retrieval statement that matches the given path.
	 * @param ast The given {@code AST} instance.
	 * @param path The given path of {@code GraphNode}s.
	 * @param thisReference The class declaring the getter. This is used to distinguish between a reference to a parameter, and a reference to {@code this}.
	 * @return An {@code Expression} instance corresponding to a retrieval statement that matches the given path.
	 */
	public static Expression generateGetterExpressionFromPath(AST ast, List<GraphNode> path, String thisReference) {
		Expression previousExpression = ast.newThisExpression();
		for (int i = 0; i < path.size(); i++) {
			GraphNode currentNode = path.get(i);
			boolean isParam = GraphNodeUtil.isParameter(currentNode);
			boolean isField = GraphNodeUtil.isField(currentNode);
			boolean isMethod = GraphNodeUtil.isMethod(currentNode);
			boolean isArrayElement = GraphNodeUtil.isArrayElement(currentNode);
			
			// We handle the cases differently
			// Consider two possible paths
			// 1) Starts with a parameter
			// 2) Starts with "this"
			// In the first case, we need to set the `previousExpression` variable to the correct parameter
			// In the second case, we need to "skip" the first node (since it's `this`)
			// The code below handles this.
			boolean isFirstNode = (i == 0);
			boolean isThisReference = GraphNodeUtil.getDeclaredClass(currentNode).equals(thisReference);
			if (isFirstNode) {
				if (isParam && !isThisReference) {
					previousExpression = ast.newSimpleName(ClassModelUtil.getParameterNameFor(currentNode));
				}
				continue;
			}
			
			if (isParam) {
				previousExpression = ast.newSimpleName(ClassModelUtil.getParameterNameFor(currentNode));
			} else if (isField) {
				FieldAccess fieldAccess = ast.newFieldAccess();
				fieldAccess.setExpression(previousExpression);
				fieldAccess.setName(ast.newSimpleName(ClassModelUtil.getFieldNameFor(currentNode)));
				previousExpression = fieldAccess;
			} else if (isMethod) {
				MethodInvocation methodInvocation = ast.newMethodInvocation();
				methodInvocation.setExpression(previousExpression);
				methodInvocation.setName(ast.newSimpleName(ClassModelUtil.getMethodNameFor(currentNode)));
				previousExpression = methodInvocation;
			} else if (isArrayElement) {
				ArrayAccess arrayAccess = ast.newArrayAccess();
				arrayAccess.setArray(previousExpression);
				arrayAccess.setIndex(ast.newNumberLiteral("0"));
				previousExpression = arrayAccess;
			}
		}
		return previousExpression;
	}
	
	/**
	 * Generates an {@code Expression} instance corresponding to a setter statement for an array element that matches the given path.
	 * @param ast The given {@code AST} instance.
	 * @param path The given path of {@code GraphNode}s.
	 * @return An {@code Expression} instance corresponding to a setter statement for an array element that matches the given path.
	 */
	public static Expression generateArrayElementSetterExpressionFromPath(AST ast, List<GraphNode> path) {
		Expression previousExpression = ast.newThisExpression();
		for (int i = 0; i < path.size(); i++) {
			GraphNode currentNode = path.get(i);
			
			boolean isFirstNode = (i == 0);
			boolean isLastNode = (i == path.size() - 1);
			boolean isParameter = GraphNodeUtil.isParameter(currentNode);
			boolean isField = GraphNodeUtil.isField(currentNode);
			boolean isMethod = GraphNodeUtil.isMethod(currentNode);
			if (isFirstNode) {
				if (isParameter) {
					previousExpression = ast.newSimpleName(ClassModelUtil.getParameterNameFor(currentNode));
				}
				continue;
			}
			
			if (isLastNode) {
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
				continue;
			}
				
			if (isField) {
				// Assume all fields as package private
				// i.e. we can access it directly
				FieldAccess fieldAccess = ast.newFieldAccess();
				fieldAccess.setExpression(previousExpression);
				fieldAccess.setName(ast.newSimpleName(ClassModelUtil.getFieldNameFor(currentNode)));
				previousExpression = fieldAccess;
			} else if (isMethod) {
				MethodInvocation methodInvocation = ast.newMethodInvocation();
				methodInvocation.setExpression(previousExpression);
				methodInvocation.setName(ast.newSimpleName("method" + currentNode.getIndex()));
				previousExpression = methodInvocation;
			}
		}
		return previousExpression;
	}
	
	/**
	 * Generates a {@code MethodDeclaration} instance that corresponds to a constructor for the given {@code Class}.
	 * @param ast The given {@code AST} instance.
	 * @param clazz The given {@code Class} instance.
	 * @return A {@code MethodDeclaration} instance that corresponds to a constructor for the given {@code Class}.
	 */
	@SuppressWarnings("unchecked")
	public static MethodDeclaration generateConstructorFor(AST ast, testcode.graphgeneration.model.Class clazz) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(true);
		methodDeclaration.setName(ast.newSimpleName(clazz.getName()));
		methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		Block methodBody = ast.newBlock();
		methodDeclaration.setBody(methodBody);
		
		Map<Field, String> fieldToParameterName = new HashMap<>();
		for (Field field : clazz.getFields()) {
			// Run through and find all fields with custom type
			// Generate a parameter for each one and store the mapping for later
			if (!field.isCustomClass()) {
				continue;
			}
			
			Type parameterType = extractTypeFrom(ast, field);
			String parameterName = field.getName();
			fieldToParameterName.put(field, parameterName);
			addParameterToMethod(ast, methodDeclaration, parameterType, parameterName);
		}
		
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
									1 + RandomNumberGenerator.getInstance().nextInt(MAXIMUM_ARRAY_SIZE - 1)
							)
					);
				} else {
					assignment.setRightHandSide(
							randomArrayCreation(
									ast, 
									field.getDataType(), 
									1 + RandomNumberGenerator.getInstance().nextInt(MAXIMUM_ARRAY_SIZE - 1)
							)
					);
				}
			} else if (field.isCustomClass()) {
				// Default initialisation of custom classes can cause infinite recursion
				// in cases where a class has itself as a transitive field. To avoid this, we set
				// the class from a parameter passed into the constructor.
				assignment.setRightHandSide(ast.newSimpleName(fieldToParameterName.get(field)));
			}
			
			methodBody.statements().add(ast.newExpressionStatement(assignment));
		}
		
		return methodDeclaration;
	}
	
	/**
	 * Helper method to add a parameter to a given method. Auto-generates parameter type and name from the 
	 * given {@code GraphNode} instance.
	 * @param ast The {@code AST} instance to use.
	 * @param methodDeclaration The {@code MethodDeclaration} instance to add the parameter to.
	 * @param node The {@code GraphNode} instance that provides context.
	 */
	public static void addParameterToMethod(AST ast, MethodDeclaration methodDeclaration, GraphNode node) {
		Type parameterType = ClassModelUtil.extractTypeFrom(ast, node);
		String parameterName = ClassModelUtil.getParameterNameFor(node);
		addParameterToMethod(ast, methodDeclaration, parameterType, parameterName);
	}
	
	/**
	 * Overloaded version of {@code addParameterToMethod} that allows more fine-grained control over
	 * parameter type and name.
	 * @param ast The {@code AST} instance to use.
	 * @param methodDeclaration The {@code MethodDeclaration} instance to add the parameter to.
	 * @param parameterType The type of the parameter.
	 * @param parameterName The name of the parameter.
	 */
	public static void addParameterToMethod(AST ast, MethodDeclaration methodDeclaration, Type parameterType, String parameterName) {
		SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
		parameter.setType(parameterType);
		parameter.setName(ast.newSimpleName(parameterName));
		methodDeclaration.parameters().add(parameter);
	}
}
