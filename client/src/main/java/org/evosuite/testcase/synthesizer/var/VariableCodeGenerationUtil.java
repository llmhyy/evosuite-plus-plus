package org.evosuite.testcase.synthesizer.var;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.runtime.System;
import org.evosuite.runtime.instrumentation.RuntimeInstrumentation;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.AbstractStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.DataDependencyUtil;
import org.evosuite.testcase.synthesizer.FieldInitializer;
import org.evosuite.testcase.synthesizer.NonPrimitiveFieldInitializer;
import org.evosuite.testcase.synthesizer.PotentialSetter;
import org.evosuite.testcase.synthesizer.PrimitiveFieldInitializer;
import org.evosuite.testcase.synthesizer.ReflectionUtil;
import org.evosuite.testcase.synthesizer.UsedReferenceSearcher;
import org.evosuite.testcase.variable.FieldReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericField;
import org.evosuite.utils.generic.GenericMethod;

public class VariableCodeGenerationUtil {
//	private static final Logger logger = LoggerFactory.getLogger(VariableCodeGenerationUtil.class);
	
	public static Class<?> getClassForType(org.objectweb.asm.Type type) {
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

	private static String extractedArrayString(org.objectweb.asm.Type type) {
		int arrayLength = type.getDimensions();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arrayLength; i++) {
			buffer.append("[");
		}
		return buffer.toString();
	}
	
	public static String convertToShortName(String desc) {
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

	public static Type convertToType(String desc) {
		switch (desc) {
		case "Z":
			return Boolean.TYPE;
		case "B":
			return Byte.TYPE;
		case "C":
			return Character.TYPE;
		case "S":
			return Short.TYPE;
		case "I":
			return Integer.TYPE;
		case "J":
			return Long.TYPE;
		case "F":
			return Float.TYPE;
		case "D":
			return Double.TYPE;
		default:
			Class<?> clazz;
			String str = desc.substring(1, desc.length()-1);
			String className = str.replace("/", ".");
			try {
				clazz = Class.forName(className, true,
						TestGenerationContext.getInstance().getClassLoaderForSUT());
			} catch (ClassNotFoundException e) {
				clazz = null;
				e.printStackTrace();
			}
			return clazz;
		}
	}
	
	
	public static VariableReference generateFieldGetterInTest(TestCase test, VariableReference targetObjectReference,
			Map<DepVariableWrapper, List<VariableReference>> map, Class<?> fieldDeclaringClass, Field field, 
			UsedReferenceSearcher usedRefSearcher, Branch b)
			throws ConstructionFailedException {
		/**
		 * make sure this field has been set before get, 
		 * otherwise we may have a null pointer exception after using the retrieved field.
		 */
		VariableReference fieldSetter = usedRefSearcher.searchRelevantFieldWritingReferenceInTest(test, field, targetObjectReference);
		if(fieldSetter == null) {
			try {
				fieldSetter = generateFieldSetterInTest(test, targetObjectReference, map, fieldDeclaringClass, field, false);
				if(fieldSetter != null) {
					Statement s = test.getStatement(fieldSetter.getStPosition());
					/**
					 * if the field setter is a constructor, thus, the object represented by the field setter, o_new, should be
					 * the target object reference as the field is now only relevant to o_new.
					 */
					if(s instanceof ConstructorStatement) {
						ConstructorStatement cStat = (ConstructorStatement)s;
						VariableReference relevantParam = null;
//						if(cStat.getParameterReferences().size() == 1) {
//							relevantParam = cStat.getParameterReferences().get(0);
//						}
						
						for(VariableReference vRef: cStat.getParameterReferences()) {
							if(vRef.getType().equals(field.getGenericType())) {
								relevantParam = vRef;
							}
						}
						
						if(relevantParam != null) {
							return relevantParam;
						}
						else {
							List<VariableReference> pList = cStat.getParameterReferences();
							UsedReferenceSearcher searcher = new UsedReferenceSearcher();
							String methodName = cStat.getMethodName() + cStat.getDescriptor();
							List<VariableReference> params = 
									searcher.searchRelevantParameterOfSetterInTest(pList, fieldDeclaringClass.getCanonicalName(), methodName, field);
							
							if(!params.isEmpty()) {
								return params.get(0);
							}
						}
						
						targetObjectReference = fieldSetter;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (ConstructionFailedException e) {
//				printConstructionError(test, null, b);
				e.printStackTrace();
			}
		}
		
		int insertionPostion = -1;
		if(fieldSetter != null) {
			insertionPostion = fieldSetter.getStPosition()+1;
			if(targetObjectReference != null && 
					targetObjectReference.getStPosition() > fieldSetter.getStPosition()) {
				insertionPostion = targetObjectReference.getStPosition() + 1;
			}
		}
		else if (targetObjectReference == null) {
			MethodStatement mStat = test.findTargetMethodCallStatement();
			insertionPostion = mStat.getPosition() - 1;
		}
		else {
			insertionPostion = targetObjectReference.getStPosition() + 1;
		}
		
		Method getter = searchForPotentialGetterInClass(fieldDeclaringClass, field);
		if (getter != null) {
			VariableReference newParentVarRef = null;
			GenericMethod gMethod = new GenericMethod(getter, getter.getDeclaringClass());
			if (targetObjectReference == null) {
				newParentVarRef = TestFactory.getInstance().addMethod(test, gMethod, insertionPostion, 2, false);
			} else {
				newParentVarRef = TestFactory.getInstance().addMethodFor(test, targetObjectReference, gMethod,
						insertionPostion, false);
			}
			return newParentVarRef;
		}
		
		return null;
	}
	
	/**
	 * generate getter in current class
	 */
	public static Method searchForPotentialGetterInClass(Class<?> fieldDeclaringClass, Field field) {
		Set<Method> targetMethods = new HashSet<>();

		for (Method method : fieldDeclaringClass.getMethods()) {
			boolean isValid = DataDependencyUtil.isFieldGetter(method, field);
			if (isValid) {
				targetMethods.add(method);
			}
		}

		return Randomness.choice(targetMethods);
	}
	
	public static boolean isPrimitiveClass(String fieldType) {
		boolean flag = fieldType.equals("int") || fieldType.equals("double") || fieldType.equals("float")
				|| fieldType.equals("long") || fieldType.equals("short") || fieldType.equals("char") || fieldType.equals("byte");
		return flag;
	}
	
	public static boolean isCompatible(String parentType, String subType) {
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
	
	/**
	 * If field is not found in current declaring class, we search recursively for
	 * its superclass. For simplicity, we only search for setter, getter,
	 * constructor. in current class instead of the real declaring class of this
	 * field.
	 */
	public static Field searchForField(Class<?> fieldDeclaringClass, String fieldName) {
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
	
	public static void generateElements(Class<?> type, TestCase test, VariableReference usedFieldInTest) {
		
		Method addMethod = null;
		for(Method m: type.getDeclaredMethods()) {
			if(m.getName().equals("add") && m.getParameterCount() == 1) {
				addMethod = m;
				break;
			}
		}
		
		if(addMethod != null) {
			int addElementNum = Randomness.nextInt(0, 3);
			
			for(int i=0; i<addElementNum; i++) {
				GenericMethod gMethod = new GenericMethod(addMethod, type);
				try {
					int position = usedFieldInTest.getStPosition()+1;
					TestFactory.getInstance().addMethodFor(test, usedFieldInTest, gMethod, position, false);
				} catch (ConstructionFailedException e) {
					e.printStackTrace();
				}				
			}
			
		}
		
		System.currentTimeMillis();
		
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * generate setter in current test
	 */
	public static VariableReference generateFieldSetterInTest(TestCase test, VariableReference targetObjectReference,
			Map<DepVariableWrapper, List<VariableReference>> map, Class<?> fieldDeclaringClass, Field field, boolean allowNullValue)
			throws ClassNotFoundException, ConstructionFailedException {
		String className = fieldDeclaringClass.getName();
		List<BytecodeInstruction> insList = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getInstructionsIn(className);

		if (insList == null && RuntimeInstrumentation.checkIfCanInstrument(className)) {
			GraphPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).registerClass(className);
			insList = BytecodeInstructionPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
					.getInstructionsIn(className);
		}
		
		if (insList == null) {
			
			return null;
		}
		
		String targetClassName = checkTargetClassName(field, targetObjectReference);
		Executable setter = searchForPotentialSetterInClass(field, targetClassName);
		if (setter != null && !isTarget(setter)) {
			if(setter instanceof Method){
				GenericMethod gMethod = new GenericMethod((Method)setter, setter.getDeclaringClass());
				VariableReference generatedSetter = null;
				if (targetObjectReference == null) {
					MethodStatement mStat = test.findTargetMethodCallStatement();
					generatedSetter = TestFactory.getInstance().addMethod(test, gMethod, mStat.getPosition() - 1, 2, allowNullValue);
				} else {
					generatedSetter = TestFactory.getInstance().addMethodFor(test, targetObjectReference, gMethod,
							targetObjectReference.getStPosition() + 1, allowNullValue);
				}
				return generatedSetter;
			}
			else if(setter instanceof Constructor){
				GenericConstructor gConstructor = new GenericConstructor((Constructor)setter,
						setter.getDeclaringClass());
				VariableReference returnedVar = TestFactory.getInstance().addConstructor(test, gConstructor,
						targetObjectReference.getStPosition() + 1, 2);

				for (int i = 0; i < test.size(); i++) {
					Statement stat = test.getStatement(i);
					if (stat.references(targetObjectReference)) {
						if (returnedVar.getStPosition() < stat.getPosition()) {
							stat.replace(targetObjectReference, returnedVar);
							replaceMapFromNode2Code(map, targetObjectReference, returnedVar);
						}
						else if (returnedVar.getStPosition() > stat.getPosition() 
								&& stat.getPosition() != targetObjectReference.getStPosition()){
							System.currentTimeMillis();
						}
					}
					
				}
				
				return returnedVar;
			}
		}
		
		return null;
	}
	
	public static void replaceMapFromNode2Code(Map<DepVariableWrapper, List<VariableReference>> map,
			VariableReference oldObject, VariableReference newObject) {
		for(DepVariableWrapper key: map.keySet()) {
			List<VariableReference> list = map.get(key);
			
			if(list.contains(oldObject)) {
				for(int i=0; i<list.size(); i++) {
					if(list.get(i).equals(oldObject)) {
						list.set(i, newObject);
					}
				}
				
			}
		}
		
	}
	
	private static boolean isTarget(Executable setter) {
		String className = setter.getDeclaringClass().getCanonicalName();
		
		String name = null;
		if(setter instanceof Method) {
			name = setter.getName();
		}
		else {
			name = "<init>";
		}
		
		String methodName = name + ReflectionUtil.getSignature(setter);
		
		return className.equals(Properties.TARGET_CLASS) 
				&& methodName.equals(Properties.TARGET_METHOD);
	}
	
	/**
	 * return a method along with one of its parameters to setter the field.
	 * 
	 * @param field
	 * @param fieldOwner
	 * @param targetClass
	 * @param insList
	 * @param operation
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static Executable searchForPotentialSetterInClass(Field field, String targetClassName) throws ClassNotFoundException{
		
		PotentialSetter potentialSetter = DataDependencyUtil.searchForPotentialSettersInClass(field, targetClassName);
		List<Executable> fieldSettingMethods = potentialSetter.setterList;
		List<Map<BytecodeInstruction, List<BytecodeInstruction>>> difficultyList = potentialSetter.difficultyList;
		List<Set<Integer>> numberOfValidParams = potentialSetter.numberOfValidParams;
		
		System.currentTimeMillis();
		
		if(!fieldSettingMethods.isEmpty()){
//			Executable entry = Randomness.choice(fieldSettingMethods);
			double[] scores = new double[fieldSettingMethods.size()];
			for(int i=0; i<scores.length; i++){
				scores[i] = estimateCoverageLikelihood(difficultyList.get(i), numberOfValidParams.get(i).size());

				java.lang.reflect.Parameter[] pList = fieldSettingMethods.get(i).getParameters();
				boolean typeCompatible = false;
				
				for(Integer index: numberOfValidParams.get(i)) {
					scores[i] += 1;
					java.lang.reflect.Parameter p = pList[index];
					Class<?> c = p.getType();
					Class<?> c0 = field.getType();
					
					if(c0.isAssignableFrom(c)) {
						scores[i] += 20;
						typeCompatible = true;
					}
				}
				
				if(fieldSettingMethods.get(i) instanceof Method && typeCompatible) {
					scores[i] += 10;
				}
				
//				System.currentTimeMillis();
			}
			
			double[] probability = normalize(scores);
			double p = Randomness.nextDouble();
//			System.currentTimeMillis();
			int selected = select(p, probability);
			return fieldSettingMethods.get(selected);
		}
		
		return null;
	}
	
	/**
	 * we have three factors to estimate how difficult a setter is to influence some branch in the 
	 * target method, (1) call chain, (2) the control flow in the call chain, (3) the number of mutable 
	 * variables.
	 * 
	 * @param map
	 * @param integer
	 * @return
	 */
	private static double estimateCoverageLikelihood(Map<BytecodeInstruction, List<BytecodeInstruction>> map,
			Integer validParamNum) {
		
		double sum = 0;
		for(BytecodeInstruction ins: map.keySet()) {
			double callchainSize = map.get(ins).size();
			sum += 1/(callchainSize+1);
		}
		
		return (double)(validParamNum+1) * sum;
	}

	private static int select(double p, double[] probability) {
		for(int i=0; i<probability.length; i++){
			if(i==0){
				if(p<=probability[i]){
					return i;
				}				
			}
			else{
				if(probability[i-1]<p && p<=probability[i]){
					return i;
				}
			}
			
		}
		
		return 0;
	}

	private static double[] normalize(double[] scores) {
		double sum = 0;
		for(int i=0; i<scores.length; i++){
			sum += scores[i];
		}
		
		double[] prob = new double[scores.length];
		for(int i=0; i<scores.length; i++){
			prob[i] = scores[i]/sum;
		}
		
		for(int i=1; i<scores.length; i++){
			prob[i] += prob[i-1];
		}
		
		return prob;
	}
	
	public static String checkTargetClassName(Field field, VariableReference targetObjectReference) {
		if(targetObjectReference != null){
			String typeName = targetObjectReference.getType().getTypeName();
			if(typeName.contains("<")) {
				typeName = typeName.substring(0, typeName.indexOf("<"));
				return typeName;
			}
			else {
				return typeName;
			}
		}
		else{
			return field.getDeclaringClass().getCanonicalName();
		}
	}
	
	public static VariableReference generatePublicFieldSetterOrGetter(TestCase test, VariableReference targetObjectReference,
			String fieldType, GenericField genericField, boolean allowNullValue) throws ConstructionFailedException {
		AbstractStatement stmt;
		if (CollectionUtil.existIn(fieldType, "Z", "B", "C", "S", "I", "J", "F", "D")) {
			stmt = addStatementToSetOrGetPublicField(test, fieldType,
					genericField, targetObjectReference, new PrimitiveFieldInitializer(), allowNullValue);
		} else {
			stmt = addStatementToSetOrGetPublicField(test, fieldType,
					genericField, targetObjectReference, new NonPrimitiveFieldInitializer(), allowNullValue);
		}
		

		if (stmt != null && stmt.getReturnValue() != null) {
			if(Collection.class.isAssignableFrom(genericField.getField().getType())) {
				Class<?> fType = genericField.getField().getType();
				System.currentTimeMillis();
				VariableCodeGenerationUtil.generateElements(fType, test, stmt.getReturnValue());
			}
			return stmt.getReturnValue();
		}
		
		return null;
	}
	
	private static AbstractStatement addStatementToSetOrGetPublicField(TestCase test, String fieldType,
			GenericField genericField, VariableReference parentVarRef, FieldInitializer fieldInitializer, boolean allowNullValue) 
					throws ConstructionFailedException {
		MethodStatement mStat = test.findTargetMethodCallStatement();
		int insertionPosition = (parentVarRef != null) ? 
				parentVarRef.getStPosition() + 1 : mStat.getPosition() - 1; 
		
		if(insertionPosition >= mStat.getPosition()) {
			insertionPosition = mStat.getPosition() - 1;
		}
		
		FieldReference fieldVar = null;
		if (genericField.isStatic() || parentVarRef == null) {
			fieldVar = new FieldReference(test, genericField);
		} else {
			fieldVar = new FieldReference(test, genericField, parentVarRef);
		}

		VariableReference objRef = fieldInitializer.assignField(TestFactory.getInstance(), test, fieldType, 
				genericField, insertionPosition, fieldVar, allowNullValue);	
		
		if(objRef == null) {
			System.currentTimeMillis();
		}
		
		AbstractStatement stmt = new AssignmentStatement(test, fieldVar, objRef);		
		test.addStatement(stmt, objRef.getStPosition()+1);
		
		return stmt;
	}
}
