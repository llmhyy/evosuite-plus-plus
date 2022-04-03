package testcode.graphgeneration;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

	public List<GraphNode> getTopLayer(){
		List<GraphNode> topLayer = new ArrayList<GraphNode>();
		for(GraphNode node: this.nodeSet) {
			if(node.getParents().isEmpty()) {
				topLayer.add(node);
			}
			
			if(node.getIndex() == 0) {
				if(!topLayer.contains(node)) {
					topLayer.add(node);
				}
			}
		}
		
		return topLayer;
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

		String dataType = null;
		
		List<GraphNode> topLayer = this.getTopLayer();
		Queue<GraphNode> queue = new LinkedList<GraphNode>();
		queue.addAll(topLayer);
		
		/**
		 * make sure the parents are always processed before the children
		 */
		while(!queue.isEmpty()) {
			GraphNode node = queue.poll();
			
			/**
			 * non-leaf node, reference type access
			 */
			if(!node.isLeaf()) {
				if(typePool.isEmpty()) {
					dataType = createRandomType();
				}
				else {
					if(OCGGenerator.RANDOM.nextDouble() > 0.5) {
						dataType = createRandomType();
					}
					else {
						int index = OCGGenerator.RANDOM.nextInt(typePool.size());
						dataType = typePool.get(index);
					}
				}
				
				if(!typePool.contains(dataType)) {
					typePool.add(dataType);
				}
			}
			/**
			 * leaf node, primitive type access
			 */
			else {
				dataType = randomPrimitiveType();
			}
			
			/**
			 * either field access or method call here
			 */
			Relation relation = Relation.randomRelation();
			
			/**
			 * constraint 1: the top layer can only be parameter or field
			 */
			if(node.getParents().isEmpty()) {
				if(OCGGenerator.RANDOM.nextDouble() < 0.5) {
					relation = Relation.PARAM;
				}
				else {
					relation = Relation.FIELD;					
				}
			}
			
			/**
			 * constraint 2: leaf node can only be field or array element, if it is array element, its parent's data type
			 * must be an array.
			 */
			if(node.isLeaf() && node.isSingleParent()) {
				GraphNode par = node.getParents().get(0);
				if(par.isSingleChild()) {
					if(OCGGenerator.RANDOM.nextDouble() < 0.9) {
						relation = Relation.ARRAY_ELEMENT;

						/**
						 * if the leaf is an array element access, we need to change the data type in the parent access.
						 */
						NodeType parentAccessType = par.getNodeType();
						parentAccessType.setType(dataType + "[]");
					}
				}
				
			}
			
			NodeType accessType = generateNodeType(relation, dataType, node);
			
			node.setNodeType(accessType);
			
			queue.addAll(node.getChildren());
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
		if(relation.equals(Relation.ARRAY_ELEMENT)) {
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
