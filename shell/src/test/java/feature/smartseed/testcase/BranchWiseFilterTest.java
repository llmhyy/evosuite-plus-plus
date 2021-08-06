package feature.smartseed.testcase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.classpath.ResourceList;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.seeding.smart.SeedingApplicationEvaluator;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.utils.MethodUtil;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import common.SF100Project;
import common.TestUtil;
import common.TestUtility;
import evosuite.shell.experiment.SFBenchmarkUtils;

public class BranchWiseFilterTest {
	@Before
	public void beforeTest() {
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
		
		Properties.CLIENT_ON_THREAD = true;
		Properties.COMPUTATION_GRAPH_METHOD_CALL_DEPTH = 0;
	}
	
	@Test
	public void filterTest() throws AnalyzerException, IOException {
		String projectId = SF100Project.P5;
		String[] targetMethods = new String[]{				
				"org.templateit.Poi2ItextUtil#chooseFontFamily(Lorg/apache/poi/hssf/usermodel/HSSFFont;I)I"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void filterTest1() throws AnalyzerException, IOException {
		String projectId = SF100Project.P8;
		String[] targetMethods = new String[]{				
				"fr.unice.gfarce.interGraph.TableStockage#setColumnClass(ILjava/lang/Class;)V"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.DYNAMIC_POOL;
		
	}
	
	@Test
	public void filterTest2() throws AnalyzerException, IOException {
		String projectId = SF100Project.P13;
		String[] targetMethods = new String[]{				
				"org.databene.jdbacl.model.jdbc.DBIndexInfo#addColumn(SLjava/lang/String;)V"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void filterTest3() throws AnalyzerException, IOException {
		String projectId = SF100Project.P13;
		String[] targetMethods = new String[]{				
				"org.databene.jdbacl.DatabaseDialectManager#getDialectForProduct(Ljava/lang/String;)Lorg/databene/jdbacl/DatabaseDialect;"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void filterTest4() throws AnalyzerException, IOException {
		String projectId = SF100Project.P17;
		String[] targetMethods = new String[]{				
				"com.allenstudio.ir.util.XmlElement#getElement(Ljava/lang/String;)Lcom/allenstudio/ir/util/XmlElement;"//17
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void filterTest5() throws AnalyzerException, IOException {
		String projectId = SF100Project.P17;
		String[] targetMethods = new String[]{		
				"com.allenstudio.ir.util.XmlElement#removeElement(Lcom/allenstudio/ir/util/XmlElement;)Lcom/allenstudio/ir/util/XmlElement;"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	@Test
	public void filterTest6() throws AnalyzerException, IOException {
		String projectId = SF100Project.P17;
		String[] targetMethods = new String[]{				
				"com.allenstudio.ir.util.InspirentoUtilities#stringReplaceAll(Ljava/lang/StringBuffer;CLjava/lang/String;)Ljava/lang/StringBuffer;"//17
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void filterTest7() throws AnalyzerException, IOException {
		String projectId = SF100Project.P18;
		String[] targetMethods = new String[]{		
				"org.jsecurity.ri.authc.module.dao.MemoryAuthenticationDAO#getAuthenticationInfo(Ljava/security/Principal;)Lorg/jsecurity/authc/module/AuthenticationInfo;"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void filterTest8() throws AnalyzerException, IOException {
		String projectId = SF100Project.P18;
		String[] targetMethods = new String[]{		
				"org.jsecurity.ri.authz.module.ModularAuthorizer#isAuthorized(Lorg/jsecurity/authz/AuthorizationContext;Lorg/jsecurity/authz/AuthorizedAction;)Z"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		System.out.println("type:NO_POOL");
		assert type == SeedingApplicationEvaluator.NO_POOL;
		
	}
	
	@Test
	public void filterTest36() throws AnalyzerException, IOException {
		String projectId = SF100Project.P36;
		String[] targetMethods = new String[]{				
				"net.sourceforge.schemaspy.Config#isLogoEnabled()Z"
		};
		
		Properties.TARGET_CLASS = targetMethods[0].split("#")[0];//methodName;
		Properties.TARGET_METHOD = targetMethods[0].split("#")[1];//className;
		SFBenchmarkUtils.setupProjectProperties(projectId);
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			DependencyAnalysis.analyzeClass(Properties.TARGET_CLASS, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		int type = SeedingApplicationEvaluator.NO_POOL;
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);
		if(branchesInTargetMethod == null) {
			System.out.println("type:NO_POOL");
			return;
		} 
		for (Branch br : branchesInTargetMethod.keySet()) {
			
			if(br != null && br.getInstruction().getLineNumber() != -1) {					
				type = SeedingApplicationEvaluator.evaluate(br).getBenefiticalType();
				if (type == SeedingApplicationEvaluator.STATIC_POOL) {
					System.out.println("type:STATIC_POOL");
					break;
				}
				else if (type == SeedingApplicationEvaluator.DYNAMIC_POOL) {
					System.out.println("type:DYNAMIC_POOL");
					break;
					}
				}
			}
		assert type == SeedingApplicationEvaluator.STATIC_POOL;
		
	}
	
	protected Set<BytecodeInstruction> getIfBranchesInMethod(ActualControlFlowGraph cfg) {
		Set<BytecodeInstruction> ifBranches = new HashSet<BytecodeInstruction>();
		for (BytecodeInstruction b : cfg.getBranches()) {
			if (b.isBranch()) {
				if (b.getASMNode().getOpcode() == Opcodes.JSR || b.getASMNode().getOpcode() == Opcodes.GOTO) {
					continue;
				}
				ifBranches.add(b);
			}
		}
		return ifBranches;
	}
}
