package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.utils.LoggerUtils;

public class BranchwiseMethodFillter extends MethodFlagCondFilter {
private static Logger log = LoggerUtils.getLogger(BranchwiseMethodFillter.class);
private static List<String> validStaticMethods = new ArrayList<String>();
private static List<String> validDynamicMethods = new ArrayList<String>();
	
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
        
        // Get instructions for target method
        BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
        List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);

        // Write your filter logic here
        // Return true if the method passes your filter
        for(BytecodeInstruction b : cfg.getBranches()) {
        	Branch br = b.toBranch();
        	
        	int type = SeedingApplicationEvaluator.evaluate(br);
        	if(type == SeedingApplicationEvaluator.STATIC_POOL) {
        		validStaticMethods.add(methodName);
        		return true;
        	}else if(type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
        		validDynamicMethods.add(methodName);
        		return true;
        	}
        	
        	
        	
        }
        
        return false;
	}
}

