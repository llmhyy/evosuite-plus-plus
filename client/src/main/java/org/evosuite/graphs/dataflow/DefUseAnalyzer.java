package org.evosuite.graphs.dataflow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.coverage.dataflow.DefUseFactory;
import org.evosuite.coverage.dataflow.DefUsePool;
import org.evosuite.coverage.dataflow.Definition;
import org.evosuite.coverage.dataflow.Use;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;


public class DefUseAnalyzer {
	
	private static Set<String> analizedList = new HashSet<>();
	
	public static void resetSingleton() {
		analizedList.clear();
	}
	
	public static List<BytecodeInstruction> getDefFromUse(BytecodeInstruction insOfuse) {
		String className = insOfuse.getClassName();
		String methodName = insOfuse.getMethodName();
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		MethodNode node = getMethodNode(classLoader, className, methodName);
		
		DefUseAnalyzer defUseAnalyzer = new DefUseAnalyzer();
		defUseAnalyzer.analyze(classLoader, node, className, methodName, node.access);
		Use use = DefUseFactory.makeUse(insOfuse);
		// Ignore method parameter
		List<Definition> defs = DefUsePool.getDefinitions(use);
		
		if(defs == null) return null;

		List<BytecodeInstruction> list = new ArrayList<BytecodeInstruction>();
		for(Definition def: defs) {
			ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			BytecodeInstruction defInstruction = convert2BytecodeInstruction(cfg, node, def.getASMNode());
			if(insOfuse.getInstructionId() > defInstruction.getInstructionId()) {
				list.add(defInstruction);				
			}
			
		}
		
		return list;
	}
	
	public static BytecodeInstruction convert2BytecodeInstruction(ActualControlFlowGraph cfg, MethodNode node,
			AbstractInsnNode ins) {
		AbstractInsnNode condDefinition = (AbstractInsnNode)ins;
		BytecodeInstruction defIns = cfg.getInstruction(node.instructions.indexOf(condDefinition));
		return defIns;
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
//					System.currentTimeMillis();
				}
				System.currentTimeMillis();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void analyze(ClassLoader classLoader, MethodNode mn, String className,
	        String methodName, int access) {
		String methodId = String.format("%s#%s%s", className, methodName, mn.desc);
		if (analizedList.contains(methodId)) {
			return;
		}
		
		RawControlFlowGraph completeCFG = GraphPool.getInstance(classLoader).getRawCFG(className,
		                                                                               methodName);
		Iterator<AbstractInsnNode> j = mn.instructions.iterator();
		while (j.hasNext()) {
			AbstractInsnNode in = j.next();
			for (BytecodeInstruction v : completeCFG.vertexSet()) {
			    if (in.equals(v.getASMNode()) && v.isDefUse()) {
					boolean isValidDU = false;

//					if(v.isLocalArrayDefinition()) {
//						LoggingUtils.getEvoLogger().info(
//							"LOCAL ARRAY VAR DEF " + v.toString()+" loaded by "+v.getSourceOfStackInstruction(2).toString());
//					}
					
					if (v.isMethodCallOfField()) {
						// keep track of field method calls, though we do not
						// know
						// how to handle them at this point during the analysis
						// (need complete CCFGs first)
						isValidDU = DefUsePool.addAsFieldMethodCall(v);
					} else {
						// keep track of uses
						if (v.isUse())
							isValidDU = DefUsePool.addAsUse(v);
						// keep track of definitions
						if (v.isDefinition())
							isValidDU = DefUsePool.addAsDefinition(v) || isValidDU;
					}
				}
			}
		}
		analizedList.add(methodId);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private int getNextLocalVariable(MethodNode mn) {
		int var = 1;
		List<LocalVariableNode> nodes = mn.localVariables;
		for(LocalVariableNode varNode : nodes) {
			if(varNode.index >= var) {
				var = varNode.index + 1;
			}
		}
		return var;
	}


}
