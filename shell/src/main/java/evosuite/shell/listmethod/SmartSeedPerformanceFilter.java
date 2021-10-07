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
import org.evosuite.coverage.branch.Branch;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.graphs.interprocedural.var.DepVariable;
import org.evosuite.seeding.ConstantPool;
import org.evosuite.seeding.ConstantPoolManager;
import org.evosuite.seeding.StaticConstantPool;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.EvosuiteForMethod;
import evosuite.shell.utils.LoggerUtils;

/**
 * Aims to pick out methods where EvoSeed should perform better than Evosuite.
 *
 */
public class SmartSeedPerformanceFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(SmartSeedPerformanceFilter.class);

	private static final int CONSTANT_COUNT_THRESHOLD = 0;
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
		// At this point, the dependency analysis should be complete (assumed).
		// Now we wish to check how many constants the class has.
		long numberOfConstantsInClass = getNumberOfConstantsInClass(className);
		boolean isNumberOfConstantsOverThreshold = (numberOfConstantsInClass >= CONSTANT_COUNT_THRESHOLD);
		
		List<BytecodeInstruction> eligibleBranches = getEligibleBranchesInMethod(className, methodName, cfg, methodNode);
		long numberOfEligibleBranches = eligibleBranches.size();
		boolean isNumberOfEligibleBranchesOverThreshold = (numberOfEligibleBranches >= BRANCH_COUNT_THRESHOLD);
		
		log.debug("[" + className + "#" + methodName + "]: {" + numberOfConstantsInClass + ", " + numberOfEligibleBranches + ", " + (isNumberOfConstantsOverThreshold && isNumberOfEligibleBranchesOverThreshold) + "}");
		return isNumberOfConstantsOverThreshold && isNumberOfEligibleBranchesOverThreshold;
	}
	
	/**
	 * Returns the "head" of the transitive chain by looking upwards from the given instruction.
	 * For example, given methodParameter.nestedParameter.deeplyNestedParameter.methodCall() (the instruction
	 * corresponding to the method call), this method should return the instruction corresponding to the loading
	 * of the parameter onto the stack.
	 * 
	 * @param operand The instruction to begin looking from.
	 * @return The head of the transitive chain.
	 */
	private static BytecodeInstruction getHeadOfTransitiveChain(BytecodeInstruction operand) {
		// What if the operand is a static variable/field variable of class of method?
		// Need to account for that as well
		
		// The (current) idea is that we do some kind of backtracking.
		// We start at the "bottom" of the chain = the operand. 
		// - If the operand is a method call, go "upwards" using operand.getSourceOfMethodInvocationInstruction
		// - If the operand is a field access, ???
		// - Otherwise, check if the operand is a parameter. If it is, return true, else false.
		// Repeat this process until we arrive at the end (can't go up the chain any further), then check if the head is a parameter.
		BytecodeInstruction currentInstruction = operand;
		boolean isCurrentInstructionMethodCall = currentInstruction.isMethodCall();
		boolean isCurrentInstructionFieldUse = currentInstruction.isFieldUse();
		while (isCurrentInstructionMethodCall || isCurrentInstructionFieldUse) {
			if (isCurrentInstructionMethodCall) {
				currentInstruction = currentInstruction.getSourceOfMethodInvocationInstruction();
			} else if (isCurrentInstructionFieldUse) {
				// Not sure if this works?
				currentInstruction = currentInstruction.getPreviousInstruction();
			}
			
			// We've hit some unexpected state
			// Return a null value (error).
			if (currentInstruction == null) {
				return currentInstruction;
			}
			
			isCurrentInstructionMethodCall = currentInstruction.isMethodCall();
			isCurrentInstructionFieldUse = currentInstruction.isFieldUse();
		}
		return currentInstruction;
	}
	
	
	/**
	 * Checks if the operand matches a "transitive chain" of a parameter i.e. something of the form
	 * parameter.foo().bar.baz() etc. This method will not be able to determine if the operand is something
	 * of the form foo(parameter).bar.baz(). This method will also not be able to determine if the operand was
	 * defined before use in the branch.
	 * 
	 * @param operand The operand to check.
	 * @return {@code true} if the operand is a transitive chain of the parameter, and {@code false} otherwise.
	 */
	private static boolean checkIfTransitiveChainOfParameter(BytecodeInstruction operand) {
		BytecodeInstruction headOfChain = getHeadOfTransitiveChain(operand);
		if (headOfChain == null) {
			return false;
		}
		
		return headOfChain.isParameter();
	}
	
	private void forceEvosuiteToLoadClassAndMethod(String className, String methodName) {
		String cp = ClassPathHandler.getInstance().getTargetProjectClasspath();
		cp = cp.replace('\\', '/');
		try {
			Properties.TARGET_CLASS = className;
			Properties.TARGET_METHOD = methodName;
			DependencyAnalysis.analyzeClassForFilter(className, Arrays.asList(cp.split(File.pathSeparator)));
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
	
	private boolean isBranchEligible(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode methodNode, Set<DepVariable> dependentVariables) {
		if (dependentVariables == null) {
			// We can't do the dataflow analysis using InterproceduralGraphAnalysis if we can't 
			// get the set of dependent variables for this branch. In this case, fallback to 
			// direct instruction analysis
			return checkIfBranchIsEligibleWithoutInterproceduralGraphAnalysis(branch, cfg, methodNode);
		}
		
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
		return false;		
	}
	
	private boolean checkIfBranchIsEligibleWithoutInterproceduralGraphAnalysis(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode methodNode) {
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
		
		for (BytecodeInstruction operand : operands) {
			boolean isOperandParameter = operand.isParameter();
			if (isOperandParameter) {
				return true;
			}
			
			boolean isTransitiveCase = checkIfTransitiveChainOfParameter(operand);
			if (isTransitiveCase) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<BytecodeInstruction> getEligibleBranchesInMethod(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg); 
		List<BytecodeInstruction> eligibleBranches = new ArrayList<>();
		Map<Branch, Set<DepVariable>> branchToDependentVariables = InterproceduralGraphAnalysis.branchInterestedVarsMap.get(methodName);
		for (BytecodeInstruction branch : branches) {
			Branch branchAsBranch = branch.toBranch();
			Set<DepVariable> dependentVariables = (branchToDependentVariables == null ? null : branchToDependentVariables.get(branchAsBranch));
			boolean isCurrentBranchEligible = isBranchEligible(branch, cfg, node, dependentVariables);
			if (isCurrentBranchEligible) {
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
