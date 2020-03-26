package evosuite.shell.listmethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ResourceList;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.utils.LoggerUtils;

public class CallsRecursiveMethodFilter implements IMethodFilter {
	private static Logger log = LoggerUtils.getLogger(CallsRecursiveMethodFilter.class);

	@Override
	public List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader)
			throws AnalyzerException, IOException {
		InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getClassAsStream(targetClass.getName());
		List<String> validMethods = new ArrayList<String>();

		try {
			ClassReader reader = new ClassReader(is);
			ClassNode cn = new ClassNode();
			reader.accept(cn, ClassReader.SKIP_FRAMES);
			List<MethodNode> l = cn.methods;

			for (MethodNode m : l) {
				String methodName = CommonUtility.getMethodName(m);
				
				// Filter out abstract methods
                if ((m.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
                    continue;
                }
	
				if ((m.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
						|| (m.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED
						|| (m.access & Opcodes.ACC_PRIVATE) == 0 /* default */ ) {
					
					// Get CFG
                    String className = targetClass.getName();
                    ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className,
                            methodName);
                    if (cfg == null) {
                        BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
                        bytecodeAnalyzer.analyze(classLoader, className, methodName, m);
                        bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
                        cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
                    }
                    
                    BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
                    List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);
                    for (BytecodeInstruction ins : instructions) {
                    	if (ins.isMethodCall()) {                    
                            // Check if this called method is recursive
                    		RawControlFlowGraph calledCfg = ins.getCalledCFG();
                    		if (calledCfg == null) {
                    			continue;
                    		}
                    		
                    		Boolean isRecursive = false;
                    		String calledName = calledCfg.getMethodName();
                    		String calledClass = calledCfg.getClassName();
                    		Set<BytecodeInstruction> calledInstructions = calledCfg.vertexSet();
                    		for (BytecodeInstruction calledIns : calledInstructions) {
                    			if (calledIns.isMethodCall()) {
                    				if (calledName.equals(calledIns.getCalledMethod()) &&
                    						calledClass.equals(calledIns.getCalledMethodsClass())) {
                    					
                    					System.out.println(calledName);
                    					System.out.println(calledClass);
                    					isRecursive = true;
                    					break;
                    				}
                    			}
                    		}
                    		
                    		if (isRecursive) {
                    			validMethods.add(methodName);
            					break;
                    		}
                    	}
                    }
				} 
			}
		} catch (Exception e) {
			log.info("error", e);
		} finally {
			is.close(); 
		}
		
		return validMethods;
	}
}
