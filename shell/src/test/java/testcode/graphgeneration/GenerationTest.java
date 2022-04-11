package testcode.graphgeneration;

import static org.junit.Assert.fail;

import org.junit.Test;

import testcode.graphgeneration.model.ClassModel;

public class GenerationTest {

	@Test
	public void testStep1() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.visualize(1000, "graph", "graph");
	}
	
	
	@Test
	public void testStep2() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.visualize(1000, "graph", "graph");
	}

	@Test
	public void testStep3() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.visualize(1000, "graph", "graph");
	}
	
	@Test
	public void testStep4() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.generateCode();
		graph.visualize(1000, "graph", "graph");
	}
	
	@Test
	public void testClassModelGeneration() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		ClassModel classModel = new ClassModel(graph);
		System.currentTimeMillis();
	}
	
	@Test
	public void testCodeGeneration() {
		try {
			OCGGenerator generator = new OCGGenerator();
			Graph graph = generator.generateGraph(5, 6, false);
			graph.labelNodeType();
			graph.labelAccessibility();
			graph.visualize(1000, "graph", "graph");
			ClassModel classModel = new ClassModel(graph);
			classModel.transformToCode();
			System.currentTimeMillis();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
}
