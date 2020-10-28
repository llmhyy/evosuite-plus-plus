package regression.objectconstruction.testgeneration.testcase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.junit.Before;
import org.junit.Test;

import evosuite.shell.experiment.SFBenchmarkUtils;


public class SF100LegitimizationTest extends ObjectOrientedTest{
	
	@Before
	public void init() {
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10000;
		Properties.CRITERION = new Criterion[]{Criterion.FBRANCH};
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.TIMEOUT = 100000;
	}
	
	@Test
	public void testAuxilaryBranchWithLegitimization() throws ClassNotFoundException, RuntimeException {
		Properties.RANDOM_SEED = 1600079372686l;
		
		setup();
		
		String projectId = "84_ifx-framework";
		String className = "net.sourceforge.ifxfv3.beans.LoanInfoCommon";
		String methodName = "equals(Ljava/lang/Object;)Z";
		int lineNumber = 210;
		
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

		
		Branch b = searchBranch(rankedList, lineNumber);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
//		generateCode(b, true);
		
		assertLegitimization(b, false, false);
		
	}
	
	@Test
	public void testLegitimization2() throws ClassNotFoundException, RuntimeException {
		Properties.RANDOM_SEED = 1600079372686l;
		
		setup();
		
		String projectId = "6_jnfe";
		String className = "br.com.jnfe.core.standalone.DefaultJNFeInstaller";
		String methodName = "attachSeriesNFe(Lbr/com/jnfe/core/Emitente;)V";
		int lineNumber = 1;
		
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

		
		Branch b = searchBranch(rankedList, lineNumber);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
//		generateCode(b, true);
		
		assertLegitimization(b, true, false);
		
	}
	
	@Test
	public void testLegitimizationBad1() throws ClassNotFoundException, RuntimeException {
		Properties.RANDOM_SEED = 1600079372686l;
		
		setup();
		
		String projectId = "83_xbus";
		String className = "net.sf.xbus.protocol.xml.XBUSXMLMessage";
		String methodName = "synchronizeResponseFields(Lnet/sf/xbus/base/xbussystem/XBUSSystem;)V";
		int lineNumber = 230;
		
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

		
		Branch b = searchBranch(rankedList, lineNumber);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
//		generateCode(b, true);
		
		assertLegitimization(b, false, false);
		
	}
	
	@Test
	public void testLegitimizationBad2() throws ClassNotFoundException, RuntimeException {
		Properties.RANDOM_SEED = 1600079372686l;
		
		setup();
		
		String projectId = "85_shop";
		String className = "umd.cs.shop.JSPlanningDomain";
		String methodName = "solve(Lumd/cs/shop/JSPlanningProblem;Ljava/util/Vector;)Lumd/cs/shop/JSPairPlanTSListNodes;";
		int lineNumber = 3;
		
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

		
		Branch b = searchBranch(rankedList, lineNumber);
		System.out.println(b);
		ConstructionPathSynthesizer.debuggerFolder = "D:\\linyun\\test\\";
//		generateCode(b, true);
		
		assertLegitimization(b, true, false);
		
	}

}
