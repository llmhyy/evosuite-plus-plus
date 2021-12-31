package org.evosuite.testcase.synthesizer.graphviz;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.junit.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

public class DeserializerTestDriver {
	public static final boolean IS_VISUALISE_PARTIAL_GRAPH = true;
	public static final boolean IS_VISUALISE_CFG = false;
	public static final boolean IS_TRAVERSE_GRAPH = false;
	
	@Test
	public void deserializationTest() throws StreamReadException, DatabindException, IOException {
		File file = new File("D:\\linyun\\graphVisData_I194 Branch 1 IF_ICMPNE L19_0.json");
		GraphVisualisationData graphVisData = GraphVisualisationData.from(file);
		SimplePartialGraph graph = graphVisData.getPartialGraph();
		SimpleControlFlowGraph cfg = graphVisData.cfg;
		
		if (IS_VISUALISE_PARTIAL_GRAPH) {
			GraphVisualizer.visualizeComputationGraph(graph, 5000, "test");
		}
		
		if (IS_VISUALISE_CFG) {
			GraphVisualizer.visualizeCfg(cfg, 5000, "test", "CFG");
		}
		
		if (IS_TRAVERSE_GRAPH) {
			List<String> graphTraversalOrder = graph.graphTraversalOrder;
			for (String traversedNode : graphTraversalOrder) {
				SimpleStatement simpleStatement = graph.nodeToStatement.get(traversedNode);
				String correspondingStatement = simpleStatement.statementNumberToTestCase.get(simpleStatement.statementNumber);
				String testCase = simpleStatement.wholeTestCase;
			}
		}
	}
}
