package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.seeding.ConstantPoolManager;
import org.evosuite.seeding.StaticConstantPool;
import org.evosuite.seeding.StaticConstantVariableProbabilityPool;
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

public class ConstantMethodFilter extends MethodFlagCondFilter {
    private static Logger log = LoggerUtils.getLogger(ConstantMethodFilter.class);
    public static final String excelProfileSubfix = "_constantMethod.xlsx";
    private ExcelWriter writer;
    
	public ConstantMethodFilter() {
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		writer = new ExcelWriter(FileUtils.newFile("D:\\linyun\\git_space\\SF100-clean\\evoTest-reports\\all_constantMethod.xlsx"));
//		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data",
				new String[] { "ProjectId", "Class","Method", "ConstantNum" },
				0);
	}
	
    @Override
    protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
            ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
        log.debug(String.format("#Method %s#%s", className, methodName));

//        // Get actual CFG for target method
//        ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className,
//                methodName);
//        if (cfg == null) {
//            BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
//            bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
//            bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
//            cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
//        }
//        
//        // Get instructions for target method
//        BytecodeInstructionPool insPool = BytecodeInstructionPool.getInstance(classLoader);
//        List<BytecodeInstruction> instructions = insPool.getAllInstructionsAtMethod(className, methodName);
        
        // Write your filter logic here
        // Return true if the method passes your filter

		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
				Properties.TARGET_CLASS = className;
				Properties.TARGET_METHOD = methodName;
				DependencyAnalysis.analyzeClass(className, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 3; i++) {
			if (ConstantPoolManager.pools[i] instanceof StaticConstantPool) {
				StaticConstantPool pool = (StaticConstantPool) ConstantPoolManager.pools[i];
				if (pool.getPoolSize(pool) > 200)
				{
					logToExcel(className, methodName, pool.getPoolSize(pool));
					pool.poolClear(pool);
					return true;
				}
			}
		}
        
	return false;
    }
    
    protected void logToExcel(String className, String methodName, long num) throws IOException {
		List<List<Object>> data = new ArrayList<>();
		String methodFullName = className + "#" + methodName;
		List<Object> rowData = new ArrayList<>();
		String pId = EvosuiteForMethod.projectId.toString().split("_")[0];
		rowData.add(pId);
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(num);
		data.add(rowData);
		writer.writeSheet("data", data);
	}
}
