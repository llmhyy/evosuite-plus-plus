package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class BranchwiseMethodFillter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(BranchwiseMethodFillter.class);
	private static List<String> validStaticMethods = new ArrayList<String>();
	private static List<String> validDynamicMethods = new ArrayList<String>();
	private static List<String> visitedClass = new ArrayList<String>();
	
	public static final String excelProfileSubfix = "_branchwiseMethods.xlsx";
	private ExcelWriter writer;

	public BranchwiseMethodFillter() {
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		Properties.APPLY_SMART_SEED = true;
//		Properties.APPLY_GRADEINT_ANALYSIS_IN_SMARTSEED = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		writer = new ExcelWriter(FileUtils.newFile("D:\\linyun\\git_space\\SF100-clean\\evoTest-reports\\all_branchwiseMethods.xlsx"));
//		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data",
				new String[] { "ProjectId", "Class","Method", "Type" },
				0);
	}
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		
		Properties.COMPUTATION_GRAPH_METHOD_CALL_DEPTH = 0;
		
		log.debug(String.format("#Method %s#%s", className, methodName));
		DependencyAnalysis.clear();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();	
		cp = cp.replace('\\', '/');
		try {
			Properties.TARGET_METHOD = methodName;
			Properties.TARGET_CLASS = className;
			DependencyAnalysis.analyzeClass(className, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		// Get actual CFG for target method
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		}
		
		// Get instructions for target method
		BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
		List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);

		// Write your filter logic here
		// Return true if the method passes your filter	
		for (BytecodeInstruction b : getIfBranchesInMethod(cfg)) {
			Branch br = b.toBranch();		
			
			if(br != null && b.getOperandNum() > 0 && b.getLineNumber() != -1) {					
				int type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					validStaticMethods.add(className + "#" + methodName);
					System.out.println("type:STATIC_POOL");
					logToExcel(className, methodName, "STATIC_POOL");
					return true;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					validDynamicMethods.add(className + "#" + methodName);
					System.out.println("type:DYNAMIC_POOL");
					logToExcel(className, methodName, "DYNAMIC_POOL");
					return true;
					}
				}
			}
//		System.out.println("type:NO_POOL");
		logToExcel(className, methodName, "NO_POOL");
		return false;
			
	}
	
	protected void logToExcel(String className, String methodName, String type) throws IOException {
		List<List<Object>> data = new ArrayList<>();
		String methodFullName = className + "#" + methodName;
		List<Object> rowData = new ArrayList<>();
//		rowData.add(EvosuiteForMethod.projectId);
		String pId = EvosuiteForMethod.projectId.toString().split("_")[0];
		rowData.add(pId);
		rowData.add(Properties.TARGET_CLASS);
		rowData.add(Properties.TARGET_METHOD);
//		rowData.add(EvosuiteForMethod.projectName);
//		rowData.add(methodFullName);
//		rowData.add(br.toString());
		rowData.add(type);
		data.add(rowData);
		writer.writeSheet("data", data);
	}
}
