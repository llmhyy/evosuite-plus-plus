package testcode.graphgeneration;

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
		graph.transformToCode();
		graph.visualize(1000, "graph", "graph");
	}
	
	@Test
	public void testClassModelGeneration() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		ClassModel classModel = new ClassModel(graph);
	}
	
	@Test
	public void testCodeGeneration() {
		long seed = -2220534647818254570L;
		OCGGenerator generator = new OCGGenerator(seed); // Specific seed
//		OCGGenerator generator = new OCGGenerator(); // Random seed
		Graph graph = generator.generateGraph(5, 6, false);
		graph.labelNodeType();
		graph.labelAccessibility();
		graph.visualize(1000, "graph", "graph");
		graph.transformToCode();
	}
}
