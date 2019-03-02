package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.List;

public interface IMethodFilter {

	List<String> listTestableMethods(Class<?> targetClass, ClassLoader classLoader) throws IOException;

}
