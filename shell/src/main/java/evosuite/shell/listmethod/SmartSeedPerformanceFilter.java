package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.ConstantPoolManager;
import org.evosuite.seeding.StaticConstantPool;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.FileUtils;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

/**
 * Aims to pick out methods where EvoSeed should perform better than Evosuite.
 *
 */
public class SmartSeedPerformanceFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(SmartSeedPerformanceFilter.class);

	private static final int CONSTANT_COUNT_THRESHOLD = 50;
	private static final int BRANCH_COUNT_THRESHOLD = 1;
	
	private static Map<String,Long> classToNumberOfConstants = new HashMap<>();
	
	private static Set<String> primitiveTypes = new HashSet<>();
	
	static {
		primitiveTypes.add(int.class.toString());
		primitiveTypes.add(long.class.toString());
		primitiveTypes.add(float.class.toString());
		primitiveTypes.add(double.class.toString());
		primitiveTypes.add(char.class.toString());
		primitiveTypes.add(boolean.class.toString());
		primitiveTypes.add(byte.class.toString());
		primitiveTypes.add(short.class.toString());
	}
	
	public SmartSeedPerformanceFilter() {
	}
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode methodNode,
			ClassNode classNode) throws AnalyzerException, IOException, ClassNotFoundException {
		DependencyAnalysis.clear();
		clearAllPools();
		
		if (className.contains("NestedObjectConstructionTest")) {
			System.currentTimeMillis();
		}
		
		// New logic to filter methods
		// 1) Check if the number of constants meets a predefined threshold
		// 2) Check if the branch has input-related instructions in branch operands
		//    There is a distinction between "static" and "dynamic" branches, but it's not relevant
		//    for the purposes of the filter - we want all such branches.
		// What qualifies as an "input-related" branch? We want to capture branches of the form
		// a) if (input.x.y.z == ...) { ... }
		// b) if (someMethod(input) == ...) { ... }
		// c) if (someMethod(input.x()) == ...) { ... }
		
		/*
		 * This section adds context to the list of instructions
		 * - Add data and control flow information (in CFG) to this list of instructions 
		 */
		ActualControlFlowGraph cfg = getCfg(className, methodName, classLoader, methodNode);
        
        // Get instructions for target method
        BytecodeInstructionPool instructionPool = BytecodeInstructionPool.getInstance(classLoader);
        List<BytecodeInstruction> instructions = instructionPool.getAllInstructionsAtMethod(className, methodName);
        
        // Force Evosuite to load specific class and method
		forceEvosuiteToLoadClassAndMethod(className, methodName);
		
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg);
		
		int numberOfEligibleBranches = 0;
		for (BytecodeInstruction branch : branches) {
			List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
			// Possible cases:
			// 1) The operand is a parameter (e.g. if (methodParam == ...))
			// 1a) The operand is a (transitive) field of a parameter (e.g. if (methodParam.foo.bar.baz == ...))
			// 2) The operand is a method call from a parameter (e.g. if (methodParam.foo() == ...))
			// 2a) The operand is a (transitive) method call from a parameter (e.g. if (methodParam.foo().bar() == ...))
			// 3) The operand is a method call on a parameter e.g. if (foo(methodParam) == ...))
			// 3a) The operand is a method call on a parameter (transitive) field e.g. if (foo(methodParam.foo().bar) == ...))
			// We need to check if any of them fit.
			
			// We structure it in this manner so that we only do the computations
			// if the simpler cases fail.
			for (BytecodeInstruction operand : operands) {
				boolean isOperandParameter = operand.isParameter();
				if (isOperandParameter) {
					numberOfEligibleBranches++;
					break;
				}
				
				boolean isOperandParameterTransitiveField = false;
				if (isOperandParameterTransitiveField) {
					numberOfEligibleBranches++;
					break;
				}
				
				boolean isMethodCall = operand.isMethodCall();
				if (!isMethodCall) {
					break;
				}

				boolean isFromParameter = operand.getSourceOfMethodInvocationInstruction().isParameter();			
				boolean isMethodCallFromParameter = (isMethodCall && isFromParameter);
				if ((isMethodCallFromParameter)) {
					numberOfEligibleBranches++;
					break;
				}
				

				boolean isMethodCallOnParameter = false;
				if (isMethodCallOnParameter) {
					numberOfEligibleBranches++;
					break;
				}
			}
		}
		
		// At this point, the dependency analysis should be complete (assumed).
		// Now we wish to check how many constants the class has.
		long numberOfConstantsInClass = getNumberOfConstantsInClass(className);
		boolean isNumberOfConstantsOverThreshold = (numberOfConstantsInClass >= CONSTANT_COUNT_THRESHOLD);
		boolean isNumberOfEligibleBranchesOverThreshold = (numberOfEligibleBranches >= BRANCH_COUNT_THRESHOLD);
		
		log.debug("[" + className + "#" + methodName + "]: {" + numberOfConstantsInClass + ", " + numberOfEligibleBranches + "}");
		return isNumberOfConstantsOverThreshold && isNumberOfEligibleBranchesOverThreshold;
	}
	
	private void forceEvosuiteToLoadClassAndMethod(String className, String methodName) {
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			Properties.TARGET_CLASS = className;
			Properties.TARGET_METHOD = methodName;
			DependencyAnalysis.analyzeClass(className, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	private ActualControlFlowGraph getCfg(String className, String methodName, ClassLoader classLoader, MethodNode methodNode) throws AnalyzerException {
		GraphPool graphPool = GraphPool.getInstance(classLoader);
		ActualControlFlowGraph cfg = graphPool.getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, methodNode);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			graphPool = GraphPool.getInstance(classLoader);
			cfg = graphPool.getActualCFG(className, methodName);
		}
		return cfg;
	}
	
	private List<ConstantPool> getConstantPools() {
		ConstantPool staticConstantsInClass = ConstantPoolManager.pools[0];
		ConstantPool staticConstantsOutsideClass = ConstantPoolManager.pools[1];
		List<ConstantPool> pools = new ArrayList<>();
		pools.add(staticConstantsInClass);
		pools.add(staticConstantsOutsideClass);
		return pools;
	}
	
	private List<BytecodeInstruction> getEligibleBranchesInMethod(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg); 
		List<BytecodeInstruction> eligibleBranches = new ArrayList<>();
		for (BytecodeInstruction branch : branches) {
			boolean isBranchHasConstantOperands = isBranchHasConstantOperands(branch, cfg, node);
			if (isBranchHasConstantOperands) {
				eligibleBranches.add(branch);
			}
		}
		
		return eligibleBranches;
	}
	
	private long getNumberOfConstantsInClass(String className) {
		String key = getKey(className);
		if (classToNumberOfConstants.containsKey(key)) {
			return classToNumberOfConstants.get(key);
		}
		
		// Else we have not encountered this class before
		// Manually count from pool
		// pool[0] is for constants from inside the class
		// pool[1] is for constants from outside the class (e.g. constants from other classes)
		ConstantPool staticConstantsInClass = ConstantPoolManager.pools[0];
		ConstantPool staticConstantsOutsideClass = ConstantPoolManager.pools[1];
		boolean isPoolInstanceOfStaticConstantPool = (staticConstantsInClass instanceof StaticConstantPool);
		boolean isSecondPoolInstanceOfStaticConstantPool = (staticConstantsOutsideClass instanceof StaticConstantPool);
		if (!isPoolInstanceOfStaticConstantPool) {
			// Error
			return -1;
		}
		if (!isSecondPoolInstanceOfStaticConstantPool) {
			// Error
			return -1;
		}
		
		StaticConstantPool insideClassPool = (StaticConstantPool) staticConstantsInClass;
		StaticConstantPool outsideClassPool = (StaticConstantPool) staticConstantsOutsideClass;
		long constantCount = (insideClassPool.poolSize() + outsideClassPool.poolSize());
		classToNumberOfConstants.put(key,  constantCount);
		return constantCount;
	}
	
	/**
	 * Returns the number of branches with constant operands.
	 * @param className 
	 * @param methodName
	 * @return
	 */
	private long getNumberOfEligibleBranchesInMethod(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg); 
		int numberOfEligibleBranches = 0;
		for (BytecodeInstruction branch : branches) {
			boolean isBranchHasConstantOperands = isBranchHasConstantOperands(branch, cfg, node);
			if (isBranchHasConstantOperands) {
				numberOfEligibleBranches++;
			}
		}
		
		return numberOfEligibleBranches;
	}
	
	/**
	 * Returns true if the branch has at least one constant operand, but not
	 * all constant operands i.e.
	 * 
	 * while (x == 3) { .. } => true
	 * while (true) { .. } => false
	 * while (x == y) { .. } => false
	 * @param branch
	 * @return
	 */
	private boolean isBranchHasConstantOperands(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode node) {
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, node);
		int numConstantOperands = getNumberOfConstantOperandsFromBranch(branch, cfg, node);
		
		boolean isAllOperandsConstant = (numConstantOperands == operands.size());
		boolean isAtLeastOneOperandConstant = (numConstantOperands > 0);
		
		return (!isAllOperandsConstant && isAtLeastOneOperandConstant);
	}
	
	private int getNumberOfConstantOperandsFromBranch(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode node) {
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, node);
		List<BytecodeInstruction> constantOperands = new ArrayList<>();
		for (BytecodeInstruction operand : operands) {
			boolean isConstantOperand = operand.isConstant();
			if (isConstantOperand) {
				constantOperands.add(operand);
			}
		}
		
		return constantOperands.size();
	}

	private static List<BytecodeInstruction> getOperandsFromBranch(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode node) {
		List<BytecodeInstruction> operands = new ArrayList<>();
		Frame frame = branch.getFrame();
		
		for (int i = 0; i < branch.getOperandNum(); i++) {
			int index = frame.getStackSize() - i - 1;
			Value val = frame.getStack(index);

			if (val instanceof SourceValue) {
				SourceValue srcValue = (SourceValue) val;
				/**
				 * get all the instruction defining the value.
				 */
				for (AbstractInsnNode insNode : srcValue.insns) {
					BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);

					if (defIns != null) {
						operands.add(defIns);
					}
				}
			}
		}
		return operands;
	}
	
	/*
	 * Returns a key generated from a combination of the project id and class name.
	 */
	private String getKey(String className) {
		String projectId = EvosuiteForMethod.projectId.toString().split("_")[0];
		return projectId + "#" + className;
	}
	
	private void clearAllPools() {
		for (int j = 0; j < 2; j++) {
			if (ConstantPoolManager.pools[j] instanceof StaticConstantPool) {
				StaticConstantPool pool = (StaticConstantPool) ConstantPoolManager.pools[j];
				pool.clear();
			}
		}
	}
	
	private static boolean isPrimitive(String inputType) {
		if (primitiveTypes.contains(inputType)) {
			return true;
		}

		// String
		if (inputType.contains("java.lang.String")) {
			return true;
		}

		return false;
	}
}
