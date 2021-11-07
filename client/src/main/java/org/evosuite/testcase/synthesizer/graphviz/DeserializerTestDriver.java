package org.evosuite.testcase.synthesizer.graphviz;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.evosuite.graphs.interprocedural.GraphVisualizer;
import org.junit.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeserializerTestDriver {
	@Test
	public void deserializationTest() throws StreamReadException, DatabindException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File("D:\\linyun\\simplePartialGraph.json");
		SimplePartialGraph graph = objectMapper.readValue(file, SimplePartialGraph.class);
		
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
