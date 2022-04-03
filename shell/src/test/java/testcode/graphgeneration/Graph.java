package testcode.graphgeneration;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.LinkSource;

public class Graph {
	public static String path = "D://linyun/";
	private List<GraphNode> nodeSet = new ArrayList<>();
	private Map<GraphNode, List<GraphNode>> accessbilityMap = new HashedMap();

	public void addNode(GraphNode node) {
		if(!nodeSet.contains(node)) {
			nodeSet.add(node);
		}
		
	}

	public void visualize(int resolution, String folderName, String fileName) {

		List<LinkSource> links = new ArrayList<LinkSource>();

		for (GraphNode source: nodeSet) {
			for (GraphNode target : source.getChildren()) {
				guru.nidi.graphviz.model.Node n = node(source.getName())
						.link(node(target.getName()));
				if (!links.contains(n)) {
					links.add(n);
				}

			}

			if (source.getChildren().isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(source.getName());
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}

		guru.nidi.graphviz.model.Graph g = graph(fileName).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			File f = new File(path + folderName + File.separator + fileName + ".png");
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void visualizeStep2(int resolution, String folderName, String fileName) {

		List<LinkSource> links = new ArrayList<LinkSource>();

		for (GraphNode source: nodeSet) {
			for (GraphNode target : source.getChildren()) {
				guru.nidi.graphviz.model.Node n = node(source.getName())
						.link(node(target.getName()));
				if (!links.contains(n)) {
					links.add(n);
				}

			}

			if (source.getChildren().isEmpty()) {
				guru.nidi.graphviz.model.Node n = node(source.getName());
				if (!links.contains(n)) {
					links.add(n);
				}
			}
		}

		guru.nidi.graphviz.model.Graph g = graph(fileName).directed().graphAttr()
				.with(Rank.dir(RankDir.LEFT_TO_RIGHT)).with(links);
		try {
			File f = new File(path + folderName + File.separator + fileName + ".png");
			Graphviz.fromGraph(g).height(resolution).render(Format.PNG).toFile(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void labelNodeType() {
		List<String> typePool = new ArrayList<String>();

		String type = null;
		for(GraphNode node: this.nodeSet) {
			/**
			 * non-leaf node, reference type access
			 */
			if(!node.getChildren().isEmpty()) {
				if(typePool.isEmpty()) {
					type = createRandomType();
				}
				else {
					if(OCGGenerator.RANDOM.nextDouble() > 0.5) {
						type = createRandomType();
					}
					else {
						int index = OCGGenerator.RANDOM.nextInt(typePool.size());
						type = typePool.get(index);
					}
				}
				
				if(!typePool.contains(type)) {
					typePool.add(type);
				}
			}
			/**
			 * leaf node, primitive type access
			 */
			else {
				type = randomPrimitiveType();
			}
			
			/**
			 * either parameter, field access, array element access, or method call
			 */
			Relation relation = Relation.randomRelation();
			if(node.getParents().isEmpty()) {
				relation = Relation.PARAM;
			}
			
			NodeType accessType = generateNodeType(relation, type, node);
			
			node.setNodeType(accessType);
			
		}
		
	}

	private String createRandomType() {
		return "Class" + OCGGenerator.RANDOM.nextInt(100);
	}

	private String randomPrimitiveType() {
		// TODO Darien, please create more enriched primitive type
		return "int";
	}

	private NodeType generateNodeType(Relation relation, String type, GraphNode node) {
		String name = relation.name() + node.getName();
		if(relation.equals(Relation.ARRAY)) {
			return new ArrayElementAccess(type, name);
		}
		else if(relation.equals(Relation.FIELD)) {
			return new FieldAccess(type, name);
		}
		else if(relation.equals(Relation.METHOD)) {
			return new MethodCallAccess(type, name);
		}
		else if(relation.equals(Relation.PARAM)) {
			return new Parameter(type, name);
		}
		
		return null;
	}

	public void labelAccessibility() {
		// TODO Darien
		
		/**
		 * for every pair of ancestor-descendant, we random decide whether the ancestor can access descendant
		 * store the relation in {@code this.accessbilityMap},
		 * use OCGGenerator.RANDOM to for the random algorithm for debugging.
		*/
		
	}

	public void generateCode() {
		// TODO Auto-generated method stub
		
	}
	
}
