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
		Properties.TIMEOUT = 1000000;
	}
	
	@Test
	public void testLegitimization1() throws ClassNotFoundException, RuntimeException {
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
		
		assertLegitimization(b, true);
		
	}

}
