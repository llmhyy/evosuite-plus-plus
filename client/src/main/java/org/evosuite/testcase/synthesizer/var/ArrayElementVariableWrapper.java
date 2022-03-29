package org.evosuite.testcase.synthesizer.var;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.runtime.System;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.VariableInTest;
import org.evosuite.testcase.synthesizer.improvedsynth.Operation;
import org.evosuite.testcase.variable.ArrayIndex;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

public class ArrayElementVariableWrapper extends DepVariableWrapper {

	protected ArrayElementVariableWrapper(DepVariable var) {
		super(var);
	}

	@Override
	public VarRelevance generateOrFindStatement(TestCase test, boolean isLeaf, VariableInTest variable,
			Map<DepVariableWrapper, VarRelevance> map, Branch b, boolean allowNullValue, Operation recommendation) {
		
		try {
			return generateArrayElementStatement(test, isLeaf, variable);
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private VarRelevance generateArrayElementStatement(TestCase test, boolean isLeaf,
			VariableInTest variable) throws ConstructionFailedException {
		Statement stat = test.getStatement(variable.callerObject.getStPosition());
		if(stat instanceof NullStatement) {
			List<VariableReference> list = new ArrayList<>();
			list.add(stat.getReturnValue());
			return new VarRelevance(list, list);
		}
		
		
		int opcodeRead = this.var.getInstruction().getASMNode().getOpcode();			
		int opcodeWrite = getCorrespondingWriteOpcode(opcodeRead);
//		Statement statement = test.getStatement(variable.callerObject.getStPosition());

		/**
		 * check reused array element
		 */
		List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, variable);
		if(!usedArrayElementList.isEmpty()) {
			return new VarRelevance(usedArrayElementList, usedArrayElementList);
		}
		
		List<VariableReference> usedArrayRefs = isLeaf
				? searchArrayElementWritingReference(test, variable, opcodeWrite)
				: searchArrayElementReadingReference(test, variable, opcodeRead);
		if (usedArrayRefs != null && !usedArrayRefs.isEmpty()) {
			return new VarRelevance(usedArrayRefs, usedArrayRefs);
		}
		
		double prob = Randomness.nextDouble();
		if (prob > 0.8) {
			if (isLeaf) {
				/**
				 * generate setter
				 */
				ElementSetter setter = searchSetterForArrayElement(test, opcodeWrite, variable);
				if (setter != null) {
					GenericMethod gMethod = new GenericMethod(setter.method, setter.method.getDeclaringClass());
					TestFactory.getInstance().addMethodFor(test, variable.callerObject, gMethod,
							variable.callerObject.getStPosition() + 1, false);
					return null;
				}
			} else {
				/**
				 * generate getter
				 */
				Method getter = searchGetterForArrayElement(test, opcodeRead, variable);
				if (getter != null) {
					VariableReference newParentVarRef = null;
					GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
					newParentVarRef = TestFactory.getInstance().addMethodFor(test, variable.callerObject, gMethod,
							variable.callerObject.getStPosition() + 1, false);
//					V newParentVarRef;
					List<VariableReference> vars = new ArrayList<>();
					if(newParentVarRef != null) {
						vars.add(newParentVarRef);						
					}
					return new VarRelevance(vars, vars);
				}
				
				return null;
			}
		}
		/**
		 * have a try to set array element directly
		 */
		else{
			if (variable.callerObject instanceof ArrayReference) {
				ArrayReference arrayRef = (ArrayReference) variable.callerObject;
				int length = arrayRef.getArrayLength();
				if(arrayRef.getArrayLength() <= 0) {
					length = Randomness.nextInt(10) + 1;
				}
				int index = Randomness.nextInt(length);
				
				ArrayIndex arrayIndex = new ArrayIndex(test, arrayRef, index);
				VariableReference varRef = createArrayElementVariable(test, arrayRef);
				if(varRef == null)
					return null;
				
				AssignmentStatement assignStat = new AssignmentStatement(test, arrayIndex, varRef);
				test.addStatement(assignStat, varRef.getStPosition() + 1);
				VariableReference ref = assignStat.getReturnValue();
				List<VariableReference> vars = new ArrayList<>();
				if(ref != null) {
					vars.add(ref);						
				}
				
				return new VarRelevance(vars, vars);
			}
		}
		
		return null;
	}

	private List<VariableReference> searchUsedArrayElementReference(TestCase test, VariableInTest variable) {
		List<VariableReference> elementList = new ArrayList<>();
		if(variable.callerObject instanceof ArrayReference && variable.nodePath.size()==1) {
			ArrayReference arrayObject = (ArrayReference)variable.callerObject;
			for(int i=0; i<test.size(); i++) {
				Statement statement = test.getStatement(i);
				if(statement instanceof AssignmentStatement) {
					AssignmentStatement aStat = (AssignmentStatement)statement;
					VariableReference var = aStat.getReturnValue();
					if(var.isArrayIndex()) {
						if(var instanceof ArrayIndex) {
							ArrayIndex index = (ArrayIndex)var;
							if(index.getArray().equals(arrayObject)) {
								elementList.add(aStat.getValue());
							}
						}
						
						System.currentTimeMillis();
					}
				}
			}
		}
		
		return elementList;
	}

	private Method searchGetterForArrayElement(TestCase test, int opcodeRead, VariableInTest variable) {
		Set<Method> targetMethods = new HashSet<>();
		Class<?> clazz = variable.callerObject.getGenericClass().getRawClass();
		for (Method method : clazz.getMethods()) {
			boolean isValid = checkValidArrayElementGetter(method, opcodeRead, variable);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}

	
	class ElementSetter {
		Method method;
		Integer paramPos;

		public ElementSetter(Method method, Integer paramPos) {
			super();
			this.method = method;
			this.paramPos = paramPos;
		}
		
	}
	
	private ElementSetter searchSetterForArrayElement(TestCase test, int opcodeWrite, VariableInTest variable) {
		Set<ElementSetter> targetMethods = new HashSet<>();
		Class<?> clazz = variable.callerObject.getGenericClass().getRawClass();
		for (Method method : clazz.getMethods()) {
			Integer paramPos = checkValidArrayElementSetter(method, opcodeWrite, variable);
			if (paramPos != -1) {
				targetMethods.add(new ElementSetter(method, paramPos));
			}
		}

		return Randomness.choice(targetMethods);
	}

	/**
	 * AASTORE, BASTORE, CASTORE, DASTORE, FASTORE, IASTORE, LASTORE, SASTORE
	 */
	private List<VariableReference> searchArrayElementWritingReference(TestCase test, VariableInTest variable, int opcodeWrite) {
		
		List<VariableReference> variableList = new ArrayList<VariableReference>(); 
		/**
		 * check array[*] = ....;, and collect all array[*]
		 */
		if(variable.isDirentNodeAccess() && variable.callerObject instanceof ArrayReference) {
			for (int i = 0;i < test.size(); i ++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof AssignmentStatement) {
					AssignmentStatement assignStat = (AssignmentStatement) stat;
					for (VariableReference varRef : assignStat.getVariableReferences()) {
						if (varRef.equals(variable.callerObject)) {
							variableList.add(assignStat.getReturnValue());
						}
					}		
				} 
			}
		}
		/**
		 * check obj.m() which assign the array element
		 */
		else {
			for (int i = 0;i < test.size(); i ++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof MethodStatement) {
					MethodStatement mStat = (MethodStatement) stat;
					VariableReference ref = mStat.getCallee();
					if (ref != null && ref.equals(variable.callerObject)) {
						Integer parameterPosition = checkValidArrayElementSetter(mStat.getMethod().getMethod(), opcodeWrite, variable);
						if(parameterPosition != -1) {
							VariableReference param = mStat.getParameterReferences().get(parameterPosition);
							variableList.add(param);
						}
						
					}	
				} 
			}
		}
		
		System.currentTimeMillis();
		
		return variableList;
	}

	private int getCorrespondingWriteOpcode(int opcodeRead) {
		switch (opcodeRead) {
		case Opcodes.AALOAD:
			return Opcodes.AASTORE;
		case Opcodes.BALOAD:
			return Opcodes.BASTORE;
		case Opcodes.CALOAD:
			return Opcodes.CASTORE;
		case Opcodes.DALOAD:
			return Opcodes.DASTORE;
		case Opcodes.FALOAD:
			return Opcodes.FASTORE;
		case Opcodes.IALOAD:
			return Opcodes.IASTORE;
		case Opcodes.LALOAD:
			return Opcodes.LASTORE;
		case Opcodes.SALOAD:
			return Opcodes.SASTORE;
		default:
			throw new IllegalArgumentException("Illegal Opcode");
		}
	}

	/**
	 * check if current method is a valid setter for array element
	 * 
	 * @param className
	 * @param methodName
	 * @param node
	 * @param opcodeWrite
	 * @return
	 */
	private Integer checkValidArrayElementSetter(Method method, 
			int opcodeWrite, VariableInTest variable) {
		
		String className = method.getDeclaringClass().getCanonicalName();
		String methodName = method.getName() + MethodUtil.getSignature(method);
		
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getAllInstructionsAtMethod(className, methodName);
		if (insList == null /* && RuntimeInstrumentation.checkIfCanInstrument(className) */) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(className, methodName);
		}
		
		if (insList != null) {
			for (BytecodeInstruction ins : insList) {
				// *ASTORE
				if (ins.getASMNode().getOpcode() == opcodeWrite) {
					BytecodeInstruction param = isMatchPath(ins, variable);
					if(param != null) {
						return param.getParameterPosition();
					}
					
				}
			}
		}
		
		return -1;
	}

	/**
	 * check whether this method can cover the instructions on the node path,
	 * 
	 * if yes, we return the parameter with dataflow to setting the array element.
	 * 
	 * @param ins
	 * @param variable
	 * @return
	 */
	private BytecodeInstruction isMatchPath(BytecodeInstruction ins, VariableInTest variable) {
		BytecodeInstruction param = null;
		/**
		 * correspond to each node in the path
		 */
		boolean[] visitedPath = new boolean[variable.nodePath.size()];
		int cursor = variable.nodePath.size()-1;
		visitedPath[cursor] = true;
		cursor--;
		
		while(cursor >= 0) {
			boolean isValid = false;
			for (int i = 0; i < ins.getOperandNum(); i++) {
				BytecodeInstruction defIns = ins.getSourceOfStackInstruction(i);
				
				if(defIns.isParameter()) {
					param = defIns;
				}
				
				if(defIns.equals(variable.nodePath.get(cursor).var.getInstruction())) {
					cursor--;
					isValid = true;
					break;
				}
			}
			
			if(!isValid) {
				break;
			}
		}
		
		boolean isMatch = true;
		for(int i=0; i<visitedPath.length; i++) {
			isMatch = isMatch & visitedPath[i];
		}
		
		if(!isMatch) {
			return null;
		}
		
		return param;
	}

	/**
	 * check if current method is a valid getter for array element
	 * 
	 * @param className
	 * @param methodName
	 * @param node
	 * @param opcodeRead
	 * @return
	 */
	private boolean checkValidArrayElementGetter(Method method, int opcodeRead, VariableInTest variable) {
		DepVariable parentVar = this.parents.get(0).var;
		if (parentVar.getType() != DepVariable.INSTANCE_FIELD && parentVar.getType() != DepVariable.STATIC_FIELD) {
			return false;
		}
		FieldInsnNode parentFNode = (FieldInsnNode) parentVar.getInstruction().getASMNode();
		if (!parentFNode.desc.contains("[")) {
			return false;
		}
		
		String parentDesc = parentFNode.desc;
		String formalDesc = parentDesc.substring(2, parentDesc.length() - 1).replace("/", ".");
		if (!method.getReturnType().getCanonicalName().equals(formalDesc)) {
			return false;
		}
		
		String className = method.getDeclaringClass().getCanonicalName();
		String methodName = method.getName() + MethodUtil.getSignature(method);
		ActualControlFlowGraph cfg = GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getActualCFG(className, methodName);
		if (cfg == null) {
			return false;
		}
		for (BytecodeInstruction exit : cfg.getExitPoints()) {
			//TODO needs to check whether the instruction can correspond to the VariableInTest
			if (exit.isReturn()) {
				BytecodeInstruction returnInsn = exit.getSourceOfStackInstruction(0);
				if (returnInsn.getASMNode().getOpcode() == opcodeRead) {
					for (int i = 0;i < returnInsn.getOperandNum(); i ++) {
						BytecodeInstruction defIns = returnInsn.getSourceOfStackInstruction(i);
						if (defIns.isFieldUse()) {
							FieldInsnNode fnode = ((FieldInsnNode) defIns.getASMNode());
							if (fnode.name.equals(parentFNode.name)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/*
	 * AALOAD, BALOAD, CALOAD, DALOAD, FALOAD, IALOAD, LALOAD, SALOAD
	 */
	private List<VariableReference> searchArrayElementReadingReference(TestCase test, VariableInTest variable, int opcodeRead) {
		List<VariableReference> varList = new ArrayList<VariableReference>();
		
		for (int i = 0;i < test.size(); i ++) {
			Statement stat = test.getStatement(i);
			// check reading through method call
			if (stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement) stat;
				Method method = mStat.getMethod().getMethod();
				VariableReference ref = mStat.getCallee();
				if (ref != null && ref.equals(variable.callerObject)) {
					boolean isValid = checkValidArrayElementGetter(method, opcodeRead, variable);
					if (isValid) {
						VariableReference var = mStat.getReturnValue();
						varList.add(var);
					}
				}
			}
		}
		
		return varList;
	}

	private VariableReference createArrayElementVariable(TestCase test, ArrayReference arrayRef) {
		Class<?> clazz = arrayRef.getComponentClass();
		if(clazz.getConstructors().length < 1)
			return null;
		
		Constructor<?> constructor = clazz.getConstructors()[0];
		GenericConstructor gConstructor = new GenericConstructor(constructor,
				constructor.getDeclaringClass());
		try {
			VariableReference returnedVar = TestFactory.getInstance().addConstructor(test, gConstructor,
					arrayRef.getStPosition() + 1, 2);
			return returnedVar;
			
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

//	@Override
//	public List<VariableReference> findCorrespondingVariables(TestCase test, boolean isLeaf, VariableReference callerObject, Map<DepVariableWrapper, List<VariableReference>> map) {
//		Statement stat = test.getStatement(callerObject.getStPosition());
//		if(stat instanceof NullStatement) {
//			List<VariableReference> list = new ArrayList<>();
//			list.add(stat.getReturnValue());
//			return list;
//		}
//		
//		if(this.var.getType() == DepVariable.PARAMETER) {
//			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
//			if(!usedArrayElementList.isEmpty()) {
//				return usedArrayElementList;
//			}
//		}
//		
//		if (callerObject instanceof ArrayReference) {
//			int opcodeRead = this.var.getInstruction().getASMNode().getOpcode();			
//			int opcodeWrite = getCorrespondingWriteOpcode(opcodeRead);
//			VariableReference realParentRef = null;
//			Statement statement = test.getStatement(callerObject.getStPosition());
//
//			if (statement instanceof MethodStatement) {
//				MethodStatement mStatement = (MethodStatement) statement;
//				realParentRef = mStatement.getCallee();
//			}
//			
//			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
//			if(!usedArrayElementList.isEmpty()) {
//				return usedArrayElementList;
//			}
//			
//			double prob = Randomness.nextDouble();
//			if (realParentRef  != null && prob > 0.8) {
//				/**
//				 * check reused array element
//				 */
//				VariableReference usedArrayRef = isLeaf
//						? searchArrayElementWritingReference(test, callerObject, realParentRef, opcodeWrite)
//						: searchArrayElementReadingReference(test, callerObject, realParentRef, opcodeRead);
//				if (usedArrayRef != null) {
//					VariableReference generatedVariable = isLeaf ? null : usedArrayRef;
//					List<VariableReference> vars = new ArrayList<>();
//					if(generatedVariable != null) {
//						vars.add(generatedVariable);						
//					}
//					return vars;
//				}
//
//			}
//		}
//		
//		return new ArrayList<>();
//	}
	
	@Override
	public VariableReference find(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, VarRelevance> map) {
		//TODO
		return null;
	}
}
