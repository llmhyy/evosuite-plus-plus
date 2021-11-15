package org.evosuite.testcase.synthesizer.graphviz;

import org.evosuite.TestGenerationContext;
import org.evosuite.graphs.GraphPool;
import org.evosuite.graphs.cfg.ActualControlFlowGraph;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.testcase.synthesizer.PartialGraph;
import org.evosuite.testcase.synthesizer.var.DepVariableWrapper;
import org.evosuite.testcase.synthesizer.var.VarRelevance;

public class GraphVisualisationDataBuilder {
	private GraphVisualisationData data = new GraphVisualisationData();
	private boolean isPartialGraphAdded = false;
	private boolean isCfgAdded = false;
	
	public GraphVisualisationDataBuilder() {
	}
	
	public void addPartialGraph(PartialGraph partialGraph) {
		data.setPartialGraph(new SimplePartialGraph(partialGraph));
		isPartialGraphAdded = true;
	}
	
	public void recordGraphTraversalOrder(DepVariableWrapper node, VarRelevance varRelevance) throws Exception {
		if (!isPartialGraphAdded) {
			throw new Exception("Can't add graph traversal order if partial graph isn't added!");
		}
		
		data.getPartialGraph().recordGraphTraversalOrder(node, varRelevance);
	}
	
	public void addCfg(ActualControlFlowGraph actualCfg) {
		data.setCfg(new SimpleControlFlowGraph(actualCfg.getRawGraph()));
		isCfgAdded = true;
	}
	
	public void addCfgFor(String className, String methodName) {
		InstrumentingClassLoader classLoader = TestGenerationContext.getInstance().getClassLoaderForSUT();
		ActualControlFlowGraph cfg = GraphPool.getInstance(classLoader).getActualCFG(className, methodName);
		this.addCfg(cfg);
	}
	
	public GraphVisualisationData build() throws Exception {
		if (!isPartialGraphAdded) {
			throw new Exception("Partial graph not set.");
		}
		
		if (!isCfgAdded) {
			throw new Exception("CFG not set.");
		}
		return data;
	}
}
