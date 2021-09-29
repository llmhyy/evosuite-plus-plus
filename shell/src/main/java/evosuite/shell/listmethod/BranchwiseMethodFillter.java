package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.seeding.ConstantPoolManager;
import org.evosuite.seeding.StaticConstantPool;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.EvoTestResult;
import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFBenchmarkUtils;
import evosuite.shell.experiment.SFConfiguration;
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
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		
		Properties.CLIENT_ON_THREAD = true;
		
		String statisticFile = new StringBuilder(Settings.getReportFolder()).append(File.separator)
				.append(EvosuiteForMethod.projectId).append(excelProfileSubfix).toString();
		File newFile = new File(statisticFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		writer = new ExcelWriter(FileUtils.newFile("D:\\linyun\\git_space\\SF100-clean\\evoTest-reports\\all_branchwiseMethods.xlsx"));
//		writer = new ExcelWriter(new File(statisticFile));
		writer.getSheet("data",
				new String[] { "ProjectId", "Class","Method", "Branch-Type" ,"Num"},
				0);
	}
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		
		Properties.COMPUTATION_GRAPH_METHOD_CALL_DEPTH = 0;
		
		log.debug(String.format("#Method %s#%s", className, methodName));
//		DependencyAnalysis.clear();
//		poolClear();
	
		SeedingApplicationEvaluator.cache.clear();
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			Properties.TARGET_METHOD = methodName;
			Properties.TARGET_CLASS = className;

			DependencyAnalysis.analyzeClass(className, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
				
		Map<String,String> branchTypes = new HashMap<String, String>();
		int branchNum = 0;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		
		if(branchesInTargetMethod == null) {
//			logToExcel(className, methodName, branchTypes, branchNum);
			return false;
		}
			
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				int type = SeedingApplicationEvaluator.evaluate(br, null ,null).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					validStaticMethods.add(className + "#" + methodName);
					branchTypes.put(br.getInstruction().toString(), "STATIC_POOL");
					branchNum += 1;
					System.out.println("type:STATIC_POOL");
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					validDynamicMethods.add(className + "#" + methodName);
					branchTypes.put(br.getInstruction().toString(), "DYNAMIC_POOL");
					branchNum += 1;
					System.out.println("type:DYNAMIC_POOL");
					}
				}
			}
		if(branchNum != 0)
			logToExcel(className, methodName, branchTypes , branchNum);
		return false;
			
	}
	
	protected void logToExcel(String className, String methodName, Map<String, String> branchTypes, int branchNum) throws IOException {
		List<List<Object>> data = new ArrayList<>();
		String methodFullName = className + "#" + methodName;
		List<Object> rowData = new ArrayList<>();
		String pId = EvosuiteForMethod.projectId.toString().split("_")[0];
		rowData.add(pId);
		rowData.add(Properties.TARGET_CLASS);
		rowData.add(Properties.TARGET_METHOD);
		
		rowData.add(branchTypes.toString());
		rowData.add(branchNum);
		data.add(rowData);
		writer.writeSheet("data", data);
	}
	
	private void poolClear() {
		for (int j = 0; j < 2; j++) {
			if (ConstantPoolManager.pools[j] instanceof StaticConstantPool) {
				StaticConstantPool pool = (StaticConstantPool) ConstantPoolManager.pools[j];
				if(pool.poolSize() > 400)
					pool.clear();
			}
		}
	}
	
	
	public static List<EvoTestResult> evoTestSingleMethodSmartSeedProbability(String projectId,
			String[] targetMethods, String fitnessAppraoch, int iteration, 
			long seconds, boolean context, Long seed, 
			boolean applyObjectRule,
			String option,
			String strategy,
			String algorithm,
			double primitivePool,
			double dynamicPool,
			boolean applySmartSeed
			) {
		/* configure */
	
		/* run */
		
		String projectName = projectId.substring(projectId.indexOf("_")+1, projectId.length());
		
//		if(!new File(SFConfiguration.sfBenchmarkFolder + File.separator + "1_tullibee").exists()) {
//			System.err.println("The dataset in " + SFConfiguration.sfBenchmarkFolder + " does not exsit!");
//			return null;
//		}
		
		File file = new File(SFConfiguration.sfBenchmarkFolder + "/tempInclusives.txt");
		file.deleteOnExit();
		SFBenchmarkUtils.writeInclusiveFile(file, false, projectName, targetMethods);

		String[] args = new String[] {
				"-Dapply_smart_seed", String.valueOf(applySmartSeed),
				"-"+option,
				"-Dstrategy", strategy,
				"-Dalgorithm", algorithm,
				
//				"-generateSuiteUsingDSE",
//				"-Dstrategy", "DSE",
				
//				"-generateTests",
//				"-Dstrategy", "EMPIRICAL_HYBRID_COLLECTOR",
				
//				"-generateMOSuite",
//				"-Dstrategy", "MOSUITE",
//				"-Dalgorithm", "DYNAMOSA",
				
				
//				"-generateRandom",
//				"-Dstrategy", "random",
//				"-generateSuite",
				"-criterion", fitnessAppraoch,
				"-target", FileUtils.getFilePath(SFConfiguration.sfBenchmarkFolder, projectId, projectName + ".jar"),
				"-inclusiveFile", file.getAbsolutePath(),
				"-iteration", String.valueOf(iteration),
				"-Dadopt_smart_mutation", "true",
				"-Dsearch_budget", String.valueOf(seconds),
				"-Dcriterion", fitnessAppraoch,
				"-Dinstrument_context", String.valueOf(context), 
				"-Dp_test_delete", "0.0",
				"-Dp_test_change", "0.9",
				"-Dp_test_insert", "0.3",
				"-Dp_change_parameter", "0.6",
				"-Dp_functional_mocking", "0",
				"-Dmock_if_no_generator", "false",
				"-Dfunctional_mocking_percent", "0",
				"-Dinstrument_libraries", "true",
				"-Dinstrument_parent", "true",
				"-Dassertions", "false",
				"-Delite", "10",
				"-Dprimitive_pool", String.valueOf(primitivePool),
				"-Ddynamic_pool", String.valueOf(dynamicPool),
//				"-seed", "1556035769590"
				
		};
		
		if(seed != null) {
			args = ArrayUtils.add(args, "-seed");
			args = ArrayUtils.add(args,  seed.toString());
		}
		
		SFBenchmarkUtils.setupProjectProperties(projectId);
		return EvosuiteForMethod.generateTests(args);
	}
	
}
