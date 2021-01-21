package org.evosuite.graphs.interprocedural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchPool;
import org.evosuite.coverage.fbranch.FBranchDefUseAnalyzer;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.graphs.cfg.BasicBlock;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.interprocedural.interestednode.IInterestedNodeFilter;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class InterproceduralGraphAnalysis {
	/**
	 * a map maintains what variables are dependent by which branch, method->branch->dependent variables
	 * 
	 */
	public static Map<String, Map<Branch, Set<DepVariable>>> branchInterestedVarsMap = new HashMap<>();
	public static Map<Integer, List<String>> recommendedClasses = new HashMap<>();

	public static boolean isReachableInClass(BytecodeInstruction source, BytecodeInstruction target) {
		
		BasicBlock sourceBlock = source.getBasicBlock();
		BasicBlock targetBlock = target.getBasicBlock();
		
		int distance = 0;
		try {
			ActualControlFlowGraph cfg = source.getActualCFG();
			distance = cfg.getDistance(sourceBlock, targetBlock);
		}
		catch(Exception e) {
			return false;
		}
		
		if(distance >= 0) {
			if(distance == 0) {
				if(source.getInstructionId() < target.getInstructionId()) {
					return true;							
				}
			}
			else {
				return true;			
			}
		}
		return false;
	}
	
	public static void initializeDataflow(IInterestedNodeFilter interestedNodeFilter) {
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();

		for (String className : BranchPool.getInstance(classLoader).knownClasses()) {
			// when limitToCUT== true, if not the class under test of a inner/anonymous
			// class, continue
			if (!isCUT(className))
				continue;
			// when limitToCUT==false, consider all classes, but excludes libraries ones
			// according the INSTRUMENT_LIBRARIES property
			if (!Properties.INSTRUMENT_LIBRARIES && !DependencyAnalysis.isTargetProject(className))
				continue;

			// Branches
			for (String methodName : BranchPool.getInstance(classLoader).knownMethods(className)) {
				if(Properties.TARGET_METHOD.equals(methodName)) {
					ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
					FBranchDefUseAnalyzer.analyze(cfg.getRawGraph());
					
					Map<Branch, Set<DepVariable>> map = analyzeIndividualMethod(cfg, interestedNodeFilter);
					System.currentTimeMillis();
					recommendedClasses = analyzeRecommendationClasses(cfg);
					System.currentTimeMillis();
					branchInterestedVarsMap.put(methodName, map);					
				}
			}
		}

//		GraphVisualizer.visualizeComputationGraph();
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<Integer, List<String>> analyzeRecommendationClasses(ActualControlFlowGraph cfg){
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		
		Map<Integer, List<String>> recommendationClasses = new HashMap<Integer, List<String>>();
		for(BytecodeInstruction ins: BytecodeInstructionPool.getInstance(classLoader).
				getAllInstructionsAtMethod(className, methodName)) {
			/**
			 * handle instanceof instruction, it is essential for test initialization with polymorphism
			 */
			if(ins.checkInstanceOf()) {
				String checkingClassName = ins.getInstanceOfCheckingType();
				if(checkingClassName != null) {
					int operandNum = ins.getOperandNum();
					for (int i = 0; i < operandNum; i++) {
						Frame frame = ins.getFrame();
						int index = frame.getStackSize() - operandNum + i ;
						Value val = frame.getStack(index);
						if (!(val instanceof SourceValue)) {
							continue;
						}
						
						SourceValue srcValue = (SourceValue) val;
						MethodNode node = DefUseAnalyzer.getMethodNode(classLoader, className, methodName);
						/**
						 * get all the instruction defining the value.
						 */
						for(AbstractInsnNode insNode: srcValue.insns) {
							BytecodeInstruction defIns = DefUseAnalyzer.convert2BytecodeInstruction(cfg, node, insNode);
							if (defIns != null) {
								System.currentTimeMillis();
								DepVariable var = new DepVariable(defIns);
								if(var.isParameter()) {
									int position = var.getInstruction().getParameterPosition();
									List<String> classes = recommendationClasses.get(position);
									if(classes == null) {
										classes = new ArrayList<String>();
									}
									if(!classes.contains(checkingClassName)) {
										classes.add(checkingClassName);
										recommendationClasses.put(position, classes);										
									}
									
								}
							}
						}
					}
				}
				
			}
		}
		
		return recommendationClasses;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Map<Branch, Set<DepVariable>> analyzeIndividualMethod(ActualControlFlowGraph cfg, IInterestedNodeFilter interestedNodeFilter) {
		Map<Branch, Set<DepVariable>> map = new HashMap<>();
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		
		for (Branch b : BranchPool.getInstance(classLoader).retrieveBranchesInMethod(className, methodName)) {
			InterproceduralGraphAnalyzer graphAnalyzer = new InterproceduralGraphAnalyzer(b, interestedNodeFilter);
			Set<DepVariable> inputRootVars = new HashSet<DepVariable>();
			Set<BytecodeInstruction> visitedIns = new HashSet<BytecodeInstruction>();
			if (!b.isInstrumented()) {
				Frame frame = b.getInstruction().getFrame();
				for (int i = 0; i < b.getInstruction().getOperandNum(); i++) {
					int index = frame.getStackSize() - i - 1;
					Value val = frame.getStack(index);
					
					graphAnalyzer.searchDependantVariables(val, cfg, inputRootVars, visitedIns, 
							Properties.COMPUTATION_GRAPH_METHOD_CALL_DEPTH);
				}
			}
			
			map.put(b, inputRootVars);
		}
		
		return map;
	}

	private static boolean isCUT(String className) {
		if (!Properties.TARGET_CLASS.equals("") && !(className.equals(Properties.TARGET_CLASS)
				|| className.startsWith(Properties.TARGET_CLASS + "$"))) {
			return false;
		}
		return true;
	}

	public static Map<Branch, List<ConstructionPath>> checkObjectDifficultPath() {
		Map<Branch, Set<DepVariable>> interestedBranches = branchInterestedVarsMap.get(Properties.TARGET_METHOD);
		
		Map<Branch, List<ConstructionPath>> interestedPaths = new HashMap<>(); 
		for(Branch branch: interestedBranches.keySet()) {
			Set<DepVariable> interestedVariables = interestedBranches.get(branch);
			
			List<ConstructionPath> paths = new ArrayList<ConstructionPath>();
			for(DepVariable interestVar: interestedVariables) {
				for(DepVariable root: interestVar.getRootVars().keySet()) {
					ConstructionPath path = root.findPath(interestVar);
					if(path != null) {
						if(path.hasValidRoot() 
								&& !paths.contains(path)) {
							paths.add(path);							
						}
					}
				}
			}
			
			if(!paths.isEmpty()) {
				interestedPaths.put(branch, paths);				
			}
		}
		
		return interestedPaths;
	}
	
}
