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
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
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
	public List<VariableReference> generateOrFindStatement(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		try {
			return generateArrayElementStatement(test, isLeaf, callerObject);
		} catch (ConstructionFailedException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}

	private List<VariableReference> generateArrayElementStatement(TestCase test, boolean isLeaf,
			VariableReference callerObject) throws ConstructionFailedException {
		Statement stat = test.getStatement(callerObject.getStPosition());
		if(stat instanceof NullStatement) {
			List<VariableReference> list = new ArrayList<>();
			list.add(stat.getReturnValue());
			return list;
		}
		
		if(this.var.getType() == DepVariable.PARAMETER) {
			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
			if(!usedArrayElementList.isEmpty()) {
				return usedArrayElementList;
			}
		}
		
		if (callerObject instanceof ArrayReference) {
			int opcodeRead = this.var.getInstruction().getASMNode().getOpcode();			
			int opcodeWrite = getCorrespondingWriteOpcode(opcodeRead);
			VariableReference realParentRef = null;
			Statement statement = test.getStatement(callerObject.getStPosition());

			if (statement instanceof MethodStatement) {
				MethodStatement mStatement = (MethodStatement) statement;
				realParentRef = mStatement.getCallee();
			}
			
			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
			if(!usedArrayElementList.isEmpty()) {
				return usedArrayElementList;
			}
			
			double prob = Randomness.nextDouble();
			if (realParentRef  != null && prob > 0.8) {
				/**
				 * check reused array element
				 */
				VariableReference usedArrayRef = isLeaf
						? searchArrayElementWritingReference(test, callerObject, realParentRef, opcodeWrite)
						: searchArrayElementReadingReference(test, callerObject, realParentRef, opcodeRead);
				if (usedArrayRef != null) {
					VariableReference generatedVariable = isLeaf ? null : usedArrayRef;
					List<VariableReference> vars = new ArrayList<>();
					if(generatedVariable != null) {
						vars.add(generatedVariable);						
					}
					return vars;
				}

				if (isLeaf) {
					/**
					 * generate setter
					 */
					Method setter = searchSetterForArrayElement(test, realParentRef, opcodeWrite);
					if (setter != null) {
						GenericMethod gMethod = new GenericMethod(setter, setter.getDeclaringClass());
						TestFactory.getInstance().addMethodFor(test, realParentRef, gMethod,
								realParentRef.getStPosition() + 1, false);
						return null;
					}
				} else {
					/**
					 * generate getter
					 */
					Method getter = searchGetterForArrayElement(test, realParentRef, opcodeRead);
					if (getter != null) {
						VariableReference newParentVarRef = null;
						GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
						newParentVarRef = TestFactory.getInstance().addMethodFor(test, realParentRef, gMethod,
								realParentRef.getStPosition() + 1, false);
//						V newParentVarRef;
						List<VariableReference> vars = new ArrayList<>();
						if(newParentVarRef != null) {
							vars.add(newParentVarRef);						
						}
						return vars;
					}
					return null;
				}
			}
			/**
			 * direct set
			 */
			else{
				ArrayReference arrayRef = (ArrayReference) callerObject;
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
				return vars;
			}
		}
		
		return null;
	}

	private List<VariableReference> searchUsedArrayElementReference(TestCase test, VariableReference arrayObject) {
		List<VariableReference> elementList = new ArrayList<>();
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
		return elementList;
	}

	private Method searchGetterForArrayElement(TestCase test, VariableReference realParentRef, int opcodeRead) {
		Set<Method> targetMethods = new HashSet<>();
		Class<?> clazz = realParentRef.getGenericClass().getRawClass();
		for (Method method : clazz.getMethods()) {
			boolean isValid = checkValidArrayElementGetter(method, opcodeRead);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}

	private Method searchSetterForArrayElement(TestCase test, VariableReference realParentRef, int opcodeWrite) {
		Set<Method> targetMethods = new HashSet<>();
		Class<?> clazz = realParentRef.getGenericClass().getRawClass();
		for (Method method : clazz.getMethods()) {
			boolean isValid = checkValidArrayElementSetter(method, opcodeWrite);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}

	private VariableReference searchArrayElementWritingReference(TestCase test, VariableReference parentVarRef, VariableReference realParentRef, int opcodeWrite) {
		/*
		 * AASTORE, BASTORE, CASTORE, DASTORE, FASTORE, IASTORE, LASTORE, SASTORE
		 */
		for (int i = 0;i < test.size(); i ++) {
			Statement stat = test.getStatement(i);
			// check directly writing
			if (stat instanceof AssignmentStatement) {
				AssignmentStatement assignStat = (AssignmentStatement) stat;
				for (VariableReference varRef : assignStat.getVariableReferences()) {
					if (varRef.equals(parentVarRef)) {
						return assignStat.getReturnValue();
					}
				}
			} 
			// check writing through method call
			else if (stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement) stat;
				VariableReference ref = mStat.getCallee();
				if (ref != null && ref.equals(realParentRef)) {
					boolean isValid = checkValidArrayElementSetter(mStat.getMethod().getMethod(), opcodeWrite);
					if (isValid) {
						return mStat.getReturnValue();
					}
				}
			}
		}
		return null;
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
	private boolean checkValidArrayElementSetter(Method method, 
			int opcodeWrite) {
		boolean isValid = false;
		DepVariable parentVar = this.parents.get(0).var;
		if (parentVar.getType() != DepVariable.INSTANCE_FIELD && parentVar.getType() != DepVariable.STATIC_FIELD) {
			return false;
		}
		String className = method.getDeclaringClass().getCanonicalName();
		String methodName = method.getName() + MethodUtil.getSignature(method);
		FieldInsnNode parentFNode = (FieldInsnNode) parentVar.getInstruction().getASMNode();
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getAllInstructionsAtMethod(className, methodName);
		if (insList == null && RuntimeInstrumentation.checkIfCanInstrument(className)) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getAllInstructionsAtMethod(className, methodName);
		}
		if (insList != null) {
			for (BytecodeInstruction ins : insList) {
				// *ASTORE
				if (ins.getASMNode().getOpcode() == opcodeWrite) {
					for (int i = 0; i < ins.getOperandNum(); i++) {
						BytecodeInstruction defIns = ins.getSourceOfStackInstruction(i);
						if (defIns.getASMNode().getOpcode() == Opcodes.ACONST_NULL) {
							return false;
						}
						if (defIns.isFieldUse()) {
							FieldInsnNode fnode = ((FieldInsnNode) defIns.getASMNode());
							if (fnode.name.equals(parentFNode.name)) {
								isValid = true;
							}
						}
					}
				}
			}
		}
		return isValid;
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
	private boolean checkValidArrayElementGetter(Method method, int opcodeRead) {
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

	private VariableReference searchArrayElementReadingReference(TestCase test, VariableReference parentVarRef, VariableReference realParentRef, int opcodeRead) {
		/*
		 * AALOAD, BALOAD, CALOAD, DALOAD, FALOAD, IALOAD, LALOAD, SALOAD
		 */
		for (int i = 0;i < test.size(); i ++) {
			Statement stat = test.getStatement(i);
			// check reading through method call
			if (stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement) stat;
				Method method = mStat.getMethod().getMethod();
				VariableReference ref = mStat.getCallee();
				if (ref != null && ref.equals(realParentRef)) {
					boolean isValid = checkValidArrayElementGetter(method, opcodeRead);
					if (isValid) {
						return mStat.getReturnValue();
					}
				}
			}
		}
		return null;
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

	@Override
	public List<VariableReference> findCorrespondingVariables(TestCase test, boolean isLeaf, VariableReference callerObject, Map<DepVariableWrapper, List<VariableReference>> map) {
		Statement stat = test.getStatement(callerObject.getStPosition());
		if(stat instanceof NullStatement) {
			List<VariableReference> list = new ArrayList<>();
			list.add(stat.getReturnValue());
			return list;
		}
		
		if(this.var.getType() == DepVariable.PARAMETER) {
			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
			if(!usedArrayElementList.isEmpty()) {
				return usedArrayElementList;
			}
		}
		
		if (callerObject instanceof ArrayReference) {
			int opcodeRead = this.var.getInstruction().getASMNode().getOpcode();			
			int opcodeWrite = getCorrespondingWriteOpcode(opcodeRead);
			VariableReference realParentRef = null;
			Statement statement = test.getStatement(callerObject.getStPosition());

			if (statement instanceof MethodStatement) {
				MethodStatement mStatement = (MethodStatement) statement;
				realParentRef = mStatement.getCallee();
			}
			
			List<VariableReference> usedArrayElementList = searchUsedArrayElementReference(test, callerObject);
			if(!usedArrayElementList.isEmpty()) {
				return usedArrayElementList;
			}
			
			double prob = Randomness.nextDouble();
			if (realParentRef  != null && prob > 0.8) {
				/**
				 * check reused array element
				 */
				VariableReference usedArrayRef = isLeaf
						? searchArrayElementWritingReference(test, callerObject, realParentRef, opcodeWrite)
						: searchArrayElementReadingReference(test, callerObject, realParentRef, opcodeRead);
				if (usedArrayRef != null) {
					VariableReference generatedVariable = isLeaf ? null : usedArrayRef;
					List<VariableReference> vars = new ArrayList<>();
					if(generatedVariable != null) {
						vars.add(generatedVariable);						
					}
					return vars;
				}

			}
		}
		
		return new ArrayList<>();
	}
	
	@Override
	public VariableReference find(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map) {
		return null;
	}
}
