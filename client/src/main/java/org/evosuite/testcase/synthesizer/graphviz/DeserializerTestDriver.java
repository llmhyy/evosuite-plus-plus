package org.evosuite.testcase.synthesizer.graphviz;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.junit.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

public class DeserializerTestDriver {
	@Test
	public void deserializationTest() throws StreamReadException, DatabindException, IOException {
		File file = new File("D:\\linyun\\simplePartialGraph.json");
		SimplePartialGraph graph = SimplePartialGraph.from(file);
		
		GraphVisualizer.visualizeComputationGraph(graph, 5000, "test");
		
		List<String> graphTraversalOrder = graph.graphTraversalOrder;
		for (String traversedNode : graphTraversalOrder) {
			SimpleStatement simpleStatement = graph.nodeToStatement.get(traversedNode);
			String correspondingStatement = simpleStatement.statementNumberToTestCase.get(simpleStatement.statementNumber);
			String testCase = simpleStatement.wholeTestCase;
			System.currentTimeMillis();
		}
	}
}
