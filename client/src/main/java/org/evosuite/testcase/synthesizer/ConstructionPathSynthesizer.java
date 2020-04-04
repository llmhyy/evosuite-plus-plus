package org.evosuite.testcase.synthesizer;

import static guru.nidi.graphviz.model.Factory.node;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.graphs.dataflow.GraphVisualizer;
import org.evosuite.runtime.System;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AbstractStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
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
import org.evosuite.testcase.variable.ArrayIndex;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericField;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import guru.nidi.graphviz.model.LinkSource;

public class ConstructionPathSynthesizer {

	private TestFactory testFactory;

	public ConstructionPathSynthesizer(TestFactory testFactory) {
		super();
		this.testFactory = testFactory;
	}

	private static void collectLinks(DepVariable source, List<LinkSource> links) {
		
		List<DepVariable>[] relations = source.getRelations();
		for(int i=0; i<relations.length; i++) {
			List<DepVariable> child = relations[i];
			
			if(child == null) continue;
			
			for(DepVariable target: child) {
				
				guru.nidi.graphviz.model.Node n  = node(source.getUniqueLabel()).link(node(target.getUniqueLabel()));
				
				if(!links.contains(n)) {
					links.add(n);
					collectLinks(target, links);					
				}
			}
		}
		
	}
	
	private PartialGraph constructPartialComputationGraph(Branch b) {
		PartialGraph graph = new PartialGraph();
		
		Map<Branch, Set<DepVariable>> map = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
		Set<DepVariable> variables = map.get(b);
		
		HashSet<DepVariable> roots = new HashSet<DepVariable>();
		for(DepVariable source: variables) {
			Map<DepVariable, ArrayList<DepVariable>> rootInfo = source.getRootVars();
			for(DepVariable root: rootInfo.keySet()) {
				
				if((root.referenceToThis() || root.isParameter() || root.isStaticField()) 
						&& root.getInstruction().getMethodName().equals(Properties.TARGET_METHOD)) {
					roots.add(root);
					
					ArrayList<DepVariable> path = rootInfo.get(root);
					for(int i=0; i<path.size()-1; i++) {
						DepVariableWrapper child = graph.fetch(path.get(i));
						DepVariableWrapper parent = graph.fetch(path.get(i+1));
						
						child.addParent(parent);
						parent.addChild(child);
					}
				}
			
					
			}
		}
		
		System.currentTimeMillis();
		
		return graph;
	}
	
	public void constructDifficultObjectStatement(TestCase test, Branch b)
			throws ConstructionFailedException, ClassNotFoundException {

		PartialGraph partialGraph = constructPartialComputationGraph(b);
		System.currentTimeMillis();
		
		GraphVisualizer.visualizeComputationGraph(b);
		GraphVisualizer.visualizeComputationGraph(partialGraph);
		
		List<DepVariableWrapper> topLayer = partialGraph.getTopLayer();
		
		/**
		 * track what variable reference can be reused.
		 */
		Map<DepVariable, VariableReference> map = new HashMap<>();

		/**
		 * use BFS on partial graph to generate test code.
		 */
		Queue<DepVariableWrapper> queue = new ArrayDeque<>(topLayer);
		
		Set<DepVariableWrapper> visited = new HashSet<>();
		System.currentTimeMillis();
		
		while(!queue.isEmpty()) {
			DepVariableWrapper node = queue.remove();
			visited.add(node);
			
			enhanceTestStatement(test, map, node);
			for(DepVariableWrapper child: node.children) {
				if(!visited.contains(child)) {
					queue.add(child);					
				}
			}
		}
	}

	private boolean enhanceTestStatement(TestCase test, Map<DepVariable, VariableReference> map,
			DepVariableWrapper node) throws ClassNotFoundException, ConstructionFailedException {
		VariableReference targetObject = null;
		DepVariable var = node.var;
		VariableReference codeVar = map.get(var);
		if (codeVar != null) {
			targetObject = codeVar;
		}

		boolean isLeaf = node.children.isEmpty();

		if (var.getType() == DepVariable.STATIC_FIELD) {
			targetObject = generateFieldStatement(test, var, isLeaf, targetObject);
		} else if (var.getType() == DepVariable.PARAMETER) {
			String castSubClass = checkClass(node);
			targetObject = generateParameterStatement(test, var, targetObject, castSubClass);
		} else if (var.getType() == DepVariable.INSTANCE_FIELD) {
			if (targetObject == null) {
				return false;
			}
			targetObject = generateFieldStatement(test, var, isLeaf, targetObject);
		} else if (var.getType() == DepVariable.OTHER) {
			/**
			 * FIXME: need to handle other cases than method call in the future.
			 */
			int methodPos = findTargetMethodCallStatement(test).getPosition();
			targetObject = generateOtherStatement(test, methodPos, var, targetObject);
		} else if (var.getType() == DepVariable.THIS) {
			MethodStatement mStat = findTargetMethodCallStatement(test);
			targetObject = mStat.getCallee();
		} else if (var.getType() == DepVariable.ARRAY_ELEMENT) {
			targetObject = generateArrayElementStatement(test, var, isLeaf, targetObject);
			System.currentTimeMillis();
		}

		if (targetObject != null) {
			map.put(var, targetObject);
		}
		
		return true;
	}

	private VariableReference generateArrayElementStatement(TestCase test, DepVariable var,
			boolean isLeaf, VariableReference parentVarRef) {
		/**
		 *  FIXME ziheng, we need to 
		 *  (1) handle primitive type, 
		 *  (2) search for method call which can set the array element,
		 *  (3) reuse*.
		 */
		
		if(parentVarRef instanceof ArrayReference) {
			ArrayReference arrayRef = (ArrayReference)parentVarRef;
			int index = Randomness.nextInt(10);
			ArrayIndex arrayIndex = new ArrayIndex(test, arrayRef, index);
			
			
			VariableReference varRef = createVariable(test, arrayRef);
			AssignmentStatement assign = new AssignmentStatement(test, arrayIndex, varRef);
			test.addStatement(assign, varRef.getStPosition()+1);
			System.currentTimeMillis();
			
			return assign.getReturnValue();
			
//			if(isLeaf) {
//				//generate setter method. 
//			}
//			else {
//				//generate getter method.
//			}
		}
		
		
		return null;
	}

	private VariableReference createVariable(TestCase test, ArrayReference arrayRef) {
		Class<?> clazz = arrayRef.getComponentClass();
		Constructor<?> constructor = clazz.getConstructors()[0];
		GenericConstructor gConstructor = new GenericConstructor(constructor,
				constructor.getDeclaringClass());
		try {
			VariableReference returnedVar = testFactory.addConstructor(test, gConstructor,
					arrayRef.getStPosition() + 1, 2);
			return returnedVar;
			
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	private String checkClass(DepVariableWrapper node) throws ClassNotFoundException {
		DepVariable var = node.var;
		
		String potentialCastType = null;
		
		DepVariableWrapper parent = node.parents.iterator().next();
		while (parent != null) {
			DepVariable v = parent.var;
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
			
			parent = parent.parents.iterator().next();
		}

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
	private VariableReference generateFieldStatement(TestCase test, DepVariable var, boolean isLeaf,
			VariableReference targetObjectReference) {
		FieldInsnNode fieldNode = (FieldInsnNode) var.getInstruction().getASMNode();
		String desc = fieldNode.desc;
		String fieldOwner = fieldNode.owner.replace("/", ".");
		String fieldName = fieldNode.name;

		AbstractStatement stmt = null;
		
		if (targetObjectReference != null) {
			String parentType = targetObjectReference.getClassName();
			if (!isCompatible(fieldOwner, parentType)) {
				return targetObjectReference;
			}
		}

		try {
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
			registerAllMethods(fieldDeclaringClass);
			
			/**
			 * If field is not found in current declaring class, we search recursively for
			 * its superclass However, for simplicity, we only search for setter, getter,
			 * constructor in current class instead of the real declaring class of this
			 * field
			 */
			Field field = searchForField(fieldDeclaringClass, fieldName);
			
			/**
			 * if the field is leaf, check if there is setter in the testcase
			 * if the field is not leaf, check if there is getter in the testcase
			 * if found, stop here
			 */
			VariableReference usedFieldInTest = isLeaf
					? searchRelevantFieldWritingReference(test, field, targetObjectReference)
					: searchRelevantFieldReadingReference(test, field, targetObjectReference);
			if (usedFieldInTest != null) {
				return isLeaf ? null : usedFieldInTest;
			}

			/**
			 * now we try to generate the relevant statement in the test case.
			 */
			GenericField genericField = new GenericField(field, field.getDeclaringClass());
			int fieldModifiers = field.getModifiers();

			/**
			 * deal with public field
			 */
			if (Modifier.isPublic(fieldModifiers) || fieldModifiers == 0) {
				if (CollectionUtil.existIn(desc, "Z", "B", "C", "S", "I", "J", "F", "D")) {
					stmt = addStatementToSetPrimitiveField(test, targetObjectReference.getStPosition() + 1, desc,
							genericField, targetObjectReference);
				} else {
					stmt = addStatementToSetNonPrimitiveField(test, targetObjectReference.getStPosition() + 1, desc,
							genericField, targetObjectReference);
				}

				if (stmt != null && stmt.getReturnValue() != null) {
					return stmt.getReturnValue();
				}
				
				return null;
			}

			/**
			 * deal with non-public field
			 */
			if (!isLeaf) {
				/**
				 * generate getter in current class
				 */
				// FIXME ziheng: we need to consider cascade method call.
				Method getter = searchForPotentialGetterInClass(fieldDeclaringClass, field);
				if (getter != null) {
					VariableReference newParentVarRef = null;
					GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
					if (targetObjectReference == null) {
						MethodStatement mStat = findTargetMethodCallStatement(test);
						newParentVarRef = testFactory.addMethod(test, gMethod, mStat.getPosition() - 1, 2);
					} else {
						newParentVarRef = testFactory.addMethodFor(test, targetObjectReference, gMethod,
								targetObjectReference.getStPosition() + 1);
					}
					return newParentVarRef;
				}
				return null;
			} 
			else {
				/**
				 * generate setter in current class
				 */
				List<BytecodeInstruction> insList = BytecodeInstructionPool
						.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getInstructionsIn(fieldDeclaringClass.getName());

				if (insList == null) {
					return null;
				}

				// TODO ziheng: we need to consider cascade method call.
				Map.Entry<Method, Parameter> entry = searchForPotentialSetterInClass(field, fieldNode.owner,
						fieldDeclaringClass, insList);
				if (entry != null) {
					Method setter = entry.getKey();
					GenericMethod gMethod = new GenericMethod(setter, setter.getDeclaringClass());
					if (targetObjectReference == null) {
						MethodStatement mStat = findTargetMethodCallStatement(test);
						testFactory.addMethod(test, gMethod, mStat.getPosition() - 1, 2);
					} else {
						testFactory.addMethodFor(test, targetObjectReference, gMethod,
								targetObjectReference.getStPosition() + 1);
					}
					return null;
				}

				/**
				 * deal with the case when the class has neither getter nor setter.
				 */
				// FIXME ziheng: we need to consider cascade method call.
				Map.Entry<Constructor, Parameter> constructorEntry = searchForPotentialConstructor(field,
						fieldNode.owner, fieldDeclaringClass, insList);
				if (constructorEntry != null) {
					Constructor constructor = constructorEntry.getKey();
//					if (!isCalledConstructor(test, targetObjectReference, constructor)) {
						GenericConstructor gConstructor = new GenericConstructor(constructor,
								constructor.getDeclaringClass());
						VariableReference returnedVar = testFactory.addConstructor(test, gConstructor,
								targetObjectReference.getStPosition() + 1, 2);

						for (int i = 0; i < test.size(); i++) {
							Statement stat = test.getStatement(i);
							if (returnedVar.getStPosition() < stat.getPosition()) {
								if (stat.references(targetObjectReference)) {
									stat.replace(targetObjectReference, returnedVar);
								}
							}
						}
						return null;
//					}
				}

				return null;

			}

		} catch (ClassNotFoundException | SecurityException | ConstructionFailedException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}

	}

	private VariableReference searchRelevantFieldReadingReference(TestCase test, Field field,
			VariableReference targetObject) {
		List<VariableReference> relevantRefs = new ArrayList<VariableReference>();

		if (targetObject != null) {
			for (int i = 0; i < test.size(); i++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof MethodStatement) {
					MethodStatement mStat = (MethodStatement) stat;
					VariableReference ref = mStat.getCallee();
					if (ref != null && ref.equals(targetObject)) {
						boolean isValidGetter = checkValidGetterInTest(mStat.getMethod().getMethod(), field);
						if (isValidGetter) {
							relevantRefs.add(mStat.getReturnValue());
						}
					}
				}
			}
		}

		/**
		 * FIXME, ziheng: we need to provide some priority for those parameters here.
		 */
		if (relevantRefs.isEmpty())
			return null;

		VariableReference ref = Randomness.choice(relevantRefs);
		return ref;
	}

	private VariableReference retrieveParamReference4Field(TestCase test, Parameter parameter, Executable executable,
			Statement statement) {

		if (!(statement instanceof EntityWithParametersStatement)) {
			return null;
		}

		EntityWithParametersStatement newStatement = (EntityWithParametersStatement) statement;
		for (int i = 0; i < executable.getParameters().length; i++) {
			Parameter setterParam = executable.getParameters()[i];
			if (parameter.equals(setterParam)) {
				List<VariableReference> paramRefs = newStatement.getParameterReferences();
				return paramRefs.get(i);
			}
		}

		/**
		 * FIXME Ziheng, see whether here is reachable
		 */
		// assert false;

		return null;
	}

	/**
	 * @author linyun: Given a field, we should check the cascade call to write such
	 *         a method. The more controllable a variable, the more likely we return
	 *         the variable.
	 * 
	 * @param test
	 * @param field
	 * @param targetObject
	 * @return
	 */
	private VariableReference searchRelevantFieldWritingReference(TestCase test, Field field,
			VariableReference targetObject) {

		List<VariableReference> relevantRefs = new ArrayList<VariableReference>();

		if (targetObject != null) {
			/**
			 * check the variables passed as parameters to the constructor of targetObject,
			 * which are data-flow relevant to writing the field @{code field}
			 */
			Statement s = test.getStatement(targetObject.getStPosition());
			if (s instanceof NullStatement) {
				TestFactory testFactory = TestFactory.getInstance();
				testFactory.changeNullStatement(test, s);
			}

			if (s instanceof ConstructorStatement) {
				ConstructorStatement constructorStat = (ConstructorStatement) s;
				List<VariableReference> params = constructorStat.getParameterReferences();
				String className = constructorStat.getDeclaringClassName();
				String methodName = constructorStat.getMethodName() + constructorStat.getDescriptor();
				if (!params.isEmpty()) {
					List<VariableReference> paramRefs = searchRelevantParameterOfSetterInTest(params, className, methodName,
							field);
					relevantRefs.addAll(paramRefs);
				}
			}

			/**
			 * check the variables passed as parameters to the method invocation from
			 * targetObject, which are data-flow relevant to writing the field @{code field}
			 */
			for (int i = 0; i < test.size(); i++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof MethodStatement) {
					MethodStatement mStat = (MethodStatement) stat;
					VariableReference ref = mStat.getCallee();
					if (ref != null && ref.equals(targetObject)) {
						List<VariableReference> params = mStat.getParameterReferences();
						String className = mStat.getDeclaringClassName();
						String methodName = mStat.getMethodName() + mStat.getDescriptor();
						List<VariableReference> paramRefs = searchRelevantParameterOfSetterInTest(params, className, methodName,
								field);

						for (VariableReference pRef : paramRefs) {
							if (!relevantRefs.contains(pRef)) {
								relevantRefs.add(pRef);
							}
						}
					}
				}
			}
		}

		/**
		 * FIXME, ziheng: we need to provide some priority for those parameters here.
		 */
		if (relevantRefs.isEmpty())
			return null;

		VariableReference ref = Randomness.choice(relevantRefs);
		return ref;
	}

	/**
	 * opcode should always be "getfield"
	 * 
	 * @param statement
	 * @param params
	 * @param opcode
	 * @return
	 */
	private List<VariableReference> searchRelevantParameterOfSetterInTest(List<VariableReference> params, String className,
			String methodName, Field field) {
		/**
		 * get all the field setter bytecode instructions in the method. TODO: the field
		 * setter can be taken from callee method of @code{methodName}.
		 */
		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<BytecodeInstruction, List<BytecodeInstruction>>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = checkFieldSetter(className, methodName,
				field, 5, cascadingCallRelations, setterMap);
		List<VariableReference> validParams = new ArrayList<VariableReference>();
		if (fieldSetterMap.isEmpty()) {
			return validParams;
		}

		for (Entry<BytecodeInstruction, List<BytecodeInstruction>> entry : fieldSetterMap.entrySet()) {
			BytecodeInstruction setterIns = entry.getKey();
			List<BytecodeInstruction> callList = entry.getValue();
			Set<Integer> validParamPos = checkValidParameterPositions(setterIns, className, methodName, callList);
			for (Integer val : validParamPos) {
				if (val >= 0) {
					validParams.add(params.get(val));
				}
			}
			System.currentTimeMillis();
		}
		return validParams;
	}

	private Set<Integer> searchRelevantParameterOfSetterInMethod(String className, String methodName, Field field) {
		/**
		 * get all the field setter bytecode instructions in the method. TODO: the field
		 * setter can be taken from callee method of @code{methodName}.
		 */
		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<BytecodeInstruction, List<BytecodeInstruction>>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = checkFieldSetter(className, methodName,
				field, 5, cascadingCallRelations, setterMap);
//		List<VariableReference> validParams = new ArrayList<VariableReference>();
		Set<Integer> validParams = new HashSet<>();
		if (fieldSetterMap.isEmpty()) {
			return validParams;
		}

		for (Entry<BytecodeInstruction, List<BytecodeInstruction>> entry : fieldSetterMap.entrySet()) {
			BytecodeInstruction setterIns = entry.getKey();
			List<BytecodeInstruction> callList = entry.getValue();
			Set<Integer> validParamPos = checkValidParameterPositions(setterIns, className, methodName, callList);
			if (!validParamPos.isEmpty()) {
				validParams.addAll(validParamPos);
			}
		}
		return validParams;
	}
	
	private boolean checkValidGetterInTest(Method method, Field field) {
		int opcode = Modifier.isStatic(field.getModifiers()) ? Opcodes.GETSTATIC : Opcodes.GETFIELD;

		if (!method.getReturnType().getCanonicalName().equals(field.getType().getCanonicalName())) {
			return false;
		}

		ActualControlFlowGraph cfg = GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getActualCFG(method.getDeclaringClass().getCanonicalName(),
						method.getName() + MethodUtil.getSignature(method));
		if (cfg == null) {
			return false;
		}
		return checkValidGetter(field, opcode, cfg);
	}

	private boolean checkValidGetter(Field field, int opcode, ActualControlFlowGraph cfg) {
		for (BytecodeInstruction exit : cfg.getExitPoints()) {
			if (exit.isReturn()) {
				BytecodeInstruction insn = exit.getSourceOfStackInstruction(0);
				if (insn.getASMNode().getOpcode() == opcode) {
					String fieldName = getFieldName(insn);
					if (fieldName.equals(field.getName())) {
						return true;
					}
				} else if (insn.isMethodCall()) {
					ActualControlFlowGraph calledCfg = insn.getCalledActualCFG();
					if (calledCfg == null) {
						calledCfg = MethodUtil.registerMethod(insn.getCalledMethodsClass(), insn.getCalledMethod());
					}
					if (calledCfg != null) {
						return checkValidGetter(field, opcode, calledCfg);
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Output is the valid parameter position of most top method call
	 * 
	 * @param setterInstruction
	 * @param className
	 * @param methodName
	 * @param callList
	 * @return
	 */
	private Set<Integer> checkValidParameterPositions(BytecodeInstruction setterInstruction, String className,
			String methodName, List<BytecodeInstruction> callList) {
		Collections.reverse(callList);

		// only check for current method
		if (callList.isEmpty()) {
			Set<BytecodeInstruction> paramInstructions = checkCurrentParamInstructions(setterInstruction,
					new HashSet<>());
			Set<Integer> paramPositions = checkSetterParamPositions(paramInstructions);
			return paramPositions;
		}

		Set<BytecodeInstruction> paramInstructions = analyzingCascadingCall(
				new HashSet<>(Arrays.asList(setterInstruction)), callList, 0, new HashSet<>());
		Set<Integer> paramPositions = checkSetterParamPositions(paramInstructions);
		return paramPositions;
	}

	private Set<BytecodeInstruction> analyzingCascadingCall(Set<BytecodeInstruction> insns,
			List<BytecodeInstruction> callList, int index, Set<BytecodeInstruction> topParamInsns) {
		BytecodeInstruction call = callList.get(index);
		for (BytecodeInstruction ins : insns) {
			Set<BytecodeInstruction> paramInsns = checkCurrentParamInstructions(ins, new HashSet<>());
			if (paramInsns.isEmpty()) {
				return paramInsns;
			}
			boolean isValidParameter = checkParamValidation(call, paramInsns, ins);
			if (!isValidParameter) {
				return new HashSet<>();
			}
			Set<BytecodeInstruction> newParamInsns = searchForNewParameterInstruction(call, paramInsns);
			if (index == callList.size() - 1) {
				topParamInsns.addAll(newParamInsns);
			} else {
				analyzingCascadingCall(newParamInsns, callList, index + 1, topParamInsns);
			}
		}
		return topParamInsns;
	}

	/**
	 * search for new load instruction when analyzing next layer of method
	 * invocation
	 * 
	 * @param call
	 * @param paramPos
	 * @return
	 */
	private Set<BytecodeInstruction> searchForNewParameterInstruction(BytecodeInstruction call,
			Set<BytecodeInstruction> paramInsns) {
		Set<BytecodeInstruction> newParamInsns = new HashSet<>();
		Set<Integer> paramPosSet = checkSetterParamPositions(paramInsns);
		Set<BytecodeInstruction> newParams = new HashSet<>();
		for (Integer pos : paramPosSet) {
			if (call.getASMNode().getOpcode() == Opcodes.INVOKESTATIC) {
				BytecodeInstruction defIns = call.getSourceOfStackInstruction(call.getOperandNum() - pos - 1);
				newParams = checkCurrentParamInstructions(defIns, new HashSet<>());
			} else {
				BytecodeInstruction defIns = call.getSourceOfStackInstruction(call.getOperandNum() - pos - 2);
				newParams = checkCurrentParamInstructions(defIns, new HashSet<>());
			}
			if (newParams != null) {
				newParamInsns.addAll(newParams);
			}
		}
		return newParamInsns;
	}

	/**
	 * return localVariableUse instruction for parameter
	 * 
	 * @param descIns
	 * @param paramInstructionSet
	 * @return
	 */
	private Set<BytecodeInstruction> checkCurrentParamInstructions(BytecodeInstruction descIns,
			Set<BytecodeInstruction> paramInstructionSet) {
		if (descIns.isLocalVariableUse()) {
			if (isParameter(descIns)) {
				// position here similar to list index (i.e., starts from 0)
//				int pos = checkSetterParamPos(defIns);
//				paramInstructionSet.add(new Integer(pos));
				paramInstructionSet.add(descIns);
			}
		}
		for (int i = 0; i < descIns.getOperandNum(); i++) {
			BytecodeInstruction defIns = descIns.getSourceOfStackInstruction(i);
			if (defIns == null) {
				return paramInstructionSet;
			}
			if (defIns.isLocalVariableUse()) {
				if (isParameter(defIns)) {
					// position here similar to list index (i.e., starts from 0)
//					int pos = checkSetterParamPos(defIns);
//					paramInstructionSet.add(new Integer(pos));
					paramInstructionSet.add(defIns);
				}
			}
			checkCurrentParamInstructions(defIns, paramInstructionSet);
		}
		return paramInstructionSet;
	}

	public static BytecodeInstruction traceBackToMethodCall(BytecodeInstruction ins) {

		if (ins.isUse()) {
			FBranchDefUseAnalyzer.analyze(ins.getRawCFG());

			Use use = DefUsePool.getUseByInstruction(ins);
			List<Definition> defs = DefUsePool.getDefinitions(use);

			if (defs == null) {
				return null;
			}

			if (!defs.isEmpty()) {
				Definition def = defs.get(0);
				if (def.getFrame().getStackSize() > 0) {
					BytecodeInstruction call = def.getSourceOfStackInstruction(0);

					if (call != null && call.isMethodCall()) {
						return call;
					}
				}
			}
		}

		return null;
	}

	private boolean checkParamValidation(BytecodeInstruction call, Set<BytecodeInstruction> paramInsns,
			BytecodeInstruction ins) {
		if (call.getCalledMethodsArgumentCount() == 0) {
			return false;
		}
		if (!call.getCalledMethod().equals(ins.getMethodName())
				|| !call.getCalledMethodsClass().equals(ins.getClassName())) {
			return false;
		}

		Set<Integer> paramPositions = checkSetterParamPositions(paramInsns);
		for (Integer pos : paramPositions) {
			if (pos > call.getCalledMethodsArgumentCount() - 1) {
				return false;
			}
		}
		return true;
	}
//			//keep traverse
//			DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
//			defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
//			Use use = DefUseFactory.makeUse(defIns);
//			// Ignore method parameter
//			List<Definition> defs = DefUsePool.getDefinitions(use);
//			for (Definition def : CollectionUtil.nullToEmpty(defs)) {
//				if (def != null) {
//					BytecodeInstruction defInstruction = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, def.getASMNode());
//					buildInputOutputForInstruction(defInstruction, node,
//							outputVar, cfg, allLeafDepVars, visitedIns);
//				}
//			}

	private Set<Integer> checkSetterParamPositions(Set<BytecodeInstruction> paramInsns) {
		Set<Integer> paramPositions = new HashSet<>();
		for (BytecodeInstruction paramInstruction : paramInsns) {

			int slot = paramInstruction.getLocalVariableSlot();

			if (paramInstruction.getRawCFG().isStaticMethod()) {
				paramPositions.add(new Integer(slot));
			} else {
				paramPositions.add(new Integer(slot - 1));
			}
		}
		return paramPositions;
	}

	private Map<BytecodeInstruction, List<BytecodeInstruction>> checkFieldSetter(String className, String methodName,
			Field field, int depth, List<BytecodeInstruction> cascadingCallRelations,
			Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap) {
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getAllInstructionsAtMethod(className, methodName);
//		MockFramework.disable();
		if (insList == null && RuntimeInstrumentation.checkIfCanInstrument(className)) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(className, methodName);
		}

		String opcode = Modifier.isStatic(field.getModifiers()) ? "PUTSTATIC" : "PUTFIELD";
		if (insList != null) {
			for (BytecodeInstruction ins : insList) {
				if (ins.getASMNodeString().contains(opcode)) {
					AbstractInsnNode node = ins.getASMNode();
					if (node instanceof FieldInsnNode) {
						FieldInsnNode fNode = (FieldInsnNode) node;
						if (fNode.name.equals(field.getName())) {
							setterMap.put(ins, new ArrayList<>(cascadingCallRelations));
						}
					}
				} else if (ins.getASMNode() instanceof MethodInsnNode) {
					if (depth > 0) {
						MethodInsnNode mNode = (MethodInsnNode) (ins.getASMNode());

						String calledClass = mNode.owner;
						calledClass = calledClass.replace("/", ".");
						/**
						 * FIXME, ziheng: we only analyze the callee method in the same class, but we
						 * need to consider the invocation in other class.
						 */
						if (calledClass.equals(className)) {
							String calledMethodName = mNode.name + mNode.desc;

							calledClass = confirmClassNameInParentClass(calledClass, mNode);
							if (calledMethodName != null) {
								cascadingCallRelations.add(ins);
								checkFieldSetter(calledClass, calledMethodName, field, depth - 1,
										cascadingCallRelations, setterMap);
							}
						} else {
							System.currentTimeMillis();
						}
					} 
				}
			}
		}
		cascadingCallRelations.clear();

		return setterMap;

	}

	private boolean containMethod(ClassNode classNode, MethodInsnNode mNode) {

//		if(!mNode.owner.equals(classNode.name)) {
//			return false;
//		}

		for (MethodNode methodNode : classNode.methods) {
			String methodNodeName = methodNode.name + methodNode.desc;
			String methodInsName = mNode.name + mNode.desc;
			if (methodNodeName.equals(methodInsName)) {
				return true;
			}
		}

		return false;
	}

	private String confirmClassNameInParentClass(String calledClass, MethodInsnNode mNode) {

		List<String> superClassList = DependencyAnalysis.getInheritanceTree().getOrderedSuperclasses(calledClass);
		for (String superClass : superClassList) {
			ClassNode parentClassNode = DependencyAnalysis.getClassNode(superClass);
			if (containMethod(parentClassNode, mNode)) {
				return superClass;
			}
		}

		System.currentTimeMillis();

		return null;
	}

	@SuppressWarnings("rawtypes")
	private Parameter searchForQualifiedParameter(Executable executable, Field field, Parameter[] parameters,
			String opcode) {
		List<Parameter> qualifiedParams = new ArrayList<>();
		String fullName = null;
		for (Parameter param : parameters) {
			if (param.getType().getCanonicalName().equals(field.getType().getCanonicalName())) {
				qualifiedParams.add(param);
			}
		}

		if (!qualifiedParams.isEmpty()) {
			if (executable instanceof Method) {
				Method method = ((Method) executable);
				fullName = new GenericMethod(method, method.getDeclaringClass()).getNameWithDescriptor();
			} else if (executable instanceof Constructor) {
				Constructor constructor = ((Constructor) executable);
				fullName = new GenericConstructor(constructor, constructor.getDeclaringClass()).getNameWithDescriptor();
			}

			List<BytecodeInstruction> insList = BytecodeInstructionPool
					.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(executable.getDeclaringClass().getCanonicalName(), fullName);
			for (BytecodeInstruction ins : insList) {
				if (ins.getASMNodeString().contains(opcode)) {
					BytecodeInstruction defIns = ins.getSourceOfStackInstruction(0);
					int count = 0;
					if (isParameter(defIns)) {
						for (int i = 0; i < defIns.getLocalVariableSlot(); i++) {
							String typeName = executable.getParameterTypes()[i].getTypeName();
							if (typeName.equals(qualifiedParams.get(0).getType().getCanonicalName())) {
								count++;
							}
						}
						if (count >= 1) {
							return qualifiedParams.get(count - 1);
						}
					}
				}
			}
		}
		return null;
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

	private Method searchForPotentialGetterInClass(Class<?> fieldDeclaringClass, Field field) {
		Set<Method> targetMethods = new HashSet<>();

		for (Method method : fieldDeclaringClass.getMethods()) {
			boolean isValid = checkValidGetterInTest(method, field);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		/**
		 * FIXME linyun, probability distribution to choice
		 */
		return Randomness.choice(targetMethods);
	}

	private boolean isPrimitiveType(String fieldType) {
		boolean flag = !fieldType.contains("L") && !fieldType.contains(";");
		return flag;
	}

	@SuppressWarnings("rawtypes")
	private boolean isCalledConstructor(TestCase test, VariableReference parentVarRef, Constructor constructor) {

		if (parentVarRef == null) {
			return false;
		}

		Statement stat = test.getStatement(parentVarRef.getStPosition());
		if (stat instanceof ConstructorStatement) {
			ConstructorStatement s = (ConstructorStatement) stat;
			Constructor calledConstructor = s.getConstructor().getConstructor();

			return calledConstructor.equals(constructor);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private void registerAllMethods(Class<?> fieldDeclaringClass) {
		try {
			for (Method method : fieldDeclaringClass.getDeclaredMethods()) {
				String methodName = method.getName() + MethodUtil.getSignature(method);
				MethodUtil.registerMethod(fieldDeclaringClass, methodName);
			}

			for (Constructor constructor : fieldDeclaringClass.getDeclaredConstructors()) {
				String constructorName = "<init>" + MethodUtil.getSignature(constructor);
				MethodUtil.registerMethod(fieldDeclaringClass, constructorName);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	 * parameter statement is supposed to be used only in the target method
	 * invocation
	 * 
	 * @param test
	 * @param position
	 * @param var
	 * @param parentVarRef
	 * @param castSubClass
	 * @return
	 * @throws ConstructionFailedException
	 * @throws ClassNotFoundException
	 */
	private VariableReference generateParameterStatement(TestCase test, DepVariable var,
			VariableReference parentVarRef, String castSubClass)
			throws ConstructionFailedException, ClassNotFoundException {

		/**
		 * find the existing parameters
		 */
		if (parentVarRef == null) {
			MethodStatement mStat = findTargetMethodCallStatement(test);
			int paramPosition = var.getParamOrder();
			VariableReference paramRef = mStat.getParameterReferences().get(paramPosition - 1);

			/**
			 * what if the parameter is null?
			 */
			return paramRef;
		}

		VariableReference paramRef = generateParameter(test, var, castSubClass);

		if (paramRef == null) {
			return parentVarRef;
		}

		MethodStatement targetStatement = findTargetMethodCallStatement(test);
		if (targetStatement != null) {
			VariableReference oldParamRef = targetStatement.getParameterReferences().get(var.getParamOrder() - 1);
			targetStatement.replace(oldParamRef, paramRef);
		}

		return paramRef;
	}

	private MethodStatement findTargetMethodCallStatement(TestCase test) {
		for (int i = 0; i < test.size(); i++) {
			Statement stat = test.getStatement(i);
			if (stat instanceof MethodStatement) {
				MethodStatement methodStat = (MethodStatement) stat;
				if (methodStat.getMethod().getNameWithDescriptor().equals(Properties.TARGET_METHOD)) {
					return methodStat;
				}
			}
		}

		return null;
	}

	private VariableReference generateParameter(TestCase test, DepVariable var, String castSubClass)
			throws ConstructionFailedException {

		String paramType = castSubClass;
		if (paramType == null) {
			ActualControlFlowGraph actualControlFlowGraph = var.getInstruction().getActualCFG();
			int paramOrder = var.getParamOrder();

			String methodSig = actualControlFlowGraph.getMethodName();
			String[] parameters = extractParameter(methodSig);
			paramType = parameters[paramOrder - 1];
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
			MethodStatement mStat = findTargetMethodCallStatement(test);
			paramRef = testFactory.addConstructor(test, gc, mStat.getPosition() - 1, 2);
		}

		return paramRef;
	}

	private String[] extractParameter(String methodSig) {
		String parameters = methodSig.substring(methodSig.indexOf("(") + 1, methodSig.indexOf(")"));
		String[] args = parameters.split(";");
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace("/", ".").substring(1, args[i].length());
		}
		return args;
	}

	/**
	 * return a method along with one of its parameters to setter the field.
	 * 
	 * @param field
	 * @param fieldOwner
	 * @param fieldDeclaringClass
	 * @param insList
	 * @param operation
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	private Map.Entry<Method, Parameter> searchForPotentialSetterInClass(Field field, String fieldOwner,
			Class<?> fieldDeclaringClass, List<BytecodeInstruction> insList)
			throws NoSuchMethodException, ClassNotFoundException {

		String opcode = Modifier.isStatic(field.getModifiers()) ? "PUTSTATIC" : "PUTFIELD";

		Map<Method, Parameter> targetMethods = new HashMap<>();
		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNodeString().contains(opcode)) {
				FieldInsnNode insnNode = ((FieldInsnNode) ins.getASMNode());
				String tmpName = insnNode.name;
				String tmpOwner = insnNode.owner;
				if (tmpName.equals(field.getName()) && tmpOwner.equals(fieldOwner)) {
					String methodName = ins.getMethodName();
					org.objectweb.asm.Type[] types = org.objectweb.asm.Type
							.getArgumentTypes(methodName.substring(methodName.indexOf("("), methodName.length()));
					Class<?>[] paramClasses = new Class<?>[types.length];
					int index = 0;
					for (org.objectweb.asm.Type type : types) {
						Class<?> paramClass = getClassForType(type);
						paramClasses[index++] = paramClass;
					}

					if (!methodName.contains("<init>") && !(methodName.equals(Properties.TARGET_METHOD)
							&& ins.getClassName().equals(Properties.TARGET_CLASS))) {
						Method targetMethod = fieldDeclaringClass
								.getMethod(methodName.substring(0, methodName.indexOf("(")), paramClasses);
						
						Set<Integer> validParamPositions = searchRelevantParameterOfSetterInMethod(fieldDeclaringClass.getCanonicalName(), methodName, field);
						if (!validParamPositions.isEmpty()) {
							//FIXME probability distribution
							Integer validParamPos = Randomness.choice(validParamPositions);
							Parameter param = targetMethod.getParameters()[validParamPos];
							targetMethods.put(targetMethod, param);
						}
					}
				}
			}
		}

		/**
		 * FIXME, linyun probability distribution
		 */
		Map.Entry<Method, Parameter> entry = Randomness.choice(targetMethods.entrySet());
		return entry;
	}

	@SuppressWarnings("rawtypes")
	private Map.Entry<Constructor, Parameter> searchForPotentialConstructor(Field field, String fieldOwner,
			Class<?> fieldDeclaringClass, List<BytecodeInstruction> insList)
			throws NoSuchMethodException, ClassNotFoundException {

		String opcode = Modifier.isStatic(field.getModifiers()) ? "PUTSTATIC" : "PUTFIELD";

		Map<Constructor, Parameter> targetConstructors = new HashMap<>();
		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNodeString().contains(opcode)) {
				FieldInsnNode insnNode = ((FieldInsnNode) ins.getASMNode());
				String tmpName = insnNode.name;
				String tmpOwner = insnNode.owner;
				if (tmpName.equals(field.getName()) && tmpOwner.equals(fieldOwner)) {
					String methodName = ins.getMethodName();
					org.objectweb.asm.Type[] types = org.objectweb.asm.Type
							.getArgumentTypes(methodName.substring(methodName.indexOf("("), methodName.length()));
					Class<?>[] paramClasses = new Class<?>[types.length];
					int index = 0;
					for (org.objectweb.asm.Type type : types) {
						Class<?> paramClass = getClassForType(type);
						paramClasses[index++] = paramClass;
					}

					if (methodName.contains("<init>")) {
						Constructor targetConstructor = fieldDeclaringClass.getDeclaredConstructor(paramClasses);
						Set<Integer> validParamPositions = searchRelevantParameterOfSetterInMethod(fieldDeclaringClass.getCanonicalName(), methodName, field);
						if (!validParamPositions.isEmpty()) {
							//FIXME probability distribution
							Integer validParamPos = Randomness.choice(validParamPositions);
							Parameter param = targetConstructor.getParameters()[validParamPos];
							targetConstructors.put(targetConstructor, param);
						}
					}
				}
			}
		}

		Map.Entry<Constructor, Parameter> entry = Randomness.choice(targetConstructors.entrySet());
		return entry;
	}

	private AbstractStatement addStatementToSetNonPrimitiveField(TestCase test, int position, String desc,
			GenericField genericField, VariableReference parentVarRef) throws ConstructionFailedException {
		VariableReference constructorVarRef = addConstructorForClass(test, position, desc);
		if (constructorVarRef == null) {
			return null;
		}

		if (genericField.isFinal()) {
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
			test.addStatement(stmt, constructorVarRef.getStPosition() + 1);

			return stmt;
		}

	}

	private VariableReference addPrimitiveStatement(TestCase test, int position, String desc) {
		try {
			PrimitiveStatement<?> primStatement = createNewPrimitiveStatement(test, desc);
			primStatement.randomize();
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

	private VariableReference addConstructorForClass(TestCase test, int position, String desc)
			throws ConstructionFailedException {
		try {
			String fieldType;
			if (desc.contains("/")) {
				fieldType = desc.replace("/", ".").substring(desc.indexOf("L") + 1, desc.length() - 1);
			} else {
				fieldType = desc;
			}
			Class<?> fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(fieldType);

			if (fieldClass.isInterface() || Modifier.isAbstract(fieldClass.getModifiers())) {
				Set<String> subclasses = DependencyAnalysis.getInheritanceTree()
						.getSubclasses(fieldClass.getCanonicalName());
				String subclass = Randomness.choice(subclasses);
				fieldClass = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(subclass);
			}

			Constructor<?> constructor = Randomness.choice(fieldClass.getConstructors());
			if (constructor != null) {
				GenericConstructor genericConstructor = new GenericConstructor(constructor, fieldClass);
				VariableReference variableReference = testFactory.addConstructor(test, genericConstructor, position + 1,
						2);
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
		} else if (type == org.objectweb.asm.Type.BYTE_TYPE) {
			return byte.class;
		} else if (type == org.objectweb.asm.Type.CHAR_TYPE) {
			return char.class;
		} else if (type == org.objectweb.asm.Type.SHORT_TYPE) {
			return short.class;
		} else if (type == org.objectweb.asm.Type.INT_TYPE) {
			return int.class;
		} else if (type == org.objectweb.asm.Type.LONG_TYPE) {
			return long.class;
		} else if (type == org.objectweb.asm.Type.FLOAT_TYPE) {
			return float.class;
		} else if (type == org.objectweb.asm.Type.DOUBLE_TYPE) {
			return double.class;
		} else {
			try {
				String className = type.getClassName();
				if (type.getSort() != org.objectweb.asm.Type.ARRAY) {
					Class<?> clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
					return clazz;
				} else {
					StringBuffer buffer = new StringBuffer();
					org.objectweb.asm.Type elementType = type.getElementType();
					String arrayString = extractedArrayString(type);
					if (elementType.getSort() <= 8) {
						className = convertToShortName(elementType.getClassName());
						buffer.append(arrayString);
						buffer.append(className);
					} else {
						className = elementType.getClassName();
						buffer.append(arrayString);
						buffer.append("L");
						buffer.append(className);
						buffer.append(";");
					}
					String fullName = buffer.toString();
					Class<?> clazzArray = Class.forName(fullName, true,
							TestGenerationContext.getInstance().getClassLoaderForSUT());
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
		for (int i = 0; i < arrayLength; i++) {
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

	private String convertToShortName(String desc) {
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

	private String convertToLongName(String desc) {
		switch (desc) {
		case "Z":
			return "boolean";
		case "B":
			return "byte";
		case "C":
			return "char";
		case "S":
			return "short";
		case "I":
			return "int";
		case "J":
			return "long";
		case "F":
			return "float";
		case "D":
			return "double";
		default:
			return null;
		}
	}

	private boolean isParameter(BytecodeInstruction instruction) {
		if (instruction.isLocalVariableUse()) {
			String methodName = instruction.getRawCFG().getMethodName();
			String methodDesc = methodName.substring(methodName.indexOf("("), methodName.length());
			org.objectweb.asm.Type[] typeArgs = org.objectweb.asm.Type.getArgumentTypes(methodDesc);
			int paramNum = typeArgs.length;

			int slot = instruction.getLocalVariableSlot();

			if (instruction.getRawCFG().isStaticMethod()) {
				return slot < paramNum;
			} else {
				return slot < paramNum + 1 && slot != 0;
			}
		}
		return false;
	}
}
