package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.utils.LoggerUtils;

public class ObjectConstructionFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(CallsRecursiveMethodFilter.class);

	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug(String.format("#Method %s#%s", className, methodName));
		
        // 1. Check if there is a non-primitive parameter
        if (FilterHelper.containNonPrimitiveParameter(node.desc)) {
        	return true;
        }

		// Get actual CFG for target method
        ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className,
                methodName);
        if (cfg == null) {
            BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
            bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
            bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
            cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
        }
        
        BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
        List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);
        

        // 2. Check if this method accesses a non-primitive field
        for (BytecodeInstruction ins : instructions) {
        	if (ins.isFieldUse()) {
    			Type t = Type.getType(ins.getFieldType());
    			if (!FilterHelper.considerAsPrimitiveType(t)) {
        			return true;
    			}
        	}
        }

		return false;
	}
}