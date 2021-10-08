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
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
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
		
		String debugOutput = "[" + className + "#" + methodName + "]: {" + numberOfConstantsInClass + ", " + numberOfEligibleBranches + ", " + (isNumberOfConstantsOverThreshold && isNumberOfEligibleBranchesOverThreshold) + "}";
		log.debug(debugOutput);
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
	
	/**
	 * Checks if the operand passed in is a dependent variable.
	 * @param operand The operand to check.
	 * @param dependentVariables The dependent variables.
	 * @return {@code true} if the operand corresponds to any of the variables in the set, {@code false} otherwise.
	 */
	private static boolean isOperandDependentVariable(BytecodeInstruction operand, Set<DepVariable> dependentVariables) {
		AbstractInsnNode operandNode = operand.getASMNode();
		if (operandNode == null) {
			return false;
		}
		boolean isOperandNodeField = operandNode instanceof FieldInsnNode;
		boolean isOperandNodeParameter = operandNode instanceof VarInsnNode;
		boolean isOperandIload = operand.getInstructionType().equals("ILOAD");
		
		for (DepVariable dependentVariable : dependentVariables) {		
			BytecodeInstruction dependentVariableInstruction = dependentVariable.getInstruction();
			if (dependentVariableInstruction == null) {
				continue;
			}
			AbstractInsnNode dependentVariableNode = dependentVariableInstruction.getASMNode();
			if (dependentVariableNode == null) {
				continue;
			}
			
			if (isOperandNodeField) {
				FieldInsnNode operandFieldNode = (FieldInsnNode) operandNode;
				boolean isDependentVariableNodeField = (dependentVariableNode instanceof FieldInsnNode);
				if (!isDependentVariableNodeField) {
					continue;
				}
				
				FieldInsnNode dependentVariableFieldNode = (FieldInsnNode) dependentVariableNode;
				boolean isVariableNamesMatch = (operandFieldNode.name.equals(dependentVariableFieldNode.name));
				boolean isOwnersMatch = (operandFieldNode.owner.equals(dependentVariableFieldNode.owner));
				// We consider the fields to match if their variable names and owners match.
				if (isVariableNamesMatch && isOwnersMatch) {
					return true;
				}
			}
			
			// This case handles precomputing the branch operand e.g.
			// branchOperand = parameter.someMethod();
			// if (branchOperand == ...) 
			// We essentially treat the branch operand as a function, trace upwards and match against
			if (isOperandIload) {
				List<BytecodeInstruction> iloadOperands = getOperandsOfMethod(operand);
				for (BytecodeInstruction iloadOperand : iloadOperands) {
					if (isOperandDependentVariable(iloadOperand, dependentVariables)) {
						return true;
					}
				}
			}
			
			if (isOperandNodeParameter) {
				VarInsnNode operandVarNode = (VarInsnNode) operandNode;
				boolean isDependentVariableNodeParameter = (dependentVariableNode instanceof VarInsnNode);
				if (!isDependentVariableNodeParameter) {
					continue;
				}
				
				VarInsnNode dependentVariableVarNode = (VarInsnNode) dependentVariableNode;
				// We check if the following values match between the nodes
				// 1) the local variable position
				// 2) opcode
				// The idea is that both should be the same opcode (*LOAD) and the same local variable position
				boolean isLocalVarIndexEqual = (operandVarNode.var == dependentVariableVarNode.var);
				boolean isOpcodesEqual = (operandVarNode.getOpcode() == dependentVariableVarNode.getOpcode());
				boolean isWeaklyEqual = (isLocalVarIndexEqual && isOpcodesEqual);
				if (isWeaklyEqual) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Traces through the method call to get all parameters. This method is not perfect,
	 * in that it might pick up some operands that are strictly speaking, not parameters of 
	 * the method. However, the additional operands should be irrelevant.
	 * 
	 * @param methodCall The method call to analyse.
	 * @return A list of parameters.
	 */
	private static List<BytecodeInstruction> getOperandsOfMethod(BytecodeInstruction methodCall) {
		List<BytecodeInstruction> operands = new ArrayList<>();
		
		BytecodeInstruction currentInstruction = methodCall.getPreviousInstruction();
		while (currentInstruction != null) {
			operands.add(currentInstruction);
			currentInstruction = currentInstruction.getPreviousInstruction();
		}
		
		return operands;
	}
	
	/**
	 * Checks if an operand is eligible. We define an operand to be eligible if 
	 * it is a transitive method call/field use from a method input i.e. of the form
	 * methodInput.foo.bar().baz for arbitrary chains of method calls and field accesses.
	 * 
	 * @param operand The operand to check.
	 * @return {@code true} if the operand is eligible, {@code false} otherwise.
	 */
	private static boolean isOperandEligible(BytecodeInstruction operand, Set<DepVariable> dependentVariables) {
		// Ignore constants
		boolean isConstant = operand.isConstant();
		if (isConstant) {
			return false;
		}
		
		// Check if the operand is one of our dependent variables
		boolean isDependentVariable = isOperandDependentVariable(operand, dependentVariables);
		if (isDependentVariable) {
			return true;
		}
		
		// Possible cases for when the operand is a method call
		// 1) input.foo.baz.bar()
		// 2) someMethod(parameter.foo)
		// We need to distinguish between the two and treat them differently
		boolean isMethodCall = operand.isMethodCall();
		if (isMethodCall) {
			// We check the first case by tracing the ancestor object
			// If it is non-null and parameter or field, then we know
			// it's the first case. Else we check for the second case.
			BytecodeInstruction ancestorObject = getHeadOfTransitiveChain(operand);
			boolean isTransitiveMethodCallOnMethodInput = ((ancestorObject != null) && (ancestorObject.isParameter() || ancestorObject.isFieldUse()));
			if (isTransitiveMethodCallOnMethodInput) {
				return true;
			}
			
			// We check the second case by sifting through all the operands of the method call
			// For each, we run the same procedure.
			List<BytecodeInstruction> methodOperands = getOperandsOfMethod(operand);
			for (BytecodeInstruction methodOperand : methodOperands) {
				boolean isMethodOperandEligible = isOperandEligible(methodOperand, dependentVariables);
				if (isMethodOperandEligible) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isBranchEligible(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode methodNode, Set<DepVariable> dependentVariables) {
		if (dependentVariables == null) {
			// We can't do the dataflow analysis using InterproceduralGraphAnalysis if we can't 
			// get the set of dependent variables for this branch. In this case, fallback to 
			// direct instruction analysis
			return checkIfBranchIsEligibleWithoutInterproceduralGraphAnalysis(branch, cfg, methodNode);
		}
		
		List<BytecodeInstruction> operands = getOperandsFromBranch(branch, cfg, methodNode);
		
		for (BytecodeInstruction operand : operands) {
			boolean isOperandEligible = isOperandEligible(operand, dependentVariables);
			if (isOperandEligible) {
				return true;
			}
		}
		
		return false;		
	}
	
	private static boolean checkIfBranchIsEligibleWithoutInterproceduralGraphAnalysis(BytecodeInstruction branch, ActualControlFlowGraph cfg, MethodNode methodNode) {
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
			Set<DepVariable> dependentVariables = getDependentVariablesByBranch(branch, branchToDependentVariables);
			boolean isCurrentBranchEligible = isBranchEligible(branch, cfg, node, dependentVariables);
			if (isCurrentBranchEligible) {
				eligibleBranches.add(branch);
			}
		}
		
		return eligibleBranches;
	}
	
	/*
	 * The way that the InterproceduralGraphAnalysis and the filter was set up means that the two use
	 * different ClassLoaders, so we have to account for when the two branches (either in Branch or BytecodeInstruction format)
	 * refer to the same branch, but aren't formally equal (they might not even have the same hashcode). This
	 * method aims to check if such a situation occurs, and retrieve the correct set of dependent variables in that case.
	 * As such, it can be quite slow in the event that it occurs (O(n) traversal of the key set).
	 */
	private static Set<DepVariable> getDependentVariablesByBranch(BytecodeInstruction branch, Map<Branch, Set<DepVariable>> branchToDependentVariables) {
		// Nothing we can do if this happens.
		if (branchToDependentVariables == null) {
			return null;
		}
		Branch branchAsBranch = branch.toBranch();
		Set<DepVariable> attemptAtGettingDependentVariables = branchToDependentVariables.get(branchAsBranch);
		if (attemptAtGettingDependentVariables != null) {
			// Jackpot
			return attemptAtGettingDependentVariables;
		}
		
		// Need to identify the two branches
		for (Map.Entry<Branch, Set<DepVariable>> entry : branchToDependentVariables.entrySet()) {
			Branch currentBranch = entry.getKey();
			boolean isBranchesWeaklyEqual = weakEquals(currentBranch, branchAsBranch);
			if (isBranchesWeaklyEqual) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Weaker form of identification of branches. Checks equality of
	 * 1) Branch id
	 * 2) Class name
	 * 3) Method name
	 * 4) Line number
	 * 
	 * @param branch The first branch to compare.
	 * @param otherBranch The second branch to compare.
	 * @returns {@code true} if the two branches are weakly equal, {@code false} otherwise.
	 */
	private static boolean weakEquals(Branch branch, Branch otherBranch) {
		int branchId = branch.getActualBranchId();
		String className = branch.getClassName();
		String methodName = branch.getMethodName();
		BytecodeInstruction instruction = branch.getInstruction();
		int lineNumber = (instruction == null ? -1 : instruction.getLineNumber());
		if (className == null) {
			className = "className";
		}
		if (methodName == null) {
			methodName = "methodName";
		}
		
		int otherBranchId = branch.getActualBranchId();
		String otherClassName = branch.getClassName();
		String otherMethodName = branch.getMethodName();
		BytecodeInstruction otherInstruction = branch.getInstruction();
		int otherLineNumber = (otherInstruction == null ? -2 : otherInstruction.getLineNumber());
		if (otherClassName == null) {
			otherClassName = "otherClassName";
		}
		if (otherMethodName == null) {
			otherMethodName = "otherMethodName";
		}
		
		boolean isBranchIdsMatch = (branchId == otherBranchId);
		if (!isBranchIdsMatch) {
			return false;
		}
		
		boolean isClassNamesMatch = (className.equals(otherClassName));
		if (!isClassNamesMatch) {
			return false;
		}
		
		boolean isMethodNamesMatch = (methodName.equals(otherMethodName));
		if (!isMethodNamesMatch) {
			return false;
		}
		
		boolean isLineNumbersMatch = (lineNumber == otherLineNumber);
		if (!isLineNumbersMatch) {
			return false;
		}
		
		return true;
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
