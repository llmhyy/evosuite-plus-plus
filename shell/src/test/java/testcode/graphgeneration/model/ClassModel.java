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
	private Map<CodeElement, ASTNode> nodeToCode = new HashMap<>();
	
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
			if (_nullCheck(parentField, "Failed to find a corresponding field for " + parentNode + ".")) {
				return null;
			}
			return new FieldArrayElementSetter(
					GraphNodeUtil.getDeclaredClass(parentNode), 
					ClassModelUtil.getGetterNameFor(toNode), 
					"void", 
					parentField, 
					path
				);
		}
		if (isParentMethod) {
			Method parentMethod = getCorrespondingMethod(parentNode);
			if (_nullCheck(parentMethod, "Failed to find a corresponding method for " + parentNode + ".")) {
				return null;
			}
			return new MethodArrayElementSetter(
					GraphNodeUtil.getDeclaredClass(parentNode), 
					ClassModelUtil.getSetterNameFor(toNode), 
					"void", 
					parentMethod, 
					path
				);
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
		String desiredMethodName = ClassModelUtil.getMethodNameFor(node);
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
		
		String desiredFieldName = ClassModelUtil.getFieldNameFor(node);
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
					Field childField = new Field(ClassModelUtil.getFieldNameFor(childNode), childDeclaredClass);
					classRepresentation.addField(childField);
				}
				
				if (isMethod) {
					Method childMethod = new Method(declaredClass, ClassModelUtil.getMethodNameFor(childNode), childDeclaredClass);
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
		if (_nullCheck(fieldType, "Failed to generate an appropriate Type for " + fieldRepresentation)) {
			return null;
		}
		fieldDeclaration.setType(fieldType);
		
		Modifier fieldModifier;
		if (!fieldRepresentation.getVisibility().equals(Visibility.DEFAULT)) {
			Modifier.ModifierKeyword fieldModifierKeyword = ClassModelUtil.visibilityToModifierKeyword(fieldRepresentation.getVisibility());
			if (_nullCheck(fieldModifierKeyword, "Failed to find an appropriate Modifier.ModifierKeyword for " + fieldRepresentation.getVisibility())) {
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
		if (_nullCheck(returnType, "Failed to generate an appropriate Type for " + method.getReturnType())) {
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
	private void addMethodToAst(Method method, AST ast, TypeDeclaration typeDeclaration) {
		boolean isGetter = (method instanceof Getter);
		boolean isFieldSetter = (method instanceof Setter);
		boolean isFieldArrayElementSetter = (method instanceof FieldArrayElementSetter);
		boolean isMethodArrayElementSetter = (method instanceof MethodArrayElementSetter);
		boolean isSetter = (isFieldSetter || isFieldArrayElementSetter || isMethodArrayElementSetter);
		boolean isRegularMethod = !(isGetter || isFieldSetter || isFieldArrayElementSetter || isMethodArrayElementSetter);
		MethodDeclaration methodDeclaration = generateMethodSkeletonFrom(method, ast);
		if (isGetter || isRegularMethod) {
			Block methodBody = null;
			typeDeclaration.bodyDeclarations().add(methodDeclaration);
			if (isGetter) {
				methodBody = generateGetterBodyFromPath(ast, ((Getter) method).getPathToReturnedField());
			} else {
				methodBody = generateRegularMethodBody(ast, method);
			}
			methodDeclaration.setBody(methodBody);
		} else if (isSetter) {
			Block methodBody = null;
			Type parameterType = null;
			if (isFieldSetter) {
				Setter setter = (Setter) method;
				parameterType = ClassModelUtil.extractTypeFrom(ast, setter.getSetField());
				methodBody = generateSetterBodyFromPath(ast, setter.getPathToSetField());
			} else if (isFieldArrayElementSetter) {
				FieldArrayElementSetter fieldArrayElementSetter = (FieldArrayElementSetter) method;
				ArrayType arrayType = (ArrayType) ClassModelUtil.extractTypeFrom(ast, fieldArrayElementSetter.getArray());
				parameterType = ClassModelUtil.getCopyOfType(ast, arrayType.getComponentType());
				methodBody = generateArrayElementSetterBodyFromPath(ast, fieldArrayElementSetter.getPath());
			} else if (isMethodArrayElementSetter) {
				MethodArrayElementSetter methodArrayElementSetter = (MethodArrayElementSetter) method;
				ArrayType arrayType = (ArrayType) ClassModelUtil.extractReturnTypeFrom(ast, methodArrayElementSetter.getArray());
				parameterType = ClassModelUtil.getCopyOfType(ast, arrayType.getComponentType());
				methodBody = generateArrayElementSetterBodyFromPath(ast, methodArrayElementSetter.getPath());
			}
			
			if (_nullCheck(methodBody, "Failed to generate a method body for " + method)) {
				return;
			}
			
			methodDeclaration.setBody(methodBody);
			
			if (_nullCheck(parameterType, "Failed to generate a parameterType for " + method)) {
				return;
			}
			
			SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
			parameter.setType(parameterType);
			// TODO: Extend for multiple parameters
			// If and when we do this, we'll need to fix the setter bodies as well
			parameter.setName(ast.newSimpleName("arg0"));
			methodDeclaration.parameters().add(parameter);			
			
			typeDeclaration.bodyDeclarations().add(methodDeclaration);	
		}
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
		ReturnStatement returnStatement = ast.newReturnStatement();
		if (isPrimitive) {
			returnStatement.setExpression(ClassModelUtil.randomPrimitiveExpression(ast, returnType));
		} else if (isArray) {
			returnStatement.setExpression(
					ClassModelUtil.randomArrayCreation(ast, returnType, 1 + OCGGenerator.RANDOM.nextInt(9)));
		} else if (isObject) {
			returnStatement.setExpression(ClassModelUtil.newClassInstance(ast, returnType));
		}
		methodBody.statements().add(returnStatement);
		return methodBody;
	}
	
	@SuppressWarnings("unchecked")
	private Block generateGetterBodyFromPath(AST ast, List<GraphNode> path) {
		Block methodBody = ast.newBlock();
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(ClassModelUtil.generateGetterExpressionFromPath(ast, path));
		methodBody.statements().add(returnStatement);
		
		return methodBody;
	}

	// We assume that the method belongs to the first node in the path
	@SuppressWarnings("unchecked")
	private Block generateArrayElementSetterBodyFromPath(AST ast, List<GraphNode> path) {
		Block methodBody = ast.newBlock();
		methodBody.statements().add(
				ast.newExpressionStatement(
						ClassModelUtil.generateArrayElementSetterExpressionFromPath(ast, path)
				)
		);
		return methodBody;
	}
	
	@SuppressWarnings("unchecked")
	private CompilationUnit generateCodeFromClass(Class clazz) {
		AST ast = AST.newAST(AST.JLS4);
		
		CompilationUnit compilationUnit = ast.newCompilationUnit();
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(ast.newSimpleName("test"));
		compilationUnit.setPackage(packageDeclaration);
		
		TypeDeclaration typeDeclaration = ast.newTypeDeclaration();
		typeDeclaration.setName(ast.newSimpleName(clazz.getName()));
		compilationUnit.types().add(typeDeclaration);
		
		for (Field fieldRepresentation : clazz.getFields()) {
			addFieldToAst(fieldRepresentation, ast, typeDeclaration);
		}
		
		// Generate a constructor that initialises all fields
		// either to a random value (if primitive)
		// or a new object instance (if non-primitive)
		typeDeclaration.bodyDeclarations().add(ClassModelUtil.generateConstructorFor(ast, clazz));
		
		for (Method methodRepresentation : clazz.getMethods()) {
			addMethodToAst(methodRepresentation, ast, typeDeclaration);
		}
		
		return compilationUnit;
	}
	
	public void transformToCode() {		
		for (String className : classNameToClass.keySet()) {
			Class clazz = classNameToClass.get(className);
			CompilationUnit compilationUnit = generateCodeFromClass(clazz);
			nodeToCode.put(clazz, compilationUnit);
			
			System.out.println(compilationUnit.toString());
		}
	}

	private void generateGettersAndSetters() {
		if (!isFieldsAndMethodsGenerated) {
			throw new IllegalStateException("Attempted to generate getters and setters before fields and methods were generated!");
		}
		
		for (GraphNode currentNode : processedNodes) {
			boolean isArray = GraphNodeUtil.isArray(currentNode);
			if (isArray) {
				continue;
			}
			
			List<GraphNode> accessibleNodes = graph.getNodesAccessibleFrom(currentNode);
			for (GraphNode accessibleNode : accessibleNodes) {
				boolean isField = GraphNodeUtil.isField(accessibleNode);
				boolean isArrayElement = GraphNodeUtil.isArrayElement(accessibleNode);
				Class classRepresentation = classNameToClass.get(GraphNodeUtil.getDeclaredClass(currentNode));
				if (_nullCheck(classRepresentation, "ERROR: Attempted to find a corresponding class for " + currentNode + ", but could not find anything.")) {
					continue;
				}
				
				if (isField) {
					Field field = getCorrespondingField(accessibleNode);
					if (_nullCheck(field, "ERROR: Attempted to find a corresponding field for " + accessibleNode + ", but could not find anything.")) {
						continue;
					}
					Method method;
					if (accessibleNode.isLeaf()) {
						method = generateSetterForField(currentNode, accessibleNode, field);
					} else {
						method = generateGetterForField(currentNode, accessibleNode, field);
					}
					classRepresentation.addMethod(method);
				} else if (isArrayElement) {
					// Array elements are always leaf nodes
					// Generate a setter for the array element
					// We indicate that it's an array element setter
					// by indicating the array that we want to set
					Method setter = generateSetterForArrayElement(currentNode, accessibleNode);
					classRepresentation.addMethod(setter);
				}
			}
		}
		
		isGettersAndSettersGenerated = true;
	}
	
	private boolean _nullCheck(Object object, String errorMessage) {
		if (object == null) {
			System.err.println(errorMessage);
			return true;
		}
		return false;
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
	// Need to add multi-arg support for setters
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
					fieldAccess.setName(ast.newSimpleName(ClassModelUtil.getFieldNameFor(currentNode)));
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
					methodInvocation.setName(ast.newSimpleName(ClassModelUtil.getMethodNameFor(currentNode)));
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
