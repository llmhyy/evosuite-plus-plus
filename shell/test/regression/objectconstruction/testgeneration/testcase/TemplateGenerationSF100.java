package regression.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;
import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.junit.Test;

import evosuite.shell.experiment.SFBenchmarkUtils;

public class TemplateGenerationSF100 extends ObjectOrientedTest {
	@Test
	public void testLongTest1() throws ClassNotFoundException, RuntimeException {
		
		Properties.RANDOM_SEED = 1599466414837l;
		
		setup();
		
		String projectId = "84_ifx-framework";
		String className = "net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2";
		String methodName = "equals(Ljava/lang/Object;)Z";
		
		String defaultClassPath = System.getProperty("java.class.path");
		StringBuffer buffer = new StringBuffer();
		List<String> classPaths = SFBenchmarkUtils.setupProjectProperties(projectId);
		for(String classPath: classPaths) {			
//			ClassPathHandler.getInstance().addElementToTargetProjectClassPath(classPath);
			buffer.append(File.pathSeparator + classPath);
		}
		
		String newPath = defaultClassPath + buffer.toString();
		System.setProperty("java.class.path", newPath);
		
		Properties.TARGET_CLASS = className;
		Properties.TARGET_METHOD = methodName;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph4SF100(classPaths);

		//29
		Branch b = rankedList.get(12);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, true);
	}
	
	@Test
	public void testLongTest2() throws ClassNotFoundException, RuntimeException {
		
//		Properties.RANDOM_SEED = 1599466414837l;
		
		setup();
		
		String projectId = "84_ifx-framework";
		String className = "net.sourceforge.ifxfv3.beans.CustPayeeMsgRecChoice";
		String methodName = "equals(Ljava/lang/Object;)Z";
		
		String defaultClassPath = System.getProperty("java.class.path");
		StringBuffer buffer = new StringBuffer();
		List<String> classPaths = SFBenchmarkUtils.setupProjectProperties(projectId);
		for(String classPath: classPaths) {			
//			ClassPathHandler.getInstance().addElementToTargetProjectClassPath(classPath);
			buffer.append(File.pathSeparator + classPath);
		}
		
		String newPath = defaultClassPath + buffer.toString();
		System.setProperty("java.class.path", newPath);
		
		Properties.TARGET_CLASS = className;
		Properties.TARGET_METHOD = methodName;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph4SF100(classPaths);

		//29
		Branch b = rankedList.get(12);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, true);
	}
	
	@Test
	public void testLongTest3() throws ClassNotFoundException, RuntimeException {
		
//		Properties.RANDOM_SEED = 1599466414837l;
		
		setup();
		
		String projectId = "84_ifx-framework";
		String className = "net.sourceforge.ifxfv3.beans.CustAddRqSequence";
		String methodName = "equals(Ljava/lang/Object;)Z";
		
		String defaultClassPath = System.getProperty("java.class.path");
		StringBuffer buffer = new StringBuffer();
		List<String> classPaths = SFBenchmarkUtils.setupProjectProperties(projectId);
		for(String classPath: classPaths) {			
//			ClassPathHandler.getInstance().addElementToTargetProjectClassPath(classPath);
			buffer.append(File.pathSeparator + classPath);
		}
		
		String newPath = defaultClassPath + buffer.toString();
		System.setProperty("java.class.path", newPath);
		
		Properties.TARGET_CLASS = className;
		Properties.TARGET_METHOD = methodName;

		ArrayList<Branch> rankedList = buildObjectConstructionGraph4SF100(classPaths);

		//29
		Branch b = rankedList.get(5);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
		generateCode(b, true);
	}
}
