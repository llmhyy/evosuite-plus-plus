package org.evosuite.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodUtil {
	private static Logger log = LoggerFactory.getLogger(MethodUtil.class);
	
	
	/**
	 * 
	 * return a list of parameter types followed by the return type.
	 * 
	 * class name such as com.a.b.Class
	 * primitive type such as int, double, etc.
	 * @param desc
	 * @param ins 
	 * @return
	 */
	public static String[] parseSignature(String desc) {
		// TODO Cheng Yan, also need to parse primitive type like int, char, boolea, etc.
		
		//input:()
		String input = desc.split("[)]")[0];
		input = input.substring(input.indexOf('(') + 1, input.length());
		//output:after ')'
		String[] outputTypes = desc.split("[)]")[1].split(";");
		
		String[] inputTypes = input.split(";");
		List<String> allTypes = new ArrayList<>();
		prase(allTypes,inputTypes);
		prase(allTypes,outputTypes);	
		String[] localSeparateTypes  = allTypes.toArray(new String[0]);
				
		for(int i=0; i<localSeparateTypes.length; i++) {
			if(localSeparateTypes[i].startsWith("L")) {
				localSeparateTypes[i] = localSeparateTypes[i].substring(1, localSeparateTypes[i].length());
				localSeparateTypes[i] = localSeparateTypes[i].replace("/", ".");
			}
			}
		return localSeparateTypes;
	}
	
	private static void prase(List<String> inputs, String[] inputTypes) {
		if(inputTypes.length == 1) {//void
			if(inputTypes[0].equals("") || inputTypes[0].equals("V")) {
				inputs.add("void");
				return;
			}
		}
		for(int i = 0;i < inputTypes.length ;i++) {
			if(!inputTypes[i].startsWith("L")) {
				//&& !inputTypes[i].startsWith("[L")
				//start with primitive type || '[' type
				char[] charList = inputTypes[i].toCharArray();
				boolean isList = false;
				int startIndex = 0;
				for(int j = 0;j < charList.length ;j++) {
					char c = charList[j];
					String clas = null;
					if(!isList && c == '[') {
						startIndex = j;
						isList = true;
						continue;
					}
					switch(c) {
					case '[':
						continue;
					case 'I':
						clas = "int";
						break;
					case 'J':
						clas = "long";
						break;
					case 'F':
						clas = "float";
						break;
					case 'D':
						clas = "double";
						break;
					case 'C':
						clas = "char";
						break;
					case 'Z':
						clas = "boolean";
						break;
					case 'B':
						clas = "byte";
						break;
					case 'S':{
						clas = "short";
						break;
					}
					case 'L':{
						clas = inputTypes[i].substring(j, charList.length);
						j = charList.length - 1;
						break;
						}
					}
					String type = praseInstructionType(isList, j, clas, startIndex);
					inputs.add(type);
					isList = false;
				}
			}else
				//start with 'L'
				inputs.add(inputTypes[i]);
		}
		
	}

	private static String praseInstructionType(boolean isList, int j, String string, int startIndex) {		
		if(isList) {
			if(j - startIndex == 2)
				return string + "[][]";
			else
				return string + "[]";
		}else
			return string;
	}

	public static MethodNode getMethodNode(InstrumentingClassLoader classLoader, String className, String methodName) {
		InputStream is = ResourceList.getInstance(classLoader).getClassAsStream(className);
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;

			for (MethodNode n : l) {
				String methodSig = n.name + n.desc;
				if (methodSig.equals(methodName)) {
					return n;
				}
			}
			
			// Can't find the method in current class
			// Check its parent class
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz.getSuperclass() != null) {
					Class<?> superClazz = clazz.getSuperclass();
					return getMethodNode(classLoader, superClazz.getName(), methodName);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static ActualControlFlowGraph registerMethod(String className, String methodName) {
		Class<?> clazz;
		try {
			clazz = TestGenerationContext.getInstance().getClassLoaderForSUT()
					.loadClass(className);
			return registerMethod(clazz, methodName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ActualControlFlowGraph registerMethod(Class<?> fieldDeclaringClass, String methodName) {
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		String className = fieldDeclaringClass.getName();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		MethodNode innerNode = MethodUtil.getMethodNode(classLoader, className, methodName);
		System.currentTimeMillis();
		BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
		if (cfg == null && innerNode != null) {
			try {
				bytecodeAnalyzer.analyze(classLoader, className, methodName, innerNode);
				Properties.ALWAYS_REGISTER_BRANCH = true;
				bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
				cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
				Properties.ALWAYS_REGISTER_BRANCH = false;
			} catch (Exception e) {
				/**
				 * the cfg (e.g., jdk/library class) is out of our consideration
				 */
				return null;
			}
		}
		
		return cfg;
	}
	
	public static List<String> getInvokedMethods(String targetClass, String methodName) {
		InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getClassAsStream(targetClass);
		List<String> validMethods = new ArrayList<String>();
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> methods = cn.methods;
			for (MethodNode method : methods) {
				if (methodName.equals(CommonUtility.getMethodName(method))) {
					for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
						AbstractInsnNode insnNode = it.next();
						if (CommonUtility.isInvokeMethodInsn(insnNode)) {
							if (insnNode instanceof MethodInsnNode) {
								MethodInsnNode invokedNode = (MethodInsnNode) insnNode;
								validMethods.add(CommonUtility.getMethodId(invokedNode.owner, invokedNode.name + invokedNode.desc));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("error", e);
		}
		return validMethods;
	}
	
	public static String getSignature(Method m) {
		String sig;
		StringBuilder sb = new StringBuilder("(");
		for (Class<?> c : m.getParameterTypes())
			sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
		String str = sb.append(')')
				.append(m.getReturnType() == void.class ? "V"
						: (sig = Array.newInstance(m.getReturnType(), 0).toString()).substring(1, sig.indexOf('@')))
				.toString();
		return str.replace(".", "/");
	}
	
	@SuppressWarnings("rawtypes")
	public static String getSignature(Constructor m) {
		String sig;

		StringBuilder sb = new StringBuilder("(");
		for (Class<?> c : m.getParameterTypes())
			sb.append((sig = Array.newInstance(c, 0).toString()).substring(1, sig.indexOf('@')));
		String str = sb.append(')').append("V").toString();
		
//		.append(m.getReturnType() == void.class ? "V"
//				: (sig = Array.newInstance(m.getReturnType(), 0).toString()).substring(1, sig.indexOf('@')))
//		.toString();
		return str.replace(".", "/");
	}
}
