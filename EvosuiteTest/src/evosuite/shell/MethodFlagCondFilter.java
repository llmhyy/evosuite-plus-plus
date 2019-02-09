package evosuite.shell;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.CFGFrame;
import org.evosuite.utils.CollectionUtil;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.OpcodeUtils;

public class MethodFlagCondFilter implements IMethodFilter {
	private static Logger log = LoggerUtils.getLogger(MethodFlagCondFilter.class);
	private static final int METHOD_INVOKE_LEVEL = 2;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader) throws IOException {
		InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getClassAsStream(targetClass.getName());
		List<String> validMethods = new ArrayList<String>();
		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;
			for (MethodNode m : l) {
				/* methodName should be the same as declared in evosuite: String methodName = method.getName() + Type.getMethodDescriptor(method); */
				String methodName = CommonUtility.getMethodName(m);
				if ((m.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
					continue;
				}
				if ((m.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
						|| (m.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED
						|| (m.access & Opcodes.ACC_PRIVATE) == 0 /* default */ ) {
					try {
						if (checkCond(classLoader, targetClass.getName(), methodName, m)) {
							validMethods.add(methodName);
						}
					} catch (Exception e) {
						log.info("error!!", e);
					}
				} 
			}
		} finally {
			is.close(); 
		}
		return validMethods;
	}
	
	protected boolean checkCond(ClassLoader classLoader, String className, String methodName, MethodNode node) throws AnalyzerException, IOException {
		log.debug(String.format("#Method %s#%s", className, methodName));
//		GraphPool.clearAll();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}
//		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (CollectionUtil.isNullOrEmpty(cfg.getBranches())) {
			return false;
		} 
		boolean defuseAnalyzed = false;
		for (BytecodeInstruction insn : cfg.getBranches()) {
			AbstractInsnNode insnNode = insn.getASMNode();
			if (CollectionUtil.existIn(insnNode .getOpcode(), Opcodes.IFEQ, Opcodes.IFNE)) {
				StringBuilder sb = new StringBuilder()
							.append(OpcodeUtils.getCode(insnNode.getOpcode()))
							.append(", prev -- ")
							.append(OpcodeUtils.getCode(insnNode.getPrevious().getOpcode()));
				log.info(sb.toString());
				CFGFrame frame = insn.getFrame();
				Value value = frame.getStack(0);
				if (value instanceof SourceValue) {
					SourceValue srcValue = (SourceValue) value;
					AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();
					if (CommonUtility.isInvokeMethodInsn(condDefinition)) {
						if (checkInvokedMethod(classLoader, condDefinition, METHOD_INVOKE_LEVEL)) {
							log.info("!FOUND IT! in method " + methodName);
							return true;
						}
					} else {
						BytecodeInstruction condBcDef = cfg.getInstruction(node.instructions.indexOf(condDefinition));
						if (condBcDef.isUse()) {
							if (!defuseAnalyzed) {
								DefUseAnalyzer instr = new DefUseAnalyzer();
								instr.analyze(classLoader, node, className, methodName, node.access);
								defuseAnalyzed = true;
							}
							Use use = DefUseFactory.makeUse(condBcDef);
							List<Definition> defs = DefUsePool.getDefinitions(use); // null if it is a method parameter.
							Definition lastDef = null;
							for (Definition def : CollectionUtil.nullToEmpty(defs)) {
								if (lastDef == null || def.getInstructionId() > lastDef.getInstructionId()) {
									lastDef = def;
								}
							}
							if (lastDef != null && CommonUtility.isInvokeMethodInsn(lastDef.getASMNode())) {
								if (checkInvokedMethod(classLoader, lastDef.getASMNode(), METHOD_INVOKE_LEVEL)) {
									log.info("!FOUND IT! in method " + methodName);
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	protected boolean checkInvokedMethod(ClassLoader classLoader, AbstractInsnNode condDefinition, int level)
			throws AnalyzerException, IOException {
		return true;
	}

}
