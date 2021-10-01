package org.evosuite.testcase.synthesizer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.runtime.System;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;

public class UsedReferenceSearcher {
	public VariableReference searchRelevantFieldReadingReferenceInTest(TestCase test, Field field,
			VariableReference targetObject) {
		List<VariableReference> relevantRefs = new ArrayList<VariableReference>();
		List<VariableReference> backuprelevantRefs = new ArrayList<>();

		if (targetObject != null) {
			for (int i = 0; i < test.size(); i++) {
				Statement stat = test.getStatement(i);
				if (stat instanceof MethodStatement) {
					MethodStatement mStat = (MethodStatement) stat;
					VariableReference ref = mStat.getCallee();
					if (ref != null && ref.equals(targetObject)) {
						boolean isValidGetter = DataDependencyUtil.isFieldGetter(mStat.getMethod().getMethod(), field);
						if (isValidGetter) {
							relevantRefs.add(mStat.getReturnValue());
						}
						else {
							VariableReference usedFieldInTest = searchRelevantFieldWritingReferenceInTest(test, field, targetObject);
							backuprelevantRefs.add(usedFieldInTest);
						}
					}
				}
				else if(stat instanceof ConstructorStatement) {
					ConstructorStatement constructorStat = (ConstructorStatement) stat;
					if(constructorStat.getReturnValue().equals(targetObject)) {
						List<VariableReference> params = constructorStat.getParameterReferences();
						String className = constructorStat.getDeclaringClassName();
						String methodName = constructorStat.getMethodName() + constructorStat.getDescriptor();
						if (!params.isEmpty()) {
							List<VariableReference> paramRefs = searchRelevantParameterOfSetterInTest(params, className, methodName, field);
							relevantRefs.addAll(paramRefs);
							System.currentTimeMillis();
						}
					}
				}
			}
		}

		/**
		 * FIXME, ziheng: we need to provide some priority for those parameters here.
		 */
		if (relevantRefs.isEmpty()) {
			if(backuprelevantRefs.isEmpty()) {
				return null;				
			}
			else {
				return Randomness.choice(backuprelevantRefs);
			}
		}

		VariableReference ref = Randomness.choice(relevantRefs);
		return ref;
	}
	
	/**
	 * @author linyun: 
	 * 
	 * Given a field, we find which method call or constructor has the effect of
	 * setting this field.
	 * 
	 * We should check the cascade call to write such
	 * a method. The more controllable a variable, the more likely we return
	 * the variable.
	 * 
	 * @param test
	 * @param field
	 * @param targetObject
	 * @return
	 */
	public VariableReference searchRelevantFieldWritingReferenceInTest(TestCase test, Field field,
			VariableReference targetObject) {

		List<VariableReference> relevantRefs = new ArrayList<VariableReference>();
		
		Map<Integer,VariableReference> refPostions = new HashMap<Integer,VariableReference>();
		
		if (targetObject != null) {
			/**
			 * check the variables passed as parameters to the constructor of targetObject,
			 * which are data-flow relevant to writing the field @{code field}
			 */
			int pos = targetObject.getStPosition();
			Statement s = test.getStatement(pos);
			if (s instanceof NullStatement) {
				TestFactory testFactory = TestFactory.getInstance();
				boolean isSuccess = testFactory.changeNullStatement(test, s);
				if(isSuccess){
					s = test.getStatement(pos);
				}
			}

			if (s instanceof ConstructorStatement) {
				ConstructorStatement constructorStat = (ConstructorStatement) s;
				List<VariableReference> params = constructorStat.getParameterReferences();
				String className = constructorStat.getDeclaringClassName();
				String methodName = constructorStat.getMethodName() + constructorStat.getDescriptor();
				if (!params.isEmpty()) {
					List<VariableReference> paramRefs = searchRelevantParameterOfSetterInTest(params, className, methodName, field);
					relevantRefs.addAll(paramRefs);
					
					for(VariableReference vRef : paramRefs) {
						refPostions.put(pos, vRef);
					}
					System.currentTimeMillis();
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
								refPostions.put(i, pRef);
							}
						}
					}
				} else if (stat instanceof AssignmentStatement) {
					// obj.name = string;
					AssignmentStatement aStat = (AssignmentStatement) stat;
					String returnName = aStat.getReturnValue().getName();
					if (returnName.contains(".")) {
						String fieldName = returnName.substring(returnName.lastIndexOf(".") + 1);
						if (fieldName.equals(field.getName())
								&& aStat.getReturnValue().getAdditionalVariableReference().equals(targetObject)) {
							VariableReference ref = aStat.getValue();
							if (!relevantRefs.contains(ref)) {
								relevantRefs.add(ref);
								refPostions.put(i, ref);
							}
						}
					}

				}
			}
		}

		/**
		 * get the var reference with more compatible types
		 */
		if (relevantRefs.isEmpty())
			return null;
		
		List<VariableReference> compatibles = getCompatibleRef(relevantRefs, field);
		System.currentTimeMillis();
		VariableReference ref = null;
		if(!compatibles.isEmpty()) {
			ref = getLast(compatibles, refPostions);
		}
		else {
			ref = getLast(relevantRefs, refPostions);
		}
		
		return ref;
	}
	
	private VariableReference getLast(List<VariableReference> compatibles, Map<Integer, VariableReference> refPostions) {
		Collections.sort(compatibles, new Comparator<VariableReference>() {
			@Override
			public int compare(VariableReference o1, VariableReference o2) {
				
				return o2.getStPosition() - o1.getStPosition();
			}
		});
		
		int lastPos = 0;
		for(Integer i :refPostions.keySet()) {
			lastPos = i > lastPos ? i : lastPos;
		}
		
		VariableReference ref = refPostions.get(lastPos);
		return ref;
	}

	private List<VariableReference> getCompatibleRef(List<VariableReference> relevantRefs, Field field) {
		List<VariableReference> list = new ArrayList<>();
		for(VariableReference ref: relevantRefs) {
			String refType = ref.getType().getTypeName();
			
			if(refType.contains("<")) {
				refType = refType.substring(0, refType.indexOf("<"));
			}
			
			String fieldType = field.getType().getName();
			if(refType.equals(fieldType)) {
				list.add(ref);
			}
		}
		return list;
	}

	/**
	 * opcode should always be "getfield"
	 * 
	 * @param statement
	 * @param params
	 * @param opcode
	 * @return
	 */
	public List<VariableReference> searchRelevantParameterOfSetterInTest(List<VariableReference> params, String className,
			String methodName, Field field) {
		/**
		 * get all the field setter bytecode instructions in the method. TODO: the field
		 * setter can be taken from callee method of @code{methodName}.
		 */
		List<BytecodeInstruction> cascadingCallRelations = new LinkedList<>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> setterMap = new HashMap<BytecodeInstruction, List<BytecodeInstruction>>();
		Map<BytecodeInstruction, List<BytecodeInstruction>> fieldSetterMap = DataDependencyUtil.analyzeFieldSetter(className, methodName,
				field, 5, cascadingCallRelations, setterMap);
		List<VariableReference> validParams = new ArrayList<VariableReference>();
		if (fieldSetterMap.isEmpty()) {
			return validParams;
		}

		for (Entry<BytecodeInstruction, List<BytecodeInstruction>> entry : fieldSetterMap.entrySet()) {
			BytecodeInstruction setterIns = entry.getKey();
			List<BytecodeInstruction> callList = entry.getValue();
			Set<Integer> validParamPos = DataDependencyUtil.checkValidParameterPositions(setterIns, className, methodName, callList);
			for (Integer val : validParamPos) {
				if (val >= 0) {
					validParams.add(params.get(val));
				}
			}
			System.currentTimeMillis();
		}
		return validParams;
	}

	
}
