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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;

import evosuite.shell.FileUtils;
import evosuite.shell.Settings;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class ConstantBranchOperandFilter extends MethodFlagCondFilter {
	// For demographic analysis
	private class BranchCount {
		public int stringTypeOnly;
		public int numberTypeOnly;
		public int stringAndNumberType;
		
		public BranchCount(int stringTypeOnly, int numberTypeOnly, int stringAndNumberType) {
			this.stringTypeOnly = stringTypeOnly;
			this.numberTypeOnly = numberTypeOnly;
			this.stringAndNumberType = stringAndNumberType;
		}
	}
	
	private static Logger log = LoggerUtils.getLogger(ConstantBranchOperandFilter.class);

	// Flags
	// Experimental settings to improve filter performance
	private static final boolean IS_BRANCH_COUNT_THRESHOLD_PROPORTIONAL = true;
	private static final float PROPORTIONAL_BRANCH_COUNT_THRESHOLD = 0.3f;
	private static final int ABSOLUTE_BRANCH_COUNT_THRESHOLD = 1;
	
	private static final boolean IS_ONLY_ALLOW_STRING_CONSTANTS = true;
	private static final boolean IS_REQUIRE_AT_LEAST_ONE_BRANCH_OPERAND_IS_PARAMETER = false;
	
	private static final boolean IS_PRINT_DEBUG_INFO = true;
	
	// Special flag where we don't filter anything, we just 
	// collect information on all the methods.
	private static final boolean IS_DEMOGRAPHIC_ANALYSIS_MODE = true;
	
	public ConstantBranchOperandFilter() {
	}
	
	public static void reportFlags() {
		String prefix = "[ConstantBranchOperandFilter] ";
		String indent = "  ";
		
		if (!IS_PRINT_DEBUG_INFO) {
			return;
		}
		
		if (IS_DEMOGRAPHIC_ANALYSIS_MODE) {
			System.out.println(prefix + "Demographic analysis mode active");
		}
		
		System.out.println(prefix + "Branch count type: " + (IS_BRANCH_COUNT_THRESHOLD_PROPORTIONAL ? "Proportional" : "Absolute"));
		if (IS_BRANCH_COUNT_THRESHOLD_PROPORTIONAL) {
			System.out.println(prefix + "Branch count threshold: " + String.format("%.2f", PROPORTIONAL_BRANCH_COUNT_THRESHOLD));
		} else {
			System.out.println(prefix + "Branch count threshold: " + ABSOLUTE_BRANCH_COUNT_THRESHOLD);
		}
		
		System.out.println(prefix + "Flags set:");
		
		if (IS_ONLY_ALLOW_STRING_CONSTANTS) {
			System.out.println(prefix + indent + "Allow only string constants");
		}
		
		if (IS_REQUIRE_AT_LEAST_ONE_BRANCH_OPERAND_IS_PARAMETER) {
			System.out.println(prefix + indent + "At least one branch operand must be a parameter");
		}		
	}
	
	@Override
	protected boolean checkMethod(ClassLoader classLoader, String className, String methodName, MethodNode methodNode,
			ClassNode classNode) throws AnalyzerException, IOException, ClassNotFoundException {
		log.debug("Working on [" + className + "#" + methodName + "]");

		DependencyAnalysis.clear();
		clearAllPools();

		/*
		 * This section adds context to the list of instructions
		 * - Add data and control flow information (in CFG) to this list of instructions 
		 */
		ActualControlFlowGraph cfg = getCfg(className, methodName, classLoader, methodNode);
		forceEvosuiteToLoadClassAndMethod(className, methodName);
		
		if (className.contains("DemographicAnalysisTagTest")) {
			System.currentTimeMillis();
		}
		
		if (IS_DEMOGRAPHIC_ANALYSIS_MODE) {
			recordMethodInformation(classLoader, className, methodName, cfg, methodNode, classNode);
			return true;
		}
		
		return isEligibleBranchesOverThreshold(className, methodName, cfg, methodNode);
	}
	
	private void recordMethodInformation(ClassLoader classLoader, String className, String methodName,
			ActualControlFlowGraph cfg, MethodNode methodNode, ClassNode classNode) {
		int numberOfCrBranches = computeNumberOfCrBranches(className, methodName, cfg, methodNode);
		float ratioOfCrBranches = computeRatioOfCrBranches(numberOfCrBranches, cfg);
		BranchCount branchCount = computeTagsOfMethod(cfg, methodNode);
		
		ExcelWriter writer = setupExcelWriter();
		List<Object> rowData = new ArrayList<>();
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(numberOfCrBranches);
		rowData.add(ratioOfCrBranches);
		rowData.add(branchCount.stringTypeOnly);
		rowData.add(branchCount.numberTypeOnly);
		rowData.add(branchCount.stringAndNumberType);
		
		try {
			writer.writeSheet("Data", Arrays.asList(rowData));
		} catch (IOException ioe) {
			log.error("Error", ioe);
		}
	}
	
	
	private ExcelWriter setupExcelWriter() {
		String[] headers = {
			"Class",
			"Method",
			"CR Branch Count",
			"CR Branch Ratio",
			"String type only",
			"Number type only",
			"String and number type"
		};
		ExcelWriter excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), "demographic_analysis.xlsx"));
		excelWriter.getSheet("Data", headers, 0);
		return excelWriter;
	}

	private int computeNumberOfCrBranches(String className, String methodName, ActualControlFlowGraph cfg, MethodNode node) {
		int numberOfCrBranches = 0;
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg);
		for (BytecodeInstruction branch : branches) {
			if (isCrBranch(branch, cfg, node)) {
				numberOfCrBranches++;
			}
		}
		return numberOfCrBranches;
	}
	
	private boolean isCrBranch(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode methodNode) {
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
		for (BytecodeInstruction operand : operands) {
			if (operand.isConstant()) {
				return true;
			}
		}
		return false;
	}
	
	private float computeRatioOfCrBranches(int numberOfCrBranches, ActualControlFlowGraph cfg) {
		float numberOfCrBranchesAsFloat = (float) numberOfCrBranches;
		float numberOfBranchesAsFloat = (float) getIfBranchesInMethod(cfg).size();
		return (numberOfCrBranchesAsFloat / numberOfBranchesAsFloat);
	}
	
	private BranchCount computeTagsOfMethod(ActualControlFlowGraph cfg, MethodNode methodNode) {
		int stringTypeOnlyCount = 0;
		int numberTypeOnlyCount = 0;
		int stringAndNumberTypeCount = 0;
		
		Set<BytecodeInstruction> branches = getIfBranchesInMethod(cfg);
		for (BytecodeInstruction branch : branches) {
			List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
			boolean isStringType = false;
			boolean isNumberType = false;
			
			for (BytecodeInstruction operand : operands) {
				if (!isStringType) {
					isStringType = isStringConstant(operand);
				}
				
				if (!isNumberType) {
					isNumberType = isNumberConstant(operand);
				}
				
				if (isStringType && isNumberType) {
					// Early exit if we've ascertained both types exist
					break;
				}
			}
			
			if (isStringType && !isNumberType) {
				stringTypeOnlyCount++;
			}
			
			if (!isStringType && isNumberType) {
				numberTypeOnlyCount++;
			}
			
			if (isStringType && isNumberType) {
				stringAndNumberTypeCount++;
			}
		}
		
		return new BranchCount(stringTypeOnlyCount, numberTypeOnlyCount, stringAndNumberTypeCount);
		
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
		if (IS_ONLY_ALLOW_STRING_CONSTANTS) {
			// Filter out all non-string constants before further processing.
			operands.removeIf(operand -> !isStringConstant(operand));
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

	private static List<BytecodeInstruction> getOperandsFromMethodCall(BytecodeInstruction methodCall) {
		// Yes, this method will pick up a few unnecessary instructions. They shouldn't matter for our purposes.
		List<BytecodeInstruction> operands = new ArrayList<>();
		BytecodeInstruction currentInstruction = methodCall.getPreviousInstruction();
		while (currentInstruction != null) {
			// This won't catch "deeper" operands e.g. 2-level nested operands
			// This is because we want to keep it lightweight enough to minimise filter computation time
			// The alternative would be e.g. recording the function traversed to prevent infinite recursion
			// e.g. if a function calls itself
			operands.add(currentInstruction);
			currentInstruction = currentInstruction.getPreviousInstruction();
		}
		return operands;
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
		
		// After the first round of obtaining operands, we need to add all the operands
		// from method calls
		// This deals with cases where e.g. we have
		// if (a.equals("hello world"))
		// The operand for the branch is simply an INVOKEVIRTUAL instruction
		// We want to go beyond that to extract the actual operands (a, "hello world").
		List<BytecodeInstruction> additionalOperands = new ArrayList<>();
		for (BytecodeInstruction operand : operands) {
			if (operand.isMethodCall()) {
				additionalOperands.addAll(getOperandsFromMethodCall(operand));
			}
		}
		operands.addAll(additionalOperands);
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
	
	private static boolean isStringConstant(BytecodeInstruction operand) {
		if (!operand.isLoadConstant()) {
			return false;
		}
		
		Object constant = ((LdcInsnNode) operand.getASMNode()).cst;
		return (constant instanceof String);
	}
	
	private static boolean isNumberConstant(BytecodeInstruction operand) {
		if (operand.getASMNode() == null) {
			return false;
		}
		
		int opcode = operand.getASMNode().getOpcode();
		// Refer to org.objectweb.asm.Opcodes
		// Opcodes 3-17 inclusive are all numeric constant-loading instructions 
		// TODO: Check if BIPUSH/SIPUSH are always constant-loading
		if (3 <= opcode && opcode <= 17) {
			return true;
		}		
		
		if (operand.isLoadConstant()) {
			Object constant = ((LdcInsnNode) operand.getASMNode()).cst;
			return ((constant instanceof Integer) ||
					(constant instanceof Long) ||
					(constant instanceof Float) ||
					(constant instanceof Double));
		}
		
		return false;
	}
	
	private static boolean isBooleanConstant(BytecodeInstruction operand) {
		// TODO
		return false;
	}
}
