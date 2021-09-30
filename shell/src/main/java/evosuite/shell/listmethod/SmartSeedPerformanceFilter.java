package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
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
	private enum BranchType {
		NONE,
		STATIC,
		DYNAMIC
	};
	
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
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode node,
			ClassNode cn) throws AnalyzerException, IOException, ClassNotFoundException {
		DependencyAnalysis.clear();
		clearAllPools();
		
		// Logic to filter methods goes here
		// Idea:
		// - We want methods with a large number of constants
		// - We want methods with branches satisfying at least one of these constraints:
		//   - Static pool
		//     - Value preserving and one branch operand is a static constant
		//   - Dynamic pool
		//     - Value preserving and one branch operand is a dynamic constant
		//   - No pool
		//     - Not value preserving
		//   - To sum it up, we care if one of the branch operands is a constant
		//     - Don't care if static/dynamic
		
		/*
		 * This section adds context to the list of instructions
		 * - Add data and control flow information (in CFG) to this list of instructions 
		 */
		GraphPool graphPool = GraphPool.getInstance(classLoader);
		ActualControlFlowGraph cfg = graphPool.getActualCFG(className, methodName);
		if (cfg == null) {
			BytecodeAnalyzer bytecodeAnalyzer = new BytecodeAnalyzer();
			bytecodeAnalyzer.analyze(classLoader, className, methodName, node);
			bytecodeAnalyzer.retrieveCFGGenerator().registerCFGs();
			graphPool = GraphPool.getInstance(classLoader);
			cfg = graphPool.getActualCFG(className, methodName);
		}
        
        // Get instructions for target method
        BytecodeInstructionPool instructionPool = BytecodeInstructionPool.getInstance(classLoader);
        List<BytecodeInstruction> instructions = instructionPool.getAllInstructionsAtMethod(className, methodName);
        
        /*
         * Force Evosuite to load specific class and method
         */
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			Properties.TARGET_CLASS = className;
			Properties.TARGET_METHOD = methodName;
			DependencyAnalysis.analyzeClass(className, Arrays.asList(cp.split(File.pathSeparator)));
		} catch (ClassNotFoundException | RuntimeException e) {
			e.printStackTrace();
		}
		
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg); 
		for (BytecodeInstruction branch : branches) {
			List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, node);
			List<ParameterNode> methodInputs = node.parameters;
			
			if (node.localVariables.size() > 1) {
				System.currentTimeMillis();
			}
		}
		
		// At this point, the dependency analysis should be complete (assumed).
		// Now we wish to check how many constants the class has.
		long numberOfConstantsInClass = getNumberOfConstantsInClass(className);
		boolean isNumberOfConstantsOverThreshold = (numberOfConstantsInClass >= CONSTANT_COUNT_THRESHOLD);
		
		// At this point we assume that the constant requirement is met (else return false). 
		// Next check for branches with constant operands.
		// We check if the number of branches with constant operands meets a certain threshold?
		long numberOfEligibleBranches = getNumberOfEligibleBranchesInMethod(className, methodName, cfg, node);
		boolean isNumberOfEligibleBranchesOverThreshold = (numberOfEligibleBranches >= BRANCH_COUNT_THRESHOLD);
		
		// Since each eligible branch has at least one constant operand (= one constant in the class),
		// the number of constants in the class should never be strictly less than the number of eligible branches.
		// This guard serves as a breakpoint location during debugging.
		if (numberOfConstantsInClass < numberOfEligibleBranches) {
			List<ConstantPool> pools = getConstantPools();
			List<BytecodeInstruction> eligibleBranches = getEligibleBranchesInMethod(className, methodName, cfg, node);
			BytecodeInstruction firstEligibleBranch = eligibleBranches.get(0);
			List<BytecodeInstruction> operands = getOperandsFromBranch(firstEligibleBranch, cfg, node);
			System.currentTimeMillis();
			
			// Since the number of constants in the class is bounded below by the number of eligible branches,
			// a temporary fix we can do for now is to accept the method if the number of eligible branches
			// is over the constant threshold, since then we are guaranteed that the number of constants
			// is over the threshold.
			boolean isNumberOfEligibleBranchesOverConstantThreshold = (numberOfEligibleBranches >= CONSTANT_COUNT_THRESHOLD);
			return isNumberOfEligibleBranchesOverConstantThreshold;
		}
		
		log.debug("[" + className + "#" + methodName + "]: " + numberOfConstantsInClass + " constants, " + numberOfEligibleBranches + " eligible branches.");
		return isNumberOfConstantsOverThreshold && isNumberOfEligibleBranchesOverThreshold;
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
	
	private static BranchType getBranchType(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode node) {
//		2.input exists in operands (static/dynamic)
//		3. When there is a constant in branch operands, the branch can be considered as a static type
//		4. When there are input-related instructions in branch operands, you can consider the branch as a dynamic type
		return BranchType.STATIC;
	}
	
	private static boolean isInputExistsInOperands(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode node) {
		List<ParameterNode> methodInputs = node.parameters;
		List<BytecodeInstruction> branchOperands = getOperandsFromBranch(branch, cfg, node);
		for (ParameterNode input : methodInputs) {
			for (BytecodeInstruction branchOperand : branchOperands) {
				boolean isMatch = (input.name == branchOperand.getVariableName());
				if (isMatch) {
					return true;
				}
			}
		}
		return false;
	}
}
