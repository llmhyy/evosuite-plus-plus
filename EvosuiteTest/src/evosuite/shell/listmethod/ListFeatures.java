package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.evosuite.TestGenerationContext;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.classpath.ResourceList;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.CommonUtility;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class ListFeatures {
	private static Logger log = LoggerUtils.getLogger(ListFeatures.class);

	private ExcelWriter excelWriter;
	
	public static String[] header = new String[]{
			"project_id", 
			"class",
			"method", 
			"branch_id",
			"value", 
			"is_in_loop", 
			"depth", 
			"path_condition"
			};
	
	public ListFeatures() {
		excelWriter = new ExcelWriter(new File("branch_features.xlsx"));
		excelWriter.getSheet("feature", header, 0);
	}
	
	public void execute(String projectId, String branchFile, URLClassLoader classLoader) {
		File file = new File(branchFile);
		if(!file.exists()) {
			log.error("The branch file " + file + " does not exist!");
		}
		
		ExcelReader reader = new ExcelReader(file, 0);
		
		List<BranchFeature> featureList = new ArrayList<>();
		
		Map<String, List<MethodNode>> classMethodMap = new HashMap<>();
		
		List<List<Object>> bc = reader.listData("branch");
		for(List<Object> row: bc) {
			String rowProjectId = (String) row.get(0);
			
			if(!rowProjectId.equals(projectId)) {
				continue;
			}
			
			String className = (String) row.get(1);
			String methodName = (String) row.get(2);
			int branchId = ((Double) row.get(3)).intValue();
			
			List<MethodNode> relevantMethods = parseRelevantMethod(classLoader, className, classMethodMap);
			
			BranchFeature feature;
			try {
				feature = parseBranchFeature(classLoader, className, relevantMethods, 
						methodName, branchId);
				featureList.add(feature);
			} catch (AnalyzerException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			
		}
		
		List<List<Object>> featureLabel = transformFeatureToRawData(featureList);
		if(featureLabel != null) {
			try {
				this.excelWriter.writeSheet("feature", featureLabel);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}

	private BranchFeature parseBranchFeature(ClassLoader classLoader, String className, 
			List<MethodNode> relevantMethods, String methodName, int branchId) throws AnalyzerException, ClassNotFoundException, RuntimeException {
		
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		// Here is where the <clinit> code should be invoked for the first time
		org.evosuite.Properties.TARGET_CLASS = className;
		try {
			DependencyAnalysis.analyzeClass(className, Arrays.asList(cp.split(File.pathSeparator)));			
		}
		// I am not that sure, using DepndencyAnalysis to analyze call graph will have a lot of exception,
		// In this regard, I just need to get the branch id from the cfg. 
		catch(Exception e) {
			e.printStackTrace();
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		
		
		for(MethodNode node: relevantMethods) {
			String mName = CommonUtility.getMethodName(node);
			if(mName.equals(methodName)) {
				log.debug(String.format("#Method %s#%s", className, methodName));
//				GraphPool.clearAll();
				ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
				if (cfg == null) {
					BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
					bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
					bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
					cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
				}
				
				boolean value = branchId > 0? true : false;
				branchId = Math.abs(branchId);
				
				//TODO get branch features in CFG
				System.currentTimeMillis();
				
			}
		}
		
		
		

		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<MethodNode> parseRelevantMethod(URLClassLoader classLoader, String className,
			Map<String, List<MethodNode>> classMethodMap) {
		if(classMethodMap.containsKey(className)) {
			return classMethodMap.get(className);
		}
		else {
			try {
				Class<?> targetClass = classLoader.loadClass(className);
				InputStream is = ResourceList.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
						.getClassAsStream(targetClass.getName());
				try {
					ClassReader reader = new ClassReader(is);
					ClassNode cn = new ClassNode();
					reader.accept(cn, ClassReader.SKIP_FRAMES);
					List<MethodNode> l = cn.methods;
					return l;
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
	}

	private List<List<Object>> transformFeatureToRawData(List<BranchFeature> featureList) {
		// TODO Auto-generated method stub
		return null;
	}
}
