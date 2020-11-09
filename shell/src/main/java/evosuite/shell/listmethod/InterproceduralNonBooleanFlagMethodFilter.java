package evosuite.shell.listmethod;

import java.io.IOException;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import evosuite.shell.listmethod.InterproceduralBooleanFlagMethodFilter.MethodContent;

public class InterproceduralNonBooleanFlagMethodFilter extends MethodFlagCondFilter {
	//TODO Aaron
	protected boolean checkNonBooleanFlagMethod(ClassLoader classLoader, AbstractInsnNode flagDefIns,
			int calledLineInTargetMethod, MethodContent mc, Map<String, Boolean> visitMethods)
					throws AnalyzerException, IOException, ClassNotFoundException {
		return false;
	}
	
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		//TODO Aaron
		return  false;
	}
}
