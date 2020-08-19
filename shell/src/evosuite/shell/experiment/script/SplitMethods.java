package evosuite.shell.experiment.script;

import java.io.IOException;

import org.junit.Test;

import evosuite.shell.utils.TargetMethodIOUtils;

public class SplitMethods {
	String baseDir = System.getProperty("user.dir");

	@Test
	public void selectMethods() throws IOException {
		String excelFile = baseDir + "/experiments/SF100/reports/flag-filtered-wth-GA-involved-branch.xlsx";
		String resultTxt = baseDir + "/experiments/SF100/reports/targetMethods-100methods.txt";
		String inclusiveTxt = baseDir + "/experiments/SF100/reports/targetMethods-invokedMethodFiltered.txt";
		TargetMethodIOUtils.selectMethods(excelFile, resultTxt, inclusiveTxt);
	}
	
	@Test
	public void selectMethodsSet() throws IOException {
		TargetMethodIOUtils.selectMethods(baseDir + "/experiments/SF100/reports/experimentSets/targetMethod_byType_uninstrumentable_100.txt",
				baseDir + "/experiments/SF100/reports/targetMethod_byType_uninstrumentable.txt", 100);
		
		TargetMethodIOUtils.selectMethods(baseDir + "/experiments/SF100/reports/experimentSets/targetMethod_byType_uninterested_100.txt",
				baseDir + "/experiments/SF100/reports/targetMethod_byType_uninterested.txt", 100);
		
		TargetMethodIOUtils.splitMethods(baseDir + "/experiments/SF100/reports/experimentSets/targetMethod_byType_determined", 
				baseDir + "/experiments/SF100/reports/targetMethod_byType_determined.txt", 100);
	}
	
	@Test
	public void selectMethodsSetMath() throws IOException {
//		tool.splitMethods(baseDir + "/experiments/testProjects/reports/targetMethod_byType_determined",
//				baseDir + "/experiments/testProjects/reports/targetMethod_byType_determined.txt", 100);
		TargetMethodIOUtils.selectMethods(baseDir + "/experiments/testProjects/reports/targetMethod_byType_uninstrumentable_100.txt",
				baseDir + "/experiments/testProjects/reports/targetMethod_byType_uninstrumentable.txt", 100);
	}
}
