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
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import testcode.graphgeneration.ClassModelUtil;
import testcode.graphgeneration.Graph;
import testcode.graphgeneration.GraphNode;
import testcode.graphgeneration.GraphNodeUtil;
import testcode.graphgeneration.Visibility;

public class ClassModel {
	private Map<String, Class> classNameToClass = new HashMap<>();
	private Graph graph = null;
	private Set<GraphNode> processedNodes = null;
	
	public ClassModel(Graph graph) {
		this.graph = graph;
		
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
		
	}
	
	private Method generateSetterForArrayElement(GraphNode node) {
		if (!GraphNodeUtil.isArrayElement(node)) {
			return null;
		}
		
		// Assume array elements always have a single parent
		if (node.getParents().size() != 1) {
			return null;
		}
		
		GraphNode parentNode = node.getParents().get(0);
		boolean isParentField = GraphNodeUtil.isField(parentNode);
		boolean isParentMethod = GraphNodeUtil.isMethod(parentNode);
		if (isParentField) {
			// Generate a FieldArrayElementSetter
			Field parentField = getCorrespondingField(parentNode);
			if (parentField == null) {
				System.err.println("Failed to find a corresponding field for " + parentNode + ".");
				return null;
			}
			return new FieldArrayElementSetter(GraphNodeUtil.getDeclaredClass(parentNode), "setNode" + node.getIndex(), "void", parentField);
		}
		if (isParentMethod) {
			Method parentMethod = getCorrespondingMethod(parentNode);
			if (parentMethod == null) {
				System.err.println("Failed to find a corresponding method for " + parentNode + ".");
				return null;
			}
			return new MethodArrayElementSetter(GraphNodeUtil.getDeclaredClass(parentNode), "setNode" + node.getIndex(), "void", parentMethod);
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
			return methodDeclaration;
		} else if (isSetter) {
			MethodDeclaration methodDeclaration = generateMethodSkeletonFrom(method, ast);
			
			Type parameterType = null;
			if (isFieldSetter) {
				Setter setter = (Setter) method;
				parameterType = ClassModelUtil.extractTypeFrom(ast, setter.getSetField());
			} else if (isFieldArrayElementSetter) {
				FieldArrayElementSetter fieldArrayElementSetter = (FieldArrayElementSetter) method;
				ArrayType arrayType = (ArrayType) ClassModelUtil.extractTypeFrom(ast, fieldArrayElementSetter.getArray());
				parameterType = ClassModelUtil.getCopyOfType(ast, arrayType.getComponentType());
			} else if (isMethodArrayElementSetter) {
				MethodArrayElementSetter methodArrayElementSetter = (MethodArrayElementSetter) method;
				ArrayType arrayType = (ArrayType) ClassModelUtil.extractReturnTypeFrom(ast, methodArrayElementSetter.getArray());
				parameterType = ClassModelUtil.getCopyOfType(ast, arrayType.getComponentType());
			}
			
			if (parameterType == null) {
				System.err.println("Failed to generate a parameterType for " + method);
				return null;
			}
			SingleVariableDeclaration parameter = ast.newSingleVariableDeclaration();
			parameter.setType(parameterType);
			// TODO: Extend for multiple parameters
			parameter.setName(ast.newSimpleName("arg0"));
			methodDeclaration.parameters().add(parameter);
			
			// TODO Darien we shall generate method body here
			// e.g., we new a variable of specific type and return it. 
			
			
			typeDeclaration.bodyDeclarations().add(methodDeclaration);
			
			return methodDeclaration;
		}
		
		return null;
	}
	
	
	HashMap<CodeElement, ASTNode> node2CodeMap = new HashMap<>();
	
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
		
		generateAccessibilityCode();
	}

	private void generateAccessibilityCode() {
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
						Method setter = generateSetterForArrayElement(accessibleNode);
						classRepresentation.addMethod(setter);
						
						CompilationUnit cu = (CompilationUnit)node2CodeMap.get(classRepresentation);
						// TODO Darien: generate a path from currentNode to accessibleNode
						// TODO then, we generate a method with its body including the path.
						
					}
					
					if (isField) {
						// If it's a leaf node, generate a setter
						// Else generate a getter
						if (accessibleNode.isLeaf()) {
							// Generate setter
							Method setter = new Setter(GraphNodeUtil.getDeclaredClass(currentNode), "setNode" + accessibleNode.getIndex(), "void", field);
							classRepresentation.addMethod(setter);
							
							CompilationUnit cu = (CompilationUnit)node2CodeMap.get(classRepresentation);
							// TODO Darien: generate a path from currentNode to accessibleNode
							// TODO then, we generate a method with its body including the path.
						} else {
							// Generate getter
							Method getter = new Getter(GraphNodeUtil.getDeclaredClass(currentNode), "getNode" + accessibleNode.getIndex(), GraphNodeUtil.getDeclaredClass(accessibleNode), field);
							classRepresentation.addMethod(getter);
							
							CompilationUnit cu = (CompilationUnit)node2CodeMap.get(classRepresentation);
							// TODO Darien: generate a path from currentNode to accessibleNode
							// TODO then, we generate a method with its body including the path.
						}
					}
				}
			}
		}
		
	}
}
