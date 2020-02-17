package org.evosuite.testcase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.dataflow.ConstructionPath;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.runtime.System;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.statements.AbstractStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.numeric.BooleanPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.BytePrimitiveStatement;
import org.evosuite.testcase.statements.numeric.CharPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.DoublePrimitiveStatement;
import org.evosuite.testcase.statements.numeric.FloatPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.IntPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.LongPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.NumericalPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.ShortPrimitiveStatement;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericField;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ConstructionPathSynthesizer {
	
	private TestFactory testFactory;
	
	public ConstructionPathSynthesizer(TestFactory testFactory) {
		super();
		this.testFactory = testFactory;
	}

	public void constructDifficultObjectStatement(TestCase test, List<ConstructionPath> paths, int position)
			throws ConstructionFailedException, ClassNotFoundException {

		Collections.sort(paths, new Comparator<ConstructionPath>() {
			@Override
			public int compare(ConstructionPath o1, ConstructionPath o2) {
				return o1.size() - o2.size();
			}
		});

		/**
		 * track what variable reference can be reused.
		 */
		Map<DepVariable, VariableReference> map = new HashMap<>();

		for (ConstructionPath path : paths) {
			VariableReference parentVarRef = null;

			List<DepVariable> vars = path.getPath();
			for (int i = 0; i < vars.size(); i++) {
				DepVariable var = vars.get(i);
				VariableReference codeVar = map.get(var);
				if (codeVar != null) {
					parentVarRef = codeVar;
					continue;
				}

				if (var.getType() == DepVariable.STATIC_FIELD) {
					parentVarRef = generateFieldStatement(test, position, var, parentVarRef, true);
				} else if (var.getType() == DepVariable.PARAMETER) {
					String castSubClass = checkClass(var, path);
					parentVarRef = generateParameterStatement(test, position, var, parentVarRef, castSubClass);

				} else if (var.getType() == DepVariable.INSTANCE_FIELD) {
					if(parentVarRef == null) {
						break;
					}
					
					parentVarRef = generateFieldStatement(test, position, var, parentVarRef, false);
				} else if (var.getType() == DepVariable.OTHER) {
					/**
					 * TODO: need to handle other cases than method call in the future.
					 */
					int methodPos = findTargetMethodCallStatement(test).getPosition();
					parentVarRef = generateOtherStatement(test, methodPos, var, parentVarRef);
				} else if (var.getType() == DepVariable.THIS) {
					MethodStatement mStat = findTargetMethodCallStatement(test);
					parentVarRef = mStat.getCallee();
				}

				if (parentVarRef != null) {
					map.put(var, parentVarRef);
				}

				MethodStatement mStat = findTargetMethodCallStatement(test);
				position = mStat.getPosition() - 1;
			}
		}

	}

	private String checkClass(DepVariable var, ConstructionPath path) throws ClassNotFoundException {
		String potentialCastType = null;
		for (DepVariable v : path.getPath()) {
			if (v.getInstruction().toString().contains("CHECKCAST")) {
				BytecodeInstruction ins = v.getInstruction();
				AbstractInsnNode insNode = ins.getASMNode();
				if (insNode instanceof TypeInsnNode) {
					TypeInsnNode tNode = (TypeInsnNode) insNode;
					String classType = tNode.desc;
					potentialCastType = org.objectweb.asm.Type.getObjectType(classType).getClassName();

					ActualControlFlowGraph actualControlFlowGraph = var.getInstruction().getActualCFG();
					int paramOrder = var.getParamOrder();

					String methodSig = actualControlFlowGraph.getMethodName();
					String[] parameters = extractParameter(methodSig);
					String paramType = parameters[paramOrder - 1];

					if (isCompatible(paramType, potentialCastType)) {
						return potentialCastType;
					}
				}
			}
		}
		System.currentTimeMillis();

		return null;
	}

	private VariableReference generateOtherStatement(TestCase test, int position, DepVariable var,
			VariableReference parentVarRef) {
		if (var.getInstruction().getASMNode() instanceof MethodInsnNode) {
			VariableReference returnValue = generateMethodCall(test, position, var, parentVarRef);
			return returnValue;
		}

		return parentVarRef;
	}

	private VariableReference generateMethodCall(TestCase test, int position, DepVariable var,
			VariableReference parentVarRef) {
		try {
			MethodInsnNode methodNode = ((MethodInsnNode) var.getInstruction().getASMNode());
			String owner = methodNode.owner;
			String fieldOwner = owner.replace("/", ".");
			String fullName = methodNode.name + methodNode.desc;
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
			if (fieldDeclaringClass.isInterface() || Modifier.isAbstract(fieldDeclaringClass.getModifiers())) {
				return parentVarRef;
			}
			org.objectweb.asm.Type[] types = org.objectweb.asm.Type
					.getArgumentTypes(fullName.substring(fullName.indexOf("("), fullName.length()));
			Class<?>[] paramClasses = new Class<?>[types.length];
			int index = 0;
			for (org.objectweb.asm.Type type : types) {
				Class<?> paramClass = getClassForType(type);
				paramClasses[index++] = paramClass;
			}

			if (!fullName.contains("<init>")) {
				Method call = fieldDeclaringClass.getMethod(fullName.substring(0, fullName.indexOf("(")), paramClasses);

				VariableReference calleeVarRef;
				if (parentVarRef == null) {
					calleeVarRef = addConstructorForClass(test, position + 1, fieldDeclaringClass.getName());
				} else {
					calleeVarRef = parentVarRef;
				}

				GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
				VariableReference varRef = testFactory.addMethodFor(test, calleeVarRef, genericMethod,
						calleeVarRef.getStPosition() + 1);
				return varRef;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parentVarRef;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * set the field into the parentVarRef
	 * 
	 * @param test
	 * @param position
	 * @param var
	 * @param parentVarRef
	 * @param isStatic
	 * @return
	 */
	private VariableReference generateFieldStatement(TestCase test, int position, DepVariable var,
			VariableReference parentVarRef, boolean isStatic) {
		FieldInsnNode fieldNode = (FieldInsnNode) var.getInstruction().getASMNode();
		String desc = fieldNode.desc;
		String owner = fieldNode.owner;
		String fieldName = fieldNode.name;
		String fieldOwner = owner.replace("/", ".");

		if (parentVarRef != null) {
			String parentType = parentVarRef.getType().getTypeName();
			if (!parentType.equals(fieldOwner)) {
				return parentVarRef;
			}
		}

		AbstractStatement stmt = null;
		try {
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
			// Ignore "this" check
			Field field = searchForField(fieldDeclaringClass, fieldName);
			GenericField genericField = new GenericField(field, fieldDeclaringClass);
			int fieldModifiers = field.getModifiers();

			if (Modifier.isPublic(fieldModifiers)) {
				if (CollectionUtil.existIn(desc, "Z", "B", "C", "S", "I", "J", "F", "D")) {
					stmt = addStatementToSetPrimitiveField(test, position + 1, desc, genericField, parentVarRef);
				} else {
					stmt = addStatementToSetNonPrimitiveField(test, position + 1, desc, genericField, parentVarRef);
				}

				if (stmt != null && stmt.getReturnValue() != null) {
					return stmt.getReturnValue();
				}
			} else {
				registerAllMethods(fieldDeclaringClass);
				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getInstructionsIn(fieldDeclaringClass.getName());
				if (insList != null) {
					String fieldWriteOpcode = "PUTSTATIC";
					if (!isStatic) {
						fieldWriteOpcode = "PUTFIELD";
					}

					Method potentialSetter = searchForRelevantMethod(owner, fieldName, fieldDeclaringClass, insList,
							fieldWriteOpcode);
					System.currentTimeMillis();

					if (potentialSetter != null) {
						GenericMethod gMethod = new GenericMethod(potentialSetter, potentialSetter.getDeclaringClass());
						VariableReference newParentVarRef = null;
						if (parentVarRef == null) {
							newParentVarRef = testFactory.addMethod(test, gMethod, position, 2);
						}
						else {
							newParentVarRef = testFactory.addMethodFor(test, parentVarRef, gMethod, parentVarRef.getStPosition() + 1);
						}
						return newParentVarRef;
					}

					if (!isPrimitiveType(var.getInstruction().getFieldType())) {
						Method potentialGetter = searchForPotentialGetter(owner, var.getInstruction(),
								fieldDeclaringClass);
						if (potentialGetter != null) {
							GenericMethod gMethod = new GenericMethod(potentialGetter,
									potentialGetter.getDeclaringClass());
							VariableReference newParentVarRef = null;
							if (parentVarRef == null) {
								newParentVarRef = testFactory.addMethod(test, gMethod, position, 2);
							}
							else {
								newParentVarRef = testFactory.addMethodFor(test, parentVarRef, gMethod, parentVarRef.getStPosition() + 1);
							}
//							createOrReuseObjectVariable(test, position+1, 2, newParentVarRef, false, false);
							return newParentVarRef;
						}
					}

					Constructor constructor = searchForPotentialConstructor(owner, fieldName, fieldDeclaringClass,
							insList, fieldWriteOpcode);
					/**
					 * the constructor cannot return the field, so we stop here.
					 */
					if (constructor != null) {
						if (!isCalledConstructor(test, parentVarRef, constructor)) {
							GenericConstructor gConstructor = new GenericConstructor(constructor,
									constructor.getDeclaringClass());
							VariableReference newParentVarRef = testFactory.addConstructor(test, gConstructor,
									parentVarRef.getStPosition() + 1, 2);

							for (int i = 0; i < test.size(); i++) {
								Statement stat = test.getStatement(i);
								if (newParentVarRef.getStPosition() < stat.getPosition()) {
									if (stat.references(parentVarRef)) {
										stat.replace(parentVarRef, newParentVarRef);
									}
								}
							}
						}
					}

					return parentVarRef;
				}
			}

		} catch (ClassNotFoundException | SecurityException | ConstructionFailedException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		return parentVarRef;
	}

	private boolean isCompatible(String parentType, String subType) {
		try {
			Class<?> parentClass = Class.forName(parentType, true,
					TestGenerationContext.getInstance().getClassLoaderForSUT());
			Class<?> subClass = Class.forName(subType, true,
					TestGenerationContext.getInstance().getClassLoaderForSUT());
			return parentClass.isAssignableFrom(subClass);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	private String getFieldName(BytecodeInstruction ins) {
		if (ins.getASMNode().getType() == AbstractInsnNode.FIELD_INSN) {
			FieldInsnNode fNode = (FieldInsnNode) ins.getASMNode();
			return fNode.name;
		}

		return null;
	}

	private Method searchForPotentialGetter(String owner, BytecodeInstruction bytecodeInstruction,
			Class<?> fieldDeclaringClass) {
		Set<Method> targetMethods = new HashSet<>();

		for (Method method : fieldDeclaringClass.getMethods()) {
			org.objectweb.asm.Type className = org.objectweb.asm.Type.getType(bytecodeInstruction.getFieldType());
			if (method.getReturnType().getCanonicalName().equals(className.getClassName())) {

				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getInstructionsIn(fieldDeclaringClass.getCanonicalName(),
								method.getName() + MethodUtil.getSignature(method));

				if (insList == null) {
					continue;
				}

				for (BytecodeInstruction ins : insList) {
					String f = getFieldName(ins);
					String fieldName = getFieldName(bytecodeInstruction);
					if (f != null && fieldName != null && f.equals(fieldName)) {
						targetMethods.add(method);
						break;
					}
				}

			}
		}

		return Randomness.choice(targetMethods);
	}

	private boolean isPrimitiveType(String fieldType) {
		boolean flag = !fieldType.contains("L") && !fieldType.contains(";");
		return flag;
	}
	

	@SuppressWarnings("rawtypes")
	private boolean isCalledConstructor(TestCase test, VariableReference parentVarRef, Constructor constructor) {
		
		if(parentVarRef == null) {
			return false;
		}
		
		Statement stat = test.getStatement(parentVarRef.getStPosition());
		if(stat instanceof ConstructorStatement) {
			ConstructorStatement s = (ConstructorStatement)stat;
			Constructor calledConstructor = s.getConstructor().getConstructor();
			
			return calledConstructor.equals(constructor);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private void registerAllMethods(Class<?> fieldDeclaringClass) {
		try {
			for(Method method: fieldDeclaringClass.getDeclaredMethods()) {
				String methodName = method.getName() + MethodUtil.getSignature(method);
				MethodUtil.registerMethod(fieldDeclaringClass, methodName);
			}
			
			for(Constructor constructor: fieldDeclaringClass.getDeclaredConstructors()) {
				String constructorName = "<init>" + MethodUtil.getSignature(constructor);
				MethodUtil.registerMethod(fieldDeclaringClass, constructorName);
			}
		}
		catch(Exception e) {
			
		}
		
	}

	
	private Field searchForField(Class<?> fieldDeclaringClass, String fieldName) {
		try {
			Field field = fieldDeclaringClass.getDeclaredField(fieldName);
			return field;
		} catch (NoSuchFieldException e) {
			if (fieldDeclaringClass.getSuperclass() != null) {
				return searchForField(fieldDeclaringClass.getSuperclass(), fieldName);
			}
		}
		return null;
	}

	/**
	 * parameter statement is supposed to be used only in the target method invocation
	 * @param test
	 * @param position
	 * @param var
	 * @param parentVarRef
	 * @param castSubClass 
	 * @return
	 * @throws ConstructionFailedException
	 * @throws ClassNotFoundException 
	 */
	private VariableReference generateParameterStatement(TestCase test, int position, DepVariable var, VariableReference parentVarRef, String castSubClass) 
			throws ConstructionFailedException, ClassNotFoundException {
		VariableReference paramRef = generateParameter(test, position, var, castSubClass);
		
		if (paramRef == null) {
			return parentVarRef;
		}
		
		MethodStatement targetStatement = findTargetMethodCallStatement(test);
		if(targetStatement != null) {
			VariableReference oldParamRef = targetStatement.getParameterReferences().get(var.getParamOrder()-1);
			targetStatement.replace(oldParamRef, paramRef);			
		}
		
		return paramRef;
	}

	private MethodStatement findTargetMethodCallStatement(TestCase test) {
		for(int i=0; i<test.size(); i++) {
			Statement stat = test.getStatement(i);
			if(stat instanceof MethodStatement) {
				MethodStatement methodStat = (MethodStatement)stat;
				if(methodStat.getMethod().getNameWithDescriptor().equals(Properties.TARGET_METHOD)) {
					return methodStat;
				}
			}
		}
		
		return null;
	}

	private VariableReference generateParameter(TestCase test, int position, DepVariable var, String castSubClass)
			throws ConstructionFailedException {
		
		String paramType = castSubClass;
		if(paramType == null) {
			ActualControlFlowGraph actualControlFlowGraph = var.getInstruction().getActualCFG();
			int paramOrder = var.getParamOrder();
			
			String methodSig = actualControlFlowGraph.getMethodName();
			String[] parameters = extractParameter(methodSig);
			paramType = parameters[paramOrder-1];
		}
		
		Class<?> paramClass;
		try {
			paramClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(paramType);
		} catch (ClassNotFoundException e) {
			return null;
		}
		GenericClass paramDeclaringClazz = new GenericClass(paramClass);
		VariableReference paramRef = null;
		Constructor<?> constructor = Randomness.choice(paramDeclaringClazz.getRawClass().getConstructors());
		if (constructor != null) {
			GenericConstructor gc = new GenericConstructor(constructor, paramDeclaringClazz);
			paramRef = testFactory.addConstructor(test, gc, position, 2);
		} 

		return paramRef;
	}

	private String[] extractParameter(String methodSig) {
		String parameters = methodSig.substring(methodSig.indexOf("(") + 1, methodSig.indexOf(")"));
		String[] args = parameters.split(";");
		for(int i=0; i< args.length; i++) {
			args[i] = args[i].replace("/", ".").substring(1, args[i].length());
		}
		return args;
	}

	private Method searchForRelevantMethod(String owner, String fieldName, Class<?> fieldDeclaringClass, 
			List<BytecodeInstruction> insList, String operation) 
			throws NoSuchMethodException, ClassNotFoundException {
		Set<Method> targetMethods = new HashSet<>();
		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNodeString().contains(operation)) {
				FieldInsnNode insnNode = ((FieldInsnNode) ins.getASMNode());
				String tmpName = insnNode.name;
				String tmpOwner = insnNode.owner;
				if (tmpName.equals(fieldName) && tmpOwner.equals(owner)) {
					String methodName = ins.getMethodName();
					org.objectweb.asm.Type[] types = org.objectweb.asm.Type
							.getArgumentTypes(methodName.substring(methodName.indexOf("("), methodName.length()));
					Class<?>[] paramClasses = new Class<?>[types.length];
					int index = 0;
					for (org.objectweb.asm.Type type : types) {
						
						Class<?> paramClass = getClassForType(type);
						paramClasses[index++] = paramClass;
					}
					
					if(!methodName.contains("<init>") && 
							!(methodName.equals(Properties.TARGET_METHOD) && ins.getClassName().equals(Properties.TARGET_CLASS))) {
						Method targetMethod = fieldDeclaringClass.getDeclaredMethod(methodName.substring(0, methodName.indexOf("(")), paramClasses);
						targetMethods.add(targetMethod);						
					}
				}
			}
		}
		
		return Randomness.choice(targetMethods);
	}
	
	
	@SuppressWarnings("rawtypes")
	private Constructor searchForPotentialConstructor(String owner, String fieldName, Class<?> fieldDeclaringClass, List<BytecodeInstruction> insList, String operation) 
			throws NoSuchMethodException, ClassNotFoundException {
		Set<Constructor> targetConstructors = new HashSet<>();
		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNodeString().contains(operation)) {
				FieldInsnNode insnNode = ((FieldInsnNode) ins.getASMNode());
				String tmpName = insnNode.name;
				String tmpOwner = insnNode.owner;
				if (tmpName.equals(fieldName) && tmpOwner.equals(owner)) {
					String fullName = ins.getMethodName();
					org.objectweb.asm.Type[] types = org.objectweb.asm.Type
							.getArgumentTypes(fullName.substring(fullName.indexOf("("), fullName.length()));
					Class<?>[] paramClasses = new Class<?>[types.length];
					int index = 0;
					for (org.objectweb.asm.Type type : types) {
						
						Class<?> paramClass = getClassForType(type);
						paramClasses[index++] = paramClass;
					}
					
					if(fullName.contains("<init>")) {
						Constructor constructor = fieldDeclaringClass.getDeclaredConstructor(paramClasses);
						targetConstructors.add(constructor);					
					}
				}
			}
		}
		
		return Randomness.choice(targetConstructors);
	}
	
	private VariableReference addNullStatement(TestCase test, int position, Type type) {	
		NullStatement nullStatement = new NullStatement(test, type);
		VariableReference reference = null;
		try {
			reference = testFactory.addPrimitive(test, nullStatement, position);
		} catch (ConstructionFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		test.addStatement(nullStatement, position);	
		return reference;
	}

	private AbstractStatement addStatementToSetNonPrimitiveField(TestCase test, int position, String desc,
			GenericField genericField, VariableReference parentVarRef) throws ConstructionFailedException {
		VariableReference constructorVarRef = addConstructorForClass(test, position, desc);
		if (constructorVarRef == null) {
			return null;
		}
		
		if(genericField.isFinal()) {
			return null;
		}
		
		FieldReference fieldVar = null;
		if (genericField.isStatic()) {
			fieldVar = new FieldReference(test, genericField);
			AbstractStatement stmt = new AssignmentStatement(test, fieldVar, constructorVarRef);
			test.addStatement(stmt, 0);			
			return stmt;
		} else {
			if (parentVarRef != null) {
				fieldVar = new FieldReference(test, genericField, parentVarRef);
			} else {
				VariableReference var = addConstructorForClass(test, position,
						genericField.getDeclaringClass().getName());
				fieldVar = new FieldReference(test, genericField, var);
			}
			AbstractStatement stmt = new AssignmentStatement(test, fieldVar, constructorVarRef);
			test.addStatement(stmt, fieldVar.getStPosition() + 1);		
			
			return stmt;
		}
		
	}
	
	private VariableReference addPrimitiveStatement(TestCase test, int position, String desc) {
		try {
			NumericalPrimitiveStatement<?> primStatement = createNewPrimitiveStatement(test, desc);
			VariableReference varRef = testFactory.addPrimitive(test, primStatement, position);
			return varRef;
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private AbstractStatement addStatementToSetPrimitiveField(TestCase test, int position, String desc,
			GenericField genericField, VariableReference parentVarRef) throws ConstructionFailedException {
		FieldReference fieldVar = null;
		if (genericField.isStatic()) {
			fieldVar = new FieldReference(test, genericField);
		} else {
			if (parentVarRef != null) {
				fieldVar = new FieldReference(test, genericField, parentVarRef);
			} else {
				VariableReference var = addConstructorForClass(test, position,
						genericField.getDeclaringClass().getName());
				fieldVar = new FieldReference(test, genericField, var);
			}
		}

		VariableReference primVarRef = addPrimitiveStatement(test, fieldVar.getStPosition() + 1, desc);
		AbstractStatement stmt = new AssignmentStatement(test, fieldVar, primVarRef);
		test.addStatement(stmt, primVarRef.getStPosition() + 1);
		return stmt;
	}

	private VariableReference addConstructorForClass(TestCase test, int position, String desc) throws ConstructionFailedException {
		try {
			String fieldType;
			if (desc.contains("/")) {
				fieldType = desc.replace("/", ".").substring(desc.indexOf("L") + 1, desc.length() - 1);
			} else {
				fieldType = desc;
			}
			Class<?> fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(fieldType);
			
			if(fieldClass.isInterface() || Modifier.isAbstract(fieldClass.getModifiers())) {
				Set<String> subclasses = DependencyAnalysis.getInheritanceTree().getSubclasses(fieldClass.getCanonicalName());
				String subclass = Randomness.choice(subclasses);
				fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(subclass);
			}
			
			Constructor<?> constructor = Randomness.choice(fieldClass.getConstructors());
			if (constructor != null) {
				GenericConstructor genericConstructor = new GenericConstructor(constructor, fieldClass);
				VariableReference variableReference = testFactory.addConstructor(test, genericConstructor, position+1, 2);
				return variableReference;
			}
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Class<?> getClassForType(org.objectweb.asm.Type type) {
		if (type == org.objectweb.asm.Type.BOOLEAN_TYPE) {
			return boolean.class;
		}
		else if (type == org.objectweb.asm.Type.BYTE_TYPE) {
			return byte.class;
		}
		else if (type == org.objectweb.asm.Type.CHAR_TYPE) {
			return char.class;
		}
		else if (type == org.objectweb.asm.Type.SHORT_TYPE) {
			return short.class;
		}
		else if (type == org.objectweb.asm.Type.INT_TYPE) {
			return int.class;
		}
		else if (type == org.objectweb.asm.Type.LONG_TYPE) {
			return long.class;
		}
		else if (type == org.objectweb.asm.Type.FLOAT_TYPE) {
			return float.class;
		}
		else if (type == org.objectweb.asm.Type.DOUBLE_TYPE) {
			return double.class;
		}
		else {
			try {
				String className = type.getClassName();
				if(type.getSort() != org.objectweb.asm.Type.ARRAY) {
					Class<?> clazz = TestGenerationContext.getInstance().getClassLoaderForSUT()
							.loadClass(className);					
					return clazz;
				}
				else {
					StringBuffer buffer = new StringBuffer();
					org.objectweb.asm.Type elementType = type.getElementType();
					String arrayString = extractedArrayString(type);
					if(elementType.getSort() <= 8) {
						className = transfer(elementType.getClassName());
						buffer.append(arrayString);
						buffer.append(className);
					}
					else {
						className = elementType.getClassName();	
						buffer.append(arrayString);
						buffer.append("L");
						buffer.append(className);
						buffer.append(";");
					}
					String fullName = buffer.toString();
					Class<?> clazzArray = Class.forName(fullName, true, TestGenerationContext.getInstance().getClassLoaderForSUT());
					return clazzArray;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private String extractedArrayString(org.objectweb.asm.Type type) {
		int arrayLength = type.getDimensions();
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<arrayLength; i++) {
			buffer.append("[");
		}
		return buffer.toString();
	}

	@SuppressWarnings("rawtypes")
	private NumericalPrimitiveStatement createNewPrimitiveStatement(TestCase test, String desc) {
		switch (desc) {
		case "Z":
		case "boolean":
			return new BooleanPrimitiveStatement(test);
		case "B":
		case "byte":
			return new BytePrimitiveStatement(test);
		case "C":
		case "char":
			return new CharPrimitiveStatement(test);
		case "S":
		case "short":
			return new ShortPrimitiveStatement(test);
		case "I":
		case "int":
			return new IntPrimitiveStatement(test);
		case "J":
		case "long":
			return new LongPrimitiveStatement(test);
		case "F":
		case "float":
			return new FloatPrimitiveStatement(test);
		case "D":
		case "double":
			return new DoublePrimitiveStatement(test);
		default:
			return null;
		}
	}
	
	private String transfer(String desc) {
		switch (desc) {
		case "boolean":
			return "Z";
		case "byte":
			return "B";
		case "char":
			return "C";
		case "short":
			return "S";
		case "int":
			return "I";
		case "long":
			return "J";
		case "float":
			return "F";
		case "double":
			return "D";
		default:
			return null;
		}
	}
	
	
}
