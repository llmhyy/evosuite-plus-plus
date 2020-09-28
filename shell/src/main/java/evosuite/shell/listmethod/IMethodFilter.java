package evosuite.shell.listmethod;


import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.IOException;
import java.util.List;

public interface IMethodFilter {

	List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader) throws AnalyzerException, IOException;

}
