package org.evosuite.testcase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.dataflow.ConstructionPath;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.runtime.System;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.evosuite.setup.DependencyAnalysis;
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
					parentVarRef = generateFieldStatement(test, position, var, parentVarRef);
				} else if (var.getType() == DepVariable.PARAMETER) {
					String castSubClass = checkClass(var, path);
					parentVarRef = generateParameterStatement(test, position, var, parentVarRef, castSubClass);
				} else if (var.getType() == DepVariable.INSTANCE_FIELD) {
					if(parentVarRef == null) {
						break;
					}
					parentVarRef = generateFieldStatement(test, position, var, parentVarRef);
				} else if (var.getType() == DepVariable.OTHER) {
					/**
					 * FIXME: need to handle other cases than method call in the future.
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
			VariableReference parentVarRef) {
		FieldInsnNode fieldNode = (FieldInsnNode) var.getInstruction().getASMNode();
		String desc = fieldNode.desc;
		String fieldOwner = fieldNode.owner.replace("/", ".");
		String fieldName = fieldNode.name;

		AbstractStatement stmt = null;
		
		if (parentVarRef != null) {
			String parentType = parentVarRef.getType().getTypeName();
			if (!parentType.equals(fieldOwner)) {
				return parentVarRef;
			}
		}

		try {
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);

			/** 
			 * If field is not found in current declaring class, we search recursively for its superclass
			 * However, for simplicity, we only search for setter, getter, constructor in current class instead of the real
			 * declaring class of this field
			 */
			Field field = searchForField(fieldDeclaringClass, fieldName);

			VariableReference usedField = searchRelevantReferenceInTest(test, field, parentVarRef);
			if(usedField != null) {
				return usedField;						
			}
			System.currentTimeMillis();
			
			GenericField genericField = new GenericField(field, field.getDeclaringClass());
			int fieldModifiers = field.getModifiers();

			if (Modifier.isPublic(fieldModifiers) || fieldModifiers == 0) {
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
					Map.Entry<Method, Parameter> entry = searchForPotentialSetter(field, fieldNode.owner, fieldDeclaringClass, insList);
					if (entry != null) {
						Method setter = entry.getKey();
						
						VariableReference returnedVar = null;
						Statement setterStatement = checkCallInTest(parentVarRef, setter, test);
						if(setterStatement == null) {
							GenericMethod gMethod = new GenericMethod(setter, setter.getDeclaringClass());
							if (parentVarRef == null) {
								returnedVar = testFactory.addMethod(test, gMethod, position, 2);
							}
							else {
								returnedVar = testFactory.addMethodFor(test, parentVarRef, gMethod, parentVarRef.getStPosition() + 1);
							}
							
							setterStatement = test.getStatement(returnedVar.getStPosition());
						}
						
						Parameter param = entry.getValue();
						if (param == null) {
							return null;
						}
						return retrieveParamReference4Field(test, param, setter, setterStatement);
					}

					/**
					 * if the field is not primitive, we can get the object-typed field and return it.
					 */
					if (!isPrimitiveType(var.getInstruction().getFieldType())) {
						Method getter = searchForPotentialGetter(var.getInstruction(), fieldDeclaringClass);
						if (getter != null) {
							
							MethodStatement getterStatement = checkCallInTest(parentVarRef, getter, test);
							VariableReference newParentVarRef = null;
							if(getterStatement == null) {
								GenericMethod gMethod = new GenericMethod(getter,
										getter.getDeclaringClass());
								if (parentVarRef == null) {
									newParentVarRef = testFactory.addMethod(test, gMethod, position, 2);
								}
								else {
									newParentVarRef = testFactory.addMethodFor(test, parentVarRef, gMethod, parentVarRef.getStPosition() + 1);
								}
							}
							else {
								newParentVarRef = getterStatement.getReturnValue();
							}
							
							return newParentVarRef;
						}
					}

					/**
					 * deal with the case when the class has neither getter nor setter.
					 */
					Map.Entry<Constructor, Parameter> constructorEntry = searchForPotentialConstructor(field, fieldNode.owner, fieldDeclaringClass,
							insList);
					/**
					 * the constructor cannot set the field, so we stop here.
					 */
					if (constructorEntry != null) {
						Constructor constructor = constructorEntry.getKey();
						Parameter constructorParam = constructorEntry.getValue();
						if (!isCalledConstructor(test, parentVarRef, constructor)) {
							GenericConstructor gConstructor = new GenericConstructor(constructor,
									constructor.getDeclaringClass());
							VariableReference returnedVar = testFactory.addConstructor(test, gConstructor,
									parentVarRef.getStPosition() + 1, 2);

							for (int i = 0; i < test.size(); i++) {
								Statement stat = test.getStatement(i);
								if (returnedVar.getStPosition() < stat.getPosition()) {
									if (stat.references(parentVarRef)) {
										stat.replace(parentVarRef, returnedVar);
									}
								}
							}
							
							Statement methodStatement = test.getStatement(returnedVar.getStPosition());
							VariableReference paramRef4Field = retrieveParamReference4Field(test, constructorParam, constructor, methodStatement);
							return paramRef4Field;
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

	private MethodStatement checkCallInTest(VariableReference calleeVar, Method setter, TestCase test) {
		for(int i=0; i<test.size(); i++) {
			Statement statement = test.getStatement(i);
			if(statement instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement)statement;
				Method calledMethod = mStat.getMethod().getMethod();
				
				if(calledMethod.equals(setter)) {
					if(mStat.getMethod().isStatic()) {
						return mStat;
					}
					/**
					 * otherwise, we check the preference
					 */
					else if(mStat.getCallee().equals(calleeVar)){
						return mStat;
					}
				}
			}
		}
		
		return null;
	}

	private VariableReference retrieveParamReference4Field(TestCase test, Parameter parameter, Executable executable,
			Statement statement) {
		
		if(!(statement instanceof EntityWithParametersStatement)) {
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
	 * @author linyun: Given a field, we should check the cascade call to write such a method.
	 * The more controllable a variable, the more likely we return the variable.
	 * 
	 * @param test
	 * @param field
	 * @param opcode
	 * @return
	 */
	private VariableReference searchRelevantReferenceInTest(TestCase test, Field field, VariableReference targetObject) {
		
		List<VariableReference> relevantRefs = new ArrayList<VariableReference>();
		
		if (targetObject != null) {
			/**
			 * check the variables passed as parameters to the constructor of targetObject, which are 
			 * data-flow relevant to writing the field @{code field} 
			 */
			Statement s = test.getStatement(targetObject.getStPosition());
			/**
			 * TODO: if it is a null statement, change a random constructor here.
			 */
			if(s instanceof NullStatement) {
				TestFactory testFactory = TestFactory.getInstance();
				testFactory.changeNullStatement(test, s);
				System.currentTimeMillis();
			}
			
			
			if (s instanceof ConstructorStatement) {
				ConstructorStatement constructorStat = (ConstructorStatement) s;
				List<VariableReference> params = constructorStat.getParameterReferences();
				String className = constructorStat.getDeclaringClassName();
				String methodName = constructorStat.getMethodName() + constructorStat.getDescriptor();
				if (!params.isEmpty()) {
					List<VariableReference> paramRefs = searchRelevantParameterInTest(params, className, methodName, field);
					relevantRefs.addAll(paramRefs);
				}		
			}
			
			/**
			 * check the variables passed as parameters to the method invocation from targetObject, which are 
			 * data-flow relevant to writing the field @{code field} 
			 */
			for(int i=0; i<test.size(); i++) {
				Statement stat = test.getStatement(i);
				if(stat instanceof MethodStatement) {
					MethodStatement mStat = (MethodStatement)stat;
					VariableReference ref = mStat.getCallee();
					if(ref != null && ref.equals(targetObject)) {
						List<VariableReference> params = mStat.getParameterReferences();
						String className = mStat.getDeclaringClassName();
						String methodName = mStat.getMethodName() + mStat.getDescriptor();
						List<VariableReference> paramRefs = searchRelevantParameterInTest(params, className, methodName, field);
	
						for(VariableReference pRef: paramRefs) {
							if(!relevantRefs.contains(pRef)) {
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
		if(relevantRefs.isEmpty())
			return null;
		
		VariableReference ref = Randomness.choice(relevantRefs);
		return ref;
	}
	
	/**
	 * opcode should always be "getfield"
	 * @param statement
	 * @param params
	 * @param opcode
	 * @return
	 */
	private List<VariableReference> searchRelevantParameterInTest(List<VariableReference> params, String className,
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
	
	/**
	 * Output is the valid parameter position of most top method call
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

		Set<BytecodeInstruction> paramInstructions = analyzingCascadingCall(new HashSet<>(Arrays.asList(setterInstruction)), callList, 0, new HashSet<>());
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
	 * search for new load instruction when analyzing next layer of method invocation
	 * @param call
	 * @param paramPos
	 * @return
	 */
	private Set<BytecodeInstruction> searchForNewParameterInstruction(BytecodeInstruction call, Set<BytecodeInstruction> paramInsns) {
		Set<BytecodeInstruction> newParamInsns = new HashSet<>();
		Set<Integer> paramPosSet = checkSetterParamPositions(paramInsns);
		Set<BytecodeInstruction> newParams = new HashSet<>();
		for (Integer pos : paramPosSet) {
			if (call.getASMNode().getOpcode() == Opcodes.INVOKESTATIC) {
				BytecodeInstruction defIns = call.getSourceOfStackInstruction(call.getOperandNum() - pos - 1);
				newParams = checkCurrentParamInstructions(defIns, new HashSet<>());
			} else {
				BytecodeInstruction defIns = call.getSourceOfStackInstruction(call.getOperandNum() - pos - 2);
				newParams = checkCurrentParamInstructions(defIns, new HashSet<>());			}
			if (newParams != null) {
				newParamInsns.addAll(newParams);
			}
		}
		return newParamInsns;
	}

	/**
	 * return localVariableUse instruction for parameter 
	 * @param descIns
	 * @param paramInstructionSet
	 * @return
	 */
	private Set<BytecodeInstruction> checkCurrentParamInstructions(BytecodeInstruction descIns, Set<BytecodeInstruction> paramInstructionSet) {
		if (descIns.isLocalVariableUse()) {
			if (isParameter(descIns)) {
				// position here similar to list index (i.e., starts from 0)
//				int pos = checkSetterParamPos(defIns);
//				paramInstructionSet.add(new Integer(pos));
				paramInstructionSet.add(descIns);
			}
		}
		for (int i = 0;i < descIns.getOperandNum(); i ++) {
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
		
		if(ins.isUse()) {
			FBranchDefUseAnalyzer.analyze(ins.getRawCFG());
			
			Use use = DefUsePool.getUseByInstruction(ins);
			List<Definition> defs = DefUsePool.getDefinitions(use);
			
			if(defs==null) {
				return null;
			}
			
			if(!defs.isEmpty()) {
				Definition def = defs.get(0);
				if(def.getFrame().getStackSize() > 0) {
					BytecodeInstruction call = def.getSourceOfStackInstruction(0);
					
					if(call != null && call.isMethodCall()) {
						return call;
					}					
				}
			}
		}
		
		return null;
	}
	
	private boolean checkParamValidation(BytecodeInstruction call, Set<BytecodeInstruction> paramInsns, BytecodeInstruction ins) {
		if (call.getCalledMethodsArgumentCount() == 0) {
			return false;
		}
		if (!call.getCalledMethod().equals(ins.getMethodName()) || !call.getCalledMethodsClass().equals(ins.getClassName())) {
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

	private boolean checkDataDependency(BytecodeInstruction instruction, int paramPosition) {
		if (isParameter(instruction)) {
			System.currentTimeMillis();
		}
		if(instruction.isLocalVariableUse()) {
			int slot = instruction.getLocalVariableSlot();
			return slot == paramPosition;
		}
		//FIXME, ziheng, fix it to false when data dependency is ready
		return true;
	}

	private Map<BytecodeInstruction, List<BytecodeInstruction>> checkFieldSetter(String className, String methodName,
			Field field, int depth, List<BytecodeInstruction> cascadingCallRelations,
			Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap) {
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getAllInstructionsAtMethod(className, methodName);
		if (insList == null && RuntimeInstrumentation.checkIfCanInstrument(className)) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(className, methodName);
		}

		String opcode = Modifier.isStatic(field.getModifiers()) ? "PUTSTATIC" : "PUTFIELD";

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
							checkFieldSetter(calledClass, calledMethodName, field, depth - 1, cascadingCallRelations, setterMap);
						}
					}

				} else {
					System.currentTimeMillis();
				}
			}

		}
		cascadingCallRelations.clear();;

		return setterMap;

	}

	private boolean containMethod(ClassNode classNode, MethodInsnNode mNode) {
		
//		if(!mNode.owner.equals(classNode.name)) {
//			return false;
//		}
		
		for(MethodNode methodNode: classNode.methods) {
			String methodNodeName = methodNode.name + methodNode.desc;
			String methodInsName = mNode.name + mNode.desc;
			if(methodNodeName.equals(methodInsName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private String confirmClassNameInParentClass(String calledClass, MethodInsnNode mNode) {
		
		List<String> superClassList = DependencyAnalysis.getInheritanceTree().getOrderedSuperclasses(calledClass);
		for(String superClass: superClassList) {
			ClassNode parentClassNode = DependencyAnalysis.getClassNode(superClass);
			if(containMethod(parentClassNode, mNode)) {
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

	private Method searchForPotentialGetter(BytecodeInstruction bytecodeInstruction, Class<?> fieldDeclaringClass) {
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
				
		/**
		 * find the existing parameters
		 */
		if(parentVarRef == null) {
			MethodStatement mStat = findTargetMethodCallStatement(test);
			int paramPosition = var.getParamOrder();
			VariableReference paramRef = mStat.getParameterReferences().get(paramPosition-1);
			return paramRef;
		}
				
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
	private Map.Entry<Method, Parameter> searchForPotentialSetter(Field field, String fieldOwner, Class<?> fieldDeclaringClass,
			List<BytecodeInstruction> insList) throws NoSuchMethodException, ClassNotFoundException {
		
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
								.getDeclaredMethod(methodName.substring(0, methodName.indexOf("(")), paramClasses);
						Parameter[] paramList = targetMethod.getParameters();
						Parameter param = searchForQualifiedParameter(targetMethod, field, paramList, opcode);
						targetMethods.put(targetMethod, param);
//						if (param != null) {
//						}
					}
				}
			}
		}

		Map.Entry<Method, Parameter> entry = Randomness.choice(targetMethods.entrySet());
		return entry;
	}
	
	@SuppressWarnings("rawtypes")
	private Map.Entry<Constructor, Parameter> searchForPotentialConstructor(Field field, String fieldOwner, 
			Class<?> fieldDeclaringClass, List<BytecodeInstruction> insList) 
			throws NoSuchMethodException, ClassNotFoundException {
		
		String opcode = java.lang.reflect.Modifier.isStatic(field.getModifiers()) ? "PUTSTATIC" : "PUTFIELD";
		
		Map<Constructor, Parameter> targetConstructors = new HashMap<>();
		for (BytecodeInstruction ins : insList) {
			if (ins.getASMNodeString().contains(opcode)) {
				FieldInsnNode insnNode = ((FieldInsnNode) ins.getASMNode());
				String tmpName = insnNode.name;
				String tmpOwner = insnNode.owner;
				if (tmpName.equals(field.getName()) && tmpOwner.equals(fieldOwner)) {
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
						Parameter[] paramList = constructor.getParameters();
						Parameter param = searchForQualifiedParameter(constructor, field, paramList, opcode);
						targetConstructors.put(constructor, param);												
//						if(param != null) {
//						}
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
						className = convertToShortName(elementType.getClassName());
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
		if(instruction.isLocalVariableUse()) {
			String methodName = instruction.getRawCFG().getMethodName();
			String methodDesc = methodName.substring(methodName.indexOf("("), methodName.length());
			org.objectweb.asm.Type[] typeArgs = org.objectweb.asm.Type.getArgumentTypes(methodDesc);
			int paramNum = typeArgs.length;
			
			int slot = instruction.getLocalVariableSlot();
			
			if(instruction.getRawCFG().isStaticMethod()) {
				return slot < paramNum;
			}
			else {
				return slot < paramNum+1 && slot != 0;				
			}
		}
		return false;
	}
}
