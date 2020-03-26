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

public class CallsIntMethodFilter implements IMethodFilter {
	private static Logger log = LoggerUtils.getLogger(CallsIntMethodFilter.class);

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
                    
                    Set<BytecodeInstruction> instructions = cfg.getBranches();
                    for (BytecodeInstruction ins : instructions) {
                    	if (ins.isMethodCall()) {                    
                            // Check if instruction calls methods that returns INT
                    		String calledName = ins.getCalledMethod();
            				String calledDesc = calledName.substring(calledName.indexOf("("));
            				Type returnType = Type.getReturnType(calledDesc);
            				if (returnType == Type.INT_TYPE) {
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
