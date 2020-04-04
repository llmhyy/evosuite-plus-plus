package org.evosuite.graphs.dataflow;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
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

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;

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

//		visualizeComputationGraph();
	}
	
	public static void visualizeComputationGraph(Branch b) {
		for(String methodName: branchDepVarsMap.keySet()) {
			Map<Branch, Set<DepVariable>> map = branchDepVarsMap.get(methodName);
			
			Set<DepVariable> variables = map.get(b);
			
			if(variables == null) continue;
			
			List<LinkSource> links = new ArrayList<LinkSource>();
			HashSet<DepVariable> roots = new HashSet<DepVariable>();
			for(DepVariable source: variables) {
				for(DepVariable root: source.getRootVars().keySet()) {
					
					if(!roots.contains(root)) {
						roots.add(root);
						collectLinks(root, links);
					}
					
				}
				
			}
			
			Graph g = graph("example1").directed()
			        .graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT))
			        .with(links);
			try {
				File f = new File("D://linyun/ex1.png");
				Graphviz.fromGraph(g).height(1000).render(Format.PNG).toFile(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void collectLinks(DepVariable source, List<LinkSource> links) {
		
		List<DepVariable>[] relations = source.getRelations();
		for(int i=0; i<relations.length; i++) {
			List<DepVariable> child = relations[i];
			
			if(child == null) continue;
			
			for(DepVariable target: child) {
				
				guru.nidi.graphviz.model.Node n  = node(source.getUniqueLabel()).link(node(target.getUniqueLabel()));
				
				if(!links.contains(n)) {
					links.add(n);
					collectLinks(target, links);					
				}
			}
		}
		
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
				Frame frame = b.getInstruction().getFrame();
				for (int i = 0; i < b.getInstruction().getOperandNum(); i++) {
					int index = frame.getStackSize() - i - 1;
					Value val = frame.getStack(index);
					
					fAnalyzer.searchDependantVariables(val, cfg, allDepVars, visitedIns, Properties.COMPUTATION_GRAPH_METHOD_CALL_DEPTH);
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
