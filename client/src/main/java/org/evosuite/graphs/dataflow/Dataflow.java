package org.evosuite.graphs.dataflow;

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
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.setup.DependencyAnalysis;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

public class Dataflow {
	/**
	 * a map maintains what variables are dependent by which branch, method->branch->dependent variables
	 * 
	 */
	public static Map<String, Map<Branch, Set<DepVariable>>> branchDepVarsMap = new HashMap<>();

	public static void initializeDataflow() {
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
					
					Map<Branch, Set<DepVariable>> map = analyzeIndividualMethod(cfg);
					branchDepVarsMap.put(methodName, map);					
				}
			}
		}

		System.currentTimeMillis();
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<Branch, Set<DepVariable>> analyzeIndividualMethod(ActualControlFlowGraph cfg) {
		Map<Branch, Set<DepVariable>> map = new HashMap<>();
		
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		String className = cfg.getClassName();
		String methodName = cfg.getMethodName();
		
		for (Branch b : BranchPool.getInstance(classLoader).retrieveBranchesInMethod(className, methodName)) {
			FieldUseAnalyzer fAnalyzer = new FieldUseAnalyzer();
			Set<DepVariable> allDepVars = new HashSet<DepVariable>();
			Set<BytecodeInstruction> visitedIns = new HashSet<BytecodeInstruction>();
			if (!b.isInstrumented()) {
				for (int i = 0; i < b.getInstruction().getOperandNum(); i ++) {
					int operandNum = b.getInstruction().getOperandNum();
					for (int j = 0; j < operandNum; j++) {
						Frame frame = b.getInstruction().getFrame();
						int index = frame.getStackSize() - j - 1;
						Value val = frame.getStack(index);
						fAnalyzer.searchDependantVariables(val, cfg, allDepVars, visitedIns);
					}
					
				}
			}
			
			map.put(b, allDepVars);
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
		Map<Branch, Set<DepVariable>> interestedBranches = branchDepVarsMap.get(Properties.TARGET_METHOD);
		
		Map<Branch, List<ConstructionPath>> interestedPaths = new HashMap<>(); 
		for(Branch branch: interestedBranches.keySet()) {
			Set<DepVariable> interestedVariables = interestedBranches.get(branch);
			
			List<ConstructionPath> paths = new ArrayList<ConstructionPath>();
			for(DepVariable interestVar: interestedVariables) {
				for(DepVariable root: interestVar.getRootVars()) {
					ConstructionPath path = root.findPath(interestVar);
					if(path != null && path.isDifficult()) {
						if(!paths.contains(path)) {
							paths.add(path);							
						}
					}
				}
			}
			
			System.currentTimeMillis();
			
			if(!paths.isEmpty()) {
				interestedPaths.put(branch, paths);				
			}
		}
		
		return interestedPaths;
	}

}
