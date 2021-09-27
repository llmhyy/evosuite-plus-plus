package org.evosuite.testcase.synthesizer.var;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

import javassist.bytecode.Opcode;

public class OtherVariableWrapper extends DepVariableWrapper {

	protected OtherVariableWrapper(DepVariable var) {
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
	
	public VariableReference generateOrFind(TestCase test, boolean isLeaf, VariableReference callerObject,
			Map<DepVariableWrapper, List<VariableReference>> map, Branch b, boolean allowNullValue) {
		int opcode = this.var.getInstruction().getASMNode().getOpcode();
		if(opcode == Opcode.ALOAD ||
				opcode == Opcode.ALOAD_1||
				opcode == Opcode.ALOAD_2||
				opcode == Opcode.ALOAD_3 ||
				opcode == Opcode.DUP ||
				opcode == Opcode.DUP2) {
			for(DepVariableWrapper parentNode: this.parents) {
				if(map.get(parentNode) != null) {
					VariableReference generatedVariable = map.get(parentNode).get(0);
					return generatedVariable;
				}
			}
		}
		else if (opcode == Opcode.INVOKEVIRTUAL || 
				opcode == Opcode.INVOKESPECIAL ||
				opcode == Opcode.INVOKESTATIC || 
				opcode == Opcode.INVOKEDYNAMIC ||
				opcode == Opcode.INVOKEINTERFACE){
			VariableReference generatedVariable = generateMethodCallStatement(test, map, callerObject, allowNullValue);
			return generatedVariable;
		}
		
		return null;
	}

	private VariableReference generateMethodCallStatement(TestCase test, 
			Map<DepVariableWrapper, List<VariableReference>> map, VariableReference callerObject, boolean allowNullValue) {
		int opcode = this.var.getInstruction().getASMNode().getOpcode();
		try {
			MethodInsnNode methodNode = ((MethodInsnNode) this.var.getInstruction().getASMNode());
			String owner = methodNode.owner;
			String fieldOwner = owner.replace("/", ".");
			String fullName = methodNode.name + methodNode.desc;
			Class<?> fieldDeclaringClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(fieldOwner);
			org.objectweb.asm.Type[] types = org.objectweb.asm.Type
					.getArgumentTypes(fullName.substring(fullName.indexOf("("), fullName.length()));
			Class<?>[] paramClasses = new Class<?>[types.length];
			int index = 0;
			for (org.objectweb.asm.Type type : types) {
				Class<?> paramClass = VariableCodeGenerationUtil.getClassForType(type);
				paramClasses[index++] = paramClass;
			}

			if (!fullName.contains("<init>")) {
				Method call = null;
				try {
					call = fieldDeclaringClass.getMethod(fullName.substring(0, fullName.indexOf("(")), paramClasses); 
				}
				catch(Exception e) {}
						
				if(call == null) {
					return null;
				}
				
				VariableReference calleeVarRef = null;
				Map<Integer, VariableReference> paramRefMap = new HashMap<>();

				for (DepVariableWrapper par : this.parents) {
					VariableReference parRef = map.get(par).get(0);
					int position = par.findRelationPosition(this);
					if (position > -1) {
						paramRefMap.put(position, parRef);
					}
				}

				if (opcode == Opcodes.INVOKESTATIC) {
					calleeVarRef = null;
					GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
					VariableReference varRef = TestFactory.getInstance().addMethod(test, genericMethod,
							callerObject.getStPosition() + 1, 1, allowNullValue);
					MethodStatement statement = (MethodStatement) test.getStatement(varRef.getStPosition());
					for (int i = 0; i < statement.getParameterReferences().size();i ++) {
						VariableReference oldParam = statement.getParameterReferences().get(i);
						VariableReference newParam = paramRefMap.get(i);
						if (newParam != null) {
							statement.replace(oldParam, newParam);
							VariableCodeGenerationUtil.replaceMapFromNode2Code(map, oldParam, newParam);
						}
					}
					return varRef;
				} else {
					Statement stat = test.getStatement(callerObject.getStPosition());
					if(stat instanceof NullStatement) {
						return null;
					}
					
					calleeVarRef = paramRefMap.get(0);
					if (calleeVarRef != null) {
						
						Class<?> calleeType = calleeVarRef.getVariableClass();
						Class<?> callObjectType = call.getDeclaringClass();
						
						if(calleeType.isAssignableFrom(callObjectType)) {
							GenericMethod genericMethod = new GenericMethod(call, call.getDeclaringClass());
							VariableReference varRef = TestFactory.getInstance().addMethodFor(test, calleeVarRef, genericMethod,
									calleeVarRef.getStPosition() + 1, allowNullValue);
							MethodStatement statement = (MethodStatement) test.getStatement(varRef.getStPosition());
							for (int i = 0; i < statement.getParameterReferences().size();i ++) {
								VariableReference oldParam = statement.getParameterReferences().get(i);
								VariableReference newParam = paramRefMap.get(i + 1);
								if (newParam != null) {
									statement.replace(oldParam, newParam);
									VariableCodeGenerationUtil.replaceMapFromNode2Code(map, oldParam, newParam);
								}
							}
							return varRef;
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
}