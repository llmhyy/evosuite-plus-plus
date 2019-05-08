package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.DefUseAnalyzer;
import evosuite.shell.utils.LoggerUtils;
import evosuite.shell.utils.OpcodeUtils;

public class NoFlagMethodFilter extends FlagMethodProfilesFilter {
	private static Logger log = LoggerUtils.getLogger(NoFlagMethodFilter.class);
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException {
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
		MethodContent mc = new MethodContent();
		
		/* check parameter types of target method */
		mc.hasPrimitiveParam = hasPrimitiveParam(node, cn);
		
		boolean valid = true;
		Map<String, Boolean> methodValidityMap = new HashMap<>();
		for (BytecodeInstruction insn : cfg.getBranches()) {
			AbstractInsnNode insnNode = insn.getASMNode();
			/* check whether it is a flag condition */
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
					
					//TODO the value could be defined multiple times
					AbstractInsnNode condDefinition = (AbstractInsnNode) srcValue.insns.iterator().next();
					
					if (CommonUtility.isInvokeMethodInsn(condDefinition)) {
						if (checkFlagMethod(classLoader, condDefinition, insn.getLineNumber(), mc, methodValidityMap)) {
							return false;
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
								if (checkFlagMethod(classLoader, lastDef.getASMNode(), insn.getLineNumber(), mc, methodValidityMap)) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		logToExcel(mc, className, methodName);
		return valid;
	}


}
