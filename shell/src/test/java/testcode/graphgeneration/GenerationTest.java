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
	/**
	 * Issues noted: 
	 *   1) Some graphs (e.g. -2220534647818254570L) have multiple root nodes, 
	 *      might need to generate a parent class to hold all root nodes. Also need to refine
	 *      choice of target class for MUT generation.
	 *   2) Recursive case (class has itself as a field) may trigger infinite recursion
	 *      e.g. 4680529636307580942L, may need to provide a different kind of initialisation for these cases?
	 *   3) Extension of recursive case (e.g. ClassX has a field of ClassY, ClassY has a field of ClassX) may
	 *      trigger infinite recursion too (e.g. -2493682970431517121L), may need to provide a different kind of initialisation for these cases?
	 */
}
