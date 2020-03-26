package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.List;

import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.utils.LoggerUtils;

public class CallsIntMethodFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(CallsIntMethodFilter.class);
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug(String.format("#Method %s#%s", className, methodName));

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
        
        // Check if instruction calls methods that returns INT
        for (BytecodeInstruction ins : instructions) {
        	if (ins.isMethodCall()) {                    
        		String calledName = ins.getCalledMethod();
				String calledDesc = calledName.substring(calledName.indexOf("("));
				Type returnType = Type.getReturnType(calledDesc);
				if (returnType == Type.INT_TYPE) {
					return true;
				}
        	}
        }

		return false;
	}
}
