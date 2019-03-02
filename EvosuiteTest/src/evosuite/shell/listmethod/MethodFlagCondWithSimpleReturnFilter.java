package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.evosuite.Properties;
import org.evosuite.classpath.ResourceList;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.experiment.SFConfiguration;
import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.OpcodeUtils;

public class MethodFlagCondWithSimpleReturnFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(MethodFlagCondWithSimpleReturnFilter.class);
	
	protected boolean checkInvokedMethod(ClassLoader classLoader, AbstractInsnNode insn, int level) throws AnalyzerException, IOException {
		if (level <= 0) {
			return false;
		}
		System.out.println();
		if (!CollectionUtil.existIn(insn.getOpcode(), Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESTATIC,
				Opcodes.INVOKESPECIAL)) {
			return false;
		}
		MethodInsnNode methodInsn = (MethodInsnNode) insn;
		String className = methodInsn.owner.replace("/", ".");
		String methodName = CommonUtility.getMethodName(methodInsn.name, methodInsn.desc);
		if (CollectionUtil.existIn(className, String.class.getName(), File.class.getName(),
				HashMap.class.getName(), ArrayList.class.getName(), HashSet.class.getName(),
				Collection.class.getName(), List.class.getName())) {
			return false;
		}
		
		MethodNode node = getMethod(classLoader, methodInsn, className);
		if (node == null) {
			return false;
		}
		
		try {
//			GraphPool.clearAll();
			ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			if (cfg == null) {
				BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
				bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
				bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
				cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
//			ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
			}
			
			if (CollectionUtil.getSize(cfg.getBranches()) <= 1) {
				return false;
			}
			Set<BytecodeInstruction> exitPoints = cfg.getExitPoints();
			for (BytecodeInstruction exit : exitPoints) {
				if (OpcodeUtils.isReturnInsn(exit.getASMNode().getOpcode())) {
					AbstractInsnNode prev = getPreviousInsnNode(exit.getASMNode());
					if (prev instanceof MethodInsnNode) {
						return checkInvokedMethod(classLoader, prev, level - 1);
					}
					if (CollectionUtil.existIn(prev.getOpcode(), Opcodes.ICONST_0, Opcodes.ICONST_1)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			log.debug("error!!", e);
			return false;
		}
	}
	
	private AbstractInsnNode getPreviousInsnNode(AbstractInsnNode insn) {
		AbstractInsnNode prevNode = insn.getPrevious();
//		while (prevNode != null && ((prevNode instanceof LabelNode) 
//				|| (prevNode instanceof LineNumberNode))) {
//			prevNode = prevNode.getPrevious();
//		}
		return prevNode;
	}
	
	private MethodNode getMethod(ClassLoader classLoader, MethodInsnNode methodInsn, String className) throws IOException {
		InputStream is = null;
		try {
			if (methodInsn.owner.startsWith("java")) {
				is = ResourceList.getInstance(this.getClass().getClassLoader()).getClassAsStream(className);
			} else {
				is = ResourceList.getInstance(classLoader).getClassAsStream(className);
			}
			if (is == null) {
				is = getClassAsStream(className);
			}
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;
			for (MethodNode m : l) {
				if (m.name.equals(methodInsn.name) && m.desc.equals(methodInsn.desc)) {
					if ((m.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
						return null;
					}
					return m;
				}
			}
		} finally {
			if (is != null) {
				is.close(); 
			}
		}
		return null;
	}
	
	
	private InputStream getClassAsStream(String name) throws IOException {
		String path = name.replace('.', '/') + ".class";
		String windowsPath = name.replace(".", "\\") + ".class";
		String[] cpEntries = Properties.CP.split(File.pathSeparator);
		for (String cpEntry : cpEntries) {
			if (cpEntry.endsWith(".jar")) {
				JarFile jar = new JarFile(cpEntry);
				JarEntry entry = jar.getJarEntry(path);
				if (entry != null) {
					InputStream is = null;
					try {
						is = jar.getInputStream(entry);
						return is;
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
		return null;
	}
}
