package testcode.graphgeneration;

import org.junit.Test;

public class GenerationTest {

	@Test
	public void test() {
		OCGGenerator generator = new OCGGenerator();
		Graph graph = generator.generateGraph(5, 6, true);
		graph.visualize(1000, "graph", "graph");
	}

}
