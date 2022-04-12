package testcode.graphgeneration.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import testcode.graphgeneration.ClassModelUtil;
import testcode.graphgeneration.Graph;
import testcode.graphgeneration.GraphNode;
import testcode.graphgeneration.GraphNodeUtil;
import testcode.graphgeneration.OCGGenerator;
import testcode.graphgeneration.Visibility;

public class ClassModel {
	private Map<String, Class> classNameToClass = new HashMap<>();
	private Graph graph = null;
	private Set<GraphNode> processedNodes = null;
	private Map<CodeElement, ASTNode> node2CodeMap = new HashMap<>();
	
	private boolean isFieldsAndMethodsGenerated = false;
	private boolean isGettersAndSettersGenerated = false;
	
	public ClassModel(Graph graph) {
		this.graph = graph;
		
		generateFieldsAndMethods();
		generateGettersAndSetters();	
	}
	
	private void generateFieldsAndMethods() {
		// Process all graph nodes in order
		this.processedNodes = new HashSet<>();
		Queue<GraphNode> queue = new ArrayDeque<>();
		queue.addAll(graph.getTopLayer());
		while (!queue.isEmpty()) {
			GraphNode currentNode = queue.poll();
			boolean isAllParentsProcessed = 
					currentNode.getParents() == null 
					|| currentNode.getParents().size() == 0 
					|| processedNodes.containsAll(currentNode.getParents());
			if (!isAllParentsProcessed) {
				queue.offer(currentNode);
				continue;
			}
			
			processGraphNode(currentNode);
			processedNodes.add(currentNode);
			
			for (GraphNode childNode : currentNode.getChildren()) {
				queue.offer(childNode);
			}
		}
		
		isFieldsAndMethodsGenerated = true;
	}
	
	private Method generateSetterForArrayElement(GraphNode fromNode, GraphNode toNode) {
		if (!GraphNodeUtil.isArrayElement(toNode)) {
			return null;
		}
		
		// Assume array elements always have a single parent
		if (toNode.getParents().size() != 1) {
			return null;
		}
		
		GraphNode parentNode = toNode.getParents().get(0);
		boolean isParentField = GraphNodeUtil.isField(parentNode);
		boolean isParentMethod = GraphNodeUtil.isMethod(parentNode);
		List<GraphNode> path = graph.getPath(fromNode, toNode);
		if (isParentField) {
			// Generate a FieldArrayElementSetter
			Field parentField = getCorrespondingField(parentNode);
			if (parentField == null) {
				System.err.println("Failed to find a corresponding field for " + parentNode + ".");
				return null;
			}
			return new FieldArrayElementSetter(GraphNodeUtil.getDeclaredClass(parentNode), "setNode" + toNode.getIndex(), "void", parentField, path);
		}
		if (isParentMethod) {
			Method parentMethod = getCorrespondingMethod(parentNode);
			if (parentMethod == null) {
				System.err.println("Failed to find a corresponding method for " + parentNode + ".");
				return null;
			}
			return new MethodArrayElementSetter(GraphNodeUtil.getDeclaredClass(parentNode), "setNode" + toNode.getIndex(), "void", parentMethod, path);
		}
		
		return null;
	}

	private Method getCorrespondingMethod(GraphNode node) {
		if (!GraphNodeUtil.isMethod(node)) {
			return null;
		}
		
		// Strictly speaking, we should also use the method's declaring class
		// to uniquely identify the method. However, since we use unique names for each method
		// this should be sufficient too.
		String desiredMethodName = "method" + node.getIndex();
		String desiredMethodReturnType = GraphNodeUtil.getDeclaredClass(node);
		for (Class clazz : classNameToClass.values()) {
			List<Method> methods = clazz.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				String methodReturnType = method.getReturnType();
				boolean isFieldNameMatches = methodName.equals(desiredMethodName);
				boolean isFieldTypeMatches = methodReturnType.equals(desiredMethodReturnType);
				if (isFieldNameMatches && isFieldTypeMatches) {
					return method;
				}
			}
		}
		return null;
	}
	
	/*
	 * Returns the corresponding field
	 * Only works if the classNameToClass map has been initialized
	 */
	private Field getCorrespondingField(GraphNode node) {
		if (!GraphNodeUtil.isField(node)) {
			return null;
		}
		
		String desiredFieldName = GraphNodeUtil.getDeclaredClass(node) + "_" + node.getIndex();
		String desiredFieldType = GraphNodeUtil.getDeclaredClass(node);
		for (Class clazz : classNameToClass.values()) {
			List<Field> fields = clazz.getFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String fieldType = field.getDataType();
				boolean isFieldNameMatches = fieldName.equals(desiredFieldName);
				boolean isFieldTypeMatches = fieldType.equals(desiredFieldType);
				if (isFieldNameMatches && isFieldTypeMatches) {
					return field;
				}
			}
		}
		return null;
	}
	
	/**
	 * Extracts class information from a given node and stores it in the current ClassModel
	 */
	private void processGraphNode(GraphNode node) {
		boolean isObject = GraphNodeUtil.isObject(node);
		boolean isPrimitive = GraphNodeUtil.isPrimitive(node);
		boolean isArray = GraphNodeUtil.isArray(node);
		
		// Don't do anything for primitive nodes
		if (isPrimitive) {
			return;
		}
		
		// Don't do anything for arrays
		if (isArray) {
			return;
		}
		
		// Generate/update the appropriate Class object corresponding to this node
		// 1) Check if we've seen this class name before
		//   1a) If we have, retrieve the appropriate Class object to update
		//   1b) Else, generate a new Class object and store it
		// 2) Lookahead to its children and add the appropriate methods/fields
		if (isObject) {
			String declaredClass = GraphNodeUtil.getDeclaredClass(node);
			boolean isNewClass = !classNameToClass.containsKey(declaredClass);
			Class classRepresentation = null;
			if (isNewClass) {
				classRepresentation = new Class(declaredClass);
				classNameToClass.put(declaredClass, classRepresentation);
			} else {
				classRepresentation = classNameToClass.get(declaredClass);
			}
			
			for (GraphNode childNode : node.getChildren()) {
				boolean isField = GraphNodeUtil.isField(childNode);
				boolean isMethod = GraphNodeUtil.isMethod(childNode);
				String childDeclaredClass = GraphNodeUtil.getDeclaredClass(childNode);
				if (isField) {
					Field childField = new Field(childDeclaredClass + "_" + childNode.getIndex(), childDeclaredClass);
					classRepresentation.addField(childField);
				}
				
				if (isMethod) {
					Method childMethod = new Method(declaredClass, "method" + childNode.getIndex(), childDeclaredClass);
					classRepresentation.addMethod(childMethod);
				}
			}
		}
	}
	
	public List<Class> getClasses() {
		return new ArrayList<>(classNameToClass.values());
	}
	
	@SuppressWarnings("unchecked")
	private FieldDeclaration addFieldToAst(Field fieldRepresentation, AST ast, TypeDeclaration typeDeclaration) {
		VariableDeclarationFragment variableDeclarationFragment = ast.newVariableDeclarationFragment();
		variableDeclarationFragment.setName(ast.newSimpleName(fieldRepresentation.getName()));
		FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(variableDeclarationFragment);
		
		Type fieldType = ClassModelUtil.extractTypeFrom(ast, fieldRepresentation);
		if (fieldType == null) {
			System.err.println("Failed to generate an appropriate Type for " + fieldRepresentation);
			return null;
		}
		fieldDeclaration.setType(fieldType);
		
		Modifier fieldModifier;
		if (!fieldRepresentation.getVisibility().equals(Visibility.DEFAULT)) {
			Modifier.ModifierKeyword fieldModifierKeyword = ClassModelUtil.visibilityToModifierKeyword(fieldRepresentation.getVisibility());
			if (fieldModifierKeyword == null) {
				// Error state, skip
				System.err.println("Failed to find an appropriate Modifier.ModifierKeyword for " + fieldRepresentation.getVisibility());
				return null;
			}
			fieldModifier = ast.newModifier(fieldModifierKeyword);
			fieldDeclaration.modifiers().add(fieldModifier);
		}
		
		typeDeclaration.bodyDeclarations().add(fieldDeclaration);
		
		return fieldDeclaration;
	}
	
	@SuppressWarnings("unchecked")
	private MethodDeclaration generateMethodSkeletonFrom(Method method, AST ast) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		Type returnType = ClassModelUtil.extractReturnTypeFrom(ast, method);
		if (returnType == null) {
			System.err.println("Failed to generate an appropriate Type for " + method.getReturnType());
			return null;
		}
		methodDeclaration.setReturnType2(returnType);
		if (method.getVisibility() != Visibility.DEFAULT) {
			methodDeclaration.modifiers().add(ClassModelUtil.extractModifierFrom(ast, method));
		}
		methodDeclaration.setName(ast.newSimpleName(method.getName()));
		return methodDeclaration;
	}
	
	@SuppressWarnings("unchecked")
	private MethodDeclaration addMethodToAst(Method method, AST ast, TypeDeclaration typeDeclaration) {
		boolean isGetter = (method instanceof Getter);
		boolean isFieldSetter = (method instanceof Setter);
		boolean isFieldArrayElementSetter = (method instanceof FieldArrayElementSetter);
		boolean isMethodArrayElementSetter = (method instanceof MethodArrayElementSetter);
		boolean isSetter = (isFieldSetter || isFieldArrayElementSetter || isMethodArrayElementSetter);
		boolean isRegularMethod = !(isGetter || isFieldSetter || isFieldArrayElementSetter || isMethodArrayElementSetter);
		if (isGetter || isRegularMethod) {
			MethodDeclaration methodDeclaration = generateMethodSkeletonFrom(method, ast);
			typeDeclaration.bodyDeclarations().add(methodDeclaration);
			if (isGetter) {
				Block getterBody = generateGetterBodyFromPath(ast, ((Getter) method).getPathToReturnedField());
				methodDeclaration.setBody(getterBody);
			} else {
				Block regularMethodBody = generateRegularMethodBody(ast, method);
				methodDeclaration.setBody(regularMethodBody);
			}
			return methodDeclaration;
		} else if (isSetter) {
			MethodDeclaration methodDeclaration = generateMethodSkeletonFrom(method, ast);
			Type parameterType = null;
			if (isFieldSetter) {
				Setter setter = (Setter) method;
				parameterType = ClassModelUtil.extractTypeFrom(ast, setter.getSetField());
				Block setterBody = generateSetterBodyFromPath(ast, setter.getPathToSetField());
				methodDeclaration.setBody(setterBody);
			} else if (isFieldArrayElementSetter) {
				FieldArrayElementSetter fieldArrayElementSetter = (FieldArrayElementSetter) method;
				ArrayType arrayType = (ArrayType) ClassModelUtil.extractTypeFrom(ast, fieldArrayElementSetter.getArray());
				parameterType = ClassModelUtil.getCopyOfType(ast, arrayType.getComponentType());
				Block arrayElementSetterBody = generateArrayElementSetterBodyFromPath(ast, fieldArrayElementSetter.getPath());
				methodDeclaration.setBody(arrayElementSetterBody);
			} else if (isMethodArrayElementSetter) {
				MethodArrayElementSetter methodArrayElementSetter = (MethodArrayElementSetter) method;
				ArrayType arrayType = (ArrayType) ClassModelUtil.extractReturnTypeFrom(ast, methodArrayElementSetter.getArray());
				parameterType = ClassModelUtil.getCopyOfType(ast, arrayType.getComponentType());
				Block arrayElementSetterBody = generateArrayElementSetterBodyFromPath(ast, methodArrayElementSetter.getPath());
				methodDeclaration.setBody(arrayElementSetterBody);
			}
			
			if (parameterType == null) {
				System.err.println("Failed to generate a parameterType for " + method);
				return null;
			}
			SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
			parameter.setType(parameterType);
			// TODO: Extend for multiple parameters
			// If and when we do this, we'll need to fix the setter bodies as well
			parameter.setName(ast.newSimpleName("arg0"));
			methodDeclaration.parameters().add(parameter);			
			
			typeDeclaration.bodyDeclarations().add(methodDeclaration);
			
			return methodDeclaration;
		}
		
		return null;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private Block generateRegularMethodBody(AST ast, Method method) {
		String returnType = method.getReturnType();
		// Three possibilities
		// 1) Return type is primitive
		boolean isPrimitive = ClassModelUtil.isPrimitive(returnType);
		// 2) Return type is array
		boolean isArray = ClassModelUtil.isArray(returnType);
		// 3) Return type is object
		boolean isObject = ClassModelUtil.isObject(returnType);
		
		Block methodBody = ast.newBlock();
		if (isPrimitive) {
			ReturnStatement returnStatement = ast.newReturnStatement();
			returnStatement.setExpression(ClassModelUtil.getRandomPrimitiveLiteral(ast, returnType));
			methodBody.statements().add(returnStatement);
		} else if (isArray) {
			ArrayCreation arrayCreation = ast.newArrayCreation();
			arrayCreation.setType(ClassModelUtil.stringToArrayType(ast, returnType));
			
			String baseType = ClassModelUtil.extractBaseTypeFromArray(returnType);
			boolean isBaseTypePrimitive = ClassModelUtil.isPrimitive(baseType);
			ArrayInitializer arrayInitializer = ast.newArrayInitializer();
			int arrayLength = OCGGenerator.RANDOM.nextInt(9) + 1;
			for (int i = 0; i < arrayLength; i++) {
				if (isBaseTypePrimitive) {
					arrayInitializer.expressions().add(ClassModelUtil.getRandomPrimitiveLiteral(ast, baseType));
				}
				
				// TODO: What if the array isn't primitive?
			}
			arrayCreation.setInitializer(arrayInitializer);
			
			ReturnStatement returnStatement = ast.newReturnStatement();
			returnStatement.setExpression(arrayCreation);
			methodBody.statements().add(returnStatement);
		} else if (isObject) {
			ReturnStatement returnStatement = ast.newReturnStatement();
			ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
			classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(returnType)));
			returnStatement.setExpression(classInstanceCreation);
			methodBody.statements().add(returnStatement);
		}
		
		return methodBody;
	}
	
	@SuppressWarnings("unchecked")
	private Block generateGetterBodyFromPath(AST ast, List<GraphNode> path) {
		Block methodBody = ast.newBlock();
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
				fieldAccess.setName(ast.newSimpleName(GraphNodeUtil.getDeclaredClass(currentNode) + "_" + currentNode.getIndex()));
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
		
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(previousExpression);
		methodBody.statements().add(returnStatement);
		
		return methodBody;
	}

	// We assume that the method belongs to the first node in the path
	@SuppressWarnings("unchecked")
	private Block generateArrayElementSetterBodyFromPath(AST ast, List<GraphNode> path) {
		Block methodBody = ast.newBlock();
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
					fieldAccess.setName(ast.newSimpleName(GraphNodeUtil.getDeclaredClass(currentNode) + "_" + currentNode.getIndex()));
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
				
				methodBody.statements().add(ast.newExpressionStatement(assignment));
			}
		}
		
		return methodBody;
	}

	@SuppressWarnings("unchecked")
	private CompilationUnit generateSkeletonCodeFromClass(Class clazz) {
		
		String className = clazz.getName();
		
		AST ast = AST.newAST(AST.JLS4);
		Class classRepresentation = classNameToClass.get(className);
		if (classRepresentation == null) {
			return null;
		}
		
		CompilationUnit compilationUnit = ast.newCompilationUnit();
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(ast.newSimpleName("test"));
		compilationUnit.setPackage(packageDeclaration);
		
		TypeDeclaration typeDeclaration = ast.newTypeDeclaration();
		typeDeclaration.setName(ast.newSimpleName(className));
		compilationUnit.types().add(typeDeclaration);
		
		node2CodeMap.put(clazz, compilationUnit);
		
		for (Field fieldRepresentation : classRepresentation.getFields()) {
			FieldDeclaration f =  addFieldToAst(fieldRepresentation, ast, typeDeclaration);
			node2CodeMap.put(fieldRepresentation, f);
		}
		
		for (Method methodRepresentation : classRepresentation.getMethods()) {
			MethodDeclaration m = addMethodToAst(methodRepresentation, ast, typeDeclaration);
			node2CodeMap.put(methodRepresentation, m);
		}
		
		return compilationUnit;
	}
	
	public void transformToCode() {
		Map<String, CompilationUnit> classNameToCode = new HashMap<>();
		
		for (String className : classNameToClass.keySet()) {
			Class clazz = classNameToClass.get(className);
			CompilationUnit compilationUnit = generateSkeletonCodeFromClass(clazz);
			classNameToCode.put(className, compilationUnit);
			System.out.println(compilationUnit.toString());
		}
	}

	private void generateGettersAndSetters() {
		if (!isFieldsAndMethodsGenerated) {
			throw new IllegalStateException("Attempted to generate getters and setters before fields and methods were generated!");
		}
		
		/**
		 * build accessibility
		 */
		for (GraphNode currentNode : processedNodes) {
			boolean isArray = GraphNodeUtil.isArray(currentNode);
			if (isArray) {
				continue;
			}
			
			List<GraphNode> accessibleNodes = graph.getNodesAccessibleFrom(currentNode);
			for (GraphNode accessibleNode : accessibleNodes) {
				
				boolean isField = GraphNodeUtil.isField(accessibleNode);
				boolean isArrayElement = GraphNodeUtil.isArrayElement(accessibleNode);
				if (isField || isArrayElement) {
					// Generate a method returning this field.
					Field field = null;
					if (isField) {
						field = getCorrespondingField(accessibleNode);
						if (field == null) {
							// Note warning
							System.err.println("ERROR: Attempted to find a corresponding field for " + accessibleNode + ", but could not find anything.");
							continue;
						}
					}				
					
					Class classRepresentation = classNameToClass.get(GraphNodeUtil.getDeclaredClass(currentNode));
					if (classRepresentation == null) {
						// Note warning
						System.err.println("ERROR: Attempted to find a corresponding class for " + currentNode + ", but could not find anything.");
						continue;
					}
					
					if (isArrayElement) {
						// Array elements are always leaf nodes
						// Generate a setter for the array element
						// We indicate that it's an array element setter
						// by indicating the array that we want to set
						Method setter = generateSetterForArrayElement(currentNode, accessibleNode);
						classRepresentation.addMethod(setter);
					}
					
					if (isField) {
						// If it's a leaf node, generate a setter
						// Else generate a getter
						if (accessibleNode.isLeaf()) {
							// Generate setter
							Method setter = generateSetterForField(currentNode, accessibleNode, field);
							classRepresentation.addMethod(setter);
						} else {
							// Generate getter
							Method getter = generateGetterForField(currentNode, accessibleNode, field);
							classRepresentation.addMethod(getter);
						}
					}
				}
			}
		}
		
		isGettersAndSettersGenerated = true;
	}

	private Method generateGetterForField(GraphNode fromNode, GraphNode toNode, Field field) {
		List<GraphNode> path = graph.getPath(fromNode, toNode);
		
		return new Getter(
				GraphNodeUtil.getDeclaredClass(fromNode), 
				"getNode" + toNode.getIndex(), 
				GraphNodeUtil.getDeclaredClass(toNode), 
				field, 
				path
			);
	}

	private Setter generateSetterForField(GraphNode fromNode, GraphNode toNode, Field field) {
		List<GraphNode> path = graph.getPath(fromNode, toNode);
		
		return new Setter(
				GraphNodeUtil.getDeclaredClass(fromNode), 
				"setNode" + toNode.getIndex(), 
				"void", 
				field, 
				path
			);
	}
	
	// TODO, not complete
	@SuppressWarnings("unchecked")
	private Block generateSetterBodyFromPath(AST ast, List<GraphNode> path) {
		Block methodBody = ast.newBlock();
		Expression previousExpression = null;
		for (int i = 0; i < path.size(); i++) {
			GraphNode currentNode = path.get(i);
			boolean isParam = GraphNodeUtil.isParameter(currentNode);
			if (isParam) {
				continue;
			}
			
			if (i != path.size() - 1) {
				boolean isField = GraphNodeUtil.isField(currentNode);
				if (isField) {
					// Assume all fields as package private
					// i.e. we can access it directly
					FieldAccess fieldAccess = ast.newFieldAccess();
					if (i == 1) {
						fieldAccess.setExpression(ast.newThisExpression());
						previousExpression = fieldAccess;
					} else {
						fieldAccess.setExpression(previousExpression);
						previousExpression = fieldAccess;
					}
					fieldAccess.setName(ast.newSimpleName(GraphNodeUtil.getDeclaredClass(currentNode) + "_" + currentNode.getIndex()));
				}
				
				boolean isMethod = GraphNodeUtil.isMethod(currentNode);
				if (isMethod) {
					MethodInvocation methodInvocation = ast.newMethodInvocation();
					if (i == 1) {
						methodInvocation.setExpression(ast.newThisExpression());
						previousExpression = methodInvocation;
					} else {
						methodInvocation.setExpression(previousExpression);
						previousExpression = methodInvocation;
					}
					methodInvocation.setName(ast.newSimpleName("method" + currentNode.getIndex()));
				}
			} else {
				Assignment assignment = ast.newAssignment();
				assignment.setLeftHandSide(previousExpression);
				assignment.setRightHandSide(ast.newSimpleName("arg0"));
				
				methodBody.statements().add(ast.newExpressionStatement(assignment));
			}
		}
		
		return methodBody;
	}
}
