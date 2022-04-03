package testcode.graphgeneration;

import org.junit.Test;

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
}
