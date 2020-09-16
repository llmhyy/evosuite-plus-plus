package evosuite.shell.listmethod;

import evosuite.shell.utils.LoggerUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

public class EqualsMethodFilter extends MethodFlagCondFilter {

	private static Logger log = LoggerUtils.getLogger(EqualsMethodFilter.class);

	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug(String.format("#Method %s#%s", className, methodName));

		// Get actual CFG for target method
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}

		String declaringClass = cfg.getClassName();
		if (declaringClass.equals("java.lang.Object")) {
			return false;
		}
		
		if (cfg.getMethodName().equals("equals(Ljava/lang/Object;)Z")) {
			return true;
		}
		return false;
	}
}
