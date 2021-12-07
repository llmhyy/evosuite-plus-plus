package evosuite.shell.listmethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.classpath.ClassPathHandler;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BytecodeAnalyzer;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.DefUseAnalyzer;
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

import evosuite.shell.utils.LoggerUtils;

public class ConstantBranchOperandFilter extends MethodFlagCondFilter {
	private static Logger log = LoggerUtils.getLogger(ConstantBranchOperandFilter.class);

	// Flags
	// Experimental settings to improve filter performance
	private static final boolean IS_BRANCH_COUNT_THRESHOLD_PROPORTIONAL = false;
	private static final float PROPORTIONAL_BRANCH_COUNT_THRESHOLD = 0.3f;
	private static final int ABSOLUTE_BRANCH_COUNT_THRESHOLD = 3;
	
	private static final boolean IS_ONLY_ALLOW_STRING_CONSTANTS = false;
	private static final boolean IS_REQUIRE_AT_LEAST_ONE_BRANCH_OPERAND_IS_PARAMETER = false;
	private static final boolean IS_REQUIRE_BRANCH_OPERANDS_PRIMITIVE = false;
	
	private static final boolean IS_PRINT_DEBUG_INFO = true;
		
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
	
	public ConstantBranchOperandFilter() {
		System.out.println("[ConstantBranchOperandFilter]: Current branch count threshold: " + ABSOLUTE_BRANCH_COUNT_THRESHOLD);
	}
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode methodNode,
			ClassNode classNode) throws AnalyzerException, IOException, ClassNotFoundException {
//		log.debug("Working on [" + className + "#" + methodName + "]");
		DependencyAnalysis.clear();
		clearAllPools();

		/*
		 * This section adds context to the list of instructions
		 * - Add data and control flow information (in CFG) to this list of instructions 
		 */
		ActualControlFlowGraph cfg = getCfg(className, methodName, classLoader, methodNode);
		forceEvosuiteToLoadClassAndMethod(className, methodName);
		
		return isEligibleBranchesOverThreshold(className, methodName, cfg, methodNode);
	}
	
	private static void debug(String message) {
		if (IS_PRINT_DEBUG_INFO) {
			log.debug(message);
		}
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
	
	private static boolean isBranchEligible(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode methodNode) {
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
		
		if (IS_REQUIRE_BRANCH_OPERANDS_PRIMITIVE) {
			// Remove all non-primitive types before further processing.
			operands.removeIf(operand -> !isPrimitive(operand.getType()));
		}
		
		if (IS_ONLY_ALLOW_STRING_CONSTANTS) {
			// Filter out all string constants before further processing.
			operands.removeIf(operand -> !isString(operand));
		}
		
		boolean isAtLeastOneOperandConstant = false;
		boolean isAtLeastOneOperandParameter = false;
		for (BytecodeInstruction operand : operands) {
			boolean isOperandConstant = operand.isConstant();
			
			if (isOperandConstant) {
				isAtLeastOneOperandConstant = true;
			}
			
			if (IS_REQUIRE_AT_LEAST_ONE_BRANCH_OPERAND_IS_PARAMETER) {
				boolean isOperandParameter = operand.isParameter();
				if (isOperandParameter) {
					isAtLeastOneOperandParameter = true;
				}
			}
		}
		
		boolean isAllConditionsMet = isAtLeastOneOperandConstant;
		if (IS_REQUIRE_AT_LEAST_ONE_BRANCH_OPERAND_IS_PARAMETER) {
			isAllConditionsMet = isAllConditionsMet && isAtLeastOneOperandParameter;
		}
		
		return isAllConditionsMet;
	}
	
	private List<BytecodeInstruction> getEligibleBranchesInMethod(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg); 
		List<BytecodeInstruction> eligibleBranches = new ArrayList<>();
		for (BytecodeInstruction branch : branches) {
			boolean isCurrentBranchEligible = isBranchEligible(branch, cfg, node);
			if (isCurrentBranchEligible) {
				eligibleBranches.add(branch);
			}
		}
		
		return eligibleBranches;
	}
	
	/**
	 * Handles deciding whether we are over the threshold, depending on the settings
	 * (Absolute/proportional threshold, threshold values, etc.)
	 * 
	 * @return
	 */
	private boolean isEligibleBranchesOverThreshold(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {	
		if (IS_BRANCH_COUNT_THRESHOLD_PROPORTIONAL) {
			return isEligibleBranchesOverProportionalThreshold(className, methodName, cfg, node);
		}
		
		return isEligibleBranchesOverAbsoluteThreshold(className, methodName, cfg, node);
	}
	
	private boolean isEligibleBranchesOverProportionalThreshold(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg);
		List<BytecodeInstruction> eligibleBranches = getEligibleBranchesInMethod(className, methodName, cfg, node);
		int eligibleBranchCount = eligibleBranches.size();
		int totalBranchCount = branches.size();
		
		float eligibleBranchCountAsFloat = (float) eligibleBranchCount;
		float totalBranchCountAsFloat = (float) totalBranchCount;
		float eligibleBranchProportion = eligibleBranchCountAsFloat / totalBranchCountAsFloat;
		boolean isOverThreshold = (eligibleBranchProportion >= PROPORTIONAL_BRANCH_COUNT_THRESHOLD);
		
		if (IS_PRINT_DEBUG_INFO) {
			String message = "[" + className + "#" + methodName + "]: " + eligibleBranchCount + " / " + totalBranchCount + " = " + String.format("%.2f", eligibleBranchProportion) + " (" + isOverThreshold + ")";
			debug(message);
		}
		
		return isOverThreshold;
	}
	
	private boolean isEligibleBranchesOverAbsoluteThreshold(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		List<BytecodeInstruction> eligibleBranches = getEligibleBranchesInMethod(className, methodName, cfg, node);
		int eligibleBranchCount = eligibleBranches.size();
		boolean isOverThreshold = (eligibleBranchCount >= ABSOLUTE_BRANCH_COUNT_THRESHOLD);
		
		if (IS_PRINT_DEBUG_INFO) {
			String message = "[" + className + "#" + methodName + "]: " + eligibleBranchCount + " (" + isOverThreshold + ")";
			debug(message);
		}
		return isOverThreshold;
	}

	private static List<BytecodeInstruction> getOperandsFromBranch(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode node) {
		List<BytecodeInstruction> operands = new ArrayList<>();
		@SuppressWarnings("rawtypes")
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
	
	private static boolean isString(BytecodeInstruction operand) {
		String operandType = operand.getType();
		return (operandType.contains("java.lang.String"));
	}
}
