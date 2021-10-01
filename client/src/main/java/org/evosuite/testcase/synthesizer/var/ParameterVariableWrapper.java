package org.evosuite.testcase.synthesizer.var;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.runtime.System;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ParameterVariableWrapper extends DepVariableWrapper {

	protected ParameterVariableWrapper(DepVariable var) {
		super(var);
	}

	@Override
	public List<VariableReference> generateOrFindStatement(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		List<VariableReference> list = new ArrayList<>();
		VariableReference var = generateOrFind(test, isLeaf, callerObject, map, b, allowNullValue);
		if(var != null) {
			list.add(var);
		}
		
		return list;
	}
	
	
	/**
	 * parameter statement is supposed to be used only in the target method
	 * invocation
	 * 
	 * @param test
	 * @param position
	 * @param var
	 * @param callerObject
	 * @param map 
	 * @param castSubClass
	 * @return
	 * @throws ConstructionFailedException
	 * @throws ClassNotFoundException
	 */
	public VariableReference generateOrFind(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		
		VariableReference parameter = find(test, allowNullValue, callerObject, map);
		if(parameter != null) {
			Statement s = test.getStatement(parameter.getStPosition());
			if(!(s instanceof NullStatement)) {
				return parameter;				
			}
		}
		
		
		String castSubClass = checkCastClassForParameter();
		if(castSubClass == null) {
			int paramPosition = this.var.getParamOrder() - 1;
			System.currentTimeMillis();
			List<String> recommendations = InterproceduralGraphAnalysis.recommendedClasses.get(paramPosition);
			if(recommendations!=null && !recommendations.isEmpty()) {
				castSubClass = Randomness.choice(recommendations);					
			}
		}
		
		if(castSubClass != null) {
			VariableReference newParameter = null;
			try {
				newParameter = generateParameter(test, castSubClass, allowNullValue);
			} catch (ConstructionFailedException e) {
				e.printStackTrace();
			}

			if (newParameter == null) {
				return callerObject;
			}

			MethodStatement targetStatement = test.findTargetMethodCallStatement();
			if (targetStatement != null) {
				VariableReference oldParamRef = targetStatement.getParameterReferences().get(this.var.getParamOrder() - 1);
				targetStatement.replace(oldParamRef, newParameter);
			}

			return newParameter;
		}
		else {
			/**
			 * find the existing parameters
			 */
			MethodStatement mStat = test.findTargetMethodCallStatement();
			int paramPosition = this.var.getParamOrder();
			VariableReference paramRef = mStat.getParameterReferences().get(paramPosition - 1);
			
			/**
			 * make sure the parameter obj is not null
			 */
			int paramPosInTest = paramRef.getStPosition();
			Statement paramDef = test.getStatement(paramPosInTest);
			if(!allowNullValue && paramDef instanceof NullStatement){
				TestFactory testFactory = TestFactory.getInstance();
				boolean isSuccess = testFactory.changeNullStatement(test, paramDef);
				if(isSuccess){
					paramRef = mStat.getParameterReferences().get(paramPosition - 1);
				}
			}
			
			return paramRef;
		}
	}
	
	private String checkCastClassForParameter() {
		DepVariable var = this.var;
		
		String potentialCastType = null;
		
		if(this.parents.isEmpty()) return null;
		
		DepVariableWrapper parent = this.parents.iterator().next();
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

					if (VariableCodeGenerationUtil.isCompatible(paramType, potentialCastType)) {
						return potentialCastType;
					}
				}
			}
			
			parent = parent.parents.iterator().next();
		}

		return null;
	}
	
	private VariableReference generateParameter(TestCase test, String castSubClass, boolean allowNullValue) throws ConstructionFailedException{

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
			MethodStatement mStat = test.findTargetMethodCallStatement();
			
			if(allowNullValue) {
				if(Randomness.nextDouble() < Properties.NULL_PROBABILITY) {
					paramRef = TestFactory.getInstance().createNull(test, paramClass, mStat.getPosition() - 1, 0);
				}
			}
			else {
				paramRef = TestFactory.getInstance().addConstructor(test, gc, mStat.getPosition() - 1, 2);				
			}
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

	@Override
	public VariableReference find(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map) {
		Statement s = test.getStatement(test.size()-1);
		if(s instanceof MethodStatement) {
			
			int parameterOrder = this.var.getParamOrder();
			
			MethodStatement targetMethodStatement = (MethodStatement)s;
			
			List<VariableReference> paraList = targetMethodStatement.getParameterReferences();
			
			return paraList.get(parameterOrder-1);
		}
		
		return null;
	}
}
